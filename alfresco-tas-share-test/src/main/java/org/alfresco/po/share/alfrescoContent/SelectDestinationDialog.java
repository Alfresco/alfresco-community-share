package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class SelectDestinationDialog extends BaseDialogComponent
{
    private final By destinationList = By.cssSelector("button[role='radio']");
    private final By siteList = By.cssSelector(".site-picker h4");
    private final By dialogTitle = By.cssSelector("div[id*='title']");
    private final By sitesListCopyFilesTo = By.cssSelector("div.alfresco-pickers-SingleItemPicker div[id^='alfresco_menus_AlfMenuBarItem']");
    private final By pathList = By.cssSelector(".path .ygtvlabel");
    private final By okButton = By.cssSelector("button[id*='ok']");
    private final By cancelButton = By.cssSelector("button[id*='destinationDialog-cancel']");
    private final By linkButton = By.cssSelector("button[id$='_default-rulesPicker-ok-button']");

    public SelectDestinationDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void clickOkButton()
    {
        webElementInteraction.clickElement(okButton);
    }

    public void clickLinkButton()
    {
        webElementInteraction.clickElement(linkButton);
    }

    public void clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public void clickSite(String siteName)
    {
        webElementInteraction.findFirstElementWithValue(siteList, siteName).click();
    }

    public void clickPathFolder(String folderName)
    {
        webElementInteraction.waitUntilElementsAreVisible(By.cssSelector(".path .ygtvlabel"));
        List<WebElement> list = webElementInteraction.findElements(pathList);
        for (WebElement aPathList : list)
        {
            if (aPathList.getText().equals(folderName))
                aPathList.click();
        }
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public void selectSite(String siteName)
    {
        webElementInteraction.waitUntilElementsAreVisible(By.cssSelector("div.alfresco-pickers-SingleItemPicker div[id^='alfresco_menus_AlfMenuBarItem']"));
        webElementInteraction.clickElement(webElementInteraction.findFirstElementWithValue(sitesListCopyFilesTo, siteName));
    }
}