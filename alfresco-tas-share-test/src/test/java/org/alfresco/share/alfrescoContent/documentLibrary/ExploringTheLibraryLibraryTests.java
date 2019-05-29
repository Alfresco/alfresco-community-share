package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
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

public class ExploringTheLibraryLibraryTests extends ContextAwareWebTest
{
    private final String user = String.format("C6333User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C6333SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String folderName = "testFolder1";
    private final String folderName1 = "testFolder2";
    private final String docName = "testFile1";
    private final String docName1 = "testFile2";
    private final String siteName = String.format("C6333SiteName%s", RandomData.getRandomAlphanumeric());
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private DocumentsFilters filters;

    @BeforeClass (alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentService.createFolder(user, password, folderName1, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName1, DocumentType.TEXT_PLAIN, docName1, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6333")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void OpenFoldersFromTree()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Verify Library section");
        Assert.assertTrue(filters.isDocumentsLinkPresent(), "Documents link is not present");
        Assert.assertEquals(filters.getDocumentsLinkText(), "Documents", "Documents Link name is not correct ");
        Assert.assertEquals(filters.getFirstFolderName(), folderName, "First test folder is not displayed under Library Documents");
        Assert.assertEquals(filters.getSecondFolderName(), folderName1, "Second test folder is not displayed under Library Documents");

        LOG.info("Step 2: Click on folder from Library section, testFolder1");
        filters.clickFirstFolder();
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "testFile1 is not displayed");
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents, testFolder1]",
            "The breadcrumb does not display the correct position.The position currently displayed is: ");

        LOG.info("Step 3: Click on folder from Library section, testFolder2");
        filters.clickSecondFolder();
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName1), "testFile2 is not displayed");
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents, testFolder2]",
            "The breadcrumb does not display the correct position.The position currently displayed is: ");
    }
}
