package org.alfresco.po.share.user.admin;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ReplicationJobsPage extends SharePage2<ReplicationJobsPage>
{
    @Autowired
    CreateEditReplicationJobPage createEditReplicationJobPage;

    @RenderWebElement
    @FindBy (css = "a[id*='create']")
    private WebElement createJobButton;

    @FindBy (css = "button[id*='run']")
    private WebElement runJobButton;

    @FindBy (css = "[id*='jobsList'] a")
    private List<WebElement> jobsList;

    @FindBy (css = "[id*='jobStatus']")
    private WebElement status;

    public ReplicationJobsPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "/page/console/admin-console/replication-jobs";
    }

    public CreateEditReplicationJobPage clickCreateJobButton()
    {
        createJobButton.click();
        return (CreateEditReplicationJobPage) createEditReplicationJobPage.renderedPage();
    }

    public void clickRunJobButton()
    {
        runJobButton.click();
    }

    public boolean isJobDisplayedInList(String jobName)
    {
        for (WebElement aJobsList : jobsList)
        {
            if (aJobsList.getText().equals(jobName))
                return true;
        }
        return false;
    }

    public String getJobStatus()
    {
        return status.getText();
    }
}