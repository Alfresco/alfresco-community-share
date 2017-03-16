package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class CreateTagTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private EditPropertiesDialog editPropertiesDialog;

    @Autowired private SelectDialog selectDialog;

    private final String userName = "profileUser-" + DataUtil.getUniqueIdentifier();
    private final String firstName = "FirstName";
    private final String lastName = "LastName";
    private final String description = "Description-" + DataUtil.getUniqueIdentifier();
    private final String fileContent = "content of the file.";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
    }

    @TestRail(id = "C10209")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createTagEditPropertiesDialog()
    {
        String random = DataUtil.getUniqueIdentifier();
        String siteName = "site-C10209-" + random;
        String fileName = "file-C10209-" + random;
        String tagName = "tagName-C10209-" + random;
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the file name");
        documentLibraryPage.mouseOverFileName(fileName);
        assertTrue(documentLibraryPage.isMoreMenuDisplayed(fileName), "'More' menu is displayed.");

        LOG.info("STEP2: Click \"Edit Properties\" link");
        documentLibraryPage.clickCheckBox(fileName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Properties", documentLibraryPage);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileName), "Displayed dialog=");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP4: Type any tag name in the input field and click \"Create new item\" icon");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        assertTrue(selectDialog.isItemSelected(tagName.toLowerCase()),
                "'Select...' dialog -> added tags section: '" + tagName.toLowerCase() + "' is displayed.");
        assertTrue(selectDialog.isItemRemovable(tagName.toLowerCase()), "'Select...' dialog -> 'Remove' icon is displayed for: " + tagName.toLowerCase());

        LOG.info("STEP5: Click \"Ok\" button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isTagSelected(tagName.toLowerCase()), "'Tags:' section=");

        LOG.info("STEP6: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertEquals(documentLibraryPage.getTags(fileName), tagsList.toString(), fileName + " -> tags=");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C10210")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createTagEditTagIcon()
    {
        String random = DataUtil.getUniqueIdentifier();
        String siteName = "site-C10210-" + random;
        String folderName = "folder-C10210-" + random;
        String tagName = "tagName-C10210-" + random;
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the text \"No Tags\" from the folder");
        documentLibraryPage.mouseOverNoTags(folderName);
        getBrowser().waitInSeconds(3);
        assertTrue(documentLibraryPage.isEditTagIconDisplayed(folderName), folderName + " -> \"Edit Tag\" icon is displayed");

        LOG.info("STEP2: Click \"Edit Tag\" icon");
        documentLibraryPage.clickEditTagIcon(folderName);
        assertTrue(documentLibraryPage.isEditTagInputFieldDisplayed(), folderName + " -> Edit tag text input field is displayed.");

        LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
        documentLibraryPage.typeTagName(tagName);
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
        assertEquals(documentLibraryPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");

        cleanupAuthenticatedSession();
    }
}