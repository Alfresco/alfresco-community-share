package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateFileFromTemplateTests extends ContextAwareWebTest
{
    private final String templateContent = "template content";

    @Autowired
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel testUser;
    private SiteModel testSite;
    private FolderModel nodeTemplates = new FolderModel("Node Templates");
    private FileModel templateFile;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        nodeTemplates.setCmisLocation("/Data Dictionary/Node Templates");
        templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);

        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        cmisApi.authenticateUser(getAdminUser()).usingResource(nodeTemplates).createFile(templateFile);
        setupAuthenticatedSession(testUser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(testUser);
        deleteSites(testSite);
        cmisApi.authenticateUser(getAdminUser()).usingResource(templateFile).delete();
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
}
