package org.alfresco.share.alfrescoContent.organizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.profile.MyProfileNavigation;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class RecoveringDeletedContentTests extends ContextAwareWebTest
{
    private final String firstName = "FirstName";
    private final String lastName = "LastName";
    private final String description = String.format("Description-%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "content of the file.";
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private HeaderMenuBar headerMenuBar;
    @Autowired
    private DeleteDialog deleteDialog;
    @Autowired
    private UserProfilePage userProfilePage;
    @Autowired
    private MyProfileNavigation myProfileNavigation;
    @Autowired
    private UserTrashcanPage userTrashcanPage;

    @TestRail (id = "C7570")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void recoverDeletedDocument()
    {
        String random = RandomData.getRandomAlphanumeric();
        String userName = "profileUser-C7570-" + random;
        String siteName = "siteName-C7570-" + random;
        String fileName1 = "docName1-C7570-" + random;
        String fileName2 = "docName2-C7570-" + random;
        String fileName3 = "docName3-C7570-" + random;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName1, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName2, fileContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName3, fileContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Select document items and delete them by selecting 'Delete' option from 'Selected Items...' menu");
        documentLibraryPage.clickCheckBox(fileName1);
        documentLibraryPage.clickCheckBox(fileName2);
        ArrayList<String> expectedSelectedContent1 = new ArrayList<>(Arrays.asList(fileName1, fileName2));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedSelectedContent1), expectedSelectedContent1.toString(), "Selected content=");
        ArrayList<String> expectedNotSelectedContent1 = new ArrayList<>(Collections.singletonList(fileName3));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedNotSelectedContent1), expectedNotSelectedContent1.toString(),
            "Not selected content=");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        deleteDialog.clickDelete();
        ArrayList<String> expectedDisplayedContent1 = new ArrayList<>(Collections.singletonList(fileName3));
        assertEquals(documentLibraryPage.getFilesList().toString(), expectedDisplayedContent1.toString(), "Displayed files in 'Documents' list=");

        LOG.info("STEP2: Open the user menu on the toolbar and click 'My Profile' then the 'Trashcan' tab.");
        userProfilePage.navigate(userName);
        assertEquals(userProfilePage.getPageTitle(), "Alfresco » User Profile Page", "Displayed page=");
        myProfileNavigation.clickTrashcan();
        assertEquals(userTrashcanPage.getPageTitle(), "Alfresco » User Trashcan", "Displayed page=");
        ArrayList<String> trashcanItems = new ArrayList<>(Arrays.asList(fileName2, fileName1));
        assertEquals(userTrashcanPage.getItemsNamesList(), trashcanItems.toString(), "Trashcan items=");
        assertEquals(userTrashcanPage.verifyItemButtons(), "ok", "Trashcan item's displayed buttons 'Recover' and 'Delete'.");
        assertEquals(userTrashcanPage.verifyItemDeleteInfo(), "ok", "Trashcan item's deletion info.");

        LOG.info("STEP3: Click 'Recover' button next to file name");
        userTrashcanPage.clickRecoverButton(fileName1);
        assertFalse(userTrashcanPage.getItemsNamesList().contains(fileName1), fileName1 + " isn't displayed in Trashcan.");
        assertTrue(userTrashcanPage.getItemsNamesList().contains(fileName2), fileName2 + " is displayed in Trashcan.");

        LOG.info("STEP4: Navigate to site's Document Library page");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName1), fileName1 + " is displayed in Document Library.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName3), fileName3 + " is displayed in Document Library.");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName2), fileName2 + " isn't displayed in Document Library.");

        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7571")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void recoverDeletedFolder()
    {
        String random = RandomData.getRandomAlphanumeric();
        String userName = "profileUser-C7571-" + random;
        String siteName = "siteName-C7571-" + random;
        String folderName1 = "folderName1-C7571-" + random;
        String folderName2 = "folderName2-C7571-" + random;
        String folderName3 = "folderName3-C7571-" + random;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName1, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);
        contentService.createFolder(userName, password, folderName3, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Select document items and delete them by selecting 'Delete' option from 'Selected Items...' menu");
        documentLibraryPage.clickCheckBox(folderName1);
        documentLibraryPage.clickCheckBox(folderName2);
        ArrayList<String> expectedSelectedContent1 = new ArrayList<>(Arrays.asList(folderName1, folderName2));
        assertEquals(documentLibraryPage.verifyContentItemsSelected(expectedSelectedContent1), expectedSelectedContent1.toString(), "Selected content=");
        ArrayList<String> expectedNotSelectedContent1 = new ArrayList<>(Collections.singletonList(folderName3));
        assertEquals(documentLibraryPage.verifyContentItemsNotSelected(expectedNotSelectedContent1), expectedNotSelectedContent1.toString(),
            "Not selected content=");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        deleteDialog.clickDelete();
        ArrayList<String> expectedDisplayedContent1 = new ArrayList<>(Collections.singletonList(folderName3));
        assertTrue(documentLibraryPage.getFoldersList().toString().contains(expectedDisplayedContent1.toString()), "Displayed files in 'Documents' list=");
        LOG.info("STEP2: Open the user menu on the toolbar and click 'My Profile' then the 'Trashcan' tab");
        userProfilePage.navigate(userName);
        assertEquals(userProfilePage.getPageTitle(), "Alfresco » User Profile Page", "Displayed page=");
        myProfileNavigation.clickTrashcan();
        assertEquals(userTrashcanPage.getPageTitle(), "Alfresco » User Trashcan", "Displayed page=");
        ArrayList<String> trashcanItems = new ArrayList<>(Arrays.asList(folderName2, folderName1));
        assertEquals(userTrashcanPage.getItemsNamesList(), trashcanItems.toString(), "Trashcan items=");
        assertEquals(userTrashcanPage.verifyItemButtons(), "ok", "Trashcan item's displayed buttons 'Recover' and 'Delete'.");
        assertEquals(userTrashcanPage.verifyItemDeleteInfo(), "ok", "Trashcan item's deletion info.");

        LOG.info("STEP3: Click 'Recover' button next to folder's name");
        userTrashcanPage.clickRecoverButton(folderName1);
        assertFalse(userTrashcanPage.getItemsNamesList().contains(folderName1), folderName1 + " isn't displayed in Trashcan.");
        assertTrue(userTrashcanPage.getItemsNamesList().contains(folderName2), folderName2 + " is displayed in Trashcan.");

        LOG.info("STEP4: Navigate to site's Document Library page");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        assertTrue(documentLibraryPage.getFoldersList().contains(folderName1), folderName1 + " is displayed in Document Library.");
        assertTrue(documentLibraryPage.getFoldersList().contains(folderName3), folderName3 + " is displayed in Document Library.");
        assertFalse(documentLibraryPage.getFoldersList().contains(folderName2), folderName2 + " isn't displayed in Document Library.");

        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }
}