/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Bogdan Bocancea
 */
@Configuration
@ComponentScan(basePackages = "org.alfresco")
@ImportResource({"classpath*:alfresco-tester-context.xml", "classpath:dataprep-context.xml"})
@PropertySource({"dataprep.properties"})
public class ShareTestContext
{
}
