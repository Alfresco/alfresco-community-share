package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.Theme;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@Slf4j
public class CustomizeSitePage extends SiteCommon<CustomizeSiteDashboardPage>
{
    private final By siteThemeSelect = By.cssSelector("select[id$='default-theme-menu']");
    private final By availableSitePagesArea = By.cssSelector("ul[id$='default-availablePages-ul']");
    private final By currentSitePagesArea = By.cssSelector("ul[id$='default-currentPages-ul']");
    private final By okButton = By.cssSelector("button[id$='save-button-button']");
    private final By cancelButton = By.cssSelector("button[id$='cancel-button-button']");

    private String renameAction = ".actions > a[name='.onRenameClick']";
    private String removeAction = ".actions > a[name='.onRemoveClick']";

    public CustomizeSitePage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

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
        return isElementDisplayed(siteThemeSelect);
    }

    public void selectTheme(Theme theme)
    {
        Select themeType = new Select(waitUntilElementIsVisible(siteThemeSelect));
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
        Select themes = new Select(waitUntilElementIsVisible(siteThemeSelect));
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
        return getPages(findElement(currentSitePagesArea));
    }

    /**
     * Returns All Available {@link SitePageType}.
     *
     * @return List<SitePageType>
     */
    public List<SitePageType> getAvailablePages()
    {
        return getPages(findElement(availableSitePagesArea));
    }

    public void saveChanges()
    {
        clickElement(okButton);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancel()
    {
        clickElement(cancelButton);
    }

    public boolean isRenameDisplayed(SitePageType page)
    {
        return isElementDisplayed(By.cssSelector(page.getCustomizeCssLocator() + " " + renameAction));
    }

    public boolean isRemoveDisplayed(SitePageType page)
    {
        return isElementDisplayed(By.cssSelector(page.getCustomizeCssLocator() + " " + removeAction));
    }

    public void addPageToSite(SitePageType page)
    {
        WebElement pageElem = getSitePageType(page);
        clickElement(pageElem);
        waitUntilElementIsVisible(pageElem);
        scrollToElement(waitUntilElementIsVisible(currentSitePagesArea));
        clickElement(pageElem);
        waitUntilElementHasAttribute(pageElem, "class", "dnd-focused");
        dragAndDrop(pageElem, waitUntilElementIsVisible(currentSitePagesArea));
        retryAddPageToSite(page, pageElem);
    }

    private void retryAddPageToSite(SitePageType page, WebElement pageElem )
    {
        int i = 0;
        int retry = 5;
        boolean added = isPageAddedToCurrentPages(page);
        while (i < retry && !added)
        {
            log.info(String.format("Retry add page - %s", i));
            dragAndDrop(pageElem, waitUntilElementIsVisible(currentSitePagesArea));
            added = isPageAddedToCurrentPages(page);
            i++;
        }
    }

    public boolean isPageAddedToCurrentPages(SitePageType page)
    {
        return isElementDisplayed(By.cssSelector("ul[id$='default-currentPages-ul'] " + page.getCustomizeCssLocator()));
    }

    public void removePage(SitePageType page)
    {
        WebElement pageElem = getSitePageType(page);
        pageElem.findElement(By.cssSelector(removeAction));
    }

    public CustomizeSitePage renameSitePage(SitePageType page, String newName)
    {
        log.info("Rename page {} with value {}", page, newName);
        WebElement pageType = getSitePageType(page);
        clickElement(pageType.findElement(By.cssSelector(renameAction)));
        typeDisplayNameAndSave(newName);

        return this;
    }

    private void typeDisplayNameAndSave(String newName)
    {
        RenameSitePageDialog renameSitePageDialog = new RenameSitePageDialog(webDriver);
        renameSitePageDialog.typeDisplayName(newName);
        renameSitePageDialog.clickOk();
    }

    private WebElement getSitePageType(SitePageType page)
    {
        return waitUntilElementIsVisible(By.cssSelector(page.getCustomizeCssLocator()));
    }

    /**
     * Get the display name for a page
     *
     * @param page SitePageType the page
     * @return String display name
     */
    public String getPageDisplayName(SitePageType page)
    {
        WebElement pageElem = getSitePageType(page);
        return pageElem.findElement(By.cssSelector(".title")).getText();
    }
}
