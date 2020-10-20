package org.alfresco.po.share.searching.dialogs;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@PageObject
public class SearchCopyMoveDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy(id = "ALF_COPY_MOVE_DIALOG")
    private WebElement dialogBody;

    @FindBy(css = "#ALF_COPY_MOVE_DIALOG span[class*='call-to-action']:first-child>span>span")
    private WebElement createLinkButton;

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisplayed()
    {
        LOG.info("Assert Create Link button is displayed");
        browser.waitUntilElementVisible(createLinkButton);
        Assert.assertTrue(browser.isElementDisplayed(createLinkButton), "Create link button is displayed");
        return this;
    }

    public SearchCopyMoveDialog assertCreateLinkButtonIsDisabled()
    {
        LOG.info("Assert Create Link button is disabled");
        Assert.assertTrue(createLinkButton.getAttribute("aria-disabled").equals("true"), "Create link button is disabled");
        return this;
    }
}
