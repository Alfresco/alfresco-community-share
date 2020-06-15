package org.alfresco.po.share.user.admin.adminTools;

import java.util.List;

import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateModelDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.ImportModelDialogPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ModelManagerPage extends AdminToolsPage
{
    private By actionsButton = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup']");
    private By nameValue = By.cssSelector("td[class*='nameColumn'] span[class='value']");
    private By statusValue = By.cssSelector("td[class*='statusColumn'] span[class='value']");
    private By nameSpaceValue = By.cssSelector("td[class*='namespaceColumn'] span[class='value']");
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

    @RenderWebElement
    @FindBy (css = "span[class*='importButton'] span[class='dijitReset dijitStretch dijitButtonContents']")
    private WebElement importModelButton;

    @FindBy (css = "div.alfresco-lists-views-AlfListView__no-data")
    private WebElement noModelsText;

    @FindAll (@FindBy (css = "tr[id^='alfresco_lists_views_layouts_Row']"))
    private List<WebElement> modelItemsList;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/custom-model-manager";
    }

    public boolean isCreateModelButtonDisplayed()
    {
        return browser.isElementDisplayed(createModelButton);
    }

    public boolean isImportModelButtonDisplayed()
    {
        return browser.isElementDisplayed(importModelButton);
    }

    public boolean isNameColumnDisplayed()
    {
        return browser.isElementDisplayed(nameColumn);
    }

    public boolean isNamespaceColumnDisplayed()
    {
        return browser.isElementDisplayed(namespaceColumn);
    }

    public boolean isStatusColumnDisplayed()
    {
        return browser.isElementDisplayed(statusColumn);
    }

    public boolean isActionsColumnDisplayed()
    {
        return browser.isElementDisplayed(actionsColumn);
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

    public WebElement selectModelByName(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        getBrowser().waitUntilElementIsDisplayedWithRetry(modelRowLocator, (int) properties.getImplicitWait());
        return getBrowser().findElement(modelRowLocator);
    }

    public boolean isModelDisplayed(String modelName)
    {
        By modelRowLocator = By.xpath(String.format(modelRow, modelName));
        return browser.isElementDisplayed(modelRowLocator);
    }

    public void waitForModel(String modelName)
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath(String.format(modelRow, modelName)), WAIT_15_SEC);
    }

    public void createModel(String name, String nameSpace, String prefix)
    {
        navigate();
        clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.clickCreateButton();
        waitForModel(name);
    }

    public void createModel(String name, String nameSpace, String prefix, String creator, String description)
    {
        clickCreateModelButton();
        createModelDialogPage.sendNamespaceText(nameSpace);
        createModelDialogPage.sendPrefixText(prefix);
        createModelDialogPage.sendNameText(name);
        createModelDialogPage.sendCreatorText(creator);
        createModelDialogPage.sendDescription(description);
        createModelDialogPage.clickCreateButton();
        waitForModel(name);
    }

    public ImportModelDialogPage clickImportModel()
    {
        importModelButton.click();
        return (ImportModelDialogPage) importModelDialogPage.renderedPage();
    }

    public ModelDetailsPage clickModelName(String modelName)
    {
        browser.waitUntilElementClickable(selectModelByName(modelName).findElement(nameValue)).click();
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }

    public void clickActionsButtonForModel(String modelName)
    {
        Parameter.checkIsMandotary("Model", selectModelByName(modelName));
        browser.waitUntilElementClickable(selectModelByName(modelName).findElement(actionsButton)).click();
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
        return page.renderedPage();
    }

    public String getModelNamespace(String modelName)
    {
        Parameter.checkIsMandotary("Model", selectModelByName(modelName));
        return selectModelByName(modelName).findElement(nameSpaceValue).getText();
    }

    public String getModelStatus(String modelName)
    {
        Parameter.checkIsMandotary("Model", selectModelByName(modelName));
        return selectModelByName(modelName).findElement(statusValue).getText();
    }

    public boolean isAlertPresent()
    {
        try
        {
            browser.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
    }
}
