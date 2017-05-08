package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ActivitiesFeedPage extends AdminConsolePage<ActivitiesFeedPage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-activitiesfeed";
    }

    @RenderWebElement
    @FindBy(className = "intro-tall")
    WebElement intro;

    @FindBy(name = "Alfresco:Type=Configuration,Category=ActivitiesFeed,id1=default|activities.feed.notifier.cronExpression")
    WebElement frequencycronexpression;

    @FindBy(name = "Alfresco:Type=Configuration,Category=ActivitiesFeed,id1=default|activities.feed.max.size")
    WebElement maximumnumber;

    @FindBy(name = "Alfresco:Type=Configuration,Category=ActivitiesFeed,id1=default|activities.feed.max.ageMins")
    WebElement maximumage;

    public String getMaximumNumber()
    {
        return maximumnumber.getAttribute("value");
    }

    public void setMaximumNumber(String value)
    {
        this.maximumnumber.clear();
        this.maximumnumber.sendKeys(value);
    }

    public String getMaximumAge()
    {
        return maximumage.getAttribute("value");
    }

    public void setMaximumAge(String maximumage)
    {
        this.maximumage.clear();
        this.maximumage.sendKeys(maximumage);
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

}
