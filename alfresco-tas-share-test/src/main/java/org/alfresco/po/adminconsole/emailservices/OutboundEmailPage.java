package org.alfresco.po.adminconsole.emailservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class OutboundEmailPage extends AdminConsolePage<OutboundEmailPage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-outboundemail";
    }

    @FindBy (className = "intro")
    WebElement intro;

    @FindBy (className = "info")
    WebElement info;

    @Override
    public String getInfoPage()
    {
        return info.getText();
    }

    @Override
    public String getIntroPage()
    {
        return intro.getText();
    }

}
