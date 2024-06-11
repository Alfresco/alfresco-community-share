package org.alfresco.web.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.alfresco.web.site.servlet.AIMSLogoutHandler;
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AIMSLogoutHandlerTest
{

    ClientRegistration clientRegistration;
    @Mock
    ClientRegistrationRepository clientRegistrationRepository;
    @Mock
    AIMSLogoutHandler logoutHandler;
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    Authentication authentication;
    @Mock
    AIMSConfig aimsConfig;
    @Mock
    RedirectStrategy redirectStrategy;
    Map<String, Object> endpointMap;


    @Before
    public void setup()
    {
        endpointMap = new HashMap<>();
        endpointMap.put("end_session_endpoint", "http://localhost:8080");
        request = new MockHttpServletRequest("POST", "http://localhost:8080/share/page/dashboard");

    }

    @Test
    public void getTargetUrlWhenLogoutUriIsNull() throws ServletException, IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getLogoutClientIDLabel()).thenReturn("alfresco");
        when(aimsConfig.getLogoutClientIDValue()).thenReturn("alfresco");
        doNothing().when(redirectStrategy)
            .sendRedirect(any(), any(), anyString());
        clientRegistration = ClientRegistration.withRegistrationId("alfresco")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("alfresco")
            .redirectUri("http://localhost:8080/share/page/dashboard")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("tokenuri")
            .providerConfigurationMetadata(endpointMap)
            .build();
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(clientRegistration);
        doNothing().when(logoutHandler)
            .handle(request, response, authentication);
        logoutHandler.handle(request, response, authentication);
        verify(logoutHandler, times(1)).handle(request, response, authentication);
    }

    @Test
    public void getTargetUrlWhenLogoutUriAndPostLogoutUriIsNotNull() throws ServletException, IOException
    {
        when(aimsConfig.getResource()).thenReturn("alfresco");
        when(aimsConfig.getLogoutUri()).thenReturn("http://localhost:8999/auth");
        endpointMap.put("post_redirect_uri", "http://localhost:8999/auth");
        clientRegistration = ClientRegistration.withRegistrationId("123")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientId("alfresco")
            .redirectUri("http://localhost:8080/share/page/dashboard")
            .authorizationUri("http://localhost:8999/auth")
            .tokenUri("tokenuri")
            .providerConfigurationMetadata(endpointMap)
            .build();
        when(aimsConfig.getLogoutClientIDLabel()).thenReturn("client_id");
        when(aimsConfig.getLogoutClientIDValue()).thenReturn("alfresco");
        when(aimsConfig.getPostLogoutRedirectUrlLabel()).thenReturn("post_logout_redirect_uri");
        when(aimsConfig.getPostLogoutRedirectUrlValue()).thenReturn("http://localhost:8999/auth");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(clientRegistration);
        doNothing().when(redirectStrategy)
            .sendRedirect(any(), any(), anyString());
        doNothing().when(logoutHandler)
            .handle(request, response, authentication);
        logoutHandler.handle(request, response, authentication);
        verify(logoutHandler, times(1)).handle(request, response, authentication);

    }

    @Test
    public void getTargetUrlWhenClientRegistrationIsNull() throws ServletException, IOException
    {
        when(aimsConfig.getResource()).thenReturn("123");
        when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(null);
        doNothing().when(logoutHandler)
            .handle(request, response, authentication);
        logoutHandler.handle(request, response, authentication);
        verify(logoutHandler, times(1)).handle(request, response, authentication);

    }
}
