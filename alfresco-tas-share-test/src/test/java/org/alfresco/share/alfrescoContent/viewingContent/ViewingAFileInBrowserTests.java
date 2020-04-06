package org.alfresco.share.alfrescoContent.viewingContent;

import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.PreviewFileActionsSection;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ViewingAFileInBrowserTests extends ContextAwareWebTest
{
    private final String user = String.format("C5920User%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C5920SiteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C5920SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String docName = "File-C5920";
    private final String folderName = "testFolder";
    @Autowired
    PreviewFileActionsSection documentActions;
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5920")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })

    public void viewingAFileInBrowser()
    {
        LOG.info("Step 1: Navigate to Document Library page for testSite.");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentDetailsPage.isDocumentLibraryOpened(siteName), "Document Library is not opened!");
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is not displayed!");
        LOG.info("Step 2: Click on a folder (e.g. testFolder) and then hover over a file in the document library (e.g. testFile) .");
        documentLibraryPage.clickOnFolderName(folderName);
        List<String> expectedActions = Arrays.asList("Download", "View In Browser", "Edit in Google Docs™");
        Assert.assertTrue(documentLibraryPage.areActionsAvailableForLibraryItem(docName, expectedActions), "Expected actions");
        Assert.assertTrue(documentLibraryPage.isMoreMenuDisplayed(docName), "More menu is not displayed");
        LOG.info("Step 3: Click View In Browser.");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "View In Browser", documentLibraryPage);
        Assert.assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), "Document content",
            "File content is not correct or file has not be opened in new window");
    }

}
