package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet.DocumentsFilter;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MyDocumentsWorkspacesTests extends ContextAwareWebTest
{
    @Autowired
    MyDocumentsDashlet myDocumentsDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @TestRail (id = "C2432")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void noDocumentsAdded()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        String file = "TestDoc1";
        Assert.assertFalse(contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, file).getId().isEmpty(),
            file + " document creation failed");
        contentService.updateDocumentContent(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, RandomStringUtils.randomAlphabetic(10));

        file = "TestDoc2";
        Assert.assertFalse(contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, file).getId().isEmpty(),
            file + " document creation failed");
        contentAction.checkOut(userName, password, siteName, file);

        file = "TestDoc3";
        Assert.assertFalse(contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, file).getId().isEmpty(),
            file + " document creation failed");
        contentAction.setFileAsFavorite(userName, password, siteName, file);
        getBrowser().waitInSeconds(10);
        myDocumentsDashlet.waitForDocument();

        LOG.info("STEP 1 - Select \"I've Recently Modified\" value from filter");
        myDocumentsDashlet.filterMyDocuments(DocumentsFilter.RecentlyModified.toString());
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(3), "3 documents should be displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc1"), "\"TestDoc1\" is displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc2"), "\"TestDoc2\" is displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc3"), "\"TestDoc3\" is displayed");

        LOG.info("STEP 2 - Select \"I'm Editing\" value from filter");
        myDocumentsDashlet.filterMyDocuments(DocumentsFilter.Editing.toString());
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(1), "1 document should be displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc2"), "\"TestDoc2\" is displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc1"), "\"TestDoc1\" isn't displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc3"), "\"TestDoc3\" isn't displayed");

        LOG.info("STEP 3 - Select \"My Favorites\" value from filter");
        myDocumentsDashlet.filterMyDocuments(DocumentsFilter.MyFavorites.toString());
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(1), "1 document should be displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc3"), "\"TestDoc3\" is displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc1"), "\"TestDoc1\" isn't displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc2"), "\"TestDoc2\" isn't displayed");

        LOG.info("STEP 4 - Log out and log in again");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName, password);
        getBrowser().waitInSeconds(8);
        userDashboardPage.navigate(userName);
        myDocumentsDashlet.waitForDocument();
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(1), "1 document should be displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc3"), "\"TestDoc3\" is displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc1"), "\"TestDoc1\" isn't displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc2"), "\"TestDoc2\" isn't displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
