package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataProviderClass;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.DocumentTransformationEnginePage;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.PerformActionRulePage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.PerformActionRulePage.Mimetype;
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

//NOTE: This tests will fail if alfresco license is not uploaded!
public class TransformationServerTest extends ContextAwareWebTest
{

    //@Autowired
    EditRulesPage editRulesPage;

    //@Autowired
    PerformActionRulePage performActionRulePage;

   // @Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    UploadFileDialog uploadFileDialog;

    @Autowired
    DocumentTransformationEnginePage documentTransformationEnginePage;

    //@Autowired
    UploadContent uploadContent;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private String siteName_C239081 = "C239081Site-" + random;
    private String siteName_C239079 = "C239079Site-" + random;
    private String siteName_C239082 = "C239082Site-" + random;
    private String siteName_C239083 = "C239083Site-" + random;
    private String siteName_C239084 = "C239084Site-" + random;
    private String siteName_C239085 = "C239085Site-" + random;
    private String siteName_C239088 = "C239088Site-" + random;
    private final String description = "description-" + random;
    private String sourceFolderName = "FOR";
    private String targetFolderName = "Transformed";
    private String ruleName = "RuleName_" + random;


    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName + "-First Name", userName + "-Last Name");
        siteService.create(userName, password, domain, siteName_C239081, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239079, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239082, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239083, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239084, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239085, description, SiteService.Visibility.PUBLIC);
        siteService.create(userName, password, domain, siteName_C239088, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239081);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239081);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239079);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239079);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239082);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239082);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239083);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239083);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239084);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239084);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239085);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239085);
        contentService.createFolder(userName, password, sourceFolderName, siteName_C239088);
        contentService.createFolder(userName, password, targetFolderName, siteName_C239088);
        setupAuthenticatedSession(userName, password);
        editRulesPage.defineRule(ruleName, siteName_C239081, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.BITMAP_IMAGE, siteName_C239081, targetFolderName);
        editRulesPage.defineRule(ruleName, siteName_C239079, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_CONTENT);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.ADOBE_PDF_DOCUMENT, siteName_C239079, targetFolderName);
        editRulesPage.defineRule(ruleName, siteName_C239082, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.JPEG_IMAGE, siteName_C239082, targetFolderName);
        editRulesPage.defineRule(ruleName, siteName_C239083, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.GIF_IMAGE, siteName_C239083, targetFolderName);
        editRulesPage.defineRule(ruleName, siteName_C239084, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.PNG_IMAGE, siteName_C239084, targetFolderName);
        editRulesPage.defineRule(ruleName, siteName_C239085, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.TIFF_IMAGE, siteName_C239085, targetFolderName);
        editRulesPage.defineRule(ruleName, siteName_C239088, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(Mimetype.ADOBE_PDF_DOCUMENT, siteName_C239088, targetFolderName);

    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName_C239081);
        siteService.delete(adminUser, adminPassword, siteName_C239079);
        siteService.delete(adminUser, adminPassword, siteName_C239082);
        siteService.delete(adminUser, adminPassword, siteName_C239083);
        siteService.delete(adminUser, adminPassword, siteName_C239084);
        siteService.delete(adminUser, adminPassword, siteName_C239085);
        siteService.delete(adminUser, adminPassword, siteName_C239088);
    }


    @TestRail (id = "C239081")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "ImageTransformToBMP", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToBMP(String imageToTransform)
    {
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


    @TestRail (id = "C239079")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "DocumentTransformToPDF", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToPDF(String documentToTransform)
    {
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


    @TestRail (id = "C239082")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "ImageTransformToJPG", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToJPG(String imageToTransform)
    {
        LOG.info("STEP 1: Go to the folder with rule and upload " + imageToTransform);
        documentLibraryPage.navigate(siteName_C239082).clickOnFolderName(sourceFolderName);
        documentLibraryPage.uploadNewImage(testDataFolder + imageToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform), String.format("File [%s] is not displayed.", imageToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .jpg transformation of " + imageToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239082).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(imageToTransform, ".jpg")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(imageToTransform, ".jpg")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " and verify successful info about " + imageToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform, PerformActionRulePage.Mimetype.JPEG_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform));
    }


    @TestRail (id = "C239083")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "ImageTransformToGIF", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToGIF(String imageToTransform)
    {
        LOG.info("STEP 1: Go to the folder with rule and upload " + imageToTransform);
        documentLibraryPage.navigate(siteName_C239083).clickOnFolderName(sourceFolderName);
        documentLibraryPage.uploadNewImage(testDataFolder + imageToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform), String.format("File [%s] is not displayed.", imageToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .gif transformation of " + imageToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239083).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(imageToTransform, ".gif")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(imageToTransform, ".gif")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " URL and verify successful info about " + imageToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform, PerformActionRulePage.Mimetype.GIF_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform));
    }


    @TestRail (id = "C239084")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "ImageTransformToPNG", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToPNG(String imageToTransform)
    {
        LOG.info("STEP 1: Go to the folder with rule and upload " + imageToTransform);
        documentLibraryPage.navigate(siteName_C239084).clickOnFolderName(sourceFolderName);
        documentLibraryPage.uploadNewImage(testDataFolder + imageToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform), String.format("File [%s] is not displayed.", imageToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .png transformation of " + imageToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239084).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(imageToTransform, ".png")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(imageToTransform, ".png")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " URL and verify successful info about " + imageToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform, PerformActionRulePage.Mimetype.PNG_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform));
    }


    @TestRail (id = "C239085")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "ImageTransformToTIFF", dataProviderClass = DataProviderClass.class)
    public void supportedTypesTransformationToTIFF(String imageToTransform)
    {

        LOG.info("STEP 1: Go to the folder with rule and upload " + imageToTransform);
        documentLibraryPage.navigate(siteName_C239085).clickOnFolderName(sourceFolderName);
        documentLibraryPage.uploadNewImage(testDataFolder + imageToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform), String.format("File [%s] is not displayed.", imageToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .tiff transformation of " + imageToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239085).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(imageToTransform, ".tiff")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(imageToTransform, ".tiff")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " URL and verify successful info about " + imageToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform, PerformActionRulePage.Mimetype.TIFF_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform));
    }


    @TestRail (id = "C239086")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" }, dataProvider = "LargeDocumentTransformToPDF", dataProviderClass = DataProviderClass.class)
    public void transformationFileWithLargeSize(String largeDocumentToTransform)
    {
        LOG.info("STEP 1: Go to the folder with rule and upload " + largeDocumentToTransform);
        documentLibraryPage.navigate(siteName_C239079).clickOnFolderName(sourceFolderName);
        uploadContent.uploadContent(testDataFolder + largeDocumentToTransform);
        assertTrue(documentLibraryPage.isContentNameDisplayed(largeDocumentToTransform), String.format("File [%s] is not displayed", largeDocumentToTransform));

        LOG.info("STEP2: Go to the target (e.g. Transformed space) and verify the .pdf transformation of " + largeDocumentToTransform + " file.");
        documentLibraryPage.navigate(siteName_C239079).clickOnFolderName(targetFolderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(largeDocumentToTransform, ".pdf")), String.format("Transformed file [%s] is not displayed", documentLibraryPage.replaceFileExtension(largeDocumentToTransform, ".pdf")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " and verify successful info about " + largeDocumentToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), largeDocumentToTransform, PerformActionRulePage.Mimetype.ADOBE_PDF_DOCUMENT, userName), String.format("Document %s wasn't transformed!", largeDocumentToTransform));
    }
    
    
    @TestRail (id = "C239088")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "TransformationServer" })
    public void nonSupportedTypesTransformation()
    {
        String nonSupportedDocumentToTransform = "mp4.mp4";

        LOG.info("STEP 1: Go to the folder with rule and upload " + nonSupportedDocumentToTransform);
        documentLibraryPage.navigate(siteName_C239088).clickOnFolderName(sourceFolderName);
        documentLibraryPage.clickUpload().uploadFile(testDataFolder + nonSupportedDocumentToTransform);
        assertTrue(uploadFileDialog.isUploadFailedMessageDisplayed(), "'Upload Failed' message is not displayed but it should.");
        uploadFileDialog.clickClose();
        assertFalse(documentLibraryPage.isContentNameDisplayed(nonSupportedDocumentToTransform), String.format("File [%s] is displayed.", nonSupportedDocumentToTransform));

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the .pdf copy of " + nonSupportedDocumentToTransform + " was not created.");
        documentLibraryPage.navigate(siteName_C239088).clickOnFolderName(targetFolderName);
        assertFalse(documentLibraryPage.isContentNameDisplayed(documentLibraryPage.replaceFileExtension(nonSupportedDocumentToTransform, ".pdf")), String.format("Transformed file [%s] not displayed", documentLibraryPage.replaceFileExtension(nonSupportedDocumentToTransform, ".pdf")));

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " URL and verify successful info about " + nonSupportedDocumentToTransform + " transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        assertFalse(documentTransformationEnginePage.searchTransformation(getBrowser(), nonSupportedDocumentToTransform, Mimetype.ADOBE_PDF_DOCUMENT, userName), String.format("Document %s is present in Transformation Server but it shouldn't.", nonSupportedDocumentToTransform));
    }
}