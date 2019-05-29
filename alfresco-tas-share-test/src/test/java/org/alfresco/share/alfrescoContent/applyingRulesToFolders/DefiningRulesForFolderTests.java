package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class DefiningRulesForFolderTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private final String description = "description-" + random;
    private final String path = "Documents";
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private EditInAlfrescoPage editInAlfrescoPage;
    @Autowired
    private ManageRulesPage manageRulesPage;
    @Autowired
    private EditRulesPage editRulesPage;
    @Autowired
    private RuleDetailsPage ruleDetailsPage;
    @Autowired
    private SelectDestinationDialog selectDestinationDialog;
    @Autowired
    private DeleteDialog deleteDialog;
    @Autowired
    private HeaderMenuBar headerMenuBar;
    private String siteName = "Site-" + random;
    private String siteName2 = "Site2-" + random;
    private String fileName;
    private String folderName, ruleName1, ruleName2;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "First Name", "Last Name");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteName2);

    }

    @TestRail (id = "C6367")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyFolderRulesPage()
    {
        folderName = "Folder-C6367-" + random;
        fileName = "fileC6367";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
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
        folderName = "Folder-C12857-" + random;
        fileName = "fileC12857";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Mouse over folder, click More and select Manage Rules");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getNoRulesText(), language.translate("documentLibrary.rules.noRules"), "'No rules' message=");

        LOG.info("STEP2: Click on 'Create Rule' link");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP3: Verify the 'If all criteria are met:' drop-down values");
        ArrayList<String> expectedOptionsList = new ArrayList<>(
            Arrays.asList("All Items", "Size", "Created Date", "Modified Date", "Creator", "Modifier", "Author", "Mimetype", "Encoding", "Description",
                "Name", "Title", "Has tag", "Has category", "Content of type or sub-type", "Has aspect", "Show more..."));
        editRulesPage.verifyDropdownOptions("ruleConfigIfCondition", expectedOptionsList);

        LOG.info("STEP4: Verify \"Perform Action\" drop-down values");
        expectedOptionsList.clear();
        expectedOptionsList = new ArrayList<>(
            Arrays.asList("Select...", "Execute script", "Copy", "Move", "Check in", "Check out", "Link to category", "Add aspect", "Remove aspect",
                "Add simple workflow", "Send email", "Transform and copy content", "Transform and copy image", "Extract common metadata fields",
                "Import", "Specialise type", "Increment Counter", "Set property value", "webqs_publishTree", "webqs_publish"));
        editRulesPage.verifyDropdownOptions("ruleConfigAction", expectedOptionsList);
    }

    @TestRail (id = "C6372")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createRule()
    {
        ruleName1 = "rule-C6372-" + random;
        folderName = "Folder-C6372-" + random;
        fileName = "fileC6372";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");

        LOG.info("STEP2: Click on 'Create Rule' link");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP3: Type rule name, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 2);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder("Documents");
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
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
        ruleName1 = "rule-C6622-" + random;
        folderName = "Folder-C6622-" + random;
        fileName = "fileC6622";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Create Rule page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("STEP2: Create a file in folder");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.HTML, fileName, "docContent");

        LOG.info("STEP3: Navigate to site's document library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed");
    }

    @TestRail (id = "C7239")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createAndCreateAnother()
    {
        ruleName1 = "rule1-C7239-" + random;
        ruleName2 = "rule2-C7239-" + random;
        folderName = "Folder-C7239-" + random;
        fileName = "fileC7239";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Create Rule page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.clickCreateAndCreateAnotherButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP2: Fill in Create Rule details and submit form");
        editRulesPage.typeRuleDetails(ruleName2, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
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
        ruleName1 = "rule-C7240-" + random;
        folderName = "Folder-C7240-" + random;
        fileName = "fileC7240";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Create Rule page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and cancel form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickCancelButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7245")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void disableRule()
    {
        ruleName1 = "rule-C7245-" + random;
        folderName = "Folder-C7245-" + random;
        fileName = "fileC7245";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickDisableRuleCheckbox();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("STEP2: Create a file in folder and verify if rule is applied");
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.HTML, fileName, "docContent");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed in Document Library");
    }

    @TestRail (id = "C6621")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreUpdated()
    {
        ruleName1 = "rule-C6621-" + random;
        folderName = "FolderC6621-" + random;
        fileName = "FileC6621-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("STEP2: Create a file in folder");
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        LOG.info("STEP3: Navigate to site document library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");

        LOG.info("STEP4: Navigate to folder content. For file click on 'Edit in Alfresco' and update the content");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, language.translate("documentLibrary.contentActions.editInAlfresco"), editInAlfrescoPage);
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
        ruleName1 = "rule-C6623-" + random;
        folderName = "FolderC6623-" + random;
        fileName = "FileC6623-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(2, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("STEP2: Create a file in folder");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        LOG.info("STEP3: Delete file");
        documentLibraryPage.clickCheckBox(fileName);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.selectedItemsMenu.delete"));
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 1, fileName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.clickDelete();
        getBrowser().waitInSeconds(7);

        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");

        LOG.info("STEP4: Navigate to 'Site1' document library");
        documentLibraryPage.navigate();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");

        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7246")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreCreatedRuleAppliesToSubfolders()
    {
        ruleName1 = "rule-C7246-" + random;
        folderName = "Folder-C7246-" + random;
        fileName = "C7246File";
        String folderName2 = "Folder2-C7246-" + random;
        String pathToFolder2 = "Sites/" + siteName + "/documentlibrary/" + folderName;

        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createFolderInRepository(userName, password, folderName2, pathToFolder2);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Create Rule page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
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

        LOG.info("STEP2: Navigate to Document Library ->" + folderName + " and create a plain text file in the subfolder");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertEquals(documentLibraryPage.getFoldersList().toString(), Arrays.asList(folderName2).toString(),
            "Folders list in Document Library -> " + folderName + "=");
        contentService.createDocumentInRepository(userName, password, pathToFolder2, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        LOG.info("STEP3: Navigate to the path specified in the rule, Document Library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed. " + ruleName1 + " applied to subfolder " + folderName2);

        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C202963")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreUpdatedRuleAppliesToSubfolders()
    {
        ruleName1 = "rule-C202963-" + random;
        folderName = "Folder-C202963-" + random;
        fileName = "C202963file";
        String folderName2 = "Folder2-C202963-" + random;
        String pathToFolder2 = "Sites/" + siteName + "/documentlibrary/" + folderName;

        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createFolderInRepository(userName, password, folderName2, pathToFolder2);
        contentService.createDocumentInRepository(userName, password, pathToFolder2, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Create Rule page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
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

        LOG.info("STEP2: Navigate to Document Library ->" + folderName);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertEquals(documentLibraryPage.getFoldersList().toString(), Arrays.asList(folderName2).toString(),
            "Folders list in Document Library -> " + folderName + "=");

        LOG.info("STEP3: Navigate to folder content. For file click on 'Edit in Alfresco' and update the content");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, language.translate("documentLibrary.contentActions.editInAlfresco"), editInAlfrescoPage);
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
        ruleName1 = "rule-C202964-" + random;
        folderName = "Folder-C202964-" + random;
        fileName = "C202964file";
        String folderName2 = "Folder2-C202964-" + random;
        String pathToFolder2 = "Sites/" + siteName + "/documentlibrary/" + folderName;

        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createFolderInRepository(userName, password, folderName2, pathToFolder2);
        contentService.createDocumentInRepository(userName, password, pathToFolder2, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "docContent");

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Create Rule page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(2, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
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

        LOG.info("STEP2: Navigate to Document Library ->" + folderName + " and delete " + fileName);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        documentLibraryPage.clickCheckBox(fileName);
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.selectedItemsMenu.delete"));
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 1, fileName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.clickDelete();
        getBrowser().waitInSeconds(7);

        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");

        LOG.info("STEP3: Navigate to the path specified in the rule, Document Library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed. " + ruleName1 + " applied to subfolder " + folderName2);

        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7285")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkoutToSite()
    {
        ruleName1 = "rule-C7285-" + random;
        folderName = "Folder-C7285-" + random;
        fileName = "C7285file";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Precondition: Navigate to 'Create rule' page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Type rule title, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 5);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName2);
        selectDestinationDialog.clickPathFolder("Documents");
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Checkout working copy to .../documentLibrary in " + siteName2, "'Perform Action' section=");

        LOG.info("STEP2: Navigate to " + folderName + " . Create a file in the folder");
        contentService.createDocumentInRepository(userName, password, "Sites/" + siteName + "/documentLibrary/" + folderName, CMISUtil.DocumentType.HTML,
            fileName, "docContent");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed in " + folderName);

        LOG.info("STEP3: Navigate to " + siteName2);
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed in " + folderName);
        assertTrue(documentLibraryPage.isInfoBannerDisplayed(fileName), "Document is locked.");

        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7284")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void moveToSite()
    {
        ruleName1 = "rule-C7284-" + random;
        folderName = "Folder-C7284-" + random;
        fileName = "C7284file";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Precondition: Navigate to 'Create rule' page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Type rule title, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 3);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName2);
        selectDestinationDialog.clickPathFolder("Documents");
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Move items to .../documentLibrary in " + siteName2, "'Perform Action' section=");

        LOG.info("STEP2: Create a file in " + folderName + " from " + siteName + "'s Document Library");
        contentService.createDocumentInRepository(userName, password, "Sites/" + siteName + "/documentLibrary/" + folderName, CMISUtil.DocumentType.HTML,
            fileName, "docContent");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed in " + folderName);

        LOG.info("STEP3: Navigate to " + siteName2 + "'s Document Library");
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " moved.");

        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7283")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void copyToSite()
    {
        ruleName1 = "rule-C7283-" + random;
        folderName = "Folder-C7283-" + random;
        fileName = "C7283file";
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("Precondition: Navigate to 'Create rule' page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Type rule title, description and select value from each dropdown");
        editRulesPage.typeName(ruleName1);
        editRulesPage.typeDescription(description);
        editRulesPage.selectOptionFromDropdown("ruleConfigType", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigIfCondition", 0);
        editRulesPage.selectOptionFromDropdown("ruleConfigAction", 2);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName2);
        selectDestinationDialog.clickPathFolder("Documents");
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), ruleName1, "Rule title=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary in " + siteName2, "'Perform Action' section=");

        LOG.info("STEP2: Create a file in " + folderName + " from " + siteName + "'s Document Library");
        contentService.createDocumentInRepository(userName, password, "Sites/" + siteName + "/documentLibrary/" + folderName, CMISUtil.DocumentType.HTML,
            fileName, "docContent");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed in " + folderName);

        LOG.info("STEP3: Navigate to " + siteName2 + "'s Document Library");
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " moved.");

        editRulesPage.cleanupSelectedValues();
    }
}
