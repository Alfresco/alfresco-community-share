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
package org.alfresco.web.config.properties;

import org.alfresco.web.config.util.BaseTest;
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.junit.Assert;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.xml.XMLConfigService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AIMSConfigTest extends BaseTest
{
    public static final String CLASSPATH_SHARE_CONFIG_PROPERTIES = "classpath*:alfresco/module/*/share-config.properties";
    private static final String TEST_CONFIG_AIMS_BASIC_XML = "test-config-aims-basic.xml";

    private List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add(TEST_CONFIG_AIMS_BASIC_XML);
        return result;
    }

    public String getResourcesDir()
    {
        return "classpath:";
    }

    /**
     * Clear system properties before each test run so we won't clutter the environment
     */
    private void clearSystemProperties()
    {
        System.clearProperty("aims.enabled");
        System.clearProperty("aims.realm");
        System.clearProperty("aims.resource");
        System.clearProperty("aims.authServerUrl");
        System.clearProperty("aims.sslRequired");
        System.clearProperty("aims.publicClient");
        System.clearProperty("aims.autodetectBearerOnly");
        System.clearProperty("aims.alwaysRefreshToken");
        System.clearProperty("aims.principalAttribute");
        System.clearProperty("aims.enableBasicAuth");
        System.clearProperty("aims.secret");
        System.clearProperty("aims.scopes");
        System.clearProperty("aims.atIssuerAttribute");
    }

    /**
     *
     * @return
     */
    private AIMSConfig initAIMSConfig()
    {
        Resource[] resources;
        AIMSConfig aimsConfig = new AIMSConfig();

        try
        {
            // Define properties from a property file
            resources = new PathMatchingResourcePatternResolver().getResources(CLASSPATH_SHARE_CONFIG_PROPERTIES);
            XMLConfigService configService = initXMLConfigServiceWithProperties(this.getConfigFiles(), resources);
            Assert.assertNotNull(configService);

            Config config = configService.getConfig("AIMS");
            Assert.assertNotNull(config);

            aimsConfig.setConfigService(configService);
            aimsConfig.init();
        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

        return aimsConfig;
    }

    /**
     * Test if default properties are set correctly
     */
    public void testDefaultPropertiesAreSetCorrectly()
    {
        this.clearSystemProperties();

        AIMSConfig aimsConfig = this.initAIMSConfig();

        Assert.assertFalse(aimsConfig.isEnabled());
        Assert.assertEquals(Set.of("email", "profile", "openid"), aimsConfig.getScopes());
        Assert.assertEquals("issuer", aimsConfig.getAtIssuerAttribute());
    }

    /**
     * Test if custom properties are set correctly
     */
    public void testCustomPropertiesAreSetCorrectly()
    {
        this.clearSystemProperties();

        AIMSConfig aimsConfig = this.initAIMSConfig();
        aimsConfig.setScopes("customScope1, customScope2");
        aimsConfig.setAtIssuerAttribute("access_token_issuer");

        Assert.assertFalse(aimsConfig.isEnabled());
        Assert.assertEquals(Set.of("customScope1", "customScope2"), aimsConfig.getScopes());
        Assert.assertEquals("access_token_issuer", aimsConfig.getAtIssuerAttribute());
    }
}
