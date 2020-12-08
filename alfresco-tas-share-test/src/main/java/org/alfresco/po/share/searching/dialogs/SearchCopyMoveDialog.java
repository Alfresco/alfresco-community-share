package org.alfresco.po.share.searching.dialogs;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SearchCopyMoveDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By createLinkButton = By.cssSelector("#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child>span>span");

    public SearchCopyMoveDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        getBrowser().waitUntilElementVisible(createLinkButton);
        assertTrue(getBrowser().isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisabled()
    {
        LOG.info("Assert Create Link button is disabled");
        assertEquals(getBrowser().findElement(createLinkButton).getAttribute("aria-disabled"),
            "true", "Create link button is disabled");
        return this;
    }
}
