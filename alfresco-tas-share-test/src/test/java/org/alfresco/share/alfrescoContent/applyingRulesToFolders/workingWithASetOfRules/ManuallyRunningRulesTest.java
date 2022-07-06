package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.alfrescoContent.applyingRulesToFolders.AbstractFolderRuleTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class ManuallyRunningRulesTest extends AbstractFolderRuleTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String ruleName = "rule-C7320-" + random;

    private DocumentLibraryPage documentLibraryPage;
    private ManageRulesPage manageRulesPage;
    private EditRulesPage editRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private SiteDashboardPage siteDashboardPage;
    private SelectDestinationDialog selectDestinationDialog;
    private DeleteDialog deleteDialog;

    private FolderModel folderToCheck;
    private FileModel fileModel;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
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
        getCmisApi()
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat().existsInRepo();

        fileModel = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "Test content");
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileModel);
    }

    @TestRail(id = "C7320")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void shouldAbleToRunCreatedRule()
    {
        createFolderRule(user.get(),
            folderToCheck, "script",
            ruleName, description,
            false, false, false, "inbound");

        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        documentLibraryPage
            .assertDocumentLibraryPageTitleEquals("Alfresco » Document Library");

        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), MANAGE_RULES);

        ruleDetailsPage
            .openEditRuleForm();

        editRulesPage
            .clickSaveButton();

        ruleDetailsPage
            .clickButton("runRules");
        ruleDetailsPage
            .clickOnRunRulesOption(0);

        documentLibraryPage
            .navigate(site.get().getId())
            .clickOnFolderName(folderToCheck.getName())
            .isActiveWorkflowsIconDisplayed(fileModel.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}