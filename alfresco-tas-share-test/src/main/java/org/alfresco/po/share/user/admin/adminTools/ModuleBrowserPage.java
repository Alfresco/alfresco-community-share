package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertTrue;

public class ModuleBrowserPage extends SharePage2<ModuleBrowserPage>
{
    @RenderWebElement
    private final By moduleContent = By.id("LIST_WITH_HEADER_ITEMS");
    @RenderWebElement
    private final By titleTableHeader = By.id("titleTableHeader");
    private final By descriptionTableHeader = By.id("descriptionTableHeader");
    private final By versionTableHeader = By.id("versionTableHeader");
    private final By modulesList = By.cssSelector("tr[id*=alfresco_lists_views_layouts_Row]");
    private final By modelManagerPageTitle = By.id("HEADER_TITLE");

    public ModuleBrowserPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/module-package";
    }

    public ModuleBrowserPage assertModuleBrowserPageIsOpened()
    {
        LOG.info("Assert Module browser page is opened");
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Module browser page is not opened");
        return this;
    }

    public WebElement selectModuleName(String moduleName)
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(modulesList,1, WAIT_10.getValue());
        return getBrowser().findFirstElementWithValue(modulesList, moduleName);
    }

    public boolean isModuleAvailable(String moduleName)
    {
        return getBrowser().isElementDisplayed(selectModuleName(moduleName));
    }

    public ModuleBrowserPage assertGoogleDocsModuleIsPresent()
    {
        assertTrue(isModuleAvailable(language.translate("moduleBrowser.googleDocs.title")),
            "Google Docs Share Module is not available");
        return this;
    }

    public ModuleBrowserPage assertModuleTableHeadersAreDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(titleTableHeader), "Title header is displayed");
        assertTrue(getBrowser().isElementDisplayed(descriptionTableHeader), "Description header is displayed");
        assertTrue(getBrowser().isElementDisplayed(versionTableHeader), "Version header is displayed");
        return this;
    }

    public String getModelManagerPageTitle()
    {
        return getBrowser().waitUntilElementVisible(modelManagerPageTitle).getText();
    }
}
