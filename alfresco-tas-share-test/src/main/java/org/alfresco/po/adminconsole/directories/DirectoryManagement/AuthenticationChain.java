package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.List;

import org.alfresco.utility.Utility;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Mirela Tifui on 7/4/2017.
 * This class is modeling the "Authentication Chain" table found on /alfresco/s/enterprise/admin/admin-directorymanagement
 */
public class AuthenticationChain
{
    WebBrowser browser;
    private List<WebElement> rowInfo;

    public AuthenticationChain(List<WebElement> rowInfo, WebBrowser browser)
    {
        this.rowInfo = rowInfo;
        this.browser = browser;
    }

    public String getOrder()
    {
        return rowInfo.get(0).getText();
    }

    public String getName()
    {
        return rowInfo.get(1).getText();
    }

    public String getType()
    {
        return rowInfo.get(2).getText();
    }

    public String getEnabled()
    {
        return rowInfo.get(3).getText();
    }

    public String getSynchronized()
    {
        STEP("Get synchronized value");
        return rowInfo.get(4).getText();
    }

    public void clickAction(String actionName)
    {
        WebElement link;
        STEP(String.format("Click action %s", actionName));
        Utility.waitToLoopTime(1);
        if (actionName.equals("Status [+]"))
        {
            actionName = actionName.replace(" [+]", "");
            link = rowInfo.get(5).findElement(By.xpath(".//a[contains(text(),'" + actionName + "')]"));
        } else
            link = rowInfo.get(5).findElement(By.xpath(".//a[text()='" + actionName + "']"));
        link.click();
        Utility.waitToLoopTime(1);
    }
}
