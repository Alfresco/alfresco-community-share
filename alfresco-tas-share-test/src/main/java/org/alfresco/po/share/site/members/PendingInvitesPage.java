package org.alfresco.po.share.site.members;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class PendingInvitesPage extends SiteCommon<PendingInvitesPage>
{
    @RenderWebElement
    @FindBy (css = "[id*='default-search-text']")
    protected WebElement searchInput;

    @RenderWebElement
    @FindBy (css = "[id*='default-search-button']")
    protected WebElement searchButton;

    @RenderWebElement
    @FindBy (className = "results")
    private WebElement resultsPane;

    private By pendingInvitesList = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private By invitationUserAvatar = By.cssSelector(".avatar");
    private By invitationUserName = By.xpath("(//*[@class='attr-value'])[1]");
    private By invitationSentDate = By.xpath("(//*[@class='attr-value'])[2]");
    private By invitationUserRole = By.xpath("(//*[@class='attr-value'])[3]");
    private By cancelButton = By.cssSelector("button");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/pending-invites", getCurrentSiteName());
    }

    private WebElement selectPendingInvitationRow(String firstName)
    {
        return browser.findFirstElementWithValue(pendingInvitesList, firstName);
    }

    public boolean isPendingInvitesListDisplayed()
    {
        return browser.isElementDisplayed(pendingInvitesList);
    }

    public void typeIntoSearchInput(String input)
    {
        searchInput.clear();
        searchInput.sendKeys(input);
    }

    public PendingInvitesPage clickSearchButton()
    {
        searchButton.click();
        browser.waitInSeconds(3);
        return (PendingInvitesPage) this.renderedPage();
    }

    public boolean isPendingInvitationListed(String firstName)
    {
        WebElement pendingInvitation = selectPendingInvitationRow(firstName);
        String pendingInvitationText;
        if (pendingInvitation != null)
        {
            pendingInvitationText = pendingInvitation.getText();
            return pendingInvitationText.contains(firstName);
        }
        return false;
    }

    public boolean isSearchInputDisplayed()
    {
        return browser.isElementDisplayed(searchInput);
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }

    public boolean isCancelButtonDisplayed(String firstName)
    {
        return browser.isElementDisplayed(selectPendingInvitationRow(firstName).findElement(cancelButton));
    }

    public String getInvitationAvatarSource(String firstName)
    {
        return selectPendingInvitationRow(firstName).findElement(invitationUserAvatar).getAttribute("src");
    }

    public String getInvitationUserName(String firstName)
    {
        return selectPendingInvitationRow(firstName).findElement(invitationUserName).getText();
    }

    public String getInvitationSentDate(String firstName)
    {
        return selectPendingInvitationRow(firstName).findElement(invitationSentDate).getText();
    }

    public String getInvitationUserRole(String firstName)
    {
        return selectPendingInvitationRow(firstName).findElement(invitationUserRole).getText();
    }

    /**
     * @return list of users from 'Pending Requests' section
     */
    public ArrayList<String> getPendingRequests()
    {
        ArrayList<String> pendingUsersText = new ArrayList<>();
        List<WebElement> users = browser.findDisplayedElementsFromLocator(By.cssSelector(".to-invitee .attr-value"));
        for (WebElement user : users)
        {
            pendingUsersText.add(user.getText());
        }
        return pendingUsersText;
    }
}
