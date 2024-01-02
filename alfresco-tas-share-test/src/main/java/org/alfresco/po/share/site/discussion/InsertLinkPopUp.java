package org.alfresco.po.share.site.discussion;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by Claudia Agache on 8/11/2016.
 */
public class InsertLinkPopUp extends BaseDialogComponent
{
    private TopicViewPage topicViewPage;
    private final By targetButton = By.xpath("//label[text()='Target']/following-sibling::*/button");
    private final By titleInput = By.xpath("//label[text()='Title']/following-sibling::input");
    private final By textToDisplayInput = By.xpath("//label[text()='Text to display']/following-sibling::input");
    private final By urlInput = By.xpath("//label[text()='Url']/following-sibling::*/input");
    private final By okButton = By.xpath("//span[text()='Ok']");
    private final By insertLinkPopup = By.xpath(".//*[@class='mce-reset']");
    private final By targetMenuItem = By.className("mce-text");

    public InsertLinkPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
        topicViewPage = new TopicViewPage(webDriver);
    }

    public void insertLink(String url, String text, String title, String target)
    {
        findElement(urlInput).sendKeys(url);
        findElement(textToDisplayInput).clear();
        findElement(textToDisplayInput).sendKeys(text);
        findElement(titleInput).sendKeys(title);
        waitInSeconds(2);
        findElement(targetButton).click();
        findFirstElementWithValue(findElements(targetMenuItem), target).click();
        waitInSeconds(2);
        findElement(okButton).click();
    }

    public boolean isTextPresent(String text)
    {
        waitUntilElementContainsText(findElement(insertLinkPopup), text);
        return findElement(insertLinkPopup).getText().contains(text);
    }
}
