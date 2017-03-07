package org.alfresco.po.share.site;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CreateSiteDialog extends EditSiteDetailsDialog implements AccessibleByMenuBar
{
    @FindBy(css = "label[for='alfresco-createSite-instance-shortName']")
    private WebElement urlNameLabel;

    @FindBy(id = "alfresco-createSite-instance-shortName")
    private TextInput urlNameInput;

    @FindBy(xpath = ".//*[@id='alfresco-createSite-instance-form']/div[2]//span[@class='help']")
    private WebElement urlNameDescription;

    @FindBy(xpath = ".//*[@id='alfresco-createSite-instance-form']/div[1]/div[2]")
    private WebElement nameMandatory;

    @FindBy(xpath = ".//*[@id='alfresco-createSite-instance-form']/div[2]/div[2]")
    private WebElement urlMandatory;

    @FindBy(css = "input[id*='createSite-instance-title']")
    private WebElement titleInput;

    @RenderWebElement
    @FindBy(css = "div[id*='createSite-instance-dialog']")
    private WebElement createSiteDialog;

    @FindBy(css = "button[id*='createSite-instance-ok-button']")
    private WebElement createSiteOKbutton;

    @FindBy(css = "button[id*='createSite-instance-cancel-button']")
    private WebElement createSiteCANCELbutton;

    private By urlErrorMessage = By.cssSelector("div[id='prompt_c'] div[class='bd']");
    private By urlErrorOkButton = By.cssSelector("div[class='ft'] button");

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    ToolbarSitesMenu toolbarSitesMenu;

    @SuppressWarnings("unchecked")
    @Override
    public CreateSiteDialog navigateByMenuBar()
    {
        toolbarSitesMenu.clickCreateSite();
        return (CreateSiteDialog) renderedPage();
    }

    public CreateSiteDialog navigateFromDashlet()
    {
        mySitesDashlet.clickCreateSiteButton();
        return (CreateSiteDialog) renderedPage();
    }

    public boolean isUrlNameInputDisplayed()
    {
        return urlNameInput.isDisplayed();
    }

    public void clickCreateButton() { createSiteOKbutton.click(); }

    public String getUrlNameInputText()
    {
        return urlNameInput.getText();
    }

    public String getUrlNameLabel()
    {
        return urlNameLabel.getText();
    }

    public String getUrlNameDescriptionText()
    {
        return urlNameDescription.getText();
    }

    public void clearUrlNameInput()
    {
        urlNameInput.clear();
    }

    public boolean isUrlNameInputEmpty()
    {
        return urlNameInput.getText().isEmpty();
    }

    public void typeUrlName(String urlName)
    {
        urlNameInput.sendKeys(urlName);
    }

    public void clickOkFromErrorPopup()
    {
        browser.waitUntilElementVisible(urlErrorOkButton).click();
    }

    public String getUrlErrorMessage()
    {
        browser.waitInSeconds(1);
        browser.waitUntilElementVisible(urlErrorMessage);
        return browser.findFirstDisplayedElement(urlErrorMessage).getText();
    }

    public boolean isTitleMandatory()
    {
        if (nameMandatory.getText().contains("*"))
            return true;
        return false;
    }

    public boolean isUrlNameMandatory()
    {
        if (urlMandatory.getText().contains("*"))
            return true;
        return false;
    }

    public void typeName(String urlName)
    {
        titleInput.clear();
        titleInput.sendKeys(urlName);
    }

    public String getNameInputText() { return titleInput.getAttribute("value"); }
}