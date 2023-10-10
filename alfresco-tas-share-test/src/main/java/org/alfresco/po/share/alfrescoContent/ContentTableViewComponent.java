package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class ContentTableViewComponent extends AlfrescoContentPage
{
    private final By titleLocator = By.cssSelector("td[headers$='th-cmtitle '] span");
    private final By descriptionLocator = By.cssSelector("td[headers$='th-cmdescription '] span");
    private final By creatorLocator = By.cssSelector("td[headers$='th-cmcreator '] span");
    private final By modifierLocator = By.cssSelector("td[headers$='th-cmmodifier '] span");
    private final By createDateLocator = By.cssSelector("td[headers$='th-cmcreated '] span");
    private final By modifiedDateLocator = By.cssSelector("td[headers$='th-modified '] span");

    private final String contentTableRow = "//td[contains(@class, 'yui-dt-col-name')]//a[text()='%s']/../../../..";

    protected ContentTableViewComponent(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement getContentTableRow(String contentName)
    {
        By contentRowElement = By.xpath(String.format(contentTableRow, contentName));

        int retryCount = 0;
        while(retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(contentRowElement))
        {
            log.warn("Content {} not displayed - retry: {}", contentName, retryCount);
            refresh();
            waitToLoopTime(WAIT_2.getValue());
            retryCount++;
        }
        return waitUntilElementIsVisible(contentRowElement);
    }

    public ContentTableViewComponent assertContentIsDisplayed(ContentModel contentModel)
    {
        log.info("Assert content {} is displayed", contentModel.getName());
        assertTrue(isElementDisplayed(getContentTableRow(contentModel.getName())),
            String.format("Content %s is not displayed", contentModel.getName()));
        return this;
    }

    public ContentTableViewComponent assertTitleEqualsTo(ContentModel content, String expectedTitle)
    {
        log.info("Assert title for content {} equals to {}", content.getName(), expectedTitle);
        WebElement contentTitle = getContentTableRow(content.getName()).findElement(titleLocator);
        assertEquals(getElementText(contentTitle), expectedTitle,
            String.format("Expected title for content %s is not as expected", content.getName()));
        return this;
    }

    public ContentTableViewComponent assertDescriptionEqualsTo(ContentModel content, String expectedDescription)
    {
        log.info("Assert description for content {} equals to {}", content.getName(), expectedDescription);
        WebElement description = getContentTableRow(content.getName()).findElement(descriptionLocator);
        assertEquals(getElementText(description), expectedDescription,
            String.format("Expected description for content %s is not as expected", content.getName()));
        return this;
    }

    public ContentTableViewComponent assertCreatorEqualsTo(ContentModel content, String expectedCreator)
    {
        log.info("Assert creator for content {} equals to {}", content.getName(), expectedCreator);
        WebElement creator = getContentTableRow(content.getName()).findElement(creatorLocator);
        assertEquals(getElementText(creator), expectedCreator,
            String.format("Expected creator for content %s is not as expected", content.getName()));
        return this;
    }

    public ContentTableViewComponent assertModifierEqualsTo(ContentModel content, String expectedModifier)
    {
        log.info("Assert modifier for content {} equals to {}", content.getName(), expectedModifier);
        WebElement modifier = getContentTableRow(content.getName()).findElement(modifierLocator);
        assertEquals(getElementText(modifier), expectedModifier,
            String.format("Expected modifier for content %s is not as expected", content.getName()));
        return this;
    }

    public ContentTableViewComponent assertCreatedDateEqualsTo(ContentModel content, String expectedCreateDate)
    {
        log.info("Assert created date for content {} equals to {}", content.getName(), expectedCreateDate);
        WebElement createDate = getContentTableRow(content.getName()).findElement(createDateLocator);
        assertEquals(getElementText(createDate), expectedCreateDate,
            String.format("Expected created date for content %s is not as expected", content.getName()));
        return this;
    }

    public ContentTableViewComponent assertModifiedDateEqualsTo(ContentModel content, String expectedModifiedDate)
    {
        log.info("Assert modified date for content {} equals to {}", content.getName(), expectedModifiedDate);
        WebElement modifiedDate = getContentTableRow(content.getName()).findElement(modifiedDateLocator);
        assertEquals(getElementText(modifiedDate), expectedModifiedDate,
            String.format("Expected modified date for content %s is not as expected", content.getName()));
        return this;
    }
}
