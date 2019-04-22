package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.po.share.alfrescoContent.RepositoryPage;
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

public class ActionsManageAspectsTests extends ContextAwareWebTest
{
    @Autowired private RepositoryPage repositoryPage;

    @Autowired private AspectsForm aspectsForm;

    private String nonAdminUser;
    private String folderInRepoUserHomes;
    private String folderInRepoMainPath;
    private String repositoryUserHomesPath;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        nonAdminUser = String.format("nonAdminUser%s", RandomData.getRandomAlphanumeric());
        folderInRepoMainPath = String.format("testFolderInRepoMainPath%s", RandomData.getRandomAlphanumeric());
        folderInRepoUserHomes = String.format("folderInRepoUserHomes%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, nonAdminUser, password, nonAdminUser + domain, nonAdminUser, nonAdminUser);
        contentService.createFolderInRepository(adminUser, adminPassword, folderInRepoMainPath, "/");
        contentService.createFolderInRepository(nonAdminUser, password, folderInRepoUserHomes, "User Homes/" + nonAdminUser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, nonAdminUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, folderInRepoMainPath);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + nonAdminUser);

    }

    @TestRail(id = "C8254")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void checkAspectsForm()
    {
        setupAuthenticatedSession(nonAdminUser, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(nonAdminUser);

        LOG.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        repositoryPage.clickDocumentLibraryItemAction(folderInRepoUserHomes, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel is not diaplyed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Chnages button is not displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");
    }

    @TestRail(id = "C8250")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void manageAspectsApplyChanges()
    {
        setupAuthenticatedSession(nonAdminUser, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(nonAdminUser);

        LOG.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage.clickDocumentLibraryItemAction(folderInRepoUserHomes, "Manage Aspects", aspectsForm);

        LOG.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton(repositoryPage);
        repositoryPage.clickDocumentLibraryItemAction(folderInRepoUserHomes, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

    }

    @TestRail(id = "C13763")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void manageAspectsForNonAdminOnRepositoryMainFolder()
    {
        setupAuthenticatedSession(nonAdminUser, password);
        repositoryPage.navigate();
        LOG.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage.mouseOverContentItem(folderInRepoMainPath);
        Assert.assertEquals(repositoryPage.isMoreMenuDisplayed(folderInRepoMainPath), false);
        Assert.assertEquals(repositoryPage.isActionAvailableForLibraryItem(folderInRepoMainPath, "Manage Aspects"), false);
    }

    @TestRail(id = "C13764")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void manageAspectsAdminOnRepositoryMainFolder()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();

        LOG.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage.mouseOverContentItem(folderInRepoMainPath);
        Assert.assertEquals(repositoryPage.isMoreMenuDisplayed(folderInRepoMainPath), true);
        Assert.assertEquals(repositoryPage.isActionAvailableForLibraryItem(folderInRepoMainPath, "Manage Aspects"), true);

    }
}
