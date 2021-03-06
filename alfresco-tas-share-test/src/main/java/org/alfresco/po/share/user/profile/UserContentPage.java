package org.alfresco.po.share.user.profile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author bogdan.bocancea
 */
@Slf4j
public class UserContentPage extends SharePage2<UserContentPage>
{
    private String userName;

    private final By recentlyAddedLabel = By.cssSelector(".viewcolumn>div:nth-child(1)");
    private final By noAddedContentMessage = By.cssSelector(".profile > div > p:nth-of-type(1)");
    private final By recentlyModfiedLabel = By.cssSelector(".viewcolumn>div:nth-child(3)");
    private final By noModifiedContentMessage = By.cssSelector(".profile > div > p:nth-of-type(2)");

    public UserContentPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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
        log.info("Assert Recently Added Label is displayed");
        waitUntilElementIsVisible(recentlyAddedLabel);
        assertTrue(isElementDisplayed(recentlyAddedLabel), "Recently Added Label is displayed");
        return this;
    }

    public UserContentPage assertNoAddedContentMessageIsDisplayed()
    {
        log.info("No content added message is displayed");
        waitUntilElementIsVisible(noAddedContentMessage);
        assertTrue(isElementDisplayed(noAddedContentMessage), "No content added message is displayed");
        return this;
    }

    public UserContentPage assertRecentlyModifiedLabelIsDisplayed()
    {
        log.info("Assert Recently Modified Label is displayed");
        waitUntilElementIsVisible(recentlyModfiedLabel);
        assertTrue(isElementDisplayed(recentlyModfiedLabel), "Recently Modified Label is displayed");
        return this;
    }

    public UserContentPage assertNoModifiedContentMessageIsDisplayed()
    {
        log.info("No modified content message is displayed");
        waitUntilElementIsVisible(noAddedContentMessage);
        assertTrue(isElementDisplayed(noAddedContentMessage), "No modified content message is displayed");
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
