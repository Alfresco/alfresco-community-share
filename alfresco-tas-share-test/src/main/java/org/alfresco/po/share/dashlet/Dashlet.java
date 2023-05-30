package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

@Slf4j
public abstract class Dashlet<T> extends BasePage
{
    protected final By dashletTitle = By.cssSelector("div.title");
    private final By helpBallon = By.cssSelector("div[style*='visible']>div>div.balloon");
    private final By helpBallonText = By.cssSelector("div[style*='visible']>div>div.balloon>div.text");
    private final By helpBalloonCloseButton = By.cssSelector("div[style*='visible']>div>div>.closeButton");

    private final String dashlet = "//div[contains(@class, 'dashlet') and contains(@class, '%s')]";
    private final String dashletBar = "div[class*='%s'] div[class='title']";
    private final String dashletContainerLocator = "//div[text()='%s']/..";
    private final String helpIcon = "div[class*='%s'] div[class='titleBarActionIcon help']";
    private final String resizeDashlet = "//div[text()='%s']/../div[@class='yui-resize-handle yui-resize-handle-b']/div";

    protected Dashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    protected abstract String getDashletTitle();

    public boolean isDashletDisplayed(DashletHelpIcon dashletName)
    {
        By dashletToCheck = By.xpath(String.format(dashlet, dashletName.name));
        waitUntilElementIsVisible(dashletToCheck);
        return isElementDisplayed(By.xpath(String.format(dashlet, dashletName.name)));
    }

    public T clickOnHelpIcon(DashletHelpIcon dashlet)
    {
        log.info("Click Help Icon");
        mouseOver(By.cssSelector(String.format(dashletBar, dashlet.name)));
        WebElement helpElement = waitUntilElementIsVisible(
            By.cssSelector(String.format(helpIcon, dashlet.name)));
        clickElement(helpElement);

        return (T) this;
    }

    public T assertBalloonMessageIsDisplayed()
    {
        log.info("Assert balloon message is displayed");
        waitUntilElementIsVisible(helpBallon);
        assertTrue(isElementDisplayed(helpBallon), "Balloon message is not displayed");
        return (T) this;
    }

    public T assertBalloonMessageIsNotDisplayed()
    {
        log.info("Assert balloon message is NOT displayed");
        assertFalse(isElementDisplayed(helpBallon), "Balloon message is displayed");
        return (T) this;
    }

    public T assertDashletHelpIconIsDisplayed(DashletHelpIcon dashletHelpIcon)
    {
        log.info("Assert dashlet help icon is displayed");
        WebElement bar = waitUntilElementIsVisible(By.cssSelector(String.format(dashletBar, dashletHelpIcon.name)));
        mouseOver(bar);
        assertTrue(waitUntilElementIsVisible(
            By.cssSelector(String.format(helpIcon, dashletHelpIcon.name))).isDisplayed(),"Dashlet help icon is not displayed");

        return (T) this;
    }

    public T assertHelpBalloonMessageEquals(String expectedMessage)
    {
        log.info("Assert balloon message equals: {}", expectedMessage);
        assertEquals(getElementText(helpBallonText), expectedMessage,
            String.format("Help balloon message not equals %s ", expectedMessage));

        return (T) this;
    }

    public T closeHelpBalloon()
    {
        log.info("Close help balloon");
        waitUntilElementIsVisible(helpBalloonCloseButton);
        clickElement(helpBalloonCloseButton);
        waitUntilElementDisappears(helpBalloonCloseButton);
        return (T) this;
    }

    public T assertDashletIsExpandable()
    {
        log.info("Assert dashlet is expandable");
        By dashletElement = By.xpath(String.format(resizeDashlet, this.getDashletTitle()));
        waitUntilElementIsVisible(dashletElement);
        assertTrue(isElementDisplayed(dashletElement),
            String.format("Dashlet %s is expandable", this.getDashletTitle()));
        return (T) this;
    }

    public T assertDashletTitleEquals(String expectedTitle)
    {
        log.info("Assert dashlet title equals: {}", expectedTitle);
        assertEquals(getDashletTitle(), expectedTitle,
            String.format("Dashlet title not equals %s ",expectedTitle));

        return (T) this;
    }

    public void resizeDashlet(int height, int scrolldown)
    {
        WebElement resizeDash = waitUntilElementIsVisible(
            By.xpath(String.format(resizeDashlet, this.getDashletTitle())));
        if (scrolldown == 1)
        {
            executeJavaScript("window.scrollBy(0,500)");
        }
        try
        {
            dragAndDrop(resizeDash, 0, height);
        }
        catch (MoveTargetOutOfBoundsException e)
        {
            log.error("Retry resize dashlet");
        }
        dragAndDrop(resizeDash, 0, height);
    }

    public int getDashletHeight()
    {
        WebElement dashletContainer = findElement(
            By.xpath(String.format(dashletContainerLocator, this.getDashletTitle())));
        String val = dashletContainer.getCssValue("height").replace("px", "");
        try
        {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException nf)
        {
            double dblVal = Double.parseDouble(val);
            return (int) dblVal;
        }
    }

    public String getDashletColor()
    {
        String hex = "";
        WebElement dashletContainer = findElement(By.cssSelector(".dashlet .title"));
        String color = dashletContainer.getCssValue("background-color");
        String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
        int number1 = Integer.parseInt(numbers[0]);
        numbers[1] = numbers[1].trim();
        int number2 = Integer.parseInt(numbers[1]);
        numbers[2] = numbers[2].trim();
        int number3 = Integer.parseInt(numbers[2]);
        hex = String.format("#%02x%02x%02x", number1, number2, number3);
        return hex;
    }
}