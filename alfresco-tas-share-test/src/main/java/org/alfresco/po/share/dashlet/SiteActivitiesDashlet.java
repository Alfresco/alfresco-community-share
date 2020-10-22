package org.alfresco.po.share.dashlet;

import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@PageObject
public class SiteActivitiesDashlet extends AbstractActivitiesDashlet<SiteActivitiesDashlet>
{
    public SiteActivitiesDashlet assertEmptyDashletMessageEquals()
    {
        LOG.info("Assert site activities dashlet message is correct when there are no activities");
        assertEquals(activitiesEmptyList.getText(), language.translate("siteActivities.noActivities.message"),
                "Empty dashlet message is not correct");
        return this;
    }

    public SiteActivitiesDashlet assertCreatedLinkActivityIsDisplayed(UserModel user, ContentModel contentModel)
    {
        LOG.info(String.format("Assert create link activity is displayed for content %s", contentModel.getName()));
        assertTrue(browser.isElementDisplayed(getActivityRow(
            String.format(language.translate("siteActivities.link.createActivity"),
                user.getFirstName(), user.getLastName(), contentModel.getName()))),
                "Create link activity is not displayed");
        return this;
    }
}
