package org.alfresco.po.adminconsole.directories;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.*;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.utility.report.log.Step.STEP;

@PageObject
public class DirectoryManagementPage extends AdminConsolePage<AuthenticationChain>
{
    @Autowired
    SyncUserDirectoriesDialog syncUserDirectoriesDialog;

    @FindBy(id = "dm-name")
    WebElement nameField;

    @FindBy(id = "dm-authtable")
    Table authenticationTable;

    @FindBy(className = "sync-status")
    Table syncStatusTable;

    @FindBy(css = "input[value='Synchronization Settings']")
    WebElement synchronizationSettingsButton;

    @RenderWebElement
    @FindBy(css = "input[value='Run Synchronize']")
    WebElement runSynchronizeButton;

    @RenderWebElement
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

    @FindBy(css = ".column-full>p>b")
    WebElement syncStatusMessage;

    public enum Type
    {
        OPEN_LDAP("ldap"), LDAP("ldap-ad"), PASSTHRU("passthru"), KERBEROS("kerberos"), EXTERNAL("external");

        private String value;

        Type(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public enum CifsAuthenticationOptions
    {
        INTERNAL("alfrescoNtlm1"), DISABLED("");

        private String value;

        CifsAuthenticationOptions(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public enum BrowserBasedAutomaticLogin
    {
        INTERNAL("alfrescoNtlm1"), DISABLED("");

        private String value;

        BrowserBasedAutomaticLogin(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    @Override
    public String getInfoPage()
    {
        return "";
    }

    @Override
    public String getIntroPage()
    {
        return "";
    }

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-directorymanagement";
    }

    public List<AuthenticationChain> getAuthenticationDetails()
    {
        STEP("Get authentication details.");
        this.renderedPage();
        ArrayList<AuthenticationChain> row = new ArrayList<>();
        List<List<WebElement>> rows = authenticationTable.getRows();
        for (List<WebElement> details : rows)
        {
            AuthenticationChain authDetail = new AuthenticationChain(details, getBrowser());
            row.add(authDetail);
        }

        return row;
    }

    public List<SynchronizationStatus> getSynchronizationStatus()
    {
        STEP("Get synchronization status.");
        this.renderedPage();
        ArrayList<SynchronizationStatus> row = new ArrayList<>();
        List<List<WebElement>> rows = syncStatusTable.getRows();
        for (List<WebElement> details : rows)
        {
            SynchronizationStatus syncStatusDetail = new SynchronizationStatus(details, getBrowser());
            row.add(syncStatusDetail);
        }

        return row;
    }

    public SynchronizationSettingsDialog clickSynchronizationSettings()
    {
        synchronizationSettingsButton.click();
        return new SynchronizationSettingsDialog();
    }

    public SyncUserDirectoriesDialog clickRunSynchronize()
    {
        STEP("Click Run Synchronize button.");
        runSynchronizeButton.click();
        return (SyncUserDirectoriesDialog) syncUserDirectoriesDialog.renderedPage();
    }

    public void setName(String name)
    {
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void clickAdd()
    {
        addButton.click();
    }

    public DirectoryManagementPage selectType(Type type)
    {
        availableTypes.selectByValue(type.getValue());
        clickAdd();
        return this;
    }

    public DirectoryManagementPage selectCIFSAuthentication(CifsAuthenticationOptions option)
    {
        cifsAuthentication.selectByValue(option.getValue());
        return this;
    }

    public DirectoryManagementPage selectBrowserBasedAutomaticLogin(BrowserBasedAutomaticLogin option)
    {
        browserBasedAutomaticLogin.selectByValue(option.getValue());
        return this;
    }

    public String getSyncStatusMessage() {
        this.refresh();
        this.renderedPage();
        return syncStatusMessage.getText();
    }

    public void clickSave()
    {
        saveButton.click();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public void synchronize()
    {
        clickRunSynchronize();
        Assert.assertEquals(syncUserDirectoriesDialog.clickSync(), "Sync Started", "Sync message should appear");
        syncUserDirectoriesDialog.clickClose();
    }

    public SynchronizationStatus getStatusForBean(SynchronizationStatus.BeanNames beanName) throws Exception
    {
        for(SynchronizationStatus status: getSynchronizationStatus())
        {
            if(status.getBeanName().contains(beanName.getName()))
                return status;
        }
        throw new Exception("Bean is not available in the Status list!");
    }

    public EditDirectoryDialog edit(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth: getAuthenticationDetails())
        {
            if(auth.getType().equals(authenticationType))
            {
                auth.clickAction("Edit");
                return new EditDirectoryDialog();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public AuthenticationTestDialog test(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth: getAuthenticationDetails())
        {
            if(auth.getType().contains(authenticationType))
            {
                auth.clickAction("Test");
                return new AuthenticationTestDialog();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public SyncUserDirectoryTestDialog testSynchronize(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth: getAuthenticationDetails())
        {
            if(auth.getType().equals(authenticationType))
            {
                auth.clickAction("Test Synchronize");
                return new SyncUserDirectoryTestDialog();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public DirectoryManagementPage status(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth: getAuthenticationDetails())
        {
            if(auth.getType().equals(authenticationType))
            {
                auth.clickAction("Status [+]");
                return (DirectoryManagementPage) this.renderedPage();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public DirectoryManagementPage remove(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth: getAuthenticationDetails())
        {
            if(auth.getType().equals(authenticationType))
            {
                auth.clickAction("Remove");
                return (DirectoryManagementPage) this.renderedPage();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }
}
