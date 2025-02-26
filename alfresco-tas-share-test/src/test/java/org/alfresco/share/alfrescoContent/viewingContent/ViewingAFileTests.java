package org.alfresco.share.alfrescoContent.viewingContent;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DefaultProperties;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.FileActionsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FileType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


@Slf4j
public class ViewingAFileTests extends BaseTest
{
    private final DateTime currentDate = new DateTime();
    private final String thumbNailImage = "docx-file-48.png";
    private DocumentDetailsPage documentPreviewPage;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentLibraryPage2 documentLibraryPage2;
    private FileActionsPage fileActions;
    private SocialFeatures socialFeatures;
    @Autowired
    private DefaultProperties properties;
    private FileModel fileToCheck;
    FolderModel folder = FolderModel.getRandomFolderModel();
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a random user and a random public site");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        getCmisApi().authenticateUser(user.get());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentPreviewPage = new DocumentDetailsPage(webDriver);
        documentLibraryPage2 = new DocumentLibraryPage2(webDriver);
        fileActions = new FileActionsPage(webDriver);
        socialFeatures = new SocialFeatures(webDriver);

        log.info("Create File in document library.");
        fileToCheck = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();

        authenticateUsingCookies(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C9917")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void informationAndOptionsCheckAvailableInfo()
    {
        log.info("Navigate to the site document library and verify file present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage
            .clickOnFile(fileToCheck.getName());

        log.info("Step 2: Verify the file location and name information available"
            + " Step 3: Verify and confirm that the file version is displayed."
            + " Step 4: Verify and confirm that information regarding the last user who has modified the file is available."
            + " Step 5: Verify that the date/time of the last modification is displayed."
            + " Step 6: Verify that the file type is indicated by an icon to the left of the information section.");

        documentPreviewPage
            .assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName())
            .assertIsDocumentsLinkPresent()
            .assertVerifyFileVersion("1.0")
            .assertVerifyItemModifire(user.get().getFirstName(), user.get().getLastName())
            .assertVerifyModifiedDate(currentDate.toString("EEE d MMM yyyy"))
            .assertIsDocumentThumbnailDisplayed(thumbNailImage);
    }

    @TestRail (id = "C9923")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void informationAndOptionsCheckLinkToReturnToDocumentLibrary()
    {
        log.info("Navigate to the site document library and verify file present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library."
            + " Step 2: Verify the presence of the link to return to Document Library.");
        documentLibraryPage
            .clickOnFile(fileToCheck.getName())
            .assertIsDocumentsLinkPresent();

        log.info("Step 3: Click on the Documents link.");
        documentPreviewPage
            .clickDocumentsLink();

        log.info("Step 4: Verify Relative path of document library page, after clicking on link to return to Document Library");
        documentLibraryPage
            .assertVerifyDocumentRelativePath(site.get().getTitle());
    }

    @TestRail (id = "C9925")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void informationAndOptionsLikeOption()
    {
        log.info("Navigate to the site document library and verify file present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage
            .clickOnFile(fileToCheck.getName());

        log.info("Step 2: Verify that the Like option is available and then click on Like button");
        documentPreviewPage
            .assertLikeButtonDisplayedOnDocumentDetailsPage()
            .clickOnLikeUnlike();

        log.info("Step 3: Verify the number of like should be 1 and the Click the Like button again to unlike the content.");

        documentPreviewPage
            .assertVerifyNoOfLikes(1)
            .clickOnLikeUnlike();

        log.info("Step 4: Verify the number of likes should be 0 After Clicking the Like button again to unlike the content");

        documentPreviewPage
            .assertVerifyNoOfLikes(0);
    }

    @TestRail (id = "C9926")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void informationAndOptionsFavoriteOption()
    {
        log.info("Navigate to the site document library and verify file present in the document library");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage
            .clickOnFile(fileToCheck.getName());

        log.info("Step 2: Verify that the Favorite option is available and Click the Favorite button.");
        documentPreviewPage
            .assertIsAddToFavoriteLinkDisplayed()
            .clickOnFavoriteUnfavoriteLink();

        log.info("Step 3: Verify that the document marked as favorite and Click the Favorite button again.");
        documentPreviewPage
            .assertContantMarkedAsFavorite()
            .clickOnFavoriteUnfavoriteLink();

        log.info("Step 4: Verify that now the document is not marked as favorite after Click the Favorite button again.");
        documentPreviewPage
            .assertContantNotMarkedAsFavorite();
    }


    @TestRail (id = "C9936")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void checkActionsAvailability()
    {
        log.info("Step 1: Click on document name");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .clickOnFile(fileToCheck.getName());

        log.info("Step 2: Check the document actions available");
        fileActions
            .assertIsDocumentActionsBlockDisplayed();

        fileActions
            .assertIsViewInBrowserDisplayed()
            .assertIsEditInGoogleDocsDisplayed()
            .assertIsEditInMicrosoftOfficeDisplayed()
            .assertIsUploadNewVersionDisplayed()
            .assertIsEditPropertiesDisplayed()
            .assertIsMoveToOptionDisplayed()
            .assertIsCopyToOptionDisplayed()
            .assertIsDeleteDocumentOptionDisplayed()
            .assertIsStartWorkflowDisplayed()
            .assertIsManagePermissionDisplayed()
            .assertIsBecomeOwnerDisplayed()
            .assertIsManageAspectsDisplayed()
            .assertIsChangeTypeOptionDisplayed();

        log.info("Step 3: Check Tags presence");
        fileActions
            .assertIsTagBlockDisplayed();

        log.info("Step 4: Check that Share block present");
        fileActions
            .assertIsShareBlockDisplayed();

        log.info("Step 5: Verify share link in share block");

        String shareUrl = properties.getShareUrl().toString();
        String nodeRef = fileToCheck.getNodeRefWithoutVersion();
        String siteName = site.get().getTitle();
        String shareLinkText = shareUrl+"/page/site/"+siteName+"/document-details?nodeRef=workspace://SpacesStore/"+nodeRef;

        fileActions
            .assertVerifyShareLink(shareLinkText);

        log.info("Step 6: Check that the Properties block is displayed");
        fileActions
            .assertIsPropertiesBlockDisplayed();


        log.info("Step 7: Check that the workflow block is displayed");
        fileActions
            .assertIsWorkFlowsBlockDisplayed();

        log.info("Step 8: Check that Version History block is displayed ");
        fileActions
            .assertIsVersionHistoryBlockDisplayed();
    }

    @TestRail (id = "C5885")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void comments()
    {
        getCmisApi().authenticateUser(user.get()).usingSite(site.get())
            .createFolder(folder).usingResource(folder)
            .createFile(fileToCheck);

        documentLibraryPage.navigate(site.get())
            .clickOnFolderName(folder.getName())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(fileToCheck.getName());
        documentLibraryPage.addMultileComment();
        assertEquals(documentLibraryPage.getCommentCount(), "1 - 10 of 12");
        documentLibraryPage.ClickOnNextCommentPage();
        assertEquals(documentLibraryPage.getCommentCount(), "11 - 12 of 12");
        documentLibraryPage.ClickOnPerviousCommentPage();
        assertEquals(documentLibraryPage.getCommentCount(), "1 - 10 of 12");

        log.info("Step 2: validate Edit comment");
        documentPreviewPage.clickEditComment("comment");
        documentPreviewPage.editComment("commentEditedByUser");
        documentPreviewPage.clickOnSaveButtonEditComment();
        Assert.assertEquals(documentPreviewPage.getCommentContent(), "commentEditedByUser", "Comment edited successfully by user");
    }

    @TestRail (id = "C6355")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void documentActions()
    {
        getCmisApi().authenticateUser(user.get()).usingSite(site.get())
            .createFolder(folder).usingResource(folder)
            .createFile(fileToCheck);

        documentLibraryPage.navigate(site.get())
            .clickOnFolderName(folder.getName())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(fileToCheck.getName());

        log.info("Step 2: Check the document actions available");
        fileActions.assertIsDocumentActionsBlockDisplayed();
        fileActions
            .assertIsViewInBrowserDisplayed()
            .assertIsEditInGoogleDocsDisplayed()
            .assertIsEditInMicrosoftOfficeDisplayed()
            .assertIsUploadNewVersionDisplayed()
            .assertIsEditPropertiesDisplayed()
            .assertIsMoveToOptionDisplayed()
            .assertIsCopyToOptionDisplayed()
            .assertIsDeleteDocumentOptionDisplayed()
            .assertIsStartWorkflowDisplayed()
            .assertIsManagePermissionDisplayed()
            .assertIsBecomeOwnerDisplayed()
            .assertIsManageAspectsDisplayed()
            .assertIsChangeTypeOptionDisplayed();

        log.info("Step 3: Collapse document actions(click on Document Actions)");
        documentLibraryPage.collapsedocumentAction();
        assertFalse(fileActions.isViewInBrowserDisplayed(), "View in Browser option is not displayed");
        assertFalse(fileActions.isMoveToDisplayed(), "Move to... option is not displayed");
    }

    @TestRail (id = "C6356")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void documentTags()
    {
        getCmisApi().authenticateUser(user.get()).usingSite(site.get())
            .createFolder(folder).usingResource(folder)
            .createFile(fileToCheck);

        documentLibraryPage.navigate(site.get())
            .clickOnFolderName(folder.getName())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("STEP1: Click \"Edit Tag\" icon");
        documentLibraryPage2.usingContent(fileToCheck)
            .clickTagEditIcon();
        documentLibraryPage2.usingContent(fileToCheck).setTag("check tag")
            .clickSave();

        log.info("Step 2: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(fileToCheck.getName());
        assertEquals(documentLibraryPage.getTagValue(), "check tag");
        assertTrue(documentLibraryPage.isEditTagDisplayed(), "Move to... option is not displayed");

        log.info("Step 3: Collapse Tags section and check that tag is no longer displayed.");
        documentLibraryPage.clickOnTagToggle();
        assertFalse(documentLibraryPage.assertTagDisplayed(), "Tag value not Available");
    }

    @TestRail (id = "C6357")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void shareDocument()
    {
        getCmisApi().authenticateUser(user.get()).usingSite(site.get())
            .createFolder(folder).usingResource(folder)
            .createFile(fileToCheck);

        documentLibraryPage.navigate(site.get())
            .clickOnFolderName(folder.getName())
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(fileToCheck.getName());
        documentPreviewPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());

        log.info("Step 2: Validate file value in new tab");
        documentPreviewPage.openUrlNewTab();
        documentPreviewPage.assertIsFileNameDisplayedOnPreviewPage(fileToCheck.getName());
    }
}
