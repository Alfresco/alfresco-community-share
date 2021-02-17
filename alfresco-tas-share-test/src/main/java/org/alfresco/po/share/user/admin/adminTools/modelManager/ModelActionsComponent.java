package org.alfresco.po.share.user.admin.adminTools.modelManager;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * @author Bogdan Bocancea
 */
@Slf4j
public class ModelActionsComponent
{
    private WebElementInteraction webElementInteraction;

    private CustomContentModel contentModel;
    private RestCustomTypeModel customTypeModel;
    private CustomAspectModel aspectModel;

    private ModelManagerPage modelManagerPage;
    private EditModelDialog editModelDialog;
    private DeleteModelDialog deleteModelDialog;
    private ModelDetailsPage modelDetailsPage;
    private boolean isAspect;

    private final By nameSpaceValue = By.cssSelector("td[class*='namespaceColumn'] span[class='value']");
    private final By displayLabelValue = By.cssSelector("td[class*='displayLabelColumn'] span[class='value']");
    private final By parentValue = By.cssSelector("td[class*='parentColumn'] span[class='value']");
    private final By layoutValue = By.cssSelector("td[class*='layoutColumn '] span[class='value']");
    private final By statusValue = By.cssSelector("td[class*='statusColumn'] span[class='value']");
    private final By actionsButton = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup']");
    private final By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");
    private final By nameValue = By.cssSelector("td[class*='nameColumn'] span[class='value']");
    private final By activeStatus = By.cssSelector("span[class^='status active']");
    private final By inactiveStatus = By.cssSelector("span[class='status alfresco-renderers-Property medium']");
    private String modelRow = "//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='%s']/../../../..";

    public ModelActionsComponent(CustomContentModel contentModel,
                                 WebElementInteraction webElementInteraction,
                                 ModelManagerPage modelManagerPage,
                                 EditModelDialog editModelDialog,
                                 DeleteModelDialog deleteModelDialog,
                                 ModelDetailsPage modelDetailsPage)
    {
        this.contentModel = contentModel;
        this.webElementInteraction = webElementInteraction;
        this.modelManagerPage = modelManagerPage;
        this.editModelDialog = editModelDialog;
        this.deleteModelDialog = deleteModelDialog;
        this.modelDetailsPage = modelDetailsPage;
        log.info("Using custom model: {}", contentModel.getName());
    }

    public ModelActionsComponent(CustomContentModel contentModel,
                                 WebElementInteraction webElementInteraction,
                                 RestCustomTypeModel restCustomTypeModel,
                                 ModelManagerPage modelManagerPage)
    {
        this.contentModel = contentModel;
        this.webElementInteraction = webElementInteraction;
        this.customTypeModel = restCustomTypeModel;
        this.modelManagerPage = modelManagerPage;
        log.info("Using custom type: {}", restCustomTypeModel.getName());
        isAspect = false;
    }

    public ModelActionsComponent(CustomContentModel contentModel,
                                 WebElementInteraction webElementInteraction,
                                 CustomAspectModel aspectModel,
                                 ModelManagerPage modelManagerPage)
    {
        this.contentModel = contentModel;
        this.webElementInteraction = webElementInteraction;
        this.aspectModel = aspectModel;
        this.modelManagerPage = modelManagerPage;
        log.info("Using aspect: {}", aspectModel.getName());
        isAspect = true;
    }

    private WebElement getModelByName(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        boolean isDisplayed = webElementInteraction.isElementDisplayed(modelRowLocator);
        int retryTimes = 0;
        while (retryTimes < RETRY_TIME_80.getValue() && !isDisplayed)
        {
            log.warn("Model {} not displayed - retry: {}", modelName, retryTimes);
            webElementInteraction.refresh();
            webElementInteraction.waitInSeconds(WAIT_2.getValue());
            modelManagerPage.waitUntilLoadingMessageDisappears();
        }
        return webElementInteraction.findElement(modelRowLocator);
    }

    public WebElement getModelRow()
    {
        return getModelByName(contentModel.getName());
    }

    private WebElement getCustomTypeRow()
    {
        return getModelByName(String.format("%s:%s", contentModel.getName(), customTypeModel.getName()));
    }

    private WebElement getAspectRow()
    {
        return getModelByName(String.format("%s:%s", contentModel.getName(), aspectModel.getName()));
    }

    public ModelActionsComponent assertModelIsDisplayed()
    {
        log.info("Assert model is displayed");
        WebElement expectedModelRow = getModelRow();
        webElementInteraction.waitUntilElementIsVisible(expectedModelRow);
        assertTrue(webElementInteraction.isElementDisplayed(expectedModelRow),
            String.format("Model %s is displayed", contentModel.getName()));
        return this;
    }

    public ModelActionsComponent assertModelIsNotDisplayed()
    {
        log.info("Assert model is not displayed");
        assertFalse(webElementInteraction.isElementDisplayed(By.xpath(String.format(modelRow, contentModel.getName()))));
        return this;
    }

    public ModelActionsComponent assertModelNameSpaceIs(String expectedNameSpace)
    {
        log.info("Assert model namespace is: {}", expectedNameSpace);
        assertEquals(getModelRow().findElement(nameSpaceValue).getText(),
            expectedNameSpace, "Name space value is correct");
        return this;
    }

    public ModelActionsComponent assertStatusIsInactive()
    {
        log.info("Assert status is Inactive");
        assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.inactive"));
        return this;
    }

    public ModelActionsComponent assertStatusIsActive()
    {
        log.info("Assert status is Active");
        assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.active"));
        return this;
    }

    public ModelActionsComponent clickActions()
    {
        log.info("Click Actions");
        WebElement actionButton = getModelRow().findElement(actionsButton);
        webElementInteraction.mouseOver(actionButton);
        webElementInteraction.clickElement(actionButton);
        webElementInteraction.waitUntilElementsAreVisible(actions);

        return this;
    }

    public ModelActionsComponent assertActionsAreAvailable(String... expectedActions)
    {
        log.info("Assert available actions are: {}", Arrays.asList(expectedActions));
        String[] values = webElementInteraction.getTextFromLocatorList(actions).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(expectedActions);
        assertEquals(values, expectedActions);
        return this;
    }

    public ModelActionsComponent activateModel()
    {
        log.info("Activate model");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.activate"));
        modelManagerPage.waitUntilLoadingMessageDisappears();
        waitForContentModelStatus(activeStatus);
        return this;
    }

    public ModelActionsComponent deactivateModel()
    {
        log.info("Activate model");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.deactivate"));
        modelManagerPage.waitUntilLoadingMessageDisappears();
        waitForContentModelStatus(inactiveStatus);
        return this;
    }

    private void waitForContentModelStatus(By modelStatus)
    {
        int i = 0;
        while(i < WAIT_15.getValue())
        {
            try
            {
                webElementInteraction.waitUntilChildElementIsPresent(getModelRow(), modelStatus);
                break;
            }
            catch (StaleElementReferenceException e)
            {
                log.error("Wait for custom model status to change");
                i++;
            }
        }
    }

    public EditModelDialog clickEdit()
    {
        log.info("Click Edit");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.edit"));
        return editModelDialog;
    }

    public DeleteModelDialog clickDelete()
    {
        log.info("Click Edit");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.delete"));
        return deleteModelDialog;
    }

    public ModelActionsComponent exportModel()
    {
        log.info("Click Export");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.export"));
        modelManagerPage.waitUntilLoadingMessageDisappears();
        return this;
    }

    public ModelDetailsPage openCustomModel()
    {
        log.info("Open custom model");
        webElementInteraction.clickElement(getModelRow().findElement(nameValue));
        return modelDetailsPage;
    }

    public ModelActionsComponent assertAspectIsDisplayed()
    {
        log.info("Assert aspect is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getAspectRow()),
            String.format("Aspect %s is displayed", aspectModel.getName()));
        return this;
    }

    public ModelActionsComponent assertDisplayLabelIs(String expectedDisplayLabel)
    {
        log.info("Assert display label is: {}", expectedDisplayLabel);
        if(isAspect)
        {
            assertEquals(getAspectRow().findElement(displayLabelValue).getText(),
                expectedDisplayLabel, "Display label is correct");
        }
        else
        {
            assertEquals(getCustomTypeRow().findElement(displayLabelValue).getText(),
                expectedDisplayLabel, "Display label is correct");
        }
        return this;
    }

    public ModelActionsComponent assertParentIs(String expectedParent)
    {
        log.info("Assert display label is: {}", expectedParent);
        if(isAspect)
        {
            assertEquals(getAspectRow().findElement(parentValue).getText(),
                expectedParent, String.format("Parent label not equals %s ", expectedParent));
        }
        else
        {
            assertEquals(getCustomTypeRow().findElement(parentValue).getText(),
                expectedParent, String.format("Parent label not equals %s ", expectedParent));
        }
        return this;
    }

    public ModelActionsComponent assertLayoutIsNo()
    {
        log.info("Assert Layout is No");
        if(isAspect)
        {
            assertEquals(getAspectRow().findElement(layoutValue).getText(), modelManagerPage.language
                .translate("modelManager.layout.no"));
        }
        else
        {
            assertEquals(getCustomTypeRow().findElement(layoutValue).getText(), modelManagerPage.language
                .translate("modelManager.layout.no"));
        }
        return this;
    }
}
