/*
 * #%L
 * Alfresco Records Management Module
 * %%
 * Copyright (C) 2005 - 2025 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * -
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 * -
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * -
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * -
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import org.json.simple.JSONObject;

/**
 * Frozen indicator shown in non-RM sites that contain frozen active content
 *
 * @author: Ross Gale
 * @author: Sara Aspery
 */
public class DocLibFrozenIndicatorEvaluator extends BaseRMEvaluator
{

    private static final String ASPECT_FROZEN = "rma:frozen";

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;

        if (getNodeAspects(jsonObject).contains(ASPECT_FROZEN))
        {
            result = true;
        }

        return result;
    }
}

