package org.alfresco.po.adminconsole.directories;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.AuthenticationChain;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.AuthenticationTestDialog;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.EditDirectoryDialog;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.SyncUserDirectoriesDialog;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.SyncUserDirectoryTestDialog;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.SynchronizationSettingsDialog;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.SynchronizationStatus;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.Table;

@PageObject
public class DirectoryManagementPage extends AdminConsolePage<AuthenticationChain>
{
    @Autowired
    SyncUserDirectoriesDialog syncUserDirectoriesDialog;

    @FindBy (id = "dm-name")
    WebElement nameField;

    @FindBy (id = "dm-authtable")
    Table authenticationTable;

    @FindBy (className = "sync-status")
    Table syncStatusTable;

    @FindBy (css = "input[value='Synchronization Settings']")
    WebElement synchronizationSettingsButton;

    @RenderWebElement
    @FindBy (css = "input[value='Run Synchronize']")
    WebElement runSynchronizeButton;

    @FindBy (id = "dm-type")
    Select availableTypes;

    @FindBy (id = "dm-cifs")
    Select cifsAuthentication;

    @RenderWebElement
    @FindBy (id = "dm-browser")
    Select browserBasedAutomaticLogin;

    @FindBy (css = "input[value='Add']")
    Button addButton;

    @FindBy (css = ".column-full>p>b")
    WebElement syncStatusMessage;

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
        Utils.clearAndType(nameField, name);
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

    public DirectoryManagementPage selectCIFSAuthentication(String option) throws Exception
    {
        if (getCIFSAuthenticationOptions().contains(option))
            cifsAuthentication.selectByVisibleText(option);
        else
            throw new Exception("Option not available!");
        return this;
    }

    public List<String> getCIFSAuthenticationOptions() throws Exception
    {
        List<WebElement> optionsList = cifsAuthentication.getOptions();
        List<String> optionsValues = new ArrayList<>();
        for (WebElement option : optionsList)
            optionsValues.add(option.getText());
        return optionsValues;
    }

    public String getCIFSAuthenticationSelectedOption() throws Exception
    {
        return cifsAuthentication.getFirstSelectedOption().getText();
    }

    public DirectoryManagementPage selectBrowserBasedAutomaticLogin(String option) throws Exception
    {
        if (getBrowserBasedAutomaticLoginOptions().contains(option))
            browserBasedAutomaticLogin.selectByVisibleText(option);
        else
            throw new Exception("Option not available!");
        return this;
    }

    public List<String> getBrowserBasedAutomaticLoginOptions() throws Exception
    {
        List<WebElement> optionsList = browserBasedAutomaticLogin.getOptions();
        List<String> optionsValues = new ArrayList<>();
        for (WebElement option : optionsList)
            optionsValues.add(option.getText());
        return optionsValues;
    }

    public String getBrowserBasedAutomaticLoginSelectedOption() throws Exception
    {
        return browserBasedAutomaticLogin.getFirstSelectedOption().getText();
    }

    public String getSyncStatusMessage()
    {
        this.refresh();
        this.renderedPage();
        return syncStatusMessage.getText();
    }

    public HtmlPage synchronize()
    {
        clickRunSynchronize();
        Assert.assertEquals(syncUserDirectoriesDialog.clickSync(), "Sync Started", "Sync message should appear");
        return syncUserDirectoriesDialog.clickClose(this);
    }

    public SynchronizationStatus getStatusForBean(SynchronizationStatus.BeanNames beanName) throws Exception
    {
        for (SynchronizationStatus status : getSynchronizationStatus())
        {
            if (status.getBeanName().contains(beanName.getName()))
                return status;
        }
        throw new Exception("Bean is not available in the Status list!");
    }

    public EditDirectoryDialog edit(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth : getAuthenticationDetails())
        {
            if (auth.getType().equals(authenticationType))
            {
                auth.clickAction("Edit");
                Utility.waitToLoopTime(3);
                return new EditDirectoryDialog();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public AuthenticationTestDialog test(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth : getAuthenticationDetails())
        {
            if (auth.getType().contains(authenticationType))
            {
                auth.clickAction("Test");
                return new AuthenticationTestDialog();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public SyncUserDirectoryTestDialog testSynchronize(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth : getAuthenticationDetails())
        {
            if (auth.getType().equals(authenticationType))
            {
                auth.clickAction("Test Synchronize");
                return new SyncUserDirectoryTestDialog();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public DirectoryManagementPage status(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth : getAuthenticationDetails())
        {
            if (auth.getType().equals(authenticationType))
            {
                auth.clickAction("Status [+]");
                return (DirectoryManagementPage) this.renderedPage();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public DirectoryManagementPage remove(String authenticationType) throws Exception
    {
        for (AuthenticationChain auth : getAuthenticationDetails())
        {
            if (auth.getType().equals(authenticationType))
            {
                auth.clickAction("Remove");
                return (DirectoryManagementPage) this.renderedPage();
            }
        }
        throw new Exception("Authentication type is not available in the Authentication chain list!");
    }

    public enum Type
    {
        OPEN_LDAP("ldap", "OpenLDAP"),
        LDAP("ldap-ad", "LDAP (Active Directory)"),
        PASSTHRU("passthru", "Passthru"),
        KERBEROS("kerberos", "Kerberos"),
        EXTERNAL("external", "External");

        private String value, displayedText;

        Type(String value, String displayedText)
        {
            this.value = value;
            this.displayedText = displayedText;
        }

        public String getValue()
        {
            return value;
        }

        public String getDisplayedText()
        {
            return displayedText;
        }
    }
}
