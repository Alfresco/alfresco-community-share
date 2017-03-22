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

    private final String random = DataUtil.getUniqueIdentifier();
    private final String userName = "profileUser-" + random;
    private final String siteName = "site" + random;
    private final String description = "Description-" + random;
    private final String fileName = "fileC10209-" + random;
    private final String folderName = "folderC10210-" + random;
    private final String fileTag = "tagC10209-" + random;
    private final String folderTag = "tagC10210-" + random;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        String lastName = "LastName";
        String firstName = "FirstName";
        String fileContent = "content of the file.";

        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");
    }

    @TestRail(id = "C10209")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createTagEditPropertiesDialog()
    {
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Hover over the file name. STEP2: Click \"Edit Properties\" link");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Edit Properties", documentLibraryPage);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), fileName), "Displayed dialog=");

        LOG.info("STEP3: Click \"Select\" button");
        editPropertiesDialog.clickSelectTags();
        assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

        LOG.info("STEP4: Type any tag name in the input field and click \"Create new item\" icon");
        selectDialog.typeTag(fileTag);
        selectDialog.clickCreateNewIcon();
        assertTrue(selectDialog.isItemSelected(fileTag.toLowerCase()),
                "'Select...' dialog -> added tags section: '" + fileTag.toLowerCase() + "' is displayed.");
        assertTrue(selectDialog.isItemRemovable(fileTag.toLowerCase()), "'Select...' dialog -> 'Remove' icon is displayed for: " + fileTag.toLowerCase());

        LOG.info("STEP5: Click \"Ok\" button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isTagSelected(fileTag.toLowerCase()), "'Tags:' section=");

        LOG.info("STEP6: Click \"Save\" button");
        editPropertiesDialog.clickSave();
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(fileTag.toLowerCase()));
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertEquals(documentLibraryPage.getTags(fileName), tagsList.toString(), fileName + " -> tags=");
    }

    @TestRail(id = "C10210")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void createTagEditTagIcon()
    {
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
        documentLibraryPage.typeTagName(folderTag);
        documentLibraryPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(folderTag.toLowerCase()));
        assertEquals(documentLibraryPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");
    }
}