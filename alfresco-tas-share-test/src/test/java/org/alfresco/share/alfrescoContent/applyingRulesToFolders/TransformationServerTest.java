package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.common.DataProviderClass;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.DocumentTransformationEnginePage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.PerformActionRulePage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

//NOTE: This tests will fail if alfresco license is not uploaded!
public class TransformationServerTest extends ContextAwareWebTest {

    @Autowired
    EditRulesPage editRulesPage;

    @Autowired
    PerformActionRulePage performActionRulePage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentTransformationEnginePage documentTransformationEnginePage;

    @Autowired
    UploadContent uploadContent;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private String siteName_C239081 = "C239081Site-" + random;
    private String siteName_C239079 = "C239079Site-" + random;
    private String siteName_C239082 = "C239082Site-" + random;
    private final String description = "description-" + random;
    private String sourceFolderName = "FOR";
    private String targetFolderName = "Transformed";
    private String ruleName_C239081 = "C239081RuleName_" + random;
    private String ruleName_C239079 = "C239079RuleName_" + random;


    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName + "-First Name", userName + "-Last Name");
        siteService.create(userName, password, domain, siteName_C239081, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239079, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239082, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239081);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239081);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239079);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239079);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239082);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239082);
        setupAuthenticatedSession(userName, password);
        editRulesPage.defineRule(ruleName_C239081, siteName_C239081, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.BITMAP_IMAGE, siteName_C239081, targetFolderName);
        editRulesPage.defineRule(ruleName_C239079, siteName_C239079, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_CONTENT);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.ADOBE_PDF_DOCUMENT, siteName_C239079, targetFolderName);
        editRulesPage.defineRule(ruleName_C239079, siteName_C239082, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.JPEG_IMAGE, siteName_C239082, targetFolderName);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName_C239081);
        siteService.delete(adminUser, adminPassword, siteName_C239079);
    }


    @TestRail(id = "C239081")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer"}, dataProvider = "ImageTransformToBMP", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToBMP(String imageToTransform) {

        LOG.info("STEP 1: Go to the folder with rule and upload " + imageToTransform);
        documentLibraryPage.navigate(siteName_C239081).clickOnFolderName(sourceFolderName);
        documentLibraryPage.uploadNewImage(testDataFolder + imageToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform), String.format("File [%s] is not displayed.", imageToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .bmp transformation of " + imageToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239081).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(imageToTransform, ".bmp")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(imageToTransform, ".bmp")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " and verify successful info about " + imageToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform, PerformActionRulePage.Mimetype.BITMAP_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform));
    }


    @TestRail(id = "C239079")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer"}, dataProvider = "DocumentTransformToPDF", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToPDF(String documentToTransform) {

        LOG.info("STEP 1: Go to the folder with rule and upload " + documentToTransform);
        documentLibraryPage.navigate(siteName_C239079).clickOnFolderName(sourceFolderName);
        uploadContent.uploadContent(testDataFolder + documentToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentToTransform), String.format("File [%s] is not displayed", documentToTransform));

        LOG.info("STEP2: Go to the target (e.g. Transformed space) and verify the .pdf transformation of " + documentToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239079).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(documentToTransform, ".pdf")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(documentToTransform, ".pdf")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " and verify successful info about " + documentToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), documentToTransform, PerformActionRulePage.Mimetype.ADOBE_PDF_DOCUMENT, userName), String.format("Document %s wasn't transformed!", documentToTransform));
    }


    @TestRail(id = "C239082")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer"}, dataProvider = "ImageTransformToJPG", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToJPG(String imageToTransform) {

        LOG.info("STEP 1: Go to the folder with rule and upload " + imageToTransform);
        documentLibraryPage.navigate(siteName_C239082).clickOnFolderName(sourceFolderName);
        documentLibraryPage.uploadNewImage(String.format(testDataFolder + imageToTransform));
        assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform), String.format("File [%s] is not displayed.", imageToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .jpg transformation of " + imageToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239082).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(imageToTransform, ".jpg")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(imageToTransform, ".jpg")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " and verify successful info about " + imageToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform, PerformActionRulePage.Mimetype.JPEG_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform));
    }
}