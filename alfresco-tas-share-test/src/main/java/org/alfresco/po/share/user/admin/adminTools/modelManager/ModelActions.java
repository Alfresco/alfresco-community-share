package org.alfresco.po.share.user.admin.adminTools.modelManager;

import static org.alfresco.common.Wait.WAIT_10;
import static org.alfresco.common.Wait.WAIT_15;

import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.Arrays;

/**
 * @author Bogdan Bocancea
 */
public class ModelActions
{
    protected final Logger LOG = LoggerFactory.getLogger(ModelActions.class);

    private ModelManagerPage modelManagerPage;
    private CustomContentModel contentModel;
    private EditModelDialog editModelDialog;
    private DeleteModelDialog deleteModelDialog;
    private RestCustomTypeModel customTypeModel;
    private ModelDetailsPage modelDetailsPage;
    private CustomAspectModel aspectModel;
    private boolean isAspect;

    private By nameSpaceValue = By.cssSelector("td[class*='namespaceColumn'] span[class='value']");
    private By displayLabelValue = By.cssSelector("td[class*='displayLabelColumn'] span[class='value']");
    private By parentValue = By.cssSelector("td[class*='parentColumn'] span[class='value']");
    private By layoutValue = By.cssSelector("td[class*='layoutColumn '] span[class='value']");
    private By statusValue = By.cssSelector("td[class*='statusColumn'] span[class='value']");
    private By actionsButton = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup']");
    private By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");
    private String modelRow = "//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='%s']/../../../..";
    private By nameValue = By.cssSelector("td[class*='nameColumn'] span[class='value']");
    private By activeStatus = By.cssSelector("span[class^='status active']");
    private By inactiveStatus = By.cssSelector("span[class='status alfresco-renderers-Property medium']");

    public ModelActions(CustomContentModel contentModel, ModelManagerPage modelManagerPage, EditModelDialog editModelDialog,
                        DeleteModelDialog deleteModelDialog, ModelDetailsPage modelDetailsPage)
    {
        this.contentModel = contentModel;
        this.modelManagerPage = modelManagerPage;
        this.editModelDialog = editModelDialog;
        this.deleteModelDialog = deleteModelDialog;
        this.modelDetailsPage = modelDetailsPage;
        LOG.info(String.format("Using custom model: %s", contentModel.getName()));
    }

    public ModelActions (CustomContentModel contentModel, RestCustomTypeModel restCustomTypeModel,  ModelManagerPage modelManagerPage)
    {
        this.contentModel = contentModel;
        this.customTypeModel = restCustomTypeModel;
        this.modelManagerPage = modelManagerPage;
        LOG.info(String.format("Using custom type: %s", restCustomTypeModel.getName()));
        isAspect = false;
    }

    public ModelActions(CustomContentModel contentModel, CustomAspectModel aspectModel, ModelManagerPage modelManagerPage)
    {
        this.contentModel = contentModel;
        this.aspectModel = aspectModel;
        this.modelManagerPage = modelManagerPage;
        LOG.info(String.format("Using aspect: %s", aspectModel.getName()));
        isAspect = true;
    }

    private WebElement getModelByName(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        return getBrowser().waitWithRetryAndReturnWebElement(modelRowLocator, 1, WAIT_10.getValue());
    }

    private WebBrowser getBrowser()
    {
        return modelManagerPage.getBrowser();
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

    public ModelActions assertModelIsDisplayed()
    {
        LOG.info("Assert model is displayed");
        Assert.assertTrue(getBrowser().isElementDisplayed(getModelRow()),
            String.format("Model %s is displayed", contentModel.getName()));
        return this;
    }

    public ModelActions assertModelIsNotDisplayed()
    {
        LOG.info("Assert model is not displayed");
        Assert.assertFalse(getBrowser().isElementDisplayed(By.xpath(String.format(modelRow, contentModel.getName()))));
        return this;
    }

    public ModelActions assertModelNameSpaceIs(String expectedNameSpace)
    {
        LOG.info(String.format("Assert model namespace is: %s", expectedNameSpace));
        Assert.assertEquals(getModelRow().findElement(nameSpaceValue).getText(),
            expectedNameSpace, "Name space value is correct");
        return this;
    }

    public ModelActions assertStatusIsInactive()
    {
        LOG.info("Assert status is Inactive");
        Assert.assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.inactive"));
        return this;
    }

    public ModelActions assertStatusIsActive()
    {
        LOG.info("Assert status is Active");
        Assert.assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.active"));
        return this;
    }

    public ModelActions clickActions()
    {
        LOG.info("Click Actions");
        WebElement actionButton = getModelRow().findElement(actionsButton);
        getBrowser().mouseOver(actionButton);
        getBrowser().waitUntilElementClickable(actionButton).click();
        getBrowser().waitUntilElementsVisible(actions);
        return this;
    }

    public ModelActions assertActionsAreAvailable(String... expectedActions)
    {
        LOG.info(String.format("Assert available actions are: %s", Arrays.asList(expectedActions)));
        String[] values = getBrowser().getTextFromLocatorList(actions).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(expectedActions);
        Assert.assertEquals(values, expectedActions);
        return this;
    }

    public ModelActions activateModel()
    {
        LOG.info("Activate model");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.activate"));
        modelManagerPage.waiUntilLoadingMessageDisappears();
        waitForContentModelStatus(activeStatus);
        return this;
    }

    public ModelActions deactivateModel()
    {
        LOG.info("Activate model");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.deactivate"));
        modelManagerPage.waiUntilLoadingMessageDisappears();
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
                getBrowser().waitUntilChildElementIsPresent(getModelRow(), modelStatus);
                break;
            }
            catch (StaleElementReferenceException e)
            {
                LOG.error("Wait for custom model status to change");
                i++;
                continue;
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

    public ModelActions exportModel()
    {
        LOG.info("Click Edit");
        modelManagerPage.clickOnAction(modelManagerPage.language.translate("modelManager.action.export"));
        modelManagerPage.waiUntilLoadingMessageDisappears();
        return this;
    }

    public ModelDetailsPage openCustomModel()
    {
        LOG.info("Open custom model");
        getBrowser().waitUntilElementClickable(getModelRow().findElement(nameValue)).click();
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }

    public ModelActions assertCustomTypeIsDisplayed()
    {
        LOG.info("Assert custom type is displayed");
        Assert.assertTrue(getBrowser().isElementDisplayed(getCustomTypeRow()),
            String.format("Custom Type %s is displayed", customTypeModel.getName()));
        return this;
    }

    public ModelActions assertAspectIsDisplayed()
    {
        LOG.info("Assert aspect is displayed");
        Assert.assertTrue(getBrowser().isElementDisplayed(getAspectRow()),
            String.format("Aspect %s is displayed", aspectModel.getName()));
        return this;
    }

    public ModelActions assertDisplayLabelIs(String expectedDisplayLabel)
    {
        LOG.info(String.format("Assert display label is: %s", expectedDisplayLabel));
        if(isAspect)
        {
            Assert.assertEquals(getAspectRow().findElement(displayLabelValue).getText(),
                expectedDisplayLabel, "Display label is correct");
        }
        else
        {
            Assert.assertEquals(getCustomTypeRow().findElement(displayLabelValue).getText(),
                expectedDisplayLabel, "Display label is correct");
        }
        return this;
    }

    public ModelActions assertParentIs(String expectedParent)
    {
        LOG.info(String.format("Assert display label is: %s", expectedParent));
        if(isAspect)
        {
            Assert.assertEquals(getAspectRow().findElement(parentValue).getText(),
                expectedParent, "Display label is correct");
        }
        else
        {
            Assert.assertEquals(getCustomTypeRow().findElement(parentValue).getText(),
                expectedParent, "Display label is correct");
        }
        return this;
    }

    public ModelActions assertLayoutIsNo()
    {
        LOG.info("Assert Layout is No");
        if(isAspect)
        {
            Assert.assertEquals(getAspectRow().findElement(layoutValue).getText(), modelManagerPage.language.translate("modelManager.layout.no"));
        }
        else
        {
            Assert.assertEquals(getCustomTypeRow().findElement(layoutValue).getText(), modelManagerPage.language.translate("modelManager.layout.no"));
        }
        return this;
    }
}
