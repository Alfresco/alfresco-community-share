/*
 * #%L
 * Alfresco Repository
 * %%
 * Copyright (C) 2005 - 2025 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.web.site;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentUrlResolver extends BaseProcessorExtension implements TemplateMethodModelEx
{

    private final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    /**
     * Executes the method with the provided list of arguments for ftl files.
     */
    @Override
    public Object exec(List list) throws TemplateModelException
    {
        if (CollectionUtils.isEmpty(list))
        {
            return "";
        }
        Object arg0 = list.get(0);
        String propertyKey = null;
        if (arg0 instanceof SimpleScalar)
        {
            propertyKey = ((SimpleScalar) arg0).getAsString();
        }
        return resolvePropertyValue(propertyKey, list.subList(1, list.size()).toArray());
    }


    /**
     * Executes the method with the provided message and arguments for JS files.
     */
    public Object get(String message, Object... args)
    {
        return resolvePropertyValue(message, args);
    }


    private String resolvePropertyValue(String message, Object... args)
    {
        if (message == null)
        {
            return null;
        }
        String resolved = resolvePlaceHolders(message);

        if (args != null && args.length > 0)
        {
            return MessageFormat.format(resolved, args);
        }
        return resolved;
    }

    private String resolvePlaceHolders(String message)
    {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();
        boolean replaced = false;
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement;
            String value = I18NUtil.getMessage(key);
            if (StringUtils.isNotEmpty(value)) {
                replacement = resolvePlaceHolders(value);
                replaced = true;
            } else {
                replacement = "${" + key + "}";
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return replaced ? resolvePlaceHolders(result.toString()) : result.toString();
    }
}
