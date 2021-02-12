package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.DocumentsFilter;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
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
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    public WebElement getDocumentRow(String documentName)
    {
        By docLocator = By.xpath(String.format(documentRow, documentName));

        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !webElementInteraction.isElementDisplayed(docLocator))
        {
            log.warn("Document {} not displayed - retry: {}", documentName, retryCount);
            webElementInteraction.refresh();
            webElementInteraction.waitInSeconds(WAIT_2.getValue());
            webElementInteraction.waitUntilElementIsVisible(dashletContainer);
            retryCount++;
        }
        return webElementInteraction.waitUntilElementIsVisible(docLocator);
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
        String filterText = webElementInteraction.getElementText(filterButton);
        assertEquals(filterText.substring(0, filterText.length() - 2),
            getFilterValue(filter), "Selected filter is correct");
        return this;
    }

    public MyDocumentsDashlet filter(DocumentsFilter filter)
    {
        webElementInteraction.clickElement(filterButton);
        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(filterOptions);
        webElementInteraction.selectOptionFromFilterOptionsList(getFilterValue(filter), options);
        webElementInteraction.waitInSeconds(WAIT_2.getValue());
        return this;
    }

    public MyDocumentsDashlet selectDetailedView()
    {
        log.info("Select Detailed View");
        webElementInteraction.clickElement(detailedViewButton);
        webElementInteraction.waitUntilElementHasAttribute(detailedViewButtonSpan, "class", buttonChecked);

        return this;
    }

    public boolean isNumberOfDocumentsDisplayed(int noOfDocs)
    {
        webElementInteraction.waitUntilElementIsVisible(dashletContainer);
        boolean isDashletContainerVisible = webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElements(documentNameLink).size() == noOfDocs;
        int retryCount = 0;
        while (!isDashletContainerVisible && retryCount < 5)
        {
            webElementInteraction.refresh();
            isDashletContainerVisible = webElementInteraction.waitUntilElementIsVisible(dashletContainer)
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
        return new MyDocumentDashletComponent(this, new DocumentDetailsPage(webDriver), webElementInteraction, file);
    }
}