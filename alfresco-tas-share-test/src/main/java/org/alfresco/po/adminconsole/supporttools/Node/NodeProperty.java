package org.alfresco.po.adminconsole.supporttools.Node;

import org.alfresco.po.adminconsole.supporttools.NodeBrowserQueryPage;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Mirela Tifui on 7/3/2017.
 */
public class NodeProperty {

    private List<WebElement> rowInfo;
    WebBrowser browser;

    public NodeProperty(List<WebElement> rowInfo, WebBrowser browser)
    {
        this.rowInfo = rowInfo;
        this.browser = browser;
    }

    public String getName() {
        return rowInfo.get(0).getText();
    }

    public String getType() {
        return rowInfo.get(1).getText();
    }

    public String getValue() {
        return rowInfo.get(2).getText();
    }

    public String getResidual() {
        return rowInfo.get(3).getText();
    }

    public void clickDelete() {
        rowInfo.get(4).click();
        browser.handleModalDialogAcceptingAlert();
    }

    public String toString(){
        return String.format("\nvalue=%s,type=%s",
        getValue(),getType());
    }
}
