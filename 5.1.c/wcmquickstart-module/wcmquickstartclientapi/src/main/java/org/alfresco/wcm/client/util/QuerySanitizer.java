/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.util;

public class QuerySanitizer
{
    private static QuerySanitizer instance = new QuerySanitizer();
    
    protected QuerySanitizer()
    {
    }
    
    public static String sanitize(String text)
    {
        return instance.sanitizeImpl(text);
    }
    
    /**
     * Overridable sanitization method
     * @param text String
     * @return String
     */
    protected String sanitizeImpl(String text)
    {
        return text == null ? null : text.replaceAll("[\"'%?*()$^<>/{}\\[\\]#~@.,|\\\\+!:;&`¬=]", " ");
    }
    
    /**
     * Inject a new implementation if desired. Create a subclass of this class, override the sanitizeImpl operation,
     * and inject an instance of it using this operation. QuerySanitizer.sanitize will then be routed to your object.
     * @param sanitizer QuerySanitizer
     */
    public static void setSanitizer(QuerySanitizer sanitizer)
    {
        instance = sanitizer;
    }
}
