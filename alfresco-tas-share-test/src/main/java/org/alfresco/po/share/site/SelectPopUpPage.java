package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class SelectPopUpPage extends ShareDialog
{

    @FindBy(css = "[id$='cntrl-ok-button']")   
    private WebElement okButton;

    @FindBy(css = "[id$='issueAssignedTo-cntrl-cancel-button']")
    private WebElement cancelButton;

    @FindAll(@FindBy(css = "div[id$='cntrl-picker-results'] [class$='dt-data'] tr"))
    protected List<WebElement> list;

    private By addIcon = By.cssSelector("[class*=addIcon]");
    private By removeIcon = By.cssSelector("[class*='removeIcon']");

    /**
     * This method returns the list of documents title
     * 
     * @return
     */

    public List<String> getTitlesList()
    {
        List<String> titleList = new ArrayList<String>();
        for (WebElement title : list)
        {
            titleList.add(title.getText().trim());
        }
        return titleList;
    }

    public WebElement selectDetailsRow(String item)
    {
        return browser.findFirstElementWithValue(list, item);
    }

    public void clickItem(String item)
    {
        //browser.waitUntilElementsVisible(addIcon);
        browser.findFirstElementWithValue(list, item).findElement(By.cssSelector("h3[class$='item-name'] a")).click();
    }

    public void clickAddIcon(String item)
    {
        browser.waitInSeconds(1);
        browser.waitUntilElementVisible(addIcon);
        selectDetailsRow(item).findElement(addIcon).click();
    }

    public void clickRemoveIcon(String item)
    {
        selectDetailsRow(item).findElement(removeIcon).click();
    }

    public void clickOkButton()
    {
        okButton.click();
    }
}
