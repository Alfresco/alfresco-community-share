package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithLinkedRules;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.LinkedToRuleSetPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class LinkingToDifferentRuleSetTest extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private ManageRulesPage manageRulesPage;

    @Autowired
    private EditRulesPage editRulesPage;

    @Autowired
    private LinkedToRuleSetPage linkedToRuleSetPage;

    @Autowired
    private SelectDestinationDialog selectDestinationDialog;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String folderName = "Folder-C7327-" + random;
    private final String folderName2 = "Folder2-C7327-" + random;
    private final String ruleName1 = "rule-C7327-" + random;

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "First Name", "Last Name");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        LOG.info("Navigate to Manage Rule page for folder1");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName + ": Rules", "Rule title=");
        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");
        LOG.info("Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName1, description, indexOfOptionFromDropdown);
        selectDestinationDialog.clickSite(siteName);
        String path = "Documents";
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
        LOG.info("Navigate to Manage Rule page for folder2");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName2, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folderName2 + ": Rules", "Rule title=");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword,siteName);
    }

    @TestRail(id = "C7327")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void linkToRuleSet() {
        LOG.info("STEP1: Click on 'Link to Rule Set' link\n" + "Set path to 'Folder1'.\n" + "Click 'Link' button");
        manageRulesPage.clickLinkToRuleSet();
        selectDestinationDialog.clickPathFolder(folderName);
        selectDestinationDialog.clickLinkButton();
        linkedToRuleSetPage.renderedPage();
        linkedToRuleSetPage.setCurrentSiteName(siteName);
        assertEquals(linkedToRuleSetPage.getRelativePath(), "share/page/site/" + siteName + "/folder-rules", "Redirected to=");
        LOG.info("STEP2: Click \"Done\" button");
        linkedToRuleSetPage.clickButton("done");
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        LOG.info("STEP3: Create a file in folder2");
        String fileName = "testDoc.txt";
        contentService.createDocumentInFolder(userName, password, siteName, folderName2, CMISUtil.DocumentType.HTML, fileName, "docContent");
        LOG.info("STEP4: Navigate to Document Library");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed");
    }
}
