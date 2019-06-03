package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SiteMembersDashlet extends Dashlet<SiteMembersDashlet>
{
    private final String membersPath = "(.//div[contains(@class, 'colleagues')]//div[contains(@class, 'detail-list-item')])";
    @RenderWebElement
    @FindBy (css = "div[class*='colleagues']")
    protected WebElement dashletContainer;
    @Autowired
    AddSiteUsersPage addSiteUsersPage;
    @Autowired
    UserProfilePage userProfilePage;
    @Autowired
    SiteMembersPage siteMembersPage;
    @FindBy (css = "a[href='add-users']")
    private WebElement addUsersLink;
    @FindBy (xpath = membersPath)
    private List<WebElement> siteMembersList;

    @FindBy (css = "div[class*='scrollableList'] div[class*='info'] :first-child")
    private WebElement emptyMembersListMessage;

    @FindBy (css = "a[href='site-members']")
    private WebElement allMembersLink;

    @FindBy (css = "div[class*='colleagues'] div[class*='paginator']")
    private WebElement paginationSection;

    @FindAll (@FindBy (css = "div[class='body scrollableList'] div.person"))
    private List<WebElement> membersList;

    @FindAll (@FindBy (css = "div[class='body scrollableList'] div.person a"))
    private List<WebElement> memberNameList;

    private By memberRole = By.cssSelector("div.person>div");

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
       /* if (membersList.isEmpty())
            return emptyMembersListMessage.getText();
        else
            return "Site members list isn't empty.";*/
        return emptyMembersListMessage.getText();
    }

    public boolean isAddUsersLinkDisplayed()
    {
        return browser.isElementDisplayed(addUsersLink);
    }

    public String getAddUsersLinkText()
    {
        return addUsersLink.getText();
    }

    public boolean isAllMembersLinkDisplayed()
    {
        return browser.isElementDisplayed(allMembersLink);
    }

    public String getAllMembersLinkText()
    {
        return allMembersLink.getText();
    }

    /**
     * Verifies each member's information is displayed and not empty
     *
     * @param info member's information to be checked: username, role
     */
    public boolean isUserInfoDisplayed(String info)
    {
        int counter = 0;
        String username = "//div[@class='person']//a";
        String role = "//div[@class='person']/div";

        switch (info)
        {
            case "username":
                info = username;
                break;
            case "role":
                info = role;
                break;
            default:
                throw new IllegalArgumentException("Parameter can be 'username' or 'role'. " + info + " is incorrect!");
        }

        for (int i = 0; i < membersList.size(); i++)
        {
            String member = membersPath + "[" + (i + 1) + "]";

            if (browser.findElement(By.xpath(member + info)).isDisplayed())
                if (!browser.findElement(By.xpath(member + info)).getText().isEmpty())
                    counter++;
                else
                    break;
            else
                break;
        }

        return counter == membersList.size();
    }

    public boolean isPaginationDisplayed()
    {
        return browser.isElementDisplayed(paginationSection);
    }

    public String getPaginationText()
    {
        return paginationSection.getText();
    }

    /**
     * Press Add Users button
     */
    public AddSiteUsersPage addSiteUsers()
    {
        addUsersLink.click();
        return (AddSiteUsersPage) addSiteUsersPage.renderedPage();
    }

    public String getMemberRole(String member)
    {
        return browser.findFirstElementWithValue(membersList, member).findElement(memberRole).getText();
    }

    public UserProfilePage selectSiteMember(String userName)
    {
        browser.waitUntilElementsVisible(memberNameList);
        browser.findFirstElementWithValue(memberNameList, userName).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public boolean isMemberNameDisplayed(String userName)
    {
        browser.waitUntilElementsVisible(memberNameList);
        String actualUserName = browser.findFirstElementWithValue(memberNameList, userName).getText();
        return actualUserName.equals(userName);
    }

    public SiteMembersPage clickAllMembersButton(String buttonText)
    {
        allMembersButton(buttonText).click();
        return (SiteMembersPage) siteMembersPage.renderedPage();
    }
}
