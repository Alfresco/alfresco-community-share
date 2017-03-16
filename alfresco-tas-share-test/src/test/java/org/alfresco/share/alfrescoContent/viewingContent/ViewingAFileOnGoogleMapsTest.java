package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.PreviewFileActionsSection;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ViewingAFileOnGoogleMapsTest extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired
    PreviewFileActionsSection documentActions;
    
    @Autowired private ContentAspects contentAspects;

    private final String user = "C5920User" + DataUtil.getUniqueIdentifier();
    private final String siteName = "C5920SiteName" + DataUtil.getUniqueIdentifier();
    private final String description = "C5920SiteDescription" + DataUtil.getUniqueIdentifier();
    private final String docName = "File-C5920";
    private final String folderName = "testFolder";
    private final double longitude = 47.1585;
    private final double latitude = 27.6014;
    
    @BeforeClass
    public void setupTest()
    {

        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentAspects.addGeographicAspect(user, password, siteName, docName, longitude, latitude);
        setupAuthenticatedSession(user, password);
    }
    
    @TestRail (id="C5921")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    
    public void viewAFileOnGoogleMaps()
    {
        LOG.info("Step 1: Navigate to Document Library page");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageHeader(), siteName, "Document Library is not opened!");
        Assert.assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is not displayed!");
        
        LOG.info("Step 2: Click on folder and then hover a file that has the Geolocation Metadata available icon.");
        documentLibraryPage.clickOnFolderName(folderName);
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(documentLibraryPage.isGeolocationMetadataIconDisplayed(), "Geolocation Metadata icon is not displayed");
        documentLibraryPage.mouseOverFileName(docName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Edit in Google Docs™"),
                "Edit in Google Docs™ is not available for file");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Download"), "Download is not available for test document");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "View In Browser"),
                "View In Browser is not available for test document");
        Assert.assertTrue(documentLibraryPage.isMoreMenuDisplayed(docName), "More menu is not displayed");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "View on Google Maps"),
                "View on Google Maps is not available for file");
        
        LOG.info("Step 3: Click on View on Google Maps");
        documentLibraryPage.clickOnAction(docName, "View on Google Maps");
        Assert.assertTrue(documentLibraryPage.isFileOpenedInGoogleMaps(),"File is not opened in Google Maps");
        Assert.assertTrue(documentLibraryPage.isDocumentThumbnailDisplayedOnGoogleMaps(),"Document thumbnail is not displayed in Google Maps");
        
        LOG.info("Step 4: Click on file in Google Maps to open file preview");
        documentLibraryPage.clickOnFileInGoogleMaps();
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, "Document preview is not displayed when file is accessed form the Google Maps view");
    }
}
