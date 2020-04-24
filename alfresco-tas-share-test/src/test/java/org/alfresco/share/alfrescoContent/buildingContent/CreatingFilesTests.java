package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
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
import org.testng.asserts.SoftAssert;

public class CreatingFilesTests extends ContextAwareWebTest
{
    private final String user = String.format("C6976User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C6976SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C6976SiteName%s", RandomData.getRandomAlphanumeric());
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private CreateContent create;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @BeforeClass (alwaysRun = true)

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6976")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed"  })

    public void createPlainTextFile()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click Create... button");
        documentLibraryPage.clickCreateButton();
        Assert.assertTrue(create.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");

        LOG.info("Step 2: Click \"Plain Text...\" option.");
        create.clickPlainTextButton();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create Plain Text form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("test name");
        create.sendInputForContent("test content");
        create.sendInputForTitle("test title");
        create.sendInputForDescription("test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText(), "test content", "\"test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "test name", "\"test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C6977")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void createHTMLFile()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click Create... button and check that the create HTML button is available");
        documentLibraryPage.clickCreateButton();
        Assert.assertTrue(create.isHTMLButtonDisplayed(), "Create... HTML... is not available");

        LOG.info("Step 2: Click \"HTML...\" option.");
        create.clickHTML();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isHTMLContentFieldDisplayed(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C6977 test name");
        create.sendInputForHTMLContent("C6977 test content");
        create.sendInputForTitle("C6977 test title");
        create.sendInputForDescription("C6977 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C6977 test name", "\"C6977 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C6978")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void createXMLFile()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click Create... button");
        documentLibraryPage.clickCreateButton();
        Assert.assertTrue(create.isXMLButtonDisplayed(), "Create... XML... is not available");

        LOG.info("Step 2: Click \"XML...\" option.");
        create.clickXMLButton();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C6978 test name");
        create.sendInputForContent("C6978 test content");
        create.sendInputForTitle("C6978 test title");
        create.sendInputForDescription("C6978 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C6978 test content", "\"C6978 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C6978 test name", "\"C6978 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C6986")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void verifyCreateContentPage()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1 & 2: Click Create... button and select Plain Text option");
        documentLibraryPage.clickCreateButton();
        create.clickPlainTextButton();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create Plain Text form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Verify Mandatory fields.");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertFalse(create.isContentMarkedAsMandatory(), "Content field is marked as mandatory");
        Assert.assertFalse(create.isTitleMarkedAsMandatory(), "Title field is marked as mandatory");
        Assert.assertFalse(create.isDescriptionMarkedAsMandatory(), "Description field is marked as mandatory");
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAllCreateAvailableActions()
    {
        SoftAssert softAssert = new SoftAssert();
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click Create... button");
        documentLibraryPage.clickCreateButton();
        softAssert.assertTrue(create.isFolderOptionAvailableUnderCreate(), "Create... Folder is not available");
        softAssert.assertTrue(create.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");
        softAssert.assertTrue(create.isHTMLButtonDisplayed(), "Create... HTML... is not available");
        softAssert.assertTrue(create.isXMLButtonDisplayed(), "Create... XML... is not available");
        softAssert.assertTrue(create.isGoogleDocsDocumentDisplayed(), "Create... Google Docs Document... is not available");
        softAssert.assertTrue(create.isGoogleDocsSpreadsheetDisplayed(), "Create... Google Docs Spreadsheet... is not available");
        softAssert.assertTrue(create.isGoogleDocsPresentationDisplayed(), "Create... Google Docs Presentation... is not available");
        softAssert.assertTrue(create.isCreateFromTemplateAvailable("Create document from template"), "Create... Create document from template is not displayed");
        softAssert.assertTrue(create.isCreateFromTemplateAvailable("Create folder from template"), "Create... Create folder from template is not displayed");
        softAssert.assertAll();
    }
}
