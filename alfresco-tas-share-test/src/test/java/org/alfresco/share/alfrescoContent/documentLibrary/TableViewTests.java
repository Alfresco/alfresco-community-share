package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.alfresco.share.ContextAwareWebTest.FILE_CONTENT;

import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class TableViewTests extends BaseTest
{
    private DocumentLibraryPage2 documentLibraryPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2266, C2267")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkContentInTableView()
    {
        String fileDescription = RandomStringUtils.randomAlphabetic(10);
        String fileTitle = RandomStringUtils.randomAlphabetic(5);
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file)
                .usingResource(file)
                    .updateProperty("cmis:description", fileDescription)
                    .updateProperty("cm:title", fileTitle);

        documentLibraryPage.navigate(site.get())
            .clickOptions()
            .selectTableView()
                .assertContentIsDisplayed(file)
                .assertTitleEqualsTo(file, fileTitle)
                .assertDescriptionEqualsTo(file, fileDescription)
                .assertCreatorEqualsTo(file, user.get().getFirstName().concat(" ").concat(user.get().getLastName()))
                .assertCreatedDateEqualsTo(file, getFormattedContentDateFromServer(file, "cmis:creationDate"))
                .assertModifierEqualsTo(file, user.get().getFirstName().concat(" ").concat(user.get().getLastName()))
                .assertModifiedDateEqualsTo(file, getFormattedContentDateFromServer(file, "cmis:lastModificationDate"));
    }

    private String getFormattedContentDateFromServer(ContentModel content, String dateType)
    {
        SimpleDateFormat shareDateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
        CmisObject obj = getCmisApi().authenticateUser(user.get())
            .usingResource(content).withCMISUtil().getCmisObject(content.getCmisLocation());
        GregorianCalendar date = obj.getPropertyValue(dateType);
        return shareDateFormat.format(date.getTime());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
