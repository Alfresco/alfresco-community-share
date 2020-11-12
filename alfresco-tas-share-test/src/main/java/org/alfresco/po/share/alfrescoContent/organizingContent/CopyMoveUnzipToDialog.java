package org.alfresco.po.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CopyMoveUnzipToDialog extends SelectDestinationDialog
{
    @FindAll (@FindBy (css = "div[id='ALF_COPY_MOVE_DIALOG'] span[class*='alfresco-buttons-AlfButton']"))
    private List<WebElement> buttonsList;

    @FindBy (css = ".message")
    private WebElement message;

    @FindBy (css = "button[id$='_default-copyMoveTo-link-button']")
    private WebElement createLinkButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-copyMoveTo-ok-button']")
    private WebElement unzipCopyMoveButton;

    @FindBy (css = "button[id$='_default-copyMoveTo-cancel-button']")
    private WebElement cancelButton;

    @FindBy (css = "button[id$='copyMoveTo-recentsites-button']")
    private WebElement recentSitesDestination;

    @FindBy (css = "div[id$='default-copyMoveTo-treeview']")
    private WebElement folderPathsArea;

    private By createLinkMessage = By.cssSelector("div[id*='message_c'] .bd .message");
    private String siteToSelect = "//h4[text()='%s']";
    private String folderElementToSelect = "//span[@class='ygtvlabel' and text()='%s']";
    private String folderElementToSelectRow = "//span[@class='ygtvlabel' and text()='%s']/../../../../..";

    public CopyMoveUnzipToDialog selectRecentSitesDestination()
    {
        LOG.info("Select Recent Sites");
        recentSitesDestination.click();
        return this;
    }

    public CopyMoveUnzipToDialog selectSite(SiteModel site)
    {
        LOG.info("Select site {}", site.getTitle());
        browser.waitUntilElementVisible(By.xpath(String.format(siteToSelect, site.getTitle()))).click();
        return this;
    }

    public CopyMoveUnzipToDialog selectFolder(FolderModel folderToSelect)
    {
        LOG.info("Select folder {}", folderToSelect.getName());
        By folderRow = By.xpath(String.format(folderElementToSelectRow, folderToSelect.getName()));
        By folder = By.xpath(String.format(folderElementToSelect, folderToSelect.getName()));
        browser.waitUntilElementVisible(folderPathsArea);
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
                folderPathsArea.findElement(folderElement).click();
                if(folderPathsArea.findElement(folderRow).getAttribute("class").contains("selected"))
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

    /**
     * Click on a button from the bottom of Copy/MoveTo dialog
     *
     * @param buttonName name of the button to be clicked (e.g: Move, Cancel)
     */
    public void clickButton(String buttonName)
    {
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                aButtonsList.click();
        }
    }

    public void clickCreateLinkButton()
    {
        LOG.info("Click Create Link button");
        browser.waitUntilElementClickable(createLinkButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public boolean isCreateLinkButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(createLinkButton);
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        assertTrue(browser.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public CopyMoveUnzipToDialog assertCreateLinkButtonIsNotDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        assertFalse(browser.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public void clickUnzipButton()
    {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton, 3).click();
    }

    public void clickCopyButton()
    {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        waitUntilMessageDisappears();
    }

    public void clickCancelButton()
    {
        getBrowser().waitUntilElementClickable(cancelButton, 3).click();
    }

    public void clickMoveButton()
    {
        getBrowser().waitUntilElementClickable(unzipCopyMoveButton).click();
        waitUntilMessageDisappears();
    }
}