package org.alfresco.po.share.searching;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.searching.dialogs.SearchCopyMoveDialog;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResultContentAction
{
    private final Logger LOG = LoggerFactory.getLogger(SearchResultContentAction.class);

    private SearchPage searchPage;
    private ContentModel content;
    private SearchCopyMoveDialog copyMoveDialog;

    private final By actionsButton = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS span[class*='dijitButtonContents']");
    private final By actions = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN tr td[id*='text']");

    public SearchResultContentAction(ContentModel content, SearchPage searchPage, SearchCopyMoveDialog copyMoveDialog)
    {
        this.content = content;
        this.searchPage = searchPage;
        this.copyMoveDialog = copyMoveDialog;

        LOG.info("Using content {}", content.getName());
    }

    public WebBrowser getBrowser()
    {
        return searchPage.getBrowser();
    }

    private WebElement getContentRow()
    {
        return searchPage.getContentRowResult(content);
    }

    public SearchResultContentAction assertIsDisplayed()
    {
        LOG.info("Assert content {} is found", content.getName());
        assertTrue(getBrowser().isElementDisplayed(getContentRow()), String.format("Content %s was found", content.getName()));
        return this;
    }

    public SearchResultContentAction clickActions()
    {
        LOG.info("Click Actions");
        WebElement content = getContentRow();
        getBrowser().mouseOver(content);
        getBrowser().mouseOver(content.findElement(actionsButton));
        content.findElement(actionsButton).click();
        return this;
    }

    public SearchCopyMoveDialog clickCopyTo()
    {
        LOG.info("Click Copy To...");
        getBrowser().findFirstElementWithValue(actions, searchPage.language.translate("documentLibrary.contentActions.copyTo")).click();
        return (SearchCopyMoveDialog) copyMoveDialog.renderedPage();
    }
}
