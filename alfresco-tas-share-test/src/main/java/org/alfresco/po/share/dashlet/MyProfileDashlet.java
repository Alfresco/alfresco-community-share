package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

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
    private final By im = By.xpath("(//div[@class='fieldvalue'])[4]");

    protected String dashletBar = "div[class='%s'] div[class='title']";

    public MyProfileDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    @Override
    public MyProfileDashlet clickOnHelpIcon(DashletHelpIcon dashlet)
    {
        LOG.info("Click Help Icon");
        webElementInteraction.mouseOver(toolbarMyProfile);
        WebElement helpMyProfile = webElementInteraction.waitUntilElementIsVisible(helpIconMyProfile);
        webElementInteraction.mouseOver(helpMyProfile);
        webElementInteraction.clickElement(helpMyProfile);
        return this;
    }

    private WebElement getMyProfileDashletContainer()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer);
    }

    public MyProfileDashlet assertViewFullProfileButtonIsDisplayed()
    {
        WebElement fullProfile = webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(viewFullProfile);
        assertTrue(webElementInteraction.isElementDisplayed(fullProfile), "View full profile is displayed");
        return this;
    }

    public void clickViewFullProfile()
    {
        webElementInteraction.clickElement(getMyProfileDashletContainer().findElement(viewFullProfile));
    }

    public MyProfileDashlet assertAvatarIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(getMyProfileDashletContainer().findElement(avatar)), "Avatar is displayed");
        return this;
    }

    public MyProfileDashlet assertNameIsEnabled()
    {
        assertTrue(getMyProfileDashletContainer().findElement(name).isEnabled(), "Name is enabled");
        return this;
    }

    public void clickOnName()
    {
        webElementInteraction.clickElement(getMyProfileDashletContainer().findElement(name));
    }

    public MyProfileDashlet assertNameIs(String fullName)
    {
        assertEquals(webElementInteraction.getElementText(getMyProfileDashletContainer().findElement(name)), fullName);
        return this;
    }

    public MyProfileDashlet assertJobTitleIs(String job)
    {
        assertEquals(webElementInteraction.getElementText(getMyProfileDashletContainer().findElement(jobTitle)), job);
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
        assertEquals(webElementInteraction.getElementText(getMyProfileDashletContainer().findElement(email)), expectedEmail);
        return this;
    }

    public MyProfileDashlet assertTelephoneIs(String expectedTelephone)
    {
        assertEquals(webElementInteraction.getElementText(getMyProfileDashletContainer().findElement(telephone)), expectedTelephone);
        return this;
    }

    public MyProfileDashlet assertSkypeIs(String expectedSkype)
    {
        assertEquals(webElementInteraction.getElementText(getMyProfileDashletContainer().findElement(skype)), expectedSkype);
        return this;
    }

    public MyProfileDashlet assertIMIs(String expectedIM)
    {
        assertEquals(webElementInteraction.getElementText(getMyProfileDashletContainer().findElement(im)), expectedIM);
        return this;
    }
}
