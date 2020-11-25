package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author bogdan.bocancea
 */
public class UserContentPage extends SharePage2<UserContentPage>
{
    private String userName;

    @RenderWebElement
    private By recentlyAddedLabel = By.cssSelector(".viewcolumn>div:nth-child(1)");
    private By noAddedContentMessage = By.cssSelector(".profile > div > p:nth-of-type(1)");
    @RenderWebElement
    private By recentlyModfiedLabel = By.cssSelector(".viewcolumn>div:nth-child(3)");
    private By noModifiedContentMessage = By.cssSelector(".profile > div > p:nth-of-type(2)");

    public UserContentPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/user-content", getUserName());
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }


    public UserContentPage navigate(UserModel user)
    {
        setUserName(user.getUsername());
        return navigate();
    }

    public UserContentPage assertRecentlyAddedLabelIsDisplayed()
    {
        LOG.info("Assert Recently Added Label is displayed");
        assertTrue(getBrowser().isElementDisplayed(recentlyAddedLabel), "Recently Added Label is displayed");
        return this;
    }

    public UserContentPage assertNoAddedContentMessageIsDisplayed()
    {
        LOG.info("No content added message is displayed");
        assertTrue(getBrowser().isElementDisplayed(noAddedContentMessage), "No content added message is displayed");
        return this;
    }

    public UserContentPage assertRecentlyModifiedLabelIsDisplayed()
    {
        LOG.info("Assert Recently Modified Label is displayed");
        assertTrue(getBrowser().isElementDisplayed(recentlyModfiedLabel), "Recently Modified Label is displayed");
        return this;
    }

    public UserContentPage assertNoModifiedContentMessageIsDisplayed()
    {
        LOG.info("No modified content message is displayed");
        assertTrue(getBrowser().isElementDisplayed(noAddedContentMessage), "No modified content message is displayed");
        return this;
    }

    public UserContentPage assertNoAddedContentMessageIsCorrect()
    {
        assertEquals(getElementText(noAddedContentMessage), language.translate("userProfileContent.noAddedContentMessage"));
        return this;
    }

    public UserContentPage assertNoModifiedContentMessageIsCorrect()
    {
        assertEquals(getElementText(noModifiedContentMessage), language.translate("userProfileContent.noModifiedContentMessage"));
        return this;
    }
}
