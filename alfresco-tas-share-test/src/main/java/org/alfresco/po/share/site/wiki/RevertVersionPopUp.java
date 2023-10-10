package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class RevertVersionPopUp extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "[id*=revertWikiVersion-instance-ok]")
    private WebElement revertOkButton;

    @RenderWebElement
    @FindBy (css = "[id*=revertWikiVersion-instance-cancel]")
    private WebElement revertCancelButton;

    @FindBy (css = "[id*=revertWikiVersion-instance-prompt]")
    private WebElement revertMessage;

    public void clickRevertOk()
    {
        revertOkButton.click();
        browser.waitInSeconds(2);
    }

    public String getRevertMessage()
    {
        return revertMessage.getText();
    }

}
