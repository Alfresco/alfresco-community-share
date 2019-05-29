package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class MyProfileDashlet extends Dashlet<MyProfileDashlet>
{
    @Autowired
    UserProfilePage userProfilePage;

    @RenderWebElement
    @FindBy (css = "div[class='dashlet']")
    protected HtmlElement dashletContainer;

    private By viewFullProfile = By.cssSelector(".toolbar>div>span>span[class='first-child']>a");
    private By avatar = By.cssSelector(".photo>img");
    private By name = By.cssSelector(".namelabel>a");
    private By jobTitle = By.cssSelector(".titlelabel");
    private By email = By.cssSelector(".fieldvalue>a");
    private By telephone = By.xpath("(//div[@class='fieldvalue'])[2]");
    private By skype = By.xpath("(//div[@class='fieldvalue'])[3]");
    private By im = By.xpath("(//div[@class='fieldvalue'])[4]");

    protected String helpIcon = "div[class='%s'] div[class='titleBarActionIcon help']";
    protected String dashletBar = "div[class='%s'] div[class='title']";

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    @Override
    public boolean isHelpIconDisplayed(DashletHelpIcon dashlet)
    {
        browser.mouseOver(browser.findElement(By.cssSelector(String.format(dashletBar, dashlet.name))));
        WebElement helpIconElement = browser.findFirstDisplayedElement(By.cssSelector(String.format(helpIcon, dashlet.name)));
        return browser.isElementDisplayed(helpIconElement);
    }

    @Override
    public void clickOnHelpIcon(DashletHelpIcon dashlet)
    {
        browser.findElement(By.cssSelector(String.format(helpIcon, dashlet.name))).click();
    }

    public boolean isViewFullProfileDisplayed()
    {
        return dashletContainer.findElement(viewFullProfile).isDisplayed();
    }

    public UserProfilePage clickOnViewFullProfile()
    {
        dashletContainer.findElement(viewFullProfile).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public boolean isAvatarDisplayed()
    {
        return dashletContainer.findElement(avatar).isDisplayed();
    }

    public boolean isNameEnabled()
    {
        return dashletContainer.findElement(name).isEnabled();
    }

    public UserProfilePage clickOnName()
    {
        dashletContainer.findElement(name).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public String getName()
    {
        return dashletContainer.findElement(name).getText();
    }

    public String getJobTitle()
    {
        return dashletContainer.findElement(jobTitle).getText();
    }

    public boolean isEmailEnabled()
    {
        return dashletContainer.findElement(email).isEnabled();
    }

    public String getEmailHREF()
    {
        return dashletContainer.findElement(email).getAttribute("href");
    }

    public String getEmail()
    {
        return dashletContainer.findElement(email).getText();
    }

    public String getTelephone()
    {
        return dashletContainer.findElement(telephone).getText();
    }

    public String getSkype()
    {
        return dashletContainer.findElement(skype).getText();
    }

    public String getIM()
    {
        return dashletContainer.findElement(im).getText();
    }
}
