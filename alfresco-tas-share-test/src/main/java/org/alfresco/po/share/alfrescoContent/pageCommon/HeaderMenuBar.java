package org.alfresco.po.share.alfrescoContent.pageCommon;
import org.alfresco.common.Language;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageObject

public class HeaderMenuBar extends SiteCommon<HeaderMenuBar>

{
    @Autowired
    protected Language language;

    @FindBy(css = "button[id*='fileSelect']")
    private WebElement selectMenu;

    @FindBy(css = "div[id*='fileSelect-menu'] span")
    private List<WebElement> selectOptionsList;

    @FindBy(css = "button[id*='selectedItems']")
    private WebElement selectedItemsMenu;

    @FindBy(css = ".selected-items button[disabled]")
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
        return selectMenu.isDisplayed();
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
        for (int i = 0; i < selectOptionsList.size(); i++)
        {
            WebElement option = selectOptionsList.get(i);
            if (option.getText().equals(optionText))
                option.click();
        }
        browser.waitInSeconds(1);
    }

    /**
     * Check if "Selected Items..." menu is enabled
     *
     * @return true if menu is enabled
     */
    public boolean isSelectedItemsMenuDisabled()
    {
        return !browser.isElementDisplayed(selectedItemsMenuDisabled);
    }

    public boolean isSelectItemsMenuDisplayedDisabled()
    {
        return selectedItemsMenuDisabled.isDisplayed();
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
        if (optionText.equals(language.translate("documentLibrary.contentActions.startWorkflow")))
            browser.findElement(By.cssSelector(".onActionAssignWorkflow")).click();
        else
        {
            List<WebElement> selectedItemsOptionsList = browser.findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
            for (int i = 0; i < selectedItemsOptionsList.size(); i++)
            {
                WebElement option = selectedItemsOptionsList.get(i);
                if (option.getText().equals(optionText))
                    option.click();
            }
        }
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
        for (int i = 0; i < expectedList.size(); i++)
        {
            int k = 0;
            String expectedOption = expectedList.get(i);
            for (int j = 0; j < selectedItemsOptionsList.size(); j++)
            {
                String optionText = selectedItemsOptionsList.get(j).getText();
                if (expectedOption.equals(optionText))
                    k++;
            }
            if (k != 1)
                return "'" + expectedOption + "' not displayed";
        }
        return expectedList.toString();
    }
}