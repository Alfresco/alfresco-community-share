package org.alfresco.po.share.site.discussion;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InsertImagePopUp extends BaseDialogComponent
{
    private TopicViewPage topicViewPage;
    private final By imageDescriptionInput = By.xpath("//label[text()='Image description']/following-sibling::input");
    private final By sourceInput = By.xpath("//label[text()='Source']/following-sibling::*/input");
    private final By okButton = By.xpath("//span[text()='Ok']");
    private final By popupTitle = By.className("mce-title");

    public InsertImagePopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
        topicViewPage = new TopicViewPage(webDriver);
    }

    public void insertImage(String source, String description)
    {
        findElement(sourceInput).clear();
        findElement(sourceInput).sendKeys(source);
        findElement(imageDescriptionInput).clear();
        findElement(imageDescriptionInput).sendKeys(description);
        findElement(okButton).click();
    }

    public String getPopupTitle()
    {
        return findElement(popupTitle).getText();
    }
}
