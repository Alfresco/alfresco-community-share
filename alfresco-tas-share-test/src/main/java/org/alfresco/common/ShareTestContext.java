/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.common;

import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.browser.WebBrowserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

/**
 * @author Bogdan Bocancea
 */
@Configuration
@ComponentScan(basePackages = "org.alfresco")
@ImportResource({"classpath*:alfresco-tester-context.xml", "classpath:dataprep-context.xml"})
@PropertySource({"dataprep.properties"})
public class ShareTestContext
{
   /* @Autowired
    private WebBrowserFactory browserFactory;

    @Bean
    public ThreadLocal<WebBrowser> browser() throws Exception
    {
        ThreadLocal<WebBrowser> browser = new ThreadLocal<>();
        browser.set(browserFactory.getWebBrowser());
        return browser;
    }*/
}
