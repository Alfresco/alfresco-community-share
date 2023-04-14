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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;

public class AIMSConfig
{
    private static final Log logger = LogFactory.getLog(AIMSConfig.class);

    private boolean enabled;
    private String realm;
    private String resource;
    private String secret;
    private String authServerUrl;
    private String sslRequired;
    private String principalAttribute;
    private ConfigService configService;
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

        if(!StringUtils.isEmpty(config.getConfigElementValue("secret")))
            this.setSecret(config.getConfigElementValue("secret"));
        else
            this.setSecret(config.getConfigElementValue("resource"));

        if(!StringUtils.isEmpty(config.getConfigElementValue("principalAttribute")))
            this.setPrincipalAttribute(config.getConfigElementValue("principalAttribute"));
        else
            this.setPrincipalAttribute("sub");

    }

    /**
     *
     * @param configService ConfigService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     *
     * @param enabled boolean
     */
    private void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     *
     * @return boolean
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    public String getRealm() { return realm; }

    public void setRealm(String realm) { this.realm = realm; }

    public String getResource() { return resource; }

    public void setResource(String resource) { this.resource = resource; }

    public String getAuthServerUrl() { return authServerUrl; }

    public void setAuthServerUrl(String authServerUrl) { this.authServerUrl = authServerUrl; }

    public String getSslRequired() { return sslRequired; }

    public void setSslRequired(String sslRequired) { this.sslRequired = sslRequired; }

    public String getSecret() { return secret; }

    public void setSecret(String secret) { this.secret = secret; }

    public String getPrincipalAttribute() { return principalAttribute; }

    public void setPrincipalAttribute(String principalAttribute) { this.principalAttribute = principalAttribute; }
}
