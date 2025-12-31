package org.alfresco.adminconsole;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.adminconsole.repositoryservices.SearchServicePages;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.alfresco.common.Utils.testDataFolder;

/**
 * @author Rakesh.Ghosal
 */
@Slf4j
public class SearchServicePageElasticTests extends BaseTest {
    private SearchServicePages searchServicePages;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();
    private UploadContent uploadContent;
    private SharedFilesPage sharedFilesPage;
    private DocumentLibraryPage2 documentLibraryPage;
    private DeleteDialog deleteDialog;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteModel.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());
        uploadContent = new UploadContent(webDriver);
        sharedFilesPage = new SharedFilesPage(webDriver);
        searchServicePages = new SearchServicePages(webDriver);
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
    }

    private void refreshAndClickServiceStatus(int waitTimeInSeconds) {
        searchServicePages.navigate();
        searchServicePages.refreshPageBySeconds(waitTimeInSeconds);
        searchServicePages.clickServiceStatus();
    }

    private void uploadDocument(String userName, String siteName, String docName, String docContent) {
        String password = "password";
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateESServiceStatusIndexedDocumentCount() {
        log.info("STEP1: Navigate to Search Service");
        log.info("STEP2: Click on Service Status tab and store the already indexed document count");
        refreshAndClickServiceStatus(3);
        int indexedDocument = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        log.info("Indexed document count is {}", indexedDocument);
        String uniqueIdentifier = RandomData.getRandomAlphanumeric();
        log.info("STEP3: Uploading a random document");
        uploadDocument(user1.get().getUsername(), siteModel.get().getId(),
            "TestDoc1-" + uniqueIdentifier, "content of the file.");
        log.info("STEP4: Navigate to Search Service Status tab and check the updated indexed document count");
        refreshAndClickServiceStatus(3);
        int updatedIndexedDocument = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        log.info("Updated Indexed document count is {}", updatedIndexedDocument);
        //Validate if the indexed document count is increased by 1 or not
        Assert.assertEquals(updatedIndexedDocument, indexedDocument + 1);
        log.info("STEP5: Uploading another random document");
        uploadDocument(user1.get().getUsername(), siteModel.get().getId(),
            "Document2-" + uniqueIdentifier, "content of the file.");
        refreshAndClickServiceStatus(3);
        updatedIndexedDocument = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        log.info("Updated Indexed document count is {}", updatedIndexedDocument);
        Assert.assertEquals(updatedIndexedDocument, indexedDocument + 2);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateESServiceStatusIndexableDocumentCount() {
        log.info("STEP1: Navigate to Search Service");
        log.info("STEP2: Click on Service Status tab and store the indexable document count");
        refreshAndClickServiceStatus(3);
        int indexableDocument = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        log.info("Indexable document count is {}", indexableDocument);
        String uniqueIdentifier = RandomData.getRandomAlphanumeric();
        log.info("STEP3: Uploading a random document");
        uploadDocument(user1.get().getUsername(), siteModel.get().getId(),
            "TestDoc1-" + uniqueIdentifier, "content of the file.");
        log.info("STEP4: Navigate to Search Service Status tab and check the updated indexable document count");
        refreshAndClickServiceStatus(3);
        int updatedIndexableDocument = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        log.info("Updated indexable document count is {}", updatedIndexableDocument);
        //Validate if the indexed document count is increased by 1 or not
        Assert.assertEquals(updatedIndexableDocument, indexableDocument + 1);
        log.info("STEP5: Uploading another random document");
        uploadDocument(user1.get().getUsername(), siteModel.get().getId(),
            "Document2-" + uniqueIdentifier, "content of the file.");
        refreshAndClickServiceStatus(3);
        updatedIndexableDocument = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        log.info("Updated Indexable document count is {}", updatedIndexableDocument);
        Assert.assertEquals(updatedIndexableDocument, indexableDocument + 2);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateESServiceStatusContentIndexingSuccessCount() {
        log.info("STEP1: Navigate to Search Service");
        log.info("STEP2: Click on Service Status tab and store the Content Indexing Success count");
        refreshAndClickServiceStatus(3);
        int contentIndexSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        log.info("Content Indexing Success count is {}", contentIndexSuccessCount);
        String uniqueIdentifier = RandomData.getRandomAlphanumeric();
        log.info("STEP3: Uploading a random document");
        uploadDocument(user1.get().getUsername(), siteModel.get().getId(),
            "TestDoc1-" + uniqueIdentifier, "content of the file.");
        log.info("STEP4: Navigate to Search Service Status tab and check the content indexing success count");
        refreshAndClickServiceStatus(3);
        int updatedContentIndexedSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        log.info("Updated Content Indexed Success count is {}", updatedContentIndexedSuccessCount);
        //Validate if the indexed document count is increased by 1 or not
        Assert.assertEquals(updatedContentIndexedSuccessCount, contentIndexSuccessCount + 1);
        log.info("STEP5: Uploading another random document");
        uploadDocument(user1.get().getUsername(), siteModel.get().getId(),
            "Document2-" + uniqueIdentifier, "content of the file.");
        refreshAndClickServiceStatus(3);
        updatedContentIndexedSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        log.info("Updated Content Indexed Success count is {}", updatedContentIndexedSuccessCount);
        Assert.assertEquals(updatedContentIndexedSuccessCount, contentIndexSuccessCount + 2);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateHealthCheckCompletedWithNoIssues() {
        refreshAndClickServiceStatus(3);
        searchServicePages.clickStartHealthCheckButton();
        searchServicePages.switchToStartHealthCheckPopUp();
        searchServicePages.isStartHealthCheckPopUpDisplayed();
        searchServicePages.closeStartHealthCheckPopUp();
        refreshAndClickServiceStatus(3);
        String healthCheckStatus = searchServicePages.getHealthCheckStatus();
        Assert.assertEquals(healthCheckStatus, "Completed with 0 issues");
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateHealthCheckStartEndTimeChanges() {
        refreshAndClickServiceStatus(3);
        String healthCheckStartTime = searchServicePages.getHealthCheckStartTime();
        String healthCheckEndTime = searchServicePages.getHealthCheckEndTime();
        searchServicePages.clickStartHealthCheckButton();
        searchServicePages.switchToStartHealthCheckPopUp();
        searchServicePages.isStartHealthCheckPopUpDisplayed();
        searchServicePages.closeStartHealthCheckPopUp();
        refreshAndClickServiceStatus(3);
        String updatedHealthCheckStartTime = searchServicePages.getHealthCheckStartTime();
        String updatedHealthCheckEndTime = searchServicePages.getHealthCheckEndTime();
        if (!healthCheckStartTime.trim().isEmpty()) {
            Assert.assertTrue(DataUtil.isFirstDateAfterSecond
                    (updatedHealthCheckStartTime, healthCheckStartTime, "dd MMM yyyy, HH:mm:ss"),
                "Health Check Start Time didn't update after running a new Health Check");
        } else {
            Assert.assertTrue(DataUtil.isValidDateTime(updatedHealthCheckStartTime, "dd MMM yyyy, HH:mm:ss"),
                "Health Check Start Time is still empty after running a new Health Check");
        }
        if (!healthCheckEndTime.trim().isEmpty()) {
            Assert.assertTrue(DataUtil.isFirstDateAfterSecond
                    (updatedHealthCheckEndTime, healthCheckEndTime, "dd MMM yyyy, HH:mm:ss"),
                "Health Check Start Time didn't update after running a new Health Check");
        } else {
            Assert.assertTrue(DataUtil.isValidDateTime(updatedHealthCheckEndTime, "dd MMM yyyy, HH:mm:ss"),
                "Health Check Start Time is still empty after running a new Health Check");
        }
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateSearchServicePageFailureCount() {
        refreshAndClickServiceStatus(3);
        int contentIndexFailureCount = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        authenticateUsingLoginPage(user1.get());
        String testFilePath = testDataFolder + "corrupted_test_1.docx";
        log.info("Precondition: Navigate to Shared Files page.");
        sharedFilesPage.navigate();
        log.info("STEP1: Upload a file.");
        uploadContent.uploadContent(testFilePath);
        refreshAndClickServiceStatus(10);
        int updatedContentIndexFailureCount = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        if(updatedContentIndexFailureCount != contentIndexFailureCount+1) {
            refreshAndClickServiceStatus(10);
        }
        Assert.assertEquals(updatedContentIndexFailureCount, contentIndexFailureCount + 1);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateSearchServicePageFailureCountAfterDelete() {
        String corruptedFileName = "corrupted_test_2.docx";
        refreshAndClickServiceStatus(3);
        int contentIndexFailureCount = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        authenticateUsingLoginPage(user1.get());
        String testFilePath = testDataFolder + corruptedFileName;
        sharedFilesPage.navigate();
        uploadContent.uploadContent(testFilePath);
        refreshAndClickServiceStatus(10);
        int updatedContentIndexFailureCount = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        if (updatedContentIndexFailureCount != contentIndexFailureCount + 1) {
            refreshAndClickServiceStatus(10);
        }
        Assert.assertEquals(updatedContentIndexFailureCount, contentIndexFailureCount + 1);
        sharedFilesPage.navigate();
        sharedFilesPage.selectItemAction(corruptedFileName, ItemActions.DELETE_DOCUMENT);
        deleteDialog.assertConfirmDeleteMessageForContentEqualsTo(corruptedFileName)
            .assertDeleteButtonIsDisplayed().assertCancelButtonIsDisplayed();
        deleteDialog.confirmDeletion();
        sharedFilesPage.navigateByMenuBar().assertFileIsNotDisplayed(corruptedFileName);
        refreshAndClickServiceStatus(10);
        int updatedContentIndexFailureCountAfterDelete = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        if (updatedContentIndexFailureCountAfterDelete != updatedContentIndexFailureCount - 1) {
            refreshAndClickServiceStatus(10);
        }
        Assert.assertEquals(updatedContentIndexFailureCountAfterDelete, updatedContentIndexFailureCount - 1);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateSearchServicePageSuccessCountAfterDelete() {
        String testFile = "testFile3.txt";
        refreshAndClickServiceStatus(3);
        int indexedDocumentCount = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        int indexableDocumentCount = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        int contentIndexSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        authenticateUsingLoginPage(user1.get());
        String testFilePath = testDataFolder + testFile;
        sharedFilesPage.navigate();
        uploadContent.uploadContent(testFilePath);
        refreshAndClickServiceStatus(6);
        int updatedIndexedDocumentCount = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        int updatedIndexableDocumentCount = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        int updatedContentIndexSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        if (updatedIndexedDocumentCount != indexedDocumentCount + 1) {
            refreshAndClickServiceStatus(3);
        }
        Assert.assertEquals(indexedDocumentCount + 1, updatedIndexedDocumentCount);
        Assert.assertEquals(indexableDocumentCount + 1, updatedIndexableDocumentCount);
        Assert.assertEquals(contentIndexSuccessCount + 1, updatedContentIndexSuccessCount);
        sharedFilesPage.navigate();
        sharedFilesPage.selectItemAction(testFile, ItemActions.DELETE_DOCUMENT);
        deleteDialog.assertConfirmDeleteMessageForContentEqualsTo(testFile).assertDeleteButtonIsDisplayed().assertCancelButtonIsDisplayed();
        deleteDialog.confirmDeletion();
        sharedFilesPage.navigateByMenuBar().assertFileIsNotDisplayed(testFile);
        refreshAndClickServiceStatus(3);
        int updatedIndexedDocumentCountAfterDelete = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        int updatedIndexableDocumentCountAfterDelete = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        int updatedContentIndexSuccessCountAfterDelete = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        if (updatedIndexedDocumentCountAfterDelete != updatedIndexedDocumentCount - 1) {
            refreshAndClickServiceStatus(6);
        }
        Assert.assertEquals(updatedIndexedDocumentCount - 1, updatedIndexedDocumentCountAfterDelete);
        Assert.assertEquals(updatedIndexableDocumentCount - 1, updatedIndexableDocumentCountAfterDelete);
        Assert.assertEquals(updatedContentIndexSuccessCount - 1, updatedContentIndexSuccessCountAfterDelete);
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateHealthCheckShowsFailure() {
        String corruptedFileName = "corrupted_test_3.docx";
        refreshAndClickServiceStatus(3);
        int contentIndexFailureCount = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        authenticateUsingLoginPage(user1.get());
        String testFilePath = testDataFolder + corruptedFileName;
        sharedFilesPage.navigate();
        uploadContent.uploadContent(testFilePath);
        refreshAndClickServiceStatus(10);
        int updatedContentIndexFailureCount = Integer.parseInt(searchServicePages.getContentIndexingFailureCount());
        if (updatedContentIndexFailureCount != contentIndexFailureCount + 1) {
            refreshAndClickServiceStatus(10);
        }
        searchServicePages.clickStartHealthCheckButton();
        searchServicePages.switchToStartHealthCheckPopUp();
        searchServicePages.isStartHealthCheckPopUpDisplayed();
        searchServicePages.closeStartHealthCheckPopUp();
        refreshAndClickServiceStatus(3);
        String healthCheckStatus = searchServicePages.getHealthCheckStatus();
        Assert.assertEquals(healthCheckStatus, "Completed with "+updatedContentIndexFailureCount+" issues");
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SEARCH, "es_search_test"})
    public void validateSearchServicePageContentIndexedCountForBlankText() {
        String testFile = "testFile1.txt";
        refreshAndClickServiceStatus(3);
        int indexedDocumentCount = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        int indexableDocumentCount = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        int contentIndexSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        authenticateUsingLoginPage(user1.get());
        String testFilePath = testDataFolder + testFile;
        sharedFilesPage.navigate();
        uploadContent.uploadContent(testFilePath);
        refreshAndClickServiceStatus(6);
        int updatedIndexedDocumentCount = Integer.parseInt(searchServicePages.getIndexedDocumentCount());
        int updatedIndexableDocumentCount = Integer.parseInt(searchServicePages.getIndexableDocumentCount());
        int updatedContentIndexSuccessCount = Integer.parseInt(searchServicePages.getContentIndexingSuccessCount());
        if (updatedIndexedDocumentCount != indexedDocumentCount + 1) {
            refreshAndClickServiceStatus(3);
        }
        Assert.assertEquals(indexedDocumentCount + 1, updatedIndexedDocumentCount);
        Assert.assertEquals(indexableDocumentCount + 1, updatedIndexableDocumentCount);
        Assert.assertEquals(contentIndexSuccessCount, updatedContentIndexSuccessCount);
    }
}