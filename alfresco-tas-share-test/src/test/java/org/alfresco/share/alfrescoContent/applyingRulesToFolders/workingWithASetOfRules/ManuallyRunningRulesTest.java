package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class ManuallyRunningRulesTest extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String ruleName = "rule-C7320-" + random;
    private final String folderName = "Folder-C7320-" + random;
    private final String fileName = "FileName-C7320-" + random;
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private ManageRulesPage manageRulesPage;
    @Autowired
    private EditRulesPage editRulesPage;
    @Autowired
    private RuleDetailsPage ruleDetailsPage;
    @Autowired
    private DocumentCommon documentCommon;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "First Name", "Last Name");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);

        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "content of Document");

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_RULES, manageRulesPage);
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

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7320")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void runRule()
    {
        LOG.info("STEP1: Click 'Run' button for rule");
        ruleDetailsPage.clickButton("runRules");
        ruleDetailsPage.clickOnRunRulesOption(0);

        LOG.info("STEP2: Navigate to folder and verify file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentCommon.isActiveWorkflowIconDisplayed(), "Active workflow icon is displayed.");
    }
}
