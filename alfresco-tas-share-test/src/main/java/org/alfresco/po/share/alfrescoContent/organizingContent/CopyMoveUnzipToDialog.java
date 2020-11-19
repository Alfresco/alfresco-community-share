package org.alfresco.po.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.SharePage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.NoSuchElementException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CopyMoveUnzipToDialog extends ShareDialog2
{
    private By createLinkButton = By.cssSelector("button[id$='_default-copyMoveTo-link-button']");
    private By dialogTitle = By.cssSelector("div[id*='title']");
    @RenderWebElement
    private By unzipCopyMoveButton = By.cssSelector("button[id$='_default-copyMoveTo-ok-button']");
    private By cancelButton = By.cssSelector("button[id$='_default-copyMoveTo-cancel-button']");
    private By recentSitesDestination = By.cssSelector("button[id$='copyMoveTo-recentsites-button']");
    private By sharedFilesDestination = By.cssSelector("span[id$='default-copyMoveTo-shared']");
    private By myFilesDestination = By.cssSelector("button[id$='copyMoveTo-myfiles-button']");
    private By allSitesDestination = By.cssSelector("button[id$='copyMoveTo-site-button']");
    private By folderPathsArea = By.cssSelector("div[id$='default-copyMoveTo-treeview']");
    private By sitePickerArea = By.cssSelector(".site-picker");
    private By dialogBody = By.cssSelector("div[id$='default-copyMoveTo-dialog']");
    private String siteToSelect = "//h4[text()='%s']";
    private String folderElementToSelect = "//span[@class='ygtvlabel' and text()='%s']";
    private String folderElementToSelectRow = "//span[@class='ygtvlabel' and text()='%s']/../../../../..";

    private final String destinationChecked = "yui-radio-button-checked";

    public CopyMoveUnzipToDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public CopyMoveUnzipToDialog selectRecentSitesDestination()
    {
        LOG.info("Select Recent Sites");
        getBrowser().waitUntilElementClickable(recentSitesDestination).click();
        getBrowser().waitUntilElementVisible(sitePickerArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectSharedFilesDestination()
    {
        LOG.info("Select Shared Files destination");
        WebElement shared = getBrowser().waitUntilElementVisible(sharedFilesDestination);
        getBrowser().mouseOver(shared);
        shared.click();
        getBrowser().waitUntilElementHasAttribute(shared, "class", destinationChecked);
        getBrowser().waitUntilElementVisible(folderPathsArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectMyFilesDestination()
    {
        LOG.info("Select My Files destination");
        getBrowser().waitUntilElementClickable(myFilesDestination).click();
        getBrowser().waitUntilElementVisible(folderPathsArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectAllSitesDestination()
    {
        LOG.info("Select Shared Files destination");
        getBrowser().waitUntilElementClickable(allSitesDestination).click();
        getBrowser().waitUntilElementVisible(sitePickerArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectSite(SiteModel site)
    {
        LOG.info("Select site {}", site.getTitle());
        WebElement sitePicker =  getBrowser().waitUntilElementVisible(sitePickerArea);
        getBrowser().waitUntilChildElementIsPresent(sitePicker, By.xpath(String.format(siteToSelect, site.getTitle()))).click();
        return this;
    }

    public CopyMoveUnzipToDialog selectFolder(FolderModel folderToSelect)
    {
        LOG.info("Select folder {}", folderToSelect.getName());
        By folderRow = By.xpath(String.format(folderElementToSelectRow, folderToSelect.getName()));
        By folder = By.xpath(String.format(folderElementToSelect, folderToSelect.getName()));
        getBrowser().waitUntilElementVisible(folderPathsArea);
        waitAndSelectFolder(folder, folderRow);
        return this;
    }

    private void waitAndSelectFolder(By folderElement, By folderRow)
    {
        int retry = 0;
        while(retry < WAIT_15)
        {
            try
            {
                WebElement folder = getBrowser().findElement(folderPathsArea);
                folder.findElement(folderElement).click();
                if(folder.findElement(folderRow).getAttribute("class").contains("selected"))
                {
                    break;
                }
                break;
            }
            catch (ElementClickInterceptedException | StaleElementReferenceException | NoSuchElementException e)
            {
                retry++;
                LOG.info("Retry select folder - {}", retry);
            }
        }
    }

    public void clickCreateLinkButton()
    {
        LOG.info("Click Create Link button");
        getBrowser().waitUntilElementClickable(createLinkButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public boolean isCreateLinkButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(createLinkButton);
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        assertTrue(getBrowser().isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsNotDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        assertFalse(getBrowser().isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public void clickUnzipButton()
    {
        LOG.info("Click Unzip To button");
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        getBrowser().waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCopyToButton()
    {
        LOG.info("Click Copy To button");
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        getBrowser().waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancelButton()
    {
        LOG.info("Click Cancel button");
        getBrowser().waitUntilElementClickable(cancelButton).click();
        getBrowser().waitUntilElementDisappears(dialogBody);
    }

    public void clickMoveButton()
    {
        LOG.info("Click Move button");
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        getBrowser().waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }
}