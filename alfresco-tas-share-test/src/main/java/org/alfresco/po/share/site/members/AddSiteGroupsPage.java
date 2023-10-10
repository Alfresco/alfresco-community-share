
package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AddSiteGroupsPage extends SiteCommon<AddSiteGroupsPage>
{
    private final String OPEN_PARENTHESIS = " (";
    private final String NEW_LINE = "\n";
    private final int BEGIN_INDEX = 0;

    private final By groupSearchBox = By.cssSelector("input[id*='group-finder']");
    private final By groupSearchButton = By.cssSelector("button[id*='group-search-button']");
    private final By addGroupsButton = By.cssSelector("button[id*='default-add-button']");
    private final By backToSiteGroups = By.cssSelector("span[id*='backTo'] a");
    private final By groupResultsList = By.cssSelector("div[id*='default-results'] tbody[class='yui-dt-data'] tr");
    private final By groupInvitedList = By.cssSelector("div[id*='inviteelist'] tbody[class='yui-dt-data'] tr");
    private final By allRolesFilterOptions = By.cssSelector("li[class*='yuimenuitem']");
    private final By setAllRolesToButton = By.cssSelector("div[id*='invitationBar'] button");
    private final By roleOptions = By.cssSelector("div[id*='inviteelist'] div.yui-menu-button-menu.visible a.yuimenuitemlabel");
    private final By searchResultsText = By.cssSelector("div[id*='default-results'] td[class='yui-dt-empty']");
    private final By addButton = By.cssSelector("button");

    private final String EXPECTED_NO_GROUPS_FOUND_LABEL = "peopleFinder.noGroupsFound";

    public AddSiteGroupsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-groups", getCurrentSiteName());
    }

    public SiteGroupsPage goBackToSiteGroupsPage()
    {
        clickElement(backToSiteGroups);
        return new SiteGroupsPage(webDriver);
    }

    public AddSiteGroupsPage searchGroupByName(String groupName)
    {
        clearAndType(groupSearchBox, groupName);
        clickElement(groupSearchButton);

        return new AddSiteGroupsPage(webDriver);
    }

    private WebElement getGroupFromSearchResults(String group)
    {
        return findFirstElementWithValue(groupResultsList, group);
    }

    private WebElement getGroupFromInvitedList(String groupName)
    {
        return findFirstElementWithValue(groupInvitedList, groupName);
    }

    public AddSiteGroupsPage addGroupWithName(String groupName)
    {
        log.info("Add group with name {}", groupName);
        clickElement(getGroupFromSearchResults(groupName).findElement(addButton));
        return this;
    }

    private List<String> getFilterRolesFromDropdown()
    {
        clickElement(setAllRolesToButton);
        List<String> roles = Collections.synchronizedList(new ArrayList<>());

        for (WebElement allRolesFilterOption : findElements(allRolesFilterOptions))
        {
            roles.add(allRolesFilterOption.getText());
        }
        clickElement(setAllRolesToButton);
        return roles;
    }

    public AddSiteGroupsPage assertNotificationMessageEqualsTo(String expectedMessage)
    {
        log.info("Assert notification message equals {}", expectedMessage);
        assertEquals(waitUntilNotificationMessageDisappears().get(), expectedMessage,
            String.format("Notification message not equals %s ", expectedMessage));
        return this;
    }

    public AddSiteGroupsPage assertGroupNameEquals(String groupName)
    {
        String formattedGroup = getGroupFromSearchResults(groupName).getText()
            .substring(BEGIN_INDEX, getGroupFromSearchResults(groupName).getText().indexOf(NEW_LINE));

        assertEquals(formattedGroup, groupName,
            String.format("Group name not equals %s ", groupName));
        return this;
    }

    public AddSiteGroupsPage assertInvitedGroupNameEqualsTo(String expectedGroupName)
    {
        String formattedGroupName = getGroupFromInvitedList(expectedGroupName).getText()
            .substring(BEGIN_INDEX,
                getGroupFromInvitedList(expectedGroupName).getText().indexOf(OPEN_PARENTHESIS));

        assertEquals(formattedGroupName, expectedGroupName);
        return this;
    }

    public AddSiteGroupsPage assertFilterRolesEqualTo(List<String> expectedFilterRoles)
    {
        log.info("Assert filter roles equal to: {}", expectedFilterRoles);
        List<String> actualFilterRoles = getFilterRolesFromDropdown();

        assertEquals(actualFilterRoles, expectedFilterRoles,
            String.format("Filter drop down roles not equal %s", expectedFilterRoles));
        return this;
    }

    public AddSiteGroupsPage setGroupRole(String groupName, String roleOption)
    {
        try
        {
            clickElement(getGroupFromInvitedList(groupName).findElement(addButton));
            List<WebElement> groupRoleOptions = waitUntilElementsAreVisible(roleOptions);
            selectOptionFromFilterOptionsList(roleOption, groupRoleOptions);
            return this;
        }
        catch (NoSuchElementException nse)
        {
            log.error("Set role option not present {}", nse.getMessage());
            throw new NoSuchElementException(roleOption + " option not present.");
        }
    }

    public AddSiteGroupsPage addGroups()
    {
        waitUntilElementIsVisible(addGroupsButton);
        clickElement(addGroupsButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public AddSiteGroupsPage assertNoGroupsFoundLabelEqualsTo(String expectedLabel)
    {
        waitUntilElementContainsText(searchResultsText, language.translate(EXPECTED_NO_GROUPS_FOUND_LABEL));
        assertEquals(getElementText(searchResultsText), expectedLabel);
        return this;
    }

    public void addGroupWorkflow(String group, String role)
    {
        searchGroupByName(group);
        assertGroupNameEquals(group);

        addGroupWithName(group);
        assertInvitedGroupNameEqualsTo(group);

        setGroupRole(group, role);
        addGroups();
    }
}