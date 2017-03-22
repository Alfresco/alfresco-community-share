package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 9/13/2016.
 */
public class UnzippingContentTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private CopyMoveUnzipToDialog unzipToDialog;

    @Autowired private SiteDashboardPage siteDashboardPage;

    @Autowired private SiteContentDashlet siteContentDashlet;

    private final String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private final String siteName = "siteName" + DataUtil.getUniqueIdentifier();
    private final String zipFile = "archiveC7409.zip";
    private final String fileName = "fileC7409";
    private final String acpFile = "archiveC7410.acp";
    private final String fileName1 = "fileC7410";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        contentService.uploadFileInSite(testUser, password, siteName, testDataFolder + zipFile);
        contentService.uploadFileInSite(testUser, password, siteName, testDataFolder + acpFile);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C7409")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void unzipZipFileToDocumentLibraryOfTheSameSite()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        documentLibraryPage.clickOnFile(zipFile);
        assertTrue(documentDetailsPage.getFileName().equals(zipFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + zipFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select the site where archive will be unzipped, e.g: testSite.\n" +
                "Select the folder where archive will be unzipped, e.g: Documents");
        unzipToDialog.clickSite(siteName);
//        assertEquals(unzipToDialog.getPathList(), "[Documents]", "Path section is updated according to the path of the selected site, e.g: Documents");

        LOG.info("STEP4: Click 'Unzip' button and navigate to the previoulsy set folder from site's Document Library, e.g: Documents from testSite");
        unzipToDialog.clickButtton("Unzip");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(zipFile.substring(0, zipFile.indexOf("."))), "A folder with archive name is present in Documents list.");

        LOG.info("STEP5: Navigate to site dashboard and verify Site Content dashlet");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteContentDashlet.isFileLinkPresent(fileName), "Content of unzipped archive is displayed, e.g: testFile");
        assertTrue(siteContentDashlet.getDocDetails(fileName).contains(siteName), "Timestamp and site name are the ones set when folder was unzipped");
    }

    @TestRail(id = "C7410")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void unzipAcpFileToDocumentLibraryOfTheSameSite()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Click archive name, e.g: testArchive");
        documentLibraryPage.clickOnFile(acpFile);
        assertTrue(documentDetailsPage.getFileName().equals(acpFile), "Wrong file name!");

        LOG.info("STEP2: Click 'Unzip to...' link from 'Documents Actions'");
        documentDetailsPage.clickDocumentActionsOption("Unzip to...");
        assertEquals(unzipToDialog.getDialogTitle(), "Unzip " + acpFile + " to...", "'Unzip to....' dialog is displayed");

        LOG.info("STEP3: Select the site where archive will be unzipped, e.g: testSite.\n" +
                "Select the folder where archive will be unzipped, e.g: Documents");
        unzipToDialog.clickSite(siteName);
        assertEquals(unzipToDialog.getPathList(), "[Documents]", "Path section is updated according to the path of the selected site, e.g: Documents");

        LOG.info("STEP4: Click 'Unzip' button and navigate to the previously set folder from site's Document Library, e.g: Documents from testSite");
        unzipToDialog.clickButtton("Unzip");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(acpFile.substring(0, acpFile.indexOf("."))), "A folder with archive name is present in Documents list.");

        LOG.info("STEP5: Navigate to site dashboard and verify Site Content dashlet");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteContentDashlet.isFileLinkPresent(fileName1), "Content of unzipped archive is displayed, e.g: testFile");
        assertTrue(siteContentDashlet.getDocDetails(fileName1).contains(siteName), "Timestamp and site name are the ones set when folder was unzipped");
    }
}
