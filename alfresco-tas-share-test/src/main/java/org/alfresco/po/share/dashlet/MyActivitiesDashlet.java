package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyActivitiesDashlet extends AbstractActivitiesDashlet<MyActivitiesDashlet>
{
    private final String CREATE_ACTIVITY = "activitiesDashlet.document.createActivity";

    public MyActivitiesDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        log.info("Assert add document activity is displayed for document {}", file.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate(CREATE_ACTIVITY),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Add document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertAddDocumentActivityIsNotDisplayedForUser(UserModel user, FileModel file, SiteModel site)
    {
        log.info("Assert add document activity for {} is not displayed for user {}",file.getName(), user.getUsername());
        List<WebElement> rows = waitUntilElementsAreVisible(activityRows);
        assertFalse(getTextFromElementList(rows).contains(
            String.format(language.translate(CREATE_ACTIVITY),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle())),
                    String.format("Add document activity is displayed for user %s ", user.getUsername()));
        return this;
    }

    public MyActivitiesDashlet assertUpdateDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        log.info("Assert update document activity is displayed for document {}", file.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.updateActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Update document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertDeleteDocumentActivityIsDisplayed(UserModel user, FileModel file, SiteModel site)
    {
        log.info("Assert delete document activity is displayed for document {}", file.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.document.deleteActivity"),
                user.getFirstName(), user.getLastName(), file.getName(), site.getTitle()))),
                "Delete document activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertAddedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        log.info("Assert add folder activity is displayed for {}", folder.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.createActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))),
                "Add folder activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertDeletedFolderActivityIsDisplayed(UserModel user, FolderModel folder, SiteModel site)
    {
        log.info("Assert delete folder activity is displayed for {}", folder.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.folder.deleteActivity"),
                user.getFirstName(), user.getLastName(), folder.getName(), site.getTitle()))),
                "Delete folder activity is not displayed");
        return this;
    }

    public MyActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel, SiteModel site)
    {
        log.info("Assert create link activity is displayed for content {}", contentModel.getName());
        assertTrue(isElementDisplayed(getActivityRow(
            String.format(language.translate("activitiesDashlet.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName(), site.getTitle()))),
            "Create link activity is not displayed");
        return this;
    }

    public UserProfilePage clickUserFromAddedDocumentActivity(UserModel user, FileModel file, SiteModel site)
    {
        clickElement(getActivityRow(String
            .format(language.translate(CREATE_ACTIVITY), user.getFirstName(), user.getLastName(),
                file.getName(), site.getTitle())).findElement(userLinkLocator));
        return new UserProfilePage(webDriver);
    }

    public MyActivitiesDashlet assertEmptyDashletMessageEquals()
    {
        log.info("Assert my activities dashlet message is correct when there are no activities");
        assertEquals(getElementText(activitiesEmptyList), language.translate("myactivitiesDashlet.empty"),
            "Empty dashlet message is not correct");
        return this;
    }
}

