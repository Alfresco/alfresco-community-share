package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class SiteProfileDashlet extends Dashlet<SiteProfileDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.site-profile");
    private final By welcomeMessage = By.cssSelector(".msg.dashlet-padding>h2");
    private final By siteProfileRowLocator = By.cssSelector("div[class='msg dashlet-padding'] > p");

    private final String managerLinkLocator = "//div[@class='msg dashlet-padding']//a[text()='%s']";

    public SiteProfileDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteProfileDashlet assertSiteWelcomeMessageEquals(String expectedWelcomeMessage)
    {
        log.info("Assert welcome message equals: {}", expectedWelcomeMessage);
        assertEquals(getElementText(welcomeMessage), expectedWelcomeMessage,
            String.format("Welcome message not equals %s ", expectedWelcomeMessage));

        return this;
    }

    public SiteProfileDashlet assertSiteDescriptionEquals(String expectedSiteDescription)
    {
        log.info("Assert site description equals: {}", expectedSiteDescription);
        String actualSiteDescription = getSiteProfileRow(expectedSiteDescription).getText();
        assertEquals(actualSiteDescription, expectedSiteDescription,
                String.format("Site description not equals %s ", expectedSiteDescription));

        return this;
    }

    public SiteProfileDashlet assertSiteManagerEquals(String expectedSiteManagerLabel,String expectedSiteManagerValue)
    {
        log.info("Assert site manager equals: {}", expectedSiteManagerValue);
        String actualSiteManager = getSiteProfileRow(
                expectedSiteManagerLabel.concat(expectedSiteManagerValue)).getText();

        assertEquals(actualSiteManager, expectedSiteManagerLabel.concat(expectedSiteManagerValue),
                String.format("Site manager not equals %s ",
                        expectedSiteManagerLabel.concat(expectedSiteManagerValue)));

        return this;
    }

    public UserProfilePage clickSiteManagerLink(String managerLinkName)
    {
        clickElement(By.xpath(String.format(managerLinkLocator, managerLinkName)));
        return new UserProfilePage(webDriver);
    }

    public SiteProfileDashlet assertSiteVisibilityEquals(String expectedSiteVisibilityLabel,String expectedSiteVisibilityValue)
    {
        log.info("Assert site visibility equals: {}", expectedSiteVisibilityValue);
        String actualSiteManager = getSiteProfileRow(
                expectedSiteVisibilityLabel.concat(expectedSiteVisibilityValue)).getText();

        assertEquals(actualSiteManager.toLowerCase(),
                expectedSiteVisibilityLabel.concat(expectedSiteVisibilityValue).toLowerCase(),
                String.format("Site manager not equals %s ",
                        expectedSiteVisibilityLabel.concat(expectedSiteVisibilityValue)));

        return this;
    }

    private WebElement getSiteProfileRow(String searchedSiteLabel)
    {
        List<WebElement> siteProfileRows = waitUntilElementsAreVisible(siteProfileRowLocator);
        for (WebElement currentRow : siteProfileRows)
        {
            if (currentRow.getText().equalsIgnoreCase(searchedSiteLabel))
            {
                return currentRow;
            }
        }
        return siteProfileRows.get(0);
    }

    public SiteProfileDashlet assert_SiteVisibilityEquals(String expectedSiteVisibilityLabel,String expectedSiteVisibilityValue, int getRow)
    {
        log.info("Assert site visibility equals: {}", expectedSiteVisibilityValue);
        String actualSiteManager = get_SiteProfileRow(
            expectedSiteVisibilityLabel.concat(expectedSiteVisibilityValue),getRow).getText();

        assertEquals(actualSiteManager.toLowerCase(),
            expectedSiteVisibilityLabel.concat(expectedSiteVisibilityValue).toLowerCase(),
            String.format("Site manager not equals %s ",
                expectedSiteVisibilityLabel.concat(expectedSiteVisibilityValue)));

        return this;
    }

    public SiteProfileDashlet assert_SiteManagerEquals(String expectedSiteManagerLabel,String expectedSiteManagerValue)
    {
        log.info("Assert site manager equals: {}", expectedSiteManagerValue);
        String actualSiteManager = get_SiteProfileRow(
            expectedSiteManagerLabel.concat(expectedSiteManagerValue),1).getText();
        assertEquals(actualSiteManager, expectedSiteManagerLabel.concat(expectedSiteManagerValue),
            String.format("Site manager not equals %s ",
                expectedSiteManagerLabel.concat(expectedSiteManagerValue)));
        return this;
    }

    private WebElement get_SiteProfileRow(String searchedSiteLabel, int rowNumber)
    {
        List<WebElement> siteProfileRows = waitUntilElementsAreVisible(siteProfileRowLocator);
        for (WebElement currentRow : siteProfileRows)
        {
            if (currentRow.getText().equalsIgnoreCase(searchedSiteLabel))
            {
                return currentRow;
            }
        }
        return siteProfileRows.get(rowNumber);
    }
}
