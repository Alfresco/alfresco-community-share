package org.alfresco.share.alfrescoContent.buildingContent;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class CreatingFilesTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        site.set(dataSite.usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C6976, C6986")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void createPlainTextFile()
    {
        FileModel txtFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        documentLibraryPage.navigate(site.get())
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
        documentLibraryPage.navigate(site.get())
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
        documentLibraryPage.navigate(site.get())
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

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
