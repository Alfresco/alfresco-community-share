package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RestrictingMobileAccessTests extends ContextAwareWebTest
{

    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DocumentDetailsPage documentDetailsPage;

    @Autowired private AspectsForm aspectsForm;

    @Autowired private EditPropertiesPage editPropertiesPage;

    private String userName;
    private String siteName;
    private String fileName;
    private String fileContent = "testContent";
    private String helpMessage = "This field must contain a number.";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userName = "User" + DataUtil.getUniqueIdentifier();
        siteName = "SiteName" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, userName, password, "@tests.com", userName, userName);
        siteService.create(userName, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C7111")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void addRestrictableAspect() throws Exception
    {
        fileName = "testFileC7111" + DataUtil.getUniqueIdentifier();
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        logger.info("Preconditions: Navigate to Document Details page for the test file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);

        logger.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage.clickManageAspects();

        logger.info("Step2: From 'Available to Add' list, click 'Add' icon next to 'Restrictable' aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addElement(14);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Restrictable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Restrictable"), "Aspect is present on 'Available to Add' list");

        logger.info("Step3: Click 'Apply Changes' and verify the restrictions are placed on the file");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();
        Assert.assertTrue(documentDetailsPage.isRestrictableAspectDisplayed(), "Restrictable aspect is not added");

    }

    @TestRail(id = "C7112")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void editRestrictableProperty()
    {
        fileName = "testFileC7111" + DataUtil.getUniqueIdentifier();
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        logger.info("Preconditions: Add Restrictable aspect to test file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);
        documentDetailsPage.clickManageAspects();
        aspectsForm.addElement(14);
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();

        logger.info("Step1: Click Actions -> Edit Properties option");
        documentDetailsPage.renderedPage();
        documentDetailsPage.clickEditProperties();

        logger.info("Step2: Click '?' icon and verify the help message");

        editPropertiesPage.clickHelpIconForRestrictableAspect();
        Assert.assertEquals(helpMessage, editPropertiesPage.getHelpMessageForRestrictableAspect());

        logger.info("Step3: Fill in 'Offline Expires After (hours) and verify the change is saved");

        editPropertiesPage.addOfflineExpiresAfterValue("48");
        editPropertiesPage.clickButton("Save");
        Assert.assertTrue(documentDetailsPage.isRestrictableValueUpdated("48"), "The value for Offline Expires After (hours) has not been updated");

    }

    @TestRail(id = "C7113")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void removeRestrictableProperty() throws Exception
    {
        fileName = "testFileC7111" + DataUtil.getUniqueIdentifier();
        contentService.createDocument(userName, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        logger.info("Preconditions: Add Restrictable aspect to test file");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);
        documentDetailsPage.clickManageAspects();
        aspectsForm.addElement(14);
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();

        logger.info("Step1: Click Actions -> Manage Aspects option");
        documentDetailsPage.clickManageAspects();

        logger.info("Step2: Click 'Remove' icon next to 'Restrictable' aspect");
        aspectsForm.removeElement(0);

        logger.info("Step3: Click 'Apply changes' button and verify the 'Restrictable' property is removed from 'Properties' section");
        aspectsForm.clickApplyChangesButton();
        getBrowser().refresh();
        Assert.assertFalse(documentDetailsPage.isRestrictableAspectDisplayed(), "Restrictable aspect is not added");

    }

}
