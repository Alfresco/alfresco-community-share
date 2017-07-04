package org.alfresco.po.adminconsole.directories;

import javafx.scene.control.Tab;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Table;

@PageObject
public class DirectoryManagementPage extends AdminConsolePage<DirectoryManagementPage>
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
        return "alfresco/s/admin/admin-directorymanagement";
    }

    public void setName(String name){
        getBrowser().waitUntilElementVisible(nameField).clear();
        nameField.sendKeys(name);
    }

}
