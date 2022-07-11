package org.alfresco.po.share.site;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class RenameSitePageDialog extends BaseDialogComponent
{
    private final By pageNameLabel = By.cssSelector(".bd>label");
    private final By displayNameInput = By.cssSelector(".bd > input");
    private final By okButton = By.cssSelector(".button-group > span[class$='push-button default']");

    protected RenameSitePageDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isPageNameLabelDisplayed()
    {
        return isElementDisplayed(pageNameLabel);
    }

    public void typeDisplayName(String newName)
    {
        clearAndType(displayNameInput, newName);
    }

    public void clickOk()
    {
        clickElement(okButton);
    }
}
