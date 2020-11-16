package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SiteMembersDashlet extends Dashlet<SiteMembersDashlet>
{
    private static final String EMPTY_SPACE = " ";

    @RenderWebElement
    @FindBy (css = "div[class*='colleagues']")
    protected WebElement dashletContainer;

    //@Autowired
    AddSiteUsersPage addSiteUsersPage;
    //@Autowired
    UserProfilePage userProfilePage;
    //@Autowired
    SiteMembersPage siteMembersPage;

    @FindBy (css = "a[href='add-users']")
    private WebElement addUsersLink;

    @FindBy (css = "div[class*='scrollableList'] div[class*='info'] :first-child")
    private WebElement emptyMembersListMessage;

    @FindBy (css = "a[href='site-members']")
    private WebElement allMembersLink;

    @FindBy (css = "div[class*='colleagues'] div[class*='paginator']")
    private WebElement paginationSection;

    @FindAll (@FindBy (css = "div[class='body scrollableList'] div.person"))
    private List<WebElement> membersList;

    private By memberRole = By.cssSelector("div.person>div");
    private String usernameLocator = "//a[normalize-space()='%s']";
    private String userRoleLocator = "//div[@class='person']/div[normalize-space()='%s']";

    private WebElement allMembersButton(String buttonText)
    {
        return browser.findElement(By.xpath("//a[text()='" + buttonText + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public String getEmptyMembersListMessage()
    {
        return emptyMembersListMessage.getText();
    }

    public SiteMembersDashlet assertMembersListMessageEquals(String expectedMembersListMessage)
    {
        LOG.info("Assert members list message equals: {}", expectedMembersListMessage);
        assertEquals(emptyMembersListMessage.getText(), expectedMembersListMessage,
            String.format("Members list message not equals %s ", expectedMembersListMessage));

        return this;
    }

    public boolean isAddUsersLinkDisplayed()
    {
        return browser.isElementDisplayed(addUsersLink);
    }

    public SiteMembersDashlet assertAddUsersLinkTextEquals(String expectedAddUsersLinkText)
    {
        LOG.info("Assert Add Users link text equals: {}", expectedAddUsersLinkText);
        assertEquals(addUsersLink.getText(), expectedAddUsersLinkText,
            String.format("Add Users link text not equals %s ", expectedAddUsersLinkText));

        return this;
    }

    public SiteMembersDashlet assertAllMembersLinkTextEquals(String expectedAllMembersLinkText)
    {
        LOG.info("Assert All Members link text equals: {}", expectedAllMembersLinkText);
        assertEquals(allMembersLink.getText(), expectedAllMembersLinkText,
            String.format("All Members link text not equals %s ", expectedAllMembersLinkText));

        return this;
    }

    public SiteMembersDashlet assertUsernameEquals(String expectedFirstName, String expectedLastName)
    {
        LOG.info("Assert user name equals: {}, {}", expectedFirstName, expectedLastName);
        String actualUsername = formattedActualUsername(expectedFirstName, expectedLastName);
        assertEquals(actualUsername, expectedFirstName.concat(EMPTY_SPACE).concat(expectedLastName));

        return this;
    }

    private String formattedActualUsername(String expectedFirstName, String expectedLastName)
    {
        browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(usernameLocator,
            expectedFirstName.concat(EMPTY_SPACE).concat(expectedLastName))), WAIT_1, RETRY_TIMES);

        return browser.findElement(By.xpath(String
            .format(usernameLocator, expectedFirstName.concat(EMPTY_SPACE).concat(expectedLastName))))
            .getText();
    }

    public SiteMembersDashlet assertUserRoleEquals(String expectedUserRole)
    {
        LOG.info("Assert user role equals: {}", expectedUserRole);
        String actualUserRole = browser.findElement(By.xpath(String.format(userRoleLocator, expectedUserRole))).getText();
        assertEquals(actualUserRole, expectedUserRole,
            String.format("User role not equals %s ", expectedUserRole));

        return this;
    }

    public SiteMembersDashlet assertPaginationTextEquals(String expectedPaginationText)
    {
        LOG.info("Assert pagination text equals: {}", expectedPaginationText);
        assertEquals(paginationSection.getText(), expectedPaginationText,
            String.format("Pagination text not equals %s ", expectedPaginationText));

        return this;
    }

    public String getMemberRole(String member)
    {
        return browser.findFirstElementWithValue(membersList, member).findElement(memberRole).getText();
    }

    public UserProfilePage navigateToProfilePageOfGivenUser(String username)
    {
        LOG.info("Navigate to profile page of given user: {}", username);
        browser.findElement(By.xpath(String.format(usernameLocator, username))).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public SiteMembersPage clickAllMembersButton(String buttonText)
    {
        allMembersButton(buttonText).click();
        return (SiteMembersPage) siteMembersPage.renderedPage();
    }
}
