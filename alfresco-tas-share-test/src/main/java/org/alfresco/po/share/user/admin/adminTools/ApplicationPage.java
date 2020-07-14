package org.alfresco.po.share.user.admin.adminTools;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.Theme;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Select;

@PageObject
public class ApplicationPage extends AdminToolsPage
{
    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;

    @RenderWebElement
    @FindBy (css = "select#console-options-theme-menu")
    private Select themeDropdown;

    @FindBy (css = "div.apply button[id$='_default-apply-button-button']")
    private Button applyButton;

    @FindBy (xpath = "//img[contains(@id, '_default-logoimg') and contains(@src, '/images/app-logo-48.png')]")
    private WebElement defaultAlfrescoImage;

    @FindBy (css = ".info")
    private WebElement mainText;

    @RenderWebElement
    @FindBy (css = "button[id$='reset-button-button']")
    private Button resetButton;

    @RenderWebElement
    @FindBy (css = "form[id*=admin-console] button[id*=upload-button-button]")
    private Button uploadButton;

    private final UploadFileDialog uploadDialog;

    public ApplicationPage(UploadFileDialog uploadDialog) {
        this.uploadDialog = uploadDialog;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    public UploadFileDialog clickUpload()
    {
        browser.waitUntilElementVisible(uploadButton).click();
        return (UploadFileDialog) uploadDialog.renderedPage();
    }

    public ApplicationPage clickApply()
    {
        browser.waitUntilElementClickable(applyButton).click();
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
        return browser.isElementDisplayed(defaultAlfrescoImage);
    }

    public ApplicationPage resetImageToDefault()
    {
        //click Reset button
        browser.waitUntilElementClickable(resetButton).click();
        return (ApplicationPage) this.renderedPage();
    }

    public ApplicationPage selectTheme(Theme theme)
    {
        themeDropdown.selectByValue(theme.getSelectValue());
        clickApply();
        this.refresh();
        return (ApplicationPage) this.renderedPage();
    }

    public boolean isThemeOptionSelected(Theme theme)
    {
        List<WebElement> options = themeDropdown.getOptions();
        return options.stream().anyMatch(value ->
            value.getAttribute("value").contains(theme.getSelectValue()) && value.isSelected());
    }

    public boolean doesBodyContainTheme(Theme theme)
    {
        By themeToBeFound = By.xpath("//body[@id = 'Share' and contains(@class, 'skin-" + theme.getSelectValue() + "')]");
        return browser.isElementDisplayed(themeToBeFound);
    }

    public String checkText()
    {
        return mainText.getText();
    }
}