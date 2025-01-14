package org.alfresco.share.site.analyzingASite;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.dataprep.DashboardCustomization;

import org.alfresco.po.share.dashlet.SiteContributorBreakdownDashlet;
import org.alfresco.po.share.dashlet.SiteFileTypeBreakdownDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 2/23/2017.
 */
@Slf4j
public class AnalyzingASiteTests extends BaseTest
{
    @Autowired
    protected UserService userService;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected ContentService contentService;
   // @Autowired
    SiteDashboardPage siteDashboardPage;

    //@Autowired
    SiteFileTypeBreakdownDashlet siteFileTypeBreakdownDashlet;

   // @Autowired
    SiteContributorBreakdownDashlet siteContributorBreackdownDashlet;
    private String fileNameTxt1 = "txt file1";
    private String fileContentTxt1 = "Content for .txt file 1";
    private String fileNameTxt2 = "txt file2";
    private String fileContentTxt2 = "Content for .txt file 2";
    private String fileNameTxt3 = "txt file3";
    private String fileContentTxt3 = "Content for .txt file 3";
    private String fileNameHtml1 = "Html file1";
    private String fileContentHtml1 = "Content for .html file 1";
    private String fileNameHtml2 = "Html file2";
    private String fileContentHtml2 = "Content for .html file 2";
    private String fileNameHtml3 = "Html file3";
    private String fileContentHtml3 = "Content for .html file 3";
    private String fileNameXml1 = "xml file1";
    private String fileContentXml1 = "Content for .xml file 1";
    private String fileNameXml2 = "xml file2";
    private String fileContentXml2 = "Content for .xml file 2";
    private String fileNameDocx1 = "Docx file1";
    private String fileContentDocx1 = "Content for .docx file 1";
    private String picture = "Lighthouse";

    private String fileName1User1 = "File1" + RandomData.getRandomAlphanumeric();
    private String fileName2User1 = "File2" + RandomData.getRandomAlphanumeric();
    private String fileName3User1 = "File3" + RandomData.getRandomAlphanumeric();
    private String fileContentUser1 = "Content User 1";
    private String fileName1User2 = "File1User2" + RandomData.getRandomAlphanumeric();
    private String fileName2User2 = "File2User2" + RandomData.getRandomAlphanumeric();
    private String fileName3User2 = "File3User2" + RandomData.getRandomAlphanumeric();
    private String fileContentUser2 = "Content User 2";
    private String fileName1User3 = "File1User3" + RandomData.getRandomAlphanumeric();
    private String fileContentUser3 = "Content User 3";
    private String fileContentUser4 = "Content User 4";
    private String fileName1User4 = "File1User4" + RandomData.getRandomAlphanumeric();
    private String fileName2User4 = "File2User4" + RandomData.getRandomAlphanumeric();
    private String adminUser = "admin";
    private String adminPassword = "admin";
    private String password= "password";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user1C2234 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2C2234 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user3C2234 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user4C2234 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteNameC2234 = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        //C2233
        log.info("Precondition1: Any test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        siteService.addDashlet(adminUser, adminPassword, siteName.get().getId(), DashboardCustomization.SiteDashlet.FILE_TYPE_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);

        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileNameTxt1, fileContentTxt1);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileNameTxt2, fileContentTxt2);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileNameTxt3, fileContentTxt3);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.HTML, fileNameHtml1, fileContentHtml1);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.HTML, fileNameHtml2, fileContentHtml2);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.HTML, fileNameHtml3, fileContentHtml3);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.XML, fileNameXml1, fileContentXml1);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.XML, fileNameXml2, fileContentXml2);
        contentService.createDocument(user.get().getUsername(), password, siteName.get().getId(), CMISUtil.DocumentType.MSWORD, fileNameDocx1, fileContentDocx1);
        contentService.uploadFileInSite(user.get().getUsername(), password, siteName.get().getId(), testDataFolder + picture + ".jpg");

        //C2234
        user1C2234.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2C2234.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user3C2234.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user4C2234.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteNameC2234.set(getDataSite().usingUser(user1C2234.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1C2234.get());

        siteService.addDashlet(adminUser, adminPassword, siteNameC2234.get().getId(), DashboardCustomization.SiteDashlet.SITE_CONTRIB_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);

        userService.createSiteMember(user1C2234.get().getUsername(), password, user2C2234.get().getUsername(), siteNameC2234.get().getId(), "SiteManager");
        userService.createSiteMember(user1C2234.get().getUsername(), password, user3C2234.get().getUsername(), siteNameC2234.get().getId(), "SiteManager");
        userService.createSiteMember(user1C2234.get().getUsername(), password, user4C2234.get().getUsername(), siteNameC2234.get().getId(), "SiteManager");

        contentService.createDocument(user1C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName1User1, fileContentUser1);
        contentService.createDocument(user1C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName2User1, fileContentUser1);
        contentService.createDocument(user1C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName3User1, fileContentUser1);
        contentService.createDocument(user2C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName1User2, fileContentUser2);
        contentService.createDocument(user2C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName2User2, fileContentUser2);
        contentService.createDocument(user2C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName3User2, fileContentUser2);
        contentService.createDocument(user3C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName1User3, fileContentUser3);
        contentService.createDocument(user4C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileName1User4, fileContentUser4);
        contentService.createDocument(user4C2234.get().getUsername(), password, siteNameC2234.get().getId(), CMISUtil.DocumentType.HTML, fileName2User4, fileContentUser4);

        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteContributorBreackdownDashlet = new SiteContributorBreakdownDashlet(webDriver);
        siteFileTypeBreakdownDashlet = new SiteFileTypeBreakdownDashlet(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(siteNameC2234.get());

        deleteUsersIfNotNull(user.get());
        deleteUsersIfNotNull(user1C2234.get());
        deleteUsersIfNotNull(user2C2234.get());
        deleteUsersIfNotNull(user3C2234.get());
        deleteUsersIfNotNull(user4C2234.get());
    }

    @TestRail (id = "C2233")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES, "PieChartTests" })

    public void verifySiteFileTypeBreakdownDashlet() {
        authenticateUsingLoginPage(user.get());
        siteDashboardPage.navigate(siteName.get());
        siteDashboardPage.pageRefresh();
        log.info("Step 1&2 : Verify the content of \"Site File Type Breakdown\" dashlet.");
        siteDashboardPage.navigate(siteName.get());
        siteDashboardPage.pageRefresh();
        siteFileTypeBreakdownDashlet.assert_PieChartSizeEquals(5);
        Map<String, String> fileDetails = siteFileTypeBreakdownDashlet.getPieChartSliceTooltip();
        assertTrue(fileDetails.containsKey("image/jpeg"), "Document type JPEG is displayed.");
        assertTrue(fileDetails.get("image/jpeg").contains("1 items (10%)"), "The jpeg file number is in site's library is not correct.");
        assertTrue(fileDetails.get("image/jpeg").contains("Size: 548 KB"), "Size of the JPEG files is not correct.");

        assertTrue(fileDetails.containsKey("text/plain"), "Document type Plain Text is not correct.");
        assertTrue(fileDetails.get("text/plain").contains("3 items (30%)"), "The Plain Text file number is not correct.");
        assertTrue(fileDetails.get("text/plain").contains("Size: 69 bytes"), "Size of the Plain Text files is not correct.");

        assertTrue(fileDetails.containsKey("text/html"), "Document type HTML is not correct.");
        assertTrue(fileDetails.get("text/html").contains("3 items (30%)"), "The HTML file number is not correct.");
        assertTrue(fileDetails.get("text/html").contains("Size: 72 bytes"), "Size of the HTML files is not correct.");

        assertTrue(fileDetails.containsKey("text/xml"), "Document type XML is not correct.");
        assertTrue(fileDetails.get("text/xml").contains("2 items (20%)"), "The XML file number is not correct.");
        assertTrue(fileDetails.get("text/xml").contains("Size: 46 bytes"), "Size of the XML files is not correct.");

        assertTrue(fileDetails.containsKey("application/msword"), "Document type Microsoft Word is not correct.");
        assertTrue(fileDetails.get("application/msword").contains("1 items (10%)"), "The Microsoft Word file number is not correct.");
        assertTrue(fileDetails.get("application/msword").contains("Size: 24 bytes"), "Size of the Microsoft Word files is not correct.");
    }

    @TestRail (id = "C2234")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })

    public void verifySiteContributorBreakdownDashlet() throws InterruptedException {
        authenticateUsingLoginPage(user1C2234.get());
        siteDashboardPage.navigate(siteNameC2234.get());

        log.info("Step 1&2: Verify the content of \"Contributor Breakdown\" dashlet.");
        siteDashboardPage.pageRefresh();
        siteDashboardPage.navigate(siteNameC2234.get());
        siteDashboardPage.pageRefresh();
        siteContributorBreackdownDashlet.assert_PieChartSizeEquals(4);
        Map<String, String> fileDetails = siteContributorBreackdownDashlet.getPieChartSliceTooltip();
        assertTrue(fileDetails.containsKey(user3C2234.get().getUsername()), user3C2234 + " contribution is not displayed");
        assertTrue(fileDetails.get(user3C2234.get().getUsername()).contains("1 items (11.1%)"), user3C2234 + " user contribution number of files in not correct");
        assertTrue(fileDetails.get(user3C2234.get().getUsername()).contains("Size: 14 bytes"), user3C2234 + " user contribution file size is not correct");


        siteDashboardPage.navigate(siteNameC2234.get());
        siteDashboardPage.pageRefresh();
        assertTrue(fileDetails.containsKey(user2C2234.get().getUsername()), user2C2234.get().getUsername() + " contribution is not displayed");
        assertTrue(fileDetails.get(user2C2234.get().getUsername()).contains("3 items (33.3%)"), user2C2234 + " user contribution number of files in not correct");
        assertTrue(fileDetails.get(user2C2234.get().getUsername()).contains("Size: 42 bytes"), user2C2234 + " user contribution file size is not correct");

        assertTrue(fileDetails.containsKey(user4C2234.get().getUsername()), user4C2234 + " contribution is not displayed");
        assertTrue(fileDetails.get(user4C2234.get().getUsername()).contains("2 items (22.2%)"), user4C2234 + " user contribution number of files in not correct");
        assertTrue(fileDetails.get(user4C2234.get().getUsername()).contains("Size: 28 bytes"), user4C2234 + " user contribution file size is not correct");


        siteDashboardPage.pageRefresh();
        siteDashboardPage.navigate(siteNameC2234.get());
        siteDashboardPage.pageRefresh();
        Thread.sleep(3000);
        assertTrue(fileDetails.containsKey(user1C2234.get().getUsername()), user1C2234 + " contribution is not displayed");
        assertTrue(fileDetails.get(user1C2234.get().getUsername()).contains("3 items (33.3%)"), user1C2234 + " user contribution number of files in not correct");
        assertTrue(fileDetails.get(user1C2234.get().getUsername()).contains("Size: 42 bytes"), user1C2234 + " user contribution file size is not correct");
    }
}
