package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyFilesManageAspectsTests extends ContextAwareWebTest
{

    @Autowired private AspectsForm aspectsForm;

    @Autowired private MyFilesPage myFilesPage;

    @Autowired private MyFilesPage myFiles;

    private String userName;
    private String folderName;
    private String path;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {

        userName = "User" + DataUtil.getUniqueIdentifier();
        folderName = "testFolder" + DataUtil.getUniqueIdentifier();
        path = "User Homes/" + userName;

        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        contentService.createFolderInRepository(userName, password, folderName, path);
        setupAuthenticatedSession(userName, password);
        myFilesPage.navigate();

    }

    @TestRail(id = "C7814")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void verifyManageAspectsForm()
    {
        logger.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        myFiles.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel is not diaplyed");
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
        logger.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        myFiles.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        logger.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addElement(0);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        logger.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();
        myFiles.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

    }

}
