package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SelectDocumentPopupPage extends SelectPopUpPage
{
    private By selectDocumentPopupHeader = By.cssSelector("div[id$='packageItems-cntrl-picker-head']");
    @RenderWebElement
    private final By folderUpButton = By.cssSelector("[id*='cntrl-picker-folderUp-button']");
    private final By breadcrumbButton = By.cssSelector("button[id$='cntrl-picker-navigator-button']");
    private final By selectedDocumentsList = By.cssSelector("[id$=cntrl-picker-selectedItems] [class*=dt-data] tr");
    private final By breadcrumbMenu = By.cssSelector("[id*='packageItems-cntrl-picker-navigatorMenu'] ul");
    private final By breadcrumbOptions = By.cssSelector("[id*='packageItems-cntrl-picker-navigatorMenu'] li");
    private final By resultsList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] [class$='dt-data'] tr");
    private final By itemName = By.cssSelector(".yui-dialog[style*='visibility: visible'] [class*='item-name']");
    private String searchedItem = "//div[contains(@id, 'packageItems-cntrl-picker-results')]//tbody[contains(@class, 'dt-data')]//h3[text()='%s']";
    private String selectedItem = "//div[contains(@id, 'packageItems-cntrl-picker-results')]//tbody[contains(@class, 'dt-data')]//h3[text()='%s']";


    public SelectDocumentPopupPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public List<String> getSelectedDocumentTitlesList()
    {
        List<String> selectedDocssTitleList = new ArrayList<>();
        for (WebElement docTitle : findElements(selectedDocumentsList))
        {
            selectedDocssTitleList.add(docTitle.getText().trim());
        }
        return selectedDocssTitleList;
    }

    public boolean isSelectDocumentPopupPageHeaderDisplayed()
    {
        return isElementDisplayed(selectDocumentPopupHeader);
    }

    public boolean isSearchedItemDisplayed(String documentName)
    {
        waitInSeconds(2);
        return isElementDisplayed(By.xpath(String.format(searchedItem, documentName)));
    }

    public boolean isSelectedItemDisplayed(String documentName)
    {
        return isElementDisplayed(By.xpath(String.format(selectedItem, documentName)));
    }

    public WebElement selectDetailsRowResultList(String item)
    {
        return findFirstElementWithValue(resultsList, item);
    }

    public boolean isItemClickable(String item)
    {
        waitInSeconds(2);
        clickElement(selectDetailsRowResultList(item).findElement(itemName));
        return !isSelectDocumentPopupPageHeaderDisplayed();
    }

    public void selectBreadcrumbPath(String destinationPath)
    {
        clickElement(breadcrumbButton);
        waitUntilElementIsVisible(breadcrumbMenu);
        selectOptionFromFilterOptionsList(destinationPath, findElements(breadcrumbOptions));
        waitUntilElementContainsText(breadcrumbButton, destinationPath);
    }
}
