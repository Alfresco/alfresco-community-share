package org.alfresco.web.site.servlet;

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
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component("aimslogouthandler")
public class AIMSLogoutHandler {

    private static final Log logger = LogFactory.getLog(AIMSLogoutHandler.class);
    private String clientId;
    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private AIMSConfig aimsConfig;
    private String postLogoutRedirectUri;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrl = null;
            ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(aimsConfig.getResource());
            URI endSessionEndpoint = this.endSessionEndpoint(clientRegistration);
            if (endSessionEndpoint != null) {
                String idToken = this.idToken(authentication);
                URI postLogoutRedirectUri = this.postLogoutRedirectUri(request,clientRegistration);
                targetUrl = this.endpointUri(endSessionEndpoint, idToken, postLogoutRedirectUri);
            }
        return targetUrl;
    }

    private URI endSessionEndpoint(ClientRegistration clientRegistration) {
        URI result = null;
        if (clientRegistration != null) {
            Object endSessionEndpoint = clientRegistration.getProviderDetails().getConfigurationMetadata().get("end_session_endpoint");
            if (endSessionEndpoint != null) {
                result = URI.create(endSessionEndpoint.toString());
            }
        }
        return result;
    }

    private String idToken(Authentication authentication) {
        return ((OidcUser)authentication.getPrincipal()).getIdToken().getTokenValue();
    }

    private URI postLogoutRedirectUri(HttpServletRequest request,ClientRegistration clientRegistration) {
        if (clientRegistration != null) {
            String postLogoutEndpoint = (String)clientRegistration.getProviderDetails().getConfigurationMetadata().get("post_redirect_uri");
            UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(UrlUtils.buildFullRequestUrl(request)).replacePath(request.getContextPath()).replaceQuery(null).fragment(null).build();
            if (postLogoutEndpoint == null) {
                return UriComponentsBuilder.fromUriString(request.getRequestURL() + "?success").buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString())).toUri();
            } else {
                return UriComponentsBuilder.fromUriString(postLogoutEndpoint).buildAndExpand(Collections.singletonMap("baseUrl", uriComponents.toUriString())).toUri();
            }
        }
        return null;
    }

    private String endpointUri(URI endSessionEndpoint, String idToken, URI postLogoutRedirectUri) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endSessionEndpoint);
        builder.queryParam("id_token_hint", new Object[]{idToken});
        if (postLogoutRedirectUri != null) {
            builder.queryParam("post_logout_redirect_uri", new Object[]{postLogoutRedirectUri});
        }

        return builder.encode(StandardCharsets.UTF_8).build().toUriString();
    }

    /** @deprecated */
    @Deprecated
    public void setPostLogoutRedirectUri(URI postLogoutRedirectUri) {
        Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
        this.postLogoutRedirectUri = postLogoutRedirectUri.toASCIIString();
    }

    public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
        Assert.notNull(postLogoutRedirectUri, "postLogoutRedirectUri cannot be null");
        this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response, authentication);
        logger.debug("Value of targetUrl is: " + targetUrl);
        if (response.isCommitted()) {
            logger.error("Can't perform the redirect for the targetUrl: " + targetUrl);
        } else {
            this.redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }
}
