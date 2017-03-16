package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.LinkedToRuleSetPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
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

/**
 * @author Laura.Capsa
 */
public class BreakingTheLinkToARuleSetTest extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private ManageRulesPage manageRulesPage;

    @Autowired private EditRulesPage editRulesPage;

    @Autowired private RuleDetailsPage ruleDetailsPage;

    @Autowired private LinkedToRuleSetPage linkedToRuleSetPage;

    @Autowired private SelectDestinationDialog selectDestinationDialog;

    private final String random = DataUtil.getUniqueIdentifier();
    private final String userName = "user-" + random;
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String folderName = "Folder-C7332-" + random;
    private final String folderName2 = "Folder2-C7332-" + random;
    private final String ruleName = "rule-C7332-" + random;

    @BeforeClass()
    public void setupTest()
    {
        String lastName = "Last Name";
        String firstName = "First Name";
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");

        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");

        LOG.info("Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName, description, indexOfOptionFromDropdown);
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.clickSite(siteName);
        String path = "Documents";
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();

        LOG.info("Navigate to Manage Rule page for folder2");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.mouseOverContentItem(folderName2);
        documentLibraryPage.clickMoreMenu(folderName2);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName2, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName2 + ": Rules", "Rule title=");

        LOG.info("Link rule of folder2 with rule of folder1");
        manageRulesPage.clickLinkToRuleSet();
        selectDestinationDialog.clickPathFolder(folderName);
        getBrowser().waitInSeconds(3);
        selectDestinationDialog.clickOkButton();
        linkedToRuleSetPage.renderedPage();
        linkedToRuleSetPage.setCurrentSiteName(siteName);
        assertEquals(linkedToRuleSetPage.getRelativePath(), "share/page/site/" + siteName + "/folder-rules", "Redirected to=");
        linkedToRuleSetPage.clickButton("done");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");

        LOG.info("Navigate to Manage Rule page for folder2");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.mouseOverContentItem(folderName2);
        documentLibraryPage.clickMoreMenu(folderName2);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName2, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName2 + ": Rules", "Rule title=");
    }

    @TestRail(id = "C7332")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void unlinkRules()
    {
        LOG.info("STEP1: Click 'Unlink' button for the linked rule set");
        ruleDetailsPage.clickButton("unlink");
        manageRulesPage.renderedPage();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName2 + ": Rules", "Rule title=");
        assertEquals(manageRulesPage.getNoRulesText(), language.translate("documentLibrary.rules.noRules"), "Displayed rules:");
    }
}