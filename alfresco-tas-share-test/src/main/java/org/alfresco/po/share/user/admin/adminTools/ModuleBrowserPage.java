package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ModuleBrowserPage extends AdminToolsPage
{
    @RenderWebElement
    @FindBy (id = "LIST_WITH_HEADER_ITEMS")
    private WebElement moduleContent;

    @RenderWebElement
    private By titleTableHeader = By.id("titleTableHeader");

    @RenderWebElement
    private By descriptionTableHeader = By.id("descriptionTableHeader");

    @RenderWebElement
    private By versionTableHeader = By.id("versionTableHeader");

    private By modulesList = By.cssSelector("tr[id*=alfresco_lists_views_layouts_Row]");

    @FindBy (id = "HEADER_TITLE")
    private WebElement modelManagerPageTitle;

    @Override
    public String getRelativePath()
    {
        return null;
    }


    public WebElement selectModuleName(String moduleName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(modulesList, 6);
        List<WebElement> itemsList = browser.findElements(modulesList);
        return browser.findFirstElementWithValue(itemsList, moduleName);
    }

    public boolean isModuleAvailable(String moduleName)
    {
        return browser.isElementDisplayed(selectModuleName(moduleName));
    }

    public boolean isTitleHeaderDisplayed()
    {
        return browser.isElementDisplayed(titleTableHeader);
    }

    public boolean isDescriptionHeaderDisplayed()
    {
        return browser.isElementDisplayed(descriptionTableHeader);
    }

    public boolean isVersionHeaderDisplayed()
    {
        return browser.isElementDisplayed(versionTableHeader);
    }

    public String getModelManagerPageTitle()
    {
        browser.waitUntilElementVisible(modelManagerPageTitle);
        return modelManagerPageTitle.getText();
    }
}
