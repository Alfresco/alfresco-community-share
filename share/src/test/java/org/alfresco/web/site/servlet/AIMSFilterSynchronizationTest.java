/*
 * Copyright 2005 - 2026 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class AIMSFilterSynchronizationTest
{
    private AIMSFilter filter;

    private static final String USER1 = "user1";
    private static final String USER2 = "user2";

    private static final String SESSION1 = "session1";
    private static final String SESSION2 = "session2";

    @Before
    public void setUp()
    {
        filter = new AIMSFilter();
    }

        /**
     * Two threads handling DIFFERENT sessions must be able to call
     * {@code removeAuthorizationRequest} concurrently.
     *
     * With the original {@code synchronized(this)} on the method, thread "session2"
     * is blocked at method entry while "session1" holds the monitor.
     * "session1" then waits up to 1 second for "session2" to also enter the mock
     * — but "session2" never can — so {@code ranConcurrently} stays {@code false}
     * and the assertion fails.
     *
     * With {@code synchronized(session)} around only {@code removeAuthorizationRequest},
     * each thread holds its own session monitor and both enter the mock simultaneously.
     */
    @Test(timeout = 3000)
    @SuppressWarnings("unchecked")
    public void processAuthorizationResponse_differentSessions_canRunConcurrently() throws Exception
    {
        CountDownLatch session1InsideMock = new CountDownLatch(1);
        CountDownLatch session2InsideMock = new CountDownLatch(1);
        AtomicBoolean ranConcurrently    = new AtomicBoolean(false);

        AuthorizationRequestRepository<OAuth2AuthorizationRequest> mockRepo =
            mock(AuthorizationRequestRepository.class);

        when(mockRepo.removeAuthorizationRequest(
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
            .thenAnswer(inv ->
            {
                if (SESSION1.equals(Thread.currentThread().getName()))
                {
                    session1InsideMock.countDown();
                    // session2 can only reach here if it is not blocked by synchronized(this)
                    ranConcurrently.set(session2InsideMock.await(1, TimeUnit.SECONDS));
                }
                else
                {
                    session2InsideMock.countDown();
                }
                return null;
            });
        injectField("authorizationRequestRepository", mockRepo);

        Method method = AIMSFilter.class.getDeclaredMethod(
            "processAuthorizationResponse",
            HttpServletRequest.class, HttpServletResponse.class, HttpSession.class);
        method.setAccessible(true);

        HttpSession sessionA = mock(HttpSession.class);
        HttpSession sessionB = mock(HttpSession.class);
        CountDownLatch bothDone = new CountDownLatch(2);

        Thread t1 = new Thread(() ->
        {
            try { method.invoke(filter, buildMockRequest(sessionA), mock(HttpServletResponse.class), sessionA); }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, SESSION1);
        t1.setDaemon(true);

        Thread t2 = new Thread(() ->
        {
            try
            {
                session1InsideMock.await(2, TimeUnit.SECONDS);
                method.invoke(filter, buildMockRequest(sessionB), mock(HttpServletResponse.class), sessionB);
            }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, SESSION2);
        t2.setDaemon(true);

        t1.start();
        t2.start();
        assertTrue("Threads did not complete in time", bothDone.await(2, TimeUnit.SECONDS));
        assertTrue(
            "Different sessions must not be blocked by a global this-lock on processAuthorizationResponse",
            ranConcurrently.get());
    }

    /**
     * Two threads sharing the SAME session must not call
     * {@code removeAuthorizationRequest} concurrently (same-session regression).
     */
    @Test(timeout = 3000)
    @SuppressWarnings("unchecked")
    public void processAuthorizationResponse_sameSession_serializes_removeAuthorizationRequest() throws Exception
    {
        AtomicInteger concurrentEntries   = new AtomicInteger(0);
        AtomicBoolean concurrencyViolation = new AtomicBoolean(false);
        CountDownLatch firstInsideMock    = new CountDownLatch(1);

        AuthorizationRequestRepository<OAuth2AuthorizationRequest> mockRepo =
            mock(AuthorizationRequestRepository.class);

        when(mockRepo.removeAuthorizationRequest(
                any(HttpServletRequest.class), any(HttpServletResponse.class)))
            .thenAnswer(inv ->
            {
                if (concurrentEntries.incrementAndGet() > 1)
                {
                    concurrencyViolation.set(true);
                }
                firstInsideMock.countDown();
                Thread.sleep(300);
                concurrentEntries.decrementAndGet();
                return null;
            });
        injectField("authorizationRequestRepository", mockRepo);

        HttpSession sharedSession = mock(HttpSession.class);
        Method method = AIMSFilter.class.getDeclaredMethod(
            "processAuthorizationResponse",
            HttpServletRequest.class, HttpServletResponse.class, HttpSession.class);
        method.setAccessible(true);
        CountDownLatch bothDone = new CountDownLatch(2);

        Thread t1 = new Thread(() ->
        {
            try { method.invoke(filter, buildMockRequest(sharedSession), mock(HttpServletResponse.class), sharedSession); }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, "t1-sameSession");
        t1.setDaemon(true);

        Thread t2 = new Thread(() ->
        {
            try
            {
                firstInsideMock.await(2, TimeUnit.SECONDS);
                method.invoke(filter, buildMockRequest(sharedSession), mock(HttpServletResponse.class), sharedSession);
            }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, "t2-sameSession");
        t2.setDaemon(true);

        t1.start();
        t2.start();
        assertTrue("Threads did not complete in time", bothDone.await(2, TimeUnit.SECONDS));
        assertFalse("removeAuthorizationRequest must not be called concurrently for the same session",
            concurrencyViolation.get());
    }

    /**
     * Two threads refreshing tokens for DIFFERENT users must be able to call
     * {@code getTokenResponse} concurrently.
     *
     * With the original {@code synchronized(this)} on {@code refreshToken}, the
     * USER2 thread is blocked at method entry while USER1 holds the monitor.
     * USER1 then waits up to 1 second for USER2 to also enter the mock — it never
     * can — so {@code ranConcurrently} stays {@code false} and the assertion fails.
     *
     * With per-user {@code ConcurrentHashMap} locks, each user holds its own
     * monitor and both threads reach the mock simultaneously.
     */
    @Test(timeout = 3000)
    @SuppressWarnings("unchecked")
    public void refreshToken_differentUsers_canRunConcurrently() throws Exception
    {
        CountDownLatch user1InsideMock = new CountDownLatch(1);
        CountDownLatch user2InsideMock = new CountDownLatch(1);
        AtomicBoolean ranConcurrently  = new AtomicBoolean(false);

        OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> mockRefreshClient =
            mock(OAuth2AccessTokenResponseClient.class);

        when(mockRefreshClient.getTokenResponse(any())).thenAnswer(inv ->
        {
            if (USER1.equals(Thread.currentThread().getName()))
            {
                user1InsideMock.countDown();
                // USER2 can only reach here if he is not blocked by synchronized(this)
                ranConcurrently.set(user2InsideMock.await(1, TimeUnit.SECONDS));
            }
            else
            {
                user2InsideMock.countDown();
            }
            throw new RuntimeException("abort after concurrency check");
        });
        injectField("refreshTokenResponseClient", mockRefreshClient);

        Method method = AIMSFilter.class.getDeclaredMethod(
            "refreshToken", SecurityContext.class, HttpSession.class);
        method.setAccessible(true);
        CountDownLatch bothDone = new CountDownLatch(2);

        Thread user1Thread = new Thread(() ->
        {
            try { method.invoke(filter, buildMockSecurityContext(USER1), mock(HttpSession.class)); }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, USER1);
        user1Thread.setDaemon(true);

        Thread user2Thread = new Thread(() ->
        {
            try
            {
                user1InsideMock.await(2, TimeUnit.SECONDS);
                method.invoke(filter, buildMockSecurityContext(USER2), mock(HttpSession.class));
            }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, USER2);
        user2Thread.setDaemon(true);

        user1Thread.start();
        user2Thread.start();
        assertTrue("Threads did not complete in time", bothDone.await(2, TimeUnit.SECONDS));
        assertTrue(
            "Different users must not be blocked by a global this-lock on refreshToken",
            ranConcurrently.get());
    }

    /**
     * Two threads refreshing tokens for the SAME user must be serialised
     * (same-user regression).
     */
    @Test(timeout = 3000)
    @SuppressWarnings("unchecked")
    public void refreshToken_sameUser_serializedUnderLock() throws Exception
    {
        AtomicInteger concurrentEntries   = new AtomicInteger(0);
        AtomicBoolean concurrencyViolation = new AtomicBoolean(false);
        CountDownLatch firstInsideMock    = new CountDownLatch(1);

        OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> mockRefreshClient =
            mock(OAuth2AccessTokenResponseClient.class);

        when(mockRefreshClient.getTokenResponse(any())).thenAnswer(inv ->
        {
            if (concurrentEntries.incrementAndGet() > 1)
            {
                concurrencyViolation.set(true);
            }
            firstInsideMock.countDown();
            Thread.sleep(300);
            concurrentEntries.decrementAndGet();
            throw new RuntimeException("done");
        });
        injectField("refreshTokenResponseClient", mockRefreshClient);

        Method method = AIMSFilter.class.getDeclaredMethod(
            "refreshToken", SecurityContext.class, HttpSession.class);
        method.setAccessible(true);
        CountDownLatch bothDone = new CountDownLatch(2);

        Thread t1 = new Thread(() ->
        {
            try { method.invoke(filter, buildMockSecurityContext(USER1), mock(HttpSession.class)); }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, "t1-" + USER1);
        t1.setDaemon(true);

        Thread t2 = new Thread(() ->
        {
            try
            {
                firstInsideMock.await(2, TimeUnit.SECONDS);
                method.invoke(filter, buildMockSecurityContext(USER1), mock(HttpSession.class));
            }
            catch (Exception ignored) {}
            bothDone.countDown();
        }, "t2-" + USER1);
        t2.setDaemon(true);

        t1.start();
        t2.start();
        assertTrue("Threads did not complete in time", bothDone.await(2, TimeUnit.SECONDS));
        assertFalse("Same-user refreshToken calls must not run concurrently",
            concurrencyViolation.get());
    }

    private void injectField(String name, Object value) throws Exception
    {
        Field field = AIMSFilter.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(filter, value);
    }

    private static HttpServletRequest buildMockRequest(HttpSession session)
    {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(session);
        when(req.getSession()).thenReturn(session);
        when(req.getParameterMap()).thenReturn(Collections.emptyMap());
        when(req.getScheme()).thenReturn("http");
        when(req.getServerName()).thenReturn("localhost");
        when(req.getServerPort()).thenReturn(8080);
        when(req.getContextPath()).thenReturn("/share");
        when(req.getServletPath()).thenReturn("/page/aims-dologin");
        when(req.getRequestURI()).thenReturn("/share/page/aims-dologin");
        when(req.getQueryString()).thenReturn("state=test-state&code=test-code");
        return req;
    }

    private static SecurityContext buildMockSecurityContext(String username)
    {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test")
            .clientId("client-id")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost/callback")
            .authorizationUri("http://localhost/auth")
            .tokenUri("http://localhost/token")
            .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER, "access-token",
            Instant.now(), Instant.now().plusSeconds(3600));
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", Instant.now());

        OAuth2LoginAuthenticationToken token = mock(OAuth2LoginAuthenticationToken.class);
        when(token.getName()).thenReturn(username);
        when(token.getClientRegistration()).thenReturn(clientRegistration);
        when(token.getAccessToken()).thenReturn(accessToken);
        when(token.getRefreshToken()).thenReturn(refreshToken);

        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(token);
        return ctx;
    }
}
