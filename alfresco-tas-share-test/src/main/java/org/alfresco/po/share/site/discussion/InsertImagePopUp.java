package org.alfresco.po.share.site.discussion;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.CheckBox;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Created by Claudia Agache on 8/11/2016.
 */
@PageObject
public class InsertImagePopUp extends ShareDialog
{
    @RenderWebElement
    @FindBy(className = "mce-title")
    private WebElement popupTitle;

    @RenderWebElement
    @FindBy(xpath = "//button[text()='Ok']")
    private Button okButton;

    @FindBy(xpath = "//div[@aria-label='Insert/edit image']//button[text()='Cancel']")
    private Button cancelButton;

    @FindBy(className = "mce-close")
    private Button closeButton;

    @FindBy(xpath = "//label[text()='Source']/following-sibling::*/input")
    private TextInput sourceInput;

    @FindBy(xpath = "//label[text()='Image description']/following-sibling::input")
    private TextInput imageDescriptionInput;

    @FindBy(css = "input[aria-label='Width']")
    private TextInput widthInput;

    @FindBy(css = "input[aria-label='Height']")
    private TextInput heightInput;

    @FindBy(xpath = "//div[@role='checkbox' and descendant-or-self::*[text()='Constrain proportions']]")
    private CheckBox constrainProportionsCheckbox;

    public TopicViewPage insertImage(String source, String description)
    {
        sourceInput.clear();
        sourceInput.sendKeys(source);
        imageDescriptionInput.clear();
        imageDescriptionInput.sendKeys(description);
        okButton.click();
        return new TopicViewPage();
    }

    public String getPopupTitle()
    {
        return popupTitle.getText();
    }
}
