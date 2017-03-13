package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
@Primary
public class SelectDestinationDialog extends ShareDialog
{
    @FindBy(css = "div[id*='title']")
    private WebElement dialogTitle;

    @FindBy(css = "button[role='radio']")
    protected List<WebElement> destinationList;

    @FindBy(css = ".site-picker h4")
    protected List<WebElement> siteList;

    @FindBy(css = ".path .ygtvlabel")
    private List<WebElement> pathList;

    @FindBy(css = "button[id*='ok']")
    private WebElement okButton;

    @FindBy(css = "button[id*='destinationDialog-cancel']")
    private WebElement cancelButton;

    public void clickOkButton()
    {
        okButton.click();
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
        for (int i = 0; i < destinationList.size(); i++)
        {
            if (destinationList.get(i).getText().equals(buttonText))
                destinationList.get(i).click();
            if (buttonText.equals("Shared Files"))
                browser.waitInSeconds(5);
        }
        browser.waitInSeconds(1);
    }

    /**
     * Choose any site from "Site" section
     *
     * @param siteName to be set
     */
    public void clickSite(String siteName)
    {
        for (int i = 0; i < siteList.size(); i++)
        {
            if (siteList.get(i).getText().equals(siteName))
                siteList.get(i).click();
        }
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
        for (int i = 0; i < siteList.size(); i++)
        {
            if (siteList.get(i).getText().equals(siteName))
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
        for (int i = 0; i < pathList.size(); i++)
        {
            pathText.add(pathList.get(i).getText());
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
        for (int i = 0; i < pathList.size(); i++)
        {
            if (pathList.get(i).getText().equals(folderName))
                pathList.get(i).click();
        }
    }

    /**
     * @return dialog's title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }
}