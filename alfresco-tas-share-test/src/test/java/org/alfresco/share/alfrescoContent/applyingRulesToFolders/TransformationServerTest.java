package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.DocumentTransformationEnginePage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.EditRulesPage;
import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.PerformActionRulePage;
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


public class TransformationServerTest extends ContextAwareWebTest {

    @Autowired
    EditRulesPage editRulesPage;

    @Autowired
    PerformActionRulePage performActionRulePage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentTransformationEnginePage documentTransformationEnginePage;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "user-" + random;
    private String siteName = "Site-" + random;
    private final String description = "description-" + random;
    private String sourceFolderName = "FOR";
    private String targetFolderName = "Transformed";
    private String ruleName = "RuleName_" + random;
    private String[] imageToTransform = {"Lighthouse.jpg", "png_noBackground_file.png", "tiff_file.tif", "gif_animated.gif"};

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName + "-First Name", userName + "-Last Name");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, sourceFolderName, siteName);
        contentService.createFolder(userName, password, targetFolderName, siteName);
        setupAuthenticatedSession(userName, password);
        editRulesPage.defineRule(ruleName, siteName, sourceFolderName, EditRulesPage.WhenRule.itemsCreatedOrEnterFolder, EditRulesPage.IfAllCriteriaAreMetRule.ALL_ITEMS, EditRulesPage.PerformActionList.TRANSFORM_AND_COPY_IMAGE);
        performActionRulePage.transformAndCopy(PerformActionRulePage.Mimetype.BITMAP_IMAGE, siteName, targetFolderName);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail(id = "C239081")
    @Test(groups = {TestGroup.SANITY, TestGroup.CONTENT})
    public void supportedTypesTransformationToBMP() throws InterruptedException {

        LOG.info("STEP 1: Go to the folder with rule and upload document.");
        documentLibraryPage.navigate(siteName).clickOnFolderName(sourceFolderName);
        for (int i = 0; i <= 3; i++) {
            LOG.info("STEP: Upload " + imageToTransform[i]);
            documentLibraryPage.uploadNewImage(String.format(testDataFolder + imageToTransform[i]));
            assertTrue(documentLibraryPage.isContentNameDisplayed(imageToTransform[i]), String.format("File [%s] is not displayed", imageToTransform[i]));
        }

        LOG.info("STEP 2: Go to the target (e.g. Transformed space) and verify the *bmp copy of the file.");
        documentLibraryPage.navigate(siteName).clickOnFolderName(targetFolderName);
        for (int i = 0; i <= 3; i++) {
            LOG.info("STEP: Check transformation of " + imageToTransform[i]);
            assertTrue(documentLibraryPage.isContentNameDisplayed(String.format((imageToTransform[i].substring(0, imageToTransform[i].length() - 3) + "bmp"))), String.format("Transformed file [%s] is not displayed", imageToTransform[i].substring(0, imageToTransform[i].length() - 3) + "bmp"));
        }

        LOG.info("STEP 3: Open " + properties.getTransformationServerUrl() + " and verify successful info about your transformation.");
        navigate(properties.getTransformationServerUrl().toString());
        for (int i = 0; i <= 3; i++) {
            LOG.info("STEP: Check in DTE server after " + imageToTransform[i]);
            assertTrue(documentTransformationEnginePage.searchTransformation(getBrowser(), imageToTransform[i], PerformActionRulePage.Mimetype.BITMAP_IMAGE, userName), String.format("Document %s wasn't transformed!", imageToTransform[i]));
        }
    }
}