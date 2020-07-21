package org.alfresco.po.share.user.admin.adminTools.modelManager;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.List;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ModelManagerPage extends AdminToolsPage
{
    private By nameColumn = By.cssSelector("th[class*=' nameColumn '] span");
    private By namespaceColumn = By.cssSelector("th[class*=' namespaceColumn '] span");
    private By statusColumn = By.cssSelector("th[class*=' statusColumn '] span");
    private By actionsColumn = By.cssSelector("th[class*=' actionsColumn '] span");
    private By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");

    @Autowired
    private CreateModelDialogPage createModelDialogPage;

    @Autowired
    private ImportModelDialog importModelDialogPage;

    @Autowired
    private ModelDetailsPage modelDetailsPage;

    @Autowired
    private EditModelDialog editModelDialog;

    @Autowired
    private DeleteModelDialog deleteModelDialogPage;

    @RenderWebElement
    @FindBy (css = "span[class*='createButton'] span[class='dijitReset dijitStretch dijitButtonContents']")
    private WebElement createModelButton;

    @FindBy (css = "span[class*='importButton'] span[class='dijitReset dijitStretch dijitButtonContents']")
    private WebElement importModelButton;

    @RenderWebElement
    @FindBy (className = "alfresco-lists-views-AlfListView")
    private WebElement modelsList;

    @FindBy (css = "div.alfresco-lists-views-AlfListView__no-data")
    private WebElement noModelsText;

    @FindBy (css = "span[class*='createPropertyGroupButton'] span")
    private WebElement createAspectButton;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/custom-model-manager";
    }

    public ModelManagerPage assertCreateModelButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(createModelButton), "Create model button is displayed");
        return this;
    }

    public ModelManagerPage assertImportModelButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(importModelButton), "Create model button is displayed");
        return this;
    }

    public ModelManagerPage assertAllColumnsAreDisplayed()
    {
        LOG.info("Assert columns: Name, Namespace, Status and Actions are displayed");
        Assert.assertTrue(browser.isElementDisplayed(nameColumn), "Name column is displayed");
        Assert.assertTrue(browser.isElementDisplayed(namespaceColumn), "Name space column is displayed");
        Assert.assertTrue(browser.isElementDisplayed(statusColumn), "Status column is displayed");
        Assert.assertTrue(browser.isElementDisplayed(actionsColumn), "Actions column is displayed");
        return this;
    }

    public String getNoModelsFoundText()
    {
        return noModelsText.getText();
    }

    public CreateModelDialogPage clickCreateModelButton()
    {
        getBrowser().waitUntilElementVisible(createModelButton);
        getBrowser().waitUntilElementClickable(createModelButton).click();
        return (CreateModelDialogPage) createModelDialogPage.renderedPage();
    }

    public ModelManagerPage createModel(CustomContentModel contentModel)
    {
        return createModel(contentModel.getName(), contentModel.getNamespaceUri(), contentModel.getNamespacePrefix());
    }

    public ModelManagerPage createModel(String name, String nameSpace, String prefix)
    {
        LOG.info(String.format("Create new model: %s", name));
        clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        waitForLoadingMessageToDisappear();
        return (ModelManagerPage) this.renderedPage();
    }

    public ImportModelDialog clickImportModel()
    {
        importModelButton.click();
        return (ImportModelDialog) importModelDialogPage.renderedPage();
    }

    public HtmlPage clickOnAction(String actionName, HtmlPage page)
    {
        List<WebElement> actionsOptions = browser.waitUntilElementsVisible(actions);
        for (WebElement action : actionsOptions)
        {
            if (action.getText().equals(actionName))
            {
                browser.mouseOver(action);
                action.click();
                break;
            }
        }
        if(page instanceof ModelManagerPage)
        {
            waitForLoadingMessageToDisappear();
        }
        return page.renderedPage();
    }

    public ModelActions usingModel(CustomContentModel contentModel)
    {
        return new ModelActions(contentModel, this, editModelDialog, deleteModelDialogPage, modelDetailsPage);
    }

    public ModelActions usingCustomType(CustomContentModel contentModel, RestCustomTypeModel customTypeModel)
    {
        return new ModelActions(contentModel, customTypeModel,this);
    }

    public ModelActions usingAspect(CustomContentModel contentModel, CustomAspectModel customAspect)
    {
        return new ModelActions(contentModel, customAspect,this);
    }
}
