package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.model.*;
import org.alfresco.utility.web.annotation.PageObject;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@PageObject
public class SiteActivitiesDashlet extends AbstractActivitiesDashlet<SiteActivitiesDashlet>
{
    private static final String siteRssFeed = "share/feedservice/components/dashlets/activities/list?format=atomfeed&mode=site&site=%s";

    public SiteActivitiesDashlet assertEmptyDashletMessageEquals()
    {
        LOG.info("Assert site activities dashlet message is correct when there are no activities");
        assertEquals(activitiesEmptyList.getText(), language.translate("siteActivities.noActivities.message"),
                "Empty dashlet message is not correct");

        return this;
    }

    public SiteActivitiesDashlet assertAddDocumentActivityIsDisplayed(UserModel user, FileModel file)
    {
        LOG.info("Assert add document activity is displayed for document {}", file.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.document.createActivity"),
                    user.getFirstName(), user.getLastName(), file.getName()))),
                "Add document activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertAddDocumentActivityIsNotDisplayedForUser(UserModel user, FileModel file)
    {
        LOG.info("Assert add document activity for {} is not displayed for user {}", file.getName(), user.getUsername());
        assertFalse(browser.getTextFromElementList(activityRows).contains(
            String.format(language.translate("siteActivities.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName())),
                    String.format("Add document activity is displayed for user %s", user.getUsername()));
        return this;
    }

    public DocumentDetailsPage clickDocumentLinkForAddActivity(UserModel user, FileModel file)
    {
        LOG.info("Click document link {} from create document activity", file.getName());
        getActivityRow(String.format(language.translate("siteActivities.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName())).findElement(documentLinkLocator).click();

        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public SiteActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel)
    {
        LOG.info("Assert create link activity is displayed for content {}}", contentModel.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName()))),
                "Create link activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertAddGroupToSiteWithRoleActivityIsDisplayed(GroupModel groupModel, SiteModel siteModel, UserRole role)
    {
        LOG.info("Assert add group activity is displayed for group {} with role {}", groupModel.getDisplayName(), role.toString());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.group.addActivity"),
                groupModel.getDisplayName(), siteModel.getTitle(), role.toString().replace("Site", "")))),
                "Add group activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertUpdateGroupRoleActivityIsDisplayed(GroupModel groupModel, UserRole role)
    {
        LOG.info("Assert update group role activity is displayed for group {} with role {}", groupModel.getDisplayName(), role.toString());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.group.updateRoleActivity"),
                groupModel.getDisplayName(), role.toString().replace("Site", "")))),
            "Update group role activity is not displayed");

        return this;
    }

    public SiteActivitiesDashlet assertRemoveGroupFromSiteActivityIsDisplayed(GroupModel groupModel, SiteModel site)
    {
        LOG.info("Assert remove group from site activity is displayed for group {}", groupModel.getDisplayName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
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
