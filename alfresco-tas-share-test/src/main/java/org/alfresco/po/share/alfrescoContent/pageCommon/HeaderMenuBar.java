package org.alfresco.po.share.alfrescoContent.pageCommon;

import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

    private final By selectedItemsOptionsSelector = By.cssSelector("div[id*='selectedItems'] span");
    private final By select_menu = By.cssSelector("button[id*='fileSelect']");
    private final By select_OptionsList = By.cssSelector("div[id*='fileSelect-menu'] span");
    private final By selected_ItemsMenu = By.cssSelector("div[id*='fileSelect-menu'] span");
    private final By selected_ItemsMenuDisabled = By.cssSelector(".selected-items button[disabled]");


    public HeaderMenuBar(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSelectButtonDisplayed()
    {
        return isElementDisplayed(selectMenu);
    }

    public HeaderMenuBar clickSelectMenu()
    {
       clickElement(select_menu);
       return this;

    }

    public void clickSelectOption(String optionText)
    {
        selectOptionFromFilterOptionsList(optionText, selectOptionsList);
    }

    public boolean isSelectedItemsMenuEnabled()
    {
        return !isElementDisplayed(selected_ItemsMenuDisabled);
    }

    public boolean isSelectItemsMenuDisplayedDisabled()
    {
        return isElementDisplayed(selectedItemsMenuDisabled);
    }

    public void clickSelectedItemsMenu()
    {
        selectedItemsMenu.click();
    }

    public void clickSelectedItemsOption(String optionText)
    {
        List<WebElement> selectedItemsOptionsList = findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
        selectOptionFromFilterOptionsList(optionText, selectedItemsOptionsList);
    }
    public void click_SelectOption(String option_Name)
    {
        List<WebElement> optionList = findElements(select_OptionsList);
        for (WebElement webElement : optionList)
        {
            if (webElement.getText().contains(option_Name))
            {
                webElement.click();
            }
        }

    }
}