package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static org.alfresco.po.share.site.ItemActions.MANAGE_RULES;

import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
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

/**
 * @author Laura.Capsa
 */
public class SwitchOffInheritedRulesTest extends AbstractFolderRuleTest
{
    private static final String DOCUMENT_LIBRARY_PAGE_TITLE = "documentLibrary.browserTitle";
    private static final String INHERIT_BUTTON_TEXT ="documentLibrary.rules.inheritButton";
    private static final String INHERITED_RULE_INFO_MSG = "documentLibrary.rules.inheritRuleInfoMsg";
    private static final String DONT_INHERIT_BTN_TEXT = "documentLibrary.rules.dontInheritRuleButton";

    private final String random = RandomData.getRandomAlphanumeric();
    private final String parentFolder = "ParentFolder-" + random;
    private final String childFolder = "ChildFolder-" + random;
    private final String description = "description-" + random;
    private final String ruleName = "rule-C7254-" + random;

    private DocumentLibraryPage documentLibraryPage;
    private SiteDashboardPage siteDashboardPage;
    private ManageRulesPage manageRulesPage;

    private FolderModel folderToCheck;
    private FileModel file;

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

        authenticateUsingCookies(user.get());

        folderToCheck = FolderModel.getRandomFolderModel();
        folderToCheck.setName(parentFolder);

        getCmisApi()
            .usingSite(site.get())
            .createFolder(folderToCheck)
            .assertThat().existsInRepo();

        createFolderRule(user.get(),
            folderToCheck, "copy",
            ruleName, description,
            false, true, false, "inbound");

        folderToCheck.setName(childFolder);
        getCmisApi()
            .usingSite(site.get())
            .usingResource(folderToCheck)
            .createFolder(folderToCheck);
    }

    @TestRail(id = "C7325")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT, TestGroup.INTEGRATION })
    public void shouldSwitchOffTheInheritRules()
    {
        siteDashboardPage
            .navigate(site.get())
            .navigateToDocumentLibraryPage();

        documentLibraryPage
            .clickOnFolderName(parentFolder)
            .selectItemAction(childFolder, MANAGE_RULES);

        manageRulesPage
            .assertInheritButtonTextEquals(language.translate(INHERIT_BUTTON_TEXT))
            .assertInheritRuleInfoMessageEquals(language.translate(INHERITED_RULE_INFO_MSG))
            .clickInheritButton()
            .assertInheritButtonTextEquals(language.translate(DONT_INHERIT_BTN_TEXT));

        documentLibraryPage
            .navigate(site.get())
            .assertDocumentLibraryPageTitleEquals(language.translate(DOCUMENT_LIBRARY_PAGE_TITLE))
            .clickOnFolderName(parentFolder)
            .clickOnFolderName(childFolder);

        file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN);
        getCmisApi().usingSite(site.get()).usingResource(folderToCheck).usingResource(folderToCheck)
            .createFile(file);

        documentLibraryPage
            .assertFileIsDisplayed(file.getName())
            .navigate(site.get())
            .clickOnFolderName(parentFolder)
            .assertFileIsNotDisplayed(file.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}