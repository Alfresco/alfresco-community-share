package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class EditGroupPage extends SharePage2<EditGroupPage>
{
    @RenderWebElement
    private By saveChangesButton = By.cssSelector("button[id$='_default-updategroup-save-button-button']");
    @RenderWebElement
    private By cancelButton = By.cssSelector("button[id$='_default-updategroup-cancel-button-button']");
    private By displayNameInputField = By.cssSelector("input[id$='_default-update-displayname']");
    private By identifierField = By.cssSelector("span[id$='_default-update-shortname']");
    private By pageTitle = By.cssSelector("div[id$='_default-update'] div.title");

    public EditGroupPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public boolean isIdentifierFieldDisplayed()
    {
        getBrowser().waitUntilElementVisible(identifierField);
        return getBrowser().isElementDisplayed(identifierField);
    }

    public String getIdentifierText()
    {
        return getBrowser().waitUntilElementVisible(identifierField).getText();
    }

    public boolean isDisplayNameInputFieldDisplayed()
    {
        getBrowser().waitUntilElementVisible(displayNameInputField);
        return getBrowser().isElementDisplayed(displayNameInputField);
    }

    public void editDisplayName(String newDisplayName)
    {
        getBrowser().waitUntilElementVisible(displayNameInputField);
        clearAndType(displayNameInputField, newDisplayName);
    }

    public HtmlPage clickSaveChangesButton(HtmlPage page)
    {
        getBrowser().waitUntilElementClickable(saveChangesButton, 5L);
        getBrowser().findElement(saveChangesButton).click();
        return page.renderedPage();
    }

    public HtmlPage clickCancelButton(HtmlPage page)
    {
        getBrowser().waitUntilElementClickable(cancelButton, 5L);
        getBrowser().findElement(cancelButton).click();
        return page.renderedPage();
    }

    public String getEditGroupPageTitle()
    {
        return getElementText(pageTitle);
    }

    public boolean isSaveChangesButtonDisplayed()
    {
        getBrowser().waitUntilElementVisible(saveChangesButton);
        return getBrowser().isElementDisplayed(saveChangesButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        getBrowser().waitUntilElementVisible(cancelButton);
        return getBrowser().isElementDisplayed(cancelButton);
    }
}
