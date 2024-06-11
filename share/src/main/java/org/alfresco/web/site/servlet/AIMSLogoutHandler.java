package org.alfresco.web.site.servlet;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("aimslogouthandler")
public class AIMSLogoutHandler
{

    private static final Log logger = LogFactory.getLog(AIMSLogoutHandler.class);
    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private AIMSConfig aimsConfig;
    private String postLogoutRedirectUri;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
    {
        String targetUrl = null;
        HashMap<String, String> logoutMap = new HashMap<>();
        ClientRegistration clientRegistration =
            this.clientRegistrationRepository.findByRegistrationId(aimsConfig.getResource());
        URI endSessionEndpoint = this.endSessionEndpoint(clientRegistration);
        if (endSessionEndpoint != null)
        {
            if (aimsConfig.getUserIdTokenHint())
            {
                logoutMap.put("id_token_hint", this.idToken(authentication));
            }
            if (aimsConfig.getLogoutClientIDLabel() != null)
            {
                logoutMap.put(aimsConfig.getLogoutClientIDLabel(), aimsConfig.getLogoutClientIDValue());
            }
            URI postLogoutRedirectUri = this.postLogoutRedirectUri(request, clientRegistration);
            if (postLogoutRedirectUri != null)
            {
                if (aimsConfig.getPostLogoutRedirectUrlLabel() != null)
                {
                    logoutMap.put(aimsConfig.getPostLogoutRedirectUrlLabel(), aimsConfig.getPostLogoutRedirectUrlValue()
                                                                                  != null ? aimsConfig.getPostLogoutRedirectUrlValue() : postLogoutRedirectUri.toString());
                }
                else
                {
                    logoutMap.put("post_logout_redirect_uri", postLogoutRedirectUri.toString());
                }
            }

            targetUrl = this.endpointUri(endSessionEndpoint, logoutMap);
        }

        return targetUrl;
    }

    private URI endSessionEndpoint(ClientRegistration clientRegistration)
    {
        URI result = null;
        if (clientRegistration != null)
        {

            if (aimsConfig.getLogoutUri() != null)
            {
                result = URI.create(aimsConfig.getLogoutUri());
            }
            else
            {
                Object endSessionEndpoint = clientRegistration.getProviderDetails()
                    .getConfigurationMetadata()
                    .get("end_session_endpoint");

                if (endSessionEndpoint != null)
                {
                    result = URI.create(endSessionEndpoint.toString());
                }
            }
        }
        return result;
    }

    private String idToken(Authentication authentication)
    {
        return ((OidcUser) authentication.getPrincipal()).getIdToken()
            .getTokenValue();
    }

    private URI postLogoutRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration)
    {

        if (clientRegistration != null)
        {
            String postLogoutEndpoint = aimsConfig.getPostLogoutUrl();

            if (postLogoutEndpoint == null)
            {
                postLogoutEndpoint = (String) clientRegistration.getProviderDetails()
                    .getConfigurationMetadata()
                    .get("post_redirect_uri");
            }
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build();
            if (postLogoutEndpoint == null || postLogoutEndpoint.isEmpty())
            {
                return UriComponentsBuilder.fromUriString(request.getRequestURL() + "?success")
                    .buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString()))
                    .toUri();
            }
            else
            {
                return UriComponentsBuilder.fromUriString(postLogoutEndpoint)
                    .buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString()))
                    .toUri();
            }
        }
        return null;
    }

    private String endpointUri(URI endSessionEndpoint, HashMap<String, String> logoutMap)
    {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endSessionEndpoint);
        logoutMap.forEach((key, value) -> builder.queryParam(key, new Object[] { value }));

        return builder.encode(StandardCharsets.UTF_8)
            .build()
            .toUriString();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setPostLogoutRedirectUri(URI postLogoutRedirectUri)
    {
        Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
        this.postLogoutRedirectUri = postLogoutRedirectUri.toASCIIString();
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException
    {
        String targetUrl = this.determineTargetUrl(request, response, authentication);
        logger.debug("Value of targetUrl is: " + targetUrl);
        if (response.isCommitted())
        {
            logger.error("Can't perform the redirect for the targetUrl: " + targetUrl);
        }
        else
        {
            this.redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }
}
