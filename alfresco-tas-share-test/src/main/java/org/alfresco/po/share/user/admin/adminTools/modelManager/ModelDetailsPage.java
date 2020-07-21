package org.alfresco.po.share.user.admin.adminTools.modelManager;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateAspectDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class ModelDetailsPage extends SharePage<ModelDetailsPage>
{
    @Autowired
    private CreateCustomTypeDialog createCustomTypeDialog;

    @Autowired
    private CreateAspectDialog createAspectDialogPage;

    @RenderWebElement
    @FindBy (css = "span[class*='createTypeButton'] span")
    private WebElement createCustomTypeButton;

    @RenderWebElement
    @FindBy (css = "span[class*='createPropertyGroupButton'] span")
    private WebElement createAspectButton;

    @RenderWebElement
    @FindBy (css = "span[class*='backButton '] span[id*='alfresco_buttons_AlfButton']")
    private WebElement showModelsButton;

    private By typeList = By.cssSelector("div#TYPES_LIST tr[id^='alfresco_lists_views_layouts_Row']");
    private By aspectList = By.cssSelector("div#PROPERTY_GROUPS_LIST tr[id^='alfresco_lists_views_layouts_Row']");

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public ModelDetailsPage assertCreateCustomTypeButtonDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(createCustomTypeButton), "Create Custom Type button is displayed");
        return this;
    }

    public String getAspectDetails(String aspectName)
    {
        List<WebElement> itemsList = browser.waitUntilElementsVisible(aspectList);
        return browser.findFirstElementWithValue(itemsList, aspectName).getText();
    }

    public ModelDetailsPage assertCreateAspectButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(createAspectButton), "Create Aspect button is displayed");
        return this;
    }

    public ModelDetailsPage assertShowModelsButtonDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(showModelsButton), "Show models button is displayed");
        return this;
    }

    public CreateCustomTypeDialog clickCreateCustomType()
    {
        createCustomTypeButton.click();
        return (CreateCustomTypeDialog) createCustomTypeDialog.renderedPage();
    }

    public CreateAspectDialog clickCreateAspect()
    {
        createAspectButton.click();
        return (CreateAspectDialog) createAspectDialogPage.renderedPage();
    }
}
