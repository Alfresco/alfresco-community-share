package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class NavigationPanelTests extends ContextAwareWebTest
{
    @Autowired
    DocumentsFilters documentsFilters;

    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    CreateContent createContent;

    @Autowired
    DeleteDialog deleteDialog;

    private final String uniqueId = DataUtil.getUniqueIdentifier();
    private final String folderName = "folder-C7928-" + uniqueId;
    private final String path = "Shared/";
    private final String tag = "tag-" + uniqueId;

    @BeforeClass
    public void setupTest()
    {
        content.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentAction.addSingleTag(adminUser, adminPassword, path + folderName, tag);

        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    }

    @TestRail(id = "C7927")
    @Test()
    public void verifyFilters()
    {
        LOG.info("STEP1: Verify the Sidebar");
        ArrayList<String> expectedFilters = new ArrayList<>(Arrays.asList("Documents", "Shared Files", "Categories", "Tags"));
        assertEquals(documentsFilters.getSidebarFilters(), expectedFilters, "Sidebar filters=");
    }

    @TestRail(id = "C7928")
    @Test()
    public void verifyTags()
    {
        LOG.info("STEP1: Verify the list of tags in the Tags section");
        assertTrue(documentsFilters.getSidebarTag(tag).contains(tag), "Sidebar Tags filter contains " + tag);

        LOG.info("STEP2: Click on one tag name");
        documentsFilters.clickSidebarTag(tag);
        getBrowser().waitInSeconds(4);
        sharedFilesPage.renderedPage();
        assertTrue(sharedFilesPage.getFoldersList().toString().contains(folderName), "Displayed folders=");
    }

    @AfterClass
    public void cleanUp()
    {
        content.deleteContentByPath(adminUser, adminPassword, path + folderName);
    }
}