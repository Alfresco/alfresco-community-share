package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserContentPage extends SharePage<UserContentPage>
{
    @RenderWebElement
    @FindBy (css = ".viewcolumn>div:nth-child(1)")
    private WebElement recentlyAddedLabel;

    @FindBy (css = ".profile > div > p:nth-of-type(1)")
    private WebElement noAddedContentMessage;

    @RenderWebElement
    @FindBy (css = ".viewcolumn>div:nth-child(3)")
    private WebElement recentlyModfiedLabel;

    @FindBy (css = ".profile > div > p:nth-of-type(2)")
    private WebElement noModifiedContentMessage;

    public UserContentPage assertRecentlyAddedLabelIsDisplayed()
    {
        LOG.info("Assert Recently Added Label is displayed");
        Assert.assertTrue(browser.isElementDisplayed(recentlyAddedLabel), "Recently Added Label is displayed");
        return this;
    }

    public UserContentPage assertNoAddedContentMessageIsDisplayed()
    {
        LOG.info("No content added message is displayed");
        Assert.assertTrue(browser.isElementDisplayed(noAddedContentMessage), "No content added message is displayed");
        return this;
    }

    public UserContentPage assertRecentlyModifiedLabelIsDisplayed()
    {
        LOG.info("Assert Recently Modified Label is displayed");
        Assert.assertTrue(browser.isElementDisplayed(recentlyModfiedLabel), "Recently Modified Label is displayed");
        return this;
    }

    public UserContentPage assertNoModifiedContentMessageIsDisplayed()
    {
        LOG.info("No modified content message is displayed");
        Assert.assertTrue(browser.isElementDisplayed(noAddedContentMessage), "No modified content message is displayed");
        return this;
    }

    public UserContentPage assertNoAddedContentMessageIsCorrect()
    {
        Assert.assertEquals(noAddedContentMessage.getText(), language.translate("userProfileContent.noAddedContentMessage"));
        return this;
    }

    public UserContentPage assertNoModifiedContentMessageIsCorrect()
    {
        Assert.assertEquals(noModifiedContentMessage.getText(), language.translate("userProfileContent.noModifiedContentMessage"));
        return this;
    }

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-content", getUserName());
    }

    public UserContentPage navigate(UserModel user)
    {
        setUserName(user.getUsername());
        return navigate();
    }
}
