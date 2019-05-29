package org.alfresco.po.share.alfrescoContent.pageCommon;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class HeaderMenuBar extends SiteCommon<HeaderMenuBar>
{
    @FindBy (css = "button[id*='fileSelect']")
    private WebElement selectMenu;

    @FindBy (css = "div[id*='fileSelect-menu'] span")
    private List<WebElement> selectOptionsList;

    @FindBy (css = "button[id*='selectedItems']")
    private WebElement selectedItemsMenu;

    @FindBy (css = ".selected-items button[disabled]")
    private WebElement selectedItemsMenuDisabled;

    private By selectedItemsOptionsSelector = By.cssSelector("div[id*='selectedItems'] span");

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSelectButtonDisplayed()
    {
        return browser.isElementDisplayed(selectMenu);
    }

    public void clickSelectMenu()
    {
        browser.waitUntilElementClickable(selectMenu, 40).click();
    }

    /**
     * Click on an option from "Select" menu
     *
     * @param optionText to be clicked on
     */
    public void clickSelectOption(String optionText)
    {
        browser.selectOptionFromFilterOptionsList(optionText, selectOptionsList);
        browser.waitInSeconds(1);
    }

    /**
     * Check if "Selected Items..." menu is enabled
     *
     * @return true if menu is enabled
     */
    public boolean isSelectedItemsMenuEnabled()
    {
        return !browser.isElementDisplayed(selectedItemsMenuDisabled);
    }

    public boolean isSelectItemsMenuDisplayedDisabled()
    {
        return browser.isElementDisplayed(selectedItemsMenuDisabled);
    }

    public void clickSelectedItemsMenu()
    {
        selectedItemsMenu.click();
    }

    /**
     * Click on option from "Selected Items..." menu
     *
     * @param optionText to be clicked
     */
    public void clickSelectedItemsOption(String optionText)
    {
        browser.waitInSeconds(2);
        List<WebElement> selectedItemsOptionsList = browser.findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
        browser.selectOptionFromFilterOptionsList(optionText, selectedItemsOptionsList);
    }

    /**
     * Check "Selected Items..." menu values
     *
     * @param expectedList list of values to be displayed
     * @return displayed options from "Selected Items"
     */
    public String verifySelectedItemsValues(ArrayList<String> expectedList)
    {
        List<WebElement> selectedItemsOptionsList = browser.findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
        for (String anExpectedList : expectedList)
        {
            int k = 0;
            for (WebElement aSelectedItemsOptionsList : selectedItemsOptionsList)
            {
                String optionText = aSelectedItemsOptionsList.getText();
                if (anExpectedList.equals(optionText))
                    k++;
            }
            if (k != 1)
                return "'" + anExpectedList + "' not displayed";
        }
        return expectedList.toString();
    }
}