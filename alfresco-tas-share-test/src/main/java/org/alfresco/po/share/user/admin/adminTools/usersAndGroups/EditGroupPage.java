package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditGroupPage extends SharePage2<EditGroupPage>
{
    @RenderWebElement
    private final By saveChangesButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");
    @RenderWebElement
    private final By cancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");
    private final By displayNameInputField = By.cssSelector("input[id$='_default-update-displayname']");
    private final By identifierField = By.cssSelector("span[id$='_default-update-shortname']");
    private final By pageTitle = By.cssSelector("div[id$='_default-update'] div.title");

    public EditGroupPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public boolean isIdentifierFieldDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(identifierField);
        return webElementInteraction.isElementDisplayed(identifierField);
    }

    public String getIdentifierText()
    {
        return webElementInteraction.waitUntilElementIsVisible(identifierField).getText();
    }

    public boolean isDisplayNameInputFieldDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(displayNameInputField);
        return webElementInteraction.isElementDisplayed(displayNameInputField);
    }

    public void editDisplayName(String newDisplayName)
    {
        webElementInteraction.waitUntilElementIsVisible(displayNameInputField);
        webElementInteraction.clearAndType(displayNameInputField, newDisplayName);
    }

    public HtmlPage clickSaveChangesButton(HtmlPage page)
    {
        webElementInteraction.clickElement(saveChangesButton);
        webElementInteraction.findElement(saveChangesButton).click();
        return page.renderedPage();
    }

    public HtmlPage clickCancelButton(HtmlPage page)
    {
        webElementInteraction.clickElement(cancelButton);
        webElementInteraction.findElement(cancelButton).click();
        return page.renderedPage();
    }

    public String getEditGroupPageTitle()
    {
        return webElementInteraction.getElementText(pageTitle);
    }

    public boolean isSaveChangesButtonDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(saveChangesButton);
        return webElementInteraction.isElementDisplayed(saveChangesButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(cancelButton);
        return webElementInteraction.isElementDisplayed(cancelButton);
    }
}
