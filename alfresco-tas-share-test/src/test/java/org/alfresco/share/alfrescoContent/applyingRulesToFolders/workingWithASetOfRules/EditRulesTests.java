package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static java.util.Arrays.asList;
import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.alfrescoContent.applyingRulesToFolders.AbstractFolderRuleTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EditRulesTests extends AbstractFolderRuleTest
{
    private static final String ACTIVE_RULE = "documentLibrary.contentActions.manageRules.status.active";
    private static final String RUN_IN_BACKGROUND = "documentLibrary.contentActions.manageRules.background";
    private static final String RULE_APPLIED_TO_SUB_FOLDERS = "documentLibrary.contentActions.manageRules.apply";

    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String updatedRuleName = "updateRule-C7254-" + RandomData.getRandomAlphanumeric();
    private final String updatedDescription = "Updated Rule description";
    private final List<Integer> indexOfOptionFromDropdown = asList(1, 0, 2);

    private final String path = "Documents";
    private final String ruleName = "rule-C7254-" + random;

    private FolderModel folderToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private EditRulesPage editRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private SelectDestinationDialog selectDestinationDialog;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        editRulesPage = new EditRulesPage(webDriver);
        selectDestinationDialog = new SelectDestinationDialog(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @TestRail(id = "C7254")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void shouldEditRuleDetails()
    {
        createFolderRule(user.get(),
            folderToCheck, "copy",
            ruleName, description,
            false, true, false, "inbound");

        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), MANAGE_RULES);

        ruleDetailsPage.openEditRuleForm();
        editRulesPage.assertRulesPageHeaderEquals(ruleName);

        editRulesPage
            .typeRuleDetails(updatedRuleName, updatedDescription, indexOfOptionFromDropdown)
            .clickCopySelectButton();

        selectDestinationDialog
            .selectSite(site.get().getId())
            .selectFolderPath(path)
            .confirmFolderLocation();

        editRulesPage.clickSaveButton();

        ruleDetailsPage
            .assertRulesPageTitleEquals(updatedRuleName)
            .assertRuleDescriptionEquals(updatedDescription);

        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(asList(
            language.translate(ACTIVE_RULE),
            language.translate(RUN_IN_BACKGROUND),
            language.translate(RULE_APPLIED_TO_SUB_FOLDERS)));

        ruleDetailsPage
            .assertRuleDescriptionDetailsEqualTo(expectedDescriptionDetails)
            .assertWhenConditionTextEquals("Items are updated")
            .assertIfAllCriteriaConditionEquals("All Items")
            .assertPerformedActionEquals("Copy items to .../documentLibrary");
    }

    @TestRail(id = "C7258")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void shouldRuleGetDisable()
    {
        createFolderRule(user.get(),
            folderToCheck, "copy",
            ruleName, description,
            true, false, false, "inbound");

        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        FileModel fileModel = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "Test content");
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileModel);

        documentLibraryPage
            .navigate(site.get().getId())
            .navigateToDocumentLibraryPage();
        documentLibraryPage.assertFileIsNotDisplayed(fileModel.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}