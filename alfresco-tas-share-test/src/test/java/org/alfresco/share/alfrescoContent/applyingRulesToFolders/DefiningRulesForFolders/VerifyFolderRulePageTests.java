package org.alfresco.share.alfrescoContent.applyingRulesToFolders.DefiningRulesForFolders;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.alfrescoContent.applyingRulesToFolders.AbstractFolderRuleTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class VerifyFolderRulePageTests extends AbstractFolderRuleTest
{
    private static final String DOCUMENT_LIBRARY_PAGE_TITLE = "documentLibrary.browserTitle";
    private static final String MANAGE_RULE_PAGE_TITLE = "documentLibrary.rules.browserTitle";
    private static final String NO_RULE_DEFINED_MSG = "documentLibrary.rules.noRules";
    private static final String CREATE_RULE_LINK_TEXT = "documentLibrary.rules.createLinkText";
    private static final String CREATE_RULE_DESCRIPTION = "documentLibrary.rules.createDescription";
    private static final String LINK_TO_RULE_SET_LINK_TEXT = "documentLibrary.rules.linkToRuleSetLinkText";
    private static final String LINK_TO_RULE_SET_DESCRIPTION = "documentLibrary.rules.linkToRuleSetDescription";
    private static final String INHERIT_BUTTON_TEXT = "documentLibrary.rules.inheritButton";

    private FolderModel folderToCheck;
    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;
    private EditRulesPage editRulesPage;

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

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail(id = "C6367")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyFolderRulesPage()
    {
        siteDashboardPage.navigate(site.get()).navigateToDocumentLibraryPage();

        log.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage
            .assertDocumentLibraryPageTitleEquals(language.translate(DOCUMENT_LIBRARY_PAGE_TITLE))
            .selectItemAction(folderToCheck.getName(), MANAGE_RULES);

        log.info("STEP2: Verify components on page");
        manageRulesPage.assertCreateRuleLinkTextEquals(language.translate(CREATE_RULE_LINK_TEXT))
            .assertNoRulesTextIsEqual(language.translate(NO_RULE_DEFINED_MSG))
            .assertCreateRuleDescriptionTextEquals(language.translate(CREATE_RULE_DESCRIPTION))
            .assertLinkToRuleSetLinkTextEquals(language.translate(LINK_TO_RULE_SET_LINK_TEXT))
            .assertLinkToRuleSetDescriptionEquals(language.translate(LINK_TO_RULE_SET_DESCRIPTION))
            .assertInheritButtonTextEquals(language.translate(INHERIT_BUTTON_TEXT))
            .assertBrowserPageTitleIs(language.translate(MANAGE_RULE_PAGE_TITLE));
    }

    @TestRail (id = "C12857")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void verifyCreateRulePageDropdownElements()
    {
        siteDashboardPage.navigate(site.get()).navigateToDocumentLibraryPage();

        log.info("STEP1: Hover created folder, click on 'More' menu -> 'Manage Rules' option");
        documentLibraryPage
            .assertDocumentLibraryPageTitleEquals(language.translate(DOCUMENT_LIBRARY_PAGE_TITLE))
            .selectItemAction(folderToCheck.getName(), MANAGE_RULES);

        manageRulesPage.assertNoRulesTextIsEqual(language.translate(NO_RULE_DEFINED_MSG))
            .assertBrowserPageTitleIs(language.translate(MANAGE_RULE_PAGE_TITLE));

        log.info("STEP2: Click on 'Create Rule' link");
        manageRulesPage.openCreateNewRuleForm();

        editRulesPage.setCurrentSiteName(site.get().getTitle());

        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + site.get().getTitle() + "/rule-edit", "Redirected to=");

        log.info("STEP3: Verify the 'If all criteria are met:' drop-down values");
        ArrayList<String> expectedOptionsList = new ArrayList<>(Arrays
            .asList("All Items", "Size", "Created Date", "Modified Date", "Creator", "Modifier",
                "Author", "Mimetype", "Encoding", "Description", "Name", "Title", "Has tag",
                "Has category", "Content of type or sub-type", "Has aspect", "Show more..."));
        editRulesPage.verifyDropdownOptions("ruleConfigIfCondition", expectedOptionsList);
        expectedOptionsList.clear();

        log.info("STEP4: Verify \"Perform Action\" drop-down values");
        expectedOptionsList = new ArrayList<>(Arrays
            .asList("Select...", "Execute script", "Copy", "Move", "Check in", "Check out",
                "Link to category", "Add aspect", "Remove aspect", "Add simple workflow",
                "Send email", "Transform and copy content", "Transform and copy image",
                "Extract common metadata fields", "Import", "Specialise type", "Increment Counter",
                "Set property value", "Start Process", "webqs_publish"));
        editRulesPage.verifyDropdownOptions("ruleConfigAction", expectedOptionsList);
    }
}