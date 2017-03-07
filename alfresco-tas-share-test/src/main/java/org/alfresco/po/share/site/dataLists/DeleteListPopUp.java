package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class DeleteListPopUp extends ShareDialog
{

    @Autowired
    DataListsPage dataListsPage;
    
    @RenderWebElement
    @FindBy(css = "span[class='button-group'] span[class*='primary-button'] button")
    protected WebElement deleteButton;
    
    
    @FindBy(css = "span[class='button-group'] span[class*='default'] span button")
    protected WebElement cancelButton;
    
    public DataListsPage clickDeleteButton()
    {
        deleteButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }
    
    public DataListsPage clickCancelButton()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(cancelButton, 20);
        cancelButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }
}
