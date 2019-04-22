package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RestrictingMobileAccessTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private AspectsForm aspectsForm;

    @Autowired private EditPropertiesPage editPropertiesPage;

    private String userName = String.format("userName%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String fileName;
    private String fileContent = "testContent";
    private String helpMessage = "This field must contain a number.";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }
    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword,siteName);
    }

    @TestRail(id = "C7111")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void addRestrictableAspect() throws Exception
    {
        fileName = String.format("testFileC7111%s", RandomData.getRandomAlphanumeric());
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        LOG.info("Preconditions: Navigate to Document Details page for the test file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);

        LOG.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage.clickManageAspects();

        LOG.info("Step2: From 'Available to Add' list, click 'Add' icon next to 'Restrictable' aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addAspect("Restrictable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Restrictable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Restrictable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step3: Click 'Apply Changes' and verify the restrictions are placed on the file");
        aspectsForm.clickApplyChangesButton(documentDetailsPage);
        Assert.assertTrue(documentDetailsPage.isAspectDisplayed("Restrictable"), "Restrictable aspect is added");

    }

    @TestRail(id = "C7112")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void editRestrictableProperty()
    {
        fileName = String.format("testFileC7111%s", RandomData.getRandomAlphanumeric());
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAspects.addAspect(userName, password, siteName, fileName, CMISUtil.DocumentAspect.RESTRICTABLE);

        LOG.info("Step1: Click Actions -> Edit Properties option");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);
        documentDetailsPage.clickEditProperties();

        LOG.info("Step2: Click '?' icon and verify the help message");
        editPropertiesPage.clickHelpIconForRestrictableAspect();
        Assert.assertEquals(helpMessage, editPropertiesPage.getHelpMessageForRestrictableAspect());

        LOG.info("Step3: Fill in 'Offline Expires After (hours) and verify the change is saved");

        editPropertiesPage.addOfflineExpiresAfterValue("48");
        editPropertiesPage.clickButton("Save");
        Assert.assertTrue(documentDetailsPage.isRestrictableValueUpdated("48"), "The value for Offline Expires After (hours) has not been updated");
    }

    @TestRail(id = "C7113")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void removeRestrictableProperty() throws Exception
    {
        fileName = String.format("testFileC7111%s", RandomData.getRandomAlphanumeric());
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentAspects.addAspect(userName, password, siteName, fileName, CMISUtil.DocumentAspect.RESTRICTABLE);

        LOG.info("Step1: Click Actions -> Manage Aspects option");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);
        documentDetailsPage.clickManageAspects();

        LOG.info("Step2: Click 'Remove' icon next to 'Restrictable' aspect");
        aspectsForm.removeAspect("Restrictable");

        LOG.info("Step3: Click 'Apply changes' button and verify the 'Restrictable' property is removed from 'Properties' section");
        aspectsForm.clickApplyChangesButton(documentDetailsPage);
        Assert.assertFalse(documentDetailsPage.isAspectNotDisplayed("Restrictable"), "Restrictable aspect is removed");
    }

}
