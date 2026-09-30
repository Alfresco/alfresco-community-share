package org.alfresco.web.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.alfresco.web.site.servlet.AIMSLogoutHandler;
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the real {@link AIMSLogoutHandler}: the handler is instantiated by Mockito ({@code @InjectMocks}) and only its
 * collaborators are mocked, so the URL the handler actually builds is asserted.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AIMSLogoutHandlerTest
{

    private static final String SSO_REENTRY_URL = "http://localhost:8080/share/page/";

    ClientRegistration clientRegistration;
    @Mock
    ClientRegistrationRepository clientRegistrationRepository;
    @Mock
    AIMSConfig aimsConfig;
    @Mock
    RedirectStrategy redirectStrategy;
    @InjectMocks
    AIMSLogoutHandler logoutHandler;
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    Authentication authentication;
    @Captor
    ArgumentCaptor<String> targetUrlCaptor;
    Map<String, Object> endpointMap;

    @Before
    public void setup()
    {
        endpointMap = new HashMap<>();
        endpointMap.put("end_session_endpoint", "http://localhost:8080");
        request = new MockHttpServletRequest("POST", "http://localhost:8080/share/page/dashboard");
        when(aimsConfig.getUserIdTokenHint()).thenReturn(Boolean.FALSE);
    }

    private ClientRegistration registrationWithEndSessionEndpoint()
    {
        return ClientRegistration.withRegistrationId("alfresco")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("alfresco")
            .redirectUri("http://localhost:8080/share/page/dashboard")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("tokenuri")
            .providerConfigurationMetadata(endpointMap)
            .build();
    }

    private String capturedTargetUrl() throws IOException
    {
        verify(redirectStrategy).sendRedirect(any(), any(), targetUrlCaptor.capture());
        return targetUrlCaptor.getValue();
    }

    /** Decodes the URL so that the assertions do not depend on which characters the builder escapes. */
    private String decodedTargetUrl() throws IOException
    {
        return URLDecoder.decode(capturedTargetUrl(), StandardCharsets.UTF_8);
    }

    @Test
    public void getTargetUrlWhenLogoutUriIsNull() throws ServletException, IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getLogoutClientIDLabel()).thenReturn("client_id");
        when(aimsConfig.getLogoutClientIDValue()).thenReturn("alfresco");
        clientRegistration = registrationWithEndSessionEndpoint();
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(clientRegistration);

        logoutHandler.handle(request, response, authentication);

        // no logout URI is configured: the end_session_endpoint of the provider metadata has to be used
        String targetUrl = capturedTargetUrl();
        assertTrue(targetUrl.startsWith("http://localhost:8080"));
        assertTrue(targetUrl.contains("client_id=alfresco"));
    }

    @Test
    public void getTargetUrlWhenLogoutUriAndPostLogoutUriIsNotNull() throws ServletException, IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getLogoutUri()).thenReturn("http://localhost:8999/auth");
        endpointMap.put("post_redirect_uri", "http://localhost:8999/auth");
        clientRegistration = registrationWithEndSessionEndpoint();
        when(aimsConfig.getLogoutClientIDLabel()).thenReturn("client_id");
        when(aimsConfig.getLogoutClientIDValue()).thenReturn("alfresco");
        when(aimsConfig.getPostLogoutRedirectUrlLabel()).thenReturn("post_logout_redirect_uri");
        when(aimsConfig.getPostLogoutRedirectUrlValue()).thenReturn("http://localhost:8999/auth");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(clientRegistration);

        logoutHandler.handle(request, response, authentication);

        // the configured logout URI wins over the provider metadata and the configured post logout URL is used
        String targetUrl = decodedTargetUrl();
        assertTrue(targetUrl.startsWith("http://localhost:8999/auth"));
        assertTrue(targetUrl.contains("post_logout_redirect_uri=http://localhost:8999/auth"));
    }

    @Test
    public void getTargetUrlWhenClientRegistrationIsNull() throws ServletException, IOException
    {
        when(aimsConfig.getResource()).thenReturn("123");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(null);

        logoutHandler.handle(request, response, authentication);

        // without a client registration there is no end session endpoint, so no URL can be built
        assertNull(capturedTargetUrl());
    }

    /** The forced target must take precedence over the configured post logout URL. */
    @Test
    public void handleWithOverrideShouldPreferTheOverrideOverTheConfiguredPostLogoutUrl() throws IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getPostLogoutRedirectUrlLabel()).thenReturn("post_logout_redirect_uri");
        when(aimsConfig.getPostLogoutRedirectUrlValue()).thenReturn("http://localhost:8999/configured");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(
            registrationWithEndSessionEndpoint());

        boolean redirected = logoutHandler.handle(request, response, authentication, SSO_REENTRY_URL);

        assertTrue(redirected);
        String targetUrl = decodedTargetUrl();
        assertTrue(targetUrl.startsWith("http://localhost:8080"));
        assertTrue(targetUrl.contains("post_logout_redirect_uri=" + SSO_REENTRY_URL));
        assertFalse(targetUrl.contains("configured"));
    }

    /** Without a configured label the override has to be encoded under the default parameter name. */
    @Test
    public void handleWithOverrideShouldUseTheDefaultParameterNameWhenNoLabelIsConfigured() throws IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getPostLogoutRedirectUrlLabel()).thenReturn(null);
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(
            registrationWithEndSessionEndpoint());

        assertTrue(logoutHandler.handle(request, response, authentication, SSO_REENTRY_URL));

        assertTrue(decodedTargetUrl().contains("post_logout_redirect_uri=" + SSO_REENTRY_URL));
    }

    /** With a configured label the override has to be encoded under that label. */
    @Test
    public void handleWithOverrideShouldUseTheConfiguredParameterName() throws IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getPostLogoutRedirectUrlLabel()).thenReturn("redirect_uri");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(
            registrationWithEndSessionEndpoint());

        assertTrue(logoutHandler.handle(request, response, authentication, SSO_REENTRY_URL));

        assertTrue(decodedTargetUrl().contains("redirect_uri=" + SSO_REENTRY_URL));
    }

    /** Fail closed: without an end session endpoint no redirect is issued and the caller has to handle it. */
    @Test
    public void handleWithOverrideShouldReturnFalseWhenNoEndSessionEndpointIsAvailable() throws IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(null);

        assertFalse(logoutHandler.handle(request, response, authentication, SSO_REENTRY_URL));

        verify(redirectStrategy, never()).sendRedirect(any(), any(), anyString());
    }

    /** Nothing can be sent once the response is committed. */
    @Test
    public void handleWithOverrideShouldReturnFalseWhenTheResponseIsAlreadyCommitted() throws IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(
            registrationWithEndSessionEndpoint());
        when(response.isCommitted()).thenReturn(true);

        assertFalse(logoutHandler.handle(request, response, authentication, SSO_REENTRY_URL));

        verify(redirectStrategy, never()).sendRedirect(any(), any(), anyString());
    }
}
