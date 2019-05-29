package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SiteMembersDashlet extends Dashlet<SiteMembersDashlet>
{
    @Autowired
    AddSiteUsersPage addSiteUsersPage;

    @RenderWebElement
    @FindBy (css = "div[class*='colleagues']")
    protected WebElement dashletContainer;

    @FindBy (css = "a[href='add-users']")
    private WebElement addUsersLink;

    private final String membersPath = "(.//div[contains(@class, 'colleagues')]//div[contains(@class, 'detail-list-item')])";
    @FindBy (xpath = membersPath)
    private List<WebElement> membersList;

    @FindBy (css = "div[class*='scrollableList'] div[class*='info'] :first-child")
    private WebElement emptyMembersListMessage;

    @FindBy (css = "a[href='site-members']")
    private WebElement allMembersLink;

    @FindBy (css = "div[class*='colleagues'] div[class*='paginator']")
    private WebElement paginationSection;

    private By memberRole = By.cssSelector("div.person>div");

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
}
