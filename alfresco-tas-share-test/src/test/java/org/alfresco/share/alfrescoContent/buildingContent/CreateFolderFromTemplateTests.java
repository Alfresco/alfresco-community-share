package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.Test;


public class CreateFolderFromTemplateTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;
    
    @Autowired
    CreateFolderFromTemplate createFolderFromTemplate;

    String folderTemplateName = "Software Engineering Project";
    String userFirstName = "Jim";
    String userLastName = "Jones";

    @TestRail(id = "C6292")
    @Test
    public void createFolderFromTemplate()
    {
        String userName = "testUser" + DataUtil.getUniqueIdentifier();
        String siteName = "testSite" + DataUtil.getUniqueIdentifier();
        String fileName = "system-overview.html";
        String breadcrumbPath = "Documents > Software Engineering Project > Documentation > Samples";

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C6292", "C6292");
        siteService.create(userName, password, domain, siteName, siteName, Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibrary();

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        createFolderFromTemplate.clickCreateContentButton();
        createFolderFromTemplate.hoverOverCreateFolderFromTemplateLink();
        Assert.assertTrue(createFolderFromTemplate.isListOfAvailableTemplatesDisplayed());

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createFolderFromTemplate.clickOnFolderTemplate(folderTemplateName);
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getTemplateFolderNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Click 'Save' button.");
        createFolderFromTemplate.clickButton("Save");
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(createFolderFromTemplate.isFolderCreatedMessageDisplayed());
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists(folderTemplateName), "Subfolder not found");
        Assert.assertEquals(createFolderFromTemplate.getFolderNameFromExplorerPanel(), folderTemplateName);

        LOG.info("STEP 4: Click on the created folder ('Software Engineering Project') in 'Library' section of the browsing pane.");
        documentLibraryPage.clickOnFolderName(folderTemplateName);
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Discussions"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Documentation"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Presentations"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Quality Assurance"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("UI Design"), "Subfolder not found");

        LOG.info("STEP 5: Click on the subfolder 'Documentation' in 'Library' section of the browsing pane.");
        documentLibraryPage.clickOnFolderName("Documentation");
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Drafts"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Pending Approval"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Published"), "Subfolder not found");
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Samples"), "Subfolder not found");

        LOG.info("STEP 6: Click on the subfolder 'Samples' in 'Library' section of the browsing pane.");
        documentLibraryPage.clickOnFolderName("Samples");
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(createFolderFromTemplate.getBreadCrumbPath(), breadcrumbPath);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "File not found");
    }

    @TestRail(id = "C6293")
    @Test
    public void cancelCreatingFolderFromTemplate()
    {
        String userName = "testUser" + DataUtil.getUniqueIdentifier();
        String siteName = "testSite" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "C6292", "C6292");
        siteService.create(userName, password, domain, siteName, siteName, Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibrary();

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        createFolderFromTemplate.clickCreateContentButton();
        createFolderFromTemplate.hoverOverCreateFolderFromTemplateLink();
        Assert.assertTrue(createFolderFromTemplate.isListOfAvailableTemplatesDisplayed());

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createFolderFromTemplate.clickOnFolderTemplate(folderTemplateName);
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getTemplateFolderNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Click 'Cancel' button.");
        createFolderFromTemplate.clickButton("Cancel");
        Assert.assertFalse(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(folderTemplateName), "Folder not found");
    }
    
    @TestRail(id = "C8139")
    @Test
    public void createFolderFromTemplateUsingWildcards()
    {
        String userName = "testUser" + DataUtil.getUniqueIdentifier();
        String userRole = "Coordinator";
        String siteName = "testSite" + DataUtil.getUniqueIdentifier();
        String templateFolderName = "template1"+ DataUtil.getUniqueIdentifier();
        String folderName = "AFolder.Name";
        String illegalCharacters = "\'* \" < > \\ / . ? : |'";
        String folderPathInRepository = "Data Dictionary/Space Templates/";
        String ruleName = "Add aspect taggable rule";
        String ruleDescription = "Add aspect taggable rule test";
        String addAspectActionValue = "add-features";        
        String aspectTaggableValue = "cm:taggable";
        String mainButtonToClick = "Create";
        String aspectType = "Classifiable";
        
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userFirstName, userLastName);
        contentService.createFolderInRepository(adminUser, adminPassword, templateFolderName,folderPathInRepository);        
        setupAuthenticatedSession(adminUser, adminPassword);
        createFolderFromTemplate.clickCreateRules(templateFolderName);
        getBrowser().waitInSeconds(2);
        createFolderFromTemplate.insertRuleName(ruleName);
        getBrowser().waitInSeconds(1);
        createFolderFromTemplate.insertRuleDescription(ruleDescription);
        createFolderFromTemplate.selectAspectAndSubmit(addAspectActionValue,aspectTaggableValue,mainButtonToClick);
        createFolderFromTemplate.setPermissions(templateFolderName,userName);
        getBrowser().waitInSeconds(4);
        createFolderFromTemplate.setAspects(templateFolderName);    
        cleanupAuthenticatedSession();
        siteService.create(userName, password, domain, siteName, siteName, Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
       
       
        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        createFolderFromTemplate.clickCreateContentButton();
        createFolderFromTemplate.hoverOverCreateFolderFromTemplateLink();
        Assert.assertTrue(createFolderFromTemplate.isListOfAvailableTemplatesDisplayed(),"List of templates not displayed");
        
        LOG.info("STEP 2: Select the template: 'template1'");
        createFolderFromTemplate.clickOnFolderTemplate(templateFolderName);
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed(),"Create folder from template popup not displayed");
        Assert.assertEquals(createFolderFromTemplate.getTemplateFolderNameFieldValue(), templateFolderName);
        
        LOG.info("STEP 3: Clear the Name field. Add name \'* \" < > \\ / . ? : |' and click 'Save' button.");
        createFolderFromTemplate.insertNameInput(illegalCharacters);
        createFolderFromTemplate.clickButton("Save");
        createFolderFromTemplate.clickButton("Save");
        Assert.assertTrue(createFolderFromTemplate.isTooltipErrorMessageDisplayed(),"Tooltip error message not displayed");
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        
        LOG.info("STEP 4: Clear Name field and type 'AName.'. Click 'Save' button.");
        createFolderFromTemplate.insertNameInput("AName.");
        createFolderFromTemplate.clickButton("Save");
        createFolderFromTemplate.clickButton("Save");
        Assert.assertTrue(createFolderFromTemplate.isTooltipErrorMessageDisplayed(),"Tooltip error message not displayed");
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed(),"Create folder from template popup not displayed");
               
        LOG.info("STEP 5: Clear Name field and type 'AFolder.Name'. Click 'Save' button.");
        createFolderFromTemplate.insertNameInput(folderName);
        createFolderFromTemplate.clickButton("Save");
        Assert.assertTrue(createFolderFromTemplate.isFolderCreatedMessageDisplayed());
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("AFolder.Name"), "Subfolder not found");
        Assert.assertEquals(createFolderFromTemplate.getFolderNameFromExplorerPanel(), "AFolder.Name");
        
        LOG.info("STEP 6: Hover over the created folder (AFolder.Name). Click 'Manage Rules' option from more menu.");
        createFolderFromTemplate.clickManageRulesForAFolder(folderName);
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(createFolderFromTemplate.isContentRuleDisplayed(),"Content rule not displayed");

        LOG.info("STEP 7: Click on 'Documents' link from breadcrumb.");
        createFolderFromTemplate.returnToDocumentsPage();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(documentLibraryPage.isDocumentListDisplayed(),"Documents page not opened");
        
        LOG.info("STEP 8: Hover over the created folder (AFolder.Name). Click 'Manage Permissions' option from more menu.");
        createFolderFromTemplate.clickManagePermissionsDocumentLibrary(folderName);
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(createFolderFromTemplate.getPermissionsListUserDisplayName().contains(userFirstName),"User not found");
        Assert.assertTrue(createFolderFromTemplate.getPermissionsListUserDisplayName().contains(userLastName),"User not found");
        Assert.assertTrue(createFolderFromTemplate.getPermissionsListUserRole().contains(userRole),"User role not set to Coordinator");
        
        LOG.info("STEP 9: Click on 'Documents' link from breadcrumb.");
        createFolderFromTemplate.returnToDocumentsPage();
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(documentLibraryPage.isDocumentListDisplayed(),"Documents page not opened");
        
        LOG.info("STEP 10: Hover over the created folder (AFolder.Name). Click 'Manage Aspects' option from more menu.");
        createFolderFromTemplate.clickManageAspectsDocumentLibrary(folderName);
        getBrowser().waitInSeconds(3);
        Assert.assertTrue(createFolderFromTemplate.getAspectsForFileName().contains(folderName),"Folder not found");
        Assert.assertTrue(createFolderFromTemplate.getCurrentlySelectedAspectsForFileName().contains(aspectType),"Aspect not found");
        
        //contentService.deleteTreeByPath(adminUser, adminPassword, folderPathInRepository + templateFolderName);
    }
}
