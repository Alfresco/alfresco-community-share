package org.alfresco.adminconsole.alfrescoConsoleTest;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.consoles.BulkImportConsolePages;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/16/2017.
 */
@Slf4j
public class BulkImportConsolePageTests extends BaseTest
{
    private BulkImportConsolePages bulkImportConsolePages;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void accessBulkImportPage()
    {
        bulkImportConsolePages = new BulkImportConsolePages(webDriver);
        log.info("Step 1: Access Bulk Import Console page");
        bulkImportConsolePages.navigate();
        bulkImportConsolePages.assertPageTitleIs("Bulk Filesystem In-place Import Tool");
        bulkImportConsolePages.assertBatchSizeInputIsDisplayed();
        bulkImportConsolePages.assertContentStoreInputIsDisplayed();
        bulkImportConsolePages.assertImportDirectoryPathInputIsDisplayed();
        bulkImportConsolePages.assertTargetPathInputIsDisplayed();
        bulkImportConsolePages.assertNumThreadsInputIsDisplayed();
        bulkImportConsolePages.assertSubmitButtonIsDisplayed();
    }
}
