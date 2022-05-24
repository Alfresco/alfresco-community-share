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
package org.alfresco.web.evaluator.doclib.action;

import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

/**
 * For the node to be editable it needs to be in a supported mimetype and have a valid file extension (matching the
 * mimetype)
 *
 * @author evasques
 */
public class IsNodeEditableAos extends BaseEvaluator
{
    private static final String PROP_NAME = "cm:name";

    private Map<String, String> mimetypeExtensionMap;

    /**
     * Define the map of mimetype/extension pair for this evaluator
     *
     * @param mimetypeExtensionMap
     */
    public void setMimetypeExtensionMap(Map<String, String> mimetypeExtensionMap)
    {
        this.mimetypeExtensionMap = mimetypeExtensionMap;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (mimetypeExtensionMap.size() == 0)
        {
            return false;
        }

        try
        {
            // mimetype and name must not be empty
            String mimetype = (String) getNodeMimetype(jsonObject);
            String name = (String) getProperty(jsonObject, PROP_NAME);
            if (StringUtils.isEmpty(mimetype) || StringUtils.isEmpty(name))
            {
                return false;
            }

            // filename must have a an extension
            String fileExtension = FilenameUtils.getExtension(name);
            if (StringUtils.isEmpty(fileExtension))
            {
                return false;
            }

            // mimetype of the file must be supported (i.e. must be in the map and have an expected extension) and the
            // expected extension for the mimetype needs to match the extension in the filename
            String expectedExtension = mimetypeExtensionMap.get(mimetype);
            if (expectedExtension == null || !expectedExtension.equals(fileExtension))
            {
                return false;
            }

        }
        catch (Exception ex)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + ex.getMessage());
        }

        return true;
    }

}
