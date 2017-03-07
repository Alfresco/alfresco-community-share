package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SharedFilesManageAspectsTests extends ContextAwareWebTest
{

    @Autowired
    AspectsForm aspectsForm;

    @Autowired
    SharedFilesPage sharedFilesPage;

    private String userName;
    private String folderName;
    private String path;
    private String userName1;
    private String folderPath;

    @BeforeMethod
    public void setupTest()
    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        userName1 = "User1" + DataUtil.getUniqueIdentifier();
        folderName = "testFolder" + DataUtil.getUniqueIdentifier();
        path = "Shared";
        folderPath = "/Shared/" + folderName;
        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        userService.create(adminUser, adminPassword, userName1, password, "@tests.com", userName1, userName1);
        contentService.createFolderInRepository(userName, password, folderName, path);
    }

    @TestRail(id = "C8038")
    @Test
    public void verifyManageAspectActions()

    {
        logger.info("Preconditions: Login to Share and navigate to 'Shared Files' page");
        setupAuthenticatedSession(userName, password);
        sharedFilesPage.navigate();
        browser.waitInSeconds(2);

        logger.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects Form");
        sharedFilesPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        assertTrue(aspectsForm.isCurrentlySelectedtPanel(), "Currently Selected panel is not diaplyed");
        assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Chnages button is not displayed");
        assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");
    }

    @TestRail(id = "C8034")
    @Test
    public void manageAspectsApplyChanges()

    {
        logger.info("Preconditions: Login to Share and navigate to 'Shared Files' page");
        setupAuthenticatedSession(userName, password);
        sharedFilesPage.navigate();

        logger.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        sharedFilesPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        logger.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addElement(0);
        browser.waitInSeconds(1);
        assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        logger.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton();
        browser.waitInSeconds(1);
        browser.refresh();
        sharedFilesPage.renderedPage();
        sharedFilesPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

    }

    @TestRail(id = "C13761")
    @Test
    public void manageAspectsActionMissing()
    {
        logger.info("Preconditions: Login to Share and navigate to 'Shared Files' page");
        setupAuthenticatedSession(userName1, password);
        sharedFilesPage.navigate();
        browser.waitInSeconds(2);

        logger.info("Step1: Hover over the folder created by other user and verify 'Manage Aspects' action is missing");
        sharedFilesPage.mouseOverContentItem(folderName);
        browser.waitInSeconds(1);
        Assert.assertFalse(sharedFilesPage.isMoreMenuDisplayed(folderName), "'More' menu not displayed for " + folderName);
        Assert.assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(folderName, "Manage Aspects"));

    }

    @AfterMethod
    public void deleteContent()

    {
        contentService.deleteContentByPath(userName, password, folderPath);
        userService.delete(adminUser, adminPassword, userName);
        userService.delete(adminUser, adminPassword, userName1);
    }
}
