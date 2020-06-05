package org.alfresco.po.share.user.admin.adminTools;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
    @FindBy (id = "titleTableHeader")
    private WebElement titleTableHeader;

    @RenderWebElement
    @FindBy (id = "descriptionTableHeader")
    private WebElement descriptionTableHeader;

    @RenderWebElement
    @FindBy (id = "versionTableHeader")
    private WebElement versionTableHeader;

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
        browser.waitUntilElementIsDisplayedWithRetry(modulesList, WAIT_15_SEC);
        return browser.findFirstElementWithValue(modulesList, moduleName);
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
