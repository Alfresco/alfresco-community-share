package org.alfresco.adminconsole.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.BulkImportConsolePage;
import org.alfresco.adminconsole.ContextAwareWebAdminConsoleTest;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/16/2017.
 */
public class BulkImportConsolePageTests extends ContextAwareWebAdminConsoleTest
{
    @Autowired
    private BulkImportConsolePage bulkImportConsolePage;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void accessBulkImportPage()
    {
        LOG.info("Step 1: Access Bulk Import Console page");
        bulkImportConsolePage.navigate()
            .assertPageTitleIs("Bulk Filesystem In-place Import Tool")
            .assertBatchSizeInputIsDisplayed()
            .assertContentStoreInputIsDisplayed()
            .assertImportDirectoryPathInputIsDisplayed()
            .assertTargetPathInputIsDisplayed()
            .assertNumThreadsInputIsDisplayed()
            .assertSubmitButtonIsDisplayed();
    }
}
