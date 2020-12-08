package org.alfresco.po.share.site;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class SelectDocumentPopupPage extends SelectPopUpPage
{
    private By selectDocumentPopupHeader = By.cssSelector("div[id$='packageItems-cntrl-picker-head']");
    @RenderWebElement
    private By okButton = By.cssSelector("button[id$='-ok-button']");
    private By cancelButton = By.cssSelector("button[id$='packageItems-cntrl-cancel-button']");
    private By header = By.cssSelector("[id*=default-content-docPicker-cntrl-picker-head]");
    @RenderWebElement
    private By containerClose = By.cssSelector(".container-close");
    private By folderUpButton = By.cssSelector("[id*='cntrl-picker-folderUp-button']");
    private By breadcrumbButton = By.cssSelector("button[id$='cntrl-picker-navigator-button']");
    private By selectedDocumentsList = By.cssSelector("[id$=cntrl-picker-selectedItems] [class*=dt-data] tr");
    private By breadcrumbMenu = By.cssSelector("[id*='packageItems-cntrl-picker-navigatorMenu'] ul");
    private By breadcrumbOptions = By.cssSelector("[id*='packageItems-cntrl-picker-navigatorMenu'] li");
    private By resultsList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-results'] [class$='dt-data'] tr");
    private By itemName = By.cssSelector(".yui-dialog[style*='visibility: visible'] [class*='item-name']");
    private By selectedList = By.cssSelector(".yui-dialog[style*='visibility: visible'] div[id$='cntrl-picker-selectedItems'] [class$='dt-data'] tr");
    private By removeIcon = By.cssSelector("[class*='removeIcon']");
    private String searchedItem = "//div[contains(@id, 'packageItems-cntrl-picker-results')]//tbody[contains(@class, 'dt-data')]//h3[text()='%s']";
    private String selectedItem = "//div[contains(@id, 'packageItems-cntrl-picker-results')]//tbody[contains(@class, 'dt-data')]//h3[text()='%s']";


    public SelectDocumentPopupPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public List<String> getSelectedDocumentTitlesList()
    {
        List<String> selectedDocssTitleList = new ArrayList<>();
        for (WebElement docTitle : getBrowser().findElements(selectedDocumentsList))
        {
            selectedDocssTitleList.add(docTitle.getText().trim());
        }
        return selectedDocssTitleList;
    }

    public boolean isSelectDocumentPopupPageHeaderDisplayed()
    {
        return getBrowser().isElementDisplayed(selectDocumentPopupHeader);
    }

    public boolean isSearchedItemDisplayed(String documentName)
    {
        return getBrowser().findElement(By.xpath(String.format(searchedItem, documentName))).isDisplayed();
    }

    public boolean isSelectedItemDisplayed(String documentName)
    {
        return getBrowser().findElement(By.xpath(String.format(selectedItem, documentName))).isDisplayed();
    }

    public WebElement selectDetailsRowResultList(String item)
    {
        return getBrowser().findFirstElementWithValue(resultsList, item);
    }

    public boolean isItemClickable(String item)
    {
        selectDetailsRowResultList(item).findElement(itemName).click();
        return !isSelectDocumentPopupPageHeaderDisplayed();
    }

    public void selectBreadcrumbPath(String destinationPath)
    {
        getBrowser().findElement(breadcrumbButton).click();
        getBrowser().waitUntilElementVisible(breadcrumbMenu);
        getBrowser().selectOptionFromFilterOptionsList(destinationPath, getBrowser().findElements(breadcrumbOptions));
        getBrowser().waitUntilElementContainsText(breadcrumbButton, destinationPath);
    }
}
