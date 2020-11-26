package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class MyProfileDashlet extends Dashlet<MyProfileDashlet>
{
    @RenderWebElement
    @FindBy (css = "div[class='dashlet']")
    protected HtmlElement dashletContainer;

    protected String helpIcon = "div[class='%s'] div[class='titleBarActionIcon help']";
    protected String dashletBar = "div[class='%s'] div[class='title']";

    @Autowired
    private UserProfilePage userProfilePage;

    private By viewFullProfile = By.cssSelector(".toolbar>div>span>span[class='first-child']>a");
    private By avatar = By.cssSelector(".photo>img");
    private By name = By.cssSelector(".namelabel>a");
    private By jobTitle = By.cssSelector(".titlelabel");
    private By email = By.cssSelector(".fieldvalue>a");
    private By telephone = By.xpath("(//div[@class='fieldvalue'])[2]");
    private By skype = By.xpath("(//div[@class='fieldvalue'])[3]");
    private By im = By.xpath("(//div[@class='fieldvalue'])[4]");

    @Override
    public String getDashletTitle()
    {
        browser.waitUntilElementVisible(dashletContainer);
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public MyProfileDashlet assertViewFullProfileButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(dashletContainer, viewFullProfile), "View full profile is displayed");
        return this;
    }

    public UserProfilePage clickViewFullProfile()
    {
        dashletContainer.findElement(viewFullProfile).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public MyProfileDashlet assertAvatarIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(dashletContainer, avatar), "Avatar is displayed");
        return this;
    }

    public MyProfileDashlet assertNameIsEnabled()
    {
        Assert.assertTrue(dashletContainer.findElement(name).isEnabled(), "Name is enabled");
        return this;
    }

    public UserProfilePage clickOnName()
    {
        dashletContainer.findElement(name).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public MyProfileDashlet assertNameIs(String fullName)
    {
        Assert.assertEquals(dashletContainer.findElement(name).getText(), fullName);
        return this;
    }

    public MyProfileDashlet assertJobTitleIs(String job)
    {
        Assert.assertEquals(dashletContainer.findElement(jobTitle).getText(), job);
        return this;
    }

    public MyProfileDashlet assertEmailIsEnabled()
    {
        Assert.assertTrue(dashletContainer.findElement(email).isEnabled(), "Email is enabled");
        return this;
    }

    public MyProfileDashlet assertEmailHrefIsCorrect(UserModel user)
    {
        Assert.assertEquals(dashletContainer.findElement(email).getAttribute("href"),
            String.format("mailto:%s", user.getEmailAddress()));
        return this;
    }

    public String getEmail()
    {
        return dashletContainer.findElement(email).getText();
    }

    public MyProfileDashlet assertEmailIs(String expectedEmail)
    {
        Assert.assertEquals(dashletContainer.findElement(email).getText(), expectedEmail);
        return this;
    }

    public MyProfileDashlet assertTelephoneIs(String expectedTelephone)
    {
        Assert.assertEquals(dashletContainer.findElement(telephone).getText(), expectedTelephone);
        return this;
    }

    public MyProfileDashlet assertSkypeIs(String expectedSkype)
    {
        Assert.assertEquals(dashletContainer.findElement(skype).getText(), expectedSkype);
        return this;
    }

    public MyProfileDashlet assertIMIs(String expectedIM)
    {
        Assert.assertEquals(dashletContainer.findElement(im).getText(), expectedIM);
        return this;
    }
}
