package org.alfresco.po.alfrescoconsoles;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Mirela Tifui on 11/16/2017.
 */
@PageObject
public class BulkImportConsolePage extends Console<BulkImportConsolePage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/service/bulkfsimport/inplace";
    }
}
