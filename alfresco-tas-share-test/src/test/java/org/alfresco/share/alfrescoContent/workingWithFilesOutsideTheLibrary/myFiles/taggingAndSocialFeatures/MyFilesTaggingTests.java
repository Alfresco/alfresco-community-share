package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles.taggingAndSocialFeatures;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesTaggingTests extends ContextAwareWebTest {

	@Autowired
	private MyFilesPage myFilesPage;
	@Autowired
	private SiteDashboardPage sitePage;
	@Autowired
	private EditPropertiesDialog editPropertiesDialog;
	@Autowired
	private NewContentDialog newContentDialog;
	@Autowired
	private SelectDialog selectDialog;
	@Autowired
	private UploadContent uploadContent;
	@Autowired
	private DocumentLibraryPage documentLibraryPage;

	private String user;
	private final String testFile = DataUtil.getUniqueIdentifier() + "testFile.txt";
	private final String testFilePath = testDataFolder + testFile;
	private final String tagName = "tag-" + DataUtil.getUniqueIdentifier();
	private final String tagName2 = "tag2-" + DataUtil.getUniqueIdentifier();
	private final String folderName = "testFolder" + DataUtil.getUniqueIdentifier();

	@BeforeMethod(alwaysRun = true)
	public void setupTest() {

		user = "user" + DataUtil.getUniqueIdentifier();
		userService.create(adminUser, adminPassword, user, password, user + domain, user, user);

		LOG.info("Precondition: Login as user, navigate to My Files page and upload a file.");
		setupAuthenticatedSession(user, password);
		sitePage.clickMyFilesLink();
		Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
		uploadContent.uploadContent(testFilePath);
		assertTrue(myFilesPage.isContentNameDisplayed(testFile),
				String.format("The file [%s] is not present", testFile));
		myFilesPage.renderedPage();
	}

	@TestRail(id = "C7861")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
	public void myFilesCreateFileTag() {

		LOG.info("STEP1: Hover over the text \"No Tags\" from the file.");
		myFilesPage.mouseOverNoTags(testFile);
		assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> \"Edit Tag\" icon is displayed");

		LOG.info("STEP2: Click \"Edit Tag\" icon");
		myFilesPage.clickEditTagIcon(testFile);
		assertTrue(myFilesPage.isEditTagInputFieldDisplayed(),
				testFile + " -> Edit tag text input field is displayed.");

		LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
		myFilesPage.typeTagName(tagName);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));	
		ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
		assertEquals(myFilesPage.getTags(testFile), tagsList.toString(), testFile + " -> tags=");
	}

	@TestRail(id = "C7862")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
	public void myFilesCreateFolderTag() {

		LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
		myFilesPage.renderedPage();
		myFilesPage.clickCreateButton();
		myFilesPage.clickFolderLink();
		newContentDialog.fillInNameField(folderName);
		newContentDialog.clickSaveButton();
	    assertTrue(myFilesPage.isContentNameDisplayed(folderName),
				folderName + " displayed in My Files documents list.");

		LOG.info("STEP1: Hover over the text \"No Tags\" from the folder.");
		myFilesPage.mouseOverNoTags(folderName);
		assertTrue(myFilesPage.isEditTagIconDisplayed(folderName), folderName + " -> \"Edit Tag\" icon is displayed");

		LOG.info("STEP2: Click \"Edit Tag\" icon");
		myFilesPage.clickEditTagIcon(folderName);
		assertTrue(myFilesPage.isEditTagInputFieldDisplayed(),
				folderName + " -> Edit tag text input field is displayed.");

		LOG.info("STEP3: Type any tag name in the input field and click \"Save\" link");
		myFilesPage.typeTagName(tagName);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		myFilesPage.renderedPage();
		ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
		assertEquals(myFilesPage.getTags(folderName), tagsList.toString(), folderName + " -> tags=");
	}

	@TestRail(id = "C7873")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
	public void myFilesAddExistingTag() {

		myFilesPage.clickCreateButton();
		myFilesPage.clickFolderLink();
		newContentDialog.fillInNameField(folderName);
		newContentDialog.clickSaveButton();		 
		assertTrue(myFilesPage.isContentNameDisplayed(folderName),
				folderName + " displayed in My Files documents list.");

		LOG.info("Precondition: Add a tag to the file and folder.");
		myFilesPage.renderedPage();
		myFilesPage.mouseOverNoTags(testFile);
		myFilesPage.clickEditTagIcon(testFile);
		myFilesPage.typeTagName(tagName);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		myFilesPage.navigateByMenuBar();
		myFilesPage.mouseOverNoTags(folderName);
		myFilesPage.clickEditTagIcon(folderName);
		myFilesPage.typeTagName(tagName2);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		myFilesPage.renderedPage();

		LOG.info("STEP1: Click \"Edit Properties\" option");
		myFilesPage.clickDocumentLibraryItemAction(testFile, "Edit Properties", editPropertiesDialog);
		assertEquals(editPropertiesDialog.getDialogTitle(),
				String.format(language.translate("editPropertiesDialog.title"), testFile), "Displayed dialog=");
		assertTrue(editPropertiesDialog.isSelectTagsButtonDisplayed(), "'Select' tag button is displayed.");

		LOG.info("STEP2: Click \"Select\" button");
		editPropertiesDialog.clickSelectTags();
		assertEquals(selectDialog.getDialogTitle(), language.translate("selectDialog.title"), "Displayed dialog=");

		LOG.info("STEP3: Pick any tag from the available tags list and click \"Add\"");
		selectDialog.typeTag(tagName2.toLowerCase());
		selectDialog.selectItems(Collections.singletonList(tagName2.toLowerCase()));
		assertTrue(selectDialog.isItemSelected(tagName2.toLowerCase()),
				tagName2.toLowerCase() + " is displayed in selected categories list.");
		assertFalse(selectDialog.isItemSelectable(tagName2.toLowerCase()),
				tagName2.toLowerCase() + " -> 'Add' icon isn't displayed.");

		LOG.info("STEP4: Click \"Ok\" button");
		selectDialog.clickOk();
		assertTrue(editPropertiesDialog.isTagSelected(tagName.toLowerCase()), "'Tags:' section=");

		LOG.info("STEP5: Click \"Save\" button");
		editPropertiesDialog.clickSave();
		ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tagName.toLowerCase(), tagName2.toLowerCase()));
		assertEquals(myFilesPage.getTags(testFile), tagsList.toString(), testFile + " -> tags=");
	}

	@TestRail(id = "C7885")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
	public void myFilesEditTagFile() {

		LOG.info("Precondition: Add a tag to the file.");
		myFilesPage.renderedPage();
		myFilesPage.mouseOverNoTags(testFile);
		myFilesPage.clickEditTagIcon(testFile);
		myFilesPage.typeTagName(tagName);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));

		LOG.info("STEP1: Hover over the tag(s) from the content");
		myFilesPage.renderedPage();
		myFilesPage.mouseOverTags(testFile);
		assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> 'Edit Tag' icon is displayed.");

		LOG.info("STEP2: Click \"Edit Tag\" icon");
		myFilesPage.clickEditTagIcon(testFile);
		assertTrue(myFilesPage.isEditTagInputFieldDisplayed(),
				testFile + " -> 'Edit Tag' text input field is displayed.");

		LOG.info("STEP3: Click on any tag and type a valid tag name");
		myFilesPage.editTag(testFile, tagName.toLowerCase(), tagName2);

		LOG.info("STEP4: Click \"Save\" link and verify the content tags");
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		assertEquals(myFilesPage.getTags(testFile), Collections.singletonList(tagName2.toLowerCase()).toString(),
				tagName.toLowerCase() + " is updated with value:");
	}

	@TestRail(id = "C7886")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
	public void myFilesRemoveTag() {

		LOG.info("Precondition: Add a tag to the file.");
		myFilesPage.renderedPage();
		myFilesPage.mouseOverNoTags(testFile);
		myFilesPage.clickEditTagIcon(testFile);
		myFilesPage.typeTagName(tagName);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));	

		LOG.info("STEP1: Hover over the tag from the folder");
		myFilesPage.mouseOverTags(testFile);
		assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> 'Edit Tag' icon is displayed.");

		LOG.info("STEP2: Click \"Edit Tags\" icon");
		myFilesPage.clickEditTagIcon(testFile);
		assertTrue(myFilesPage.isEditTagInputFieldDisplayed(),
				testFile + " -> 'Edit Tag' text input field is displayed.");

		LOG.info("STEP3: Hover over the tag and click 'Remove' icon");
     	assertEquals(myFilesPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");	

		LOG.info("STEP4: Click 'Save' link");
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		if (!myFilesPage.isNoTagsTextDisplayed(testFile))
			myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		assertTrue(myFilesPage.isNoTagsTextDisplayed(testFile), testFile + " -> " + tagName + " is removed.");
	}

	@TestRail(id = "C7895")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
	public void myFilesUpdateTag() {

		LOG.info("Precondition: Add a tag to the file.");
		myFilesPage.renderedPage();
		myFilesPage.mouseOverNoTags(testFile);
		myFilesPage.clickEditTagIcon(testFile);
		myFilesPage.typeTagName(tagName);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));		

		LOG.info("STEP1: Hover over the tag from the file.");
		myFilesPage.mouseOverTags(testFile);
		assertTrue(myFilesPage.isEditTagIconDisplayed(testFile), testFile + " -> 'Edit Tag' icon is displayed.");

		LOG.info("STEP2: Click \"Edit Tags\" icon");
		myFilesPage.clickEditTagIcon(testFile);
		assertTrue(myFilesPage.isEditTagInputFieldDisplayed(),
				testFile + " -> 'Edit Tag' text input field is displayed.");

		LOG.info("STEP3: Hover over the tag. Click 'Remove' icon. Click 'Save' link");
		assertEquals(myFilesPage.removeTag(tagName.toLowerCase()), tagName.toLowerCase(), "Removed ");
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		assertTrue(myFilesPage.isNoTagsTextDisplayed(testFile), testFile + " -> " + tagName + " is removed.");

		LOG.info("STEP4: Click \"Edit Tag\" icon");
		myFilesPage.mouseOverNoTags(testFile);
		myFilesPage.clickEditTagIcon(testFile);

		LOG.info("STEP5: Type any tag name in the input field. Click \"Save\" link");
		myFilesPage.typeTagName(tagName2);
		myFilesPage.clickEditTagLink(language.translate("documentLibrary.tag.link.save"));
		myFilesPage.renderedPage();
		assertEquals(myFilesPage.getTags(testFile), Collections.singletonList(tagName2.toLowerCase()).toString(),
				testFile + " -> tags=");
	}
}
