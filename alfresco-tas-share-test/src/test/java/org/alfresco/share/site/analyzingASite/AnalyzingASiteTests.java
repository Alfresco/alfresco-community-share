package org.alfresco.share.site.analyzingASite;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.SiteContributorBreakdownDashlet;
import org.alfresco.po.share.dashlet.SiteFileTypeBreakdownDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * Created by Mirela Tifui on 2/23/2017.
 */
public class AnalyzingASiteTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteFileTypeBreakdownDashlet siteFileTypeBreakdownDashlet;

    @Autowired SiteContributorBreakdownDashlet siteContributorBreackdownDashlet;

    private String user = String.format("user2233-%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C2233%s", RandomData.getRandomAlphanumeric());
    private String siteNameC2234 = String.format("C2234SiteName%s", RandomData.getRandomAlphanumeric());
    private String user1C2234 = "C2234-1";
    private String user2C2234 = "C2234-2";
    private String user3C2234 = "C2234-3";
    private String user4C2234 = "C2234-4";
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

    private String fileName1User1 = "File1"+ RandomData.getRandomAlphanumeric();
    private String fileName2User1 = "File2"+ RandomData.getRandomAlphanumeric();
    private String fileName3User1 = "File3"+ RandomData.getRandomAlphanumeric();
    private String fileContentUser1 = "Content User 1";
    private String fileName1User2 = "File1User2"+ RandomData.getRandomAlphanumeric();
    private String fileName2User2 = "File2User2"+ RandomData.getRandomAlphanumeric();
    private String fileName3User2 = "File3User2"+ RandomData.getRandomAlphanumeric();
    private String fileContentUser2 = "Content User 2";
    private String fileName1User3 = "File1User3"+ RandomData.getRandomAlphanumeric();
    private String fileContentUser3 = "Content User 3";
    private String fileContentUser4 = "Content User 4";
    private String fileName1User4 = "File1User4"+ RandomData.getRandomAlphanumeric();
    private String fileName2User4 = "File2User4"+ RandomData.getRandomAlphanumeric();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        //C2233
        userService.create(adminUser, adminPassword, user, password, user + domain, "C2233", "C2233");
        siteService.create(user, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(adminUser, adminPassword, siteName, DashboardCustomization.SiteDashlet.FILE_TYPE_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);

        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileNameTxt1, fileContentTxt1);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileNameTxt2, fileContentTxt2);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileNameTxt3, fileContentTxt3);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.HTML, fileNameHtml1, fileContentHtml1);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.HTML, fileNameHtml2, fileContentHtml2);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.HTML, fileNameHtml3, fileContentHtml3);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.XML, fileNameXml1, fileContentXml1);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.XML, fileNameXml2, fileContentXml2);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.MSWORD, fileNameDocx1, fileContentDocx1);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + picture + ".jpg");

        //C2234
        userService.create(adminUser, adminPassword, user1C2234, password, user1C2234 + domain, "C2233-1", "C2233-1");
        userService.create(adminUser, adminPassword, user2C2234, password, user2C2234 + domain, "C2233-2", "C2233-2");
        userService.create(adminUser, adminPassword, user3C2234, password, user3C2234 + domain, "C2233-3", "C2233-3");
        userService.create(adminUser, adminPassword, user4C2234, password, user4C2234 + domain, "C2233-4", "C2233-4");
        siteService.create(user1C2234, password, domain, siteNameC2234, siteNameC2234, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(adminUser, adminPassword, siteNameC2234, DashboardCustomization.SiteDashlet.SITE_CONTRIB_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);

        userService.createSiteMember(user1C2234, password, user2C2234, siteNameC2234, "SiteManager");
        userService.createSiteMember(user1C2234, password, user3C2234, siteNameC2234, "SiteManager");
        userService.createSiteMember(user1C2234, password, user4C2234, siteNameC2234, "SiteManager");

        contentService.createDocument(user1C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName1User1, fileContentUser1);
        contentService.createDocument(user1C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName2User1, fileContentUser1);
        contentService.createDocument(user1C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName3User1, fileContentUser1);
        contentService.createDocument(user2C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName1User2, fileContentUser2);
        contentService.createDocument(user2C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName2User2, fileContentUser2);
        contentService.createDocument(user2C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName3User2, fileContentUser2);
        contentService.createDocument(user3C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName1User3, fileContentUser3);
        contentService.createDocument(user4C2234, password, siteNameC2234, CMISUtil.DocumentType.TEXT_PLAIN, fileName1User4, fileContentUser4);
        contentService.createDocument(user4C2234, password, siteNameC2234, CMISUtil.DocumentType.HTML, fileName2User4, fileContentUser4);
    }

    @TestRail(id="C2233")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void verifySiteFileTypeBreakdownDashlet()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1&2 : Verify the content of \"Site File Type Breakdown\" dashlet.");
        siteDashboardPage.navigate(siteName);
        Assert.assertEquals(siteFileTypeBreakdownDashlet.getNumberOfPieChartSlices(), 5, "There are not 5 different sections in the pie chart");
        Map<String, String> fileDetails = siteFileTypeBreakdownDashlet.getPieChartSliceTooltip();
        assertTrue(fileDetails.containsKey("JPEG Image"), "Document type JPEG is displayed.");
        assertTrue(fileDetails.get("JPEG Image").contains("1 items (10%)"), "The jpeg file number is in site's library is not correct.");
        assertTrue(fileDetails.get("JPEG Image").contains("Size: 548 KB"), "Size of the JPEG files is not correct.");

        assertTrue(fileDetails.containsKey("Plain Text"), "Document type Plain Text is not correct.");
        assertTrue(fileDetails.get("Plain Text").contains("3 items (30%)"), "The Plain Text file number is not correct.");
        assertTrue(fileDetails.get("Plain Text").contains("Size: 69 bytes"), "Size of the Plain Text files is not correct.");

        assertTrue(fileDetails.containsKey("HTML"), "Document type HTML is not correct.");
        assertTrue(fileDetails.get("HTML").contains("3 items (30%)"), "The HTML file number is not correct.");
        assertTrue(fileDetails.get("HTML").contains("Size: 72 bytes"), "Size of the HTML files is not correct.");

        assertTrue(fileDetails.containsKey("XML"), "Document type XML is not correct.");
        assertTrue(fileDetails.get("XML").contains("2 items (20%)"), "The XML file number is not correct.");
        assertTrue(fileDetails.get("XML").contains("Size: 46 bytes"), "Size of the XML files is not correct.");

        assertTrue(fileDetails.containsKey("Microsoft Word"), "Document type Microsoft Word is not correct.");
        assertTrue(fileDetails.get("Microsoft Word").contains("1 items (10%)"), "The Microsoft Word file number is not correct.");
        assertTrue(fileDetails.get("Microsoft Word").contains("Size: 24 bytes"), "Size of the Microsoft Word files is not correct.");
        cleanupAuthenticatedSession();
    }

    @TestRail(id="C2234")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void verifySiteContributorBreakdownDashlet()
    {
        setupAuthenticatedSession(user1C2234, password);
        getBrowser().waitInSeconds(10);
        siteDashboardPage.navigate(siteNameC2234);

        LOG.info("Step 1&2: Verify the content of \"Contributor Breakdown\" dashlet.");

        siteContributorBreackdownDashlet.renderedPage();
        Assert.assertEquals(siteContributorBreackdownDashlet.getNumberOfPieChartSlices(), 4, "There are not 4 different sections in the pie chart");
        getBrowser().waitInSeconds(10);

        Map<String, String> fileDetails = siteContributorBreackdownDashlet.getPieChartSliceTooltip();
        assertTrue(fileDetails.containsKey(user3C2234), user3C2234+" contribution is not displayed");
        assertTrue(fileDetails.get(user3C2234).contains("1 items (11.1%)"), user3C2234+" user contribution number of files in not correct");
        assertTrue(fileDetails.get(user3C2234).contains("Size: 14 bytes"),user3C2234+ " user contribution file size is not correct");

        assertTrue(fileDetails.containsKey(user2C2234), user2C2234+" contribution is not displayed");
        assertTrue(fileDetails.get(user2C2234).contains("3 items (33.3%)"), user2C2234+" user contribution number of files in not correct");
        assertTrue(fileDetails.get(user2C2234).contains("Size: 42 bytes"),user2C2234+ " user contribution file size is not correct");

        assertTrue(fileDetails.containsKey(user4C2234), user4C2234+" contribution is not displayed");
        assertTrue(fileDetails.get(user4C2234).contains("2 items (22.2%)"), user4C2234+" user contribution number of files in not correct");
        assertTrue(fileDetails.get(user4C2234).contains("Size: 28 bytes"),user4C2234+ " user contribution file size is not correct");

        assertTrue(fileDetails.containsKey(user1C2234), user1C2234+" contribution is not displayed");
        assertTrue(fileDetails.get(user1C2234).contains("3 items (33.3%)"), user1C2234+" user contribution number of files in not correct");
        assertTrue(fileDetails.get(user1C2234).contains("Size: 42 bytes"),user1C2234+ " user contribution file size is not correct");
    }
}
