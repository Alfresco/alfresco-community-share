package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class WorkingWithTagsTests extends ContextAwareWebTest
{
    private final String userName = String.format("profileUser-%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = "content of the file.";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;

    @TestRail (id = "C7444")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void updateTags()
    {
        String random = RandomData.getRandomAlphanumeric();
        String siteName = "site-C7444-" + random;
        String fileName = "file-C7444-" + random;
        String tagName = "tagName-C7444-" + random;
        String addedTagName = "addedTag-C7444-" + random;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        siteService.create(userName, password, domain, siteName, "Description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAction.addSingleTag(userName, password, siteName, fileName, tagName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
//        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the tag from the folder");
        documentLibraryPage.mouseOverTags(fileName);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(fileName), fileName + " -> 'Edit Tag' icon is displayed.");

        LOG.info("STEP2: Click \"Edit Tags\" icon");
        documentLibraryPage.clickEditTagIcon(fileName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), fileName + " -> 'Edit Tag' text input field is displayed.");

        LOG.info("STEP3: Hover over the tag. Click 'Remove' icon. Click 'Save' link");
        documentLibraryPage.removeTag(tagName.toLowerCase());
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
        assertEquals(documentLibraryPage.getTags(fileName), Collections.singletonList(addedTagName.toLowerCase()).toString(), fileName + " -> tags=");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }
}