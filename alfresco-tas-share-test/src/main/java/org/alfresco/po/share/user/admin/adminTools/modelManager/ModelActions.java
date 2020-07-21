package org.alfresco.po.share.user.admin.adminTools.modelManager;

import org.alfresco.utility.model.CustomContentModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.Arrays;

/**
 * @author Bogdan Bocancea
 */
public class ModelActions
{
    private ModelManagerPage modelManagerPage;
    private CustomContentModel contentModel;

    private By nameSpaceValue = By.cssSelector("td[class*='namespaceColumn'] span[class='value']");
    private By statusValue = By.cssSelector("td[class*='statusColumn'] span[class='value']");
    private By actionsButton = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup']");
    private By actions = By.cssSelector("div[id^='alfresco_menus_AlfMenuBarPopup_'] td[class ='dijitReset dijitMenuItemLabel']");

    public ModelActions(CustomContentModel contentModel, ModelManagerPage modelManagerPage)
    {
        this.contentModel = contentModel;
        this.modelManagerPage = modelManagerPage;
    }

    private WebBrowser getBrowser()
    {
        return modelManagerPage.getBrowser();
    }

    public WebElement getModelRow()
    {
        return modelManagerPage.getModelByName(contentModel.getName());
    }

    public ModelActions assertModelIsDisplayed()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(getModelRow()),
            String.format("Model %s is displayed", contentModel.getName()));
        return this;
    }

    public ModelActions assertModelNameSpaceIs(String expectedNameSpace)
    {
        Assert.assertEquals(getModelRow().findElement(nameSpaceValue).getText(),
            expectedNameSpace, "Name space value is correct");
        return this;
    }

    public ModelActions assertStatusIsInactive()
    {
        Assert.assertEquals(getModelRow().findElement(statusValue).getText(),
            modelManagerPage.language.translate("modelManager.status.inactive"));
        return this;
    }

    public ModelActions clickActions()
    {
        getModelRow().findElement(actionsButton).click();
        getBrowser().waitUntilElementsVisible(actions);
        return this;
    }

    public ModelActions assertActionsAreAvailable(String... expectedActions)
    {
        String[] values = getBrowser().getTextFromLocatorList(actions).toArray(new String[0]);
        Arrays.sort(values);
        Arrays.sort(expectedActions);
        Assert.assertEquals(values, expectedActions);
        return this;
    }
}
