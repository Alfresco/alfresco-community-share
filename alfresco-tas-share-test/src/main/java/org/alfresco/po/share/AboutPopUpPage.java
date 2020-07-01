package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import static org.testng.Assert.*;

/**
 * @author Bogdan.Bocancea
 */
@PageObject
public class AboutPopUpPage extends ShareDialog
{
    private final String alfrescoUrl = "https://www.alfresco.com/";

    @RenderWebElement
    @FindBy (css = ".about>.header:nth-child(1)")
    protected WebElement shareVersion;

    @RenderWebElement
    @FindBy (css = ".about>div:nth-child(2)")
    protected TextBlock shareComponentsVersions;

    @RenderWebElement
    @FindBy (css = ".about>.header:nth-child(3)")
    protected WebElement alfrescoVersion;

    @FindBy (css = ".about>div:nth-child(4)")
    protected TextBlock alfrescoBuildDetails;

    @RenderWebElement
    @FindBy (css = ".about .licenseHolder")
    protected WebElement licenseHolder;

    @FindBy (css = ".contributions-bg")
    protected WebElement contributions;

    @FindBy (css = ".copy > span")
    protected TextBlock copyRight;

    @FindBy (css = ".copy>a:nth-child(2)")
    protected WebElement alfrescoLink;

    @FindBy (css = ".copy>a:nth-child(3)")
    protected HtmlElement legalAndLicenceLink;

    /**
     * Get share versions
     *
     * @return String share version
     */
    public String getShareVersion()
    {
        browser.waitUntilElementVisible(shareVersion);
        return shareVersion.getText();
    }

    /**
     * Get share components versions
     *
     * @return String share components versions (e.g. Aikau, Spring, Freemarker)
     */
    public String getShareComponentsVersions()
    {
        return shareComponentsVersions.getText();
    }

    /**
     * Get alfresco version
     *
     * @return String alfresco version
     */
    public String getAlfrescoVersion()
    {
        browser.waitUntilElementVisible(alfrescoVersion);
        return alfrescoVersion.getText();
    }

    /**
     * Get alfresco build details
     *
     * @return String alfresco build details
     */
    public String getAlfrescoBuildDetails()
    {
        return alfrescoBuildDetails.getText();
    }

    /**
     * Get licence holder
     *
     * @return String license holder
     */
    public String getLicenseHolder()
    {
        return licenseHolder.getText();
    }

    /**
     * Verify if contributions section is displayed
     *
     * @return true if displayed
     */
    public boolean isContributionsDisplayed()
    {
        return contributions.isDisplayed();
    }

    /**
     * Get the copyrights details
     *
     * @return String get copyright details
     */
    public String getCopyRight()
    {
        return copyRight.getText();
    }

    /**
     * Click alfresco link
     */
    public void clickAlfrescoLink()
    {
        alfrescoLink.click();
    }

    /**
     * Click Legal and Licence link
     */
    public void clickLegalAndLicenceLink()
    {
        legalAndLicenceLink.click();
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
        getBrowser().waitUrlContains("https://www.alfresco.com/", 10);
        assertEquals(getBrowser().getCurrentUrl(), "https://www.alfresco.com/legal/agreements", "Displayed URL=");
        getBrowser().closeWindowAndSwitchBack();
        return this;
    }

    public AboutPopUpPage assertAlfrescoVersionIsDisplayed()
    {
        assertEquals(getShareVersion().substring(0, 14), "Alfresco Share", "Share version=");
        return this;
    }

    public AboutPopUpPage assertShareVersionIsDisplayed()
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
        assertEquals(getCopyRight(), language.translate("about.copyRight"), "Copy right=");
        return this;
    }

    public AboutPopUpPage assertContributionSectionIsDisplayed()
    {
        assertTrue(isContributionsDisplayed(), "Contributions is displayed.");
        return this;
    }
}
