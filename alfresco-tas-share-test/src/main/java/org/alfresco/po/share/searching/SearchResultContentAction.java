package org.alfresco.po.share.searching;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SearchResultContentAction extends BasePage
{
    private SearchPage searchPage;
    private ContentModel content;

    private final By actionsButton = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']");
    private final By actions = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']");

    public SearchResultContentAction(ThreadLocal<WebDriver> webDriver, ContentModel content, SearchPage searchPage)
    {
        super(webDriver);
        this.content = content;
        this.searchPage = searchPage;

        log.info("Using content {}", content.getName());
    }

    private WebElement getContentRow()
    {
        return searchPage.getContentRowResult(content);
    }

    public SearchResultContentAction assertIsDisplayed()
    {
        log.info("Assert content {} is found", content.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getContentRow()), String.format("Content %s was found", content.getName()));
        return this;
    }

    public SearchResultContentAction clickActions()
    {
        log.info("Click Actions");
        WebElement contentElement = getContentRow();
        webElementInteraction.mouseOver(contentElement);
        webElementInteraction.clickElement(contentElement.findElement(actionsButton));
        return this;
    }

    public SearchCopyMoveDialog clickCopyTo()
    {
        log.info("Click Copy To...");
        webElementInteraction.findFirstElementWithValue(actions, searchPage.language.translate("documentLibrary.contentActions.copyTo")).click();
        return new SearchCopyMoveDialog(webDriver);
    }
}
