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
        return webElementInteraction.isElementDisplayed(selectMenu);
    }

    public void clickSelectMenu()
    {
        webElementInteraction.clickElement(selectMenu);
    }

    public void clickSelectOption(String optionText)
    {
        webElementInteraction.selectOptionFromFilterOptionsList(optionText, selectOptionsList);
    }

    public boolean isSelectedItemsMenuEnabled()
    {
        return !webElementInteraction.isElementDisplayed(selectedItemsMenuDisabled);
    }

    public boolean isSelectItemsMenuDisplayedDisabled()
    {
        return webElementInteraction.isElementDisplayed(selectedItemsMenuDisabled);
    }

    public void clickSelectedItemsMenu()
    {
        selectedItemsMenu.click();
    }

    public void clickSelectedItemsOption(String optionText)
    {
        List<WebElement> selectedItemsOptionsList = webElementInteraction.findDisplayedElementsFromLocator(selectedItemsOptionsSelector);
        webElementInteraction.selectOptionFromFilterOptionsList(optionText, selectedItemsOptionsList);
    }
}