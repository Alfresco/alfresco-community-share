package org.alfresco.po.adminconsole.supporttools.Node;

import java.util.List;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Mirela Tifui on 7/3/2017.
 * This class is modeling the "Children" table of a Node found on /alfresco/s/admin/admin-nodebrowser
 */
public class NodeChild
{
    WebBrowser browser;
    private List<WebElement> rowInfo;

    public NodeChild(List<WebElement> rowInfo, WebBrowser browser)
    {
        this.rowInfo = rowInfo;
        this.browser = browser;
    }

    public String getName()
    {
        return rowInfo.get(0).getText();
    }

    public String getChildReference()
    {
        return rowInfo.get(1).getText();
    }

    public String getPrimary()
    {
        return rowInfo.get(2).getText();
    }

    public String getAssociationType()
    {
        return rowInfo.get(3).getText();
    }

    public String getIndex()
    {
        return rowInfo.get(4).getText();
    }

    public void clickAction(String actionName)
    {
        rowInfo.get(5).findElement(By.linkText(actionName)).click();
        browser.handleModalDialogAcceptingAlert();
    }
}
