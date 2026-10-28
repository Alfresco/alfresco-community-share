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
package org.alfresco.web.site.servlet.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static org.junit.Assert.*;

public class CustomAuthorizationRequestResolverTest
{
    private AIMSConfig config;
    private CustomAuthorizationRequestResolver resolver;
    private MockHttpServletRequest request;

    @Before
    public void setUp()
    {
        config = new AIMSConfig();
        resolver = createResolver("https://idp.example/authorize?existing=original#fragment");
        request = new MockHttpServletRequest("GET", "/oauth2/authorization/aims");
        request.setServletPath("/oauth2/authorization/aims");
    }

    private CustomAuthorizationRequestResolver createResolver(String authorizationUri)
    {
        ClientRegistration registration = ClientRegistration.withRegistrationId("aims")
            .clientId("share")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/callback")
            .scope("openid", "profile")
            .authorizationUri(authorizationUri)
            .tokenUri("https://idp.example/token")
            .jwkSetUri("https://idp.example/jwks")
            .build();
        return new CustomAuthorizationRequestResolver(
            new InMemoryClientRegistrationRepository(registration), "/oauth2/authorization", config);
    }

    @Test
    public void shouldPreserveEndpointAndOAuthParametersInAuthorizationRequestUri()
    {
        config.setAudience("repository");
        config.setRedirectURI("https://share.example/callback");
        config.setAuthorizationAdditionalParams("kc_idp_hint=upstream-keycloak");

        OAuth2AuthorizationRequest result = resolver.resolve(request);
        URI uri = URI.create(result.getAuthorizationRequestUri());
        Map<String, String> query = new HashMap<>();
        for (String pair : uri.getRawQuery().split("&"))
        {
            String[] nameValue = pair.split("=", 2);
            query.put(URLDecoder.decode(nameValue[0], StandardCharsets.UTF_8),
                nameValue.length > 1 ? URLDecoder.decode(nameValue[1], StandardCharsets.UTF_8) : "");
        }

        assertEquals("https", uri.getScheme());
        assertEquals("idp.example", uri.getHost());
        assertEquals("/authorize", uri.getPath());
        assertEquals("fragment", uri.getFragment());
        assertEquals("original", query.get("existing"));
        assertEquals("code", query.get("response_type"));
        assertEquals("share", query.get("client_id"));
        assertEquals("https://share.example/callback", query.get("redirect_uri"));
        assertEquals(result.getState(), query.get("state"));
        assertEquals(result.getAdditionalParameters().get("nonce"), query.get("nonce"));
        assertEquals(result.getAdditionalParameters().get("code_challenge"), query.get("code_challenge"));
        assertEquals("S256", query.get("code_challenge_method"));
        assertEquals("repository", query.get("audience"));
        assertEquals("upstream-keycloak", query.get("kc_idp_hint"));
        assertTrue(query.get("scope").contains("openid"));
    }

    @Test
    public void shouldKeepPreviousAuthorizationRequestUriWhenNoParametersConfigured()
    {
        assertUriMatchesPreviousBehavior();
    }

    @Test
    public void shouldKeepPreviousAuthorizationRequestUriWhenOnlyAudienceConfigured()
    {
        config.setAudience("repository");
        assertUriMatchesPreviousBehavior();
    }

    // The previous resolver only set the redirect URI and audience, then let Spring build the URI.
    private void assertUriMatchesPreviousBehavior()
    {
        OAuth2AuthorizationRequest result = resolver.resolve(request);

        String previousUri = OAuth2AuthorizationRequest.from(result).build().getAuthorizationRequestUri();
        assertEquals(previousUri, result.getAuthorizationRequestUri());
        assertEquals(request.getRequestURL().toString(), result.getRedirectUri());
    }

    @Test
    public void shouldDetectCollisionsWithEncodedExistingParameterNames()
    {
        resolver = createResolver("https://idp.example/authorize?custom%26name=original&space%20name=original");
        config.setAuthorizationAdditionalParams("custom&name=replacement,space name=replacement,added=value");

        OAuth2AuthorizationRequest result = resolver.resolve(request);
        String uri = result.getAuthorizationRequestUri();

        assertFalse(uri.contains("replacement"));
        assertFalse(result.getAdditionalParameters().containsKey("custom&name"));
        assertFalse(result.getAdditionalParameters().containsKey("space name"));
        assertTrue(uri.contains("custom%26name=original"));
        assertTrue(uri.contains("added=value"));
    }

    @Test
    public void shouldFormDecodeExistingParameterNamesForCollisionCheck()
    {
        resolver = createResolver("https://idp.example/authorize?custom+name=original");
        config.setAuthorizationAdditionalParams("custom name=duplicate,custom+name=changed");

        OAuth2AuthorizationRequest result = resolver.resolve(request);
        String uri = result.getAuthorizationRequestUri();

        assertTrue(uri.contains("custom+name=original"));
        assertFalse(uri.contains("custom%20name"));
        assertFalse(uri.contains("duplicate"));
        assertTrue(uri.contains("custom%2Bname=changed"));
        assertEquals("changed", result.getAdditionalParameters().get("custom+name"));
        assertFalse(result.getAdditionalParameters().containsKey("custom name"));
    }

    @Test
    public void shouldTreatEncodedPlusAndUtf8ExistingParameterNamesAsLiteral()
    {
        resolver = createResolver("https://idp.example/authorize?custom%2Bname=original&caf%C3%A9=original");
        config.setAuthorizationAdditionalParams("custom+name=duplicate,café=duplicate,custom name=distinct");

        OAuth2AuthorizationRequest result = resolver.resolve(request);
        String uri = result.getAuthorizationRequestUri();

        assertTrue(uri.contains("custom%2Bname=original"));
        assertTrue(uri.contains("caf%C3%A9=original"));
        assertFalse(uri.contains("duplicate"));
        assertFalse(result.getAdditionalParameters().containsKey("custom+name"));
        assertFalse(result.getAdditionalParameters().containsKey("café"));
        assertEquals("distinct", result.getAdditionalParameters().get("custom name"));
        assertTrue(uri.contains("custom%20name=distinct"));
    }

    @Test
    public void shouldNotAddParametersWhenUnsetOrEmpty()
    {
        for (String value : new String[] {null, "", "   "})
        {
            config.setAuthorizationAdditionalParams(value);
            OAuth2AuthorizationRequest result = resolver.resolve(request);

            assertEquals(3, result.getAdditionalParameters().size());
            assertProtocolParametersPreserved(result);
            assertFalse(result.getAdditionalParameters().containsKey("resourceServer"));
            assertEquals(request.getRequestURL().toString(), result.getRedirectUri());
        }
    }

    @Test
    public void shouldIncludeAndEncodeParametersForBothResolveMethods()
    {
        config.setAudience("repository");
        config.setRedirectURI("https://share.example/callback");
        config.setAuthorizationAdditionalParams(
            "resourceServer=Identity Provider unencrypted,foo=a=b & c+%/#?,empty=,"
                + "custom&name=value,literal+plus=a+b,"
                + "claims='{\"userinfo\":{\"given_name\":null,\"email\":null}}'");

        assertCustomParameters(resolver.resolve(request));
        assertCustomParameters(resolver.resolve(request, "aims"));
    }

    @Test
    public void shouldSkipMalformedEntriesAndStillResolveRequest()
    {
        config.setAuthorizationAdditionalParams("missingEquals,=ignored,  =ignored,,foo=bar,");

        OAuth2AuthorizationRequest result = resolver.resolve(request);

        assertEquals("bar", result.getAdditionalParameters().get("foo"));
        assertEquals(4, result.getAdditionalParameters().size());
        assertProtocolParametersPreserved(result);
    }

    @Test
    public void shouldPreserveAudienceAndProtocolParametersWithoutCustomParameters()
    {
        config.setAudience("repository");

        OAuth2AuthorizationRequest result = resolver.resolve(request);

        assertEquals("repository", result.getAdditionalParameters().get("audience"));
        assertProtocolParametersPreserved(result);
    }

    @Test
    public void shouldNotOverrideExistingParameters()
    {
        config.setAudience("repository");
        config.setAuthorizationAdditionalParams(
            "audience=custom,client_id=custom,state=custom,nonce=custom,redirect_uri=custom,"
                + "code_challenge=custom,existing=replacement");

        OAuth2AuthorizationRequest result = resolver.resolve(request);
        assertEquals("repository", result.getAdditionalParameters().get("audience"));
        assertNotEquals("custom", result.getState());
        assertNotEquals("custom", result.getAdditionalParameters().get("nonce"));
        assertNotEquals("custom", result.getAdditionalParameters().get("code_challenge"));
        assertEquals("share", result.getClientId());
        assertFalse(result.getAdditionalParameters().containsKey("client_id"));
        assertFalse(result.getAdditionalParameters().containsKey("redirect_uri"));
        assertFalse(result.getAdditionalParameters().containsKey("existing"));
        assertTrue(result.getAuthorizationRequestUri().contains("existing=original"));
        assertFalse(result.getAuthorizationRequestUri().contains("replacement"));
    }

    @Test
    public void shouldReturnNullForUnmatchedRequests()
    {
        request.setServletPath("/page/user/dashboard");
        request.setRequestURI("/page/user/dashboard");

        assertNull(resolver.resolve(request));
        assertNull(resolver.resolve(request, null));
    }

    private void assertCustomParameters(OAuth2AuthorizationRequest result)
    {
        Map<String, Object> params = result.getAdditionalParameters();
        assertEquals("Identity Provider unencrypted", params.get("resourceServer"));
        assertEquals("a=b & c+%/#?", params.get("foo"));
        assertEquals("", params.get("empty"));
        assertEquals("repository", params.get("audience"));
        assertEquals("https://share.example/callback", result.getRedirectUri());
        assertProtocolParametersPreserved(result);

        String uri = result.getAuthorizationRequestUri();
        assertFalse(uri.contains(" "));
        String query = URLDecoder.decode(URI.create(uri).getRawQuery(), StandardCharsets.UTF_8);
        assertTrue(query.contains("resourceServer=Identity Provider unencrypted"));
        assertTrue(query.contains("foo=a=b & c+%/#?"));
        assertTrue(query.contains("empty="));
        assertTrue(query.contains("audience=repository"));
        assertTrue(uri.contains("custom%26name=value"));
        assertTrue(uri.contains("literal%2Bplus=a%2Bb"));
        assertTrue(query.contains("claims={\"userinfo\":{\"given_name\":null,\"email\":null}}"));
        assertTrue(uri.endsWith("#fragment"));
    }

    private void assertProtocolParametersPreserved(OAuth2AuthorizationRequest result)
    {
        assertNotNull(result.getAdditionalParameters().get("nonce"));
        assertNotNull(result.getAdditionalParameters().get("code_challenge"));
        assertEquals("S256", result.getAdditionalParameters().get("code_challenge_method"));
        assertNotNull(result.getState());
        assertEquals("share", result.getClientId());
        assertTrue(result.getScopes().contains("openid"));
    }
}
