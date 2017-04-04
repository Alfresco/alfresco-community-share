package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author iulia.cojocea
 */
public class ViewFileInfoAndOptionsTest extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    private final String testUser = String.format("testUser%s", DataUtil.getUniqueIdentifier());
    private final String siteName = String.format("siteName%s", DataUtil.getUniqueIdentifier());
    private final String folderName = "testFolder";
    private final String docName = String.format("testDoc%s", DataUtil.getUniqueIdentifier());
    private final DateTime currentDate = new DateTime();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Visibility.PUBLIC);
        contentService.createFolder(testUser, password, folderName, siteName);
        contentService.createDocumentInFolder(testUser, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName, "Document content");
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C5883")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void viewFileInfoAndOptions()
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
        Assert.assertEquals(documentDetailsPage.getFileVersion(), "1.0", "Wrong file version!");
        Assert.assertEquals(documentDetailsPage.getItemModifier(), "firstName lastName", "Wrong item modifier!");
        Assert.assertTrue(documentDetailsPage.getModifyDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong modification date!");

        LOG.info("STEP 3: Click 'Download' icon to download testFile");
        documentDetailsPage.clickOnDownloadButton();
        String filePath = testDataFolder + docName;
        Assert.assertTrue(new File(filePath).exists(), "File does not exist!");


        LOG.info("STEP 4: Click 'Like' icon to like testFile then click on unlike");
        documentDetailsPage.clickOnLikeUnlike();
        Assert.assertEquals(documentDetailsPage.getLikesNo(), 1, "Wrong no of likes!");
        documentDetailsPage.clickOnLikeUnlike();
        Assert.assertEquals(documentDetailsPage.getLikesNo(), 0, "File should have 0 likes!");

        LOG.info("STEP 5: Click 'Favorite' icon to favorite testFile then click unfavorite");
        documentDetailsPage.clickOnFavoriteUnfavoriteLink();
        Assert.assertTrue(documentDetailsPage.getFavoriteText().isEmpty(), "File should be already added to favorite!");
        documentDetailsPage.clickOnFavoriteUnfavoriteLink();
        Assert.assertTrue(documentDetailsPage.getFavoriteText().equals("Favorite"), "File should be already added to favorite!");

        LOG.info("STEP 6: Click 'Comment' icon to add a comment to testFile");
        documentDetailsPage.clickOnCommentDocument();
        documentDetailsPage.addComment("Comment for test");
        Assert.assertTrue(documentDetailsPage.getCommentContent().equals("Comment for test"), "Comment should be displayed!");
        LOG.info("STEP 7: Click 'Shared' icon");
        documentDetailsPage.clickOnSharedLink();
    }

    @AfterMethod
    public void deleteFile()
    {
        File file = new File(testDataFolder + docName);
        file.delete();
        Assert.assertFalse(file.exists(), "File should not exist!");
    }
}
