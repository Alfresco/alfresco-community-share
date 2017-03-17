package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class DefiningRulesForFolderTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private EditInAlfrescoPage editInAlfrescoPage;

    @Autowired private ManageRulesPage manageRulesPage;

    @Autowired private EditRulesPage editRulesPage;

    @Autowired private RuleDetailsPage ruleDetailsPage;

    @Autowired private SelectDestinationDialog selectDestinationDialog;

    @Autowired private DeleteDialog deleteDialog;

    @Autowired private HeaderMenuBar headerMenuBar;

    private final String random = DataUtil.getUniqueIdentifier();
    private final String userName = "user-" + random;
    private final String firstName = "First Name";
    private final String lastName = "Last Name";
    private String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String path = "Documents";
    private String fileName = "testDoc.txt";
    private String folderName, ruleName1, ruleName2, fileName2;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
    }

    @TestRail(id = "C6367")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void verifyFolderRulesPage()
    {
        siteName = "Site-C6367-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        folderName = "Folder-C6367-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C12857")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void verifyEditRulePageDropdownElements()
    {
        siteName = "Site-C12857-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        folderName = "Folder-C12857-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C6372")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createRule()
    {
        siteName = "Site-C6372-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule-C6372-" + random;
        folderName = "Folder-C6372-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C6622")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void itemsAreCreated()
    {
        siteName = "Site-C6622-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule-C6622-" + random;
        folderName = "Folder-C6622-" + random;
        fileName = "testFile1.txt";
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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
        editRulesPage.clickCopySelectButton();
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

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7239")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createAndCreateAnother()
    {
        siteName = "Site-C7239-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule1-C7239-" + random;
        ruleName2 = "rule2-C7239-" + random;
        folderName = "Folder-C7239-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.clickCreateAndCreateAnotherButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP2: Fill in Create Rule details and submit form");
        editRulesPage.typeRuleDetails(ruleName2, description, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
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

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7240")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void cancelCreateRule()
    {
        siteName = "Site-C7240-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule-C7240-" + random;
        folderName = "Folder-C7240-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickCancelButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7245")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void disableRule()
    {
        siteName = "Site-C7245-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule-C7245-" + random;
        folderName = "Folder-C7245-" + random;
        fileName2 = "FileName2-C7245-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
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
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("STEP2: Create a file in folder");
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.HTML, fileName, "docContent");

        LOG.info("STEP3: Navigate to site document library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");

        LOG.info("STEP4: Navigate to Manage Rule page for folder1. Click 'Edit'");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");
        ruleDetailsPage.renderedPage();
        ruleDetailsPage.clickButton("edit");
        assertEquals(editRulesPage.getRulePageHeader(), String.format(language.translate("rules.editPageHeader"), ruleName1), "Page header=");

        LOG.info("STEP5: Click on 'Disable rule' \n" + "Click 'Save'");
        editRulesPage.clickDisableRuleCheckbox();
        editRulesPage.clickSaveButton();

        LOG.info("STEP6: Create a file in folder");
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.HTML, fileName2, "docContent");

        LOG.info("STEP7: Navigate to site Document Library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName2), fileName2 + " displayed in Document Library");

        documentLibraryPage.clickOnFolderName(folderName);
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folderName).toString(), "Document Library breadcrumb");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName2), fileName2 + " displayed in " + folderName);

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C6621")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createRuleItemsAreUpdated()
    {
        siteName = "Site-6621-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule-C6621-" + random;
        folderName = "Folder-C6621-" + random;
        fileName = "FileName-C6621-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder1");
        // documentLibraryPage.mouseOverContentItem(folderName);
        // documentLibraryPage.clickMoreMenu(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        getBrowser().waitInSeconds(2);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
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
        // getBrowser().findElement(By.id("Value")).sendKeys(Keys.ARROW_DOWN);
        // documentLibraryPage.mouseOverFileName(fileName);
        documentLibraryPage.clickMoreMenu(fileName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, language.translate("documentLibrary.contentActions.editInAlfresco"), editInAlfrescoPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Edit in Alfresco", "Displayed page=");
        editInAlfrescoPage.typeContent("Content updated!");
        editInAlfrescoPage.clickSaveButton();
        // editInAlfrescoPage.clickSaveButton();

        // editInAlfrescoPage.clickButton("Save");
        getBrowser().waitInSeconds(2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C6623")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createRuleItemsAreDeleted()
    {
        siteName = "Site-C6623-" + random;
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        ruleName1 = "rule-C6623-" + random;
        folderName = "Folder-C6623-" + random;
        fileName = "FileName-C6623-" + random;
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        getBrowser().waitInSeconds(2);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("STEP1: Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(2, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("STEP2: Create a file in folder");
        getBrowser().waitInSeconds(2);
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
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed.");

        cleanupAuthenticatedSession();
    }
}