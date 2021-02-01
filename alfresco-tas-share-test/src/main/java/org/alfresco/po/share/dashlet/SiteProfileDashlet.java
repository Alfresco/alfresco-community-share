package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteProfileDashlet assertSiteWelcomeMessageEquals(String expectedWelcomeMessage)
    {
        LOG.info("Assert welcome message equals: {}", expectedWelcomeMessage);
        assertEquals(webElementInteraction.getElementText(welcomeMessage), expectedWelcomeMessage,
            String.format("Welcome message not equals %s ", expectedWelcomeMessage));

        return this;
    }

    public SiteProfileDashlet assertSiteDescriptionEquals(String expectedSiteDescription)
    {
        LOG.info("Assert site description equals: {}", expectedSiteDescription);
        String actualSiteDescription = getSiteProfileRow(expectedSiteDescription).getText();
        assertEquals(actualSiteDescription, expectedSiteDescription,
                String.format("Site description not equals %s ", expectedSiteDescription));

        return this;
    }

    public SiteProfileDashlet assertSiteManagerEquals(String expectedSiteManagerLabel,String expectedSiteManagerValue)
    {
        LOG.info("Assert site manager equals: {}", expectedSiteManagerValue);
        String actualSiteManager = getSiteProfileRow(
                expectedSiteManagerLabel.concat(expectedSiteManagerValue)).getText();

        assertEquals(actualSiteManager, expectedSiteManagerLabel.concat(expectedSiteManagerValue),
                String.format("Site manager not equals %s ",
                        expectedSiteManagerLabel.concat(expectedSiteManagerValue)));

        return this;
    }

    public UserProfilePage clickSiteManagerLink(String managerLinkName)
    {
        webElementInteraction.clickElement(By.xpath(String.format(managerLinkLocator, managerLinkName)));
        return new UserProfilePage(webDriver);
    }

    public SiteProfileDashlet assertSiteVisibilityEquals(String expectedSiteVisibilityLabel,String expectedSiteVisibilityValue)
    {
        LOG.info("Assert site visibility equals: {}", expectedSiteVisibilityValue);
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
        List<WebElement> siteProfileRows = webElementInteraction.waitUntilElementsAreVisible(siteProfileRowLocator);
        for (WebElement currentRow : siteProfileRows)
        {
            if (currentRow.getText().equalsIgnoreCase(searchedSiteLabel))
            {
                return currentRow;
            }
        }
        return siteProfileRows.get(0);
    }
}
