package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class ManageRulesPage extends SiteCommon<ManageRulesPage>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    EditRulesPage editRulesPage;

    @Autowired
    SelectDestinationDialog selectDestinationDialog;

    @RenderWebElement
    @FindBy (css = ".rules-header .rules-title")
    private WebElement title;

    @FindBy (xpath = ".//*[contains(@class, 'dialog-options')]/*[1]")
    private WebElement noRulesText;

    @FindBy (css = ".dialog-option a[href*='rule-edit']")
    private WebElement createRulesLink;

    @FindBy (xpath = "(.//div[@class='dialog-option']/div)[1]")
    private WebElement createRulesDescription;

    @FindBy (css = "a[id*='linkToRuleSet']")
    private WebElement linkToRuleSetLink;

    @FindBy (xpath = "(.//div[@class='dialog-option']/div)[2]")
    private WebElement linkToRuleSetDescription;

    @RenderWebElement
    @FindBy (css = "button[id*='inheritButton']")
    private WebElement inheritButton;

    @FindBy (css = "li.rules-list-item.selected.dnd-draggable")
    public WebElement contentRule;

    @FindBy (css = "span.folder-link a")
    private List<WebElement> breadcrumbList;

    private By inheritRulesMessage = By.cssSelector("#message .bd");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return title.getText();
    }

    public String getNoRulesText()
    {
        if (browser.isElementDisplayed(noRulesText))
            return noRulesText.getText();
        return "'No rules defined for folder' header missing";
    }

    public String getCreateRulesDescription()
    {
        if (browser.isElementDisplayed(createRulesDescription))
            return createRulesDescription.getText();
        return "Create Rules description isn't displayed.";
    }

    public String getCreateRulesLinkText()
    {
        if (browser.isElementDisplayed(createRulesLink))
            return createRulesLink.getText();
        return "'Create Rules' link isn't displayed.";
    }

    public EditRulesPage clickCreateRules()
    {
        createRulesLink.click();
        return (EditRulesPage) editRulesPage.renderedPage();
    }

    public String getLinkToRuleSetLinkText()
    {
        if (browser.isElementDisplayed(linkToRuleSetLink))
            return linkToRuleSetLink.getText();
        return "'Link to Rule Set' Link isn't displayed.";
    }

    public String getLinkToRuleSetDescription()
    {
        if (browser.isElementDisplayed(linkToRuleSetLink))
            return linkToRuleSetDescription.getText();
        return "'Link to Rule Set' isn't displayed.";
    }

    public SelectDestinationDialog clickLinkToRuleSet()
    {
        linkToRuleSetLink.click();
        return (SelectDestinationDialog) selectDestinationDialog.renderedPage();
    }

    public String getInheritButtonText()
    {
        if (browser.isElementDisplayed(inheritButton))
            return inheritButton.getText();
        return "'Inherit Rules' button isn't displayed.";
    }

    public ManageRulesPage clickInheritButton()
    {
        browser.waitUntilElementVisible(inheritButton).click();
        browser.waitUntilElementVisible(inheritRulesMessage);
        browser.waitUntilElementDisappears(inheritRulesMessage, 10);

        return (ManageRulesPage) this.renderedPage();
    }

    public boolean isContentRuleDisplayed()
    {
        return browser.isElementDisplayed(contentRule);
    }

    public DocumentLibraryPage returnTo(String location)
    {
        browser.findFirstElementWithValue(breadcrumbList, location).click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }
}