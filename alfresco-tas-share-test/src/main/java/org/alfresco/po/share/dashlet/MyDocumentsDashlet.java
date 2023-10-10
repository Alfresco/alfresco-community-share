package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_5;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.DocumentsFilter;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyDocumentsDashlet extends Dashlet<MyDocumentsDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.my-documents");
    private final By filterOptions = By.cssSelector("div[class*='my-documents'] div.bd ul li");
    private final By filterButton = By.cssSelector("div[class*='my-documents'] button[id$='default-filters-button']");
    private final By detailedViewButtonSpan = By.cssSelector("span[class*='detailed-view']");
    private final By detailedViewButton = By.cssSelector("span[class*='detailed-view'] button");
    private final By documentNameLink = By.cssSelector("h3.filename > a");

    protected final String documentRow = "//div[starts-with(@class,'dashlet my-documents')]//a[text()='%s']/../../../..";
    private final String buttonChecked = "yui-radio-button-checked";

    public MyDocumentsDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public WebElement getDocumentRow(String documentName)
    {
        By docLocator = getDocLocator(documentName);

        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(docLocator))
        {
            log.warn("Document {} not displayed - retry: {}", documentName, retryCount);
            refresh();
            waitInSeconds(WAIT_5.getValue());
            waitUntilElementIsVisible(dashletContainer);
            retryCount++;
        }
        return waitUntilElementIsVisible(docLocator);
    }

    private By getDocLocator(String documentName)
    {
        return By.xpath(String.format(documentRow, documentName));
    }

    private String getFilterValue(DocumentsFilter filter)
    {
        String filterValue = "";
        switch (filter)
        {
            case RECENTLY_MODIFIED:
                filterValue = language.translate("dashlet.filter.recentlyModified");
                break;
            case EDITING:
                filterValue = language.translate("dashlet.filter.editing");
                break;
            case MY_FAVORITES:
                filterValue = language.translate("dashlet.filter.favorites");
                break;
            default:
                break;
        }
        return filterValue;
    }

    public MyDocumentsDashlet assertSelectedFilterIs(DocumentsFilter filter)
    {
        String filterText = getElementText(filterButton);
        assertEquals(filterText.substring(0, filterText.length() - 2),
            getFilterValue(filter), "Selected filter is correct");
        return this;
    }

    public MyDocumentsDashlet filter(DocumentsFilter filter)
    {
        clickElement(filterButton);
        List<WebElement> options = waitUntilElementsAreVisible(filterOptions);
        selectOptionFromFilterOptionsList(getFilterValue(filter), options);
        waitInSeconds(WAIT_2.getValue());
        return this;
    }

    public MyDocumentsDashlet selectDetailedView()
    {
        log.info("Select Detailed View");
        clickElement(detailedViewButton);
        waitUntilElementHasAttribute(detailedViewButtonSpan, "class", buttonChecked);

        return this;
    }

    public boolean isNumberOfDocumentsDisplayed(int noOfDocs)
    {
        waitUntilElementIsVisible(dashletContainer);
        boolean isDashletContainerVisible = waitUntilElementIsVisible(dashletContainer)
            .findElements(documentNameLink).size() == noOfDocs;

        int retryCount = 0;
        while (!isDashletContainerVisible && retryCount < 5)
        {
            refresh();
            isDashletContainerVisible = waitUntilElementIsVisible(dashletContainer)
                .findElements(documentNameLink).size() == noOfDocs;
            retryCount++;
        }
        return isDashletContainerVisible;
    }

    public MyDocumentsDashlet assertNrOfDisplayedDocumentsIs(int nrOfDocs)
    {
        assertTrue(isNumberOfDocumentsDisplayed(nrOfDocs), "Nr of displayed documents is correct");
        return this;
    }

    public MyDocumentDashletComponent usingDocument(FileModel file)
    {
        return new MyDocumentDashletComponent(webDriver, file);
    }
}