package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;


/**
 * handle common elements of all Alfresco Share dashlets
 *
 * @author Cristina.Axinte
 */

@PageObject
public abstract class Dashlet<T> extends SharePage<Dashlet<T>>
{
    @RenderWebElement
    @FindBy (css = "div.title")
    protected static TextBlock title;

    @FindBy (css = "div[style*='visible']>div>div.balloon")
    // @CacheLookup
    protected static WebElement helpBallon;

    @FindBy (css = "div[style*='visible']>div>div.balloon>div.text")
    protected static TextBlock helpBallonText;

    protected static By dashletTitle = By.cssSelector("div.title");
    protected static By helpBalloonCloseButton = By.cssSelector("div[style*='visible']>div>div>.closeButton");
    protected String dashletBar = "div[class*='%s'] div[class='title']";

    protected String helpIcon = "div[class*='%s'] div[class='titleBarActionIcon help']";
    private HtmlElement currentHandleElement;
    private String resizeDashlet = "//div[text()='%s']/../div[@class='yui-resize-handle yui-resize-handle-b']";

    public enum DashletHelpIcon
    {
        MY_SITES("my-sites"),
        MY_TASKS("my-tasks"),
        MY_ACTIVITIES("activities"),
        MY_DOCUMENTS("my-documents"),
        MY_PROFILE("dashlet"),
        MY_DOC_WORKSPACES("my-workspaces"),
        MY_CALENDAR("user-calendar"),
        RSS_FEED("rssfeed"),
        WEB_VIEW("webview"),
        SAVED_SEARCH("savedsearch"),
        SITE_SEARCH("sitesearch"),
        MY_DISCUSSIONS("forumsummary"),
        MY_MEETING_WORKSPACES("my-meeting-workspaces"),
        SITE_PROFILE("site-profile"),
        SITE_CALENDAR("calendar"),
        SITE_LINKS("site-links"),
        SITE_CONTENT("docsummary"),
        WIKI("wiki"),
        SITE_ACTIVITIES("activities"),
        SITE_MEMBERS("colleagues"),
        DATA_LISTS("site-data-lists"),
        SITE_NOTICE("notice-dashlet");

        public final String name;

        DashletHelpIcon(String name)
        {
            this.name = name;
        }
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/user/admin/dashboard";
    }

    /**
     * Gets the title on the dashlet panel.
     * Implementation example take a look on {@link MySitesDashlet#getDashletTitle()}
     *
     * @return String dashlet title
     */
    protected abstract String getDashletTitle();

    /**
     * This method is used to scroll down the current window.
     */
    protected void scrollDownToDashlet()
    {
        Actions action = new Actions(browser);
        action.moveToElement(currentHandleElement);
        action.perform();
    }

    /**
     * This method is used to Finds Help icon and clicks on it.
     */
    public void clickOnHelpIcon(DashletHelpIcon dashlet)
    {
        browser.findElement(By.cssSelector(String.format(helpIcon, dashlet.name))).click();
    }

    /**
     * Returns if help balloon is displayed on this dashlet.
     *
     * @return True if the balloon is displayed else false.
     */
    public boolean isBalloonDisplayed()
    {
        return browser.isElementDisplayed(helpBallon);
    }

    /**
     * Returns if help icon is displayed on this dashlet.
     *
     * @return True if the help icon is displayed else false.
     */
    public boolean isHelpIconDisplayed(DashletHelpIcon dashlet)
    {
        browser.mouseOver(browser.findElement(By.cssSelector(String.format(dashletBar, dashlet.name))));
        return browser.waitUntilElementVisible(By.cssSelector(String.format(helpIcon, dashlet.name))).isDisplayed();
    }

    /**
     * This method gets the Help balloon messages
     *
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        return helpBallonText.getText();
    }

    /**
     * This method closes the Help balloon message.
     */
    public void closeHelpBalloon()
    {
        browser.findElement(helpBalloonCloseButton).click();
        browser.waitUntilElementDisappears(helpBalloonCloseButton, TimeUnit.SECONDS.toMillis(properties.getImplicitWait()));
    }

    public boolean isDashletExpandable()
    {
        return browser.isElementDisplayed(By.xpath(String.format(resizeDashlet, this.getDashletTitle())));
    }

    /**
     * Resize dashlet
     *
     * @param height int new height
     */
    public void resizeDashlet(int height, int scrolldown)
    {
        WebElement resizeDash = null;
        try
        {
            resizeDash = browser.findElement(By.xpath(String.format(resizeDashlet, this.getDashletTitle())));
        } catch (NoSuchElementException ns)
        {
            throw new PageOperationException(this.getDashletTitle() + " is not expandable");
        }
        if (scrolldown == 1)
        {
            ((JavascriptExecutor) getBrowser()).executeScript("window.scrollBy(0,500)");
        }


        browser.dragAndDrop(resizeDash, 0, height);
    }

    /**
     * Get dashlet height
     *
     * @return int dashlet height
     */
    public int getDashletHeight()
    {
        WebElement dashletContainer = browser.findElement(By.xpath(String.format("//div[text()='%s']/..", this.getDashletTitle())));
        String val = dashletContainer.getCssValue("height").replace("px", "");
        try
        {
            return Integer.valueOf(val);
        } catch (NumberFormatException nf)
        {
            double dblVal = Double.parseDouble(val);
            return (int) dblVal;
        }
    }

    public String getDashletColor()
    {
        String hex = "";
        WebElement dashletContainer = browser.findElement(By.cssSelector(".dashlet .title"));
        String color = dashletContainer.getCssValue("background-color");
        String[] numbers = color.replace("rgb(", "").replace(")", "").split(",");
        int number1 = Integer.parseInt(numbers[0]);
        numbers[1] = numbers[1].trim();
        int number2 = Integer.parseInt(numbers[1]);
        numbers[2] = numbers[2].trim();
        int number3 = Integer.parseInt(numbers[2]);
        hex = String.format("#%02x%02x%02x", number1, number2, number3);
        return hex;
    }
}