package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class CompletedTasksPage extends SharePage<CompletedTasksPage>
{
    @RenderWebElement
    @FindBy (css = "[class=thin]")
    private WebElement completedTasksTitle;

    @Override
    public String getRelativePath()
    {
        return "share/page/my-tasks#filter=workflows|completed";
    }

}
