package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.PreviewFileActionsSection;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class ViewingAFileOnGoogleMapsTest extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired
    PreviewFileActionsSection documentActions;
    
    @Autowired private ContentAspects contentAspects;

    private final String user = String.format("C5920User%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C5920SiteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C5920SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String docName = "File-C5920";
    private final String folderName = "testFolder";
    private final double longitude = 47.1585;
    private final double latitude = 27.6014;
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {

        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentAspects.addGeographicAspect(user, password, siteName, docName, longitude, latitude);
        setupAuthenticatedSession(user, password);
    }
    
    @TestRail (id="C5921")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    
    public void viewAFileOnGoogleMaps()
    {
        LOG.info("Step 1: Navigate to Document Library page");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageHeader(), siteName, "Document Library is not opened!");
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is not displayed!");
        
        LOG.info("Step 2: Click on folder and then hover a file that has the Geolocation Metadata available icon.");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isGeolocationMetadataIconDisplayed(), "Geolocation Metadata icon is not displayed");
        List<String> expectedActions = Arrays.asList("Download", "View In Browser", "Edit in Google Docs™", "View on Google Maps");
        Assert.assertTrue(documentLibraryPage.areActionsAvailableForLibraryItem(docName, expectedActions), "Expected actions");
        Assert.assertTrue(documentLibraryPage.isMoreMenuDisplayed(docName), "More menu is not displayed");
        
        LOG.info("Step 3: Click on View on Google Maps");
        documentLibraryPage.clickOnAction(docName, "View on Google Maps");
        Assert.assertTrue(documentLibraryPage.isFileOpenedInGoogleMaps(),"File is not opened in Google Maps");
        Assert.assertTrue(documentLibraryPage.isDocumentThumbnailDisplayedOnGoogleMaps(),"Document thumbnail is not displayed in Google Maps");
        
        LOG.info("Step 4: Click on file in Google Maps to open file preview");
        documentLibraryPage.clickOnFileInGoogleMaps();
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, "Document preview is not displayed when file is accessed form the Google Maps view");
    }
}
