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
    private final By documentLibraryPath = By.cssSelector("div.dijitTreeNodeContainer span[id^='alfresco_navigation_PathTree']");
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

    public void clickDestinationButton(String buttonText)
    {
        List<WebElement> list = webElementInteraction.findElements(destinationList);
        for (WebElement aDestinationList : list)
        {
            if (aDestinationList.getText().equals(buttonText))
                aDestinationList.click();
        }
    }

    public void clickSite(String siteName)
    {
        webElementInteraction.findFirstElementWithValue(siteList, siteName).click();
    }

    public String getPathList()
    {
        webElementInteraction.waitUntilElementsAreVisible(By.cssSelector(".path .ygtvlabel"));
        ArrayList<String> pathText = new ArrayList<>();
        List<WebElement> list = webElementInteraction.findElements(pathList);
        for (WebElement aPathList : list)
        {
            pathText.add(aPathList.getText());
        }
        return pathText.toString();
    }

    public String getPathFirstItem()
    {
        webElementInteraction.waitUntilElementsAreVisible(By.cssSelector(".path table[class*='ygtv-expanded'] .ygtvlabel"));
        return webElementInteraction.findElement(By.cssSelector(".path table[class*='ygtv-expanded'] .ygtvlabel")).getText();
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
        webElementInteraction.findFirstElementWithValue(sitesListCopyFilesTo, siteName).click();
    }
}