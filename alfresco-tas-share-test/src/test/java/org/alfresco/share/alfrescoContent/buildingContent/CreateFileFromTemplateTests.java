package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateFileFromTemplateTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private CreateContent createContent;

    @Autowired private DocumentDetailsPage documentDetailsPage;
    
    private final String user = "C7000User" + DataUtil.getUniqueIdentifier();
    private final String description = "C7000SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C7000SiteName" + DataUtil.getUniqueIdentifier();
    private final String path = "Data Dictionary/Node Templates";
    private final String docName ="template"+DataUtil.getUniqueIdentifier();
    private final String docContent ="template content";
   
    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, docName,docContent );
    }
    
    @TestRail(id ="C7000")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    
    public void createFileFromTemplate()
    {
        documentLibraryPage.navigate(siteName);
        
        LOG.info("Step 1:Click 'Create' then click 'Create document from template'.");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create document from template");
        Assert.assertTrue(createContent.isTemplateDisplayed(docName), "Template is not displayed");
        
        LOG.info("Step 2: Select the template and check that the new file is created with the content from the template used");
        createContent.clickOnTemplate(docName, documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Newly created document is not displayed in Document Library");

        documentLibraryPage.clickOnFile(docName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Document is not previewed");
        Assert.assertEquals(documentDetailsPage.getContentText(), docContent);
    }
}
