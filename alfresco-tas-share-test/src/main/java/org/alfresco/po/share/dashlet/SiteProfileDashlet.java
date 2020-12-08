package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.List;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class SiteProfileDashlet extends Dashlet<SiteProfileDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.site-profile")
    protected HtmlElement dashletContainer;

    @RenderWebElement
    @FindBy (css = ".msg.dashlet-padding>h2")
    private WebElement welcomeMessage;

    private final By siteProfileRowLocator = By.cssSelector("div[class='msg dashlet-padding'] > p");
    private final String managerLinkLocator = "//div[@class='msg dashlet-padding']//a[text()='%s']";

   // @Autowired
    private UserProfilePage userProfilePage;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public SiteProfileDashlet assertSiteWelcomeMessageEquals(String expectedWelcomeMessage)
    {
        LOG.info("Assert welcome message equals: {}", expectedWelcomeMessage);
        assertEquals(welcomeMessage.getText(), expectedWelcomeMessage,
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
        browser.findElement(By.xpath(String.format(managerLinkLocator, managerLinkName))).click();
        return (UserProfilePage) userProfilePage.renderedPage();
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
        List<WebElement> siteProfileRows = browser.findElements(siteProfileRowLocator);

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
