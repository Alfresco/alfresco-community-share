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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;

public class AIMSConfig
{
    private static final Log logger = LogFactory.getLog(AIMSConfig.class);

    private boolean enabled;
    private ConfigService configService;
    /**
     *
     */
    public void init()
    {
        ConfigElement config = this.configService.getConfig("AIMS")
            .getConfigElement("enabled");
        this.setEnabled(Boolean.parseBoolean(config.getValue()));
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

    /**
     *
     * @param config Config
     */
}
