package org.alfresco.po.share.dashlet;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SelectWikiPagePopUp extends DashletPopUp
{
    @FindBy (css = "div[style*='cursor: move']")
    private WebElement selectWikiPage;

    @FindBy (css = "div[class$='yui-u']")
    private WebElement selectWikiPageText;

    @FindBy (css = "select[name='wikipage']")
    private WebElement selectAPageDropDown;

    /**
     * Method to get the Configure dashlet (Edit) PopUp text on Wiki dashlet
     *
     * @return the Configure Wiki Dashlet form text displayed.
     */

    public String getEditPopupText()
    {
        return selectWikiPageText.getText();
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

    /**
     * Method to get the name of the Wiki Page from the Select a Page drop down list.
     *
     * @return the Wiki Page name from the Select a Page drop down
     */

    public String getWikiPageName()
    {
        return selectAPageDropDown.getText();

    }

    /**
     * Method to click on the droop down list on Select Wiki Page popup.
     */
    public void clickDropDownListOnSelectWikiPage()
    {
        selectAPageDropDown.click();
    }

    /**
     * Method to check that the Select Wiki Page Form is displayed (Configure Dashlet(Edit))
     *
     * @return Select Wiki Page form title
     */

    public String getEditWikiPageFormTitle()
    {
        return selectWikiPage.getText();
    }

}
