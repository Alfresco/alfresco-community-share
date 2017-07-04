package org.alfresco.po.adminconsole.directories;

import javafx.scene.control.Tab;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.AuthenticationChain;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class DirectoryManagementPage extends AdminConsolePage<AuthenticationChain>
{
    @FindBy(id="dm-name")
    WebElement nameField;

    @FindBy(id="dm-authtable")
    Table authenticationTable;

    @RenderWebElement
    @FindBy(css = "input[value='Synchronization Settings']")
    WebElement synchronizationSettingsButton;

    @RenderWebElement
    @FindBy(css="input[value='Run Synchronize']")
    WebElement runSynchronizeButton;

    @FindBy(css="input[value='Save']")
    WebElement saveButton;

    @FindBy(css="input.cancel")
    WebElement cancelButton;

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
        ArrayList<AuthenticationChain> row = new ArrayList<AuthenticationChain>();
        List<List<WebElement>> rows = authenticationTable.getRows();
        for (List<WebElement> details: rows){
            AuthenticationChain authDetail = new AuthenticationChain(details, getBrowser());
            LOG.info("Detail: "+ authDetail.toString());
            row.add(authDetail);
        }

        return row;
    }

    public void clickSynchronizationSettingsButton()
    {
        synchronizationSettingsButton.click();
    }

    public void clickRunSynchronize()
    {
        runSynchronizeButton.click();
    }

    public void setName(String name) {
        nameField.clear();
        nameField.sendKeys(name);
    }
}
