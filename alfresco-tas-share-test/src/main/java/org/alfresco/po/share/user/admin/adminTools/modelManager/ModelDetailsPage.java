package org.alfresco.po.share.user.admin.adminTools.modelManager;

import org.alfresco.po.share.BasePages;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateAspectDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class ModelDetailsPage extends BasePages<ModelDetailsPage>
{
    @RenderWebElement
    private By createCustomTypeButton = By.cssSelector("span[class*='createTypeButton'] span");
    @RenderWebElement
    private By createAspectButton = By.cssSelector("span[class*='createPropertyGroupButton'] span");
    private By showModelsButton = By.cssSelector("span[class*='backButton '] span[id*='alfresco_buttons_AlfButton']");
    private By typeList = By.cssSelector("div#TYPES_LIST tr[id^='alfresco_lists_views_layouts_Row']");
    private By aspectList = By.cssSelector("div#PROPERTY_GROUPS_LIST tr[id^='alfresco_lists_views_layouts_Row']");

    public ModelDetailsPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public ModelDetailsPage assertCreateCustomTypeButtonDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(createCustomTypeButton), "Create Custom Type button is displayed");
        return this;
    }

    public String getAspectDetails(String aspectName)
    {
        List<WebElement> itemsList = getBrowser().waitUntilElementsVisible(aspectList);
        return getBrowser().findFirstElementWithValue(itemsList, aspectName).getText();
    }

    public ModelDetailsPage assertCreateAspectButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(createAspectButton), "Create Aspect button is displayed");
        return this;
    }

    public ModelDetailsPage assertShowModelsButtonDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(showModelsButton), "Show models button is displayed");
        return this;
    }

    public CreateCustomTypeDialog clickCreateCustomType()
    {
        getBrowser().findElement(createCustomTypeButton).click();
        return (CreateCustomTypeDialog) new CreateCustomTypeDialog(browser).renderedPage();
    }

    public CreateAspectDialog clickCreateAspect()
    {
        getBrowser().findElement(createAspectButton).click();
        return (CreateAspectDialog) new CreateAspectDialog(browser).renderedPage();
    }
}
