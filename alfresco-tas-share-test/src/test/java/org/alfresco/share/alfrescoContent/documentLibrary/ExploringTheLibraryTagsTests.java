package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExploringTheLibraryTagsTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentsFilters filters;

    @Autowired
    ContentService contentService;
    
    @Autowired
    ContentAspects contentAspects;
    
    @Autowired 
    ContentActions contentActions;

    private String user = "C6939User" + DataUtil.getUniqueIdentifier();
    private String description = "C6939SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C6939SiteName" + DataUtil.getUniqueIdentifier();
    private String folderName = "testFolder";
    private String docName = "testFile1";
    private String docContent ="C6940 content";
    
    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id="C6939")
    @Test
    
    public void noTagsAdded()
    {
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.renderedPage();
        
        LOG.info("Step 1: Verify Tags section");
        Assert.assertTrue(filters.checkIfTagsFilterIsPresent(), "Tags filter is not displayed");
        
        LOG.info("Step 2: Click Tags filter");
        filters.clickTagsLink();
        Assert.assertFalse(filters.areTagsPresent(), "Tags content is displayed ");
    }

    @TestRail(id ="C6940")
    @Test
    
    public void contentItemsWithDifferentTags()
    {
        //Preconditions
        String tagName1 = "tag1c6940";
        String tagName2 = "tag2c6940";
        String siteName = "C6940SiteName"+ DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, docName, docContent);
        setupAuthenticatedSession(user, password);
        contentActions.addSingleTag(user, password, siteName, docName, tagName1);
        contentActions.addSingleTag(user, password, siteName, folderName, tagName2);
        documentLibraryPage.navigate(siteName);
        
        LOG.info("Step 1: Click Tags section, from explorer panel on the left side of the library");
        filters.clickTagsLink();
        Assert.assertEquals(filters.getSidebarTag(tagName1), tagName1 + " " +"(1)", "Tags tag content is not correct");
        Assert.assertEquals(filters.getSidebarTag(tagName2), tagName2+ " " +"(1)", "Tags tag content is not correct");
        
        LOG.info("Step 2: Click tagName1 and verify that only the content tagged with tag1 is displayed");
        filters.clickSelectedTag(tagName1);
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document tagged with tag1 is not displayed in Document Library");
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(folderName), "Folder tagged with tag2 is displayed in Document Library");
        
        LOG.info("Step 3: Click tagName2 and verify that only the content tagged with tag2 is displayed");
        filters.clickSelectedTag(tagName2);
        getBrowser().waitInSeconds(4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Folder tagged with tag2 is not displayed in Document Library");
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(docName), "Document tagged with tag1 is displayed in Document Library");
    }
}
