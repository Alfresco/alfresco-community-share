package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SelectDocumentPopupPage extends SelectPopUpPage
{
    //@Autowired
    StartWorkflowPage startWorkflowPage;

    @FindBy (css = "div[id$='packageItems-cntrl-picker-head']")
    private WebElement selectDocumentPopupHeader;

    @RenderWebElement
    @FindBy (css = "button[id$='-ok-button']")
    private WebElement okButton;

    @FindBy (css = "button[id$='packageItems-cntrl-cancel-button']")
    private WebElement cancelButton;

    @FindBy (css = "[id*=default-content-docPicker-cntrl-picker-head]")
    private WebElement header;

    @RenderWebElement
    @FindBy (css = ".container-close")
    private WebElement containerClose;

    @FindBy (css = "[id*='cntrl-picker-folderUp-button']")
    private WebElement folderUpButton;

    @FindBy (css = "button[id$='cntrl-picker-navigator-button']")
    private WebElement breadcrumbButton;

    @FindAll (@FindBy (css = "[id$=cntrl-picker-selectedItems] [class*=dt-data] tr"))
    private List<WebElement> selectedDocumentsList;

    @FindBy (css = "[id*='packageItems-cntrl-picker-navigatorMenu'] ul")
    private WebElement breadcrumbMenu;
    ;

    @FindAll (@FindBy (css = "[id*='packageItems-cntrl-picker-navigatorMenu'] li"))
    private List<WebElement> breadcrumbOptions;

    private String searchedItem = "//div[contains(@id, 'packageItems-cntrl-picker-results')]//tbody[contains(@class, 'dt-data')]//h3[text()='%s']";
    private String selectedItem = "//div[contains(@id, 'packageItems-cntrl-picker-results')]//tbody[contains(@class, 'dt-data')]//h3[text()='%s']";
    private By itemName = By.cssSelector(".yui-dialog[style*='visibility: visible'] [class*='item-name']");


    public List<String> getSelectedDocumentTitlesList()
    {
        List<String> selectedDocssTitleList = new ArrayList<>();
        for (WebElement docTitle : selectedDocumentsList)
        {
            selectedDocssTitleList.add(docTitle.getText().trim());
        }
        return selectedDocssTitleList;
    }

    public boolean isSelectDocumentPopupPageHeaderDisplayed()
    {
        return selectDocumentPopupHeader.isDisplayed();
    }

    public boolean isSearchedItemDisplayed(String documentName)
    {
        return browser.findElement(By.xpath(String.format(searchedItem, documentName))).isDisplayed();
    }

    public boolean isSelectedItemDisplayed(String documentName)
    {
        return browser.findElement(By.xpath(String.format(selectedItem, documentName))).isDisplayed();
    }

    public WebElement selectDetailsRowResultList(String item)
    {
        return browser.findFirstElementWithValue(resultsList, item);
    }

    public boolean isItemClickable(String item)
    {
        selectDetailsRowResultList(item).findElement(itemName).click();
        return !isSelectDocumentPopupPageHeaderDisplayed();
    }

    public void selectBreadcrumbPath(String destinationPath)
    {
        breadcrumbButton.click();
        browser.waitUntilElementVisible(breadcrumbMenu);
        browser.selectOptionFromFilterOptionsList(destinationPath, breadcrumbOptions);
        browser.waitUntilElementContainsText(breadcrumbButton, destinationPath);
    }

}
