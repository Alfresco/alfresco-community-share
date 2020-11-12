package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.pageCommon.TableView;
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

public class LibraryViewOptionsTableViewTests extends ContextAwareWebTest
{
    private final String user = String.format("C2266User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C2266SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C2266SiteName%s", RandomData.getRandomAlphanumeric());
    private final String docName = "testFile1";
    private final String docContent = "C2266 content";
    private final String folderName = "C2266 test folder";
    private final String docName1 = "testFile1";
   // @Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private TableView tableView;

    @BeforeClass (alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName1, "Document content");
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2266")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void tableViewOption()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Expand the Options menu and check that the Table View is present");

        Assert.assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Table View"), "Table view is not displayed");
    }

    @TestRail (id = "C2267")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void tableViewDisplayingItems()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Check the table view action presence in the Options menu.");

        Assert.assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        Assert.assertTrue(documentLibraryPage.isviewOptionDisplayed("Table View"), "Table view is not displayed");

        LOG.info("Step 2: Click on Table view action.");
        documentLibraryPage.clickOptionsButton();
        documentLibraryPage.selectViewFromOptionsMenu("Table View");
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(tableView.isTableViewDisplayed());
        Assert.assertEquals(tableView.getContentNameTableView(docName), docName, "testFile1 is not displayed in table view");
        Assert.assertEquals(tableView.getContentNameTableView(folderName), folderName, "C2266 test folder is not displayed in table view");
        Assert.assertTrue(tableView.isSelectedColumnDisplayed(), "Selected column is not displayed in table view");
        Assert.assertTrue(tableView.isStatusColumnDisplayed(), "Status column is not displayed in table view");
        Assert.assertTrue(tableView.isThumbnailColumnDisplayed(), "Thumbnail column is not displayed in table view");
        Assert.assertTrue(tableView.isTitleColumnDisplayed(), "Title column is not displayed in table view");
        Assert.assertTrue(tableView.isDescriptionColumnDisplayed(), "Description column is not displayed in table view");
        Assert.assertTrue(tableView.isCreatorColumnDisplayed(), "Creator column is not displayed in table view");
        Assert.assertTrue(tableView.isCreatedColumnDisplayed(), "Created column is not displayed in table view");
        Assert.assertTrue(tableView.isModifierColumnDisplayed(), "Modifier column is not displayed in table view");
        Assert.assertTrue(tableView.isModifiedColumnDisplayed(), "Modified column is not displayed in table view");
        Assert.assertTrue(tableView.isActionsColumnDisplayed(), "Actions column is not displayed in table view");
    }
}
