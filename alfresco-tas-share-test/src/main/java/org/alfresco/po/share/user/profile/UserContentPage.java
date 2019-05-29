package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserContentPage extends SharePage<UserContentPage>
{
    @RenderWebElement
    @FindBy (css = ".viewcolumn>div:nth-child(1)")
    private WebElement recentlyAddedLabel;

    @RenderWebElement
    @FindBy (xpath = ".//*[@id='template_x002e_user-content_x002e_user-content_x0023_default-body']/div/p[1]")
    private WebElement noAddedContentMessage;

    @RenderWebElement
    @FindBy (css = ".viewcolumn>div:nth-child(3)")
    private WebElement recentlyModfiedLabel;

    @RenderWebElement
    @FindBy (xpath = ".//*[@id='template_x002e_user-content_x002e_user-content_x0023_default-body']/div/p[2]")
    private WebElement noModifiedContentMessage;

    public boolean isRecentlyAddedLabelDisplayed()
    {
        return browser.isElementDisplayed(recentlyAddedLabel);
    }

    public boolean isNoAddedContentMessageDisplayed()
    {
        return browser.isElementDisplayed(noAddedContentMessage);
    }

    public boolean isRecentlyModfiedLabelDisplayed()
    {
        return browser.isElementDisplayed(recentlyModfiedLabel);
    }

    public boolean isNoModifiedContentMessageDisplayed()
    {
        return browser.isElementDisplayed(noModifiedContentMessage);
    }

    public String getRecentlyAddedLabelText()
    {
        return recentlyAddedLabel.getText();
    }

    public String getNoAddedContentMessageText()
    {
        return noAddedContentMessage.getText();
    }

    public String getRecentlyModfiedLabelText()
    {
        return recentlyModfiedLabel.getText();
    }

    public String getNoModifiedContentMessage()
    {
        return noModifiedContentMessage.getText();
    }

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-content", getUserName());
    }

    public UserContentPage navigate(String userName)
    {
        setUserName(userName);
        return (UserContentPage) navigate();
    }

    /**
     * Open User Content page from the my profile navigation links
     *
     * @param myProfileNavigation
     * @return {@link UserContentPage}
     */
    public UserContentPage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickContent();
        return (UserContentPage) this.renderedPage();
    }
}
