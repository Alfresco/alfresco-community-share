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
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    ManageRulesPage manageRulesPage;

    @Autowired
    EditRulesPage editRulesPage;

    @Autowired
    RuleDetailsPage ruleDetailsPage;

    @Autowired
    DocumentCommon documentCommon;

    String random = DataUtil.getUniqueIdentifier();
    String userName = "user-" + random;
    String firstName = "First Name";
    String lastName = "Last Name";
    String siteName = "Site-" + random;
    String description = "description-" + random;
    String ruleName = "rule-C7320-" + random;
    String folderName = "Folder-C7320-" + random;
    String fileName = "FileName-C7320-" + random;

    @BeforeClass()
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);

        content.createFolder(userName, password, folderName, siteName);
        contentService.createDocumentInFolder(userName, password, siteName, folderName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "content of Document");

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder");
        documentLibraryPage.mouseOverContentItem(folderName);
        browser.waitInSeconds(2);
        documentLibraryPage.clickMoreMenu(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        browser.waitInSeconds(2);
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
    @Test()
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
