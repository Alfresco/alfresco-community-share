package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author bogdan.simion
 */
public class ViewingFolderDetailsTest extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    private String userName = "testUser" + DataUtil.getUniqueIdentifier();
    private String siteName = "testSite" + DataUtil.getUniqueIdentifier();
    private String folderName = "testFolder";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C5850", "C5850");
        siteService.create(userName, password, domain, siteName, siteName, Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C5850")
    @Test
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
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickMore();
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        assertTrue(documentDetailsPage.isFilePropertiesDetailsDisplayed(), "File Properties details are not displayed");
        assertTrue(documentDetailsPage.isFolderActionsPanelDisplayed(), "Folder actions panel is not displayed");
        assertTrue(documentDetailsPage.isSocialFeaturesActionsPanelDisplayed(), "Social features actions panel is not displayed");
        assertTrue(documentDetailsPage.isTagsFeaturePanelDisplayed(), "Tags feature panel is not displayed");

        LOG.info("Step 4 - Click the folder in the breadcrumb trail at the top of the screen to return to the item list for that folder.");
        documentDetailsPage.clickOnFolderFromBreadrcumbTrail();
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isDocumentListDisplayed(), "Documents list for folder is not displayed");
    }
}
