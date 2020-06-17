package org.alfresco.share.alfrescoConsoleTest;

import org.alfresco.po.alfrescoconsoles.BulkImportConsolePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/16/2017.
 */
public class BulkImportConsolePageTests extends ContextAwareWebTest
{
    @Autowired
    BulkImportConsolePage bulkImportConsolePage;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void accessBulkImportPage()
    {
        LOG.info("Step 1: Access Bulk Import Console page");
        bulkImportConsolePage.setBrowser(getBrowser());
        bulkImportConsolePage.navigate();
        Assert.assertEquals(bulkImportConsolePage.getPageTitle(), "Bulk Filesystem In-place Import Tool", "In-place Import Tool is not accessed");
    }
}
