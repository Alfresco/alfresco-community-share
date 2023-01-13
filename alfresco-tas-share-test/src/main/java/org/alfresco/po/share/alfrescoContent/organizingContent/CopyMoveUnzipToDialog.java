package org.alfresco.po.share.alfrescoContent.organizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.alfresco.common.Wait.WAIT_10;
import static org.alfresco.common.Wait.WAIT_15;

import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class CopyMoveUnzipToDialog extends BaseDialogComponent
{
    private final By createLinkButton = By.cssSelector("button[id$='_default-copyMoveTo-link-button']");
    private final By dialogTitle = By.cssSelector("div[id*='title']");
    private final By unzipCopyMoveButton = By.cssSelector("button[id$='_default-copyMoveTo-ok-button']");
    private final By cancelButton = By.cssSelector("button[id$='_default-copyMoveTo-cancel-button']");
    private final By copyButton = By.xpath("//span[text()=\"Copy\"]");
    private final By moveButton = By.xpath("//span[text()=\"Move\"]");
    private final By recentSitesDestination = By.cssSelector("button[id$='copyMoveTo-recentsites-button']");
    private final By sharedFilesDestination = By.cssSelector("span[id$='default-copyMoveTo-shared']");
    private final By myFilesDestination = By.cssSelector("button[id$='copyMoveTo-myfiles-button']");
    private final By allSitesDestination = By.cssSelector("button[id$='copyMoveTo-site-button']");
    private final By folderPathsArea = By.cssSelector("div[id$='default-copyMoveTo-treeview']");
    private final By sitePickerArea = By.cssSelector(".site-picker");
    private final By allSites = By.xpath("(//span[@class=\"alf-menu-bar-label-node\"])[9]");
    private final By site_PickerArea = By.xpath("//div[@class=\"alfresco-pickers-SingleItemPicker\"]");
    private final By dialogBody = By.cssSelector("div[id$='default-copyMoveTo-dialog']");
    private final By documentLibraryPath = By.xpath("//div[@class=\"alfresco-navigation-Tree \"]//span[text()=\"documentLibrary\"]");
    private final By documentsRootPath = By.cssSelector("div[id$='default-copyMoveTo-treeview'] div[class='ygtvitem selected']>table span");
    private final String siteToSelect = "//h4[text()='%s']";
    private final String site_ToSelect = "//span[text()='%s']";
    private final String folderElementToSelect = "//span[@class='ygtvlabel' and text()='%s']";
    private final String folderElementToSelectRow = "//span[@class='ygtvlabel' and text()='%s']/../../../../..";
    private final String destinationChecked = "yui-radio-button-checked";

    public CopyMoveUnzipToDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public CopyMoveUnzipToDialog selectRecentSitesDestination()
    {
        log.info("Select Recent Sites");
        clickElement(recentSitesDestination);
        waitUntilElementIsVisible(sitePickerArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectSharedFilesDestination()
    {
        log.info("Select Shared Files destination");
        WebElement shared = waitUntilElementIsVisible(sharedFilesDestination);
        mouseOver(shared);
        clickElement(shared);
        waitUntilElementHasAttribute(shared, "class", destinationChecked);
        waitUntilElementIsVisible(folderPathsArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectMyFilesDestination()
    {
        log.info("Select My Files destination");
        clickElement(myFilesDestination);
        waitUntilElementIsVisible(folderPathsArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectAllSitesDestination()
    {
        log.info("Select Shared Files destination");
        clickElement(allSitesDestination);
        waitUntilElementIsVisible(sitePickerArea);
        return this;
    }
    public CopyMoveUnzipToDialog select_AllSitesDestination()
    {
        log.info("Select Shared Files destination");
        clickElement(allSites);
        return this;
    }

    public CopyMoveUnzipToDialog selectSite(SiteModel site)
    {
        log.info("Select site {}", site.getTitle());
        WebElement sitePicker = waitUntilElementIsVisible(sitePickerArea);
        WebElement siteElement = waitUntilChildElementIsPresent(sitePicker, By.xpath(String.format(siteToSelect, site.getTitle())));
        clickElement(siteElement);
        waitUntilElementIsPresent(folderPathsArea);
        waitUntilElementIsVisible(folderPathsArea);
        waitForDocumentsPathAndClick();

        return this;
    }
    public CopyMoveUnzipToDialog select_Site(SiteModel site)
    {
        log.info("Select site {}", site.getTitle());
        WebElement sitePicker = waitUntilElementIsVisible(site_PickerArea);
        WebElement siteElement = waitUntilChildElementIsPresent(sitePicker, By.xpath(String.format(site_ToSelect, site.getTitle())));
        clickElement(siteElement);
        waitInSeconds(5);

        return this;
    }

    private void waitForDocumentsPathAndClick()
    {
        int retryCounter = 0;
        while(retryCounter < WAIT_10.getValue())
        {
            try
            {
                WebElement documents = waitUntilElementIsVisible(documentsRootPath);
                mouseOver(documents, 2000);
                clickElement(documentsRootPath);
                break;
            }
            catch (StaleElementReferenceException | ElementNotInteractableException exception)
            {
                retryCounter++;
                Utility.waitToLoopTime(1,"Wait for Documents link to be clickable");
            }
        }
    }

    public CopyMoveUnzipToDialog selectFolder(FolderModel folderToSelect)
    {
        log.info("Select folder {}", folderToSelect.getName());
        By folderRow = By.xpath(String.format(folderElementToSelectRow, folderToSelect.getName()));
        By folder = By.xpath(String.format(folderElementToSelect, folderToSelect.getName()));
        waitUntilElementIsVisible(folderPathsArea);
        waitAndSelectFolder(folder, folderRow);
        return this;
    }

    private void waitAndSelectFolder(By folderElement, By folderRow)
    {
        int retry = 0;
        while(retry < WAIT_15.getValue())
        {
            try
            {
                clickElement(folderElement);
                if(findElement(folderRow).getAttribute("class").contains("selected"))
                {
                    break;
                }
            }
            catch (ElementClickInterceptedException | StaleElementReferenceException | NoSuchElementException e)
            {
                retry++;
                log.info("Retry select folder - {}", retry);
            }
        }
    }

    public void clickCreateLinkButton()
    {
        log.info("Click Create Link button");
        clickElement(createLinkButton);
        waitUntilNotificationMessageDisappears();
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsDisplayed()
    {
        log.info("Assert Create Link button is displayed");
        waitUntilElementIsVisible(createLinkButton);
        assertTrue(isElementDisplayed(createLinkButton), "Create link button is not displayed");
        return this;
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsNotDisplayed()
    {
        log.info("Assert Create Link button is not displayed");
        assertFalse(isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public CopyMoveUnzipToDialog assertDialogTitleEquals(String contentName)
    {
        log.info("Verify the Dialog Title...");
        assertEquals(getDialogTitle(), contentName, String.format("Dialog title not matched with [%s]", contentName));
        return this;
    }

    public void clickUnzipButton()
    {
        log.info("Click Unzip To button");
        clickElement(unzipCopyMoveButton);
        waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCopyToButton()
    {
        log.info("Click Copy To button");
        clickElement(unzipCopyMoveButton);
        waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancelButton()
    {
        log.info("Click Cancel button");
        clickElement(cancelButton);
        waitUntilElementDisappears(dialogBody);
    }

    public void clickMoveButton()
    {
        log.info("Click Move button");
        clickElement(unzipCopyMoveButton);
        waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }
    public CopyMoveUnzipToDialog clickDocumentsFolder()
    {
        log.info("Click Documents Folder");
        clickElement(documentLibraryPath);
        return this;
    }
    public CopyMoveUnzipToDialog clickCopyButton()
    {
        log.info("Click Documents Folder");
        waitInSeconds(3);
        clickElement(copyButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }
    public CopyMoveUnzipToDialog click_MoveButton()
    {
        log.info("Click Documents Folder");
        waitInSeconds(3);
        clickElement(moveButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }
}
