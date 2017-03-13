package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateFileFromTemplateTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    CreateContent create;

    @Autowired
    DocumentDetailsPage documentDetailsPage;
    
    private String user = "C7000User" + DataUtil.getUniqueIdentifier();
    private String description = "C7000SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C7000SiteName" + DataUtil.getUniqueIdentifier();
    private String path = "Data Dictionary/Node Templates";
    private String docName ="template"+DataUtil.getUniqueIdentifier();
    private String docContent ="template content";
   
    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, docName,docContent );
    }
    
    @TestRail(id ="C7000")
    @Test
    
    public void createFileFromTemplate()
    {
        documentLibraryPage.navigate(siteName);
        
        LOG.info("Step 1:Click 'Create' then click 'Create document from template'.");

        documentLibraryPage.clickCreateButton();
        create.clickCreateDocumentFromTemplate("Create document from template");
        getBrowser().waitInSeconds(2);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(create.selectTemplate(docName), 6);
        Assert.assertTrue(create.isTemplateDisplayed(docName), "Template is not displayed");
        
        LOG.info("Step 2: Select the template and check that the new file is created with the content from the template used");
        
        create.clickOnTemplate(docName);
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Newly created document is not displayed in Document Library");
        documentLibraryPage.clickOnFile(docName);
        
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Document is not previewed");
        Assert.assertEquals(documentDetailsPage.getContentText(), docContent);
    }
}
