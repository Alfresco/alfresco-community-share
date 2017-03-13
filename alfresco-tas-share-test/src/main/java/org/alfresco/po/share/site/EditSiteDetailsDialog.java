package org.alfresco.po.share.site;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author Laura.Capsa
 */
@Primary
@PageObject
public class EditSiteDetailsDialog extends ShareDialog
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @FindBy(css = "input[id$='instance-title']")
    private TextInput titleInput;

    @FindBy(css = "label[for$='instance-title']")
    private WebElement titleLabel;

    @FindBy(css = "textarea[id$='instance-description']")
    private WebElement descriptionInput;

    @FindBy(css = "label[for$='instance-description']")
    private WebElement descriptionLabel;

    @FindBy(css = ".first>label[for$='instance-isPublic']")
    private WebElement visibilityLabel;

    @FindBy(css = "input[id$='instance-isPublic']")
    private WebElement publicVisibilityRadioButton;

    @FindBy(css = "span[id$='instance-public-help-text']")
    private WebElement publicVisibilityDescription;

    @FindBy(css = "input[id$='instance-isModerated']")
    private WebElement moderatedVisibilityRadioButton;

    @FindBy(css = "span[id$='instance-moderated-help-text']")
    private WebElement moderatedVisibilityDescription;

    @FindBy(css = "input[id$='instance-isPrivate']")
    private WebElement privateVisibilityRadioButton;

    @FindBy(css = "span[id$='instance-private-help-text']")
    private WebElement privateVisibilityDescription;

    @RenderWebElement
    @FindBy(css = "span[id*='ok-button'] button")
    private WebElement save;

    @FindBy(css = "span[id*='cancel-button'] button")
    private Button cancel;

    @Autowired
    SiteDashboardPage siteDashboard;

    /**
     * Navigate to site
     * Opens Edit Details dialog
     *
     * @param siteId
     */
    public void navigateToDialog(String siteId)
    {
        siteDashboard.navigate(siteId);
        siteDashboard.clickSiteConfiguration();
        siteDashboard.clickOptionInSiteConfigurationDropDown("Edit Site Details", this);
    }

    public void typeDetails(String title, String description)
    {
        titleInput.clear();
        titleInput.sendKeys(title);

        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    public boolean isTitleInputDisplayed()
    {
        return titleInput.isDisplayed();
    }

    public String getTitleLabel()
    {
        return titleLabel.getText();
    }

    public String getTitleInputText()
    {
        return titleInput.getText();
    }

    public boolean isDescriptionInputDisplayed()
    {
        return browser.isElementDisplayed(descriptionInput);
    }

    public String getDescriptionLabel()
    {
        return descriptionLabel.getText();
    }

    public String getVisibilityLabel()
    {
        return visibilityLabel.getText();
    }

    public boolean isPublicVisibilityRadioButtonDisplayed()
    {
        return browser.isElementDisplayed(publicVisibilityRadioButton);
    }

    public boolean isPublicVisibilitySelected()
    {
        return publicVisibilityRadioButton.isSelected();
    }

    public String getPublicVisibilityDescription()
    {
        return publicVisibilityDescription.getText();
    }

    public boolean isModeratedVisibilityRadioButtonDisplayed()
    {
        return browser.isElementDisplayed(moderatedVisibilityRadioButton);
    }

    public boolean isModeratedVisibilitySelected()
    {
        return moderatedVisibilityRadioButton.isSelected();
    }

    public String getModeratedVisibilityDescription()
    {
        return moderatedVisibilityDescription.getText();
    }

    public boolean isPrivateVisibilityRadioButtonDisplayed()
    {
        return browser.isElementDisplayed(privateVisibilityRadioButton);
    }

    public boolean isPrivateVisibilitySelected()
    {
        return privateVisibilityRadioButton.isSelected();
    }

    public String getPrivateVisibilityDescription()
    {
        return privateVisibilityDescription.getText();
    }

    public void selectPublicVisibility()
    {
        publicVisibilityRadioButton.click();
    }

    public void selectModeratedVisibility()
    {
        moderatedVisibilityRadioButton.click();
    }

    public void selectPrivateVisibility()
    {
        privateVisibilityRadioButton.click();
    }

    public boolean isSaveButtonDisplayed()
    {
        return browser.isElementDisplayed(save);
    }

    public void clickSaveButton()
    {
        save.click();
    }

    public boolean isCancelButtonDisplayed()
    {
        return cancel.isDisplayed();
    }

    public void clickCancelButton()
    {
        cancel.click();
    }
}