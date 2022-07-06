package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class CreateFolderFromTemplateTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final ThreadLocal<FolderModel> parentTemplateFolder = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());

        parentTemplateFolder.set(new FolderModel("Software Engineering Project"));
        parentTemplateFolder.get().setCmisLocation(Utility.buildPath(
            Utility.buildPath(String.format("/Sites/%s/documentLibrary", site.get().getId())),
            parentTemplateFolder.get().getName()));

    }

    @TestRail (id = "C6292")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFolderFromTemplate()
    {
        FolderModel discussions = new FolderModel("Discussions");
        discussions.setCmisLocation(Utility.buildPath(parentTemplateFolder.get().getCmisLocation(), discussions.getName()));
        FolderModel documentation = new FolderModel("Documentation");
        documentation.setCmisLocation(Utility.buildPath(parentTemplateFolder.get().getCmisLocation(), documentation.getName()));
        FolderModel presentations = new FolderModel("Presentations");
        presentations.setCmisLocation(Utility.buildPath(parentTemplateFolder.get().getCmisLocation(), presentations.getName()));
        FolderModel qualityAssurance = new FolderModel("Quality Assurance");
        qualityAssurance.setCmisLocation(Utility.buildPath(parentTemplateFolder.get().getCmisLocation(), qualityAssurance.getName()));
        FolderModel uiDesign = new FolderModel("UI Design");
        uiDesign.setCmisLocation(Utility.buildPath(parentTemplateFolder.get().getCmisLocation(), uiDesign.getName()));

        FolderModel drafts = new FolderModel("Drafts");
        drafts.setCmisLocation(Utility.buildPath(documentation.getCmisLocation(), drafts.getName()));
        FolderModel pendingApproval = new FolderModel("Pending Approval");
        pendingApproval.setCmisLocation(Utility.buildPath(documentation.getCmisLocation(), pendingApproval.getName()));
        FolderModel published = new FolderModel("Published");
        published.setCmisLocation(Utility.buildPath(documentation.getCmisLocation(), published.getName()));
        FolderModel samples = new FolderModel("Samples");
        samples.setCmisLocation(Utility.buildPath(documentation.getCmisLocation(), samples.getName()));
        FileModel systemOverview = new FileModel("system-overview.html");
        systemOverview.setCmisLocation(Utility.buildPath(samples.getCmisLocation(), systemOverview.getName()));

        documentLibraryPage.navigate(site.get())
            .clickCreate().clickCreateFolderFromTemplate(parentTemplateFolder.get())
                .clickSave();
        documentLibraryPage.usingContent(parentTemplateFolder.get()).assertContentIsDisplayed();
        getCmisApi().usingResource(discussions).assertThat().existsInRepo()
            .usingResource(documentation).assertThat().existsInRepo()
            .usingResource(presentations).assertThat().existsInRepo()
            .usingResource(qualityAssurance).assertThat().existsInRepo()
            .usingResource(uiDesign).assertThat().existsInRepo()
                .usingResource(drafts).assertThat().existsInRepo()
                .usingResource(pendingApproval).assertThat().existsInRepo()
                .usingResource(published).assertThat().existsInRepo()
                .usingResource(samples).assertThat().existsInRepo()
                .usingResource(systemOverview).assertThat().existsInRepo();
    }

    @TestRail (id = "C6293")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelCreatingFolderFromTemplate()
    {
        documentLibraryPage.navigate(site.get())
            .clickCreate().clickCreateFolderFromTemplate(parentTemplateFolder.get())
            .clickCancel();
        documentLibraryPage.usingContent(parentTemplateFolder.get()).assertContentIsNotDisplayed();
    }

    @TestRail (id = "C8139")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFolderFromTemplateUsingWildcards()
    {
        String illegalCharacters = "\'* \" < > \\ / . ? : |'";
        FolderModel validWildcardsFolder = new FolderModel("!@$%^&().-=+;,");
        documentLibraryPage.navigate(site.get())
            .clickCreate().clickCreateFolderFromTemplate(parentTemplateFolder.get())
                .typeName(illegalCharacters).assertNameInputIsInvalid()
                    .assertNameInputContainsIllegalCharactersMessageIsDisplayed()
                .typeName("AName.").assertNameInputIsInvalid()
                .typeName(validWildcardsFolder.getName()).clickSave();
        documentLibraryPage.usingContent(validWildcardsFolder).assertContentIsDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
