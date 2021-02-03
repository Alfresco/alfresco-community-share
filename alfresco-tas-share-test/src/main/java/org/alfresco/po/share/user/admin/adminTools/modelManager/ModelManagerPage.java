package org.alfresco.po.share.user.admin.adminTools.modelManager;

import static org.alfresco.common.Wait.WAIT_2;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
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
    public ModelManagerPage navigate()
    {
        try
        {
            super.navigate();
            waitUntilLoadingMessageDisappears();
            return this;
        }
        catch(TimeoutException | PageRenderTimeException  e)
        {
            log.error("Reload Custom Model page");
            return super.navigate();
        }
    }

    public CreateModelDialogPage clickCreateModelButton()
    {
        webElementInteraction.clickElement(createModelButton);
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
        waitUntilLoadingMessageDisappears();

        return this;
    }

    public ImportModelDialog clickImportModel()
    {
        webElementInteraction.clickElement(importModelButton);
        return new ImportModelDialog(webDriver);
    }

    public ModelActionsComponent usingModel(CustomContentModel contentModel)
    {
        return new ModelActionsComponent(contentModel,
            webElementInteraction,
            this,
            new EditModelDialog(webDriver),
            new DeleteModelDialog(webDriver),
            new ModelDetailsPage(webDriver));
    }

    public void clickOnAction(String actionName)
    {
        List<WebElement> actionsOptions = webElementInteraction.waitUntilElementsAreVisible(actions);
        for (WebElement action : actionsOptions)
        {
            if (action.getText().equals(actionName))
            {
                webElementInteraction.mouseOver(action);
                webElementInteraction.clickElement(action);
                webElementInteraction.waitInSeconds(WAIT_2.getValue());
                break;
            }
        }
    }

    public ModelActionsComponent usingCustomType(CustomContentModel contentModel, RestCustomTypeModel customTypeModel)
    {
        return new ModelActionsComponent(contentModel, webElementInteraction, customTypeModel,this);
    }

    public ModelActionsComponent usingAspect(CustomContentModel contentModel, CustomAspectModel customAspect)
    {
        return new ModelActionsComponent(contentModel, webElementInteraction, customAspect,this);
    }
}
