package org.alfresco.po.share.dashlet;

import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.testng.Assert;

@PageObject
public class SiteActivitiesDashlet extends AbstractActivitiesDashlet<SiteActivitiesDashlet>
{
    public SiteActivitiesDashlet assertEmptyDashletMessageIsCorrect()
    {
        LOG.info("Assert site activities dashlet message is correct when there are no activities");
        Assert.assertEquals(activitiesEmptyList.getText(), language.translate("siteActivities.empty"));
        return this;
    }

    public SiteActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel)
    {
        LOG.info(String.format("Assert create link activity is displayed for content %s", contentModel.getName()));
        Assert.assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName()))));
        return this;
    }
}
