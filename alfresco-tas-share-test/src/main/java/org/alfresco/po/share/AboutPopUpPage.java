package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author Bogdan.Bocancea
 */
public class AboutPopUpPage extends BaseDialogComponent
{
    private final String alfrescoUrl = "https://www.alfresco.com/";

    private final By shareVersion = By.cssSelector(".about>.header:nth-child(1)");
    private final By alfrescoVersion = By.cssSelector(".about>.header:nth-child(3)");
    private final By licenseHolder = By.cssSelector(".about .licenseHolder");
    private final By contributions = By.cssSelector(".contributions-bg");
    private final By copyRight = By.cssSelector(".copy > span");
    private final By alfrescoLink = By.cssSelector(".copy>a:nth-child(2)");
    private final By legalAndLicenceLink = By.cssSelector(".copy>a:nth-child(3)");

    public AboutPopUpPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getShareVersion()
    {
        return webElementInteraction.getElementText(shareVersion);
    }

    public String getAlfrescoVersion()
    {
        return webElementInteraction.getElementText(alfrescoVersion);
    }

    public String getLicenseHolder()
    {
        return webElementInteraction.getElementText(licenseHolder);
    }

    public boolean isContributionsDisplayed()
    {
        return webElementInteraction.isElementDisplayed(contributions);
    }

    public String getCopyRight()
    {
        return webElementInteraction.getElementText(copyRight);
    }

    public void clickAlfrescoLink()
    {
        webElementInteraction.waitUntilElementIsVisible(alfrescoLink);
        webElementInteraction.clickElement(alfrescoLink);
    }

    public void clickLegalAndLicenceLink()
    {
        webElementInteraction.waitUntilElementIsVisible(legalAndLicenceLink);
        webElementInteraction.clickElement(legalAndLicenceLink);
    }

    public AboutPopUpPage assertClickAlfrescoLink()
    {
        clickAlfrescoLink();
        webElementInteraction.switchWindow(1);
        webElementInteraction.waitUrlContains(alfrescoUrl, 10);
        assertEquals(webElementInteraction.getCurrentUrl(), alfrescoUrl, "Displayed URL=");
        webElementInteraction.closeWindowAndSwitchBack();
        return this;
    }

    public AboutPopUpPage assertClickLegalAndLicenseLink()
    {
        clickLegalAndLicenceLink();
        webElementInteraction.switchWindow(1);
        webElementInteraction.waitUrlContains(alfrescoUrl, WAIT_10.getValue());
        assertEquals(webElementInteraction.getCurrentUrl(), "https://www.alfresco.com/legal/agreements", "Displayed URL=");
        webElementInteraction.closeWindowAndSwitchBack();
        return this;
    }

    public AboutPopUpPage assertShareVersionIsDisplayed()
    {
        assertEquals(getShareVersion().substring(0, 14), "Alfresco Share", "Share version=");
        return this;
    }

    public AboutPopUpPage assertAlfrescoVersionIsDisplayed()
    {
        assertEquals(getShareVersion().substring(0, 14), "Alfresco Share", "Share version=");
        return this;
    }

    public AboutPopUpPage assertLicenseHolderIsNotEmpty()
    {
        assertFalse(getLicenseHolder().isEmpty(), "License holder is empty.");
        return this;
    }

    public AboutPopUpPage assertCopyrightIsCorrect()
    {
        assertTrue(getCopyRight().contains(language.translate("about.copyRight")) , "Copy right is correct");
        return this;
    }

    public AboutPopUpPage assertContributionSectionIsDisplayed()
    {
        assertTrue(isContributionsDisplayed(), "Contributions is displayed.");
        return this;
    }
}
