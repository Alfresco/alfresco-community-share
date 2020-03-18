package org.alfresco.share.alfrescoContent.buildingContent;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.Arrays;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.ManageRulesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateFolderFromTemplateTests extends ContextAwareWebTest
{
    private final String folderTemplateName = "Software Engineering Project";
    private final String userFirstName = "Jim";
    private final String userLastName = "Jones";
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private Notification notification;
    @Autowired
    private CreateFolderFromTemplate createFolderFromTemplate;
    @Autowired
    private CreateContent createContent;
    @Autowired
    private EditRulesPage editRulesPage;
    @Autowired
    private RepositoryPage repositoryPage;
    @Autowired
    private ManageRulesPage manageRulesPage;
    @Autowired
    private ManagePermissionsPage managePermissionsPage;
    @Autowired
    private AspectsForm aspectsForm;

    @TestRail (id = "C6292")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFolderFromTemplate()
    {
        String userName = String.format("userName%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        String fileName = "system-overview.html";
        String breadcrumbPath = Arrays.asList("Documents", "Software Engineering Project", "Documentation", "Samples").toString();

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C6292", "C6292");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create folder from template");
        Assert.assertTrue(createContent.isFolderTemplateDisplayed(folderTemplateName));

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createContent.clickOnFolderTemplate(folderTemplateName, createFolderFromTemplate);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Click 'Save' button.");
        createFolderFromTemplate.clickSaveButton();
        notification.waitUntilNotificationDisappears();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(folderTemplateName), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.getExplorerPanelDocuments().contains(folderTemplateName), "Subfolder not found in Documents explorer panel");

        LOG.info("STEP 4: Click on the created folder ('Software Engineering Project') in 'Library' section of the browsing pane.");
        documentLibraryPage.clickOnFolderName(folderTemplateName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Discussions"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Documentation"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Presentations"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Quality Assurance"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("UI Design"), "Subfolder not found");

        LOG.info("STEP 5: Click on the subfolder 'Documentation' in 'Library' section of the browsing pane.");
        documentLibraryPage.clickOnFolderName("Documentation");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Drafts"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Pending Approval"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Published"), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Samples"), "Subfolder not found");

        LOG.info("STEP 6: Click on the subfolder 'Samples' in 'Library' section of the browsing pane.");
        documentLibraryPage.clickOnFolderName("Samples");
        Assert.assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbPath);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "File not found");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6293")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void cancelCreatingFolderFromTemplate()
    {
        String userName = String.format("userName%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C6292", "C6292");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create folder from template");
        Assert.assertTrue(createContent.isFolderTemplateDisplayed(folderTemplateName));

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createContent.clickOnFolderTemplate(folderTemplateName, createFolderFromTemplate);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Click 'Cancel' button.");
        createFolderFromTemplate.clickCancelButton();
        Assert.assertFalse(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(folderTemplateName), "Folder not found");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C8139")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void createFolderFromTemplateUsingWildcards()
    {
        String userName = String.format("userName%s", RandomData.getRandomAlphanumeric());
        String userRole = "Coordinator";
        String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
        String templateFolderName = "template1" + RandomData.getRandomAlphanumeric();
        String folderName = "AFolder.Name";
        String illegalCharacters = "\'* \" < > \\ / . ? : |'";
        String folderPathInRepository = "Data Dictionary/Space Templates/";
        String ruleName = "Add aspect taggable rule";
        String ruleDescription = "Add aspect taggable rule test";
        String aspectTaggableValue = "Taggable";
        String aspectType = "Classifiable";

        userService.create(adminUser, adminPassword, userName, password, userName + domain, userFirstName, userLastName);
        contentService.createFolderInRepository(adminUser, adminPassword, templateFolderName, folderPathInRepository);
        setupAuthenticatedSession(adminUser, adminPassword);

        STEP("Precondition: Create any rule for template1 (e.g. rule1 when items are created or enter this folder, add aspect taggable)");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Data Dictionary");
        repositoryPage.clickOnFolderName("Space Templates");
        documentLibraryPage.clickDocumentLibraryItemAction(templateFolderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        manageRulesPage.clickCreateRules();

        editRulesPage.typeRuleDetails(ruleName, ruleDescription, Arrays.asList(0, 0, 7));
        editRulesPage.selectAspect(aspectTaggableValue);
        editRulesPage.clickCreateButton();

        STEP("Precondition: Add any permission for template1 (e.g. user1 has role Coordinator)");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Data Dictionary");
        repositoryPage.clickOnFolderName("Space Templates");
        documentLibraryPage.clickDocumentLibraryItemAction(templateFolderName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.searchAndAddUserOrGroup(userName, 0);
        managePermissionsPage.setRole(userFirstName + " " + userLastName, userRole);
        managePermissionsPage.clickButton("Save");

        STEP("Precondition: Add any aspect for template1 (e.g. classifiable)");
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("Data Dictionary");
        repositoryPage.clickOnFolderName("Space Templates");
        repositoryPage.clickDocumentLibraryItemAction(templateFolderName, "Manage Aspects", aspectsForm);
        aspectsForm.addAspect("Classifiable");
        aspectsForm.clickApplyChangesButton(repositoryPage);

        cleanupAuthenticatedSession();
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        documentLibraryPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create folder from template");
        Assert.assertTrue(createContent.isFolderTemplateDisplayed(templateFolderName));

        LOG.info("STEP 2: Select the template: 'template1'");
        createContent.clickOnFolderTemplate(templateFolderName, createFolderFromTemplate);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed(), "Create folder from template popup is displayed ");
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), templateFolderName);

        LOG.info("STEP 3: Clear the Name field. Add name \'* \" < > \\ / . ? : |' and click 'Save' button.");
        createFolderFromTemplate.fillInNameField(illegalCharacters);
        createFolderFromTemplate.clickSaveButton();
        createFolderFromTemplate.clickSaveButton();
        Assert.assertTrue(createFolderFromTemplate.isTooltipErrorMessageDisplayed(), "Tooltip error message not displayed");
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());

        LOG.info("STEP 4: Clear Name field and type 'AName.'. Click 'Save' button.");
        createFolderFromTemplate.fillInNameField("AName.");
        createFolderFromTemplate.clickSaveButton();
        createFolderFromTemplate.clickSaveButton();
        Assert.assertTrue(createFolderFromTemplate.isTooltipErrorMessageDisplayed(), "Tooltip error message not displayed");
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed(), "Create folder from template popup not displayed");

        LOG.info("STEP 5: Clear Name field and type 'AFolder.Name'. Click 'Save' button.");
        createFolderFromTemplate.fillInNameField(folderName);
        createFolderFromTemplate.clickSaveButton();
        //    Assert.assertEquals(notification.getDisplayedNotification(), String.format("Folder '%s' created", folderName));
        notification.waitUntilNotificationDisappears();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Subfolder not found");
        Assert.assertTrue(documentLibraryPage.getExplorerPanelDocuments().contains(folderName), "Subfolder not found in Documents explorer panel");

        LOG.info("STEP 6: Hover over the created folder (AFolder.Name). Click 'Manage Rules' option from more menu.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.manageRules"), manageRulesPage);
        Assert.assertTrue(manageRulesPage.isContentRuleDisplayed(), "Content rule not displayed");

        LOG.info("STEP 7: Click on 'Documents' link from breadcrumb.");
        manageRulesPage.returnTo("Documents");
        Assert.assertTrue(documentLibraryPage.isDocumentListDisplayed(), "Documents page not opened");

        LOG.info("STEP 8: Hover over the created folder (AFolder.Name). Click 'Manage Permissions' option from more menu.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);
        Assert.assertEquals(managePermissionsPage.getRole(userFirstName + " " + userLastName), userRole, "User role not set to Coordinator");

        LOG.info("STEP 9: Click on 'Documents' link from breadcrumb.");
        managePermissionsPage.returnTo("Documents");
        Assert.assertTrue(documentLibraryPage.isDocumentListDisplayed(), "Documents page not opened");

        LOG.info("STEP 10: Hover over the created folder (AFolder.Name). Click 'Manage Aspects' option from more menu.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList(aspectType), "Aspect is not added to 'Currently Selected' list");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }
}
