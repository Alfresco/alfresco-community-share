package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
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
    @Autowired private DocumentLibraryPage documentLibraryPage;

    private final String random = DataUtil.getUniqueIdentifier();
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

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName1, description, Site.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName2, description, Site.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName2);
        contentAction.addSingleTag(userName, password, siteName2, folderName, tagName);
        contentService.createDocument(userName, password, siteName1, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAction.addSingleTag(userName, password, siteName1, fileName, tagName);
    }

    @TestRail(id = "C7443")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
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
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
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