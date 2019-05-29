package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles;

import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
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
    private DocumentsFilters documentsFilters;

    @Autowired
    private SharedFilesPage sharedFilesPage;

    private final String uniqueId = RandomData.getRandomAlphanumeric();
    private final String folderName = "folder-C7928-" + uniqueId;
    private final String path = "Shared/";
    private final String tag = "tag-" + uniqueId.toLowerCase();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentAction.addSingleTag(adminUser, adminPassword, path + folderName, tag);

        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    }


    @TestRail (id = "C7927")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyFilters()
    {
        LOG.info("STEP1: Verify the Sidebar");
        ArrayList<String> expectedFilters = new ArrayList<>(Arrays.asList("Documents", "Shared Files", "Categories", "Tags"));
        assertEquals(documentsFilters.getSidebarFilters(), expectedFilters, "Sidebar filters=");
    }

    @TestRail (id = "C7928")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
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
        contentService.deleteContentByPath(adminUser, adminPassword, path + folderName);
    }
}