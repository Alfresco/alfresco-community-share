package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyProfileDashlet extends Dashlet<MyProfileDashlet>
{
    private final By dashletContainer = By.cssSelector("div[class='dashlet']");
    private final By helpIconMyProfile = By.cssSelector("div[class='dashlet'] div[class='titleBarActionIcon help']");
    private final By toolbarMyProfile = By.cssSelector("div[class='dashlet'] .title");
    private final By viewFullProfile = By.cssSelector(".toolbar>div>span>span[class='first-child']>a");
    private final By avatar = By.cssSelector(".photo>img");
    private final By name = By.cssSelector(".namelabel>a");
    private final By jobTitle = By.cssSelector(".titlelabel");
    private final By email = By.cssSelector(".fieldvalue>a");
    private final By telephone = By.xpath("(//div[@class='fieldvalue'])[2]");
    private final By skype = By.xpath("(//div[@class='fieldvalue'])[3]");
    private final By instantMessage = By.xpath("(//div[@class='fieldvalue'])[4]");

    public MyProfileDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return getElementText(waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    @Override
    public MyProfileDashlet clickOnHelpIcon(DashletHelpIcon dashlet)
    {
        log.info("Click Help Icon");
        mouseOver(toolbarMyProfile);
        WebElement helpMyProfile = waitUntilElementIsVisible(helpIconMyProfile);
        mouseOver(helpMyProfile);
        clickElement(helpMyProfile);
        return this;
    }

    private WebElement getMyProfileDashletContainer()
    {
        return waitUntilElementIsVisible(dashletContainer);
    }

    public MyProfileDashlet assertViewFullProfileButtonIsDisplayed()
    {
        WebElement fullProfile = waitUntilElementIsVisible(dashletContainer).findElement(viewFullProfile);
        assertTrue(isElementDisplayed(fullProfile), "View full profile is displayed");
        return this;
    }

    public MyProfileDashlet assertAvatarIsDisplayed()
    {
        assertTrue(isElementDisplayed(getMyProfileDashletContainer().findElement(avatar)), "Avatar is displayed");
        return this;
    }

    public MyProfileDashlet assertNameIsEnabled()
    {
        assertTrue(getMyProfileDashletContainer().findElement(name).isEnabled(), "Name is enabled");
        return this;
    }

    public MyProfileDashlet assertNameIs(String fullName)
    {
        assertEquals(getElementText(getMyProfileDashletContainer().findElement(name)), fullName);
        return this;
    }

    public MyProfileDashlet assertJobTitleIs(String job)
    {
        assertEquals(getElementText(getMyProfileDashletContainer().findElement(jobTitle)), job);
        return this;
    }

    public MyProfileDashlet assertEmailIsEnabled()
    {
        assertTrue(getMyProfileDashletContainer().findElement(email).isEnabled(), "Email is enabled");
        return this;
    }

    public MyProfileDashlet assertEmailHrefIsCorrect(UserModel user)
    {
        assertEquals(getMyProfileDashletContainer().findElement(email).getAttribute("href"),
            String.format("mailto:%s", user.getEmailAddress()));
        return this;
    }

    public MyProfileDashlet assertEmailIs(String expectedEmail)
    {
        assertEquals(getElementText(getMyProfileDashletContainer().findElement(email)), expectedEmail);
        return this;
    }

    public MyProfileDashlet assertTelephoneIs(String expectedTelephone)
    {
        assertEquals(getElementText(getMyProfileDashletContainer().findElement(telephone)), expectedTelephone);
        return this;
    }

    public MyProfileDashlet assertSkypeIs(String expectedSkype)
    {
        assertEquals(getElementText(getMyProfileDashletContainer().findElement(skype)), expectedSkype);
        return this;
    }

    public MyProfileDashlet assertInstantMessagesEqualTo(String expectedInstantMessage)
    {
        assertEquals(getElementText(getMyProfileDashletContainer().findElement(
            instantMessage)), expectedInstantMessage);
        return this;
    }
}
