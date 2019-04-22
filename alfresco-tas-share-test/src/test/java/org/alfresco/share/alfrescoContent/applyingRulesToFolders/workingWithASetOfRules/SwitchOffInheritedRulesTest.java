package org.alfresco.share.alfrescoContent.applyingRulesToFolders.workingWithASetOfRules;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.RuleDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
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

import static org.alfresco.dataprep.CMISUtil.DocumentType.TEXT_PLAIN;
import static org.testng.Assert.assertEquals;

/**
 * @author Laura.Capsa
 */
public class SwitchOffInheritedRulesTest extends ContextAwareWebTest
{
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

    @Autowired
    SelectDestinationDialog selectDestinationDialog;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private final String firstName = "First Name";
    private final String lastName = "Last Name";
    private final String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private final String ruleName = "rule-C7325-" + random;
    private final String folder1 = "Folder1-C7325-" + random;
    private final String folder2 = "Folder2-C7325-" + random;
    private final String fileName = "File-C7325-" + random;
    private final String path = "Sites/" + siteName + "/documentLibrary/" + folder1;

    @BeforeClass()
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folder1, siteName);
        contentService.createFolderInRepository(userName, password, folder2, path);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        LOG.info("Navigate to Manage Rule page for " + folder1);
        documentLibraryPage.clickDocumentLibraryItemAction(folder1, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folder1 + ": Rules", "Rule title=");
        LOG.info("Navigate to Create rule page");
        manageRulesPage.clickCreateRules();
        editRulesPage.setCurrentSiteName(siteName);
        assertEquals(editRulesPage.getRelativePath(), "share/page/site/" + siteName + "/rule-edit", "Redirected to=");
        LOG.info("Fill in Create Rule details and submit form");
        List<Integer> indexOfOptionFromDropdown = Arrays.asList(0, 0, 2);
        editRulesPage.typeRuleDetails(ruleName, description, indexOfOptionFromDropdown);
        selectDestinationDialog.renderedPage();
        selectDestinationDialog.clickSite(siteName);
        selectDestinationDialog.clickPathFolder(path);
        selectDestinationDialog.clickOkButton();
        editRulesPage.renderedPage();
        editRulesPage.clickCreateButton();
        assertEquals(ruleDetailsPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        editRulesPage.cleanupSelectedValues();
        LOG.info("Navigate inside " + folder1);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.clickOnFolderName(folder1);
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folder1).toString(), "Document Library breadcrumb=");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword,siteName);
    }

    @TestRail(id = "C7325")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void switchOffInheritRules() {
        LOG.info("STEP1: Navigate to 'Manage Rules' page for " + folder2);
        documentLibraryPage.clickDocumentLibraryItemAction(folder2, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        assertEquals(manageRulesPage.getPageTitle(), "Alfresco » Folder Rules", "Displayed page=");
        assertEquals(manageRulesPage.getRuleTitle(), folder2 + ": Rules", "Rule title=");
        LOG.info("STEP2: Click on 'Inherit Rules' button");
        manageRulesPage.clickInheritButton();
        assertEquals(manageRulesPage.getInheritButtonText(), "Don't Inherit Rules", "Inherit button text=");
        LOG.info("STEP3: Navigate to Document Library -> 'Folder1'");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.clickOnFolderName(folder1);
        documentLibraryPage.clickOnFolderName(folder2);
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folder1, folder2).toString(), "Document Library breadcrumb=");
        LOG.info("STEP4: Create a file");
        contentService.createDocumentInRepository(userName, password, path + "/" + folder2, TEXT_PLAIN, fileName, "content of Document");
        assertEquals(documentLibraryPage.getFilesList(), Arrays.asList(fileName), folder2 + " content=");
    }
}
