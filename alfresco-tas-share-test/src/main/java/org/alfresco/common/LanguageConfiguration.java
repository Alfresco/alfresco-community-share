/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * 
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bogdan Bocancea
 */
@Configuration
public class LanguageConfiguration
{
    @Bean
    public Language language(@Value("language/page_labels") String languageResource,
                             @Value("${locale.language}") String language,
                             @Value("${locale.country}") String country)
    {
        return new Language(languageResource, language, country);
    }
}
