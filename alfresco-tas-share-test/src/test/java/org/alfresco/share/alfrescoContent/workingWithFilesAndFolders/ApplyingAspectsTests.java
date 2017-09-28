package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ApplyingAspectsTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private AspectsForm aspectsForm;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String fileName = String.format("testFile%s", RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, "testContent");
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C7109")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void checkAspectsForm()
    {
        LOG.info("Precondition: Navigate to Document Details page for the test file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);

        LOG.info("Step1: Click Actions -> Manage Aspects and verify Manage Aspects form");

        documentDetailsPage.clickManageAspects();
        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel is not diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Chnages button is not displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");

    }

    @TestRail(id = "C7105")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void manageAspectsApplyChanges()
    {
    LOG.info("Preconditions: Navigate to Document Details page for the test file");
    documentLibraryPage.navigate(siteName);
    documentLibraryPage.clickOnFile(fileName);

    LOG.info("Step1: Click Actions -> Manage Aspects option");
    documentDetailsPage.clickManageAspects();

    LOG.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
    aspectsForm.addAspect("Audio");
    Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Audio"), "Aspect is not added to 'Currently Selected' list");
    Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Audio"), "Aspect is present on 'Available to Add' list");

    LOG.info("Step3: Click 'Apply Changes' and verify the aspect is added");
    aspectsForm.clickApplyChangesButton(documentDetailsPage);
    Assert.assertTrue(documentDetailsPage.isAspectDisplayed("Audio"), "Audio aspect is added");
    documentDetailsPage.clickManageAspects();
    Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Audio"), "Aspect is not added to 'Currently Selected' list");
    Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Audio"), "Aspect is present on 'Available to Add' list");
}
}
