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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class RemoveTagsTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    String random = DataUtil.getUniqueIdentifier();
    String siteName1 = "site1-" + random;
    String siteName2 = "site2-" + random;
    String folderName = "folder-" + random;
    String tagName = "tagName-" + random;
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
    }

    @TestRail(id = "C7443")
    @Test()
    public void removeTagFolder()
    {
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName2);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag from the folder");
        documentLibraryPage.mouseOverTags(folderName);
        getBrowser().waitInSeconds(4);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(folderName), folderName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        documentLibraryPage.clickEditTagIcon(folderName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), folderName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click the remove button");
        assertEquals(documentLibraryPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");

        LOG.info("STEP4: Click 'Save' link");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(documentLibraryPage.isNoTagsTextDisplayed(folderName), folderName + " -> " + tagName + " is removed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C10530")
    @Test()
    public void removeTagFile()
    {
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName1);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag from the folder");
        documentLibraryPage.mouseOverTags(fileName);
        getBrowser().waitInSeconds(4);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(fileName), fileName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        documentLibraryPage.clickEditTagIcon(fileName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), fileName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
        assertEquals(documentLibraryPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");
        getBrowser().waitInSeconds(3);

        LOG.info("STEP4: Click 'Save' link");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        assertTrue(documentLibraryPage.isNoTagsTextDisplayed(fileName), fileName + " -> " + tagName + " is removed.");

        cleanupAuthenticatedSession();
    }
}