package org.alfresco.po.share.dashlet;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class ImagePreviewDashlet extends Dashlet<ImagePreviewDashlet>
{
    private static By helpBalloonCloseButton = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.closeButton");
    @RenderWebElement
    @FindBy (xpath = "//div[normalize-space(.) = 'Image Preview']")
    protected HtmlElement dashletContainer;
    @FindAll (@FindBy (xpath = ".//div[@class='thumbnail']/a"))
    protected List<WebElement> ImagePreviewLinks;
    @FindBy (css = "div[id*='default-images'] img[src*='document-view-details']")
    private WebElement viewDetailsIcon;
    @FindBy (css = "div[id*='default-images'] img[src*='download']")
    private WebElement downloadIcon;
    @RenderWebElement
    @FindBy (xpath = "//div[starts-with(@class,'dashlet resizable')] // div[@class='title']")
    private WebElement dashletTitle;
    @FindBy (xpath = "//div[starts-with(@class,'dashlet resizable')] //div[@class='titleBarActionIcon help']")
    private WebElement helpIcon;
    private String dashletHelpIcon = "//div[starts-with(@class,'dashlet resizable')] //div[@class='titleBarActionIcon help']";
    private String helpBallon = "div[style*='visible']>div.bd>div.balloon";
    // @RenderWebElement
    @FindBy (css = "div[style*='visible']>div.bd>div.balloon")
    private WebElement helpBalloon;
    @FindBy (css = "div[style*='visible']>div.bd>div.balloon>div.text")
    private WebElement helpBalloonText;
    private String imageLink = "//img[contains(@title,'";

    @Override
    protected String getDashletTitle()
    {
        return null;
    }

    /**
     * Get the text from the title
     *
     * @return String dashlet title text
     */
    public String getDashletTitleText()
    {
        return dashletTitle.getText();
    }

    /**
     * Verify if Help Icon is displayed
     *
     * @return true if displayed
     */

    public boolean isHelpIconDisplayed()
    {
        browser.mouseOver(browser.findElement(By.xpath(dashletHelpIcon)));
        return browser.waitUntilElementVisible(By.xpath(dashletHelpIcon)).isDisplayed();
    }

    /**
     * This method is used to click on "Help" icon
     */
    public void clickHelpIcon()
    {
        helpIcon.click();
    }

    /**
     * Verify if help balloon is displayed on this dashlet.
     *
     * @return True if the balloon is displayed, else false.
     */
    public boolean isBalloonDisplayed()
    {
        return browser.waitUntilElementVisible(By.cssSelector(helpBallon)).isDisplayed();
    }

    /**
     * Verify if help balloon is displayed on this dashlet (without "waiting")
     *
     * @return True if the balloon is displayed, else false.
     */
    public boolean isBallonDisplayedNoWait()
    {
        return browser.isElementDisplayed(helpBalloon);
    }

    /**
     * Get the text from the Help Balloon
     *
     * @return String help balloon text
     */
    public String getHelpBalloonText()
    {
        return helpBalloonText.getText();
    }

    /**
     * This method is used to click "Close" button for help balloon
     */
    public void CloseHelpBalloon()
    {
        browser.findElement(helpBalloonCloseButton).click();
        browser.waitUntilElementDisappears(helpBalloonCloseButton, TimeUnit.SECONDS.toMillis(properties.getImplicitWait()));
    }

    /**
     * This method is used to verify if an image is displayed on "Image Preview" dashlet
     */
    public boolean isImageDisplayed(String imageName)
    {
        String image1 = imageLink + imageName + "')]";
        String image = StringUtils.deleteWhitespace(image1);

        browser.waitUntilElementIsDisplayedWithRetry(By.xpath(image), 5);
        return browser.findElement(By.xpath(image)) != null;
    }

    public void clickViewDetailsIcon(String imageName)
    {
        String image = "//a[contains(@title,'" + imageName + "')]";

        this.renderedPage();
        browser.waitUntilElementIsDisplayedWithRetry(By.xpath("//a[contains(@title,'newavatar.jpg')]"));
        WebElement imageThumbnail = browser.findElement(By.xpath(image));

        browser.mouseOver(imageThumbnail);
        browser.waitInSeconds(2);
        browser.waitUntilElementVisible(viewDetailsIcon);
        viewDetailsIcon.click();
    }

    public void clickDownloadIcon(String imageName)
    {
        String image = "//a[contains(@title,'" + imageName + "')]";

        this.renderedPage();
        browser.waitUntilElementIsDisplayedWithRetry(By.xpath("//a[contains(@title,'newavatar.jpg')]"));
        WebElement imageThumbnail = browser.findElement(By.xpath(image));

        browser.mouseOver(imageThumbnail);
        browser.waitInSeconds(2);
        browser.waitUntilElementVisible(downloadIcon);
        downloadIcon.click();
        acceptAlertIfDisplayed();
    }
}