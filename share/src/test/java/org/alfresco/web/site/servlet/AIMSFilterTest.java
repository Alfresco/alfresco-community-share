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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.alfresco.web.site.servlet.AIMSFilter.JwtAudienceValidator;
import org.junit.Test;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

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

    private Jwt tokenWithAudience(Collection<String> audience)
    {
        return Jwt.withTokenValue(UUID.randomUUID()
                                      .toString())
            .audience(audience)
            .header("JUST", "FOR TESTING")
            .build();
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

    private void setField(Object target, String fieldName, Object value) throws Exception
    {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
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

    /**
     * Only the AIMS login page is served without an SSO redirect: it is the page that builds the redirect URL
     * (query + fragment) before the authorization request is issued. Every other page keeps going through SSO.
     */
    @Test
    public void isSsoExemptPage_shouldOnlyExemptTheAimsLoginPage() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        Method isSsoExemptPage = AIMSFilter.class.getDeclaredMethod("isSsoExemptPage", HttpServletRequest.class);
        isSsoExemptPage.setAccessible(true);

        assertTrue("The AIMS login page must not be redirected",
            (boolean) isSsoExemptPage.invoke(filter, requestWithUri("/share/page/aims-login")));
        assertFalse("A regular page must still go through the SSO redirect",
            (boolean) isSsoExemptPage.invoke(filter, requestWithUri("/share/page/user/admin/dashboard")));
    }

    /**
     * The post logout redirect URI sent to the IdP when a login is aborted must be the absolute Share entry
     * point: with the IdP session terminated, going back there re-enters the SSO flow and leaves the user on
     * the IdP login screen.
     */
    @Test
    public void buildSsoReentryUrl_shouldTargetTheSharePageRoot() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/share/page/aims-dologin");
        request.setContextPath("/share");
        request.setQueryString("code=abc&state=xyz");

        Method buildSsoReentryUrl =
            AIMSFilter.class.getDeclaredMethod("buildSsoReentryUrl", HttpServletRequest.class);
        buildSsoReentryUrl.setAccessible(true);

        String url = (String) buildSsoReentryUrl.invoke(filter, request);

        assertEquals("http://localhost/share/page/", url);
    }

    /**
     * An aborted login must not leave anything behind: no Spring Security context, no Share user and no session.
     * Otherwise the session looks authenticated to the filter while having no Share user, and Share falls back
     * to its own login form (the deauthorized user issue).
     */
    @Test
    public void clearAuthenticatedSession_shouldRemoveAuthenticationTracesAndInvalidateTheSession() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "requestCache", new HttpSessionRequestCache());

        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Method clearAuthenticatedSession = AIMSFilter.class.getDeclaredMethod("clearAuthenticatedSession",
            HttpServletRequest.class, HttpServletResponse.class, HttpSession.class);
        clearAuthenticatedSession.setAccessible(true);

        clearAuthenticatedSession.invoke(filter, request, response, session);

        verify(session).removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        verify(session).removeAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        verify(session).removeAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH_AIMS);
        verify(session).invalidate();
    }

    /**
     * A deauthorized user is authenticated by the IdP but the repository answers 403 when a ticket is requested.
     * This must be reported as an authorization failure and never be silently ignored.
     */
    @Test
    public void getAlfTicket_shouldRaiseRepositoryAuthorizationException_whenRepositoryRefusesTheUser()
        throws Exception
    {
        assertTicketCallFails(Status.STATUS_FORBIDDEN);
        assertTicketCallFails(Status.STATUS_UNAUTHORIZED);
    }

    private void assertTicketCallFails(int statusCode) throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        Response repoResponse = mock(Response.class);
        when(repoResponse.getStatus()).thenReturn(statusWithCode(statusCode));

        Connector connector = mock(Connector.class);
        when(connector.call(anyString(), any(ConnectorContext.class))).thenReturn(repoResponse);

        ConnectorService connectorService = mock(ConnectorService.class);
        when(connectorService.getConnector(anyString(), anyString(), any(HttpSession.class))).thenReturn(connector);
        setField(filter, "connectorService", connectorService);

        Method getAlfTicket = AIMSFilter.class.getDeclaredMethod("getAlfTicket", HttpSession.class, String.class,
            String.class);
        getAlfTicket.setAccessible(true);

        try
        {
            getAlfTicket.invoke(filter, mock(HttpSession.class), "testuser", "an-access-token");
            fail("A " + statusCode + " answer from the repository must abort the login");
        }
        catch (InvocationTargetException e)
        {
            assertTrue("Expected a RepositoryAuthorizationException but got " + e.getCause(),
                e.getCause() instanceof AIMSFilter.RepositoryAuthorizationException);
            assertEquals(statusCode,
                ((AIMSFilter.RepositoryAuthorizationException) e.getCause()).getStatusCode());
        }
    }

    /**
     * {@code InMemoryOAuth2AuthorizedClientService} keys the saved client by {@code Authentication#getName()},
     * not by the {@code principalName} carried by the {@code OAuth2AuthorizedClient}. Aborting a login must
     * therefore remove the client with the authentication name, otherwise the rejected user's tokens would
     * silently be retained.
     */
    @Test
    public void authorizedClient_isKeyedByAuthenticationName_notByPrincipalToString()
    {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("alfresco")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("alfresco")
            .redirectUri("http://localhost/share/page/aims-dologin")
            .authorizationUri("http://idp/auth")
            .tokenUri("http://idp/token")
            .build();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(60);
        OidcIdToken idToken = new OidcIdToken("an-id-token", issuedAt, expiresAt,
            Map.of(IdTokenClaimNames.SUB, "abc123", "preferred_username", "testuser"));
        DefaultOidcUser oidcUser = new DefaultOidcUser(List.of(new SimpleGrantedAuthority("ROLE_USER")), idToken,
            "preferred_username");

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "an-access-token",
            issuedAt, expiresAt);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("a-refresh-token", issuedAt);

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("http://idp/auth")
            .clientId("alfresco")
            .redirectUri("http://localhost/share/page/aims-dologin")
            .state("a-state")
            .build();
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success("a-code")
            .redirectUri("http://localhost/share/page/aims-dologin")
            .state("a-state")
            .build();

        OAuth2LoginAuthenticationToken authentication = new OAuth2LoginAuthenticationToken(clientRegistration,
            new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse), oidcUser,
            List.of(new SimpleGrantedAuthority("ROLE_USER")), accessToken, refreshToken);

        String principalToString = authentication.getPrincipal().toString();
        assertNotEquals("The test is pointless if both values are equal", authentication.getName(), principalToString);

        OAuth2AuthorizedClientService service =
            new InMemoryOAuth2AuthorizedClientService(registrationId -> clientRegistration);
        service.saveAuthorizedClient(
            new OAuth2AuthorizedClient(clientRegistration, principalToString, accessToken, refreshToken),
            authentication);

        // The client is reachable through the authentication name only.
        assertNotNull(service.loadAuthorizedClient("alfresco", authentication.getName()));
        assertNull(service.loadAuthorizedClient("alfresco", principalToString));

        // Removing with the principal toString() would leave the rejected user's tokens behind.
        service.removeAuthorizedClient("alfresco", principalToString);
        assertNotNull(service.loadAuthorizedClient("alfresco", authentication.getName()));

        service.removeAuthorizedClient("alfresco", authentication.getName());
        assertNull(service.loadAuthorizedClient("alfresco", authentication.getName()));
    }

    /**
     * Aborting a login must actually evict the rejected user's tokens, i.e. remove the authorized client with the
     * authentication name that {@code saveAuthorizedClient} keyed it with.
     */
    @Test
    public void abortLogin_shouldRemoveTheAuthorizedClient_usingTheAuthenticationName() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();
        setField(filter, "requestCache", new HttpSessionRequestCache());

        OAuth2AuthorizedClientService clientService = mock(OAuth2AuthorizedClientService.class);
        setField(filter, "oauth2ClientService", clientService);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("alfresco")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("alfresco")
            .redirectUri("http://localhost/share/page/aims-dologin")
            .authorizationUri("http://idp/auth")
            .tokenUri("http://idp/token")
            .build();

        OAuth2LoginAuthenticationToken authentication = mock(OAuth2LoginAuthenticationToken.class);
        when(authentication.getClientRegistration()).thenReturn(clientRegistration);
        when(authentication.getName()).thenReturn("testuser");

        Method abortLogin = AIMSFilter.class.getDeclaredMethod("abortLogin", HttpServletRequest.class,
            HttpServletResponse.class, HttpSession.class, OAuth2LoginAuthenticationToken.class, String.class);
        abortLogin.setAccessible(true);

        abortLogin.invoke(filter, new MockHttpServletRequest("GET", "/share/page/aims-dologin"),
            mock(HttpServletResponse.class), mock(HttpSession.class), authentication, "notAuthorized");

        verify(clientService).removeAuthorizedClient("alfresco", "testuser");
    }

    /**
     * Race guarded here: the user is deauthorized, the login is aborted, then the repository re-authorizes him
     * before he tries again. The verdict must be re-derived on every attempt - the filter must not cache the
     * refusal - so the same filter instance that refused the user must accept him on the next call.
     */
    @Test
    public void getAlfTicket_shouldSucceedAfterAPreviousRefusal_whenTheUserIsReauthorized() throws Exception
    {
        AIMSFilter filter = new AIMSFilter();

        Response refused = mock(Response.class);
        when(refused.getStatus()).thenReturn(statusWithCode(Status.STATUS_FORBIDDEN));

        Response accepted = mock(Response.class);
        when(accepted.getStatus()).thenReturn(statusWithCode(Status.STATUS_OK));
        when(accepted.getText()).thenReturn("{\"entry\":{\"id\":\"TICKET_abc123\"}}");

        Connector connector = mock(Connector.class);
        when(connector.call(anyString(), any(ConnectorContext.class))).thenReturn(refused, accepted);

        ConnectorService connectorService = mock(ConnectorService.class);
        when(connectorService.getConnector(anyString(), anyString(), any(HttpSession.class))).thenReturn(connector);
        setField(filter, "connectorService", connectorService);

        Method getAlfTicket = AIMSFilter.class.getDeclaredMethod("getAlfTicket", HttpSession.class, String.class,
            String.class);
        getAlfTicket.setAccessible(true);

        // 1 - deauthorized: the login is refused
        try
        {
            getAlfTicket.invoke(filter, mock(HttpSession.class), "testuser", "an-access-token");
            fail("The repository refused the user, the login must be aborted");
        }
        catch (InvocationTargetException e)
        {
            assertTrue("Expected a RepositoryAuthorizationException but got " + e.getCause(),
                e.getCause() instanceof AIMSFilter.RepositoryAuthorizationException);
        }

        // 2 - re-authorized in the repository: the very next attempt must succeed, with no clean-up in between
        String ticket = (String) getAlfTicket.invoke(filter, mock(HttpSession.class), "testuser", "an-access-token");
        assertEquals("TICKET_abc123", ticket);
    }

    /**
     * An aborted login must not leave a token behind that would shadow a later, legitimate one. After the abort the
     * authorized client is gone, and re-authorizing the same user simply stores a new client - there is no stale
     * entry to evict and no state to reconcile.
     */
    @Test
    public void abortLogin_shouldLeaveNoAuthorizedClient_soAReauthorizedUserCanLogInAgain() throws Exception
    {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("alfresco")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("alfresco")
            .redirectUri("http://localhost/share/page/aims-dologin")
            .authorizationUri("http://idp/auth")
            .tokenUri("http://idp/token")
            .build();

        Instant issuedAt = Instant.now();
        OAuth2AccessToken rejectedToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
            "rejected-access-token", issuedAt, issuedAt.plusSeconds(60));

        OAuth2AuthorizedClientService clientService =
            new InMemoryOAuth2AuthorizedClientService(registrationId -> clientRegistration);

        OAuth2LoginAuthenticationToken authentication = mock(OAuth2LoginAuthenticationToken.class);
        when(authentication.getClientRegistration()).thenReturn(clientRegistration);
        when(authentication.getName()).thenReturn("testuser");

        clientService.saveAuthorizedClient(
            new OAuth2AuthorizedClient(clientRegistration, "testuser", rejectedToken, null), authentication);

        AIMSFilter filter = new AIMSFilter();
        setField(filter, "requestCache", new HttpSessionRequestCache());
        setField(filter, "oauth2ClientService", clientService);

        Method abortLogin = AIMSFilter.class.getDeclaredMethod("abortLogin", HttpServletRequest.class,
            HttpServletResponse.class, HttpSession.class, OAuth2LoginAuthenticationToken.class, String.class);
        abortLogin.setAccessible(true);
        abortLogin.invoke(filter, new MockHttpServletRequest("GET", "/share/page/aims-dologin"),
            mock(HttpServletResponse.class), mock(HttpSession.class), authentication, "notAuthorized");

        assertNull("The rejected user's tokens must not survive the abort",
            clientService.loadAuthorizedClient("alfresco", "testuser"));

        // The user is re-authorized in the repository and logs in again: nothing stale gets in the way.
        OAuth2AccessToken newToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "new-access-token",
            issuedAt, issuedAt.plusSeconds(60));
        clientService.saveAuthorizedClient(
            new OAuth2AuthorizedClient(clientRegistration, "testuser", newToken, null), authentication);

        OAuth2AuthorizedClient reloaded = clientService.loadAuthorizedClient("alfresco", "testuser");
        assertNotNull(reloaded);
        assertEquals("new-access-token", reloaded.getAccessToken().getTokenValue());
    }

    private ResponseStatus statusWithCode(int code)
    {
        ResponseStatus status = new ResponseStatus();
        status.setCode(code);
        return status;
    }

    private HttpServletRequest requestWithUri(String uri)
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

}
