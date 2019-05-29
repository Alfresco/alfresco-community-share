package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 12/12/2016.
 */
@PageObject
public class EditGroupPage extends SharePage<EditGroupPage>
{
    @RenderWebElement
    private By saveChangesButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");

    @RenderWebElement
    private By cancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");

    @FindBy (css = "input[id$='_default-update-displayname']")
    private WebElement displayNameInputField;

    @FindBy (css = "span[id$='_default-update-shortname']")
    private WebElement identifierField;

    @FindBy (css = "div[id$='_default-update'] div.title")
    private WebElement pageTitle;

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public boolean isIdentifierFieldDisplayed()
    {
        browser.waitUntilElementVisible(identifierField);
        return browser.isElementDisplayed(identifierField);
    }

    public String getIdentifierText()
    {
        browser.waitUntilElementVisible(identifierField);
        return identifierField.getText();
    }

    public boolean isDisplayNameInputFieldDisplayed()
    {
        browser.waitUntilElementVisible(displayNameInputField);
        return browser.isElementDisplayed(displayNameInputField);
    }

    public void editDisplayName(String newDisplayName)
    {
        browser.waitUntilElementVisible(displayNameInputField);
        displayNameInputField.clear();
        displayNameInputField.sendKeys(newDisplayName);
    }

    public HtmlPage clickSaveChangesButton(HtmlPage page)
    {
        browser.waitUntilElementClickable(saveChangesButton, 5L);
        browser.findElement(saveChangesButton).click();
        return page.renderedPage();
    }

    public HtmlPage clickCancelButton(HtmlPage page)
    {
        browser.waitUntilElementClickable(cancelButton, 5L);
        browser.findElement(cancelButton).click();
        return page.renderedPage();
    }

    public String getEditGroupPageTitle()
    {
        return pageTitle.getText();
    }

    public boolean isSaveChangesButtonDisplayed()
    {
        browser.waitUntilElementVisible(saveChangesButton);
        return browser.isElementDisplayed(saveChangesButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        browser.waitUntilElementVisible(cancelButton);
        return browser.isElementDisplayed(cancelButton);
    }

}
