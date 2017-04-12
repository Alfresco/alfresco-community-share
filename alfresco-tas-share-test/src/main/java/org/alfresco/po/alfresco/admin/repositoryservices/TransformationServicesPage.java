package org.alfresco.po.alfresco.admin.repositoryservices;

import org.alfresco.po.alfresco.admin.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class TransformationServicesPage extends AdminConsolePage<TransformationServicesPage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-subscriptions";
    }

    @RenderWebElement
    @FindBy(className = "intro")
    WebElement intro;

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
