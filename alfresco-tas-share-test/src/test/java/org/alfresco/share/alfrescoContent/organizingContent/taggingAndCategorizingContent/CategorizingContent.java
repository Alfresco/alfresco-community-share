package org.alfresco.share.alfrescoContent.organizingContent.taggingAndCategorizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentAspects;
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

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 9/14/2016.
 */
public class CategorizingContent extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private EditPropertiesDialog editPropertiesDialog;

    @Autowired private SelectDialog selectDialog;

    @Autowired private ContentAspects contentAspect;

    private final String testUser = "testUser" + DataUtil.getUniqueIdentifier();
    private final String siteName = "siteName1" + DataUtil.getUniqueIdentifier();
    private final String folderName = "testFolder" + DataUtil.getUniqueIdentifier();
    private final String docName = "testDoc" + DataUtil.getUniqueIdentifier();
    private final String docWithCategory = "docWithCategory" + DataUtil.getUniqueIdentifier();
    private final String removeCategoryDoc = "removeCategoryDoc" + DataUtil.getUniqueIdentifier();
    private final String editPropAction = "Edit Properties";
    private final String category = "Languages";
    private final String category2 = "Regions";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentService.createDocument(testUser, password, siteName, DocumentType.TEXT_PLAIN, docWithCategory, "Document content");
        contentService.createDocument(testUser, password, siteName, DocumentType.TEXT_PLAIN, removeCategoryDoc, "Document content");
        contentService.createFolder(testUser, password, folderName, siteName);
        List<String> categories = new ArrayList<>();
        contentAspect.addClasifiable(testUser, password, siteName, docName, categories);
        contentAspect.addClasifiable(testUser, password, siteName, folderName, categories);
        categories.add(category);
        contentAspect.addClasifiable(testUser, password, siteName, docWithCategory, categories);
        contentAspect.addClasifiable(testUser, password, siteName, removeCategoryDoc, categories);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C7484")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void addCategoryForFile_NoCategoriesAdded()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the file");
        documentLibraryPage.mouseOverFileName(docName);

        LOG.info("STEP2: Click 'Edit Properties' link");
        documentLibraryPage.clickMore();
        documentLibraryPage.clickDocumentLibraryItemAction(docName, editPropAction, editPropertiesDialog);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), docName), "'Edit Properties' pop-up is displayed");

        LOG.info("STEP3: Click 'Select' button from 'Categories'");
        editPropertiesDialog.clickSelectCategories();

        LOG.info("STEP4: For any item from the left categories picker, click 'Add' icon");
        selectDialog.selectItems(Collections.singletonList(category));
        assertTrue(selectDialog.isItemSelected(category), "Category is displayed in selected categories list.");
        assertFalse(selectDialog.isItemSelectable(category), "In the left categories picker, 'Add' icon isn't displayed next to the added item");

        LOG.info("STEP5: Click 'OK' button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isCategorySelected(category), "Category is displayed in selected categories list.");

        LOG.info("STEP5: Click 'Save' button");
        editPropertiesDialog.clickSave();
        assertTrue(documentLibraryPage.getItemCategories(docName).contains(category), "Added category is displayed in Document Library, as detail below content name");
    }

    @TestRail(id = "C7485")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void addCategoryForFolder_NoCategoriesAdded()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Mouse hover the folder's name link");
        documentLibraryPage.mouseOverContentItem(folderName);

        LOG.info("STEP2: Click 'Edit Properties' link");
        documentLibraryPage.clickMore();
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, editPropAction, editPropertiesDialog);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), folderName), "'Edit Properties' pop-up is displayed");

        LOG.info("STEP3: Click 'Select' button from 'Categories'");
        editPropertiesDialog.clickSelectCategories();

        LOG.info("STEP4: For any item from the left categories picker, click 'Add' icon");
        selectDialog.selectItems(Collections.singletonList(category));
        assertTrue(selectDialog.isItemSelected(category), "Category is displayed in selected categories list.");
        assertFalse(selectDialog.isItemSelectable(category), "In the left categories picker, 'Add' icon isn't displayed next to the added item");

        LOG.info("STEP5: Click 'OK' button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isCategorySelected(category), "Category is displayed in selected categories list.");

        LOG.info("STEP5: Click 'Save' button");
        editPropertiesDialog.clickSave();
        assertTrue(documentLibraryPage.getItemCategories(folderName).contains(category), "Added category is displayed in Document Library, as detail below folder's name");
    }

    @TestRail(id = "C7487")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void addCategory_ContentWithCategory()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the file");
        documentLibraryPage.mouseOverFileName(docWithCategory);

        LOG.info("STEP2: Click 'Edit Properties' link");
        documentLibraryPage.clickDocumentLibraryItemAction(docWithCategory, editPropAction, editPropertiesDialog);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), docWithCategory), "'Edit Properties' pop-up is displayed");
        assertTrue(editPropertiesDialog.isCategorySelected(category), "Precondition category is displayed in selected categories list.");

        LOG.info("STEP3: Click 'Select' button from 'Categories'");
        editPropertiesDialog.clickSelectCategories();

        LOG.info("STEP4: For any item from the left categories picker, click 'Add' icon");
        selectDialog.selectItems(Collections.singletonList(category2));
        assertTrue(selectDialog.isItemSelected(category2), "Category is displayed in selected categories list.");
        assertFalse(selectDialog.isItemSelectable(category2), "In the left categories picker, 'Add' icon isn't displayed next to the added item");

        LOG.info("STEP5: Click 'OK' button");
        selectDialog.clickOk();
        assertTrue(editPropertiesDialog.isCategorySelected(category2), "Category is displayed in selected categories list.");

        LOG.info("STEP5: Click 'Save' button");
        editPropertiesDialog.clickSave();
        assertTrue(documentLibraryPage.getItemCategories(docWithCategory).contains(category), "Precondition category is displayed in Document Library, as detail below content name");
        assertTrue(documentLibraryPage.getItemCategories(docWithCategory).contains(category2), "Added category is displayed in Document Library, as detail below content name");
    }

    @TestRail(id = "C7486")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void removeCategory()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the file");
        documentLibraryPage.mouseOverFileName(removeCategoryDoc);

        LOG.info("STEP2: Click 'Edit Properties' link");
        documentLibraryPage.clickDocumentLibraryItemAction(removeCategoryDoc, editPropAction, editPropertiesDialog);
        assertEquals(editPropertiesDialog.getDialogTitle(), String.format(language.translate("editPropertiesDialog.title"), removeCategoryDoc), "'Edit Properties' pop-up is displayed");
        assertTrue(editPropertiesDialog.isCategorySelected(category), "Precondition category is displayed in selected categories list.");

        LOG.info("STEP3: Click 'Select' button from 'Categories'");
        editPropertiesDialog.clickSelectCategories();
        assertTrue(selectDialog.isItemSelected(category), "Precondition category is displayed in selected categories list.");

        LOG.info("STEP4: For any item from the selected categories list, click 'Remove' icon");
        selectDialog.removeItems(Collections.singletonList(category));
        assertFalse(selectDialog.isItemSelected(category), "The removed category isn't displayed in selected categories list.");
        assertTrue(selectDialog.isItemSelectable(category), "In the left categories picker, 'Add' icon is displayed next to the removed item");

        LOG.info("STEP5: Click 'OK' button");
        selectDialog.clickOk();
        assertFalse(editPropertiesDialog.isCategorySelected(category), "The removed category isn't displayed in 'Categories' section from 'Edit Properties' dialog");

        LOG.info("STEP5: Click 'Save' button");
        editPropertiesDialog.clickSave();
        assertFalse(documentLibraryPage.getItemCategories(removeCategoryDoc).contains(category), "The removed category isn't displayed in Document Library, as detail below content name");
    }
}
