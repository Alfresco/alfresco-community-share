package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_40;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
@Slf4j
public class MyFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    public MyFilesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private By createButton = By.cssSelector("button[id*='createContent']");

    private final By dialogTitle = By.xpath("//div[@class='hd']");
    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public MyFilesPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickMyFiles();
    }

    public MyFilesPage assertMyFilesPageIsOpened()
    {
        waitUrlContains(getRelativePath(), WAIT_40.getValue());
        assertTrue(getCurrentUrl().contains(getRelativePath()), "My Files page is opened");
        return this;
    }
    public MyFilesPage click_CreateButton()
    {
        clickElement(createButton);
        waitUntilElementIsVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
        return this;
    }
    public MyFilesPage click_FolderLink()
    {
        clickElement(CreateMenuOption.FOLDER.getLocator());
        return this;
    }
    public void assertDialogTitleEquals(String contentName)
    {
        log.info("Verify the Dialog Title..");
        assertEquals(getDialogTitle(), contentName, String.format("Dialog title not matched with [%s]", contentName));
    }
    public MyFilesPage assertIsContantNameDisplayed(String contantName)
    {
        log.info("Verify file/folder name displayed {}", contantName);
        assertTrue(isContentNameDisplayed(contantName), String.format("file/folder name not matched with %s ", contantName));
        return this;
    }
    public boolean isLikeButtonDisplayed(String fileName)
    {
        log.info("To Verify the Like Button is Displayed");
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), likeButton);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }
}