package org.alfresco.po.share.site.members;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PendingInvitesPage extends SiteCommon<PendingInvitesPage>
{
    @RenderWebElement
    protected By searchInput = By.cssSelector("[id*='default-search-text']");
    @RenderWebElement
    private final By searchButton = By.cssSelector("[id*='default-search-button']");
    private final By pendingInvitesList = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private final By invitationUserAvatar = By.cssSelector(".avatar");
    private final By invitationUserName = By.xpath("(//*[@class='attr-value'])[1]");
    private final By invitationSentDate = By.xpath("(//*[@class='attr-value'])[2]");
    private final By invitationUserRole = By.xpath("(//*[@class='attr-value'])[3]");
    private final By cancelButton = By.cssSelector("button");

    public PendingInvitesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/pending-invites", getCurrentSiteName());
    }

    private WebElement selectPendingInvitationRow(String firstName)
    {
        return webElementInteraction.findFirstElementWithValue(pendingInvitesList, firstName);
    }

    public boolean isPendingInvitesListDisplayed()
    {
        return webElementInteraction.isElementDisplayed(pendingInvitesList);
    }

    public void typeIntoSearchInput(String input)
    {
        webElementInteraction.clearAndType(webElementInteraction.findElement(searchInput), input);
    }

    public PendingInvitesPage clickSearchButton()
    {
        webElementInteraction.findElement(searchButton).click();
        return new PendingInvitesPage(webDriver);
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
        return webElementInteraction.isElementDisplayed(searchInput);
    }

    public boolean isSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchButton);
    }

    public boolean isCancelButtonDisplayed(String firstName)
    {
        return webElementInteraction.isElementDisplayed(selectPendingInvitationRow(firstName).findElement(cancelButton));
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
        List<WebElement> users = webElementInteraction.findDisplayedElementsFromLocator(By.cssSelector(".to-invitee .attr-value"));
        for (WebElement user : users)
        {
            pendingUsersText.add(user.getText());
        }
        return pendingUsersText;
    }
}
