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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateFileFromTemplateTests extends BaseTest
{
    private final String templateContent = "template content";

    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7000")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFileFromTemplate()
    {
        FolderModel nodeTemplates = new FolderModel("Node Templates");
        nodeTemplates.setCmisLocation("/Data Dictionary/Node Templates");
        FileModel templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);
        getCmisApi().usingResource(nodeTemplates).createFile(templateFile);
        documentLibraryPage.navigate(site.get())
            .clickCreate().createFileFromTemplate(templateFile)
                .usingContent(templateFile).assertContentIsDisplayed()
                    .selectFile().assertFileContentEquals(templateContent);

        getCmisApi().usingResource(templateFile).delete();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
