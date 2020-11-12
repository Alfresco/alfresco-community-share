package org.alfresco.po.share.user.admin.adminTools;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.Theme;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;

public class ApplicationPage extends AdminToolsPage
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

    private final UploadFileDialog uploadDialog;

    public ApplicationPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
        this.uploadDialog = new UploadFileDialog(browser);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    public UploadFileDialog clickUpload()
    {
        getBrowser().waitUntilElementVisible(uploadButton).click();
        return (UploadFileDialog) uploadDialog.renderedPage();
    }

    public ApplicationPage clickApply()
    {
        getBrowser().waitUntilElementClickable(applyButton).click();
        return (ApplicationPage) this.renderedPage();
    }

    public ApplicationPage uploadImage()
    {
        String testFilePath = testDataFolder + "alfrescoLogo.png";
        clickUpload().uploadFileAndRenderPage(testFilePath, this);
        return clickApply();
    }

    public boolean isAlfrescoDefaultImageDisplayed()
    {
        return  getBrowser().isElementDisplayed(defaultAlfrescoImage);
    }

    public ApplicationPage resetImageToDefault()
    {
        //click Reset button
        getBrowser().waitUntilElementClickable(resetButton).click();
        getBrowser().waitUntilElementVisible(defaultAlfrescoImage);
        return this;
    }

    public ApplicationPage selectTheme(Theme theme)
    {
        Select themeOptions = new Select(getBrowser().findElement(themeDropdown));
        themeOptions.selectByValue(theme.getSelectValue());
        clickApply();
        getBrowser().waitUntilElementVisible(By.xpath(String.format(bodyTheme, theme.getSelectValue())));
        getBrowser().refresh();
        return (ApplicationPage) this.renderedPage();
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
        Assert.assertTrue(isThemeOptionSelected(theme), "Theme is selected");
        return this;
    }

    public ApplicationPage assertBodyContainsTheme(Theme theme)
    {
        Assert.assertTrue(doesBodyContainTheme(theme), "New theme is applied");
        return this;
    }

    public String checkText()
    {
        return getBrowser().findElement(mainText).getText();
    }
}