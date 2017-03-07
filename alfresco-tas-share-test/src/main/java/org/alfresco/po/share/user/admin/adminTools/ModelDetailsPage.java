package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class ModelDetailsPage extends SharePage<ModelDetailsPage>
{
    @Override
    public String getRelativePath()
    {
        return null;
    }

    @RenderWebElement
    private By createCustomTypeButton = By.cssSelector("span[class*='createTypeButton'] span");

    @RenderWebElement
    private By createAspectButton = By.cssSelector("span[class*='createPropertyGroupButton'] span");

    @RenderWebElement
    private By showModelsButton = By.cssSelector("span[class*='backButton '] span[id*='alfresco_buttons_AlfButton']");

    @FindAll(@FindBy (css= "tr[id^='alfresco_lists_views_layouts_Row']"))
    private List<WebElement> list;

    private By typeAspectList =  By.cssSelector("tr[id^='alfresco_lists_views_layouts_Row']");

    public WebElement selectRow (String typeAspectName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(typeAspectList, 5);
        List<WebElement> itemsList = browser.findElements(typeAspectList);
        return browser.findFirstElementWithValue(itemsList, typeAspectName);
    }
    public boolean isCreateCustomTypeButtonDisplayed()
    {
        browser.waitUntilElementClickable(createCustomTypeButton, 5L);
        return browser.isElementDisplayed(createCustomTypeButton);
    }

    public String getTypeDetails(String typeName)
    {
        return selectRow(typeName).getText();
    }
    public boolean isCreateAspectButtonDisplayed()
    {
        browser.waitUntilElementClickable(createAspectButton, 5L);
        return browser.isElementDisplayed(createAspectButton);
    }

    public boolean isShowModelsButtonDisplayed()
    {
        browser.waitUntilElementClickable(showModelsButton, 5L);
        return browser.isElementDisplayed(showModelsButton);
    }

    public HtmlPage clickCreateCustomTypeButton(HtmlPage page)
    {
        browser.waitUntilElementClickable(createCustomTypeButton, 5L);
        browser.findElement(createCustomTypeButton).click();
        return page.renderedPage();
    }

    public HtmlPage clickOnCreateAspectButton(HtmlPage page)
    {
        browser.findElement(createAspectButton).click();
        return page.renderedPage();
    }
}
