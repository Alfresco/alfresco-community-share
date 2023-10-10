package org.alfresco.share.alfrescoContent.applyingRulesToFolders.DefiningRulesForFolders;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Slf4j
public class ItemsAreCreatedRuleAppliesToSubfoldersTests extends AbstractFolderRuleTest
{
    private static final String DOCUMENT_LIBRARY_PAGE_TITLE = "documentLibrary.browserTitle";
    private static final String MANAGE_RULE_PAGE_TITLE = "documentLibrary.rules.browserTitle";
    private static final String NO_RULE_DEFINED_MSG = "documentLibrary.rules.noRules";
    private static final String ITEMS_ARE_CREATED = "documentLibrary.rules.createRule.rulesDetailsPage.when.itemsAreCreated";
    private static final String ALL_ITEMS = "documentLibrary.rules.createRule.rulesDetailsPage.ifAllCriteria.allItems";
    private static final String COPY_ITEM_TO = "documentLibrary.rules.createRule.rulesDetailsPage.performAction.copyItem";

    private final String random = RandomData.getRandomAlphanumeric();
    private final String description = "description-" + random;
    private final String parentFolder = "ParentFolder-" + random;
    private final String childFolder = "ChildFolder-" + random;
    private final String ruleName = "rule-C7254-" + random;

    private FolderModel folderToCheck;
    private FileModel fileToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;
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
        manageRulesPage = new ManageRulesPage(webDriver);
        editRulesPage = new EditRulesPage(webDriver);
        selectDestinationDialog = new SelectDestinationDialog(webDriver);
        ruleDetailsPage = new RuleDetailsPage(webDriver);

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C6622")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyItemsAreCreatedRulesAppliesToSubfolder()
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
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(site.get().getTitle());
        selectDestinationDialog.selectFolderPath("Documents");
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickRulesAppliesToSubfoldersCheckbox();
        editRulesPage.clickCreateButton();

        ArrayList<String> expectedDescriptionDetails = new ArrayList<>(Arrays.asList("Active", "Run in background", "Rule applied to subfolders"));
        ruleDetailsPage
            .assertRulesPageTitleEquals(ruleName1)
            .assertRuleDescriptionDetailsEqualTo(expectedDescriptionDetails)
            .assertWhenConditionTextEquals(language.translate(ITEMS_ARE_CREATED))
            .assertIfAllCriteriaConditionEquals(language.translate(ALL_ITEMS))
            .assertPerformedActionEquals(language.translate(COPY_ITEM_TO));

        editRulesPage.cleanupSelectedValues();

        log.info("STEP4: Creating a Subfolder");
        folderToCheck.setName(childFolder);
        getCmisApi()
            .usingSite(site.get())
            .usingResource(folderToCheck)
            .createFolder(folderToCheck);

        log.info("STEP5: Navigate to site's document library");
        documentLibraryPage
            .navigate(site.get().getId())
            .navigateToDocumentLibraryPage();

        log.info("STEP6: Create a file in folder");
        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, description);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).usingResource(folderToCheck).createFile(fileToCheck);

        log.info("STEP7: Navigate to site's document library");
        documentLibraryPage
            .navigate(site.get().getId())
            .navigateToDocumentLibraryPage();

        log.info("Step 8: Verify file be displayed in the document library");
        documentLibraryPage
            .assertFileIsDisplayed(fileToCheck.getName());
    }
}