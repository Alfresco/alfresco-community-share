package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class EditTagTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    String random = DataUtil.getUniqueIdentifier();
    String siteName1 = "site1-" + random;
    String siteName2 = "site2-" + random;
    String folderName = "folder-" + random;
    String tagName = "tagName-" + random;
    String newTagName = "newTagName-" + random;
    String fileName = "file-" + random;
    String userName = "profileUser-" + random;
    String firstName = "FirstName";
    String lastName = "LastName";
    String description = "Description-" + random;
    String fileContent = "content of the file.";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, description, Site.Visibility.PUBLIC);
        content.createFolder(userName, password, folderName, siteName2);
        contentAction.addSingleTag(userName, password, siteName2, folderName, tagName);
        content.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAction.addSingleTag(userName, password, siteName1, fileName, tagName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");
    }

    @TestRail(id = "C7460")
    @Test()
    public void editTagFile()
    {
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag(s) from the content");
        documentLibraryPage.mouseOverTags(fileName);
        getBrowser().waitInSeconds(3);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(fileName), fileName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.clickEditTagIcon(fileName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), fileName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Click on any tag and type a valid tag name");
        documentLibraryPage.editTag(fileName, tagName.toLowerCase(), newTagName);

        LOG.info("STEP4: Click \"Save\" link and verify the content tags");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        try
        {
            assertEquals(documentLibraryPage.getTags(fileName), Arrays.asList(newTagName.toLowerCase()).toString(),
                    tagName.toLowerCase() + " is updated with value:");
        }
        catch (AssertionError e)
        {
            getBrowser().refresh();
            documentLibraryPage.renderedPage();
        }
        assertEquals(documentLibraryPage.getTags(fileName), Arrays.asList(newTagName.toLowerCase()).toString(),
                tagName.toLowerCase() + " is updated with value:");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C10529")
    @Test()
    public void editTagFolder()
    {
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag(s) from the content");
        documentLibraryPage.mouseOverTags(folderName);
        getBrowser().waitInSeconds(3);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(folderName), folderName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.clickEditTagIcon(folderName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), folderName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Click on any tag and type a valid tag name");
        documentLibraryPage.editTag(folderName, tagName.toLowerCase(), newTagName);

        LOG.info("STEP4: Click \"Save\" link and verify the content tags");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        try
        {
            assertEquals(documentLibraryPage.getTags(folderName), Arrays.asList(newTagName.toLowerCase()).toString(),
                    tagName.toLowerCase() + " is updated with value:");
        }
        catch (AssertionError e)
        {
            getBrowser().refresh();
            documentLibraryPage.renderedPage();
        }
        assertEquals(documentLibraryPage.getTags(folderName), Arrays.asList(newTagName.toLowerCase()).toString(),
                tagName.toLowerCase() + " is updated with value:");

        cleanupAuthenticatedSession();
    }
}