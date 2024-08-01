package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class DefiningRulesForFolderTests extends BaseTest
{
    @Autowired
    UserService userService;
    @Autowired
    SiteService siteService;
    @Autowired
    ContentActions contentAction;
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userName2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName2 = new ThreadLocal<>();
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String path = "Documents";
    private final String testFileName = "test.pdf";
    private String testFilePath;
    private NewFolderDialog newContentDialog;
    private AddSiteUsersPage addSiteUsersPage;
    private DocumentLibraryPage documentLibraryPage;
    private EditInAlfrescoPage editInAlfrescoPage;
    private ManageRulesPage manageRulesPage;
    private EditRulesPage editRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private SelectDestinationDialog selectDestinationDialog;
    private DeleteDialog deleteDialog;
    private HeaderMenuBar headerMenuBar;
    private UploadContent uploadContent;
    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userName2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        siteName2.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());

        newContentDialog = new NewFolderDialog(webDriver);
        addSiteUsersPage = new AddSiteUsersPage(webDriver);
        editInAlfrescoPage = new EditInAlfrescoPage(webDriver);
        manageRulesPage = new ManageRulesPage(webDriver);
        editRulesPage = new EditRulesPage(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);
        selectDestinationDialog = new SelectDestinationDialog(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        uploadContent = new UploadContent(webDriver);

        authenticateUsingLoginPage(userName.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName2.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(siteName2.get());
        deleteUsersIfNotNull(userName.get());
        deleteUsersIfNotNull(userName2.get());
    }

    @TestRail (id = "C6367")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyFolderRulesPage()
    {
        String folderName = "Folder-C6367-" + random;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        log.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getNoRulesText(), language.translate("documentLibrary.rules.noRules"), "'No rules' message=");
        assertEquals(manageRulesPage.getCreateRulesLinkText(), language.translate("documentLibrary.rules.createLinkText"), "'Create rules' link text=");
        assertEquals(manageRulesPage.getCreateRulesDescription(), language.translate("documentLibrary.rules.createDescription"), "'Create rules' description=");
        assertEquals(manageRulesPage.getLinkToRuleSetLinkText(), language.translate("documentLibrary.rules.linkToRuleSetLinkText"),
            "'Link to Rule Set' link text=");
        assertEquals(manageRulesPage.getLinkToRuleSetDescription(), language.translate("documentLibrary.rules.linkToRuleSetDescription"),
            "'Link to Rule Set' description=");
        assertEquals(manageRulesPage.getInheritButtonText(), language.translate("documentLibrary.rules.inheritButton"), "Inherit rules button text=");
    }

    @TestRail (id = "C12857")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyEditRulePageDropdownElements()
    {
        String folderName = "Folder-C12857-" + random;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        log.info("STEP1: Mouse over folder, click More and select Manage Rules");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getNoRulesText(), language.translate("documentLibrary.rules.noRules"), "'No rules' message=");

        log.info("STEP2: Click on 'Create Rule' link");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP3: Verify the 'If all criteria are met:' drop-down values");
        ArrayList<String> expectedOptionsList = new ArrayList<>(
            Arrays.asList("All Items", "Size", "Created Date", "Modified Date", "Creator", "Modifier", "Author", "Mimetype", "Encoding", "Description",
                "Name", "Title", "Has tag", "Has category", "Content of type or sub-type", "Has aspect", "Show more..."));
        editRulesPage.verifyDropdownOptions("ruleConfigIfCondition", expectedOptionsList);

        log.info("STEP4: Verify \"Perform Action\" drop-down values");
        expectedOptionsList.clear();
        expectedOptionsList = new ArrayList<>(
            Arrays.asList("Select...", "Execute script", "Copy", "Move", "Check in", "Check out", "Link to category", "Add aspect", "Remove aspect",
                "Add simple workflow", "Send email", "Transform and copy content", "Transform and copy image", "Extract common metadata fields",
                "Import", "Specialise type", "Increment Counter", "Set property value", "Start Process", "webqs_publish"));
        editRulesPage.verifyDropdownOptions("ruleConfigAction", expectedOptionsList);
    }

    @TestRail (id = "C6372")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createRule()
    {
        String ruleName1 = "rule-C6372-" + random;
        String folderName = "Folder-C6372-" + random;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");

        log.info("STEP2: Click on 'Create Rule' link");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP3: Type rule name, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 2);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath("Documents");
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");

        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C6622")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreCreated()
    {
        String ruleName1 = "rule-C6622-" + random;
        String folderName = "Folder-C6622-" + random;
        String fileName = "fileC6622";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Create Rule page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Create a file in folder");
        contentService.createDocument(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.HTML, fileName, "docContent");

        log.info("STEP3: Navigate to site's document library");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed");
    }

    @TestRail (id = "C7239")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createAndCreateAnother()
    {
        String ruleName1 = "rule1-C7239-" + random;
        String ruleName2 = "rule2-C7239-" + random;
        String folderName = "Folder-C7239-" + random;
        String fileName = "fileC7239";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Create Rule page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateAndCreateAnotherButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP2: Fill in Create Rule details and submit form");
        editRulesPage.typeRuleDetails(ruleName2, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedRules = new ArrayList<>(Arrays.asList(ruleName1, ruleName2));
        assertEquals(ruleDetailsPage.getDisplayedRules().toString(), expectedRules.toString(), "Rules from folder=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7240")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelCreateRule()
    {
        String ruleName1 = "rule-C7240-" + random;
        String folderName = "Folder-C7240-" + random;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Create Rule page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and cancel form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.clickCancelButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7245")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void disableRule()
    {
        String ruleName1 = "rule-C7245-" + random;
        String folderName = "Folder-C7245-" + random;
        String fileName = "fileC7245";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        log.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickDisableRuleCheckbox();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Create a file in folder and verify if rule is applied");
        contentService.createDocumentInFolder(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), folderName, CMISUtil.DocumentType.HTML, fileName, "docContent");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
    }

    @TestRail (id = "C6621")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreUpdated()
    {
        String ruleName1 = "rule-C6621-" + random;
        String folderName = "FolderC6621-" + random;
        String fileName = "FileC6621-" + random;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        log.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Create a file in folder");
        contentService.createDocumentInFolder(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), folderName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        log.info("STEP3: Navigate to site document library");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("STEP4: Navigate to folder content. For file click on 'Edit in Alfresco' and update the content");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");
        documentLibraryPage.selectItemAction(fileName, ItemActions.EDIT_IN_ALFRESCO);
        assertEquals(editInAlfrescoPage.getPageTitle(), "Alfresco » Edit in Alfresco Share", "Displayed page=");
        editInAlfrescoPage.typeContent("Content updated!");
        editInAlfrescoPage.clickSaveButton();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");
    }

    @TestRail (id = "C6623")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreDeleted()
    {
        String ruleName1 = "rule-C6623-" + random;
        String folderName = "FolderC6623-" + random;
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        log.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(2, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Create a file in folder");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFolderName(folderName);
        testFilePath = testDataFolder + testFileName;
        uploadContent.uploadContent(testFilePath);

        log.info("STEP3: Delete file");
        documentLibraryPage.clickCheckBox(testFileName);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.selectedItemsMenu.delete"));
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 1, testFileName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.confirmDeletion();
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");

        log.info("STEP4: Navigate to 'Site1' document library");
        documentLibraryPage.navigate();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFileName), testFileName + " is displayed.");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7246")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreCreatedRuleAppliesToSubfolders()
    {
        String ruleName1 = "rule-C7246-" + random;
        String folderName = "Folder2-C7246-" + random;
        String fileName = "C7246File";
        String folderName2 = "Folder2-C7246-" + random;
        String pathToFolder2 = "Sites/" + siteName.get().getId() + "/documentlibrary/" + folderName;

        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());
        contentService.createFolderInRepository(userName.get().getUsername(), userName.get().getPassword(), folderName2, pathToFolder2);

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Create Rule page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();

        editRulesPage.clickRulesAppliesToSubfoldersCheckbox();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Navigate to Document Library ->" + folderName + " and create a plain text file in the subfolder");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertEquals(documentLibraryPage.getFoldersList().toString(), Arrays.asList(folderName).toString(),
            "Folders list in Document Library -> " + folderName + "=");
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), pathToFolder2, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        log.info("STEP3: Navigate to the path specified in the rule, Document Library");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed. " + ruleName1 + " applied to subfolder " + folderName2);
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C202963")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreUpdatedRuleAppliesToSubfolders()
    {
        String ruleName1 = "rule-C202963-" + random;
        String folderName = "Folder2-C202963-" + random;
        String fileName = "C202963file";
        String folderName2 = "Folder2-C202963-" + random;
        String pathToFolder2 = "Sites/" + siteName.get().getId() + "/documentlibrary/" + folderName;

        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());
        contentService.createFolderInRepository(userName.get().getUsername(), userName.get().getPassword(), folderName2, pathToFolder2);
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), pathToFolder2, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Create Rule page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();

        editRulesPage.clickRulesAppliesToSubfoldersCheckbox();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Navigate to Document Library ->" + folderName);
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertEquals(documentLibraryPage.getFoldersList().toString(), Arrays.asList(folderName).toString(),
            "Folders list in Document Library -> " + folderName + "=");

        log.info("STEP3: Navigate to folder content. For file click on 'Edit in Alfresco' and update the content");
        documentLibraryPage.selectItemAction(fileName, ItemActions.EDIT_IN_ALFRESCO);
        assertEquals(editInAlfrescoPage.getPageTitle(), "Alfresco » Edit in Alfresco Share", "Displayed page=");
        editInAlfrescoPage.typeContent("Content updated!");
        editInAlfrescoPage.clickSaveButton();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C202964")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreDeletedRuleAppliesToSubfolders()
    {
        String ruleName1 = "rule-C202964-" + random;
        String folderName = "Folder-C202964-" + random;
        String fileName = "C202964file";
        String folderName2 = "Folder2-C202964-" + random;
        String pathToFolder2 = "Sites/" + siteName.get().getId() + "/documentlibrary/" + folderName;

        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());
        contentService.createFolderInRepository(userName.get().getUsername(), userName.get().getPassword(), folderName2, pathToFolder2);
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), pathToFolder2, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Navigate to Create Rule page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(2, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.selectSite(siteName.get().getId());
        selectDestinationDialog.selectFolderPath(path);
        selectDestinationDialog.confirmFolderLocation();

        editRulesPage.clickRulesAppliesToSubfoldersCheckbox();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();

        log.info("STEP2: Navigate to Document Library ->" + folderName + " and delete " + fileName);
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        documentLibraryPage.clickCheckBox(fileName);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.selectedItemsMenu.delete"));
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 1, fileName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.confirmDeletion();
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");

        log.info("STEP3: Navigate to the path specified in the rule, Document Library");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed. " + ruleName1 + " applied to subfolder " + folderName2);
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7285")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkoutToSite()
    {
        String ruleName1 = "rule-C7285-" + random;
        String folderName = "Folder-C7285-" + random;
        String fileName = "C7285file";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Precondition: Navigate to 'Create rule' page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Type rule title, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 5);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(siteName2.get().getId());
        selectDestinationDialog.selectFolderPath("Documents");
        selectDestinationDialog.confirmFolderLocation();

        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Checkout working copy to .../documentLibrary in " + siteName2.get().getId(), "'Perform Action' section=");

        log.info("STEP2: Navigate to " + folderName + " . Create a file in the folder");
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), "Sites/" + siteName.get().getId() + "/documentLibrary/" + folderName, CMISUtil.DocumentType.HTML,
            fileName, "docContent");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);

        log.info("STEP3: Navigate to " + siteName2);
        documentLibraryPage.navigate(siteName2.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed in " + folderName);
        assertTrue(documentLibraryPage.isInfoBannerDisplayed(fileName), "Document is locked.");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7284")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void moveToSite()
    {
        String ruleName1 = "rule-C7284-" + random;
        String folderName = "Folder-C7284-" + random;
        String fileName = "C7284file";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        log.info("Precondition: Navigate to 'Create rule' page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Type rule title, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 3);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(siteName2.get().getId());
        selectDestinationDialog.selectFolderPath("Documents");
        selectDestinationDialog.confirmFolderLocation();

        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Move items to .../documentLibrary in " + siteName2.get().getId(), "'Perform Action' section=");

        log.info("STEP2: Create a file in " + folderName + " from " + siteName + "'s Document Library");
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), "Sites/" + siteName.get().getId() + "/documentLibrary/" + folderName, CMISUtil.DocumentType.HTML,
            fileName, "docContent");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);

        log.info("STEP3: Navigate to " + siteName2 + "'s Document Library");
        documentLibraryPage.navigate(siteName2.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " moved.");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7283")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void copyToSite()
    {
        String ruleName1 = "rule-C7283-" + random;
        String folderName = "Folder-C7283-" + random;
        String fileName = "C7283file";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());

        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        log.info("Precondition: Navigate to 'Create rule' page");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName.get().getId() + "/rule-edit", "Redirected to=");

        log.info("STEP1: Type rule title, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 2);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(siteName2.get().getId());
        selectDestinationDialog.selectFolderPath("Documents");
        selectDestinationDialog.confirmFolderLocation();

        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary in " + siteName2.get().getId(), "'Perform Action' section=");

        log.info("STEP2: Create a file in " + folderName + " from " + siteName + "'s Document Library");
        contentService.createDocumentInRepository(userName.get().getUsername(), userName.get().getPassword(), "Sites/" + siteName.get().getId() + "/documentLibrary/" + folderName, CMISUtil.DocumentType.HTML,
            fileName, "docContent");
        documentLibraryPage.navigate(siteName.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed in " + folderName);

        log.info("STEP3: Navigate to " + siteName2 + "'s Document Library");
        documentLibraryPage.navigate(siteName2.get().getId());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " moved.");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C286441")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createRuleWithUnlessShowMoreOption() {
        authenticateUsingLoginPage(getAdminUser());
        contentAction.renameContent(getAdminUser().getUsername(), getAdminUser().getPassword(), "/Data Dictionary/Scripts/backup and log.js.sample", "backupLog.js");
        authenticateUsingLoginPage(userName.get());
        String folderName = "Folder1-C286441-" + random;
        String folderName1 = "User1-C286441" + random;
        String folderName2 = "User2-C286441" + random;
        String foldername3 = "Backup";
        contentService.createFolder(userName.get().getUsername(), userName.get().getPassword(), folderName, siteName.get().getId());
        addSiteUsersPage.navigate(siteName.get().getId());
        addSiteUsersPage.searchUserWithName(userName2.get().getUsername());
        addSiteUsersPage.clickSelectUserButton(userName2.get().getUsername());
        addSiteUsersPage.setUserRole(userName2.get().getUsername(), "Manager");
        addSiteUsersPage.addUsersToSite();
        String rulename3 = "rule-C286441-" + random;
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_RULES);
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName.get().getId());

        log.info("STEP 1: Fill 'Name' field with correct data;");
        editRulesPage.typeName(rulename3);

        log.info("STEP 2: Select 'Items are created or enter this folder' value from 'When' drop-down select control;");
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);

        log.info("STEP 3: Check the 'Unless all criteria are met:' checkbox");
        if (!editRulesPage.isUnlessCheckboxSelected())
        {
            editRulesPage.clickUnlessCheckbox();
        }
        assertTrue(editRulesPage.isUnlessCheckboxSelected(), "Is not selected");

        log.info("STEP 4: Select 'Creator' value from the 'Unless all criteria are met:' first drop-down box");
        editRulesPage.selectOptionFromDropdown("ruleConfigUnlessCondition", 4);

        log.info("STEP 5: Select 'Equals'' from the second drop-down box");
        editRulesPage.selectOptionFromSecondDropdown("ruleConfigUnlessCondition", 3);

        log.info("STEP 6: Type in the third box the 'If' condition");
        editRulesPage.typeInputConfigText("ruleConfigUnlessCondition", "If");
        editRulesPage.getInputConfigText("ruleConfigUnlessCondition");
        assertEquals(editRulesPage.getInputConfigText("ruleConfigUnlessCondition"), "If", "Te value is not correct");

        log.info("STEP 7: Select 'Execute Backup script' from 'Perform Action' drop-down select;");
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 1);
        editRulesPage.selectOptionFromSecondDropdown("ruleConfigAction", 0);
        editRulesPage.clickRulesAppliesToSubfoldersCheckbox();

        log.info("STEP 8: Click 'Create' button;");
        editRulesPage.clickCreateButton();

        log.info("STEP 9: Apply the rule on 'Folder1' that was created");
        ruleDetailsPage.clickRunRulesButton();
        ruleDetailsPage.clickOnRunRulesOption(1);

        log.info("STEP 10: Create 'User1' folder in 'Folder1' (User1 should be logged in)");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFolderName(folderName);
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        newContentDialog.typeName(folderName1);
        newContentDialog.clickSave();
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName1), "Folder is not displayed!");


        log.info("STEP 11: Login as User2 and create 'User2' folder in Folder1;");
        authenticateUsingLoginPage(userName2.get());
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFolderName(folderName);
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        newContentDialog.typeName(folderName2);
        newContentDialog.clickSave();
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName2), "Folder is not displayed!");

        log.info("STEP 12: Check contents of Folder1;");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(foldername3), "Folder is not displayed!");

        editRulesPage.cleanupSelectedValues();
        contentAction.renameContent(getAdminUser().getUsername(), getAdminUser().getPassword(), "/Data Dictionary/Scripts/backupLog.js", "backup and log.js.sample");
    }
}