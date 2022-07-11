package org.alfresco.po.share.user.admin.adminTools.modelManager;

import static org.alfresco.common.Wait.WAIT_2;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.exception.PageRenderTimeException;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class ModelManagerPage extends SharePage2<ModelManagerPage>
{
    private final By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");
    private final By createModelButton = By.cssSelector("span[class*='createButton'] span[class='dijitReset dijitStretch dijitButtonContents']");
    private final By importModelButton = By.cssSelector("span[class*='importButton'] span[class='dijitReset dijitStretch dijitButtonContents']");
    private final By contentModelsTable = By.cssSelector("div[class$='dijitVisible'] div[class='alfresco-lists-AlfList']");

    public ModelManagerPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/custom-model-manager";
    }

    @Override
    public synchronized ModelManagerPage navigate()
    {
        try
        {
            super.navigate();
            waitForContentModelTableToBeLoaded();
            return this;
        }
        catch(TimeoutException | PageRenderTimeException  e)
        {
            log.error("Reload Custom Model page");
            return super.navigate();
        }
    }

    public void waitForContentModelTableToBeLoaded()
    {
        waitUntilElementIsVisible(contentModelsTable);
    }

    public CreateModelDialogPage clickCreateModelButton()
    {
        clickElement(createModelButton);
        return new CreateModelDialogPage(webDriver);
    }

    public ModelManagerPage createModel(CustomContentModel contentModel)
    {
        return createModel(contentModel.getName(), contentModel.getNamespaceUri(), contentModel.getNamespacePrefix());
    }

    public ModelManagerPage createModel(String name, String nameSpace, String prefix)
    {
        log.info("Create new model: {}", name);
        CreateModelDialogPage createModelDialogPage = clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        waitForContentModelTableToBeLoaded();

        return this;
    }

    public ImportModelDialog clickImportModel()
    {
        clickElement(importModelButton);
        return new ImportModelDialog(webDriver);
    }

    public void clickOnAction(String actionName)
    {
        List<WebElement> actionsOptions = waitUntilElementsAreVisible(actions);
        for (WebElement action : actionsOptions)
        {
            if (action.getText().equals(actionName))
            {
                mouseOver(action);
                clickElement(action);
                waitInSeconds(WAIT_2.getValue());
                break;
            }
        }
    }

    public ModelActionsComponent usingModel(CustomContentModel contentModel)
    {
        return new ModelActionsComponent(webDriver, contentModel);
    }

    public ModelActionsComponent usingCustomType(CustomContentModel contentModel, RestCustomTypeModel restCustomTypeModel)
    {
        waitForContentModelTableToBeLoaded();
        return new ModelActionsComponent(webDriver, contentModel, restCustomTypeModel);
    }

    public ModelActionsComponent usingAspect(CustomContentModel contentModel, CustomAspectModel customAspect)
    {
        waitForContentModelTableToBeLoaded();
        return new ModelActionsComponent(webDriver, contentModel, customAspect);
    }
}
