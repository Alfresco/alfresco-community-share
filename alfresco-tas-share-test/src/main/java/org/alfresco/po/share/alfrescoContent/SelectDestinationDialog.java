package org.alfresco.po.share.alfrescoContent;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

/**
 * @author Laura.Capsa
 */
@PageObject
@Primary
public class SelectDestinationDialog extends ShareDialog
{
    @FindBy (css = "button[role='radio']")
    protected List<WebElement> destinationList;
    @FindBy (css = ".site-picker h4")
    protected List<WebElement> siteList;
    @FindBy (css = "div[id*='title']")
    private WebElement dialogTitle;
    @FindBy (css = "div.alfresco-pickers-SingleItemPicker div[id^='alfresco_menus_AlfMenuBarItem']")
    private List<WebElement> sitesListCopyFilesTo;
    @FindBy (css = ".path .ygtvlabel")
    private List<WebElement> pathList;

    @FindBy (css = "button[id*='ok']")
    private WebElement okButton;

    @FindBy (css = "button[id*='destinationDialog-cancel']")
    private WebElement cancelButton;

    @FindBy (css = "div.dijitTreeNodeContainer span[id^='alfresco_navigation_PathTree']")
    private WebElement documentLibraryPath;

    @FindBy (css = "#ALF_COPY_MOVE_DIALOG_title")
    private WebElement copyToDialogTitle;

    @FindBy (css = "button[id$='_default-rulesPicker-ok-button']")
    private WebElement linkButton;

    @FindBy (css = "div[title='All Sites'] span")
    private WebElement allSitesButton;

    public void clickOkButton()
    {
        getBrowser().waitInSeconds(5);
        getBrowser().waitUntilElementClickable(okButton).click();
    }

    public void clickLinkButton()
    {
        getBrowser().waitUntilElementClickable(linkButton, 3).click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    /**
     * Choose any button from "Destination" section
     *
     * @param buttonText to be set
     */
    public void clickDestinationButton(String buttonText)
    {
        for (WebElement aDestinationList : destinationList)
        {
            if (aDestinationList.getText().equals(buttonText))
                aDestinationList.click();
            if (buttonText.equals("Shared Files"))
                browser.waitInSeconds(5);
        }
        browser.waitInSeconds(1);
    }

    public void clickAllSitesButton()
    {
        browser.waitUntilElementClickable(allSitesButton, 10L);
        allSitesButton.click();
    }

    /**
     * Choose any site from "Site" section
     *
     * @param siteName to be set
     */
    public void clickSite(String siteName)
    {
        browser.findFirstElementWithValue(siteList, siteName).click();
        browser.waitInSeconds(2);
    }

    /**
     * Check site presence in "Site" section
     *
     * @param siteName to be verified
     * @return true if site is displayed
     */
    public boolean isSiteDisplayedInSiteSection(String siteName)
    {
        for (WebElement aSiteList : siteList)
        {
            if (aSiteList.getText().equals(siteName))
                return true;
        }
        return false;
    }

    /**
     * @return folders from "Path"
     */
    public String getPathList()
    {
        browser.waitUntilElementsVisible(By.cssSelector(".path .ygtvlabel"));
        ArrayList<String> pathText = new ArrayList<>();
        for (WebElement aPathList : pathList)
        {
            pathText.add(aPathList.getText());
        }
        return pathText.toString();
    }

    /**
     * @return first folder from path
     */

    public String getPathFirstItem()
    {
        browser.waitUntilElementsVisible(By.cssSelector(".path table[class*='ygtv-expanded'] .ygtvlabel"));
        return browser.findElement(By.cssSelector(".path table[class*='ygtv-expanded'] .ygtvlabel")).getText();
    }

    /**
     * Choose any folder from "Path" section
     *
     * @param folderName to be set
     */
    public void clickPathFolder(String folderName)
    {
        browser.waitUntilElementsVisible(By.cssSelector(".path .ygtvlabel"));
        browser.waitInSeconds(2);
        for (WebElement aPathList : pathList)
        {
            if (aPathList.getText().equals(folderName))
                aPathList.click();
        }
    }

    /**
     * @return dialog's title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    public void clickDocumentLibrary()
    {
        getBrowser().waitUntilElementVisible(documentLibraryPath).click();
        getBrowser().waitInSeconds(1);
    }

    public String getCopyToDialogTitle()
    {
        browser.waitUntilElementVisible(copyToDialogTitle);
        return copyToDialogTitle.getText();
    }

    public void selectSite(String siteName)
    {
        getBrowser().waitUntilElementsVisible(By.cssSelector("div.alfresco-pickers-SingleItemPicker div[id^='alfresco_menus_AlfMenuBarItem']"));
        browser.findFirstElementWithValue(sitesListCopyFilesTo, siteName).click();
    }
}