package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteMembersDashlet extends Dashlet<SiteMembersDashlet>
{
    private final String EMPTY_SPACE = " ";

    private final By dashletContainer = By.cssSelector("div[class*='colleagues']");
    private final By addUsersLink = By.cssSelector("a[href='add-users']");
    private final By emptyMembersListMessage = By.cssSelector("div[class*='scrollableList'] div[class*='info'] :first-child");
    private final By allMembersLink = By.cssSelector("a[href='site-members']");
    private final By paginationSection = By.cssSelector("div[class*='colleagues'] div[class*='paginator']");
    private final By membersList = By.cssSelector("div[class='body scrollableList'] div.person");
    private final By memberRole = By.cssSelector("div.person>div");

    private String usernameLocator = "//a[normalize-space()='%s']";
    private String userRoleLocator = "//div[@class='person']/div[normalize-space()='%s']";

    public SiteMembersDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement allMembersButton(String buttonText)
    {
        return webElementInteraction.findElement(By.xpath("//a[text()='" + buttonText + "']"));
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public SiteMembersDashlet assertMembersListMessageEquals(String expectedMembersListMessage)
    {
        log.info("Assert members list message equals: {}", expectedMembersListMessage);
        assertEquals(webElementInteraction.getElementText(emptyMembersListMessage), expectedMembersListMessage,
            String.format("Members list message not equals %s ", expectedMembersListMessage));

        return this;
    }

    public SiteMembersDashlet assertAddUsersLinkTextEquals(String expectedAddUsersLinkText)
    {
        log.info("Assert Add Users link text equals: {}", expectedAddUsersLinkText);
        assertEquals(webElementInteraction.getElementText(addUsersLink), expectedAddUsersLinkText,
            String.format("Add Users link text not equals %s ", expectedAddUsersLinkText));

        return this;
    }

    public SiteMembersDashlet assertAllMembersLinkTextEquals(String expectedAllMembersLinkText)
    {
        log.info("Assert All Members link text equals: {}", expectedAllMembersLinkText);
        assertEquals(webElementInteraction.getElementText(allMembersLink), expectedAllMembersLinkText,
            String.format("All Members link text not equals %s ", expectedAllMembersLinkText));

        return this;
    }

    public SiteMembersDashlet assertUsernameEquals(String expectedFirstName, String expectedLastName)
    {
        log.info("Assert user name equals: {}, {}", expectedFirstName, expectedLastName);
        String actualUsername = formattedActualUsername(expectedFirstName, expectedLastName);
        assertEquals(actualUsername, expectedFirstName.concat(EMPTY_SPACE).concat(expectedLastName));

        return this;
    }

    private String formattedActualUsername(String expectedFirstName, String expectedLastName)
    {
        webElementInteraction.waitWithRetryAndReturnWebElement(By.xpath(String.format(usernameLocator,
            expectedFirstName.concat(EMPTY_SPACE).concat(expectedLastName))), WAIT_1.getValue(), WAIT_60.getValue());

        return webElementInteraction.findElement(By.xpath(String
            .format(usernameLocator, expectedFirstName.concat(EMPTY_SPACE).concat(expectedLastName))))
            .getText();
    }

    public SiteMembersDashlet assertUserRoleEquals(String expectedUserRole)
    {
        log.info("Assert user role equals: {}", expectedUserRole);
        String actualUserRole = webElementInteraction.findElement(By.xpath(String.format(userRoleLocator, expectedUserRole))).getText();
        assertEquals(actualUserRole, expectedUserRole,
            String.format("User role not equals %s ", expectedUserRole));

        return this;
    }

    public SiteMembersDashlet assertPaginationTextEquals(String expectedPaginationText)
    {
        log.info("Assert pagination text equals: {}", expectedPaginationText);
        assertEquals(webElementInteraction.getElementText(paginationSection), expectedPaginationText,
            String.format("Pagination text not equals %s ", expectedPaginationText));

        return this;
    }

    public String getMemberRole(String member)
    {
        return webElementInteraction.findFirstElementWithValue(membersList, member).findElement(memberRole).getText();
    }

    public UserProfilePage clickUser(String username)
    {
        log.info("Navigate to profile page of given user: {}", username);
        webElementInteraction.clickElement(webElementInteraction.findElement(
            By.xpath(String.format(usernameLocator, username))));

        return new UserProfilePage(webDriver);
    }

    public void clickAllMembersButton(String buttonText)
    {
        allMembersButton(buttonText).click();
    }
}
