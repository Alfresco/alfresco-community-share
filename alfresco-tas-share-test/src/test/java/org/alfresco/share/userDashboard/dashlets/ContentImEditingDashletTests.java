package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.ContentImEditingDashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ContentImEditingDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private ContentImEditingDashlet contentImEditingDashlet;

    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    private UserModel user;
    private FileModel testFile;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.authenticateUser(user).usingSite(site).createFile(testFile);
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.CONTENT_I_AM_EDITING, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        dataSite.usingAdmin().deleteSite(site);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD, "Acceptance" })
    public void checkContentImEditingTests()
    {
        userDashboard.assertDashletIsAddedInPosition(Dashlets.CONTENT_I_AM_EDITING,1, 3);
        contentImEditingDashlet.assertDashletTitleEquals(language.translate("contentImEditingDashlet.title"))
            .clickHelpIcon()
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("contentImEditingDashlet.HelpBalloonText"))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed()
            .assertAllHeadersAreDisplayed();
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD, "Acceptance" })
    public void checkDocumentIsDisplayedWhenEdited()
    {
        contentImEditingDashlet.assertDocumentIsNotDisplayed(testFile);

        cmisApi.usingResource(testFile).checkOut().assertThat().documentIsCheckedOut();
        userDashboard.navigate(user);
        contentImEditingDashlet.assertDocumentIsDisplayed(testFile)
            .clickDocument(testFile);
        Assert.assertTrue(documentLibraryPage.isFileDisplayed(testFile.getName()));

        userDashboard.navigate(user);
        contentImEditingDashlet.clickSite(testFile)
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site);
    }
}
