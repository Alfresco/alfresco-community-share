package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.PreviewFileActionsSection;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ViewingAFileInBrowserTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    PreviewFileActionsSection documentActions;

    private String user = "C5920User" + DataUtil.getUniqueIdentifier();
    private String siteName = "C5920SiteName" + DataUtil.getUniqueIdentifier();
    private String description = "C5920SiteDescription" + DataUtil.getUniqueIdentifier();
    private String docName = "File-C5920";
    private String folderName = "testFolder";

    @BeforeClass
    public void setupTest()
    {

        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C5920")
    @Test

    public void viewingAFileInBrowser()
    {
        LOG.info("Step 1: Navigate to Document Library page for testSite.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentDetailsPage.isDocumentLibraryOpened(siteName), "Document Library is not opened!");
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is not displayed!");

        LOG.info("Step 2: Click on a folder (e.g. testFolder) and then hover over a file in the document library (e.g. testFile) .");
        documentLibraryPage.clickOnFolderName(folderName);
        getBrowser().waitInSeconds(1);
        documentLibraryPage.mouseOverFileName(docName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Edit in Google Docs™"),
                "Edit in Google Docs™ is not available for file");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Download"), "Download is not available for test document");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "View In Browser"),
                "View In Browser is not available for test document");
        Assert.assertTrue(documentLibraryPage.isMoreMenuDisplayed(docName), "More menu is not displayed");

        LOG.info("Step 3: Click View in getBrowser().");
        documentLibraryPage.clickOnAction(docName, "View In Browser");
        Assert.assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), "Document content",
                "File content is not correct or file has not be opened in new window");
    }

}
