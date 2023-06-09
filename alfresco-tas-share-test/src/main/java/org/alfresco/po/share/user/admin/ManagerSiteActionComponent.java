package org.alfresco.po.share.user.admin;

import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class ManagerSiteActionComponent extends SitesManagerPage
{
    private final By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
    private final By siteRowActionsButton = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");
    private final By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager .value");
    private final By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
    private final By siteRowVisibilityArrow = By.cssSelector("input[class$='dijitArrowButtonInner']");
    private final By successIndicator = By.cssSelector("div[class='indicator success']");
    private final By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
    private final By dropdownOptionsList = By.cssSelector("div.dijitPopup[style*=visible] td.dijitMenuItemLabel");
    private final By siteManagerList = By.xpath("//span[@class=\"value\"]");

    private final String dropdownOptions = "//div[@class='dijitPopup Popup' and contains(@style, visible)]//td[@class='dijitReset dijitMenuItemLabel' and text()='%s']";
    private final String siteName;

    protected ManagerSiteActionComponent(ThreadLocal<WebDriver> webDriver, String siteName)
    {
        super(webDriver);
        this.siteName = siteName;
    }

    public ManagerSiteActionComponent assertSiteIsNotDisplayed()
    {
        log.info("Assert site is not displayed");
        assertNull(getSiteRow(), String.format("Site %s is displayed", siteName));
        return this;
    }
    public boolean isSiteDisplayed(String siteName)
    {
        List<WebElement> siteManagerSiteList = findElements(siteManagerList);
        for (WebElement siteNames : siteManagerSiteList)
        {
            if (siteNames.getText().equals(siteName))
            {
                return true;
            }
        }
        return false;
    }


    private void clickActionsButton()
    {
        WebElement actionsButton = getSiteRow().findElement(siteRowActionsButton);
        mouseOver(actionsButton);
        clickElement(actionsButton);
    }

    public ManagerSiteActionComponent becomeSiteManager()
    {
        log.info("Select action Become Site Manager");
        clickActionsButton();
        waitUntilElementsAreVisible(dropdownOptionsList);
        WebElement becomeBtn = findFirstElementWithValue(dropdownOptionsList,
            language.translate("sitesManager.becomeSiteManager"));
        mouseOver(becomeBtn);
        clickElement(becomeBtn);
        waitForSitesTableHeaderToBeVisible();
        waitInSeconds(1);

        return this;
    }

    public ManagerSiteActionComponent assertSiteManagerIsYes()
    {
        log.info("Assert I'm site manager is set to Yes");
        assertEquals(getElementText(getSiteRow().findElement(siteRowSiteManager)),
            language.translate("adminTools.siteManager.yes"), "Is site manager");
        return this;
    }

    public ManagerSiteActionComponent assertSiteManagerIsNo()
    {
        log.info("Assert I'm site manager is set to No");
        assertEquals(getElementText(getSiteRow().findElement(siteRowSiteManager)),
            language.translate("adminTools.siteManager.no"), "Is site manager");
        return this;
    }

    public SiteManagerDeleteSiteDialog clickDelete()
    {
        log.info("Click Delete");
        clickActionsButton();
        selectDeleteSite();
        return new SiteManagerDeleteSiteDialog(webDriver);
    }

    private void selectDeleteSite()
    {
        clickElement(findFirstElementWithValue(dropdownOptionsList,
            language.translate("sitesManager.deleteSite")));
    }

    public ManagerSiteActionComponent changeSiteVisibility(Visibility visibility)
    {
        log.info("Change site visibility to {}", visibility.toString());
        WebElement siteRow = getSiteRow();
        clickElement(siteRow.findElement(siteRowVisibilityArrow));

        List<WebElement> options = getVisibilityOptions();
        String visibilityValue = getCapitalizedVisibility(visibility);
        WebElement option = findFirstElementWithValue(options, visibilityValue);

        clickOptionAndWaitForChildLocatorPresence(option, successIndicator);

        return this;
    }

    private WebElement clickOptionAndWaitForChildLocatorPresence(WebElement option, By locator)
    {
        mouseOver(option);
        clickElement(option);
        waitUntilChildElementIsPresent(getSiteRow(), locator);

        return option;
    }

    private List<WebElement> getVisibilityOptions()
    {
        List<WebElement> options = waitUntilElementsAreVisible(dropdownOptionsList);
        waitInSeconds(WAIT_2.getValue());
        return options;
    }

    private String getCapitalizedVisibility(Visibility visibility)
    {
        String visibilityValue = visibility.toString().toLowerCase();
        visibilityValue = StringUtils.capitalize(visibilityValue);
        return visibilityValue;
    }

    public ManagerSiteActionComponent assertSiteVisibilityEquals(Visibility visibility)
    {
        log.info("Assert site visibility is: {}", visibility.toString());
        String visibilityValue = getCapitalizedVisibility(visibility);

        WebElement siteRow = getSiteRow();
        WebElement visibilityElement = waitUntilChildElementIsPresent(siteRow, siteRowVisibility);
        String actualVisibility = getElementText(visibilityElement);
        assertEquals(actualVisibility, visibilityValue,"Site visibility is correct");

        return this;
    }

    public ManagerSiteActionComponent assertSuccessIndicatorIsDisplayed()
    {
        log.info("Assert success indicator is displayed");
        boolean isSuccessIndicatorDisplayed = isElementDisplayed(getSiteRow().findElement(successIndicator));

        assertTrue(isSuccessIndicatorDisplayed, "Success indicator is not displayed");
        return this;
    }

    public ManagerSiteActionComponent assertSiteDescriptionEqualsTo(String expectedSiteDescription)
    {
        log.info(String.format("Assert site description is %s", expectedSiteDescription));
        String actualSiteDescription = getElementText(getSiteRow().findElement(siteRowDescription));

        assertEquals(actualSiteDescription, expectedSiteDescription,
            "Site description is correct");
        return this;
    }

    public ManagerSiteActionComponent assertBecomeSiteManagerOptionIsNotDisplayed()
    {
        log.info("Assert Become site manager option is not displayed");
        assertFalse(isBecomeSiteManagerDisplayed(), "Become site manager is displayed");
        return this;
    }

    public ManagerSiteActionComponent assertBecomeSiteManagerOptionIsDisplayed()
    {
        log.info("Assert Become site manager option is not displayed");
        assertTrue(isBecomeSiteManagerDisplayed(), "Become site manager is not displayed");
        return this;
    }

    public ManagerSiteActionComponent assertDeleteSiteOptionIsDisplayed()
    {
        log.info("Assert Delete Site option is available");
        assertTrue(isDeleteSiteDisplayed(), "Delete site is not displayed");
        return this;
    }

    private boolean isDeleteSiteDisplayed()
    {
        return isElementDisplayed(By.xpath(
            String.format(dropdownOptions, language.translate("sitesManager.deleteSite"))));
    }

    private boolean isBecomeSiteManagerDisplayed()
    {
        return isElementDisplayed(By.xpath(
            String.format(dropdownOptions, language.translate("sitesManager.becomeSiteManager"))));
    }

    private WebElement getSiteRow()
    {
        return getSiteRowBasedOnSiteName(siteName);
    }

    public ManagerSiteActionComponent openActionsDropDown()
    {
        log.info("Open Actions dropdown with site name {}", siteName);
        clickElement(getSiteRow().findElement(siteRowActionsButton));
        return this;
    }

    public SiteMembersPage navigateToSiteMembersPage()
    {
        log.info("Navigate to Site members page");
        clickElement(getSiteRow().findElement(siteRowName));
        return new SiteMembersPage(webDriver);
    }

    public ManagerSiteActionComponent assertSiteIsDisplayed(String site) {
        {
            log.info("Assert Become site manager option is not displayed");
            assertTrue(isSiteDisplayed(site), "Become site manager is not displayed");
            return this;
        }
    }
}
