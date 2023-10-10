package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 2/22/2018.
 */
@PageObject
public class CommentPage extends HtmlPage
{
    @RenderWebElement
    @FindBy (css = "form[id$='_default-add-form']")
    private WebElement commentForm;

    @RenderWebElement
    @FindBy (css = "div[aria-label='Bold'] button")
    private WebElement boldButton;

    @FindBy (xpath = "//iframe[contains(@title,'Rich Text Area')]")
    private WebElement frame;

    @FindBy (xpath = "//div[@aria-label='Bold']//button/..")
    private WebElement boldButtonState;
    private By boldText = By.xpath("//body[@id='tinymce']//p//strong");
    private String frameId = null;

    public String getFrameId()
    {
        return frameId;
    }

    public void clickBoldButton()
    {
        boldButton.click();
    }

    public boolean isBoldButtonPressed()
    {
        boolean state = false;
        String value = boldButtonState.getAttribute("aria-pressed");
        if (value.equals("true"))
        {
            state = true;
        } else
        {
            state = false;
        }

        return state;
    }

    public String getBoldText()
    {
        browser.waitInSeconds(5);
        browser.switchToFrame("tinymce");
        browser.waitUntilElementIsVisibleWithRetry(boldText, 3);
        browser.switchToDefaultContent();
        return browser.findElement(boldText).getText();
    }

    public boolean isTextBold()
    {
        browser.waitInSeconds(3);
        boolean isBold = false;
        try
        {
            // browser.switchToFrame(getFrameId());
            browser.waitUntilElementIsVisibleWithRetry(boldText, 3);
            if (browser.isElementDisplayed(boldText))
            {
                isBold = true;
            } else
            {
                isBold = false;
            }
        } catch (Exception ex)
        {
            isBold = false;
        }
        browser.switchToDefaultContent();
        return isBold;
    }
}
