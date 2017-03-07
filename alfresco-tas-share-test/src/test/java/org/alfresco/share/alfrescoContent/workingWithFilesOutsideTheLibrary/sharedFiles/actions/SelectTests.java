package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class SelectTests extends ContextAwareWebTest
{
    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired
    DeleteDialog deleteDialog;

    String uniqueIdentifier = DataUtil.getUniqueIdentifier();
    String path = "Shared/";
    String user = "user" + uniqueIdentifier;
    String docName = "DocC8004-" + uniqueIdentifier;
    String folderName = "FolderC8005-" + uniqueIdentifier;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", "firstName", "lastName");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName, "");
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);

        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    }

    @TestRail(id = "C8004")
    @Test()
    public void selectFile()
    {
        LOG.info("STEP1: Click on Select -> Documents option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.documents"));
        assertTrue(sharedFilesPage.isContentSelected(docName), docName + " is selected.");

        LOG.info("STEP2: Click on Select menu -> Invert Selection option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.invertSelection"));
        assertFalse(sharedFilesPage.isContentSelected(docName), docName + " is selected.");

        LOG.info("STEP3: Click on Select menu -> All option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        assertTrue(sharedFilesPage.isContentSelected(docName), docName + " is selected.");

        LOG.info("STEP4: Click on Select menu -> None option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.none"));
        assertFalse(sharedFilesPage.isContentSelected(docName), docName + " is selected.");

        LOG.info("STEP5: Click on Select menu -> Folders option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.folders"));
        assertFalse(sharedFilesPage.isContentSelected(docName), docName + " is selected.");

        LOG.info("STEP6: Click on checkbox next to file");
        sharedFilesPage.clickCheckBox(docName);
        assertTrue(sharedFilesPage.isContentSelected(docName), docName + " is selected.");
    }

    @TestRail(id = "C8005")
    @Test()
    public void selectFolder()
    {
        LOG.info("STEP1: Click on Select -> Folders option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.folders"));
        assertTrue(sharedFilesPage.isContentSelected(folderName), folderName + " is selected.");

        LOG.info("STEP2: Click on Select menu -> Invert Selection option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.invertSelection"));
        assertFalse(sharedFilesPage.isContentSelected(folderName), folderName + " is selected.");

        LOG.info("STEP3: Click on Select menu -> All option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        assertTrue(sharedFilesPage.isContentSelected(folderName), folderName + " is selected.");

        LOG.info("STEP4: Click on Select menu -> None option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.none"));
        assertFalse(sharedFilesPage.isContentSelected(folderName), folderName + " is selected.");

        LOG.info("STEP5: Click on Select menu -> Documents option");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.documents"));
        assertFalse(sharedFilesPage.isContentSelected(folderName), folderName + " is selected.");

        LOG.info("STEP6: Click on checkbox next to folder");
        sharedFilesPage.clickCheckBox(folderName);
        assertTrue(sharedFilesPage.isContentSelected(folderName), folderName + " is selected.");
    }

    @AfterClass
    public void cleanUp()
    {
        content.deleteContentByPath(adminUser, adminPassword, path + docName);
        content.deleteContentByPath(adminUser, adminPassword, path + folderName);
    }
}