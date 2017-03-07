package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExploringTheLibraryLibraryTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentsFilters filters;

    @Autowired
    ContentService contentService;

    private String user = "C6333User" + DataUtil.getUniqueIdentifier();
    private String description = "C6333SiteDescription" + DataUtil.getUniqueIdentifier();
    private String folderName = "testFolder1";
    private String folderName1 = "testFolder2";
    private String docName = "testFile1";
    private String docName1 = "testFile2";
    private String siteName = "C6333SiteName" + DataUtil.getUniqueIdentifier();

    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentService.createFolder(user, password, folderName1, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName1, DocumentType.TEXT_PLAIN, docName1, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6333")
    @Test

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
        browser.waitInSeconds(4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "testFile1 is not displayed");
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents, testFolder1]",
                "The breadcrumb does not display the correct position.The position currently displayed is: ");

        LOG.info("Step 3: Click on folder from Library section, testFolder2");
        filters.clickSecondFolder();
        browser.waitInSeconds(4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName1), "testFile2 is not displayed");
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents, testFolder2]",
                "The breadcrumb does not display the correct position.The position currently displayed is: ");
    }
}
