package org.alfresco.share.alfrescoContent.applyingRulesToFolders.DefiningRulesForFolders;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.alfrescoContent.applyingRulesToFolders.AbstractFolderRuleTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;
import static org.testng.Assert.assertEquals;

@Slf4j
public class ItemAreUpdatedTests extends AbstractFolderRuleTest
{
    private static final String DOCUMENT_LIBRARY_PAGE_TITLE = "documentLibrary.browserTitle";
    private static final String MANAGE_RULE_PAGE_TITLE = "documentLibrary.rules.browserTitle";
    private static final String NO_RULE_DEFINED_MSG = "documentLibrary.rules.noRules";
    private static final String ITEMS_ARE_UPDATED = "documentLibrary.rules.createRule.rulesDetailsPage.when.itemsAreUpdated";
    private static final String ALL_ITEMS = "documentLibrary.rules.createRule.rulesDetailsPage.ifAllCriteria.allItems";
    private static final String COPY_ITEM_TO = "documentLibrary.rules.createRule.rulesDetailsPage.performAction.copyItem";
    private static final String EDIT_IN_ALFRESCO_PAGE_TITLE = "documentLibrary.contentActions.editInAlfresco.pageTitle";

    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;

    private FolderModel folderToCheck;
    private FileModel fileToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;
    private EditRulesPage editRulesPage;
    private RuleDetailsPage ruleDetailsPage;
    private SelectDestinationDialog selectDestinationDialog;
    private EditInAlfrescoPage editInAlfrescoPage;

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
        editRulesPage = new EditRulesPage(webDriver);
        selectDestinationDialog = new SelectDestinationDialog(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);
        editInAlfrescoPage = new EditInAlfrescoPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C6621")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void itemsAreUpdated()
    {
        String ruleName1 = "rule-C6372-" + random;
        siteDashboardPage.navigate(site.get()).navigateToDocumentLibraryPage();

        log.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage
            .assertDocumentLibraryPageTitleEquals(language.translate(DOCUMENT_LIBRARY_PAGE_TITLE))
            .selectItemAction(folderToCheck.getName(), MANAGE_RULES);

        manageRulesPage
            .assertNoRulesTextIsEqual(language.translate(NO_RULE_DEFINED_MSG))
            .assertBrowserPageTitleIs(language.translate(MANAGE_RULE_PAGE_TITLE));

        log.info("STEP2: Click on 'Create Rule' link");
        manageRulesPage.openCreateNewRuleForm();

        editRulesPage.setCurrentSiteName(site.get().getTitle());
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + site.get().getTitle() + "/rule-edit", "Redirected to=");

        log.info("STEP3: Type rule name, description and select value from each dropdown");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(1, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(site.get().getTitle());
        selectDestinationDialog.selectFolderPath("Documents");
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();

        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        ruleDetailsPage
            .assertRulesPageTitleEquals(ruleName1)
            .assertRuleDescriptionDetailsEqualTo(expectedDescriptionDetails)
            .assertWhenConditionTextEquals(language.translate(ITEMS_ARE_UPDATED))
            .assertIfAllCriteriaConditionEquals(language.translate(ALL_ITEMS))
            .assertPerformedActionEquals(language.translate(COPY_ITEM_TO));

        editRulesPage.cleanupSelectedValues();

        log.info("STEP4: Create a file in folder");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).createFile(fileToCheck);

        log.info("STEP5: Navigate to site's document library");
        documentLibraryPage
            .navigate(site.get().getId())
            .navigateToDocumentLibraryPage();

        log.info("Step 6: Veriy file should not be displayed in the document library");
        documentLibraryPage
            .assertFileIsNotDisplayed(fileToCheck.getName());

        log.info("STEP7: Navigate to folder content. For file click on 'Edit in Alfresco' and update the content");
        documentLibraryPage
            .clickOnFolderName(folderToCheck.getName())
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.EDIT_IN_ALFRESCO);

        editInAlfrescoPage
            .assertEditInAlfrescoPageTitleEquals(language.translate(EDIT_IN_ALFRESCO_PAGE_TITLE));
        editInAlfrescoPage.typeContent("Content updated!");
        editInAlfrescoPage.clickSaveButton();

        log.info("STEP8: Navigate to the Document Library and verify file is displayed");
        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        documentLibraryPage
            .assertFileIsDisplayed(fileToCheck.getName());
    }

}