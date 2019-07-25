package org.alfresco.po.share.user.admin.adminTools;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateAspectDialogPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class ModelDetailsPage extends SharePage<ModelDetailsPage>
{
    @Autowired
    CreateCustomTypeDialog createCustomTypeDialog;

    @Autowired
    CreateAspectDialogPage createAspectDialogPage;
    @RenderWebElement
    @FindBy (css = "span[class*='createTypeButton'] span")
    private WebElement createCustomTypeButton;
    @RenderWebElement
    @FindBy (css = "span[class*='createPropertyGroupButton'] span")
    private WebElement createAspectButton;
    @RenderWebElement
    @FindBy (css = "span[class*='backButton '] span[id*='alfresco_buttons_AlfButton']")
    private WebElement showModelsButton;
    @RenderWebElement
    @FindBy (css = "div[class$='dijitVisible'] tbody[id$='ITEMS']>tr")
    private List<WebElement> rows;

    /*@RenderWebElement
    @FindBy(css = "div[widgetid*='alfresco_menus_AlfMenuBarPopup']")
    private List<WebElement> actionButtons;*/
    private By typeName = By.cssSelector("td[class*='nameColumn'] span[class='value']");
    private By actionsButton = By.cssSelector("td[class*='actionsColumn'] span[class$='arrow']");
    private By typeList = By.cssSelector("div#TYPES_LIST tr[id^='alfresco_lists_views_layouts_Row']");
    private By aspectList = By.cssSelector("div#PROPERTY_GROUPS_LIST tr[id^='alfresco_lists_views_layouts_Row']");

    @Override
    public String getRelativePath()
    {
        return null;
    }

    private void clickAction(String type)
    {
        for (WebElement element : rows)
        {
            WebElement name = element.findElement(typeName);
            if (name.getText().equals(type))
            {
                element.findElement(actionsButton).click();
            }
        }
    }

    public void selectlayouDesign(String type)
    {
        clickAction(type);
    }

    public boolean isCreateCustomTypeButtonDisplayed()
    {
        return browser.isElementDisplayed(createCustomTypeButton);
    }

    public String getAspectDetails(String aspectName)
    {
        List<WebElement> itemsList = browser.waitUntilElementsVisible(aspectList);
        return browser.findFirstElementWithValue(itemsList, aspectName).getText();
    }

    public String getTypeDetails(String typeName)
    {
        List<WebElement> itemsList = browser.waitUntilElementsVisible(typeList);
        return browser.findFirstElementWithValue(itemsList, typeName).getText();
    }

    public boolean isCreateAspectButtonDisplayed()
    {
        return browser.isElementDisplayed(createAspectButton);
    }

    public boolean isShowModelsButtonDisplayed()
    {
        return browser.isElementDisplayed(showModelsButton);
    }

    public CreateCustomTypeDialog clickCreateCustomTypeButton()
    {
        createCustomTypeButton.click();
        return (CreateCustomTypeDialog) createCustomTypeDialog.renderedPage();
    }

    public CreateAspectDialogPage clickOnCreateAspectButton()
    {
        createAspectButton.click();
        return (CreateAspectDialogPage) createAspectDialogPage.renderedPage();
    }
}
