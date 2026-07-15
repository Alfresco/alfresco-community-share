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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.alfresco.web.site.servlet.AIMSFilter.JwtAudienceValidator;
import org.junit.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

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

}
