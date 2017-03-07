package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class SharedFilesTests extends ContextAwareWebTest
{
    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    SocialFeatures socialFeatures;

    @Autowired
    CreateContent createContent;

    @Autowired
    DeleteDialog deleteDialog;

    @BeforeClass
    public void setupTest()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    }

    @TestRail(id = "C7661")
    @Test()
    public void verifyShareButton()
    {
        String docName = "Doc-C7661-" + DataUtil.getUniqueIdentifier();

        LOG.info("Create a document in 'Shared Files'");
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        sharedFilesPage.clickCreateButton();
        createContent.clickPlainTextButton();
        createContent.sendInputForName(docName);
        createContent.clickCreateButton();
        sharedFilesPage.navigate();

        LOG.info("STEP1: Hover over a file and click on the \"Share\" button.");
        sharedFilesPage.mouseOverFileName(docName);
        assertTrue(socialFeatures.checkShareButtonAvailability(), "Share button is displayed.");
        socialFeatures.clickShareButton(docName);
        assertTrue(socialFeatures.isPublicLinkInputFieldDisplayed(), "Public link input field is displayed.");
    }

    @AfterClass
    public void cleanUp()
    {
        LOG.info("Delete All from 'Shared Files'");
        sharedFilesPage.navigate();
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.breadcrumb.selectedItems.delete"));
        deleteDialog.clickDelete();

        cleanupAuthenticatedSession();
    }
}