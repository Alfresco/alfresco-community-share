package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExploringTheLibraryLibraryTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentsFilters filters;

    private final String user = "C6333User" + DataUtil.getUniqueIdentifier();
    private final String description = "C6333SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String folderName = "testFolder1";
    private final String folderName1 = "testFolder2";
    private final String docName = "testFile1";
    private final String docName1 = "testFile2";
    private final String siteName = "C6333SiteName" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)

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
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})

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
