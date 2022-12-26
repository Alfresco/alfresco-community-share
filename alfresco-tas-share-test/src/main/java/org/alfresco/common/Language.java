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
        setDefaultLanguageToEnglishIfNotSet();
        setDefaultCountryToUSIfNotSet();
        resourceBundle = ResourceBundle.getBundle(languageResource, currentLocale);
    }

    private void setDefaultCountryToUSIfNotSet()
    {
        if (!Locale.getDefault().getCountry().equalsIgnoreCase("US"))
        {
            Locale.setDefault(Locale.US);
        }
    }

    private void setDefaultLanguageToEnglishIfNotSet()
    {
        if (!Locale.getDefault().getLanguage().equalsIgnoreCase("en"))
        {
            Locale.setDefault(Locale.ENGLISH);
        }
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
    public String newTranslate(String key)
    {
        try
        {
            return resourceBundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            return key;
        }
    }
}
