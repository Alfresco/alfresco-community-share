package org.alfresco.po.share.alfrescoContent.organizingContent;

import static org.alfresco.common.Wait.WAIT_10;
import static org.alfresco.common.Wait.WAIT_15;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
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

public class CopyMoveUnzipToDialog extends BaseDialogComponent
{
    private final By createLinkButton = By.cssSelector("button[id$='_default-copyMoveTo-link-button']");
    private final By dialogTitle = By.cssSelector("div[id*='title']");
    private final By unzipCopyMoveButton = By.cssSelector("button[id$='_default-copyMoveTo-ok-button']");
    private final By cancelButton = By.cssSelector("button[id$='_default-copyMoveTo-cancel-button']");
    private final By recentSitesDestination = By.cssSelector("button[id$='copyMoveTo-recentsites-button']");
    private final By sharedFilesDestination = By.cssSelector("span[id$='default-copyMoveTo-shared']");
    private final By myFilesDestination = By.cssSelector("button[id$='copyMoveTo-myfiles-button']");
    private final By allSitesDestination = By.cssSelector("button[id$='copyMoveTo-site-button']");
    private final By folderPathsArea = By.cssSelector("div[id$='default-copyMoveTo-treeview']");
    private final By sitePickerArea = By.cssSelector(".site-picker");
    private final By dialogBody = By.cssSelector("div[id$='default-copyMoveTo-dialog']");
    private final By documentsRootPath = By.cssSelector("div[id$='default-copyMoveTo-treeview'] div[class='ygtvitem selected']>table span");

    private final String siteToSelect = "//h4[text()='%s']";
    private final String folderElementToSelect = "//span[@class='ygtvlabel' and text()='%s']";
    private final String folderElementToSelectRow = "//span[@class='ygtvlabel' and text()='%s']/../../../../..";

    private final String destinationChecked = "yui-radio-button-checked";

    public CopyMoveUnzipToDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public CopyMoveUnzipToDialog selectRecentSitesDestination()
    {
        LOG.info("Select Recent Sites");
        webElementInteraction.clickElement(recentSitesDestination);
        webElementInteraction.waitUntilElementIsVisible(sitePickerArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectSharedFilesDestination()
    {
        LOG.info("Select Shared Files destination");
        WebElement shared = webElementInteraction.waitUntilElementIsVisible(sharedFilesDestination);
        webElementInteraction.mouseOver(shared);
        webElementInteraction.clickElement(shared);
        webElementInteraction.waitUntilElementHasAttribute(shared, "class", destinationChecked);
        webElementInteraction.waitUntilElementIsVisible(folderPathsArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectMyFilesDestination()
    {
        LOG.info("Select My Files destination");
        webElementInteraction.clickElement(myFilesDestination);
        webElementInteraction.waitUntilElementIsVisible(folderPathsArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectAllSitesDestination()
    {
        LOG.info("Select Shared Files destination");
        webElementInteraction.clickElement(allSitesDestination);
        webElementInteraction.waitUntilElementIsVisible(sitePickerArea);
        return this;
    }

    public CopyMoveUnzipToDialog selectSite(SiteModel site)
    {
        LOG.info("Select site {}", site.getTitle());
        WebElement sitePicker = webElementInteraction.waitUntilElementIsVisible(sitePickerArea);
        WebElement siteElement = webElementInteraction.waitUntilChildElementIsPresent(sitePicker, By.xpath(String.format(siteToSelect, site.getTitle())));
        webElementInteraction.clickElement(siteElement);
        webElementInteraction.waitUntilElementIsPresent(folderPathsArea);
        webElementInteraction.waitUntilElementIsVisible(folderPathsArea);
        waitForDocumentsPathAndClick();

        return this;
    }

    private void waitForDocumentsPathAndClick()
    {
        int i = 0;
        while(i < WAIT_10.getValue())
        {
            try
            {
                WebElement documents = webElementInteraction.waitUntilElementIsVisible(documentsRootPath);
                webElementInteraction.mouseOver(documents, 2000);
                webElementInteraction.clickElement(documentsRootPath);
                break;
            }
            catch (StaleElementReferenceException | ElementNotInteractableException exception)
            {
                i++;
                Utility.waitToLoopTime(1,"Wait for Documents link to be clickable");
            }
        }
    }

    public CopyMoveUnzipToDialog selectFolder(FolderModel folderToSelect)
    {
        LOG.info("Select folder {}", folderToSelect.getName());
        By folderRow = By.xpath(String.format(folderElementToSelectRow, folderToSelect.getName()));
        By folder = By.xpath(String.format(folderElementToSelect, folderToSelect.getName()));
        webElementInteraction.waitUntilElementIsVisible(folderPathsArea);
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
                webElementInteraction.clickElement(folderElement);
                if(webElementInteraction.findElement(folderRow).getAttribute("class").contains("selected"))
                {
                    break;
                }
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
        webElementInteraction.clickElement(createLinkButton);
        waitUntilNotificationMessageDisappears();
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        webElementInteraction.waitUntilElementIsVisible(createLinkButton);
        assertTrue(webElementInteraction.isElementDisplayed(createLinkButton), "Create link button is not displayed");
        return this;
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsNotDisplayed()
    {
        LOG.info("Assert Create Link button is not displayed");
        assertFalse(webElementInteraction.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public void clickUnzipButton()
    {
        LOG.info("Click Unzip To button");
        webElementInteraction.clickElement(unzipCopyMoveButton);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCopyToButton()
    {
        LOG.info("Click Copy To button");
        webElementInteraction.clickElement(unzipCopyMoveButton);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancelButton()
    {
        LOG.info("Click Cancel button");
        webElementInteraction.clickElement(cancelButton);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
    }

    public void clickMoveButton()
    {
        LOG.info("Click Move button");
        webElementInteraction.clickElement(unzipCopyMoveButton);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
        waitUntilNotificationMessageDisappears();
    }
}