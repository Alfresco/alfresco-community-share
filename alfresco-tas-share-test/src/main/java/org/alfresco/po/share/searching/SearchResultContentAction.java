package org.alfresco.po.share.searching;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResultContentAction extends BasePage
{
    private final Logger LOG = LoggerFactory.getLogger(SearchResultContentAction.class);

    private SearchPage searchPage;
    private ContentModel content;
    private SearchCopyMoveDialog copyMoveDialog;

    private final By actionsButton = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']");
    private final By actions = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']");

    public SearchResultContentAction(ThreadLocal<WebDriver> webDriver, ContentModel content, SearchPage searchPage, SearchCopyMoveDialog copyMoveDialog)
    {
        super(webDriver);
        this.content = content;
        this.searchPage = searchPage;
        this.copyMoveDialog = copyMoveDialog;

        LOG.info("Using content {}", content.getName());
    }

    private WebElement getContentRow()
    {
        return searchPage.getContentRowResult(content);
    }

    public SearchResultContentAction assertIsDisplayed()
    {
        LOG.info("Assert content {} is found", content.getName());
        assertTrue(webElementInteraction.isElementDisplayed(getContentRow()), String.format("Content %s was found", content.getName()));
        return this;
    }

    public SearchResultContentAction clickActions()
    {
        LOG.info("Click Actions");
        WebElement contentElement = getContentRow();
        webElementInteraction.mouseOver(contentElement);
        webElementInteraction.mouseOver(contentElement.findElement(actionsButton));
        webElementInteraction.clickElement(contentElement);
        return this;
    }

    public SearchCopyMoveDialog clickCopyTo()
    {
        LOG.info("Click Copy To...");
        webElementInteraction.findFirstElementWithValue(actions, searchPage.language.translate("documentLibrary.contentActions.copyTo")).click();
        return new SearchCopyMoveDialog(webDriver);
    }
}
