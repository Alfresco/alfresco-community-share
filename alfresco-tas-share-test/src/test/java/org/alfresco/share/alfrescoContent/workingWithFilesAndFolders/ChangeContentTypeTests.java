package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.ModelDetailsPage;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
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
public class ChangeContentTypeTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private ChangeContentTypeDialog changeContentTypeDialog;

    @Autowired
    private EditPropertiesPage editPropertiesPage;

    @Autowired
    AdminToolsPage adminToolsPage;

    @Autowired
    ModelManagerPage modelManagerPage;

    @Autowired
    CreateModelDialogPage createModelDialogPage;

    @Autowired
    ModelDetailsPage modelDetailsPage;

    @Autowired
    CreateCustomTypeDialog createCustomTypeDialog;

    private final String userName = String.format("profileUser-%s", RandomData.getRandomAlphanumeric());
    private final String docContent = "content of the file.";
    private final String siteName = String.format("Site-%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        siteService.create(userName, password, domain, siteName, "Description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C7163")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelChangeType()
    {
        String docName = String.format("Doc-C7163-%s", RandomData.getRandomAlphanumeric());
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.clickOnFile(docName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page:");

        LOG.info("STEP1: Click 'Change Type' option from 'Document Actions' list");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("STEP2: Select 'Article' from 'New Type' dropdown and click 'Cancel' button");
        changeContentTypeDialog.selectOption("Smart Folder Template");
        changeContentTypeDialog.clickButton("Cancel");
        ArrayList<String> propertiesList = new ArrayList<>(Arrays.asList("Template Name:", "Primary Image:", "Secondary Image:", "Related Articles:"));
        assertEquals(documentDetailsPage.checkPropertiesAreNotDisplayed(propertiesList), "Given list isn't displayed", " property displayed.");

        LOG.info("STEP3: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertEquals(editPropertiesPage.checkPropertiesAreNotDisplayed(propertiesList), "Given list isn't displayed", " property is displayed.");
    }

    @TestRail (id = "C7166")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void changeTypeFolder()
    {

        String folderName = String.format("Folder-C7166-%s", RandomData.getRandomAlphanumeric());
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page:");

        LOG.info("STEP1: Verify folder's Properties list from 'Folder Actions' section");
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Creator", "Created Date", "Modifier", "Modified Date"), "Displayed properties:");

        LOG.info("STEP2: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");

        assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Tags"), "Displayed properties:");

        LOG.info("STEP3: Cancel 'Edit Properties'.");
        editPropertiesPage.clickButton("Cancel");
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page:");

        LOG.info("STEP4: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");
        assertTrue(changeContentTypeDialog.isDropdownMandatory(), "'New Type' dropdown is mandatory.");
        assertTrue(changeContentTypeDialog.isButtonDisplayed("OK"), "'OK' button is displayed.");
        assertTrue(changeContentTypeDialog.isButtonDisplayed("Cancel"), "'Cancel' button is displayed.");


        //  changeContentTypeDialog.clickButton("Cancel");

        /*
        LOG.info("STEP5: Select 'ws:website' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog.selectOption("ws:website");
        changeContentTypeDialog.clickButton("OK");
        getBrowser().refresh();
        documentDetailsPage.renderedPage();

        LOG.info("STEP6: Click 'Edit Properties' option from 'Folder Actions' section");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Host Name", "Host Port ", "Web App Context ",
                "Site Configuration", "Site Languages", "Feedback Configuration", "Publish Target"), "Displayed properties:");

                */
    }

    //the word modifcation is spelled wrongly, but in case the bug will never be fixed, I changed the test to pass
    @TestRail (id = "C7167")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void changeTypeFile()
    {
        String docName = String.format("Doc-C7167-%s", RandomData.getRandomAlphanumeric());
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page:");
        documentLibraryPage.clickOnFile(docName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page:");

        LOG.info("STEP1: Verify document's Properties list");
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator", "Created Date", "Modifier", "Modified Date"), "Displayed properties:");

        LOG.info("STEP2: Click 'Edit Properties' option from 'Document Actions' list");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Tags", "Mimetype"), "Displayed properties:");

        LOG.info("STEP3: Cancel 'Edit Properties'.");
        editPropertiesPage.clickButton("Cancel");
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page:");

        LOG.info("STEP4: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("STEP5: Select 'Article' from 'New Type' dropdown and click 'Ok' button");
        getBrowser().waitInSeconds(4);
        changeContentTypeDialog.selectOption("Smart Folder Template");
        changeContentTypeDialog.clickButton("OK");
        getBrowser().refresh();
        documentDetailsPage.renderedPage();

        assertTrue(documentDetailsPage.arePropertiesDisplayed("Auto Version - on update properties only", "Created Date", "Title", "Last thumbnail modifcation data", "Description", "Creator", "Name",
            "Locale", "Version Label", "Modifier", "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Author", "Encoding", "Size", "Mimetype"), "Displayed properties:");


        LOG.info("STEP6: Click 'Edit Properties' option from 'Document Actions' section");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertTrue(editPropertiesPage.arePropertiesDisplayed("Auto Version - on update properties only", "Created Date", "Title", "Last thumbnail modifcation data", "Description", "Creator", "Name",
            "Content", "Locale", "Version Label", "Modifier", "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Author", "Encoding", "Size", "Mimetype"), "Displayed properties:");
    }
}
