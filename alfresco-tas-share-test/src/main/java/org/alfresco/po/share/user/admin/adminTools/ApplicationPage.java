package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.FileInput;

import java.io.File;
import java.util.List;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class ApplicationPage extends SharePage<CategoryManagerPage> implements AccessibleByMenuBar
{
    @Autowired
    private Toolbar toolbar;

    @Autowired
    AdminToolsPage adminToolsPage;

    @RenderWebElement
    @FindBy(xpath = "//div[@class='title' and text()='Administration and Management Console']")
    private WebElement applicationDivTitle;

    @FindBy(className="dnd-file-selection-button")
    private FileInput fileInput;

    public enum Theme
    {
        YELLOW_THEME("yellowTheme"),
        GREEN_THEME("greenTheme"),
        BLUE_THEME("default"),
        LIGHT_THEME("lightTheme"),
        GOOGLE_DOCS_THEME("gdocs"),
        HIGH_CONTRAST_THEME("hcBlack");

        private String theme;

        Theme(String theme) {
            this.theme = theme;
        }

        public String getTheme() {
            return this.theme;
        }
    }

    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;
    private By uploadButton = By.xpath("//div[@class = 'logo']/../div//button[contains(@id , 'upload-button-button')]");
    private By resetButton = By.cssSelector("button[id$='reset-button-button']");
    private By applyButton = By.cssSelector("button[id$='apply-button-button']");
    private By defaultAlfrescoImage = By.xpath("//img[contains(@id, '_default-logoimg') and contains(@src, '/images/app-logo-48.png')]");
    private By themeDropdown = By.cssSelector("select[id$='options-theme-menu']");

    @SuppressWarnings("unchecked")
    @Override
    public ApplicationPage navigateByMenuBar()
    {
        toolbar.clickAdminTools();
        browser.waitInSeconds(5);
        adminToolsPage.navigateToNodeFromToolsPanel("Application");
        return (ApplicationPage) renderedPage();
    }

    @Override
    public String getRelativePath() { return "share/page/console/admin-console/application"; }

    public void uploadImage()
    {
        String testFile = "alfrescoLogo.png";
        String testFilePath = testDataFolder + testFile;

        //click Upload button
        browser.findElement(uploadButton).click();

        //upload the new image
        fileInput.setFileToUpload(testFilePath);

        //click 'Apply' button to save changes
        browser.waitUntilElementClickable(applyButton, 4).click();
    }

    public boolean isAlfrescoDefaultImageDisplayed()
    {
        int counter = 1;
        while (counter < 4)
        {
            if (!browser.isElementDisplayed(defaultAlfrescoImage))
            {
                counter++;
                browser.refresh();
            }
            else { break ;}
        }
        return browser.isElementDisplayed(defaultAlfrescoImage);
    }

    public void resetImageToDefault()
    {
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                //click Reset button
                browser.findElement(resetButton).click();

                //click 'Apply' button to save the reset
                browser.waitUntilElementClickable(applyButton, 4).click();
                browser.waitInSeconds(9);
                break;
            }
            catch (NoSuchElementException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(6);
            }
        }
    }

    public void selectTheme(Theme theme)
    {
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                Select select = new Select(browser.findElement(themeDropdown));
                browser.waitInSeconds(2);
                select.selectByValue(theme.getTheme());

                //click 'Apply' button to save the theme
                browser.waitUntilElementClickable(applyButton, 4).click();
                browser.waitInSeconds(9);
                break;
            }
            catch (NoSuchElementException | StaleElementReferenceException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(6);
            }
        }
    }

    public boolean isThemeOptionPresent(Theme theme)
    {
        Select select = new Select(browser.findElement(themeDropdown));
        browser.waitInSeconds(2);
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(theme.getTheme()))
                return true;
        return false;
    }

    public boolean isThemeOptionSelected(Theme theme)
    {
        Select select = new Select(browser.findElement(themeDropdown));
        browser.waitInSeconds(2);
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(theme.getTheme()) && value.isSelected())
                return true;
        return false;
    }

    public boolean doesBodyContainTheme(Theme theme)
    {
        By themeToBeFound = By.xpath("//body[@id = 'Share' and contains(@class, 'skin-" + theme.getTheme() + "')]");
        int counter = 1;
        while (counter < 4)
        {
            if (!browser.isElementDisplayed(themeToBeFound))
            {
               counter++;
               browser.refresh();
            }
            else { break; }
        }
        return browser.isElementDisplayed(themeToBeFound);
    }
}
