/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.util.UriComponentsBuilder;

public class AIMSConfig
{
    private static final Log LOGGER = LogFactory.getLog(AIMSConfig.class);

    private boolean enabled;
    private String realm;
    private String resource;
    private String secret;
    private String authServerUrl;
    private String sslRequired;
    private String principalAttribute;
    private ConfigService configService;
    private boolean alwaysRefreshToken;
    private String audience;
    private Boolean publicClient;
    private String redirectURI;
   private String logoutUri;
   private String postLogoutUrl;
   private Boolean userIdTokenHint;
   private String postLogoutRedirectUrlLabel;
   private String postLogoutRedirectUrlValue;
   private String logoutClientIDLabel;
   private String logoutClientIDValue;
   private static final String REALMS = "realms";
    private static final String DEFAULT_SCOPES = "openid,profile,email";
    private String shareContext;
   private String atIssuerAttribute;
   private Set<String> scopes;
   private String[] extraEndpointsToRedirect;

    /**
     *
     */
    public void init()
    {
        Config config = this.configService.getConfig("AIMS");
        this.setEnabled(Boolean.parseBoolean(config.getConfigElement("enabled").getValue()));
        this.setRealm(config.getConfigElementValue("realm"));
        this.setResource(config.getConfigElementValue("resource"));
        this.setAuthServerUrl(config.getConfigElementValue("authServerUrl"));
        this.setSslRequired(config.getConfigElementValue("sslRequired"));
        this.setPublicClient(Boolean.parseBoolean(config.getConfigElement("publicClient").getValue()));
        this.setAudience(config.getConfigElementValue("audience"));
        this.setScopes(config.getConfigElementValue("scopes"));
        this.setAtIssuerAttribute(config.getConfigElementValue("atIssuerAttribute"));
        if (publicClient)
        {
            this.setSecret(null);
        }
        else
        {
            if (!StringUtils.isEmpty(config.getConfigElementValue("secret")))
            {
                this.setSecret(config.getConfigElementValue("secret"));
            }
            else
            {
                OAuth2Error oauth2Error = new OAuth2Error("Missing secret-key value. Please provide a Secret Key");
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }
        }

        if (!StringUtils.isEmpty(config.getConfigElementValue("principalAttribute")))
        {
            this.setPrincipalAttribute(config.getConfigElementValue("principalAttribute"));
        }
        else
        {
            this.setPrincipalAttribute("preferred_username");
        }
        if (!StringUtils.isEmpty(config.getConfigElementValue("alwaysRefreshToken")))
        {
            this.setAlwaysRefreshToken(Boolean.parseBoolean(config.getConfigElement("alwaysRefreshToken")
                                                                .getValue()));
        }
        else
        {
            this.setAlwaysRefreshToken(false);
        }
        if (!StringUtils.isEmpty(config.getConfigElementValue("redirectURI")))
        {
            this.setRedirectURI((config.getConfigElement("redirectURI")
                .getValue()));
        }
        else
        {
            this.setRedirectURI(null);
        }
        if (!StringUtils.isEmpty(config.getConfigElementValue("logoutUri")))
        {
            this.setLogoutUri((config.getConfigElement("logoutUri")
                .getValue()));
        }
        else
        {
            this.setLogoutUri(null);
        }

        if (!StringUtils.isEmpty(config.getConfigElementValue("useIdTokenHint")))
        {
            this.setUserIdTokenHint(Boolean.parseBoolean(config.getConfigElement("useIdTokenHint")
                                                             .getValue()));
        }
        else
        {
            this.setUserIdTokenHint(true);
        }

        if (!StringUtils.isEmpty(config.getConfigElementValue("postLogoutRedirectUrlValue")))
        {
            this.setPostLogoutRedirectUrlValue(config.getConfigElementValue("postLogoutRedirectUrlValue"));
        }
        else
        {
            this.setPostLogoutRedirectUrlValue(null);
        }

        if (!StringUtils.isEmpty(config.getConfigElementValue("postLogoutRedirectUrlLabel")))
        {
            this.setPostLogoutRedirectUrlLabel(config.getConfigElementValue("postLogoutRedirectUrlLabel"));
        }
        else
        {
            this.setPostLogoutRedirectUrlLabel(null);
        }
        if (!StringUtils.isEmpty(config.getConfigElementValue("logoutClientIDLabel")))
        {
            this.setLogoutClientIDLabel(config.getConfigElementValue("logoutClientIDLabel"));
        }
        else
        {
            this.setLogoutClientIDLabel(null);
        }

        if (!StringUtils.isEmpty(config.getConfigElementValue("logoutClientIDValue")))
        {
            this.setLogoutClientIDValue(config.getConfigElementValue("logoutClientIDValue"));
        }
        else
        {
            this.setLogoutClientIDValue(null);
        }
        if (!StringUtils.isEmpty(config.getConfigElementValue("shareContext")))
        {
            this.setShareContext(config.getConfigElementValue("shareContext"));
        }
        else
        {
            this.setShareContext("/share");
        }

        if (!StringUtils.isEmpty(config.getConfigElementValue("extraEndpointsToRedirect")))
        {
            String endpoints = config.getConfigElementValue("extraEndpointsToRedirect");
            try
            {
                this.setExtraEndpointsToRedirect(endpoints.split(","));
            }
            catch (Exception e)
            {
                this.setExtraEndpointsToRedirect(null);
            }
        }
        else
        {
            this.setExtraEndpointsToRedirect(null);
        }
    }

    /**
     * @param configService ConfigService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * @param enabled boolean
     */
    private void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * @return boolean
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public String getResource()
    {
        return resource;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public String getAuthServerUrl()
    {
        return Optional.ofNullable(realm)
            .filter(StringUtils::isNotBlank)
            .filter(realm -> StringUtils.isNotBlank(authServerUrl))
            .map(realm -> UriComponentsBuilder.fromUriString(authServerUrl)
                .pathSegment(REALMS, realm)
                .build()
                .toString())
            .orElse(authServerUrl);
    }

    public void setAuthServerUrl(String authServerUrl)
    {
        this.authServerUrl = authServerUrl;
    }

    public String getSslRequired()
    {
        return sslRequired;
    }

    public void setSslRequired(String sslRequired)
    {
        this.sslRequired = sslRequired;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getPrincipalAttribute()
    {
        return principalAttribute;
    }

    public void setPrincipalAttribute(String principalAttribute)
    {
        this.principalAttribute = principalAttribute;
    }

    public Boolean getPublicClient()
    {
        return publicClient;
    }

    public void setPublicClient(Boolean publicClient)
    {
        this.publicClient = publicClient;
    }

    public boolean isAlwaysRefreshToken()
    {
        return alwaysRefreshToken;
    }

    public void setAlwaysRefreshToken(boolean alwaysRefreshToken)
    {
        this.alwaysRefreshToken = alwaysRefreshToken;
    }

    public String getAudience()
    {
        return audience;
    }

    public void setAudience(String audience)
    {
        this.audience = audience;
    }

    public String getRedirectURI()
    {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI)
    {
        this.redirectURI = redirectURI;
    }

    public String getLogoutUri()
    {
        return logoutUri;
    }

    public void setLogoutUri(String logoutUri)
    {
        this.logoutUri = logoutUri;
    }

    public String getPostLogoutUrl()
    {
        return postLogoutUrl;
    }

    public void setPostLogoutUrl(String postLogoutUrl)
    {
        this.postLogoutUrl = postLogoutUrl;
    }

    public Boolean getUserIdTokenHint()
    {
        return userIdTokenHint;
    }

    public void setUserIdTokenHint(Boolean userIdTokenHint)
    {
        this.userIdTokenHint = userIdTokenHint;
    }

    public String getPostLogoutRedirectUrlLabel()
    {
        return postLogoutRedirectUrlLabel;
    }

    public void setPostLogoutRedirectUrlLabel(String postLogoutRedirectUrlLabel)
    {
        this.postLogoutRedirectUrlLabel = postLogoutRedirectUrlLabel;
    }

    public String getLogoutClientIDLabel()
    {
        return logoutClientIDLabel;
    }

    public void setLogoutClientIDLabel(String logoutClientIDLabel)
    {
        this.logoutClientIDLabel = logoutClientIDLabel;
    }

    public String getPostLogoutRedirectUrlValue()
    {
        return postLogoutRedirectUrlValue;
    }

    public void setPostLogoutRedirectUrlValue(String postLogoutRedirectUrlValue)
    {
        this.postLogoutRedirectUrlValue = postLogoutRedirectUrlValue;
    }

    public String getLogoutClientIDValue()
    {
        return logoutClientIDValue;
    }

    public void setLogoutClientIDValue(String logoutClientIDValue)
    {
        this.logoutClientIDValue = logoutClientIDValue;
    }

    public String getShareContext() {
        return shareContext;
    }

    public void setShareContext(String shareContext) {
        this.shareContext = shareContext;
    }

    public void setScopes(String scopes)
    {
        this.scopes = Stream.of(Optional.ofNullable(scopes)
                .filter(StringUtils::isNotBlank)
                .orElse(DEFAULT_SCOPES).split(","))
            .map(String::trim)
            .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> getScopes()
    {
        return scopes;
    }

    public String getAtIssuerAttribute()
    {
        return atIssuerAttribute;
    }

    public void setAtIssuerAttribute(String atIssuerAttribute)
    {
        this.atIssuerAttribute = atIssuerAttribute;
    }

    public String[] getExtraEndpointsToRedirect() {
        return extraEndpointsToRedirect;
    }

    public void setExtraEndpointsToRedirect(String[] extraEndpointsToRedirect) {
        this.extraEndpointsToRedirect = extraEndpointsToRedirect;
    }
}
