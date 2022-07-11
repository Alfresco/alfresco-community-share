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
        waitUntilElementIsVisible(identifierField);
        return isElementDisplayed(identifierField);
    }

    public String getIdentifierText()
    {
        return waitUntilElementIsVisible(identifierField).getText();
    }

    public boolean isDisplayNameInputFieldDisplayed()
    {
        waitUntilElementIsVisible(displayNameInputField);
        return isElementDisplayed(displayNameInputField);
    }

    public void editDisplayName(String newDisplayName)
    {
        waitUntilElementIsVisible(displayNameInputField);
        clearAndType(displayNameInputField, newDisplayName);
    }

    public HtmlPage clickSaveChangesButton(HtmlPage page)
    {
        clickElement(saveChangesButton);
        findElement(saveChangesButton).click();
        return page.renderedPage();
    }

    public HtmlPage clickCancelButton(HtmlPage page)
    {
        clickElement(cancelButton);
        findElement(cancelButton).click();
        return page.renderedPage();
    }

    public String getEditGroupPageTitle()
    {
        return getElementText(pageTitle);
    }

    public boolean isSaveChangesButtonDisplayed()
    {
        waitUntilElementIsVisible(saveChangesButton);
        return isElementDisplayed(saveChangesButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        waitUntilElementIsVisible(cancelButton);
        return isElementDisplayed(cancelButton);
    }
}
