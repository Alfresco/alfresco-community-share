package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.Theme;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class CustomizeSitePage extends SiteCommon<CustomizeSiteDashboardPage>
{
    @Autowired
    RenameSitePageDialog renameSiteDialog;

    @RenderWebElement
    @FindBy (css = "select[id$='default-theme-menu']")
    private WebElement siteThemeSelect;

    @RenderWebElement
    @FindBy (css = "ul[id$='default-availablePages-ul']")
    private WebElement availableSitePagesArea;

    @RenderWebElement
    @FindBy (css = "ul[id$='default-currentPages-ul']")
    private WebElement currentSitePagesArea;

    @FindBy (css = "button[id$='save-button-button']")
    private Button okButton;

    @FindBy (css = "button[id$='cancel-button-button']")
    private Button cancelButton;
    private String renameAction = ".actions > a[name='.onRenameClick']";
    private String removeAction = ".actions > a[name='.onRemoveClick']";

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/customise-site", getCurrentSiteName());
    }

    /**
     * Verify if Site Theme drop-down is displayed
     *
     * @return true if displayed
     */
    public boolean isSiteThemeDisplayed()
    {
        return browser.isElementDisplayed(siteThemeSelect);
    }

    public void selectTheme(Theme theme)
    {
        Select themeType = new Select(siteThemeSelect);
        if (theme.equals(Theme.APPLICATION_SET))
        {
            themeType.selectByVisibleText("Application Set Theme");
        } else
        {
            themeType.selectByValue(theme.selectValue);
        }
    }

    public List<String> getThemeOptions()
    {
        List<String> themeValues = new ArrayList<>();
        Select themes = new Select(siteThemeSelect);
        for (WebElement theme : themes.getOptions())
        {
            themeValues.add(theme.getText());
        }
        return themeValues;
    }

    private List<SitePageType> getPages(WebElement webElement)
    {
        List<SitePageType> currentPageTypes = new ArrayList<>();
        SitePageType[] pageTypes = SitePageType.values();
        for (SitePageType pageType : pageTypes)
        {
            if (webElement.getText().contains(pageType.getDisplayText()))
            {
                currentPageTypes.add(pageType);
            }
        }
        return currentPageTypes;
    }

    /**
     * Returns All Current {@link SitePageType}.
     *
     * @return List<SitePageType>
     */
    public List<SitePageType> getCurrentPages()
    {
        return getPages(currentSitePagesArea);
    }

    /**
     * Returns All Available {@link SitePageType}.
     *
     * @return List<SitePageType>
     */
    public List<SitePageType> getAvailablePages()
    {
        return getPages(availableSitePagesArea);
    }

    public void clickOk()
    {
        okButton.click();
    }

    public void clickCancel()
    {
        getBrowser().waitUntilElementClickable(cancelButton).click();
    }

    /**
     * Verify if rename action is displayed for a current page
     *
     * @param SitePageType page
     * @return true if displayed
     */
    public boolean isRenameDisplayed(SitePageType page)
    {
        return browser.isElementDisplayed(By.cssSelector(page.getCustomizeCssLocator() + " " + renameAction));
    }

    /**
     * Verify if remove action is displayed for a current page
     *
     * @param SitePageType page
     * @return true if displayed
     */
    public boolean isRemoveDisplayed(SitePageType page)
    {
        return browser.isElementDisplayed(By.cssSelector(page.getCustomizeCssLocator() + " " + removeAction));
    }

    /**
     * Drag and drop available page to current site pages
     *
     * @param page SitePageType page to add
     */
    public void addPageToSite(SitePageType page)
    {
        WebElement pageElem = browser.findElement(By.cssSelector(page.getCustomizeCssLocator()));
        getBrowser().waitUntilElementClickable(pageElem).click();
        getBrowser().waitUntilElementVisible(pageElem);
        browser.scrollToElement(currentSitePagesArea);
        browser.dragAndDrop(pageElem, currentSitePagesArea);
    }

    /**
     * Verify if a page is addded in Current Site Pages
     *
     * @param page
     * @return
     */
    public boolean isPageAddedToCurrentPages(SitePageType page)
    {
        return browser.isElementDisplayed(By.cssSelector("ul[id$='default-currentPages-ul'] " + page.getCustomizeCssLocator()));
    }

    /**
     * Remove page added in Current Site Pages
     *
     * @param page SitePageType page to remove
     */
    public void removePage(SitePageType page)
    {
        WebElement pageElem = browser.findElement(By.cssSelector(page.getCustomizeCssLocator()));
        pageElem.findElement(By.cssSelector(removeAction)).click();
    }

    /**
     * Rename page from added in Current Site Pages
     *
     * @param page    SitePageType page to edit
     * @param newName String new name
     */
    public void renamePage(SitePageType page, String newName)
    {
        WebElement pageElem = browser.findElement(By.cssSelector(page.getCustomizeCssLocator()));
        pageElem.findElement(By.cssSelector(renameAction)).click();
        renameSiteDialog.renderedPage();
        renameSiteDialog.typeDisplayName(newName);
        renameSiteDialog.clickOk();
    }

    /**
     * Get the display name for a page
     *
     * @param page SitePageType the page
     * @return String display name
     */
    public String getPageDisplayName(SitePageType page)
    {
        WebElement pageElem = browser.findElement(By.cssSelector(page.getCustomizeCssLocator()));
        return pageElem.findElement(By.cssSelector(".title")).getText();
    }
}
