package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
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
        LOG.info("Assert add document activity is displayed for document {}", file.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Add document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsNotDisplayedForUser(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert add document activity for {} is not displayed for user {}",file.getName(), user.getUsername());
        Assert.assertFalse(browser.getTextFromElementList(activityRows).contains(
            String.format(language.translate("activitiesDashlet.document.createActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())),
                String.format("Add document activity is displayed for user", user.getUsername()));
        return this;
    }

    public MyActivitiesDashlet assertUpdateDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert update document activity is displayed for document {}", file.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.updateActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Update document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertPreviewedDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert preview document activity is displayed for document {}", file.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.previewedActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Preview document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertDeleteDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        LOG.info("Assert delete document activity is displayed for document {}", file.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.deleteActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Delete document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertAddedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        LOG.info("Assert add folder activity is displayed for {}", folder.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.createActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))),
                "Add folder activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertDeletedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        LOG.info("Assert delete folder activity is displayed for {}", folder.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.deleteActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))),
                "Delete folder activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel, SiteModel site)
    {
        LOG.info("Assert create link activity is displayed for content {}", contentModel.getName());
        assertTrue(browser.isElementDisplayed(getActivityRow(String.format(language.translate("activitiesDashlet.link.createActivity"),
            user.getFirstName(), user.getLastName(), contentModel.getName(), site.getTitle()))),
                "Create link activity is not displayed");
        return this;
    }

    public void clickUserFromAddedDocumentActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(userLinkLocator).click();
    }

    public void clickDocumentLinkForAddActivity(UserModel user, FileModel file, SiteModel site)
    {
        getActivityRow(String.format(language.translate("activitiesDashlet.document.createActivity"),
            user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())).findElement(documentLinkLocator).click();
    }

    public MyActivitiesDashlet assertEmptyDashletMessageEquals()
    {
        LOG.info("Assert my activities dashlet message is correct when there are no activities");
        assertEquals(activitiesEmptyList.getText(), language.translate("myactivitiesDashlet.empty"),
            "Empty dashlet message is not correct");
        return this;
    }
}

