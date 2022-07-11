package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_3;
import static org.alfresco.common.Wait.WAIT_5;

import org.alfresco.common.Wait;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class SelectDestinationDialog extends BaseDialogComponent
{
    private final By siteList = By.cssSelector(".site-picker h4");
    private final By pathList = By.cssSelector(".path .ygtvlabel");
    private final By okButton = By.cssSelector("button[id*='ok']");
    private final By cancelButton = By.cssSelector("button[id*='destinationDialog-cancel']");
    private final By linkButton = By.cssSelector("button[id$='_default-rulesPicker-ok-button']");

    public SelectDestinationDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SelectDestinationDialog confirmFolderLocation()
    {
        clickElement(okButton);
        return this;
    }

    public void clickLinkButton()
    {
        clickElement(linkButton);
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
    }

    public SelectDestinationDialog selectSite(String siteName)
    {
        clickElement(findFirstElementWithValue(siteList, siteName));
        waitInSeconds(WAIT_2.getValue());
        return this;
    }

    public SelectDestinationDialog selectFolderPath(String folderName)
    {
        List<WebElement> paths = waitUntilElementsAreVisible(pathList);
        for (WebElement path : paths)
        {
            if (getElementText(path).equals(folderName))
            {
                try
                {
                    clickElement(path);
                }
                catch (StaleElementReferenceException e)
                {
                    waitUntilElementIsVisible(path);
                    mouseOver(path);
                    clickElement(path);
                }
            }
        }
        return this;
    }
}