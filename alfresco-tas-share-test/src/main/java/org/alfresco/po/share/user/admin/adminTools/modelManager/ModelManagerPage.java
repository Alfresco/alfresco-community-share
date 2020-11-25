package org.alfresco.po.share.user.admin.adminTools.modelManager;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.exception.PageRenderTimeException;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class ModelManagerPage extends SharePage2<ModelManagerPage>
{
    private final By nameColumn = By.cssSelector("th[class*=' nameColumn '] span");
    private final By namespaceColumn = By.cssSelector("th[class*=' namespaceColumn '] span");
    private final By statusColumn = By.cssSelector("th[class*=' statusColumn '] span");
    private final By actionsColumn = By.cssSelector("th[class*=' actionsColumn '] span");
    private final By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");
    @RenderWebElement
    private final By createModelButton = By.cssSelector("span[class*='createButton'] span[class='dijitReset dijitStretch dijitButtonContents']");
    private final By importModelButton = By.cssSelector("span[class*='importButton'] span[class='dijitReset dijitStretch dijitButtonContents']");
    @RenderWebElement
    private final By noModelsText = By.cssSelector("div.alfresco-lists-views-AlfListView__no-data");

    public ModelManagerPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/custom-model-manager";
    }

    @Override
    public ModelManagerPage navigate()
    {
        try
        {
            super.navigate();
            waiUntilLoadingMessageDisappears();
            return this;
        }
        catch(TimeoutException | PageRenderTimeException  e)
        {
            LOG.error("Reload Custom Model page");
            return super.navigate();
        }
    }

    public ModelManagerPage assertCreateModelButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(createModelButton), "Create model button is displayed");
        return this;
    }

    public ModelManagerPage assertImportModelButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(importModelButton), "Create model button is displayed");
        return this;
    }

    public ModelManagerPage assertAllColumnsAreDisplayed()
    {
        LOG.info("Assert columns: Name, Namespace, Status and Actions are displayed");
        assertTrue(getBrowser().isElementDisplayed(nameColumn), "Name column is displayed");
        assertTrue(getBrowser().isElementDisplayed(namespaceColumn), "Name space column is displayed");
        assertTrue(getBrowser().isElementDisplayed(statusColumn), "Status column is displayed");
        assertTrue(getBrowser().isElementDisplayed(actionsColumn), "Actions column is displayed");

        return this;
    }

    public String getNoModelsFoundText()
    {
        return getElementText(noModelsText);
    }

    public CreateModelDialogPage clickCreateModelButton()
    {
        getBrowser().waitUntilElementVisible(createModelButton);
        getBrowser().waitUntilElementClickable(createModelButton).click();
        return (CreateModelDialogPage) new CreateModelDialogPage(browser).renderedPage();
    }

    public ModelManagerPage createModel(CustomContentModel contentModel)
    {
        return createModel(contentModel.getName(), contentModel.getNamespaceUri(), contentModel.getNamespacePrefix());
    }

    public ModelManagerPage createModel(String name, String nameSpace, String prefix)
    {
        LOG.info(String.format("Create new model: %s", name));
        CreateModelDialogPage createModelDialogPage = clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        waiUntilLoadingMessageDisappears();

        return (ModelManagerPage) this.renderedPage();
    }

    public ImportModelDialog clickImportModel()
    {
        getBrowser().findElement(importModelButton).click();
        return (ImportModelDialog) new ImportModelDialog(browser).renderedPage();
    }

    public ModelActions usingModel(CustomContentModel contentModel)
    {
        return new ModelActions(contentModel,
            this,
            new EditModelDialog(browser),
            new DeleteModelDialog(browser),
            new ModelDetailsPage(browser));
    }

    public void clickOnAction(String actionName)
    {
        List<WebElement> actionsOptions = getBrowser().waitUntilElementsVisible(actions);
        for (WebElement action : actionsOptions)
        {
            if (action.getText().equals(actionName))
            {
                getBrowser().mouseOver(action);
                action.click();
                break;
            }
        }
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
