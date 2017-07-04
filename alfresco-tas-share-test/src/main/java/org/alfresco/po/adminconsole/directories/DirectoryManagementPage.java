package org.alfresco.po.adminconsole.directories;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.AuthenticationChain;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@PageObject
public class DirectoryManagementPage extends AdminConsolePage<AuthenticationChain> {
    @FindBy(id = "dm-name")
    WebElement nameField;

    @FindBy(id = "dm-authtable")
    Table authenticationTable;

    @RenderWebElement
    @FindBy(css = "input[value='Synchronization Settings']")
    WebElement synchronizationSettingsButton;

    @RenderWebElement
    @FindBy(css = "input[value='Run Synchronize']")
    WebElement runSynchronizeButton;

    @FindBy(css = "input[value='Save']")
    WebElement saveButton;

    @FindBy(css = "input.cancel")
    WebElement cancelButton;

    @FindBy(id = "dm-type")
    Select availableTypes;

    @FindBy(id = "dm-cifs")
    Select cifsAuthentication;

    @FindBy(id = "dm-browser")
    Select browserBasedAutomaticLogin;

    @FindBy(css = "input[value='Add']")
    Button addButton;

    public enum Type {
        OpenLDAP("ldap"),
        LDAP("ldap-ad"),
        Passthru("passthru"),
        Kerberos("kerberos"),
        External("external");

        private String value;

        private Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CifsAuthenticationOptions {
        internal("alfrescoNtlm1"),
        disabled("");

        private String value;

        private CifsAuthenticationOptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum BrowserBasedAutomaticLogin {
        internal("alfrescoNtlm1"),
        disabled("");

        private String value;

        private BrowserBasedAutomaticLogin(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public String getInfoPage() {
        return "";
    }

    @Override
    public String getIntroPage() {
        return "";
    }

    @Override
    protected String relativePathToURL() {
        return "alfresco/s/enterprise/admin/admin-directorymanagement";
    }

    public List<AuthenticationChain> getAuthenticationDetails() {
        ArrayList<AuthenticationChain> row = new ArrayList<AuthenticationChain>();
        List<List<WebElement>> rows = authenticationTable.getRows();
        for (List<WebElement> details : rows) {
            AuthenticationChain authDetail = new AuthenticationChain(details, getBrowser());
            LOG.info("Detail: " + authDetail.toString());
            row.add(authDetail);
        }

        return row;
    }

    public void clickSynchronizationSettingsButton() {
        synchronizationSettingsButton.click();
    }

    public void clickRunSynchronize() {
        runSynchronizeButton.click();
        getBrowser().waitUntilElementPresent(By.cssSelector("iframe[src='/alfresco/s/enterprise/admin/admin-sync']"));
    }

    public void setName(String name) {
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void clickAdd() {
        addButton.click();
    }

    public DirectoryManagementPage selectType(Type type) {
        availableTypes.selectByValue(type.getValue());
        clickAdd();
        return this;
    }

    public DirectoryManagementPage selectCIFSAuthentication(CifsAuthenticationOptions option) {
        cifsAuthentication.selectByValue(option.getValue());
        return this;
    }

    public DirectoryManagementPage selectBrowserBasedAutomaticLogin(BrowserBasedAutomaticLogin option) {
        browserBasedAutomaticLogin.selectByValue(option.getValue());
        return this;
    }

    public void clickSave()
    {
        saveButton.click();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

}
