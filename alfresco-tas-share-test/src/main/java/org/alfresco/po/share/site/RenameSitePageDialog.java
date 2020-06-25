package org.alfresco.po.share.site;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * @author bogdan.bocancea
 */

@PageObject
public class RenameSitePageDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = ".bd>label")
    private WebElement pageNameLabel;

    @FindBy (css = ".bd > input")
    private WebElement displayNameInput;

    @FindBy (css = ".button-group > span[class$='push-button default']")
    private Button okButton;

    public boolean isPageNameLabelDisplayed()
    {
        return browser.isElementDisplayed(pageNameLabel);
    }

    public void typeDisplayName(String newName)
    {
        Parameter.checkIsMandotary("New Display Name", newName);
        Utils.clearAndType(displayNameInput, newName);
    }

    public void clickOk()
    {
        okButton.click();
    }
}
