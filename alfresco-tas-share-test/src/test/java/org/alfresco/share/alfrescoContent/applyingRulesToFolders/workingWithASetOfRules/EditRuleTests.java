package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Laura.Capsa
 */
public class EditRuleTests extends ContextAwareWebTest
{
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

    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String path = "Documents";
    private String folderName, ruleName;

    @BeforeClass(alwaysRun = true)
    public void createUserAndSite()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "First Name", "Last Name");
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        String random = RandomData.getRandomAlphanumeric();
        ruleName = "rule-" + random;
        folderName = "folder-" + random;

        contentService.createFolder(userName, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Navigate to Manage Rule page for folder");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

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
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail(id = "C7254")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editRuleDetails()
    {
        String updatedRuleName = "updateRule-C7254-" + random;
        String updatedDescription = "Updated Rule description";

        LOG.info("STEP1: Click 'Edit' button for rule");
        ruleDetailsPage.clickButton("edit");
        assertEquals(editRulesPage.getRulePageHeader(), String.format(language.translate("rules.editPageHeader"), ruleName), "Page header=");

        LOG.info("STEP2: Fill in Create Rule details with new details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(updatedRuleName, updatedDescription, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
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

    @TestRail(id = "C7258")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void disableRule()
    {
        String fileName = "fileC7258-" + random;

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
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed in Document Library");

        editRulesPage.cleanupSelectedValues();
    }
}