package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
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

    public SelectDestinationDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        this.browser = browser;
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementClickable(okButton).click();
    }

    public void clickLinkButton()
    {
        getBrowser().waitUntilElementClickable(linkButton, 3).click();
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(cancelButton).click();
    }

    public void clickDestinationButton(String buttonText)
    {
        List<WebElement> list = getBrowser().findElements(destinationList);
        for (WebElement aDestinationList : list)
        {
            if (aDestinationList.getText().equals(buttonText))
                aDestinationList.click();
        }
    }

    public void clickSite(String siteName)
    {
        getBrowser().findFirstElementWithValue(siteList, siteName).click();
    }

    public boolean isSiteDisplayedInSiteSection(String siteName)
    {
        List<WebElement> list = getBrowser().findElements(siteList);
        for (WebElement aSiteList : list)
        {
            if (aSiteList.getText().equals(siteName))
            {
                return true;
            }
        }
        return false;
    }

    public String getPathList()
    {
        getBrowser().waitUntilElementsVisible(By.cssSelector(".path .ygtvlabel"));
        ArrayList<String> pathText = new ArrayList<>();
        List<WebElement> list = getBrowser().findElements(pathList);
        for (WebElement aPathList : list)
        {
            pathText.add(aPathList.getText());
        }
        return pathText.toString();
    }

    public String getPathFirstItem()
    {
        getBrowser().waitUntilElementsVisible(By.cssSelector(".path table[class*='ygtv-expanded'] .ygtvlabel"));
        return getBrowser().findElement(By.cssSelector(".path table[class*='ygtv-expanded'] .ygtvlabel")).getText();
    }

    public void clickPathFolder(String folderName)
    {
        getBrowser().waitUntilElementsVisible(By.cssSelector(".path .ygtvlabel"));
        List<WebElement> list = getBrowser().findElements(pathList);
        for (WebElement aPathList : list)
        {
            if (aPathList.getText().equals(folderName))
                aPathList.click();
        }
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void clickDocumentLibrary()
    {
        getBrowser().waitUntilElementVisible(documentLibraryPath).click();
        getBrowser().waitInSeconds(1);
    }

    public void selectSite(String siteName)
    {
        getBrowser().waitUntilElementsVisible(By.cssSelector("div.alfresco-pickers-SingleItemPicker div[id^='alfresco_menus_AlfMenuBarItem']"));
        getBrowser().findFirstElementWithValue(sitesListCopyFilesTo, siteName).click();
    }
}