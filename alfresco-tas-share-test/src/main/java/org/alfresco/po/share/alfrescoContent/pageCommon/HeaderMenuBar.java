package org.alfresco.po.share.alfrescoContent.pageCommon;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

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

    public HeaderMenuBar(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSelectButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(selectMenu);
    }

    public void clickSelectMenu()
    {
        getBrowser().waitUntilElementClickable(selectMenu, 40).click();
    }

    public void clickSelectOption(String optionText)
    {
        getBrowser().selectOptionFromFilterOptionsList(optionText, selectOptionsList);
    }

    public boolean isSelectedItemsMenuEnabled()
    {
        return !getBrowser().isElementDisplayed(selectedItemsMenuDisabled);
    }

    public boolean isSelectItemsMenuDisplayedDisabled()
    {
        return getBrowser().isElementDisplayed(selectedItemsMenuDisabled);
    }

    public void clickSelectedItemsMenu()
    {
        selectedItemsMenu.click();
    }

    public void clickSelectedItemsOption(String optionText)
    {
        List<WebElement> selectedItemsOptionsList = getBrowser().findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
        getBrowser().selectOptionFromFilterOptionsList(optionText, selectedItemsOptionsList);
    }

    public String verifySelectedItemsValues(ArrayList<String> expectedList)
    {
        List<WebElement> selectedItemsOptionsList = getBrowser().findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
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