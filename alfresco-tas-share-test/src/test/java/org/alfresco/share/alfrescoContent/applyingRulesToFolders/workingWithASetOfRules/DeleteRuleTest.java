package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.alfrescoContent.applyingRulesToFolders.AbstractFolderRuleTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteRuleTest extends AbstractFolderRuleTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String ruleName = "rule-C7254-" + random;

    private FolderModel folderToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private DeleteDialog deleteDialog;

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
        manageRulesPage = new ManageRulesPage(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        authenticateUsingCookies(user.get());
    }

    @TestRail(id = "C7267")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void shouldDeleteExistingRule()
    {
        createFolderRule(user.get(), folderToCheck, "copy", ruleName, description, false, true,
            false, "inbound");

        siteDashboardPage.
            navigate(site.get()).
            navigateToDocumentLibraryPage();

        documentLibraryPage.
            selectItemAction(folderToCheck.getName(), MANAGE_RULES);

        ruleDetailsPage.
            clickButton("delete");

        deleteDialog.
            confirmDeletion();

        manageRulesPage.
            assertNoRulesTextIsEqual("No Rules are defined for this folder");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}


