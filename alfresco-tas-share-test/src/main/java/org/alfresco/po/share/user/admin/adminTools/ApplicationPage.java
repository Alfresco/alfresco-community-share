package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Utils.testDataFolder;
import static org.alfresco.common.Wait.WAIT_3;
import static org.alfresco.common.Wait.WAIT_30;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.enums.Theme;
import org.alfresco.po.share.UploadFileDialog;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

@Slf4j
public class ApplicationPage extends SharePage2<ApplicationPage>
{
    private final By themeDropdown = By.cssSelector("select#console-options-theme-menu");
    private final By applyButton = By.cssSelector("button[id$='default-apply-button-button']");
    private final By applyButtonAfterHover = By.cssSelector("div.apply span[class*='submit-button-hover']");
    private final By defaultAlfrescoImage = By.xpath("//img[contains(@id, '_default-logoimg') and contains(@src, '/app-logo-48.png')]");
    private final By mainText = By.cssSelector( ".info");
    private final By resetButton = By.cssSelector("button[id$='default-reset-button-button']");
    private final By uploadButton = By.cssSelector("form[id*=admin-console] button[id*=upload-button-button]");
    private final String bodyTheme = "//body[@id = 'Share' and contains(@class, 'skin-%s')]";

    public ApplicationPage(ThreadLocal<WebDriver> webdriver)
    {
        super(webdriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    public ApplicationPage navigate()
    {
        super.navigate();
        try
        {
            waitUntilElementIsVisible(uploadButton);
        }
        catch (TimeoutException e)
        {
            log.error("Failed to navigate to Application Page");
            super.navigate();
        }
        return this;
    }

    public ApplicationPage assertAdminApplicationPageIsOpened()
    {
        log.info("Assert Application Admin Tools page is opened");
        assertTrue(getCurrentUrl().contains(getRelativePath()), "Application page is not opened");
        return this;
    }

    public UploadFileDialog clickUpload()
    {
        clickElement(uploadButton);
        return new UploadFileDialog(webDriver);
    }

    public ApplicationPage clickApply()
    {
        log.info("Click Apply button");
        try
        {
            WebElement apply = waitUntilElementIsVisible(applyButton);
            mouseOver(apply, 3000);
            clickElement(applyButtonAfterHover, 3000);
            if (defaultProperties.getBrowserName().equals("chrome"))
            {
                waitUntilElementDeletedFromDom(applyButton, defaultProperties.getExplicitWait(), 3000);
            }
        }
        catch (StaleElementReferenceException | NoSuchElementException staleElementReferenceException)
        {
            log.info("Apply button is not attached to DOM");
            waitInSeconds(WAIT_3.getValue());
            mouseOverViaJavascript(findElement(applyButton));
            clickElement(applyButtonAfterHover, 3000);
            waitUntilElementDeletedFromDom(applyButton, defaultProperties.getExplicitWait(), 3000);
        }
        return this;
    }

    public ApplicationPage assertDefaultAlfrescoImageIsNotDisplayed()
    {
        log.info("Assert default Alfresco image is not displayed");
        assertFalse(isElementDisplayed(defaultAlfrescoImage), "Default Alfresco image is displayed");
        return this;
    }

    public ApplicationPage uploadImage()
    {
        String testFilePath = testDataFolder + "alfrescoLogo.png";
        clickUpload().uploadFile(testFilePath, this);
        return this;
    }

    public ApplicationPage assertDefaultAlfrescoImageIsDisplayed()
    {
        log.info("Assert default Alfresco image is displayed");
        assertTrue(isElementDisplayed(defaultAlfrescoImage), "Default Alfresco image is not displayed");

        return this;
    }

    public ApplicationPage resetImageToDefault()
    {
        log.info("Reset image to default");
        waitUntilElementIsVisibleWithRetry(resetButton, WAIT_30.getValue());
        try
        {
            clickElement(resetButton);
        }
        catch (StaleElementReferenceException e)
        {
            navigate();
            clickElement(resetButton);
        }
        if(!isElementDisplayed(defaultAlfrescoImage))
        {
            log.error("Failed to click Reset button");
            clickElement(resetButton);
        }
        return this;
    }

    public ApplicationPage selectTheme(Theme theme)
    {
        log.info("Select theme: {}", theme.name);
        WebElement themeElement = waitUntilElementIsVisible(themeDropdown);
        mouseOver(themeElement);
        waitUntilElementIsVisible(themeElement);
        Select themeOptions = new Select(themeElement);
        themeOptions.selectByValue(theme.getSelectValue());

        return this;
    }

    public boolean isThemeOptionSelected(Theme theme)
    {
        Select themeOptions = new Select(findElement(themeDropdown));
        List<WebElement> options = themeOptions.getOptions();
        return options.stream().anyMatch(value ->
                value.getAttribute("value").contains(theme.getSelectValue()) && value.isSelected());
    }

    public boolean doesBodyContainTheme(Theme theme)
    {
        WebElement appliedTheme = waitUntilElementIsVisible(By.xpath(String.format(bodyTheme, theme.getSelectValue())));
        return isElementDisplayed(appliedTheme);
    }

    public ApplicationPage assertThemeOptionIsSelected(Theme theme)
    {
        log.info("Assert theme option selected is {}", theme.name);
        assertTrue(isThemeOptionSelected(theme), "Theme is selected");
        return this;
    }

    public ApplicationPage assertBodyContainsTheme(Theme theme)
    {
        log.info("Assert share body contains theme {}", theme.name);
        assertTrue(doesBodyContainTheme(theme), "New theme is applied");
        return this;
    }

    public String checkText()
    {
        return getElementText(mainText);
    }
}