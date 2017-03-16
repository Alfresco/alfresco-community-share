package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet.DocumentsFilter;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MyDocumentsWorkspacesTests extends ContextAwareWebTest
{
    @Autowired
    MyDocumentsDashlet myDocumentsDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @TestRail(id = "C2432")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void noDocumentsAdded()
    {
        String userName = "User1" + DataUtil.getUniqueIdentifier();
        String siteName = "Site1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + "@tests.com", userName, userName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);

        String file = "TestDoc1";
        Assert.assertFalse(content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, file).getId().isEmpty(),
                file + " document creation failed");
        content.updateDocumentContent(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, RandomStringUtils.randomAlphabetic(10));

        file = "TestDoc2";
        Assert.assertFalse(content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, file).getId().isEmpty(),
                file + " document creation failed");
        contentAction.checkOut(userName, DataUtil.PASSWORD, siteName, file);

        file = "TestDoc3";
        Assert.assertFalse(content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, file, file).getId().isEmpty(),
                file + " document creation failed");
        contentAction.setFileAsFavorite(userName, DataUtil.PASSWORD, siteName, file);

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
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        userDashboardPage.navigate(userName);
        myDocumentsDashlet.waitForDocument();
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(1), "1 document should be displayed");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent("TestDoc3"), "\"TestDoc3\" is displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc1"), "\"TestDoc1\" isn't displayed");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent("TestDoc2"), "\"TestDoc2\" isn't displayed");
    }
}
