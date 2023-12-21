package org.alfresco.po.share.site.discussion;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
public class EditTopicPage extends CreateNewTopicPage
{
    private final By topicTitle = By.cssSelector("input[id$='discussions-createtopic_x0023_default-title']");
    private final By pageHeader = By.cssSelector(".page-form-header>h1");
    public EditTopicPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    /**
     * Check if page is opened
     *
     * @return true if page header is Edit Topic
     */
    public boolean isPageDisplayed()
    {
        return findElement(pageHeader).getText().equals("Edit Topic");
    }

    public String getTopicTitle()
    {
        waitInSeconds(3);
        return findElement(topicTitle).getAttribute("value");
    }
}
