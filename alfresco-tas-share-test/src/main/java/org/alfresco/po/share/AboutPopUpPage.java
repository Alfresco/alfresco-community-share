package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * @author Bogdan.Bocancea
 */
@PageObject
public class AboutPopUpPage extends ShareDialog
{
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

    @FindBy (css = ".copy")
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
        browser.waitInSeconds(2);
    }

    /**
     * Click Legal and Licence link
     */
    public void clickLegalAndLicenceLink()
    {
        legalAndLicenceLink.click();
        browser.waitInSeconds(1);
    }
}
