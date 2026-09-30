package org.alfresco.web.site.servlet;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.StringUtils;
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
        return this.determineTargetUrl(request, response, authentication, null);
    }

    /**
     * Builds the IdP front-channel logout URL.
     *
     * @param request                       HTTP Servlet Request
     * @param response                      HTTP Servlet Response
     * @param authentication                the authentication to log out
     * @param postLogoutRedirectUriOverride when not empty, the URL the IdP must send the user back to, taking
     *                                      precedence over the configured post logout URL. Used when a login has
     *                                      to be aborted and the user must land on a specific Share page.
     * @return the IdP end session URL, or null if no end session endpoint is available
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication, String postLogoutRedirectUriOverride)
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

            String postLogoutRedirectUriLabel = aimsConfig.getPostLogoutRedirectUrlLabel() != null
                                                ? aimsConfig.getPostLogoutRedirectUrlLabel()
                                                : "post_logout_redirect_uri";

            if (StringUtils.isNotEmpty(postLogoutRedirectUriOverride))
            {
                // Forced target (e.g. the Share entry point when a login is aborted) - must win over the
                // configured post logout URL.
                logoutMap.put(postLogoutRedirectUriLabel, postLogoutRedirectUriOverride);
            }
            else
            {
                URI postLogoutRedirectUri = this.postLogoutRedirectUri(request, clientRegistration);
                if (postLogoutRedirectUri != null)
                {
                    if (aimsConfig.getPostLogoutRedirectUrlLabel() != null)
                    {
                        logoutMap.put(aimsConfig.getPostLogoutRedirectUrlLabel(),
                                      aimsConfig.getPostLogoutRedirectUrlValue()
                                      != null ? aimsConfig.getPostLogoutRedirectUrlValue() : postLogoutRedirectUri.toString());
                    }
                    else
                    {
                        logoutMap.put("post_logout_redirect_uri", postLogoutRedirectUri.toString());
                    }
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
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request))
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

    /**
     * Terminates the IdP session and asks the IdP to send the user back to the given URL.
     *
     * @param request                       HTTP Servlet Request
     * @param response                      HTTP Servlet Response
     * @param authentication                the authentication to log out
     * @param postLogoutRedirectUriOverride the URL the IdP must redirect to once the session is terminated
     * @return true if the redirect to the IdP end session endpoint has been issued, false otherwise (no end session
     *         endpoint available or the response was already committed) - the caller then has to redirect the user
     * @throws IOException
     */
    public boolean handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                          String postLogoutRedirectUriOverride) throws IOException
    {
        String targetUrl = this.determineTargetUrl(request, response, authentication, postLogoutRedirectUriOverride);
        logger.debug("Value of targetUrl is: " + targetUrl);
        if (StringUtils.isEmpty(targetUrl))
        {
            logger.warn("No IdP end session endpoint is available, the IdP session can not be terminated.");
            return false;
        }
        if (response.isCommitted())
        {
            logger.error("Can't perform the redirect for the targetUrl: " + targetUrl);
            return false;
        }
        this.redirectStrategy.sendRedirect(request, response, targetUrl);
        return true;
    }
}
