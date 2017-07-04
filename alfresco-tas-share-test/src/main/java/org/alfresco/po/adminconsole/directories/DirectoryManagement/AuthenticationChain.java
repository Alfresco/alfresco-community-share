package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.po.adminconsole.directories.DirectoryManagementPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

import java.util.List;

/**
 * Created by Mirela Tifui on 7/4/2017.
 */
public class AuthenticationChain {

    @RenderWebElement
    @FindBy(id = "dm-name")
    WebElement nameField;

    @FindBy(css = "input[value='Synchronization Settings']")
    Button synchronizationSettingsButton;

    @FindBy(css="input[value='Run Synchronize']")
    Button runSynchronizeButton;

    private List<WebElement> rowInfo;
    WebBrowser browser;

    public AuthenticationChain(List<WebElement> rowInfo, WebBrowser browser) {
        this.rowInfo = rowInfo;
        this.browser = browser;
    }

    public String getOrder() {
        return rowInfo.get(0).getText();
    }

    public String getName() {
        return rowInfo.get(1).getText();
    }

    public String getType() {
        return rowInfo.get(2).getText();
    }

    public String getEnabled() {
        return rowInfo.get(3).getText();
    }

    public String getSynchronized() {
        return rowInfo.get(4).getText();
    }

    public void clickAction(String actionName) {
        rowInfo.get(5).findElement(By.linkText(actionName)).click();
        browser.handleModalDialogAcceptingAlert();
    }
}
