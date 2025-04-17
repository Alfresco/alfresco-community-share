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
    private final String hylandUrl = "https://www.hyland.com/en/solutions/products/alfresco-platform";
    private final String hylandLegalAgreementsUrl = "https://www.hyland.com/en/legal/alfresco-agreements";
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
        return getElementText(shareVersion);
    }

    public String getAlfrescoVersion()
    {
        return getElementText(alfrescoVersion);
    }

    public String getLicenseHolder()
    {
        return getElementText(licenseHolder);
    }

    public boolean isContributionsDisplayed()
    {
        return isElementDisplayed(contributions);
    }

    public String getCopyRight()
    {
        return getElementText(copyRight);
    }

    public void clickAlfrescoLink()
    {
        waitUntilElementIsVisible(alfrescoLink);
        clickElement(alfrescoLink);
    }

    public void clickLegalAndLicenceLink()
    {
        waitUntilElementIsVisible(legalAndLicenceLink);
        clickElement(legalAndLicenceLink);
    }

    public AboutPopUpPage assertClickAlfrescoLink()
    {
        clickAlfrescoLink();
        switchWindow(1);
        waitUrlContains(hylandUrl, 10);
        assertEquals(getCurrentUrl(), hylandUrl, "Displayed URL=");
        closeWindowAndSwitchBack();
        return this;
    }

    public AboutPopUpPage assertClickLegalAndLicenseLink()
    {
        clickLegalAndLicenceLink();
        switchWindow(1);
        waitUrlContains(hylandLegalAgreementsUrl, WAIT_10.getValue());
        assertEquals(getCurrentUrl(), hylandLegalAgreementsUrl, "Displayed URL=");
        closeWindowAndSwitchBack();
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
