package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateFolderFromTemplateTests extends BaseShareWebTests
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel testUser;
    private SiteModel testSite;
    private FolderModel parentTemplateFolder = new FolderModel("Software Engineering Project");

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        cmisApi.authenticateUser(testUser);

        parentTemplateFolder.setCmisLocation(Utility.buildPath(
            Utility.buildPath(String.format("/Sites/%s/documentLibrary", testSite.getId())),
            parentTemplateFolder.getName()));
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(testUser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(testUser);
        deleteSites(testSite);
    }

    @TestRail (id = "C6292")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFolderFromTemplate()
    {
        FolderModel discussions = new FolderModel("Discussions");
        discussions.setCmisLocation(Utility.buildPath(parentTemplateFolder.getCmisLocation(), discussions.getName()));
        FolderModel documentation = new FolderModel("Documentation");
        documentation.setCmisLocation(Utility.buildPath(parentTemplateFolder.getCmisLocation(), documentation.getName()));
        FolderModel presentations = new FolderModel("Presentations");
        presentations.setCmisLocation(Utility.buildPath(parentTemplateFolder.getCmisLocation(), presentations.getName()));
        FolderModel qualityAssurance = new FolderModel("Quality Assurance");
        qualityAssurance.setCmisLocation(Utility.buildPath(parentTemplateFolder.getCmisLocation(), qualityAssurance.getName()));
        FolderModel uiDesign = new FolderModel("UI Design");
        uiDesign.setCmisLocation(Utility.buildPath(parentTemplateFolder.getCmisLocation(), uiDesign.getName()));

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

        documentLibraryPage.navigate(testSite)
            .clickCreate().clickCreateFolderFromTemplate(parentTemplateFolder)
                .clickSave();
        documentLibraryPage.usingContent(parentTemplateFolder).assertContentIsDisplayed();
        cmisApi.usingResource(discussions).assertThat().existsInRepo()
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
        documentLibraryPage.navigate(testSite)
            .clickCreate().clickCreateFolderFromTemplate(parentTemplateFolder)
            .clickCancel();
        documentLibraryPage.usingContent(parentTemplateFolder).assertContentIsNotDisplayed();
    }

    @TestRail (id = "C8139")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createFolderFromTemplateUsingWildcards()
    {
        String illegalCharacters = "\'* \" < > \\ / . ? : |'";
        String validWildcards = "!@$%^&().-=+;',";
        documentLibraryPage.navigate(testSite)
            .clickCreate().clickCreateFolderFromTemplate(parentTemplateFolder)
                .typeName(illegalCharacters).assertNameInputIsInvalid()
                    .assertNameInputContainsIllegalCharactersMessageIsDisplayed()
                .typeName("AName.").assertNameInputIsInvalid()
                .typeName(validWildcards).clickSave();
        documentLibraryPage.usingContent(parentTemplateFolder).assertContentIsDisplayed();
    }
}
