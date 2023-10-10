package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ActivitiesFeedPage extends AdminConsolePage<ActivitiesFeedPage>
{
    public static final String ACTIVITIES_FEED_OBJECT = "Alfresco:Type=Configuration,Category=ActivitiesFeed,id1=default";
    @RenderWebElement
    @FindBy (className = "intro-tall")
    WebElement intro;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-activitiesfeed";
    }

    @Override
    public String getInfoPage()
    {
        return "";
    }

    @Override
    public String getIntroPage()
    {
        return intro.getText();
    }

    public enum ActivitiesFeedFields
    {
        activityFeedEnabled("Activity Feed Enabled:", "activities.feed.notifier.enabled"),
        frequencyCRONExpression("Frequency CRON Expression:", "activities.feed.notifier.cronExpression"),
        maximumNumber("Maximum Number:", "activities.feed.max.size"),
        maximumAge("Maximum Age (mins):", "activities.feed.max.ageMins");

        private String label;
        private String jmxAttribute;

        ActivitiesFeedFields(String label, String jmxAttribute)
        {
            this.label = label;
            this.jmxAttribute = jmxAttribute;
        }

        public String getLabel()
        {
            return label;
        }

        public String getJMXAttribute()
        {
            return jmxAttribute;
        }
    }

}
