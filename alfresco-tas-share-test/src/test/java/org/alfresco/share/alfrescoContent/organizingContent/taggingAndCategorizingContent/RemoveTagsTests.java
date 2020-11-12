package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class RemoveTagsTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String siteName1 = "site1-" + random;
    private final String siteName2 = "site2-" + random;
    private final String folderName = "folder-" + random;
    private final String tagName = "tagName-" + random;
    private final String fileName = "file-" + random;
    private final String userName = "profileUser-" + random;
    private final String firstName = "FirstName";
    private final String lastName = "LastName";
    private final String description = "Description-" + random;
    private final String fileContent = "content of the file.";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName1, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName2);
        contentAction.addSingleTag(userName, password, siteName2, folderName, tagName);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAction.addSingleTag(userName, password, siteName1, fileName, tagName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName1);
        siteService.delete(adminUser, adminPassword, siteName2);
    }

    @TestRail (id = "C7443")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeTagFolder()
    {
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag from the folder");
        documentLibraryPage.mouseOverTags(folderName);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(folderName), folderName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        documentLibraryPage.clickEditTagIcon(folderName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), folderName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click the remove button");
        documentLibraryPage.removeTag(tagName.toLowerCase());

        LOG.info("STEP4: Click 'Save' link");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(documentLibraryPage.isNoTagsTextDisplayed(folderName), folderName + " -> " + tagName + " is removed.");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C10530")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void removeTagFile()
    {
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag from the folder");
        documentLibraryPage.mouseOverTags(fileName);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(fileName), fileName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        documentLibraryPage.clickEditTagIcon(fileName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), fileName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
        documentLibraryPage.removeTag(tagName.toLowerCase());
        getBrowser().waitInSeconds(3);

        LOG.info("STEP4: Click 'Save' link");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(documentLibraryPage.isNoTagsTextDisplayed(fileName), fileName + " -> " + tagName + " is removed.");

        cleanupAuthenticatedSession();
    }
}