package org.alfresco.po.share.site.members;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class PendingInvitesPage extends SiteCommon<PendingInvitesPage>
{
    private final By searchButton = By.cssSelector("[id*='default-search-button']");
    private final By pendingInvitesList = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private final By cancelButton = By.cssSelector("button");

    private final String managePendingRequestsRow = "//div[@class='to-invitee']//a[text()='%s']/../../../../..";

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
        return findFirstElementWithValue(pendingInvitesList, firstName);
    }

    public PendingInvitesPage clickSearchButton()
    {
        findElement(searchButton).click();
        return new PendingInvitesPage(webDriver);
    }

    public boolean isSearchButtonDisplayed()
    {
        return isElementDisplayed(searchButton);
    }

    public boolean isCancelButtonDisplayed(String firstName)
    {
        return isElementDisplayed(selectPendingInvitationRow(firstName).findElement(cancelButton));
    }

    private By getPendingRequestRow(String username)
    {
        return By.xpath(String.format(managePendingRequestsRow, username));
    }

    public PendingInvitesPage assertUserHasNoPendingRequest(String username)
    {
        log.info("Assert user {} has no pending request", username);
        By usernameRow = getPendingRequestRow(username);
        assertFalse(isElementDisplayed(usernameRow), String.format("User %s has pending request", username));
        return this;
    }

    public PendingInvitesPage assertUserHasPendingRequest(String username)
    {
        log.info("Assert user has pending request");
        By usernameRow = getPendingRequestRow(username);
        try
        {
            waitUntilElementIsVisible(usernameRow);
        }
        catch (TimeoutException e)
        {
            log.warn("Retry get pending request for user {}", username);
            waitUntilElementIsVisible(usernameRow);
        }
        assertTrue(isElementDisplayed(usernameRow), String.format("User %s has not any pending request", username));

        return this;
    }
}
