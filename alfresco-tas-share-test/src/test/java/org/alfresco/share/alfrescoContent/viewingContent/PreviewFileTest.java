package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author iulia.cojocea
 */

public class PreviewFileTest extends ContextAwareWebTest
{
    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private DocumentLibraryPage documentLibraryPage;

    private final String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private final String siteName = "siteName" + DataUtil.getUniqueIdentifier();
    private final String folderName = "testFolder";
    private final String docName = "MultiPageDocument.docx";
    private final String testDataFolder = srcRoot + "testdata" + File.separator + "testDataC5884" + File.separator;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        contentService.createFolder(testUser, password, folderName, siteName);
        setupAuthenticatedSession(testUser, password);
        contentService.uploadFilesInFolder(testDataFolder, testUser, password, siteName, folderName);
    }

    @TestRail(id = "C5883")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void previewFile()
    {
        LOG.info("STEP 1: Navigate to 'Document Library' page for 'siteName'");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageHeader(), siteName, "Document Library is not opened!");
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is not displayed!");

        LOG.info("STEP 2: Click on folder name then on file name");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document is not displayed!");
        documentLibraryPage.clickOnFile(docName);
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, "Wrong file name!");

        LOG.info("STEP 3: Click 'Maximize' to view a larger preview");
        documentDetailsPage.clickOnMaximizeMinimizeButton();
        Assert.assertTrue(documentDetailsPage.getMinimizeMaximizeText().equals("Minimize"), "Minimize button is not displayed!");

        LOG.info("STEP 4: Scroll between testDoc pages");
        String actualPageNo = documentDetailsPage.getCurrentPageNo();
        documentDetailsPage.clickOnNextButton();
        String newPageNo = documentDetailsPage.getCurrentPageNo();
        Assert.assertFalse(actualPageNo.equals(newPageNo), "Page number should be different!");
        documentDetailsPage.clickOnPreviousButton();
        newPageNo = documentDetailsPage.getCurrentPageNo();
        Assert.assertTrue(newPageNo.equals(actualPageNo), "Page number should be different!");

        LOG.info("STEP 5: Click 'Zoom In' button, check that the Zoom level is saved for the next time you preview this item");
        String initialScaleValue = documentDetailsPage.getScaleValue();
        documentDetailsPage.clickOnZoomInButton();
        String newScaleValue = documentDetailsPage.getScaleValue();
        Assert.assertFalse(documentDetailsPage.getScaleValue().equals(initialScaleValue), "Scale value should be different!");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document is not displayed!");
        documentLibraryPage.clickOnFile(docName);
        Assert.assertEquals(documentDetailsPage.getScaleValue(), newScaleValue, "Wrong scale value! expected " + documentDetailsPage.getScaleValue()
                + "but found " + newScaleValue);
        documentDetailsPage.clickOnZoomOutButton();
        Assert.assertFalse(documentDetailsPage.getScaleValue().equals(newScaleValue), "Scale value should be different");

        LOG.info("STEP 6: Click 'Advanced Search' icon");
        documentDetailsPage.clickOnSearchButton();

        LOG.info("STEP 6: Go back to the folder content and click on the video");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("Tulips.jpg"), "Picture is not displayed!");
        documentLibraryPage.clickOnFile("Tulips.jpg");
        Assert.assertFalse(documentDetailsPage.isZoomOutButtonDisplayed(), "Zoom out button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isZoomInButtonDisplayed(), "Zoom in button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isMaximizetButtonDisplayed(), "Maximize button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isNextPageButton(), "Next page button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isPreviousPageButton(), "Previous page button should not be displayed!");

        LOG.info("STEP 7: Go back to the folder content and click on the image");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.getFilesList().contains("Tulips.jpg"), "Picture is not displayed!");
        documentLibraryPage.clickOnFile("Tulips.jpg");
        Assert.assertFalse(documentDetailsPage.isZoomOutButtonDisplayed(), "Zoom out button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isZoomInButtonDisplayed(), "Zoom in button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isMaximizetButtonDisplayed(), "Maximize button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isNextPageButton(), "Next page button should not be displayed!");
        Assert.assertFalse(documentDetailsPage.isPreviousPageButton(), "Previous page button should not be displayed!");
    }
}
