package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateFileFromTemplateTests extends ContextAwareWebTest
{
    private final String user = String.format("C7000User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C7000SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C7000SiteName%s", RandomData.getRandomAlphanumeric());
    private final String path = "Data Dictionary/Node Templates";
    private final String docName = "template" + RandomData.getRandomAlphanumeric();
    private final String docContent = "template content";
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, docName, docContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7000")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFileFromTemplate()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1:Click 'Create' then click 'Create document from template'.");
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_DOC_FROM_TEMPLATE);
        Assert.assertTrue(documentLibraryPage.isTemplateDisplayed(docName), "Template is not displayed");

        LOG.info("Step 2: Select the template and check that the new file is created with the content from the template used");
        documentLibraryPage.clickOnTemplate(docName, documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Newly created document is not displayed in Document Library");

        documentLibraryPage.clickOnFile(docName);
        Assert.assertEquals(documentDetailsPage.getContentText(), docContent);
    }
}
