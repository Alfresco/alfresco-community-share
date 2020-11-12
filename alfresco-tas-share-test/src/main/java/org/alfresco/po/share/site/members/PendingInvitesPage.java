package org.alfresco.po.share.site.members;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class PendingInvitesPage extends SiteCommon<PendingInvitesPage>
{
    @RenderWebElement
    protected By searchInput = By.cssSelector("[id*='default-search-text']");
    @RenderWebElement
    protected By searchButton = By.cssSelector("[id*='default-search-button']");
    private By resultsPane = By.className("results");
    private By pendingInvitesList = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private By invitationUserAvatar = By.cssSelector(".avatar");
    private By invitationUserName = By.xpath("(//*[@class='attr-value'])[1]");
    private By invitationSentDate = By.xpath("(//*[@class='attr-value'])[2]");
    private By invitationUserRole = By.xpath("(//*[@class='attr-value'])[3]");
    private By cancelButton = By.cssSelector("button");

    public PendingInvitesPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/pending-invites", getCurrentSiteName());
    }

    private WebElement selectPendingInvitationRow(String firstName)
    {
        return getBrowser().findFirstElementWithValue(pendingInvitesList, firstName);
    }

    public boolean isPendingInvitesListDisplayed()
    {
        return getBrowser().isElementDisplayed(pendingInvitesList);
    }

    public void typeIntoSearchInput(String input)
    {
        clearAndType(getBrowser().findElement(searchInput), input);
    }

    public PendingInvitesPage clickSearchButton()
    {
        getBrowser().findElement(searchButton).click();
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
        return getBrowser().isElementDisplayed(searchInput);
    }

    public boolean isSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton);
    }

    public boolean isCancelButtonDisplayed(String firstName)
    {
        return getBrowser().isElementDisplayed(selectPendingInvitationRow(firstName).findElement(cancelButton));
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

    public ArrayList<String> getPendingRequests()
    {
        ArrayList<String> pendingUsersText = new ArrayList<>();
        List<WebElement> users = getBrowser().findDisplayedElementsFromLocator(By.cssSelector(".to-invitee .attr-value"));
        for (WebElement user : users)
        {
            pendingUsersText.add(user.getText());
        }
        return pendingUsersText;
    }
}
