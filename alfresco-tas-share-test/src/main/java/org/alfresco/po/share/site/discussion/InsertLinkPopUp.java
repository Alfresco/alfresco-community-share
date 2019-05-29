package org.alfresco.po.share.site.discussion;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Created by Claudia Agache on 8/11/2016.
 */
@PageObject
public class InsertLinkPopUp extends ShareDialog
{
    @RenderWebElement
    @FindBy (className = "mce-title")
    private WebElement popupTitle;

    @RenderWebElement
    @FindBy (xpath = "//button[text()='Ok']")
    private Button okButton;

    @FindBy (xpath = "//button[text()='Cancel']")
    private Button cancelButton;

    @FindBy (className = "mce-close")
    private Button closeButton;

    @FindBy (xpath = "//label[text()='Url']/following-sibling::*/input")
    private TextInput urlInput;

    @FindBy (xpath = "//label[text()='Text to display']/following-sibling::input")
    private TextInput textToDisplayInput;

    @FindBy (xpath = "//label[text()='Title']/following-sibling::input")
    private TextInput titleInput;

    @FindBy (xpath = "//label[text()='Target']/following-sibling::*/button")
    private Button targetButton;

    @FindBy (xpath = ".//*[@class='mce-reset']")
    private WebElement insertLinkPopup;

    private By targetMenu = By.cssSelector("#mce-modal-block+div");
    private By targetMenuItem = By.className("mce-text");

    public TopicViewPage insertLink(String url, String text, String title, String target)
    {
        urlInput.sendKeys(url);
        textToDisplayInput.clear();
        textToDisplayInput.sendKeys(text);
        titleInput.sendKeys(title);
        targetButton.click();
        WebElement menu = browser.waitUntilElementVisible(targetMenu);
        browser.findFirstElementWithValue(menu.findElements(targetMenuItem), target).click();
        okButton.click();
        return new TopicViewPage();
    }

    public String getPopupTitle()
    {
        return popupTitle.getText();
    }

    public boolean isTextPresent(String text)
    {
        browser.waitUntilElementContainsText(insertLinkPopup, text);
        return insertLinkPopup.getText().contains(text);
    }
}
