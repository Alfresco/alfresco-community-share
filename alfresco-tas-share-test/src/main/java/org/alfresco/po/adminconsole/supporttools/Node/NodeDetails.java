package org.alfresco.po.adminconsole.supporttools.Node;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Mirela Tifui on 7/3/2017.
 */
public class NodeDetails {
    private List<WebElement> rowInfo;
    WebBrowser browser;

    public NodeDetails(List<WebElement> rowInfo, WebBrowser browser) {
        this.rowInfo = rowInfo;
        this.browser = browser;
    }

    public String getAspect(){
        return rowInfo.get(0).getText();
    }
}
