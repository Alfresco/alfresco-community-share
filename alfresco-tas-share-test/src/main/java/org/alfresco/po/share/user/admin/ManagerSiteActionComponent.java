package org.alfresco.po.share.user.admin;

import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Language;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class ManagerSiteActionComponent
{
    private final WebElementInteraction webElementInteraction;
    private final SitesManagerPage sitesManagerPage;
    private final String siteName;
    private final SiteManagerDeleteSiteDialog deleteSiteDialog;
    private final Language language;
    private final By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
    private final By siteRowActionsButton = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");
    private final By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager .value");
    private final By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
    private final By siteRowVisibilityArrow = By.cssSelector("input[class$='dijitArrowButtonInner']");
    private final By successIndicator = By.cssSelector("div[class='indicator success']");
    private final By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
    private final By dropdownOptionsList = By.cssSelector("div.dijitPopup[style*=visible] td.dijitMenuItemLabel");

    public ManagerSiteActionComponent(SitesManagerPage sitesManagerPage,
                                      WebElementInteraction webElementInteraction,
                                      String siteName,
                                      SiteManagerDeleteSiteDialog deleteSiteDialog,
                                      Language language)
    {
        this.sitesManagerPage = sitesManagerPage;
        this.webElementInteraction = webElementInteraction;
        this.siteName = siteName;
        this.deleteSiteDialog = deleteSiteDialog;
        this.language = language;

        log.info("Using site: {}", siteName);
    }

    public WebElement getSiteRow()
    {
        return sitesManagerPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
    }

    public ManagerSiteActionComponent assertSiteIsNotDisplayed()
    {
        log.info("Assert site is not displayed");
        assertNull(getSiteRow(), String.format("Site %s is displayed", siteName));
        return this;
    }

    private void clickActionsButton()
    {
        WebElement actionsButton = getSiteRow().findElement(siteRowActionsButton);
        webElementInteraction.mouseOver(actionsButton);
        webElementInteraction.clickElement(actionsButton);
    }

    public ManagerSiteActionComponent becomeSiteManager()
    {
        log.info("Select action Become Site Manager");
        clickActionsButton();
        webElementInteraction.waitUntilElementsAreVisible(dropdownOptionsList);
        WebElement becomeBtn = webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"));
        webElementInteraction.mouseOver(becomeBtn);
        webElementInteraction.clickElement(becomeBtn);
        sitesManagerPage.waitUntilLoadingMessageDisappears();

        return this;
    }

    public ManagerSiteActionComponent assertSiteManagerIsYes()
    {
        log.info("Assert I'm site manager is set to Yes");
        assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
            language.translate("adminTools.siteManager.yes"),"Is site manager");
        return this;
    }

    public ManagerSiteActionComponent assertSiteManagerIsNo()
    {
        log.info("Assert I'm site manager is set to No");
        assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
            language.translate("adminTools.siteManager.no"),"Is site manager");
        return this;
    }

    public SiteManagerDeleteSiteDialog clickDelete()
    {
        log.info("Click Delete");
        clickActionsButton();
        webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite")).click();
        return deleteSiteDialog;
    }

    public ManagerSiteActionComponent deleteSite()
    {
        log.info("Delete site");
        clickDelete();
        deleteSiteDialog.clickDeleteFromSitesManager();
        webElementInteraction.waitUntilElementDisappears(getSiteRow());
        return this;
    }

    public ManagerSiteActionComponent changeSiteVisibility(SiteService.Visibility visibility)
    {
        log.info("Change site visibility to {}", visibility.toString());
        WebElement siteRow = getSiteRow();
        String visibilityValue = visibility.toString().toLowerCase();
        visibilityValue = StringUtils.capitalize(visibilityValue);

        webElementInteraction.clickElement(siteRow.findElement(siteRowVisibilityArrow));
        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(dropdownOptionsList);
        webElementInteraction.waitInSeconds(WAIT_2.getValue());
        WebElement option = webElementInteraction.findFirstElementWithValue(options, visibilityValue);
        webElementInteraction.mouseOver(option);
        webElementInteraction.clickElement(option);
        webElementInteraction.waitUntilChildElementIsPresent(getSiteRow(), successIndicator);

        return this;
    }

    public ManagerSiteActionComponent assertSiteVisibilityEquals(SiteService.Visibility visibility)
    {
        log.info("Assert site visibility is: {}", visibility.toString());
        String visibilityValue = visibility.toString().toLowerCase();
        visibilityValue = StringUtils.capitalize(visibilityValue);
        WebElement siteRow = getSiteRow();
        WebElement visibilityElement = webElementInteraction.waitUntilChildElementIsPresent(siteRow, siteRowVisibility);
        assertEquals(webElementInteraction.getElementText(visibilityElement), visibilityValue,"Site visibility is correct");

        return this;
    }

    public ManagerSiteActionComponent assertSuccessIndicatorIsDisplayed()
    {
        log.info("Assert success indicator is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getSiteRow().findElement(successIndicator)), "Success indicator is displayed");
        return this;
    }

    public ManagerSiteActionComponent assertSiteDescriptionIs(String expectedValue)
    {
        log.info(String.format("Assert site description is %s", expectedValue));
        assertEquals(getSiteRow().findElement(siteRowDescription).getText(), expectedValue, "Site description is correct");
        return this;
    }

    public ManagerSiteActionComponent assertBecomeManagerOptionIsAvailable()
    {
        log.info("Assert Become site manager option is available");
        webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
        assertTrue(webElementInteraction.isElementDisplayed(webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
        return this;
    }

    public ManagerSiteActionComponent assertBecomeManagerOptionIsNotAvailable()
    {
        log.info("Assert Become site manager option is NOT available");
        webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
        assertFalse(webElementInteraction.isElementDisplayed( webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
        return this;
    }

    public ManagerSiteActionComponent assertDeleteSiteOptionIsAvailable()
    {
        log.info("Assert Delete Site option is available");
        webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
        assertTrue(webElementInteraction.isElementDisplayed(webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
        return this;
    }

    public void clickSiteName()
    {
        log.info("Click Site Name");
        webElementInteraction.clickElement(getSiteRow().findElement(siteRowName));
    }
}
