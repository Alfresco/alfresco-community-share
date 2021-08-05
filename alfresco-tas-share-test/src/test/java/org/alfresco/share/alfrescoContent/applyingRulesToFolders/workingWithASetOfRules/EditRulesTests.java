package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Laura.Capsa
 */
public class EditRulesTests extends BaseTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String path = "Documents";
    private final String ruleName = "rule-C7254-" + random;

    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;
    private EditRulesPage editRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private SelectDestinationDialog selectDestinationDialog;
    private DeleteDialog deleteDialog;
    private FolderModel folderToCheck;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final ThreadLocal<RuleDetailsPage> ruleName1 = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true) public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        manageRulesPage = new ManageRulesPage(webDriver);
        editRulesPage = new EditRulesPage(webDriver);
        selectDestinationDialog = new SelectDestinationDialog(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        authenticateUsingCookies(user.get());

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        siteDashboardPage.navigate(site.get()).clickDocumentLibrary();

        documentLibraryPage
            .clickDocumentLibraryItemAction(folderToCheck.getName(), ItemActions.MANAGE_RULES);
        manageRulesPage.clickCreateRules();

        editRulesPage.setCurrentSiteName(site.get().getId());
        editRulesPage.assertCurrentSiteRelativePathEquals(site.get().getId());

        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(site.get().getId());
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.clickCreateButton();
    }

    @TestRail(id = "C7254")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void shouldEditRuleDetails()
    {
        createFolderRule(user.get(),
            folderToCheck, "copy",
            ruleName, description,
            false, true, false, "inbound");

    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT,
        "tobefixed" }) public void shouldRuleDetailsEdited()
    {
        String updatedRuleName = "updateRule-C7254-" + RandomData.getRandomAlphanumeric();
        String updatedDescription = "Updated Rule description";

        ruleDetailsPage.clickButton("edit");
        editRulesPage.assertRulesPageHeaderEquals(ruleName);

        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage
            .typeRuleDetails(updatedRuleName, updatedDescription, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(site.get().getId());
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.clickSaveButton();

        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(
            Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        ruleDetailsPage.assertRulesPageTitleEquals(updatedRuleName);
        ruleDetailsPage.assertRuleDescriptionEquals(updatedDescription);
        ruleDetailsPage.assertRuleDetailsListEquals(expectedDescriptionDetails.toString());
        ruleDetailsPage
            .assertWhenConditionEquals(editRulesPage.getSelectedOptionFromDropdown().get(3));
        ruleDetailsPage.assertIfAllCriteriaConditionEquals(
            editRulesPage.getSelectedOptionFromDropdown().get(1));
        ruleDetailsPage.assertPerformedActionEquals("Copy items to .../documentLibrary");
    }

    @TestRail(id = "C7258")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void shouldRuleGetDisable()
    {
        createFolderRule(user.get(),
            folderToCheck, "copy",
            ruleName, description,
            true, false, false, "inbound");

        editRulesPage.clickDisableRuleCheckbox();
        editRulesPage.clickSaveButton();
        manageRulesPage.assertManageRulesPageTitleEquals("Alfresco » Folder Rules");

        FileModel fileModel = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "Test content");
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileModel);

        documentLibraryPage.navigate(site.get().getId());
        documentLibraryPage.assertDocumentLibraryPageTitleEquals("Alfresco » Document Library");
        documentLibraryPage.assertContentNameNotDisplayedFalse(fileModel.getName());
        editRulesPage.cleanupSelectedValues();
    }

    @AfterMethod(alwaysRun = true) public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}