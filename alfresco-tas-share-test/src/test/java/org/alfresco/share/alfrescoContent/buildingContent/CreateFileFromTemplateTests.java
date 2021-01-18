package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
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

        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);
        getCmisApi().authenticateUser(getAdminUser());
        setupAuthenticatedSession(testUser);
    }

    @TestRail (id = "C7000")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFileFromTemplate()
    {
        templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);
        getCmisApi().usingResource(nodeTemplates).createFile(templateFile);
        documentLibraryPage.navigate(testSite)
            .clickCreate().createFileFromTemplate(templateFile)
                .usingContent(templateFile).assertContentIsDisplayed()
                    .selectFile().assertFileContentEquals(templateContent);

        getCmisApi().usingResource(templateFile).delete();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser);
        deleteSitesIfNotNull(testSite);
    }
}
