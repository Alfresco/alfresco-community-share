/*
 * Copyright 2005 - 2025 Alfresco Software Limited.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.alfresco.web.site.servlet.AIMSFilter.JwtAudienceValidator;
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.junit.Test;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

public class AIMSFilterTest
{
    private static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIxNDc0ODM2NDcsImp0aSI6IjEyMzQiLCJpc3MiOiJodHRwczovL215Lmlzc3VlciIsInN1YiI6ImFiYzEyMyIsInR5cCI6IkJlYXJlciIsInByZWZlcnJlZF91c2VybmFtZSI6Im1vaGluaXNoc2FoIn0.YOvsLAZ0ZyKf4igvtBY0fsO6R1F3Xhz5IsWzsRhyOVY";

    private static final String EXPECTED_AUDIENCE = "expected-audience";

    @Test
    public void shouldNotValidateWhenIssuerAndRequiredIssuerNotEqual()
    {
        //        filter = new AIMSFilter();
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test_registration_id")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("test_client_id")
            .redirectUri("test_uri_template")
            .issuerUri("https://my.actualIssuer")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("test_token_uri")
            .build();
        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();

        Jwt jwt = Jwt.withTokenValue(token)
            .header("alg", "none")
            .claim("sub", "abc123")
            .claim("iss", "https://my.fakeIssuer")
            .claim("preferred_username", "mohinishsah")
            .build();
        OAuth2TokenValidatorResult oAuth2TokenValidatorResult = audienceValidator.validate(jwt);
        assertTrue(oAuth2TokenValidatorResult.hasErrors());
    }

    @Test
    public void shouldValidateWhenIssuerAndRequiredIssuerAreEqual()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test_registration_id")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("test_client_id")
            .redirectUri("test_uri_template")
            .issuerUri("https://my.issuer")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("test_token_uri")
            .build();

        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();

        Jwt jwt = Jwt.withTokenValue(token)
            .header("alg", "none")
            .claim("sub", "abc123")
            .claim("iss", "https://my.issuer")
            .claim("preferred_username", "mohinishsah")
            .audience(List.of(EXPECTED_AUDIENCE))
            .build();

        OAuth2TokenValidatorResult oAuth2TokenValidatorResult = audienceValidator.validate(jwt);
        assertFalse(oAuth2TokenValidatorResult.hasErrors());

    }

    @Test
    public void shouldFailWithNullAudience()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);

        final OAuth2TokenValidatorResult validationResult = audienceValidator.validate(tokenWithAudience(null));

        final OAuth2Error error = validationResult.getErrors()
            .iterator()
            .next();

        assertTrue(error.getDescription()
                       .contains(EXPECTED_AUDIENCE));
    }

    @Test
    public void shouldSucceedWithMatchingAudienceList()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);

        final OAuth2TokenValidatorResult validationResult =
            audienceValidator.validate(tokenWithAudience(List.of(EXPECTED_AUDIENCE)));
        assertFalse(validationResult.hasErrors());
        assertTrue(validationResult.getErrors()
                       .isEmpty());
    }

    @Test
    public void shouldSucceedWithMatchingSingleAudience()
    {
        final JwtAudienceValidator audienceValidator = new JwtAudienceValidator(EXPECTED_AUDIENCE);

        final Jwt token = Jwt.withTokenValue(UUID.randomUUID()
                                                 .toString())
            .claim("aud", EXPECTED_AUDIENCE)
            .header("JUST", "FOR TESTING")
            .build();
        final OAuth2TokenValidatorResult validationResult = audienceValidator.validate(token);
        assertFalse(validationResult.hasErrors());
        assertTrue(validationResult.getErrors()
                       .isEmpty());
    }

    /**
     * When allowIdpBypass is false (default), isBypassRequest must always return false
     * regardless of the useIdp parameter — the master switch is off.
     */
    @Test
    public void isBypassRequest_shouldReturnFalse_whenAllowIdpBypassDisabled() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "allowIdpBypass", false);
        setField(filter, "shareContext", "/share");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/share/page");
        when(request.getParameter("useIdp")).thenReturn("false");

        Method isBypassRequest = AIMSFilter.class.getDeclaredMethod("isBypassRequest", HttpServletRequest.class);
        isBypassRequest.setAccessible(true);

        boolean result = (boolean) isBypassRequest.invoke(filter, request);
        assertFalse("Bypass must be disabled when allowIdpBypass=false", result);
    }

    /**
     * When allowIdpBypass is true and useIdp=false is passed on a /page URI,
     * isBypassRequest must return true and store the flag in the session.
     */
    @Test
    public void isBypassRequest_shouldReturnTrue_whenUseIdpFalseOnPageUri() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "allowIdpBypass", true);
        setField(filter, "shareContext", "/share");

        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/share/page");
        when(request.getParameter("useIdp")).thenReturn("false");
        when(request.getSession(true)).thenReturn(session);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Method isBypassRequest = AIMSFilter.class.getDeclaredMethod("isBypassRequest", HttpServletRequest.class);
        isBypassRequest.setAccessible(true);

        boolean result = (boolean) isBypassRequest.invoke(filter, request);
        assertTrue("Bypass must be active when allowIdpBypass=true and useIdp=false on /page URI", result);
        // Verify the session flag was actually stored — required for the sticky /page/dologin POST
        org.mockito.Mockito.verify(session).setAttribute("aims.bypass", Boolean.TRUE);
    }

    /**
     * When allowIdpBypass is true but the URI is a proxy endpoint (not /page),
     * isBypassRequest must return false — proxy calls must never bypass SSO.
     */
    @Test
    public void isBypassRequest_shouldReturnFalse_whenUriIsProxyEndpoint() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "allowIdpBypass", true);
        setField(filter, "shareContext", "/share");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/share/proxy/alfresco/slingshot/node/content");
        when(request.getParameter("useIdp")).thenReturn("false");

        Method isBypassRequest = AIMSFilter.class.getDeclaredMethod("isBypassRequest", HttpServletRequest.class);
        isBypassRequest.setAccessible(true);

        boolean result = (boolean) isBypassRequest.invoke(filter, request);
        assertFalse("Bypass must NOT activate on proxy/content endpoints", result);
    }

    /**
     * Session-sticky branch: when bypass was activated in a previous request (flag in session)
     * and no useIdp param is present, isBypassRequest must return true to cover the form POST
     * to /page/dologin which does not carry the useIdp parameter.
     */
    @Test
    public void isBypassRequest_shouldReturnTrue_whenSessionFlagSetAndNoParam() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "allowIdpBypass", true);
        setField(filter, "shareContext", "/share");

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("aims.bypass")).thenReturn(Boolean.TRUE);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/share/page/dologin");
        when(request.getParameter("useIdp")).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Method isBypassRequest = AIMSFilter.class.getDeclaredMethod("isBypassRequest", HttpServletRequest.class);
        isBypassRequest.setAccessible(true);

        boolean result = (boolean) isBypassRequest.invoke(filter, request);
        assertTrue("Bypass must continue from session flag when no useIdp param is present", result);
    }

    /**
     * Cancellation branch: when useIdp=true is passed, isBypassRequest must clear the session flag
     * and return false, re-enabling Keycloak SSO.
     */
    @Test
    public void isBypassRequest_shouldReturnFalse_andClearFlag_whenUseIdpTrue() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "allowIdpBypass", true);
        setField(filter, "shareContext", "/share");

        HttpSession session = mock(HttpSession.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/share/page");
        when(request.getParameter("useIdp")).thenReturn("true");
        when(request.getSession(false)).thenReturn(session);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Method isBypassRequest = AIMSFilter.class.getDeclaredMethod("isBypassRequest", HttpServletRequest.class);
        isBypassRequest.setAccessible(true);

        boolean result = (boolean) isBypassRequest.invoke(filter, request);
        assertFalse("Bypass must be cancelled when useIdp=true is passed", result);

        // Verify the session flag was removed
        org.mockito.Mockito.verify(session).removeAttribute("aims.bypass");
    }

    @Test
    public void refreshSessionId_shouldChangeSessionId() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.changeSessionId()).thenReturn("new-session-id");

        Method refreshSessionId = AIMSFilter.class.getDeclaredMethod("refreshSessionId",HttpServletRequest.class);
        refreshSessionId.setAccessible(true);

        refreshSessionId.invoke(filter, request);

        verify(request).changeSessionId();
    }

    @Test
    public void refreshSessionId_shouldSwallowIllegalStateException() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.changeSessionId()).thenThrow(new IllegalStateException("no session"));

        Method refreshSessionId = AIMSFilter.class.getDeclaredMethod("refreshSessionId", HttpServletRequest.class);
        refreshSessionId.setAccessible(true);

        // Should not throw despite changeSessionId() failing.
        refreshSessionId.invoke(filter, request);

        verify(request).changeSessionId();
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldEncodeCurlyBracesInRedirectUrl() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn("https://localhost/share/page?query={value}");
        when(request.getParameter("fragment")).thenReturn(null);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
                AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                        HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect("https://localhost/share/page?query=%7Bvalue%7D");
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldNotDoubleEncodeAlreadyEncodedRedirectUrl() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn("https://localhost/share/page?query=%7Bvalue%7D");
        when(request.getParameter("fragment")).thenReturn(null);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
            AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect("https://localhost/share/page?query=%7Bvalue%7D");
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldEncodeRawCurlyBracesWhenUrlContainsEncodedOctets() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn(
            "https://localhost/share/page?searchTerm=TYPE%3A%22cm%3Acontent%22&query={value}");
        when(request.getParameter("fragment")).thenReturn(null);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
            AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect(
            "https://localhost/share/page?searchTerm=TYPE%3A%22cm%3Acontent%22&query=%7Bvalue%7D");
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldPreserveEncodedOctetsAndEncodeRawQueryCharacters() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn("https://localhost/share/page?a=%2F&b=hello world");
        when(request.getParameter("fragment")).thenReturn(null);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
            AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect("https://localhost/share/page?a=%2F&b=hello%20world");
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldPreserveEncodedFragmentAndEncodeRawQuery() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn("https://localhost/share/page?query=hello world");
        when(request.getParameter("fragment")).thenReturn("state%3Aview");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
            AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect("https://localhost/share/page?query=hello%20world#state%3Aview");
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldPreserveLiteralTokenWhenNoEncodedOctetsPresent() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn("https://localhost/share/page?x=__AIMS_OCTET_0__");
        when(request.getParameter("fragment")).thenReturn(null);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
            AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect("https://localhost/share/page?x=__AIMS_OCTET_0__");
    }

    @Test
    public void sendRedirectToOriginalTarget_shouldNotRewriteLiteralTokenWhenEncodedOctetsArePresent() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("redirectUrl")).thenReturn("https://localhost/share/page?a=%2F&x=__AIMS_OCTET_0__");
        when(request.getParameter("fragment")).thenReturn(null);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getScheme()).thenReturn("https");
        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        Method sendRedirectToOriginalTarget =
            AIMSFilter.class.getDeclaredMethod("sendRedirectToOriginalTarget", HttpServletRequest.class,
                HttpServletResponse.class);
        sendRedirectToOriginalTarget.setAccessible(true);

        sendRedirectToOriginalTarget.invoke(filter, request, response);

        verify(response).sendRedirect("https://localhost/share/page?a=%2F&x=__AIMS_OCTET_0__");
    }


    /** Every configured algorithm is accepted, so both RS256 and PS256 tokens can be validated. */
    @Test
    public void resolveSignatureAlgorithms_shouldResolveAllConfiguredAlgorithms() throws Exception
    {
        Set<SignatureAlgorithm> resolved =
            invokeResolveSignatureAlgorithms(new AIMSFilter(), Set.of("RS256", "PS256"));

        assertEquals(Set.of(SignatureAlgorithm.RS256, SignatureAlgorithm.PS256), resolved);
    }

    /** An unknown algorithm is skipped instead of breaking login. */
    @Test
    public void resolveSignatureAlgorithms_shouldIgnoreUnknownAlgorithm() throws Exception
    {
        Set<SignatureAlgorithm> resolved =
            invokeResolveSignatureAlgorithms(new AIMSFilter(), Set.of("PS256", "NOT_AN_ALGORITHM"));

        assertEquals(Set.of(SignatureAlgorithm.PS256), resolved);
    }

    /** RS256 is used when no valid algorithm is configured, keeping the previous behaviour. */
    @Test
    public void resolveSignatureAlgorithms_shouldFallBackToRS256WhenNoValidAlgorithm() throws Exception
    {
        Set<SignatureAlgorithm> resolved =
            invokeResolveSignatureAlgorithms(new AIMSFilter(), Set.of("NOT_AN_ALGORITHM"));

        assertEquals(Set.of(SignatureAlgorithm.RS256), resolved);
    }

    /** RS256 is also used when the property is absent. */
    @Test
    public void resolveSignatureAlgorithms_shouldFallBackToRS256WhenNull() throws Exception
    {
        Set<SignatureAlgorithm> resolved = invokeResolveSignatureAlgorithms(new AIMSFilter(), null);

        assertEquals(Set.of(SignatureAlgorithm.RS256), resolved);
    }

    /** An unknown algorithm configured through AIMSConfig is resolved to the RS256 default. */
    @Test
    public void resolveSignatureAlgorithms_shouldFallBackToRS256WhenConfiguredAlgorithmIsUnknown() throws Exception
    {
        AIMSConfig aimsConfig = new AIMSConfig();
        aimsConfig.setSignatureAlgorithms("NOT_AN_ALGORITHM");

        Set<SignatureAlgorithm> resolved =
            invokeResolveSignatureAlgorithms(new AIMSFilter(), aimsConfig.getSignatureAlgorithms());

        assertEquals(Set.of(SignatureAlgorithm.RS256), resolved);
    }

    /** A valid algorithm is kept when it is configured alongside an unknown one. */
    @Test
    public void resolveSignatureAlgorithms_shouldKeepValidAlgorithmWhenConfiguredWithUnknownOne() throws Exception
    {
        AIMSConfig aimsConfig = new AIMSConfig();
        aimsConfig.setSignatureAlgorithms("PS256, NOT_AN_ALGORITHM");

        Set<SignatureAlgorithm> resolved =
            invokeResolveSignatureAlgorithms(new AIMSFilter(), aimsConfig.getSignatureAlgorithms());

        assertEquals(Set.of(SignatureAlgorithm.PS256), resolved);
    }


    /** The claim is read from the ID Token and the UserInfo endpoint is not called. */
    @Test
    @SuppressWarnings("unchecked")
    public void createOidcUser_shouldUseIdTokenWhenClaimIsPresent() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        OAuth2UserService<OidcUserRequest, OidcUser> userService = mock(OAuth2UserService.class);
        setField(filter, "userService", userService);

        OidcUser oidcUser = invokeCreateOidcUser(filter,
            clientRegistrationWithUserNameAttribute("preferred_username"),
            idTokenWithClaims(Map.of("sub", "abc123", "preferred_username", "mohinishsah")),
            bearerAccessToken());

        assertEquals("mohinishsah", oidcUser.getAttribute("preferred_username"));
        verify(userService, never()).loadUser(any(OidcUserRequest.class));
    }

    /** The claim is missing from the ID Token, so the user is loaded from the UserInfo endpoint. */
    @Test
    @SuppressWarnings("unchecked")
    public void createOidcUser_shouldFallBackToUserInfoWhenClaimIsMissing() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        OAuth2UserService<OidcUserRequest, OidcUser> userService = mock(OAuth2UserService.class);
        OidcUser userInfoUser = mock(OidcUser.class);
        when(userService.loadUser(any(OidcUserRequest.class))).thenReturn(userInfoUser);
        setField(filter, "userService", userService);

        OidcUser oidcUser = invokeCreateOidcUser(filter,
            clientRegistrationWithUserNameAttribute("email"),
            idTokenWithClaims(Map.of("sub", "abc123")),
            bearerAccessToken());

        assertSame(userInfoUser, oidcUser);
        verify(userService).loadUser(any(OidcUserRequest.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createOidcUser_shouldRethrowUserInfoAuthenticationException() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        OAuth2UserService<OidcUserRequest, OidcUser> userService = mock(OAuth2UserService.class);
        OAuth2AuthenticationException expected =
            new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response"));
        when(userService.loadUser(any(OidcUserRequest.class))).thenThrow(expected);
        setField(filter, "userService", userService);

        try
        {
            invokeCreateOidcUser(filter, clientRegistrationWithUserNameAttribute("email"),
                idTokenWithClaims(Map.of("sub", "abc123")), bearerAccessToken());
            fail("Expected OAuth2AuthenticationException");
        }
        catch (InvocationTargetException exception)
        {
            assertSame(expected, exception.getCause());
        }
    }

    @Test
    public void jwtDecoders_shouldBeCachedSeparatelyByTokenType() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "signatureAlgorithms", Set.of(SignatureAlgorithm.RS256, SignatureAlgorithm.PS256));
        ClientRegistration clientRegistration = clientRegistrationWithUserNameAttribute("email");

        NimbusJwtDecoder firstAccessTokenDecoder = invokeGetJwtDecoder(filter, "getAccessTokenJwtDecoder",
            clientRegistration);
        NimbusJwtDecoder secondAccessTokenDecoder = invokeGetJwtDecoder(filter, "getAccessTokenJwtDecoder",
            clientRegistration);
        NimbusJwtDecoder firstIdTokenDecoder = invokeGetJwtDecoder(filter, "getIdTokenJwtDecoder",
            clientRegistration);
        NimbusJwtDecoder secondIdTokenDecoder = invokeGetJwtDecoder(filter, "getIdTokenJwtDecoder",
            clientRegistration);

        assertSame(firstAccessTokenDecoder, secondAccessTokenDecoder);
        assertSame(firstIdTokenDecoder, secondIdTokenDecoder);
        assertNotSame(firstAccessTokenDecoder, firstIdTokenDecoder);
    }

    private Jwt tokenWithAudience(Collection<String> audience)
    {
        return Jwt.withTokenValue(UUID.randomUUID()
                                      .toString())
            .audience(audience)
            .header("JUST", "FOR TESTING")
            .build();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception
    {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private Set<SignatureAlgorithm> invokeResolveSignatureAlgorithms(AIMSFilter filter, Set<String> configured)
        throws Exception
    {
        Method resolveSignatureAlgorithms =
            AIMSFilter.class.getDeclaredMethod("resolveSignatureAlgorithms", Set.class);
        resolveSignatureAlgorithms.setAccessible(true);
        return (Set<SignatureAlgorithm>) resolveSignatureAlgorithms.invoke(filter, configured);
    }

    private ClientRegistration clientRegistrationWithUserNameAttribute(String userNameAttributeName)
    {
        return ClientRegistration.withRegistrationId("test_registration_id")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("test_client_id")
            .redirectUri("test_uri_template")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("test_token_uri")
            .jwkSetUri("http://localhost:8999/jwks")
            .userInfoUri("http://localhost:8999/userinfo")
            .userNameAttributeName(userNameAttributeName)
            .providerConfigurationMetadata(Map.of("access_token_issuer", "https://issuer.example"))
            .build();
    }

    private OidcIdToken idTokenWithClaims(Map<String, Object> claims)
    {
        return new OidcIdToken(token, Instant.now(), Instant.now().plusSeconds(60), claims);
    }

    private OAuth2AccessToken bearerAccessToken()
    {
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token-value", Instant.now(),
            Instant.now().plusSeconds(60));
    }

    private OidcUser invokeCreateOidcUser(AIMSFilter filter, ClientRegistration clientRegistration,
                                          OidcIdToken idToken, OAuth2AccessToken accessToken) throws Exception
    {
        Method createOidcUser = AIMSFilter.class.getDeclaredMethod("createOidcUser", ClientRegistration.class,
            OidcIdToken.class, OAuth2AccessToken.class);
        createOidcUser.setAccessible(true);
        return (OidcUser) createOidcUser.invoke(filter, clientRegistration, idToken, accessToken);
    }

    private NimbusJwtDecoder invokeGetJwtDecoder(AIMSFilter filter, String methodName,
                                                  ClientRegistration clientRegistration) throws Exception
    {
        Method getJwtDecoder = AIMSFilter.class.getDeclaredMethod(methodName, ClientRegistration.class);
        getJwtDecoder.setAccessible(true);
        return (NimbusJwtDecoder) getJwtDecoder.invoke(filter, clientRegistration);
    }
}
