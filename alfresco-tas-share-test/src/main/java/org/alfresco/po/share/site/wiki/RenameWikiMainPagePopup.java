package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class RenameWikiMainPagePopup extends ShareDialog
{
    @Autowired
    WikiMainPage wikiMainPage;

    @RenderWebElement
    @FindBy (css = "[id$=default-renameTo]")
    private WebElement renameInput;

    @RenderWebElement
    @FindBy (css = "[id$=default-rename-save-button-button]")
    private WebElement savePageMainNameButton;

    @FindBy (css = "[class=container-close]")
    private WebElement closePopup;

    public void clearWikiTitle()
    {
        renameInput.clear();
    }

    public void typeNewMainPageName(String newName)
    {
        renameInput.sendKeys(newName);
    }

    public void closeRenamePopup()
    {
        browser.waitUntilElementVisible(closePopup);
        closePopup.click();
    }

    public WikiMainPage clickOnSaveButton()
    {
        savePageMainNameButton.click();
        browser.waitInSeconds(2);
        return (WikiMainPage) wikiMainPage.renderedPage();
    }
}
