package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet.DocumentsFilter;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyDocumentsDashletTests extends ContextAwareWebTest
{
    @Autowired
    private MyDocumentsDashlet myDocumentsDashlet;

    private UserModel user;
    private SiteModel site;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();

        setupAuthenticatedSession(user);
        cmisApi.authenticateUser(user);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        dataSite.usingAdmin().deleteSite(site);
    }

    @TestRail (id = "C2134")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkDetailedView()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        cmisApi.usingSite(site).createFile(testFile);
        userDashboard.navigate(user);

        myDocumentsDashlet.usingDocument(testFile)
            .assertFileIsDisplayed()
            .assertSmallIconThumbnailIsDisplayed();
        myDocumentsDashlet.selectDetailedView();
        myDocumentsDashlet.usingDocument(testFile)
            .assertThumbnailIsDisplayed()
            .assertCommentLinkIsDisplayed()
            .assertLikeIsDisplayed()
            .assertNumberOfLikesIs(0)
            .like()
            .assertUnlikeIsDisplayed()
            .assertNumberOfLikesIs(1)
            .assertAddToFavoriteIsDisplayed()
            .addToFavorite()
                .assertRemoveFromFavoriteIsDisplayed()
            .removeFromFavorite()
                .assertAddToFavoriteIsDisplayed()
            .assertNoDescriptionIsDisplayed()
            .assertVersionIs(1.0)
            .selectDocument()
                .assertDocumentDetailsPageIsOpened();
        userDashboard.navigate(user);
        myDocumentsDashlet.usingDocument(testFile)
            .clickThumbnail().assertDocumentDetailsPageIsOpened();
        userDashboard.navigate(user);
        myDocumentsDashlet.usingDocument(testFile)
            .addComment().assertDocumentDetailsPageIsOpened().assertCommentsAreaIsOpened();
    }

    @TestRail (id = "C2138")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void filterDocuments() throws Exception
    {
        FileModel file1 = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        FileModel file2 = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        FileModel file3 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);

        cmisApi.usingSite(site).createFile(file1).createFile(file2).createFile(file3)
            .then().usingResource(file1).checkOut().assertThat().documentIsCheckedOut();
        restApi.authenticateUser(user).withCoreAPI().usingAuthUser().addFileToFavorites(file2);

        userDashboard.navigate(user);
        myDocumentsDashlet.assertSelectedFilterIs(DocumentsFilter.RECENTLY_MODIFIED)
            .usingDocument(file1).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file2).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file3).assertFileIsDisplayed();
        myDocumentsDashlet.filter(DocumentsFilter.EDITING)
            .assertNrOfDisplayedDocumentsIs(1)
                .usingDocument(file1).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file2).assertFileIsNotDisplayed();
        myDocumentsDashlet.usingDocument(file3).assertFileIsNotDisplayed();

        myDocumentsDashlet.filter(DocumentsFilter.MY_FAVORITES)
            .usingDocument(file2).assertFileIsDisplayed();
        myDocumentsDashlet.usingDocument(file1).assertFileIsNotDisplayed();
        myDocumentsDashlet.usingDocument(file3).assertFileIsNotDisplayed();
    }
}