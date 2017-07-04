package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 7/4/2017.
 */
public class RunSynchronize {
    WebBrowser browser;

    public RunSynchronize( WebBrowser browser) {
        this.browser = browser;
    }
    @RenderWebElement
    @FindBy(css="iframe[src='/alfresco/s/enterprise/admin/admin-sync']")
    WebElement runSynchronizeWindow;

    @FindBy(css = "input[value='Sync']")
    WebElement sync;

    @FindBy(css="input[value='Close']")
    WebElement cancel;

    public void clickSync() {
        browser.waitUntilElementClickable(sync).click();
    }

    public void clickCancel() {
        browser.waitUntilElementClickable(cancel).click();
        browser.waitUntilElementDisappears(By.cssSelector("iframe[src='/alfresco/s/enterprise/admin/admin-sync']"));
    }

}
