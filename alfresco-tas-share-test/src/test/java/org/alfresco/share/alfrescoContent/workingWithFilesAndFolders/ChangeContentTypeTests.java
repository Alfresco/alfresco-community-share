package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class ChangeContentTypeTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    ChangeContentTypeDialog changeContentTypeDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    String userName = "profileUser-" + DataUtil.getUniqueIdentifier();
    String firstName = "FirstName";
    String lastName = "LastName";
    String description = "Description-" + DataUtil.getUniqueIdentifier();
    String docContent = "content of the file.";
    String siteName = "Site-" + DataUtil.getUniqueIdentifier();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
    }

    @TestRail(id = "C7163")
    @Test()
    public void cancelChangeType()
    {
        String docName = "Doc-C7163-" + DataUtil.getUniqueIdentifier();
        content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.clickOnFile(docName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page:");

        LOG.info("STEP1: Click 'Change Type' option from 'Document Actions' list");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("STEP2: Select 'Article' from 'New Type' dropdown and click 'Cancel' button");
        changeContentTypeDialog.selectOption("Article");
        changeContentTypeDialog.clickButton("Cancel");
        ArrayList<String> propertiesList = new ArrayList<>(Arrays.asList("Template Name:", "Primary Image:", "Secondary Image:", "Related Articles:"));
        assertEquals(documentDetailsPage.checkPropertiesAreNotDisplayed(propertiesList), "Given list isn't displayed", " property displayed.");

        LOG.info("STEP3: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickDocumentActionsOption("Edit Properties");
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertEquals(editPropertiesPage.checkPropertiesAreNotDisplayed(propertiesList), "Given list isn't displayed", " property is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7166")
    @Test()
    public void changeTypeFolder()
    {
        String folderName = "Folder-C7166-" + DataUtil.getUniqueIdentifier();
        content.createFolder(userName, DataUtil.PASSWORD, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page:");

        LOG.info("STEP1: Verify folder's Properties list from 'Folder Actions' section");
        ArrayList<String> expectedProperties = new ArrayList<>(
                Arrays.asList("Name:", "Title:", "Description:", "Author:", "Mimetype:", "Size:", "Creator:", "Created Date:", "Modifier:", "Modified Date:"));
        assertEquals(documentDetailsPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        LOG.info("STEP2: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickDocumentActionsOption("Edit Properties");
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Author:", "Mimetype:"));
        assertEquals(editPropertiesPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        LOG.info("STEP3: Cancel 'Edit Properties'.");
        editPropertiesPage.clickButton("Cancel");
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page:");

        LOG.info("STEP4: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");
        assertTrue(changeContentTypeDialog.isDropdownMandatory(), "'New Type' dropdown is mandatory.");
        assertTrue(changeContentTypeDialog.isButtonDisplayed("OK"), "'OK' button is displayed.");
        assertTrue(changeContentTypeDialog.isButtonDisplayed("Cancel"), "'Cancel' button is displayed.");

        LOG.info("STEP5: Select 'ws:website' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog.selectOption("ws:website");
        changeContentTypeDialog.clickButton("OK");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Host Name:", "Host Port:", "Web App Context :",
                "Site Configuration:", "Site Languages:", "Feedback Configuration:", "Publish Target:"));
        assertEquals(editPropertiesPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");
        browser.refresh();

        LOG.info("STEP6: Click 'Edit Properties' option from 'Folder Actions' section");
        documentDetailsPage.clickDocumentActionsOption("Edit Properties");
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Host Name:", "Host Port:", "Web App Context :",
                "Site Configuration:", "Site Languages:", "Feedback Configuration:", "Publish Target:"));
        assertEquals(editPropertiesPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7167")
    @Test
    public void changeTypeFile()
    {
        String docName = "Doc-C7167-" + DataUtil.getUniqueIdentifier();
        content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.clickOnFile(docName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page:");

        LOG.info("STEP1: Verify document's Properties list");
        ArrayList<String> expectedProperties = new ArrayList<>(
                Arrays.asList("Name:", "Title:", "Description:", "Author:", "Mimetype:", "Size:", "Creator:", "Created Date:", "Modifier:", "Modified Date:"));
        assertEquals(documentDetailsPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        LOG.info("STEP2: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickDocumentActionsOption("Edit Properties");
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Author:", "Mimetype:"));
        assertEquals(editPropertiesPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        LOG.info("STEP3: Cancel 'Edit Properties'.");
        editPropertiesPage.clickButton("Cancel");
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page:");

        LOG.info("STEP4: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("STEP5: Select 'Article' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog.selectOption("Article");
        changeContentTypeDialog.clickButton("OK");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Author:", "Creator:", "Created Date:", "Modifier:",
                "Modified Date:", "Template Name:", "Mimetype:", "Size:", "Primary Image:", "Secondary Image:", "Related Articles:"));
        assertEquals(documentDetailsPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");
        browser.refresh();

        LOG.info("STEP6: Click 'Edit Properties' option from 'Document Actions' section");
        documentDetailsPage.clickDocumentActionsOption("Edit Properties");
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        expectedProperties.clear();
        expectedProperties = new ArrayList<>(Arrays.asList("Name:", "Title:", "Description:", "Author:", "Template Name:", "Tags:", "Primary Image:",
                "Secondary Image:", "Related Articles:"));
        assertEquals(editPropertiesPage.checkDisplayedProperties(expectedProperties), expectedProperties.toString(), "Displayed properties:");

        cleanupAuthenticatedSession();
    }
}
