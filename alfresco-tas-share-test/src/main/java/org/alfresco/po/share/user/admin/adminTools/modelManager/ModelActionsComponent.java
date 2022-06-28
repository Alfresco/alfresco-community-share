package org.alfresco.po.share.user.admin.adminTools.modelManager;

import static org.alfresco.common.RetryTime.RETRY_TIME_15;
import static org.alfresco.common.Wait.WAIT_1;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteModelDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.EditModelDialog;
import org.alfresco.rest.model.RestCustomTypeModel;
import org.alfresco.utility.model.CustomAspectModel;
import org.alfresco.utility.model.CustomContentModel;
import org.openqa.selenium.*;

/**
 * @author Bogdan Bocancea
 */
@Slf4j
public class ModelActionsComponent extends ModelManagerPage
{
    private CustomContentModel contentModel;
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

    private final String modelRow = "//tr[contains(@id,'alfresco_lists_views_layouts_Row')]//span[text()='%s']/../../../..";

    public ModelActionsComponent(ThreadLocal<WebDriver> webDriver, CustomContentModel contentModel)
    {
        super(webDriver);
        this.contentModel = contentModel;
    }

    public ModelActionsComponent(ThreadLocal<WebDriver> webDriver, CustomContentModel contentModel, CustomAspectModel aspectModel)
    {
        super(webDriver);
        this.contentModel = contentModel;
        this.aspectModel = aspectModel;
        isAspect = true;
    }

    public ModelActionsComponent(ThreadLocal<WebDriver> webDriver, CustomContentModel contentModel, RestCustomTypeModel customTypeModel)
    {
        super(webDriver);
        this.contentModel = contentModel;
        this.customTypeModel = customTypeModel;
        isAspect = false;
    }

    private WebElement getModelByName(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        return waitUntilElementIsVisible(modelRowLocator);
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
        waitUntilElementIsVisible(expectedModelRow);
        assertTrue(isElementDisplayed(expectedModelRow),
            String.format("Model %s is displayed", contentModel.getName()));
        return this;
    }

    public ModelActionsComponent assertModelIsNotDisplayed()
    {
        log.info("Assert model is not displayed");
        assertFalse(isElementDisplayed(By.xpath(String.format(modelRow, contentModel.getName()))));
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
            language.translate("modelManager.status.inactive"));
        return this;
    }

    public ModelActionsComponent assertStatusIsActive()
    {
        log.info("Assert status is Active");
        assertEquals(getModelRow().findElement(statusValue).getText(), language.translate("modelManager.status.active"));
        return this;
    }

    public ModelActionsComponent clickActions()
    {
        log.info("Click Actions");
        WebElement actionButton = getModelRow().findElement(actionsButton);
        mouseOver(actionButton);
        clickElement(actionButton);
        waitUntilElementsAreVisible(actions);

        return this;
    }

    public ModelActionsComponent assertActionsAreAvailable(String... expectedActions)
    {
        log.info("Assert available actions are: {}", Arrays.asList(expectedActions));
        String[] values = getTextFromLocatorList(actions).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(expectedActions);
        assertEquals(values, expectedActions);
        return this;
    }

    public ModelActionsComponent activateModel()
    {
        log.info("Activate model");
        clickOnAction(language.translate("modelManager.action.activate"));
        waitForContentModelStatus(activeStatus);
        return this;
    }

    public ModelActionsComponent deactivateModel()
    {
        log.info("Activate model");
        clickOnAction(language.translate("modelManager.action.deactivate"));
        waitForContentModelStatus(inactiveStatus);
        return this;
    }

    private void waitForContentModelStatus(By modelStatus)
    {
        int retryCounter = 0;
        while(retryCounter < RETRY_TIME_15.getValue())
        {
            retryCounter++;
            try
            {
                waitForContentModelTableToBeLoaded();
                waitUntilChildElementIsPresent(getModelRow(), modelStatus, WAIT_1.getValue());
                break;
            }
            catch (StaleElementReferenceException | TimeoutException e)
            {
                log.error("Wait for custom model status to change");
            }
        }
    }

    public EditModelDialog clickEdit()
    {
        log.info("Click Edit");
        clickOnAction(language.translate("modelManager.action.edit"));
        return new EditModelDialog(webDriver);
    }

    public DeleteModelDialog clickDelete()
    {
        log.info("Click Edit");
        clickOnAction(language.translate("modelManager.action.delete"));
        return new DeleteModelDialog(webDriver);
    }

    public ModelActionsComponent exportModel()
    {
        log.info("Click Export");
        clickOnAction(language.translate("modelManager.action.export"));
        return this;
    }

    public ModelDetailsPage openCustomModel()
    {
        log.info("Open custom model");
        clickElement(getModelRow().findElement(nameValue));
        return new ModelDetailsPage(webDriver);
    }

    public ModelActionsComponent assertAspectIsDisplayed()
    {
        log.info("Assert aspect is displayed");
        assertTrue(isElementDisplayed(getAspectRow()), String.format("Aspect %s is displayed", aspectModel.getName()));
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
            assertEquals(getAspectRow().findElement(layoutValue).getText(), language.translate("modelManager.layout.no"));
        }
        else
        {
            assertEquals(getCustomTypeRow().findElement(layoutValue).getText(), language.translate("modelManager.layout.no"));
        }
        return this;
    }
}
