package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.model.*;
import org.alfresco.utility.web.annotation.PageObject;
import org.testng.Assert;

/**
 * My activities dashlet page object, holds all elements of the HTML page relating to
 * share's my activities dashlet on user dashboard page.
 */
@PageObject
public class MyActivitiesDashlet extends AbstractActivitiesDashlet<MyActivitiesDashlet>
{
    public MyActivitiesDashlet assertAddDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsNotDisplayedFor(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertFalse(browser.getTextFromElementList(activityRows).contains(
            String.format(language.translate("activitiesDashlet.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())));
        return this;
    }

    public MyActivitiesDashlet assertUpdateDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.updateActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertPreviewedDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.previewedActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertDeleteDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.deleteActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertAddedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.createActivity"),
            user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertDeletedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.deleteActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))));
        return this;
    }

    public MyActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel, SiteModel site)
    {
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName(), site.getTitle()))));
        return this;
    }

    public UserProfilePage clickUserFromAddedDocumentActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(userLinkLocator).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public DocumentDetailsPage clickDocumentLinkForAddActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(documentLinkLocator).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public MyActivitiesDashlet assertEmptyDashletMessageIsCorrect()
    {
        Assert.assertEquals(activitiesEmptyList.getText(), language.translate("myactivitiesDashlet.empty"));
        return this;
    }

    public MyActivitiesDashlet assertRssFeedButtonIsDisplayed()
    {
        browser.mouseOver(activitiesDashletTitle);
        browser.mouseOver(myActivitiesButton);
        Assert.assertTrue(browser.isElementDisplayed(rssFeedButton), "Rss Feed button is displayed");
        return this;
    }
}

