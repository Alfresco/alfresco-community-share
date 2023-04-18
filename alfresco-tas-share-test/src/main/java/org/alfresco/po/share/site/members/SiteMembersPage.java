package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteMembersPage extends SiteCommon<SiteMembersPage>
{
    private final int BEGIN_INDEX = 0;
    private final String EMPTY_SPACE = " ";

    private final By listNameLocator = By.cssSelector("td+td>div.yui-dt-liner>h3");
    private final By siteUsers = By.cssSelector("a[id*='site-members-link']");
    private final By siteGroups = By.cssSelector("a[id*='site-groups-link']");
    private final By siteMemberRow = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private final By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private final By currentRoleButton = By.cssSelector("td[class*='role'] button");
    private final By removeButton = By.cssSelector(".uninvite button");
    private final By currentRole = By.cssSelector("td[class*='role'] div :first-child");

    private final String memberRow = "//a[text()='%s']/../../../..";

    public SiteMembersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/site-members", getCurrentSiteName());
    }

    public SiteMembersPage assertSelectedRoleEqualsTo(String expectedRole, String memberName)
    {
        String currentRoleText = getElementText(getMemberName(memberName).findElement(currentRole));
        String actualRole = getFormattedRole(currentRoleText);

        assertEquals(actualRole, expectedRole,
            String.format("Selected roles not equals with expected %s ", expectedRole));

        return this;
    }

    private String getFormattedRole(String actualRole)
    {
        if(actualRole.contains(EMPTY_SPACE))
        {
            actualRole = actualRole.substring(BEGIN_INDEX, actualRole.indexOf(EMPTY_SPACE));
        }
        return actualRole;
    }

    public String getRole(String name)
    {
        return getMemberName(name).findElement(currentRole).getText();
    }

    public SiteMembersPage assertSiteMemberRemoveButtonIsEnabled(String name)
    {
        assertTrue(getMemberName(name).findElement(removeButton).isEnabled(), "Remove button is not enabled");
        return this;
    }

    public SiteGroupsPage openSiteGroupsPage()
    {
        clickElement(siteGroups);
        return new SiteGroupsPage(webDriver);
    }

    public SiteUsersPage openSiteUsersPage()
    {
        clickElement(siteUsers);
        return new SiteUsersPage(webDriver);
    }

    private List<String> getSiteMembersList()
    {
        waitUntilElementsAreVisible(listNameLocator);
        List<String> names = Collections.synchronizedList(new ArrayList<>());
        for (WebElement listName : findElements(listNameLocator))
        {
            names.add(listName.getText());
        }
        return names;
    }

    public SiteMembersPage assertSiteMemberNameEqualsTo(String expectedSiteMemberName)
    {
        log.info("Assert site member name equals to: {}", expectedSiteMemberName);
        assertTrue(getSiteMembersList().stream().anyMatch(member -> member.equals(expectedSiteMemberName)),
            String.format("Site member name not equals %s ", expectedSiteMemberName));
        return this;
    }

    public boolean assertSiteMemberNameNotDisplayed(String expectedSiteMemberName)
    {
        log.info("Assert site member name equals to: {}", expectedSiteMemberName);
        assertFalse(getSiteMembersList().contains(expectedSiteMemberName), "Site name is Displayed");
        return false;
    }

    public SiteMembersPage assertSiteMemberNameIsNotDisplayed(String memberName)
    {
        log.info("Assert Site member name is not displayed {}", memberName);
        By member = getMemberRow(memberName);
        assertFalse(isElementDisplayed(member));
        return this;
    }

    private By getMemberRow(String memberName)
    {
        return By.xpath(String.format(memberRow, memberName));
    }

    public WebElement getMemberName(String name)
    {
        return findFirstElementWithValue(siteMemberRow, name);
    }

    public SiteMembersPage changeRoleForMember(String newRole, String userName)
    {
        clickElement(getMemberName(userName).findElement(currentRoleButton));
        waitUntilElementsAreVisible(dropDownOptionsList);
        selectOptionFromFilterOptionsList(newRole, findElements(dropDownOptionsList));
        waitUntilNotificationMessageDisappears();

        return this;
    }
}