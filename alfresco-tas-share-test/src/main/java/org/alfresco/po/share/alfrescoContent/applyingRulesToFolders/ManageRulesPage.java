package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class ManageRulesPage extends SiteCommon<ManageRulesPage>
{
    private final By title = By.cssSelector(".rules-header .rules-title");
    private final By noRulesText = By.xpath(".//*[contains(@class, 'dialog-options')]/*[1]");
    private final By createRulesLink = By.cssSelector(".dialog-option a[href*='rule-edit']");
    private final By createRulesDescription = By.xpath("(.//div[@class='dialog-option']/div)[1]");
    private final By linkToRuleSetLink = By.cssSelector("a[id*='linkToRuleSet']");
    private final By linkToRuleSetDescription = By.xpath("(.//div[@class='dialog-option']/div)[2]");
    private final By inheritButton = By.cssSelector("button[id*='inheritButton']");
    private final By inheritRulesMessage = By.cssSelector("#message .bd");
    private final By inheritRuleInfoMessage = By.cssSelector("div .rules-info span");
    private final By createRule = By.cssSelector(".dialog-options > div:nth-child(2) a");

    public ManageRulesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override public String getRelativePath()
    {
        return String.format("share/page/site/%s/folder-rules", getCurrentSiteName());
    }

    public String getRuleTitle()
    {
        return getElementText(title);
    }

    public String getNoRulesText()
    {
        if (isElementDisplayed(noRulesText))
        {
            return getElementText(noRulesText);
        }
        return "'No rules defined for folder' header missing";
    }

    public String getCreateRulesDescription()
    {
        if (isElementDisplayed(createRulesDescription))
        {
            return findElement(createRulesDescription).getText();
        }
        return "Create Rules description isn't displayed.";
    }

    public String getCreateRulesLinkText()
    {
        if (isElementDisplayed(createRulesLink))
        {
            return findElement(createRulesLink).getText();
        }
        return "'Create Rules' link isn't displayed.";
    }

    public EditRulesPage openCreateNewRuleForm()
    {
        clickElement(createRulesLink);
        return new EditRulesPage(webDriver);
    }

    public String getLinkToRuleSetLinkText()
    {
        if (isElementDisplayed(linkToRuleSetLink))
        {
            return findElement(linkToRuleSetLink).getText();
        }
        return "'Link to Rule Set' Link isn't displayed.";
    }

    public String getLinkToRuleSetDescription()
    {
        if (isElementDisplayed(linkToRuleSetLink))
        {
            return findElement(linkToRuleSetDescription).getText();
        }
        return "'Link to Rule Set' isn't displayed.";
    }

    public SelectDestinationDialog clickLinkToRuleSet()
    {
        findElement(linkToRuleSetLink).click();
        return new SelectDestinationDialog(webDriver);
    }

    public String getInheritButtonText()
    {
        if (isElementDisplayed(inheritButton))
        {
            return findElement(inheritButton).getText();
        }
        return "'Inherit Rules' button isn't displayed.";
    }

    public String getInheritRuleInfoMsgText()
    {
        if(isElementDisplayed(inheritRuleInfoMessage))
        {
            return findElement(inheritRuleInfoMessage).getText();
        }
        return "Inherit Rule Info Message 'This folder inherits Rules from its parent folder(s)' not Displayed";
    }

    public ManageRulesPage clickInheritButton()
    {
        waitUntilElementIsVisible(inheritButton);
        clickElement(inheritButton);
        waitUntilElementIsVisible(inheritRulesMessage);
        waitUntilElementDisappears(inheritRulesMessage);
        return this;
    }

    public ManageRulesPage assertNoRulesTextIsEqual(String expectedNoRuleText)
    {
        log.info("Verify No Rules are defined for this folder {}", expectedNoRuleText);
        assertEquals(getNoRulesText(), expectedNoRuleText, String.format("Expected 'No Rules are defined for this folder' not match %s ", expectedNoRuleText));
        return this;
    }

    public ManageRulesPage assertInheritButtonTextEquals(String btnText)
    {
        log.info("Verify Inherit button text {}", btnText);
        assertEquals(getInheritButtonText(), btnText,
            String.format("Inherit button text not matched %s ", btnText));
        return this;
    }

    public ManageRulesPage assertInheritRuleInfoMessageEquals(String infoMsg)
    {
        log.info("Verify Inherit rule info message {}", infoMsg);
        assertEquals(getInheritRuleInfoMsgText(), infoMsg, String.format("Info Message 'This folder inherits Rules from its parent folder(s)' not matched %s ", infoMsg));
        return this;
    }

    public ManageRulesPage assertRuleTitleEquals(String folderName)
    {
        log.info("Verify Rule Title on manage rule page{}", folderName+": Rules");
        assertEquals(getRuleTitle(), folderName+": Rules", String.format("Rule Title '"+getRuleTitle()+"' not matched %s ", folderName+": Rules"));
        return this;
    }

    public ManageRulesPage assertCreateRuleLinkTextEquals(String linkText)
    {
        log.info("Verify create rule link text {}", linkText);
        assertEquals(getCreateRulesLinkText(), linkText, String.format("Create Rule link text should be 'Create Rules(s)' not matched %s ", linkText));
        return this;
    }

    public ManageRulesPage assertCreateRuleDescriptionTextEquals(String descriptionTxt)
    {
        log.info("Verify create rule description text {}", descriptionTxt);
        assertEquals(getCreateRulesDescription(), descriptionTxt, String.format("Create Rule Description text should be 'Define your own rules from scratch for this folder(s)' not matched %s ", descriptionTxt));
        return this;
    }

    public ManageRulesPage assertLinkToRuleSetLinkTextEquals(String linkText)
    {
        log.info("Verify 'Link to Rule Set' link text {}", linkText);
        assertEquals(getLinkToRuleSetLinkText(), linkText, String.format("Link to Rule Set link text should be 'Link to Rule Set(s)' not matched %s ", linkText));
        return this;
    }

    public ManageRulesPage assertLinkToRuleSetDescriptionEquals(String description)
    {
        log.info("Verify 'Link to Rule Set' description {}", description);
        assertEquals(getLinkToRuleSetDescription(), description, String.format("Link to Rule Set description should be 'Reuse an existing rule set defined for another folder(s)' not matched %s ", description));
        return this;
    }

    public void clickCreateRules()
    {
        waitUntilElementIsVisible(createRule);
        findElement(createRule).click();
    }
}