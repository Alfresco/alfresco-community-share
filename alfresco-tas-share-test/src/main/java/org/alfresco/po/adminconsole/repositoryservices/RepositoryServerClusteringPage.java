package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class RepositoryServerClusteringPage extends AdminConsolePage<RepositoryServerClusteringPage>
{
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-clustering";
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
