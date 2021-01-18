package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_80;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TagManagerPage extends SharePage2<TagManagerPage>
{
    private final String styleAttribute = "style";
    private final String styleDisplayNone = "display: none;";

    private final By editIconSelector = By.cssSelector("a[class$='edit-tag-active']");
    private final By deleteIconSelector = By.cssSelector("a[class$='delete-tag-active']");
    private final By tableTitle = By.cssSelector(".tags-List>.title");
    private final By tableHead = By.cssSelector(".dashlet thead");
    private final By searchInput = By.cssSelector("input[id$='default-search-text']");

    private final By searchButton = By.cssSelector(".search-button button");
    private final By noTagFoundMessage = By.cssSelector("div[class='tags-list-info']");
    private final By tabBodyMessage = By.cssSelector("div[id$='default-tags'] tbody[class='yui-dt-message']");
    private final String tagRow = "//b[text()='%s']/../../../../..";

    public TagManagerPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/tag-management";
    }

    public EditTagDialog clickEdit(String tag)
    {
        LOG.info("Click edit for tag: {}", tag);
        WebElement tagRowElement = getTagRow(tag);
        webElementInteraction.mouseOver(tagRowElement,3000);
        WebElement editButton = webElementInteraction.waitUntilChildElementIsPresent(tagRowElement, editIconSelector);
        webElementInteraction.clickElement(editButton);

        return new EditTagDialog(webDriver);
    }

    public DeleteDialog clickDelete(String tag)
    {
        LOG.info("Click delete for tag: {}", tag);
        WebElement tagRowElement = getTagRow(tag);
        webElementInteraction.mouseOver(webElementInteraction.findElement(searchInput));
        webElementInteraction.mouseOver(tagRowElement);
        WebElement deleteButton = tagRowElement.findElement(deleteIconSelector);
        webElementInteraction.clickElement(deleteButton);

        return new DeleteDialog(webDriver);
    }

    private WebElement getTagRow(String tagName)
    {
        return webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(tagRow, tagName.toLowerCase())), 2000);
    }

    public boolean isTagDisplayed(String tagName)
    {
        return webElementInteraction.isElementDisplayed(By.xpath(String.format(tagRow, tagName.toLowerCase())));
    }

    public TagManagerPage assertSearchButtonIsDisplayed()
    {
        LOG.info("Assert Search button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public TagManagerPage assertSearchInputFieldDisplayed()
    {
        LOG.info("Assert Search input is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(searchInput), "Search input is displayed");
        return this;
    }

    public TagManagerPage assertTableTitleIsCorrect()
    {
        LOG.info("Assert tags table title is: {}", language.translate("tagManager.tableTitle"));
        assertEquals(webElementInteraction.getElementText(tableTitle), language.translate("tagManager.tableTitle"), "Table title");
        return this;
    }

    public TagManagerPage assertTableHeadersEqual(String expectedTableHeaders)
    {
        LOG.info("Assert tag table headers equal: {}", expectedTableHeaders);
        assertEquals(webElementInteraction.getElementText(tableHead), expectedTableHeaders,
            String.format("Table headers not equal %s ", expectedTableHeaders));
        return this;
    }

    public TagManagerPage searchTagWithRetry(String tagName)
    {
        typeInSearch(tagName);
        clickSearch();
        webElementInteraction.waitInSeconds(WAIT_2.getValue());

        boolean isTagFound = isTagDisplayed(tagName);
        int retryCounter = 0;
        while(!isTagFound && retryCounter < WAIT_80.getValue())
        {
            LOG.error("Wait for tag {} to be displayed - retry: {}", tagName, retryCounter);
            waitToLoopTime(WAIT_2.getValue());
            if(webElementInteraction.isElementDisplayed(noTagFoundMessage))
            {
                clickSearch();
            }
            else
            {
                webElementInteraction.waitUntilElementHasAttribute(tabBodyMessage, styleAttribute, styleDisplayNone);
                break;
            }
            retryCounter++;
        }
        return this;
    }

    public TagManagerPage clickSearch()
    {
        WebElement search = webElementInteraction.findElement(searchButton);
        webElementInteraction.mouseOver(search);
        try
        {
            webElementInteraction.clickElement(search);
        }
        catch (ElementClickInterceptedException e)
        {
            LOG.error("Failed to click Search button. Retry ");
            webElementInteraction.waitUntilElementIsVisible(search);
            webElementInteraction.clickJS(search);
        }
        return this;
    }

    public TagManagerPage typeInSearch(String tagName)
    {
        LOG.info("Search for tag {}", tagName);
        WebElement input = webElementInteraction.waitUntilElementIsVisible(searchInput);
        webElementInteraction.clearAndType(input, tagName);

        return this;
    }

    public TagManagerPage assertTagIsDisplayed(String tag)
    {
        LOG.info("Assert tag {} is displayed", tag);
        assertTrue(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }

    public TagManagerPage assertTagIsNotDisplayed(String tag)
    {
        LOG.info("Assert tag {} is not displayed", tag);
        assertFalse(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }

    public TagManagerPage assertNoTagFoundMessageIsDisplayed()
    {
        LOG.info("Assert No tag found message is displayed");
        webElementInteraction.waitUntilElementIsVisible(noTagFoundMessage);
        assertTrue(webElementInteraction.isElementDisplayed(noTagFoundMessage), "No tag found message is not displayed");
        return this;
    }
}