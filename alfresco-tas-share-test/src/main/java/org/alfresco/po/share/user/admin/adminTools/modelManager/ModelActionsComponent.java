package org.alfresco.po.share.user.admin.adminTools.modelManager;

import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.common.Wait.WAIT_80;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bogdan Bocancea
 */
public class ModelActionsComponent
{
    protected final Logger LOG = LoggerFactory.getLogger(ModelActionsComponent.class);

    private ModelManagerPage modelManagerPage;
    private WebElementInteraction webElementInteraction;
    private CustomContentModel contentModel;
    private EditModelDialog editModelDialog;
    private DeleteModelDialog deleteModelDialog;
    private ModelDetailsPage modelDetailsPage;
    private RestCustomTypeModel customTypeModel;
    private CustomAspectModel aspectModel;
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
        LOG.info("Using custom model: {}", contentModel.getName());
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
        LOG.info("Using custom type: {}", restCustomTypeModel.getName());
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
        LOG.info("Using aspect: {}", aspectModel.getName());
        isAspect = true;
    }

    private WebElement getModelByName(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        return webElementInteraction.waitWithRetryAndReturnWebElement(modelRowLocator, WAIT_2.getValue(), WAIT_60.getValue());
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
        LOG.info("Assert model is displayed");
        WebElement expectedModelRow = getModelRow();
        webElementInteraction.waitUntilElementIsVisible(expectedModelRow);
        assertTrue(webElementInteraction.isElementDisplayed(expectedModelRow),
            String.format("Model %s is displayed", contentModel.getName()));
        return this;
    }

    public ModelActionsComponent assertModelIsNotDisplayed()
    {
        LOG.info("Assert model is not displayed");
        assertFalse(webElementInteraction.isElementDisplayed(By.xpath(String.format(modelRow, contentModel.getName()))));
        return this;
    }

    public ModelActionsComponent assertModelNameSpaceIs(String expectedNameSpace)
    {
        LOG.info("Assert model namespace is: {}", expectedNameSpace);
        assertEquals(getModelRow().findElement(nameSpaceValue).getText(),
            expectedNameSpace, "Name space value is correct");
        return this;
    }

    public ModelActionsComponent assertStatusIsInactive()
    {
        LOG.info("Assert status is Inactive");
        assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.inactive"));
        return this;
    }

    public ModelActionsComponent assertStatusIsActive()
    {
        LOG.info("Assert status is Active");
        assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.active"));
        return this;
    }

    public ModelActionsComponent clickActions()
    {
        LOG.info("Click Actions");
        WebElement actionButton = getModelRow().findElement(actionsButton);
        webElementInteraction.mouseOver(actionButton);
        webElementInteraction.clickElement(actionButton);
        webElementInteraction.waitUntilElementsAreVisible(actions);

        return this;
    }

    public ModelActionsComponent assertActionsAreAvailable(String... expectedActions)
    {
        LOG.info("Assert available actions are: {}", Arrays.asList(expectedActions));
        String[] values = webElementInteraction.getTextFromLocatorList(actions).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(expectedActions);
        assertEquals(values, expectedActions);
        return this;
    }

    public ModelActionsComponent activateModel()
    {
        LOG.info("Activate model");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.activate"));
        modelManagerPage.waitUntilLoadingMessageDisappears();
        waitForContentModelStatus(activeStatus);
        return this;
    }

    public ModelActionsComponent deactivateModel()
    {
        LOG.info("Activate model");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.deactivate"));
        modelManagerPage.waitUntilLoadingMessageDisappears();
        waitForContentModelStatus(inactiveStatus);
        return this;
    }

    private void waitForContentModelStatus(By modelStatus)
    {
        int i = 0;
        while(i < WAIT_80.getValue())
        {
            try
            {
                webElementInteraction.waitUntilChildElementIsPresent(getModelRow(), modelStatus);
                break;
            }
            catch (StaleElementReferenceException e)
            {
                LOG.error("Wait for custom model status to change");
                i++;
            }
        }
    }

    public EditModelDialog clickEdit()
    {
        LOG.info("Click Edit");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.edit"));
        return editModelDialog;
    }

    public DeleteModelDialog clickDelete()
    {
        LOG.info("Click Edit");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.delete"));
        return deleteModelDialog;
    }

    public ModelActionsComponent exportModel()
    {
        LOG.info("Click Export");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.export"));
        modelManagerPage.waitUntilLoadingMessageDisappears();
        return this;
    }

    public ModelDetailsPage openCustomModel()
    {
        LOG.info("Open custom model");
        webElementInteraction.clickElement(getModelRow().findElement(nameValue));
        return modelDetailsPage;
    }

    public ModelActionsComponent assertAspectIsDisplayed()
    {
        LOG.info("Assert aspect is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getAspectRow()),
            String.format("Aspect %s is displayed", aspectModel.getName()));
        return this;
    }

    public ModelActionsComponent assertDisplayLabelIs(String expectedDisplayLabel)
    {
        LOG.info("Assert display label is: {}", expectedDisplayLabel);
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
        LOG.info("Assert display label is: {}", expectedParent);
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
        LOG.info("Assert Layout is No");
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
