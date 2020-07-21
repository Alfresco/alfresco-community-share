package org.alfresco.po.share.user.admin.adminTools.modelManager;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialogPage;
import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ModelManagerPage extends AdminToolsPage
{
    private By actionsButton = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup']");
    private By nameValue = By.cssSelector("td[class*='nameColumn'] span[class='value']");
    private By statusValue = By.cssSelector("td[class*='statusColumn'] span[class='value']");// todo- delete
    private By nameSpaceValue = By.cssSelector("td[class*='namespaceColumn'] span[class='value']"); // todo- delete
    private By nameColumn = By.cssSelector("th[class*=' nameColumn '] span");
    private By namespaceColumn = By.cssSelector("th[class*=' namespaceColumn '] span");
    private By statusColumn = By.cssSelector("th[class*=' statusColumn '] span");
    private By actionsColumn = By.cssSelector("th[class*=' actionsColumn '] span");
    private By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");
    private String modelRow = "//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='%s']/../../../..";

    @Autowired
    CreateModelDialogPage createModelDialogPage;

    @Autowired
    ImportModelDialogPage importModelDialogPage;

    @Autowired
    ModelDetailsPage modelDetailsPage;

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

    public WebElement getModelByName(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        getBrowser().waitUntilElementIsDisplayedWithRetry(modelRowLocator, 1, WAIT_5_SEC);
        return getBrowser().findElement(modelRowLocator);
    }

    public boolean isModelDisplayed(String modelName) // TODO - delete
    {
        return browser.isElementDisplayed(By.xpath(String.format(modelRow, modelName)));
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

    public ImportModelDialogPage clickImportModel()
    {
        importModelButton.click();
        return (ImportModelDialogPage) importModelDialogPage.renderedPage();
    }

    public ModelDetailsPage clickModelName(String modelName)
    {
        browser.waitUntilElementClickable(getModelByName(modelName).findElement(nameValue)).click();
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }

    public void clickActionsButtonForModel(String modelName)
    {
        Parameter.checkIsMandotary("Model", getModelByName(modelName));
        Utils.retry(() ->
            {
                browser.waitUntilElementClickable(getModelByName(modelName).findElement(actionsButton)).click();
                return browser.waitUntilElementVisible(actions, WAIT_5_SEC);
            },
            DEFAULT_RETRY);
    }

    public boolean isActionAvailable(String actionName)
    {
        List<WebElement> actionsOptions = browser.waitUntilElementsVisible(actions);
        for (WebElement actionOption : actionsOptions)
        {
            if (actionOption.getText().equals(actionName))
            {
                return true;
            }
        }
        return false;
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

    public String getModelNamespace(String modelName) // TODO -delete
    {
        Parameter.checkIsMandotary("Model", getModelByName(modelName));
        return getModelByName(modelName).findElement(nameSpaceValue).getText();
    }

    public String getModelStatus(String modelName) // TODO -delete
    {
        Parameter.checkIsMandotary("Model", getModelByName(modelName));
        return getModelByName(modelName).findElement(statusValue).getText();
    }

    public ModelActions usingModel(CustomContentModel contentModel)
    {
        return new ModelActions(contentModel, this);
    }
}
