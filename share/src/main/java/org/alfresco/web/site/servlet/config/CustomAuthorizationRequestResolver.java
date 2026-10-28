package org.alfresco.web.site.servlet.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpServletRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver
{
    private static final Log LOGGER = LogFactory.getLog(CustomAuthorizationRequestResolver.class);

    private OAuth2AuthorizationRequestResolver defaultResolver;
    private AIMSConfig aimsConfig;
    private final static String AUDIENCE="audience";

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri,
                                              AIMSConfig aimsConfig)
    {
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
        this.aimsConfig = aimsConfig;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest httpServletRequest)
    {
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = defaultResolver.resolve(httpServletRequest);
        if (oAuth2AuthorizationRequest != null)
        {
            oAuth2AuthorizationRequest = customizeAuthorizationRequest(oAuth2AuthorizationRequest, httpServletRequest);
        }
        return oAuth2AuthorizationRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest httpServletRequest, String clientRegistrationId)
    {
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest =
            defaultResolver.resolve(httpServletRequest, clientRegistrationId);
        if (oAuth2AuthorizationRequest != null)
        {
            oAuth2AuthorizationRequest = customizeAuthorizationRequest(oAuth2AuthorizationRequest, httpServletRequest);
        }
        return oAuth2AuthorizationRequest;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                                                     HttpServletRequest request)
    {
        OAuth2AuthorizationRequest withDefaults = OAuth2AuthorizationRequest.from(authorizationRequest)
            .redirectUri(resolveRedirectUri(request))
            .additionalParameters(params -> {
                if (this.aimsConfig.getAudience() != null)
                {
                    params.put(AUDIENCE, this.aimsConfig.getAudience());
                }
            })
            .build();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromUriString(withDefaults.getAuthorizationRequestUri());
        Set<String> existingParams = getQueryParamNames(uriBuilder);

        Map<String, String> extraParams = new LinkedHashMap<>();
        this.aimsConfig.getAuthorizationAdditionalParams().forEach((name, value) -> {
            if (existingParams.contains(name))
            {
                LOGGER.warn("Ignoring additional authorization parameter " + name
                    + " because the authorization request already contains it");
                return;
            }
            extraParams.put(name, value);
            uriBuilder.queryParam(encodeQueryParam(name), encodeQueryParam(value));
        });

        return OAuth2AuthorizationRequest.from(withDefaults)
            .additionalParameters(params -> params.putAll(extraParams))
            .authorizationRequestUri(uriBuilder.build(true).toUriString())
            .build();
    }

    private String resolveRedirectUri(HttpServletRequest request)
    {
        return this.aimsConfig.getRedirectURI() != null
            ? this.aimsConfig.getRedirectURI()
            : String.valueOf(request.getRequestURL());
    }

    private static Set<String> getQueryParamNames(UriComponentsBuilder uriBuilder)
    {
        return uriBuilder.build(true)
            .getQueryParams()
            .keySet()
            .stream()
            .map(name -> URLDecoder.decode(name, StandardCharsets.UTF_8))
            .collect(Collectors.toSet());
    }

    private static String encodeQueryParam(String value)
    {
        return UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8).replace("+", "%2B");
    }
}
