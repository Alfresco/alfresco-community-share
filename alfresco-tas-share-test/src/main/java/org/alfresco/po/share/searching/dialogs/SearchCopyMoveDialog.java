package org.alfresco.po.share.searching.dialogs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SearchCopyMoveDialog extends BaseDialogComponent
{
    private final By createLinkButton = By.cssSelector("#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child>span>span");

    public SearchCopyMoveDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        webElementInteraction.waitUntilElementIsVisible(createLinkButton);
        assertTrue(webElementInteraction.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisabled()
    {
        LOG.info("Assert Create Link button is disabled");
        assertEquals(webElementInteraction.findElement(createLinkButton).getAttribute("aria-disabled"),
            "true", "Create link button is disabled");
        return this;
    }
}
