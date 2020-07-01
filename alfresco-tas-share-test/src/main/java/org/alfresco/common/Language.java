/*
 * Copyright 2005-2020 Alfresco Software, Ltd. All rights reserved.
 * 
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Bogdan Bocancea
 */
public class Language
{
    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    public Language(String languageResource, String language, String country)
    {
        currentLocale = new Locale(language, country);
        resourceBundle = ResourceBundle.getBundle(languageResource, currentLocale);
    }

    /**
     * All strings are saved as key=value
     * Just pass the key that you want to receive from resource file
     * @param key
     * @return
     */
    public String translate(String key)
    {
        try
        {
            return resourceBundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }
}
