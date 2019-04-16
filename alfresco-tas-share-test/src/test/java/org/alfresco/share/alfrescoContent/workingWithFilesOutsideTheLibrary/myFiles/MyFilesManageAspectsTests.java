package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyFilesManageAspectsTests extends ContextAwareWebTest
{
    @Autowired private AspectsForm aspectsForm;

    @Autowired private MyFilesPage myFilesPage;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String folderName = String.format("testFolder%s", RandomData.getRandomAlphanumeric());
    private String path = "User Homes/" + userName;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        contentService.createFolderInRepository(userName, password, folderName, path);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

    }

    @TestRail(id = "C7814")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void checkManageAspectsForm()
    {
        myFilesPage.navigate();
        LOG.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        myFilesPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel is not diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Chnages button is not displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");

    }

    @TestRail(id = "C7810")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void manageAspectsApplyChanges()
    {
        myFilesPage.navigate();
        LOG.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        myFilesPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        LOG.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton(myFilesPage);
        myFilesPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");
    }

}
