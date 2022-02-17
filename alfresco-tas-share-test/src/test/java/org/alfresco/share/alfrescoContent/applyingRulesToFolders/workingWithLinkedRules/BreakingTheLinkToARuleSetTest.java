package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithLinkedRules;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.LinkedToRuleSetPage;
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

/**
 * @author Laura.Capsa
 */
@Slf4j
public class BreakingTheLinkToARuleSetTest extends AbstractFolderRuleTest
{
    private static final String DOCUMENT_LIBRARY_PAGE_TITLE = "documentLibrary.browserTitle";
    private static final String RULE_BROWSER_TITLE ="documentLibrary.rules.browserTitle";
    private static final String NO_RULE_DEFINED = "documentLibrary.rules.noRules";

    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String folder1 = "Folder-1-" + random;
    private final String folder2 = "Folder2-" + random;
    private final String ruleName = "rule-1-" + random;

    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private LinkedToRuleSetPage linkedToRuleSetPage;
    private SelectDestinationDialog selectDestinationDialog;
    private FolderModel folderToCheck;

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
        selectDestinationDialog = new SelectDestinationDialog(webDriver);
        linkedToRuleSetPage = new LinkedToRuleSetPage(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);

        authenticateUsingCookies(user.get());

        folderToCheck = FolderModel.getRandomFolderModel();
        folderToCheck.setName(folder1);

        log.info("Create folder1{}", folder1);
        getCmisApi()
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat().existsInRepo();

        log.info("Create Rule for "+folder1+"{}",ruleName);
        createFolderRule(user.get(),
            folderToCheck, "copy",
            ruleName, description,
            false, true, false, "inbound");

        log.info("Create folder2{}", folder2);
        folderToCheck.setName(folder2);
        getCmisApi()
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat().existsInRepo();

        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        log.info("Navigate to Manage Rule page for "+folder2);
        documentLibraryPage
            .assertDocumentLibraryPageTitleEquals(language.translate(DOCUMENT_LIBRARY_PAGE_TITLE))
            .selectItemAction(folder2, MANAGE_RULES);

        manageRulesPage
            .assertRuleTitleEquals(folder2)
            .assertBrowserPageTitleIs(language.translate(RULE_BROWSER_TITLE));

        log.info("Link rule of "+folder2+" with rule of "+folder1);
        manageRulesPage
            .clickLinkToRuleSet();

        selectDestinationDialog
            .selectFolderPath(folder1)
            .confirmFolderLocation();

        linkedToRuleSetPage
            .setCurrentSiteName(site.get().getTitle());
        linkedToRuleSetPage
            .assertRelativePathEquals(site.get().getTitle())
            .clickButton("done");

        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        log.info("Navigate to Manage Rule page for "+folder2);
        documentLibraryPage
            .assertDocumentLibraryPageTitleEquals(language.translate(DOCUMENT_LIBRARY_PAGE_TITLE))
            .selectItemAction(folder2, MANAGE_RULES);
    }

    @TestRail (id = "C7332")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void shouldUnlinkTheRules()
    {
        log.info("STEP1: Click 'Unlink' button for the linked rule set");
        ruleDetailsPage
            .clickButton("unlink");

        manageRulesPage
            .assertRuleTitleEquals(folder2)
            .assertNoRulesTextIsEqual(language.translate(NO_RULE_DEFINED))
            .assertBrowserPageTitleIs(language.translate(RULE_BROWSER_TITLE));
}

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}