package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ManageRulesPage extends SiteCommon<ManageRulesPage>
{
    private DocumentLibraryPage documentLibraryPage;
    private EditRulesPage editRulesPage;
    private SelectDestinationDialog selectDestinationDialog;

    private final By contentRule = By.cssSelector("li.rules-list-item.selected.dnd-draggable");
    private final By title = By.cssSelector(".rules-header .rules-title");
    private final By noRulesText = By.xpath(".//*[contains(@class, 'dialog-options')]/*[1]");
    private final By createRulesLink = By.cssSelector(".dialog-option a[href*='rule-edit']");
    private final By createRulesDescription = By.xpath("(.//div[@class='dialog-option']/div)[1]");
    private final By linkToRuleSetLink = By.cssSelector("a[id*='linkToRuleSet']");
    private final By linkToRuleSetDescription = By.xpath("(.//div[@class='dialog-option']/div)[2]");
    private final By inheritButton = By.cssSelector("button[id*='inheritButton']");
    private final By breadcrumbList = By.cssSelector("span.folder-link a");
    private final By inheritRulesMessage = By.cssSelector("#message .bd");

    public ManageRulesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        editRulesPage = new EditRulesPage(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return webElementInteraction.getElementText(title);
    }

    public String getNoRulesText()
    {
        if (webElementInteraction.isElementDisplayed(noRulesText))
            return webElementInteraction.getElementText(noRulesText);
        return "'No rules defined for folder' header missing";
    }

    public String getCreateRulesDescription()
    {
        if (webElementInteraction.isElementDisplayed(createRulesDescription))
            return webElementInteraction.findElement(createRulesDescription).getText();
        return "Create Rules description isn't displayed.";
    }

    public String getCreateRulesLinkText()
    {
        if (webElementInteraction.isElementDisplayed(createRulesLink))
            return webElementInteraction.findElement(createRulesLink).getText();
        return "'Create Rules' link isn't displayed.";
    }

    public EditRulesPage clickCreateRules()
    {
        webElementInteraction.findElement(createRulesLink).click();
        return new EditRulesPage(webDriver);
    }

    public String getLinkToRuleSetLinkText()
    {
        if (webElementInteraction.isElementDisplayed(linkToRuleSetLink))
            return webElementInteraction.findElement(linkToRuleSetLink).getText();
        return "'Link to Rule Set' Link isn't displayed.";
    }

    public String getLinkToRuleSetDescription()
    {
        if (webElementInteraction.isElementDisplayed(linkToRuleSetLink))
            return webElementInteraction.findElement(linkToRuleSetDescription).getText();
        return "'Link to Rule Set' isn't displayed.";
    }

    public SelectDestinationDialog clickLinkToRuleSet()
    {
        webElementInteraction.findElement(linkToRuleSetLink).click();
        return new SelectDestinationDialog(webDriver);
    }

    public String getInheritButtonText()
    {
        if (webElementInteraction.isElementDisplayed(inheritButton))
            return webElementInteraction.findElement(inheritButton).getText();
        return "'Inherit Rules' button isn't displayed.";
    }

    public ManageRulesPage clickInheritButton()
    {
        webElementInteraction.waitUntilElementIsVisible(inheritButton);
        webElementInteraction.clickElement(inheritButton);
        webElementInteraction.waitUntilElementIsVisible(inheritRulesMessage);
        webElementInteraction.waitUntilElementDisappears(inheritRulesMessage);

        return this;
    }

    public boolean isContentRuleDisplayed()
    {
        return webElementInteraction.isElementDisplayed(contentRule);
    }
}