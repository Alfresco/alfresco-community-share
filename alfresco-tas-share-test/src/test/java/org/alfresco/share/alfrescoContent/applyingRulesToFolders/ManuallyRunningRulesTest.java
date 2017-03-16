package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class ManuallyRunningRulesTest extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private ManageRulesPage manageRulesPage;

    @Autowired private EditRulesPage editRulesPage;

    @Autowired private RuleDetailsPage ruleDetailsPage;

    @Autowired private DocumentCommon documentCommon;

    private final String random = DataUtil.getUniqueIdentifier();
    private final String userName = "user-" + random;
    private final String firstName = "First Name";
    private final String lastName = "Last Name";
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String ruleName = "rule-C7320-" + random;
    private final String folderName = "Folder-C7320-" + random;
    private final String fileName = "FileName-C7320-" + random;

    @BeforeClass()
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "content of Document");

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder");
        documentLibraryPage.mouseOverContentItem(folderName);
        getBrowser().waitInSeconds(2);
        documentLibraryPage.clickMoreMenu(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        getBrowser().waitInSeconds(2);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 1);
        editRulesPage.typeRuleDetails(ruleName, description, indexOfOptionFromDropdown);
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
    }

    @TestRail(id = "C7320")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void runRule()
    {
        LOG.info("STEP1: Click 'Run' button for rule");
        ruleDetailsPage.clickButton("runRules");
        ruleDetailsPage.clickOnRunRulesOption(0);

        LOG.info("STEP2: Navigate to folder and verify file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentCommon.isActiveWorkflowIconDisplayed(), "Active workflow icon is displayed.");

        cleanupAuthenticatedSession();
    }
}
