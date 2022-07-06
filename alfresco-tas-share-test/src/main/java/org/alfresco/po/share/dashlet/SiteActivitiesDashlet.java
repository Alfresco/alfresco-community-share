package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteActivitiesDashlet extends AbstractActivitiesDashlet<SiteActivitiesDashlet>
{
    private final String siteRssFeed = "share/feedservice/components/dashlets/activities/list?format=atomfeed&mode=site&site=%s";

    public SiteActivitiesDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SiteActivitiesDashlet assertEmptyDashletMessageEquals()
    {
        log.info("Assert site activities dashlet message is correct when there are no activities");
        assertEquals(getElementText(activitiesEmptyList), language.translate("siteActivities.noActivities.message"),
            "Empty dashlet message is not correct");

        return this;
    }

    public SiteActivitiesDashlet assertAddDocumentActivityIsDisplayed(UserModel user, FileModel file)
    {
        log.info("Assert add document activity is displayed for document {}", file.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.document.createActivity"),
                    user.getFirstName(), user.getLastName(), file.getName()))),
                "Add document activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertAddDocumentActivityIsNotDisplayedForUser(UserModel user, FileModel file)
    {
        log.info("Assert add document activity for {} is not displayed for user {}", file.getName(), user.getUsername());
        List<WebElement> rows = findElements(activityRows);
        assertFalse(getTextFromElementList(rows).contains(
            String.format(language.translate("siteActivities.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName())),
                    String.format("Add document activity is displayed for user %s", user.getUsername()));
        return this;
    }

    public DocumentDetailsPage clickDocumentLinkForAddActivity(UserModel user, FileModel file)
    {
        log.info("Click document link {} from create document activity", file.getName());
        clickElement(getActivityRow(String
            .format(language.translate("siteActivities.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName()))
            .findElement(documentLinkLocator));

        return new DocumentDetailsPage(webDriver);
    }

    public SiteActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel)
    {
        log.info("Assert create link activity is displayed for content {}}", contentModel.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName()))),
                "Create link activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertAddGroupToSiteWithRoleActivityIsDisplayed(GroupModel groupModel, SiteModel siteModel, UserRole role)
    {
        log.info("Assert add group activity is displayed for group {} with role {}", groupModel.getDisplayName(), role.toString());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.group.addActivity"),
                groupModel.getDisplayName(), siteModel.getTitle(), role.toString().replace("Site", "")))),
                "Add group activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertUpdateGroupRoleActivityIsDisplayed(GroupModel groupModel, UserRole role)
    {
        log.info("Assert update group role activity is displayed for group {} with role {}", groupModel.getDisplayName(), role.toString());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.group.updateRoleActivity"),
                groupModel.getDisplayName(), role.toString().replace("Site", "")))),
            "Update group role activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertRemoveGroupFromSiteActivityIsDisplayed(GroupModel groupModel, SiteModel site)
    {
        log.info("Assert remove group from site activity is displayed for group {}", groupModel.getDisplayName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.group.removeGroupFromSite"),
                groupModel.getDisplayName(), site.getTitle()))), "Remove group activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertRssFeedContainsExpectedUrForSiteActivity(SiteModel siteToCheck)
    {
        assertRssFeedContainsExpectedUrl(String.format(siteRssFeed, siteToCheck.getId()));
        return this;
    }
}
