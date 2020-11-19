package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

public class ManageRulesPage extends SiteCommon<ManageRulesPage>
{
    private DocumentLibraryPage documentLibraryPage;
    private EditRulesPage editRulesPage;

    //@Autowired
    SelectDestinationDialog selectDestinationDialog;

    public By contentRule = By.cssSelector("li.rules-list-item.selected.dnd-draggable");
    @RenderWebElement
    private By title = By.cssSelector(".rules-header .rules-title");
    private By noRulesText = By.xpath(".//*[contains(@class, 'dialog-options')]/*[1]");
    private By createRulesLink = By.cssSelector(".dialog-option a[href*='rule-edit']");
    private By createRulesDescription = By.xpath("(.//div[@class='dialog-option']/div)[1]");
    private By linkToRuleSetLink = By.cssSelector("a[id*='linkToRuleSet']");
    private By linkToRuleSetDescription = By.xpath("(.//div[@class='dialog-option']/div)[2]");
    @RenderWebElement
    private By inheritButton = By.cssSelector("button[id*='inheritButton']");
    private By breadcrumbList = By.cssSelector("span.folder-link a");
    private By inheritRulesMessage = By.cssSelector("#message .bd");

    public ManageRulesPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        documentLibraryPage = new DocumentLibraryPage(browser);
        editRulesPage = new EditRulesPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return getBrowser().findElement(title).getText();
    }

    public String getNoRulesText()
    {
        if (getBrowser().isElementDisplayed(noRulesText))
            return getBrowser().findElement(noRulesText).getText();
        return "'No rules defined for folder' header missing";
    }

    public String getCreateRulesDescription()
    {
        if (getBrowser().isElementDisplayed(createRulesDescription))
            return getBrowser().findElement(createRulesDescription).getText();
        return "Create Rules description isn't displayed.";
    }

    public String getCreateRulesLinkText()
    {
        if (getBrowser().isElementDisplayed(createRulesLink))
            return getBrowser().findElement(createRulesLink).getText();
        return "'Create Rules' link isn't displayed.";
    }

    public EditRulesPage clickCreateRules()
    {
        getBrowser().findElement(createRulesLink).click();
        return (EditRulesPage) editRulesPage.renderedPage();
    }

    public String getLinkToRuleSetLinkText()
    {
        if (getBrowser().isElementDisplayed(linkToRuleSetLink))
            return getBrowser().findElement(linkToRuleSetLink).getText();
        return "'Link to Rule Set' Link isn't displayed.";
    }

    public String getLinkToRuleSetDescription()
    {
        if (getBrowser().isElementDisplayed(linkToRuleSetLink))
            return getBrowser().findElement(linkToRuleSetDescription).getText();
        return "'Link to Rule Set' isn't displayed.";
    }

    public SelectDestinationDialog clickLinkToRuleSet()
    {
        getBrowser().findElement(linkToRuleSetLink).click();
        return (SelectDestinationDialog) selectDestinationDialog.renderedPage();
    }

    public String getInheritButtonText()
    {
        if (getBrowser().isElementDisplayed(inheritButton))
            return getBrowser().findElement(inheritButton).getText();
        return "'Inherit Rules' button isn't displayed.";
    }

    public ManageRulesPage clickInheritButton()
    {
        getBrowser().waitUntilElementVisible(inheritButton).click();
        getBrowser().waitUntilElementVisible(inheritRulesMessage);
        getBrowser().waitUntilElementDisappears(inheritRulesMessage, 10);

        return (ManageRulesPage) this.renderedPage();
    }

    public boolean isContentRuleDisplayed()
    {
        return getBrowser().isElementDisplayed(contentRule);
    }

    public DocumentLibraryPage returnTo(String location)
    {
        getBrowser().findFirstElementWithValue(breadcrumbList, location).click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }
}