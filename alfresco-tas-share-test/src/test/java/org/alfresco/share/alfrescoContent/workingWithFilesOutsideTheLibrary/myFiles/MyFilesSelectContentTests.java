package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesSelectContentTests extends ContextAwareWebTest
{
    @Autowired private MyFilesPage myFilesPage;

    @Autowired private HeaderMenuBar headerMenuBar;

    @Autowired private NewContentDialog newContentDialog;

    @Autowired private SiteDashboardPage sitePage;
    
    @Autowired
    private UploadContent uploadContent;

    private final String testFile =  DataUtil.getUniqueIdentifier() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String folderName = "testFolder" + DataUtil.getUniqueIdentifier();

    @TestRail(id = "C7682")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void selectFileByMenu()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(testFilePath);

        LOG.info("STEP1: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Documents");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(testFile));
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is disabled.");

        LOG.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Invert Selection");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("All");
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is disabled.");

        LOG.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("None");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP5: Click 'Select' button and choose 'Folders'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Folders");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP6: Click on document checkbox");
        myFilesPage.clickCheckBox(testFile);
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");
    }

    @TestRail(id = "C7683")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void selectFolderByMenu()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("STEP1: Click 'Select' button and choose 'Folders'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Folders");
        ArrayList<String> expectedContentList1 = new ArrayList<>(Collections.singletonList(folderName));
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is disabled.");

        LOG.info("STEP2: Click 'Select' button and choose 'Invert Selection' option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Invert Selection");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP3: Click 'Select' button and choose 'All'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("All");
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is disabled.");

        LOG.info("STEP4: Click 'Select' button and choose 'None'");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("None");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP5: Click 'Select' button and choose 'Documents' option.");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("Documents");
        assertEquals(myFilesPage.verifyContentItemsNotSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        Assert.assertFalse(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");

        LOG.info("STEP6: Click on folder checkbox");
        myFilesPage.clickCheckBox(folderName);
        assertEquals(myFilesPage.verifyContentItemsSelected(expectedContentList1), expectedContentList1.toString(), "Selected content = ");
        assertTrue(headerMenuBar.isSelectedItemsMenuDisabled(), "'Selected Items...' menu is enabled.");
    }
}
