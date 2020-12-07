package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.*;

/**
 * @author Bogdan.Bocancea
 */
public class AboutPopUpPage extends BaseDialogComponent
{
    private final String alfrescoUrl = "https://www.alfresco.com/";

    @RenderWebElement
    private final By shareVersion = By.cssSelector(".about>.header:nth-child(1)");
    @RenderWebElement
    private final By shareComponentsVersions = By.cssSelector(".about>div:nth-child(2)");
    private final By alfrescoVersion = By.cssSelector(".about>.header:nth-child(3)");
    private final By alfrescoBuildDetails = By.cssSelector(".about>div:nth-child(4)");
    private final By licenseHolder = By.cssSelector(".about .licenseHolder");
    private final By contributions = By.cssSelector(".contributions-bg");
    private final By copyRight = By.cssSelector(".copy > span");
    private final By alfrescoLink = By.cssSelector(".copy>a:nth-child(2)");
    private final By legalAndLicenceLink = By.cssSelector(".copy>a:nth-child(3)");

    public AboutPopUpPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getShareVersion()
    {
        return getBrowser().waitUntilElementVisible(shareVersion).getText();
    }

    public String getShareComponentsVersions()
    {
        return getBrowser().waitUntilElementVisible(shareComponentsVersions).getText();
    }

    public String getAlfrescoVersion()
    {
        return getBrowser().waitUntilElementVisible(alfrescoVersion).getText();
    }

    public String getAlfrescoBuildDetails()
    {
        return getBrowser().findElement(alfrescoBuildDetails).getText();
    }

    public String getLicenseHolder()
    {
        return getBrowser().findElement(licenseHolder).getText();
    }

    public boolean isContributionsDisplayed()
    {
        return getBrowser().isElementDisplayed(contributions);
    }

    public String getCopyRight()
    {
        return getBrowser().findElement(copyRight).getText();
    }

    public void clickAlfrescoLink()
    {
        getBrowser().waitUntilElementClickable(alfrescoLink).click();
    }

    public void clickLegalAndLicenceLink()
    {
        getBrowser().waitUntilElementClickable(legalAndLicenceLink).click();
    }

    public AboutPopUpPage assertClickAlfrescoLink()
    {
        clickAlfrescoLink();
        getBrowser().switchWindow(1);
        getBrowser().waitUrlContains(alfrescoUrl, 10);
        assertEquals(getBrowser().getCurrentUrl(), alfrescoUrl, "Displayed URL=");
        getBrowser().closeWindowAndSwitchBack();
        return this;
    }

    public AboutPopUpPage assertClickLegalAndLicenseLink()
    {
        clickLegalAndLicenceLink();
        getBrowser().switchWindow(1);
        getBrowser().waitUrlContains(alfrescoUrl, 10);
        assertEquals(getBrowser().getCurrentUrl(), "https://www.alfresco.com/legal/agreements", "Displayed URL=");
        getBrowser().closeWindowAndSwitchBack();
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
