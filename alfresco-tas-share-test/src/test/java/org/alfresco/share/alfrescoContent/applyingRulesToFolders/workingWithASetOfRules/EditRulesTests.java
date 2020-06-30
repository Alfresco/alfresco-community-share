package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
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
public class EditRulesTests extends ContextAwareWebTest
{
    private final String userName = "user-" + RandomData.getRandomAlphanumeric();
    private final String siteName = "Site-" + RandomData.getRandomAlphanumeric();
    private final String description = "description-" + RandomData.getRandomAlphanumeric();
    private final String path = "Documents";
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private ManageRulesPage manageRulesPage;
    @Autowired
    private EditRulesPage editRulesPage;
    @Autowired
    private RuleDetailsPage ruleDetailsPage;
    @Autowired
    private SelectDestinationDialog selectDestinationDialog;
    private String folderName, ruleName;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        ruleName = "rule-" + RandomData.getRandomAlphanumeric();
        folderName = "folder-" + RandomData.getRandomAlphanumeric();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "First Name", "Last Name");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);

        documentLibraryPage.navigate(siteName);
        LOG.info("Navigate to Manage Rule page for folder");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_RULES, manageRulesPage);
        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");
        LOG.info("Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        editRulesPage.cleanupSelectedValues();

    }


    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7254")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void editRuleDetails()
    {
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_RULES, manageRulesPage);

        String updatedRuleName = "updateRule-C7254-" + RandomData.getRandomAlphanumeric();
        String updatedDescription = "Updated Rule description";
        LOG.info("STEP1: Click 'Edit' button for rule");
        ruleDetailsPage.clickButton("edit");
        assertEquals(editRulesPage.getRulePageHeader(), String.format(language.translate("rules.editPageHeader"), ruleName), "Page header=");
        LOG.info("STEP2: Fill in Create Rule details with new details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(updatedRuleName, updatedDescription, indexOfOptionFromDropdown);
//        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickSaveButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        assertEquals(ruleDetailsPage.getRuleTitle(), updatedRuleName, "Rule title=");
        assertEquals(ruleDetailsPage.getRuleDescription(), updatedDescription, "Rule description=");
        assertEquals(ruleDetailsPage.getDetailsList().toString(), expectedDescriptionDetails.toString(), "Description details=");
        assertEquals(ruleDetailsPage.getWhenCondition(), editRulesPage.getSelectedOptionFromDropdown().get(0), "'When' criteria section=");
        assertEquals(ruleDetailsPage.getIfAllCriteriaCondition(), editRulesPage.getSelectedOptionFromDropdown().get(1), "'If all criteria are met' section=");
        assertEquals(ruleDetailsPage.getPerformAction(), "Copy items to .../documentLibrary", "'Perform Action' section=");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail (id = "C7258")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void disableRule()
    {
        String fileName = "fileC7258-" + RandomData.getRandomAlphanumeric();
        LOG.info("STEP1: Click 'Edit' button for rule");
        ruleDetailsPage.clickButton("edit");
        assertEquals(editRulesPage.getRulePageHeader(), String.format(language.translate("rules.editPageHeader"), ruleName), "Page header=");
        LOG.info("STEP2: Check \"Disable rule\" checkbox.\n" + "Click \"Save\" button");
        editRulesPage.clickDisableRuleCheckbox();
        editRulesPage.clickSaveButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        LOG.info("STEP3: Create a file in folder and verify if rule is applied");
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.HTML, fileName, "docContent");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed in Document Library");
        editRulesPage.cleanupSelectedValues();
    }
}