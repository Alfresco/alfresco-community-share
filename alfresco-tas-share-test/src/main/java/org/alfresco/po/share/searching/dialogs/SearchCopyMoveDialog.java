package org.alfresco.po.share.searching.dialogs;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;

public class SearchCopyMoveDialog extends ShareDialog2
{
    @RenderWebElement
    private By dialogBody = By.id("ALF_COPY_MOVE_DIALOG");
    private By createLinkButton = By.cssSelector("#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child>span>span");

    public SearchCopyMoveDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
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
        assertTrue(getBrowser().findElement(createLinkButton).getAttribute("aria-disabled").equals("true"),
            "Create link button is disabled");
        return this;
    }
}
