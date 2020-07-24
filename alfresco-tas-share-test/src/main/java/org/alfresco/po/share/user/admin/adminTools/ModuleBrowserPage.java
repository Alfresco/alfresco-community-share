package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

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
        return "share/page/console/admin-console/module-package";
    }

    public WebElement selectModuleName(String moduleName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(modulesList,1, WAIT_10);
        return browser.findFirstElementWithValue(modulesList, moduleName);
    }

    public boolean isModuleAvailable(String moduleName)
    {
        return browser.isElementDisplayed(selectModuleName(moduleName));
    }

    public ModuleBrowserPage assertGoogleDocsModuleIsPresent()
    {
        Assert.assertTrue(isModuleAvailable(language.translate("moduleBrowser.googleDocs.title")),
            "Google Docs Share Module is not available");
        return this;
    }

    public ModuleBrowserPage assertModuleTableHeadersAreDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(titleTableHeader), "Title header is displayed");
        Assert.assertTrue(browser.isElementDisplayed(descriptionTableHeader), "Description header is displayed");
        Assert.assertTrue(browser.isElementDisplayed(versionTableHeader), "Version header is displayed");
        return this;
    }

    public String getModelManagerPageTitle()
    {
        browser.waitUntilElementVisible(modelManagerPageTitle);
        return modelManagerPageTitle.getText();
    }
}
