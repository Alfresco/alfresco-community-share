package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SelectWikiPagePopUp extends DashletPopUp<SelectWikiPagePopUp>
{
    @FindBy (css = "div[style*='cursor: move']")
    private WebElement selectWikiPage;

    @FindBy (css = "div[class$='yui-u']")
    private WebElement selectWikiPageText;

    @FindBy (css = "select[name='wikipage']")
    private WebElement selectAPageDropDown;

    public SelectWikiPagePopUp assertDialogBodyMessageEquals(String expectedDialogBodyMessage)
    {
        LOG.info("Assert dialog body message equals: {}", expectedDialogBodyMessage);
        assertEquals(selectWikiPageText.getText(), expectedDialogBodyMessage,
            String.format("Dialog body message not equals %s ", expectedDialogBodyMessage));

        return this;
    }

    /**
     * Method to check if the Select a Page drop-down is displayed on the Select Wiki Page form.
     *
     * @return true if select a page drop down is found on the Select a Wiki pafe form
     */
    public boolean isSelectAPageDropDownDisplayed()
    {
        return selectAPageDropDown.isDisplayed();
    }

    /**
     * Method to select the Wiki Page situated at the given position from the Select a Page drop down.
     */
    public void selectWikiPageFromList(int position)
    {
        selectAPageDropDown.findElement(By.xpath("option[" + position + "]")).click();
    }

    public SelectWikiPagePopUp assertDropdownOptionEquals(String expectedDropdownOption)
    {
        LOG.info("Assert dropdown option equals: {}", expectedDropdownOption);
        assertEquals(selectAPageDropDown.getText(), expectedDropdownOption,
            String.format("Drop down option not equals %s ", expectedDropdownOption));

        return this;
    }

    public SelectWikiPagePopUp clickDialogDropdown()
    {
        LOG.info("Click dialog dropdown");
        selectAPageDropDown.click();
        return this;
    }
}
