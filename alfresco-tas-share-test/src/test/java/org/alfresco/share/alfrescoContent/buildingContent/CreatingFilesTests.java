package org.alfresco.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreatingFilesTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private UserModel testUser;
    private SiteModel testSite;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        testUser = dataUser.usingAdmin().createRandomTestUser();
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(testUser);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(testUser);
        deleteSites(testSite);
    }

    @TestRail (id = "C6976, C6986")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createPlainTextFile()
    {
        FileModel txtFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        documentLibraryPage.navigate(testSite)
            .clickCreate().clickTextPlain()
                .assertCreateContentPageIsOpened()
                .assertBrowserPageTitleIs(language.translate("createContentPage.browserTitle"))
                .assertNameInputHasMandatoryMarker()
                .assertCreateContentPageIsOpened()
                .assertCancelButtonIsDisplayed()
                    .typeName(txtFile.getName())
                    .typeTitle("test title")
                    .typeDescription("test description")
                    .typeContent(txtFile.getContent())
                    .clickCreate()
                        .assertDocumentTitleEquals(txtFile)
                        .assertFileContentEquals(FILE_CONTENT)
                        .assertPropertyValueEquals(language.translate("property.mimetype"), "Plain Text");
    }

    @TestRail (id = "C6977")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createHTMLFile()
    {
        FileModel htmlFile = FileModel.getRandomFileModel(FileType.HTML, FILE_CONTENT);
        documentLibraryPage.navigate(testSite)
            .clickCreate().clickHtml()
                .assertCreateContentPageIsOpened()
                .assertBrowserPageTitleIs(language.translate("createContentPage.browserTitle"))
                .assertNameInputHasMandatoryMarker()
                    .typeName(htmlFile.getName())
                    .sendInputForHTMLContent(FILE_CONTENT)
                    .typeTitle("test title")
                    .typeDescription("test description")
                    .clickCreate()
                        .assertDocumentTitleEquals(htmlFile)
                        .assertFileContentEquals(FILE_CONTENT)
                        .assertPropertyValueEquals(language.translate("property.mimetype"), "HTML");
    }

    @TestRail (id = "C6978")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createXMLFile()
    {
        FileModel xmlFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        documentLibraryPage.navigate(testSite)
            .clickCreate().clickXml()
                .assertCreateContentPageIsOpened()
                .assertBrowserPageTitleIs(language.translate("createContentPage.browserTitle"))
                .assertNameInputHasMandatoryMarker()
                    .typeName(xmlFile.getName())
                    .typeTitle("test title")
                    .typeDescription("test description")
                    .typeContent(FILE_CONTENT)
                    .clickCreate()
                        .assertDocumentTitleEquals(xmlFile)
                        .assertFileContentEquals(FILE_CONTENT)
                        .assertPropertyValueEquals(language.translate("property.mimetype"), "XML");
    }
}
