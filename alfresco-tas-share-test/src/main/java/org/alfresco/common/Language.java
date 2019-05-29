package org.alfresco.common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Handle internationalization
 *
 * @author Paul.Brodner
 */

public class Language
{
    Locale currentLocale;
    ResourceBundle resourceBundle;

    public Language(String languageResource, String language, String country)
    {
        currentLocale = new Locale(language, country);
        resourceBundle = ResourceBundle.getBundle(languageResource, currentLocale);
    }

    /**
     * All strings are saved as key=value
     * Just pass the key that you want to receive from resource file
     *
     * @param key
     * @return
     */
    public String translate(String key)
    {
        try
        {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }
}
