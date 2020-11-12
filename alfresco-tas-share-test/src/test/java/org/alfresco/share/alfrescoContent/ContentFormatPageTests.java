package org.alfresco.share.alfrescoContent;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.TinyMce.TinyMceEditor;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.CommentPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 2/22/2018.
 */
public class ContentFormatPageTests extends ContextAwareWebTest
{
    //@Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    CommentPage commentPage;
    @Autowired
    TinyMceEditor commentBox;

    private String userName = "user" + RandomData.getRandomAlphanumeric();
    private String siteName = "site" + RandomData.getRandomAlphanumeric();
    private String documentName = "doc" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, domain, "test", "user");
        siteService.create(userName, password, domain, siteName, "test_site", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, documentName, "content");
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        siteService.delete(adminUser, adminPassword, domain, siteName);
        userService.delete(adminUser, adminPassword, userName);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.CONTENT })
    public void testBoldFontOfRichTextFormatter()
    {
        String text = "test content";
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(documentName);
        documentDetailsPage.clickAddCommentButton();
        commentBox.addContent(text);
        commentBox.clickTextFormatter(TinyMceEditor.FormatType.BOLD);
        Assert.assertTrue(commentPage.isBoldButtonPressed(), "Bold button is not pressed");
        //Assert.assertEquals(commentBox.getBoldContent(), text, text +" is not bold");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.CONTENT })
    public void testItalicFontOfRichTextFormatter()
    {

    }
}
