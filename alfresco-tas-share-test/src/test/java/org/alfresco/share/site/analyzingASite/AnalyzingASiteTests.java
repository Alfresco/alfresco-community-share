package org.alfresco.share.site.analyzingASite;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.*;
import org.alfresco.po.share.dashlet.SiteContributorBreakdownDashlet;
import org.alfresco.po.share.dashlet.SiteFileTypeBreakdownDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.testng.Assert.assertTrue;

/**
 * Created by Mirela Tifui on 2/23/2017.
 */
public class AnalyzingASiteTests extends ContextAwareWebTest
{
    @Autowired
    UserService userService;

    @Autowired
    SiteService siteService;

    @Autowired
    ContentService contentService;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteFileTypeBreakdownDashlet siteFileTypeBreakdownDashlet;

    @Autowired
    SiteContributorBreakdownDashlet siteContributorBreackdownDashlet;

    private String user = "user2233-" + DataUtil.getUniqueIdentifier();
    private String siteName = "C2233" + DataUtil.getUniqueIdentifier();
    private String siteNameC2234 ="C2234SiteName"+ DataUtil.getUniqueIdentifier();
    private String user1C2234 = "C2234-1"+ DataUtil.getUniqueIdentifier();
    private String user2C2234 = "C2234-2"+ DataUtil.getUniqueIdentifier();
    private String user3C2234 = "C2234-3"+ DataUtil.getUniqueIdentifier();
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
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private String picture = "Lighthouse";

    private String fileName1User1 = "File1"+ DataUtil.getUniqueIdentifier();
    private String fileName2User1 = "File2"+ DataUtil.getUniqueIdentifier();
    private String fileName3User1 = "File3"+ DataUtil.getUniqueIdentifier();
    private String fileName4User1 = "File4"+ DataUtil.getUniqueIdentifier();
    private String fileName5User1 = "File5"+ DataUtil.getUniqueIdentifier();
    private String fileContentUser1 = "Content User 1";
    private String fileName1User2 = "File1User2"+ DataUtil.getUniqueIdentifier();
    private String fileName2User2 = "File2User2"+ DataUtil.getUniqueIdentifier();
    private String fileName3User2 = "File3User2"+ DataUtil.getUniqueIdentifier();
    private String fileName4User2 = "File4User2"+ DataUtil.getUniqueIdentifier();
    private String fileContentUser2 = "Content User 2";
    private String fileName1User3 = "File1User3"+ DataUtil.getUniqueIdentifier();
    private String fileContentUser3 = "Content User 3";

    @BeforeClass
    public void setupTest()
    {
        //C2233
        userService.create(adminUser, adminPassword, user, password, user + domain, "C2233", "C2233");
        siteService.create(user, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
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


    }

    @TestRail(id="C2233")
    @Test

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
}
