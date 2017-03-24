package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UpdateFileWithContentFromComputerTests extends ContextAwareWebTest {

	@Autowired private DocumentLibraryPage documentLibraryPage;

	@Autowired
	EditPropertiesPage editPropertiesPage;

	@Autowired private UploadFileDialog uploadFileDialog;

	@Autowired
	private UploadContent uploadContent;

	@Autowired private DocumentDetailsPage documentDetailsPage;

	private String userName;
	private String siteName;
	private String testFileName;
	private String fileContent;
	private String newVersionFilePath;
	private String newVersionFileName;

	@BeforeMethod(alwaysRun = true)
	public void setupTest() {
		userName = "User" + DataUtil.getUniqueIdentifier();
		siteName = "SiteName" + DataUtil.getUniqueIdentifier();
		testFileName = "testFileC7074.txt";
		fileContent = "Test Content C7074";
		newVersionFileName = "EditedTestFileC7074.txt";

		newVersionFilePath = testDataFolder + newVersionFileName;
		userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
		siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
		setupAuthenticatedSession(userName, password);
		contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, testFileName, fileContent);
	}

	@TestRail(id = "C7074")
	@Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
	public void uploadFileUsingUploadNewVersion() {
		logger.info("Preconditions: Navigate to Document Library page for the test site");
		documentLibraryPage.navigate(siteName);

		logger.info("Steps1: Click 'Upload new version' action for the test file");
		documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Upload New Version", uploadFileDialog);

		logger.info(
				"Step2 - Click on 'Select files to upload' button, browse to the new version of the test file and select it. Click 'Upload' button.");
		uploadContent.updateDocumentVersion(newVersionFilePath, "New Version", UploadContent.Version.Major);

		logger.info("Step3 - Check the new title of the file displayed in Document Library.");
		documentLibraryPage.navigate(siteName);
		getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath("//h3[@class='filename']//span[contains(@id, 'alf-')]//a[text()= '" + newVersionFileName + "']"), 6);
		assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFileName),
				String.format("The file [%s] is not present", newVersionFileName));
		logger.info("Steps4,5: Click on the file and check the version and content are updated.");
		documentLibraryPage.clickOnFile(newVersionFileName);
		assertEquals(documentDetailsPage.getContentText(), "Edited content C7074",
				String.format("Contents of %s are wrong.", newVersionFileName));
		assertEquals(documentDetailsPage.getFileVersion(), "2.0",
				String.format("Version of %s is wrong.", newVersionFileName));
	}
}
