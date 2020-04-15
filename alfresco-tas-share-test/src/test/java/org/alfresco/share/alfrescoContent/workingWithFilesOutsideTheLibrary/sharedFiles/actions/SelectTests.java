package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class SelectTests extends ContextAwareWebTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String path = "Shared/";
    private final String user = "user" + uniqueIdentifier;
    private final String docName = "DocC8004-" + uniqueIdentifier;
    private final String folderName = "FolderC8005-" + uniqueIdentifier;
    @Autowired
    DeleteDialog deleteDialog;
    @Autowired
    private SharedFilesPage sharedFilesPage;
    @Autowired
    private HeaderMenuBar headerMenuBar;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName, "");
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);

        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    }

    @TestRail (id = "C8004")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
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

    @TestRail (id = "C8005")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
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
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);


        contentService.deleteContentByPath(adminUser, adminPassword, path + docName);
        contentService.deleteContentByPath(adminUser, adminPassword, path + folderName);
    }
}