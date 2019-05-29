package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.BecomeContentOwnerDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class BecomeContentOwnerTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private BecomeContentOwnerDialog becomeContentOwnerDialog;

    private final String firstName = "FirstName";
    private final String lastName = "LastName";
    private final String description = String.format("Description-%s", RandomData.getRandomAlphanumeric());
    private final String docContent = "content of the file.";

    @TestRail (id = "C7152")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void becomeFileOwner()
    {
        String random = RandomData.getRandomAlphanumeric();
        String userSiteManager = "user-SiteManager-C7152-" + random;
        String userManager = "userManager-C7152-" + random;
        String userContributor = "userContributor-C7152-" + random;
        String docName = "Doc-C7152-" + random;
        String siteName = "Site-C7152-" + random;

        userService.create(adminUser, adminPassword, userSiteManager, password, userSiteManager + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, userManager, password, userSiteManager + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, userContributor, password, userSiteManager + domain, firstName, lastName);
        siteService.create(userSiteManager, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userSiteManager, password, userManager, siteName, "SiteManager");
        userService.createSiteMember(userSiteManager, password, userContributor, siteName, "SiteContributor");
        contentService.createDocument(userContributor, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userManager, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFile(docName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("STEP1: From 'Document Actions' section, click 'Become Owner' option");
        documentDetailsPage.clickDocumentActionsOption("Become Owner");
        assertTrue(becomeContentOwnerDialog.isDialogDisplayed(), "'Become Owner' dialog is displayed.");
        assertEquals(becomeContentOwnerDialog.getHeader(), language.translate("becomeContentOwner.header"), "'Become Owner' dialog header=");
        assertEquals(becomeContentOwnerDialog.getMessage(), String.format(language.translate("becomeContentOwner.message"), docName),
            "'Become Owner' dialog message:");

        LOG.info("STEP2: Click 'OK' button");
        becomeContentOwnerDialog.clickButton("OK");
        assertTrue(documentDetailsPage.isPropertyDisplayed("Owner:"), "'Owner' property is displayed.");
        assertEquals(documentDetailsPage.getPropertyValue("Owner:"), userManager, "'Owner' property value=");

        LOG.info("STEP3: Logout and login as userContributor. Navigate to site's Document Library page");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP4: Mouse over file's name link");
        documentLibraryPage.mouseOverContentItem(docName);
        assertFalse(documentLibraryPage.isMoreMenuDisplayed(docName), docName + " - 'More' menu is displayed.");

        cleanupAuthenticatedSession();

        userService.delete(adminUser, adminPassword, userSiteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userSiteManager);
        userService.delete(adminUser, adminPassword, userManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager);
        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7153")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void becomeFolderOwner()
    {
        String random = RandomData.getRandomAlphanumeric();
        String userSiteManager = "user-SiteManager-C7153-" + random;
        String userManager = "userManager-C7153-" + random;
        String userContributor = "userContributor-C7153-" + random;
        String folderName = "Folder-C7153-" + random;
        String siteName = "Site-C7153-" + random;
        userService.create(adminUser, adminPassword, userSiteManager, password, userSiteManager + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, userManager, password, userSiteManager + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, userContributor, password, userSiteManager + domain, firstName, lastName);
        siteService.create(userSiteManager, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userSiteManager, password, userManager, siteName, "SiteManager");
        userService.createSiteMember(userSiteManager, password, userContributor, siteName, "SiteContributor");
        contentService.createFolder(userContributor, password, folderName, siteName);

        setupAuthenticatedSession(userManager, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP1: Hover over 'Folder1' folder from 'Documents' list, then select 'View Details' option");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");
        assertEquals(documentDetailsPage.getPropertyValue("Modifier:"), userContributor, "'Modifier' property value=");

        LOG.info("STEP2: From 'Document Actions' section, click 'Become Owner' option");
        documentDetailsPage.clickDocumentActionsOption("Become Owner");
        assertTrue(becomeContentOwnerDialog.isDialogDisplayed(), "'Become Owner' dialog is displayed.");
        assertEquals(becomeContentOwnerDialog.getHeader(), language.translate("becomeContentOwner.header"), "'Become Owner' dialog header:");
        assertEquals(becomeContentOwnerDialog.getMessage(), String.format(language.translate("becomeContentOwner.message"), folderName),
            "'Become Owner' dialog message:");

        LOG.info("STEP3: Click 'OK' button");
        becomeContentOwnerDialog.clickButton("OK");
        assertEquals(documentDetailsPage.getPropertyValue("Modifier:"), userManager, "'Modifier' property value is updated to=");

        LOG.info("STEP4: Logout and login as userContributor. Navigate to site's Document Library page");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP5: Mouse over folder name");
        documentLibraryPage.mouseOverContentItem(folderName);
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Edit properties"), "'Edit properties' option is displayed for " + folderName);
        assertFalse(documentLibraryPage.isMoreMenuDisplayed(folderName), folderName + " - 'More' menu is displayed.");

        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, userSiteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userSiteManager);
        userService.delete(adminUser, adminPassword, userManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager);
        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7154")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelBecomeOwner()
    {
        String random = RandomData.getRandomAlphanumeric();
        String userSiteManager = "user-SiteManager-C7154-" + random;
        String userManager = "userManager-C7154-" + random;
        String userContributor = "userContributor-C7154-" + random;
        String siteName = "Site-C7154-" + random;
        String docName = "Doc-C7154-" + random;
        userService.create(adminUser, adminPassword, userSiteManager, password, userSiteManager + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, userManager, password, userSiteManager + domain, firstName, lastName);
        userService.create(adminUser, adminPassword, userContributor, password, userSiteManager + domain, firstName, lastName);
        siteService.create(userSiteManager, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userSiteManager, password, userManager, siteName, "SiteManager");
        userService.createSiteMember(userSiteManager, password, userContributor, siteName, "SiteContributor");
        contentService.createDocument(userContributor, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userManager, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        documentLibraryPage.clickOnFile(docName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("STEP1: From 'Document Actions' section, click 'Become Owner' option");
        documentDetailsPage.clickDocumentActionsOption("Become Owner");
        assertTrue(becomeContentOwnerDialog.isDialogDisplayed(), "'Become Owner' dialog is displayed.");
        assertEquals(becomeContentOwnerDialog.getHeader(), language.translate("becomeContentOwner.header"), "'Become Owner' dialog header=");
        assertEquals(becomeContentOwnerDialog.getMessage(), String.format(language.translate("becomeContentOwner.message"), docName),
            "'Become Owner' dialog message:");

        LOG.info("STEP2: Click 'Cancel' button");
        becomeContentOwnerDialog.clickButton("Cancel");
        assertFalse(becomeContentOwnerDialog.isDialogDisplayed(), "'Become Owner' dialog is displayed.");
        assertFalse(documentDetailsPage.isPropertyDisplayed("Owner:"), "'Owner' property is displayed.");

        LOG.info("STEP3: Logout and login as userContributor. Navigate to site's Document Library page");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userContributor, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("STEP4: Mouse over file's name link");
        documentLibraryPage.mouseOverContentItem(docName);
        assertTrue(documentLibraryPage.isMoreMenuDisplayed(docName), docName + " - 'More' menu is displayed. ");

        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, userSiteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userSiteManager);
        userService.delete(adminUser, adminPassword, userManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager);
        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);
        siteService.delete(adminUser, adminPassword, siteName);
    }


}