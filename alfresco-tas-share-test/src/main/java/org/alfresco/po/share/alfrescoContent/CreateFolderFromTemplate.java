package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * @author Bogdan.Simion
 */
@PageObject
public class CreateFolderFromTemplate
{
    @Autowired
    @Qualifier("webBrowserInstance")
    protected WebBrowser browser;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @FindBy(css = ".create-content button")
    protected WebElement createContentButton;

    @FindBy(css = "a.yuimenuitemlabel.yuimenuitemlabel-hassubmenu span")
    protected List<WebElement> createFolderFromTemplateLink;

    @FindBy(css = "ul.first-of-type span[title=\"\"]")
    protected List<WebElement> folderTemplates;

    private By createFolderFromTemplatePopupLocator = By.cssSelector(".set");

    @FindBy(css = "input[title='Name']")
    protected WebElement nameField;

    @FindBy(css = "button[name='-']")
    protected List<WebElement> buttons;

    @FindBy(css = "span.message")
    protected WebElement message;

    @FindBy(css = "h3.filename a")
    protected List<WebElement> contentNameLink;

    @FindBy(css = ".action-set.detailed a[alt='More...']")
    protected WebElement moreLink;

    @FindBy(css = ".folder-manage-rules a")
    protected WebElement manageRulesLink;

    @FindBy(css = "div.document-manage-repo-permissions a")
    protected WebElement managePermissionsLinkRepository;

    @FindBy(css = "a[title='Manage Permissions']")
    protected WebElement managePermissionsLinkDocumentLibrary;
    
    @FindBy(css = "span.light")
    protected WebElement selectedAspectsFileName;
    
    @FindBy(css = ".list-right td[headers*='name'] h4")
    protected WebElement currentlySelectedAspects;
    
    @FindBy(css = "a[title='Manage Aspects']")
    protected WebElement manageAspectsDocumentLibrary;
    
    @FindBy(css = "table.ygtvtable.ygtvdepth1.ygtv-expanded span.ygtvlabel")
    protected WebElement table;

    @FindBy(css = "select.config-name option[value='inbound']")
    protected WebElement defineRuleWhenSelectBox;

    @FindBy(css = ".ygtvitem.selected .ygtvitem span")
    protected WebElement folderLinkFromExplorerPanel;

    @FindBy(css = "h3.filename a.filter-change")
    protected List<WebElement> subfoldersLinks;
   
    @FindBy(css = ".permissions-list button[aria-haspopup='true']")
    protected WebElement permissionsButton;
    
    @FindBy(css = "tbody.yui-dt-data  td.yui-dt-col-role a")
    protected List<WebElement> folderPermissionRolesDropdown;
           
    @FindBy(css = "div.crumb.documentDroppable a")
    public List<WebElement> breadCrumbsSubfoldersLinks;

    @FindBy(css = ".dialog-option>a")
    public List<WebElement> createRulesLink;

    @FindBy(css = ".rule-config.action.edit select.config-name")
    public WebElement performActionSelectBox;

    @FindBy(css = ".add-user-group button")
    public WebElement addUserPermissionButton;

    @FindBy(css = "select[title='aspect-name']")
    public WebElement selectBoxAspectName;

    @FindBy(css = "input[name='title']")
    public WebElement ruleNameInput;

    @FindBy(css = ".search-bar input")
    public WebElement userSearchInput;

    @FindBy(css = ".search-bar button")
    public WebElement userSearchButton;

    @FindBy(css = "textarea[name='description']")
    public WebElement ruleDescriptonInput;

    @FindBy(css = ".yui-button.yui-push-button.alf-primary-button button")
    public WebElement saveManagePermissionsButton;

//    @FindBy(css = "a[title='Manage Aspects']")
//    public WebElement manageAspectsLink;

    @FindBy(css = "tr.yui-dt-rec.yui-dt-first.yui-dt-even a[title='Add']")
    public WebElement addAspectButton;

    @FindBy(css = ".text div")
    public WebElement tooltipErrorMessage;

    @FindBy(css = "li.rules-list-item.selected.dnd-draggable")
    public WebElement contentRule;

    @FindBy(css = "input[maxlength='255']")
    public WebElement createFolderFromTemplatePopupNameInput;

    @FindBy(css = "input[name='prop_cm_title']")
    public WebElement titleInput;

    @FindBy(css = "textarea[name='prop_cm_description']")
    public WebElement descriptionInput;

    @FindBy(css = "div[id*='directPermissions'] td[headers*='displayName'] div")
    public List<WebElement> setPermissionsListUserDisplayName;
    
    @FindBy(css = "div[id*='directPermissions'] td[headers*='role'] span")
    public List<WebElement> setPermissionsListUserRole;
    
    @FindBy(css = ".nav-bar .label a")
    public WebElement breadCrumbNavBarSpaceTemplates;

    @FindBy(css = " span.yui-button.yui-submit-button.alf-primary-button button")
    public List<WebElement> applyChangesForAspectsButton;

    @FindBy(css = ".yui-dt-liner button")
    public List<WebElement> addUserForPermissionButton;

    @FindBy(css = ".main-buttons button")
    public List<WebElement> mainButtons;

    @FindBy(css = "span.folder-link a")
    public WebElement breadcrumbLinks;

    private By manageAspectsLink = By.cssSelector("a[title='Manage Aspects']");
    
    public void clickCreateContentButton()
    {
        createContentButton.click();
    }

    public void clickOnFolderTemplate(String folderName)
    {
        for (int i = 0; i < folderTemplates.size(); i++)
        {
            if (folderTemplates.get(i).getText().equalsIgnoreCase(folderName))
                folderTemplates.get(i).click();
        }
    }

    /**
     * Click Save or Cancel button for Create Folder From Template popup
     */
    public void clickButton(String button)
    {
        for (int i = 0; i < buttons.size(); i++)
        {
            if (buttons.get(i).getText().equalsIgnoreCase(button))
                buttons.get(i).click();
        }
    }

    /**
     * Click "Create" or "Create and Create Another" or "Cancel" button from Create Rule Page
     */
    public void clickMainButton(String button)
    {
        for (WebElement mainButton : mainButtons)
        {
            if (mainButton.getText().equals(button))
                mainButton.click();
        }
    }

    public void hoverOverCreateFolderFromTemplateLink()
    {
        for (int i = 0; i < createFolderFromTemplateLink.size(); i++)
        {
            browser.mouseOver(createFolderFromTemplateLink.get(i));
        }
    }

    public boolean isListOfAvailableTemplatesDisplayed()
    {
        return (folderTemplates.size() > 0);
    }

    public boolean isCreateFolderFromTemplatePopupDisplayed()
    {
        return browser.isElementDisplayed(createFolderFromTemplatePopupLocator);
    }

    public String getTemplateFolderNameFieldValue()
    {
        return nameField.getAttribute("value");
    }

    public boolean isFolderCreatedMessageDisplayed()
    {
        browser.waitUntilElementClickable(message, 5);
        return message.isDisplayed();
    }

    public String getFolderNameFromExplorerPanel()
    {
        return folderLinkFromExplorerPanel.getText();
    }

    public void clickOnTheCreatedFolderFromBrowsingPane()
    {
        folderLinkFromExplorerPanel.click();
    }

    public boolean checkIfSubfolderExists(String subfolderName)
    {
        List<String> subfolders = documentLibraryPage.getFoldersList();

        if (subfolders.contains(subfolderName))
            return true;
        else
            return false;
    }

    public void clickMoreLinkForAFolder(String subfolderName)
    {
        documentLibraryPage.mouseOverFolder(subfolderName);
        browser.waitInSeconds(3);
        documentLibraryPage.clickMore();
    }

    public void clickManageRulesForAFolder(String subfolderName)
    {
        clickMoreLinkForAFolder(subfolderName);
        manageRulesLink.click();
    }

    public void clickManagePermissionsForAFolder(String subfolderName)
    {
        clickMoreLinkForAFolder(subfolderName);
        browser.waitInSeconds(1);
        managePermissionsLinkRepository.click();
    }

    public void clickManageAspectsForAFolder(String subfolderName)
    {
        clickMoreLinkForAFolder(subfolderName);
        browser.waitInSeconds(3);
        browser.findDisplayedElementsFromLocator(manageAspectsLink).get(0).click();
//        manageAspectsLink.click();
    }

    public String getBreadCrumbPath()
    {
        String[] path = new String[breadCrumbsSubfoldersLinks.size()];

        for (int i = 0; i < path.length; i++)
        {
            path[i] = breadCrumbsSubfoldersLinks.get(i).getText();
            if (path[i].equalsIgnoreCase(" "))
                path[i] = " > ";
        }
        StringBuilder processedPath = new StringBuilder();
        for (int i = 0; i < path.length; i++)
            processedPath.append(path[i]);

        return processedPath.toString();
    }

    public void clickCreateRules()
    {
        createRulesLink.get(0).click();
    }

    public void selectValue(WebElement selectBox, String Value)
    {
        Select clickThis = new Select(selectBox);
        clickThis.selectByValue(Value);
    }

    public void selectActionType(String action)
    {
        selectValue(performActionSelectBox, action);
    }

    public void selectTypeOfAspect(String aspect)
    {
        selectValue(selectBoxAspectName, aspect);
    }

    public void insertRuleName(String ruleName)
    {
        ruleNameInput.sendKeys(ruleName);
    }

    public void insertRuleDescription(String ruleDescription)
    {
        ruleDescriptonInput.sendKeys(ruleDescription);
    }

    public void navigateToSpaceTemplatesFolder()
    {
        repositoryPage.navigate();
        documentLibraryPage.clickFolderFromExplorerPanel("Data Dictionary");
        browser.waitInSeconds(1);
        documentLibraryPage.clickFolderFromExplorerPanel("Space Templates");
    }

    public void selectAspectAndSubmit(String aspectActionValue, String aspectValue, String buttonToClick)
    {
        selectActionType(aspectActionValue);
        selectTypeOfAspect(aspectValue);
        clickMainButton(buttonToClick);
    }

    public void clickAddUserOrGroupPermissions()
    {
        addUserPermissionButton.click();
    }

    public void insertUserNameAndClickSearch(String userName)
    {
        userSearchInput.sendKeys(userName);
        userSearchButton.click();
    }

    public void clickAddUserForPermissionButton()
    {
        addUserForPermissionButton.get(0).click();
    }

    public void clickSaveManagePermissionsButton()
    {
        saveManagePermissionsButton.click();
    }

    public void clickAddAspectButton()
    {
        addAspectButton.click();
    }

    public void clickApplyChangesForAspectsButton()
    {
        for (WebElement button : applyChangesForAspectsButton)
        {
            if (button.getText().equalsIgnoreCase("Apply changes"))
                button.click();
        }
    }

    public void clickCreateRules(String folderName)
    {
        navigateToSpaceTemplatesFolder();
        clickManageRulesForAFolder(folderName);
        clickCreateRules();
        browser.waitInSeconds(1);
    }

    public void setPermissions(String folderName, String userName)
    {
        navigateToSpaceTemplatesFolder();
        clickManagePermissionsForAFolder(folderName);
        clickAddUserOrGroupPermissions();
        insertUserNameAndClickSearch(userName);
        browser.waitInSeconds(2);
        clickAddUserForPermissionButton();
        setCoordinatorRole();
        clickSaveManagePermissionsButton();
    }

    public void setAspects(String folderName)
    {
        clickManageAspectsForAFolder(folderName);
        browser.waitInSeconds(2);
        clickAddAspectButton();
        browser.waitInSeconds(2);
        clickApplyChangesForAspectsButton();
    }

    public void insertNameInput(String nameInput)
    {
        createFolderFromTemplatePopupNameInput.clear();
        createFolderFromTemplatePopupNameInput.sendKeys(nameInput);
    }

    public void insertTitleInput(String titleText)
    {
        titleInput.clear();
        titleInput.sendKeys(titleText);
    }

    public void insertDescriptionInput(String descriptionText)
    {
        descriptionInput.clear();
        descriptionInput.sendKeys(descriptionText);
    }

    public boolean isTooltipErrorMessageDisplayed()
    {
        return tooltipErrorMessage.isDisplayed();
    }

    public boolean isContentRuleDisplayed()
    {
        return browser.isElementDisplayed(contentRule);
    }

    public void setCoordinatorRole()
    {
        permissionsButton.click();
        for(WebElement role : folderPermissionRolesDropdown)
            if(role.getText().equalsIgnoreCase("Coordinator"))
                   role.click();
    }
    
    public void returnToDocumentsPage()
    {
         breadcrumbLinks.click();
    }
    
    public void clickManagePermissionsDocumentLibrary(String folderName)
    {
        clickMoreLinkForAFolder(folderName);
        managePermissionsLinkDocumentLibrary.click();       
    }
    
    public void clickManageAspectsDocumentLibrary(String folderName)
    {
        clickMoreLinkForAFolder(folderName);
        manageAspectsDocumentLibrary.click();       
    }
  
    public String getPermissionsListUserDisplayName()
    {
        return setPermissionsListUserDisplayName.get(0).getText();
    }  
    
    public String getPermissionsListUserRole()
    {
        return setPermissionsListUserRole.get(0).getText();
    } 
    
    public String getAspectsForFileName()
    {
        return selectedAspectsFileName.getText();             
    }  
    
    public String getCurrentlySelectedAspectsForFileName()
    {
        return currentlySelectedAspects.getText();
    }
    
}
