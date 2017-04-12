package org.alfresco.po.alfresco.admin.supporttools;

import org.alfresco.po.alfresco.admin.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class DownloadJMXDumpPage extends AdminConsolePage<DownloadJMXDumpPage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-supporttools";
    }

    @RenderWebElement
    @FindBy(className = "intro")
    WebElement intro;

    @RenderWebElement
    @FindBy(css = "input[value='Export']")
    WebElement export;

    public void clickExport()
    {
        export.click();
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
