package org.alfresco.po.share.searching;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SearchContentActionComponent extends SearchPage
{
    private final By actionsButton = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']");
    private final By actions = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']");
    private final ContentModel content;

    public SearchContentActionComponent(ThreadLocal<WebDriver> webDriver, ContentModel content)
    {
        super(webDriver);
        this.content = content;
    }

    private WebElement getContentRow()
    {
        return getContentRowResult(content);
    }

    public SearchContentActionComponent assertIsDisplayed()
    {
        log.info("Assert content {} is found", content.getName());
        assertTrue(isElementDisplayed(getContentRow()), String.format("Content %s was found", content.getName()));
        return this;
    }

    public SearchContentActionComponent clickActions()
    {
        log.info("Click Actions");
        WebElement contentElement = getContentRow();
        mouseOver(contentElement);
        clickElement(contentElement.findElement(actionsButton));
        return this;
    }

    public SearchCopyMoveDialog clickCopyTo()
    {
        log.info("Click Copy To...");
        clickElement(findFirstElementWithValue(actions,
            language.translate("documentLibrary.contentActions.copyTo")));
        return new SearchCopyMoveDialog(webDriver);
    }
}
