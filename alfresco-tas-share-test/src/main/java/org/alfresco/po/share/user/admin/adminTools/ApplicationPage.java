package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.Theme;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.exception.PageRenderTimeException;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ApplicationPage extends SharePage2<ApplicationPage>
{
    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;

    @RenderWebElement
    private By themeDropdown = By.cssSelector("select#console-options-theme-menu");
    private By applyButton = By.cssSelector("div.apply button[id$='_default-apply-button-button']");
    private By defaultAlfrescoImage = By.xpath("//img[contains(@id, '_default-logoimg') and contains(@src, '/images/app-logo-48.png')]");
    private By mainText = By.cssSelector( ".info");
    @RenderWebElement
    private By resetButton = By.cssSelector("button[id$='reset-button-button']");
    private By uploadButton = By.cssSelector("form[id*=admin-console] button[id*=upload-button-button]");
    private String bodyTheme = "//body[@id = 'Share' and contains(@class, 'skin-%s')]";

    public ApplicationPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    public ApplicationPage assertAdminApplicationPageIsOpened()
    {
        LOG.info("Assert Application Admin Tools page is opened");
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Application page is not opened");
        return this;
    }

    public UploadFileDialog clickUpload()
    {
        getBrowser().waitUntilElementVisible(uploadButton).click();
        return (UploadFileDialog) new UploadFileDialog(browser).renderedPage();
    }

    public ApplicationPage clickApply()
    {
        WebElement apply = getBrowser().waitUntilElementVisible(applyButton);
        getBrowser().mouseOver(apply);
        apply.click();
        return this;
    }

    public ApplicationPage assertDefaultAlfrescoImageIsNotDisplayed()
    {
        LOG.info("Assert default Alfresco image is not displayed");
        assertFalse(getBrowser().isElementDisplayed(defaultAlfrescoImage), "Default Alfresco image is displayed");
        return this;
    }

    public ApplicationPage uploadImage()
    {
        String testFilePath = testDataFolder + "alfrescoLogo.png";
        clickUpload().uploadFileAndRenderPage(testFilePath, this);
        return clickApply();
    }

    public ApplicationPage assertDefaultAlfrescoImageIsDisplayed()
    {
        LOG.info("Assert default Alfresco image is displayed");
        getBrowser().waitUntilElementVisible(defaultAlfrescoImage);
        assertTrue(getBrowser().isElementDisplayed(defaultAlfrescoImage), "Default Alfresco image is not displayed");
        return this;
    }

    public boolean isAlfrescoDefaultImageDisplayed()
    {
        return getBrowser().isElementDisplayed(defaultAlfrescoImage);
    }

    public ApplicationPage resetImageToDefault()
    {
        LOG.info("Reset image to default");
        WebElement reset = getBrowser().waitUntilElementVisible(resetButton);
        getBrowser().clickJS(reset);
        getBrowser().waitUntilElementVisible(defaultAlfrescoImage);
        return this;
    }

    public ApplicationPage selectTheme(Theme theme)
    {
        LOG.info("Select theme: {}", theme.name);
        WebElement themeElement = getBrowser().waitUntilElementVisible(themeDropdown);
        getBrowser().mouseOver(themeElement);
        Select themeOptions = new Select(themeElement);
        themeOptions.selectByValue(theme.getSelectValue());
        clickApply();
        getBrowser().waitUntilElementVisible(By.xpath(String.format(bodyTheme, theme.getSelectValue())));

        return this;
    }

    public boolean isThemeOptionSelected(Theme theme)
    {
        Select themeOptions = new Select(getBrowser().findElement(themeDropdown));
        List<WebElement> options = themeOptions.getOptions();
        return options.stream().anyMatch(value ->
            value.getAttribute("value").contains(theme.getSelectValue()) && value.isSelected());
    }

    public boolean doesBodyContainTheme(Theme theme)
    {
        By themeToBeFound = By.xpath(String.format(bodyTheme, theme.getSelectValue()));
        return getBrowser().isElementDisplayed(themeToBeFound);
    }

    public ApplicationPage assertThemeOptionIsSelected(Theme theme)
    {
        LOG.info("Assert theme option selected is {}", theme.name);
        assertTrue(isThemeOptionSelected(theme), "Theme is selected");
        return this;
    }

    public ApplicationPage assertBodyContainsTheme(Theme theme)
    {
        LOG.info("Assert share body contains theme {}", theme.name);
        assertTrue(doesBodyContainTheme(theme), "New theme is applied");
        return this;
    }

    public String checkText()
    {
        return getBrowser().findElement(mainText).getText();
    }
}