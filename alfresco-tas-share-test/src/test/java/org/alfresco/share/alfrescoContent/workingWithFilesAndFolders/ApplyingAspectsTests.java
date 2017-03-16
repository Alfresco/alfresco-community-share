package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApplyingAspectsTests extends ContextAwareWebTest
{

    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private AspectsForm aspectsForm;

    @Autowired private SiteDashboardPage siteDashboardPage;

    private String userName;
    private String siteName;
    private String fileName;
    private String fileContent;

    @BeforeMethod
    public void setupTest()
    {

        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        fileName = "testFile";
        fileContent = "testContent";

        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);

    }

    @TestRail(id = "C7109")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void verifyAspectsForm()

    {
        logger.info("Precondition: Navigate to Document Details page for the test file");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickDocumentLibrary();
        documentLibraryPage.clickOnFile(fileName);

        logger.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");

        documentDetailsPage.clickManageAspects();
        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel is not diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Chnages button is not displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");

    }

    @TestRail(id = "C7105")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void manageAspectsApplyChanges()

    {

        logger.info("Preconditions: Navigate to Document Details page for the test file");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickDocumentLibrary();
        documentLibraryPage.clickOnFile(fileName);

        logger.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage.clickManageAspects();

        logger.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addElement(0);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        logger.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();
        documentDetailsPage.clickManageAspects();
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

    }

}
