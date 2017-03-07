package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ActiveTasksPage extends SharePage<ActiveTasksPage>
{
    @RenderWebElement
    @FindBy(css = ".thin")
    private WebElement activeTasksTitle;

    @Override
    public String getRelativePath()
    {
        return "share/page/my-tasks#filter=workflows|active";
    }
}
