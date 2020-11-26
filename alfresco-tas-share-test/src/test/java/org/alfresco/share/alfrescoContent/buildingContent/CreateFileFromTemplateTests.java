package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateFileFromTemplateTests extends BaseTest
{
    private final String templateContent = "template content";

    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel testUser;
    private SiteModel testSite;
    private FolderModel nodeTemplates = new FolderModel("Node Templates");
    private FileModel templateFile;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        nodeTemplates.setCmisLocation("/Data Dictionary/Node Templates");
        templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);

        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        cmisApi.authenticateUser(getAdminUser()).usingResource(nodeTemplates).createFile(templateFile);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(testUser);
    }

    @TestRail (id = "C7000")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFileFromTemplate()
    {
        documentLibraryPage.navigate(testSite)
            .clickCreate().createFileFromTemplate(templateFile)
                .usingContent(templateFile).assertContentIsDisplayed()
                    .selectFile().assertFileContentEquals(templateContent);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(testUser);
        deleteSites(testSite);
        cmisApi.authenticateUser(getAdminUser()).usingResource(templateFile).delete();
    }
}
