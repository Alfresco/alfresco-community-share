package org.alfresco.share.alfrescoContent.viewingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bogdan.simion
 */
public class ViewingFolderDetailsTest extends ContextAwareWebTest
{
    private final String userName = String.format("userName%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String folderName = "testFolder";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C5850", "C5850");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5850")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void viewFolderDetails()
    {
        LOG.info("Step 1 - Navigate to 'Document Library' page for 'testSite'.");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageHeader(), siteName, siteName + " Document Library is not displayed");
        assertTrue(documentLibraryPage.isDocumentListDisplayed(), "There is no file added in the file list");

        LOG.info("Step 2 - Verify the 'Options' menu. Make sure that 'Hide Folders' isn't selected. Otherwise check 'Show Folders'.");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options Menu is not displayed");
        assertTrue(documentLibraryPage.isHideFoldersMenuOptionDisplayed(), "Hide Folders menu option is not displayed");

        LOG.info("Step 3 - Hover over a folder (e.g. testFolder) in the file list in the document 'Library' and click on 'View Details' icon.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.VIEW_DETAILS);
        assertTrue(documentDetailsPage.isFilePropertiesDetailsDisplayed(), "File Properties details are not displayed");
        assertTrue(documentDetailsPage.isFolderActionsPanelDisplayed(), "Folder actions panel is not displayed");
        assertTrue(documentDetailsPage.isSocialFeaturesActionsPanelDisplayed(), "Social features actions panel is not displayed");
        assertTrue(documentDetailsPage.isTagsFeaturePanelDisplayed(), "Tags feature panel is not displayed");

        LOG.info("Step 4 - Click the folder in the breadcrumb trail at the top of the screen to return to the item list for that folder.");
        documentDetailsPage.clickOnFolderFromBreadcrumbTrail();
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isDocumentListDisplayed(), "Documents list for folder is not displayed");
    }
}
