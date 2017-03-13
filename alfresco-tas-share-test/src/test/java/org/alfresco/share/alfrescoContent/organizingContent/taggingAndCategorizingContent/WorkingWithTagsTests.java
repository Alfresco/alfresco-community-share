package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class WorkingWithTagsTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    String userName = "profileUser-" + DataUtil.getUniqueIdentifier();
    String firstName = "FirstName";
    String lastName = "LastName";
    String description = "Description-" + DataUtil.getUniqueIdentifier();
    String fileContent = "content of the file.";

    @TestRail(id = "C7444")
    @Test()
    public void updateTags()
    {
        String random = DataUtil.getUniqueIdentifier();
        String siteName = "site-C7444-" + random;
        String fileName = "file-C7444-" + random;
        String tagName = "tagName-C7444-" + random;
        String addedTagName = "addedTag-C7444-" + random;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAction.addSingleTag(userName, password, siteName, fileName, tagName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag from the folder");
        documentLibraryPage.mouseOverTags(fileName);
        getBrowser().waitInSeconds(2);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(fileName), fileName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        documentLibraryPage.clickEditTagIcon(fileName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), fileName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag. Click 'Remove' icon. Click 'Save' link");
        assertEquals(documentLibraryPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        getBrowser().waitInSeconds(3);
        assertTrue(documentLibraryPage.isNoTagsTextDisplayed(fileName), fileName + " -> " + tagName + " is removed.");

        LOG.info("STEP4: Click \"Edit Tag\" icon");
        documentLibraryPage.mouseOverNoTags(fileName);
        getBrowser().waitInSeconds(3);
        documentLibraryPage.clickEditTagIcon(fileName);

        LOG.info("STEP5: Type any tag name in the input field. Click \"Save\" link");
        documentLibraryPage.typeTagName(addedTagName);
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        getBrowser().waitInSeconds(2);
        assertEquals(documentLibraryPage.getTags(fileName), Arrays.asList(addedTagName.toLowerCase()).toString(), fileName + " -> tags=");

        cleanupAuthenticatedSession();
    }
}