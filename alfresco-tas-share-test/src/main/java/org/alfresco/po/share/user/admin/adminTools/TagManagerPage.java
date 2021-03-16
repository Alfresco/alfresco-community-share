package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.*;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class TagManagerPage extends SharePage2<TagManagerPage>
{
    private final By editIconSelector = By.cssSelector("a[class$='edit-tag-active']");
    private final By deleteIconSelector = By.cssSelector("a[class$='delete-tag-active']");
    private final By tableTitle = By.cssSelector(".tags-List>.title");
    private final By tableHead = By.cssSelector(".dashlet thead");
    private final By searchInput = By.cssSelector("input[id$='default-search-text']");

    private final By searchButton = By.cssSelector(".search-button button");
    private final By noTagFoundMessage = By.cssSelector("div[class='tags-list-info']");

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
        log.info("Click edit for tag: {}", tag);
        WebElement tagRowElement = getTagRow(tag);
        mouseOver(tagRowElement,3000);
        WebElement editButton = waitUntilChildElementIsPresent(tagRowElement, editIconSelector);
        clickElement(editButton);

        return new EditTagDialog(webDriver);
    }

    public DeleteDialog clickDelete(String tag)
    {
        log.info("Click delete for tag: {}", tag);
        WebElement tagRowElement = getTagRow(tag);
        mouseOver(findElement(searchInput));
        mouseOver(tagRowElement);
        WebElement deleteButton = tagRowElement.findElement(deleteIconSelector);
        clickElement(deleteButton);

        return new DeleteDialog(webDriver);
    }

    private WebElement getTagRow(String tagName)
    {
        return waitUntilElementIsVisible(
            By.xpath(String.format(tagRow, tagName.toLowerCase())));
    }

    private boolean isTagDisplayed(String tagName)
    {
        return isElementDisplayed(By.xpath(String.format(tagRow, tagName.toLowerCase())));
    }

    public TagManagerPage assertSearchButtonIsDisplayed()
    {
        log.info("Assert Search button is displayed");
        waitUntilElementIsVisible(searchButton);
        assertTrue(isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public TagManagerPage assertSearchInputFieldDisplayed()
    {
        log.info("Assert Search input is displayed");
        assertTrue(isElementDisplayed(searchInput), "Search input is displayed");
        return this;
    }

    public TagManagerPage assertTableTitleIsCorrect()
    {
        log.info("Assert tags table title is: {}", language.translate("tagManager.tableTitle"));
        assertEquals(getElementText(tableTitle), language.translate("tagManager.tableTitle"), "Table title");
        return this;
    }

    public TagManagerPage assertTableHeadersEqual(String expectedTableHeaders)
    {
        log.info("Assert tag table headers equal: {}", expectedTableHeaders);
        assertEquals(getElementText(tableHead), expectedTableHeaders,
            String.format("Table headers not equal %s ", expectedTableHeaders));
        return this;
    }

    /**
     * Method to get document row with retry
     *
     * @param tagName tag name
     * @return instance of TagManager page object
     *
     * @implNote The default value of solr indexing resources in database is 10 seconds. In order to
     * avoid reaching maximum number of retries and have failing tests, we decided to increase that
     * wait time within while loop to 5 seconds.
     * <p>
     * https://github.com/Alfresco/SearchServices/blob/master/search-services/alfresco-search/src/main/resources/solr/instance/templates/rerank/conf/solrcore.properties#L58
     */
    public TagManagerPage searchTagWithRetry(String tagName)
    {
        typeInSearch(tagName);
        clickSearch();

        int retryCounter = 0;
        while(!isTagDisplayed(tagName) && retryCounter < RETRY_TIME_80.getValue())
        {
            log.warn("Tag {} not displayed - retry: {}", tagName, retryCounter);
            clickSearch();
            waitToLoopTime(WAIT_5.getValue());
            retryCounter++;
        }
        return this;
    }

    public TagManagerPage clickSearch()
    {
        WebElement search = findElement(searchButton);
        mouseOver(search);
        try
        {
            clickElement(search);
        }
        catch (ElementClickInterceptedException e)
        {
            log.error("Failed to click Search button. Retry");
            waitUntilElementIsVisible(search);
            clickJS(search);
        }
        return this;
    }

    public TagManagerPage typeInSearch(String tagName)
    {
        log.info("Search for tag {}", tagName);
        WebElement input = waitUntilElementIsVisible(searchInput);
        clearAndType(input, tagName);

        return this;
    }

    public TagManagerPage assertTagIsDisplayed(String tag)
    {
        log.info("Assert tag {} is displayed", tag);
        assertTrue(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }

    public TagManagerPage assertTagIsNotDisplayed(String tag)
    {
        log.info("Assert tag {} is not displayed", tag);
        assertFalse(isTagDisplayed(tag), String.format("Tag %s was found", tag));
        return this;
    }

    public TagManagerPage assertNoTagFoundMessageIsDisplayed()
    {
        log.info("Assert No tag found message is displayed");
        waitUntilElementIsVisible(noTagFoundMessage);
        assertTrue(isElementDisplayed(noTagFoundMessage), "No tag found message is not displayed");
        return this;
    }
}