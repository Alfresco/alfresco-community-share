package org.alfresco.po.share.searching.dialogs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class SearchCopyMoveDialog extends BaseDialogComponent
{
    private final By createLinkButton = By.cssSelector("#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child>span>span");

    public SearchCopyMoveDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisplayed()
    {
        log.info("Assert Create Link button is displayed");
        waitUntilElementIsVisible(createLinkButton);
        assertTrue(isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisabled()
    {
        log.info("Assert Create Link button is disabled");
        assertEquals(findElement(createLinkButton).getAttribute("aria-disabled"),
            "true", "Create link button is disabled");
        return this;
    }
}
