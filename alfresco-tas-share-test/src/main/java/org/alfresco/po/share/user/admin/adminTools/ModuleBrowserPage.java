package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ModuleBrowserPage extends SharePage2<ModuleBrowserPage>
{
    private final By titleTableHeader = By.id("titleTableHeader");
    private final By descriptionTableHeader = By.id("descriptionTableHeader");
    private final By versionTableHeader = By.id("versionTableHeader");
    private final By modulesList = By.cssSelector("tr[id*=alfresco_lists_views_layouts_Row]");
    private final By modelManagerPageTitle = By.id("HEADER_TITLE");

    public ModuleBrowserPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/module-package";
    }

    public ModuleBrowserPage assertModuleBrowserPageIsOpened()
    {
        LOG.info("Assert Module browser page is opened");
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "Module browser page is not opened");
        return this;
    }

    public WebElement selectModuleName(String moduleName)
    {
        webElementInteraction.waitUntilElementIsDisplayedWithRetry(modulesList,1, WAIT_10.getValue());
        return webElementInteraction.findFirstElementWithValue(modulesList, moduleName);
    }

    public boolean isModuleAvailable(String moduleName)
    {
        return webElementInteraction.isElementDisplayed(selectModuleName(moduleName));
    }

    public ModuleBrowserPage assertGoogleDocsModuleIsPresent()
    {
        assertTrue(isModuleAvailable(language.translate("moduleBrowser.googleDocs.title")),
            "Google Docs Share Module is not available");
        return this;
    }

    public ModuleBrowserPage assertModuleTableHeadersAreDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(titleTableHeader), "Title header is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(descriptionTableHeader), "Description header is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(versionTableHeader), "Version header is displayed");
        return this;
    }

    public String getModelManagerPageTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(modelManagerPageTitle).getText();
    }
}
