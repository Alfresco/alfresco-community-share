package org.alfresco.po.share.site;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Primary
@PageObject
public class DocumentLibraryPage extends SiteCommon<DocumentLibraryPage>
{
    @Autowired
    private UploadFileDialog uploadDialog;

    @Autowired
    private NewContentDialog newContentDialog;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @FindAll(@FindBy(css = "a.filter-link"))
    private List<WebElement> documentsFilterOptions;

    @RenderWebElement
    @FindBy(css = "div[id$='default-navBar']")
    private WebElement navigationBar;

    @RenderWebElement
    @FindBy(css = "div[id$='default-paginatorBottom']")
    private WebElement paginator;

    @RenderWebElement
    @FindBy(css = "div[id$='_default-dl-body']")
    private WebElement docListContainer;

    @FindBy(css = ".documents[id$='_default-documents']")
    private WebElement documentList;

    @FindBy(css = "button[id*='createContent']")
    private WebElement createButton;

    public By createContentMenu = By.cssSelector("div[id*='_default-createContent-menu'].visible");

    @FindBy(css = "[id$='default-fileUpload-button-button']")
    protected WebElement uploadButton;

    @FindBy(css = ".folder-file")
    private WebElement folderLink;

    @FindBy(css = "div[id$='default-options-menu'] span")
    private List<WebElement> optionsList;

    private By optionsMenuDropDown = By.cssSelector("div[id*='default-options-menu'].visible");
    private By displayedOptionsListBy = By.xpath("//div[contains(@id, 'default-options-menu')]//li[not(contains(@class, 'hidden'))]");

    @RenderWebElement
    @FindBy(css = "button[id$='default-options-button-button']")
    protected WebElement optionsMenu;

    @FindBy(xpath = "//span[contains(text(), 'More...')]")
    protected WebElement moreLink;

    @FindBy(css = ".hideFolders")
    protected WebElement hideFoldersMenuOption;

    @FindAll(@FindBy(css = ".filter-change:nth-child(1)"))
    private List<WebElement> foldersList;

    private By filesList = By.cssSelector(".filename a[href*='document-details']");
    private By documentLibraryItemsList = By.cssSelector("[class*='data'] tr");

    @FindAll(@FindBy(css = ".crumb .folder"))
    private List<WebElement> breadcrumbList;

    @FindBy(css = ".crumb .label a")
    private WebElement breadcumbCurrentFolder;

    @FindBy(css = "button[id*='folderUp']")
    private WebElement folderUpButton;

    private By contentNameInputField = By.cssSelector("input[id*='form-field']");

    @FindBy(css = ".insitu-edit a")
    private List<WebElement> buttonsFromRenameContent;

    @FindBy(css = ".inlineTagEditAutoCompleteWrapper input")
    private WebElement editTagInputField;

    @FindBy(css = "form[class='insitu-edit'] a")
    private List<WebElement> editTagButtons;

    @FindBy(css = ".inlineTagEditAutoCompleteWrapper input")
    private WebElement tagToBeEdited;

    @FindBy(css = "div[class ='google-map']")
    private WebElement googleMap;

    @FindBy(css = "div[id*='_default-info'] div[class='thumbnail'] a[href*='document-details']")
    private WebElement googleMapPopUp;

    @FindBy(css = "div[class ='status'] img[title ='Geolocation metadata available']")
    private WebElement geolocationMetadataIcon;

    @FindBy(css = "div[class='info-banner'] a")
    private WebElement lockedByUser;

    @FindBy(css = "span[class ='setDefaultView']")
    private WebElement setDefaultView;

    @FindBy(css = "span[class ='removeDefaultView']")
    private WebElement removeDefaultView;

    @FindBy(css = "div[class ='alf-gallery-item']")
    private WebElement galleryViewItem;

    @FindBy(css = "div[id*='_default-filmstrip-nav-handle']")
    private WebElement downArrowPointer;

    @FindBy(css = "div[class ='alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-next']")
    private WebElement rightArrowPointer;

    @FindBy(css = "div[class ='alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-prev']")
    private WebElement leftArrowPointer;

    @FindBy(css = "button[id*='_default-sortAscending-button-button']")
    private WebElement sortButton;

    @FindBy(css = "button[id*='_default-sortField-button-button']")
    private WebElement sortByFieldButton;

    @FindBy(css = "span.yui-pg-current")
    private WebElement currentPage;

    @FindAll(@FindBy(css = ".documentDroppable .ygtvlabel"))
    private List<WebElement> explorerPanelDocumentsList;

    @FindBy(css = "a[title^='Locate']")
    private WebElement locateFolder;


    private WebElement selectViewInOptions(String viewName)
    {
        return browser.findElement(By.xpath("//div[contains(@id, '_default-options-menu')]//ul[@class= 'first-of-type']//span[text()='" + viewName + "']"));
    }

    private WebElement findItemInCarrouselFilmstripView(String contentName)
    {
        return browser.findElement(By.xpath("//div[contains(@class, 'alf-filmstrip-nav-item-thumbnail')]//div[text()='" + contentName + "']"));
    }

    private By renameIcon = By.cssSelector(".filename span[class='insitu-edit']");
    private By linkToFolderLocator = By.cssSelector(".filename [href*='FdocumentLibrary']");
    private By uploadNewVersion = By.cssSelector("a[title='Upload New Version']");
    private By moreMenuSelector = By.cssSelector("div[id*='onActionShowMore'] a span");
    public By editTagSelector = By.cssSelector("td .detail span[class='insitu-edit']:first-child");
    private By noTagsSelector = By.cssSelector("td[class*='fileName'] .detail .item .faded");
    private By contentTagsSelector = By.cssSelector(".item .tag-link");
    private By inlineEditTagsSelector = By.cssSelector(".inlineTagEditTag span");
    private By removeTagIconSelector = By.cssSelector(".inlineTagEditTag img[src*='delete-item-off']");

    private By contentNameSelector = By.cssSelector(".filename a");
    private By checkBoxSelector = By.cssSelector("tbody[class='yui-dt-data'] input[id*='checkbox']");
    private By favoriteLink = By.className("favourite-action");
    private By actionsSet = By.cssSelector(".action-set a span");

    private By categoriesDetails = By.cssSelector("div.detail span.category");
    private By infoBanner = By.cssSelector("div[class='info-banner']");
    private By titleSelector = By.cssSelector("td .title");
    private By descriptionSelector = By.cssSelector("td .detail:nth-child(3) span");
    private By downloadButtonSelector = By.cssSelector("a[title='Download']");
    private By downloadAsZipSelector = By.cssSelector("a[title='Download as Zip']");
    public By descriptionTagFilter = By.cssSelector("div.message span.more");
    private By commentButton = By.cssSelector("a.comment");
    protected By likeButton = By.cssSelector("a.like-action");

    public enum DocumentsFilters
    {
        All("All Documents", "All Documents in the Document Library"),
        EditingMe("I'm Editing", "Documents I'm Editing(working copies)"),
        EditingOthers("Others are Editing", "Documents Others are Editing(working copies)"),
        RecentlyModified("Recently Modified", "Documents Recently Modified"),
        RecentlyAdded("Recently Added", "Documents Added Recently"),
        Favorites("My Favorites", "My Favorite Documents and Folders");

        public final String title;
        public final String header;

        DocumentsFilters(String title, String header)
        {
            this.title = title;
            this.header = header;
        }
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/documentlibrary", getCurrentSiteName());
    }

    public boolean isUploadButtonDisplayed()
    {
        return browser.isElementDisplayed(browser.waitUntilElementVisible(uploadButton));
    }

    public boolean isContentNameDisplayed(String contentName)
    {
        boolean state = false;

        try
        {
            WebElement webElement = selectDocumentLibraryItemRow(contentName);
            getBrowser().waitUntilWebElementIsDisplayedWithRetry(selectDocumentLibraryItemRow(contentName), 5);
            state = browser.isElementDisplayed(webElement);
        }
        catch (Exception ex)
        {
            state = false;
        }
        return state;
    }

    /**
     * Verify presence of content with exact value for it's name
     */
    public boolean isContentWithExactValuePresent(String content)
    {
        this.renderedPage();
        WebElement webElement = selectDocLibItemWithExactValue(content);
        return browser.isElementDisplayed(webElement);
    }

    public UploadFileDialog clickUpload()
    {
        browser.waitUntilElementVisible(uploadButton).click();
        return (UploadFileDialog) uploadDialog.renderedPage();

    }

    public DocumentLibraryPage uploadNewImage(String pathToPhoto)
    {
        clickUpload().uploadFile(pathToPhoto);
        browser.waitInSeconds(2);
        browser.refresh();
        return (DocumentLibraryPage) this.renderedPage();

    }

    public boolean isDocumentListDisplayed()
    {
        return browser.isElementDisplayed(documentList);
    }

    /**
     * Verify presence of "Options" menu
     *
     * @return true if displayed
     */
    public boolean isOptionsMenuDisplayed()
    {
        return browser.isElementDisplayed(optionsMenu);
    }

    public boolean isHideFoldersMenuOptionDisplayed()
    {
        boolean elementDisplayed;
        optionsMenu.click();
        elementDisplayed = browser.isElementDisplayed(hideFoldersMenuOption);
        optionsMenu.click();
        return elementDisplayed;
    }

    public DocumentLibraryPage selectViewFromOptionsMenu(String view)
    {
        optionsMenu.click();
        browser.selectOptionFromFilterOptionsList(view, optionsList);
        return (DocumentLibraryPage) this.renderedPage();
    }

    public List<String> getAllOptionsText()
    {
        List<String> optionsText = new ArrayList<>();
        List<WebElement> options = browser.findElements(displayedOptionsListBy);
        for (WebElement option : options)
        {
            optionsText.add(option.getText());
        }
        return optionsText;
    }

    /**
     * This method is used to get the list of folders name
     *
     * @return foldersName
     */
    public List<String> getFoldersList()
    {
        browser.waitInSeconds(1);
        List<String> foldersName = new ArrayList<>();
        for (WebElement folder : foldersList)
        {
            foldersName.add(folder.getText());
        }
        return foldersName;
    }

    public WebElement selectDocumentLibraryItemRow(String documentItem)
    {
        browser.waitInSeconds(3);
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 6);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithValue(itemsList, documentItem);
    }

    /**
     * Select document library item matching exact value of content name
     */

    public WebElement selectDocLibItemWithExactValue(String item)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 2);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithExactValue(itemsList, item);
    }

    /**
     * Open any folder from Document Library
     *
     * @param folderName to be clicked on
     */
    public DocumentLibraryPage clickOnFolderName(String folderName)
    {
        WebElement folderElement = selectDocumentLibraryItemRow(folderName);
        Parameter.checkIsMandotary("Folder", folderElement);
        folderElement.findElement(contentNameSelector).click();
        browser.waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return (DocumentLibraryPage) this.renderedPage();
    }

    /**
     * Mouse over a content name link
     *
     * @param contentItem content item's name link to be hovered
     */
    public void mouseOverContentItem(String contentItem)
    {
        WebElement contentItemElement = selectDocumentLibraryItemRow(contentItem);
        Parameter.checkIsMandotary("Content item", contentItemElement);
        WebElement contentItemName = contentItemElement.findElement(contentNameSelector);
        browser.mouseOver(contentItemName);
        browser.waitUntilElementHasAttribute(contentItemElement, "class", "yui-dt-highlighted");
    }

    /**
     * This method is used to get the list of files name
     *
     * @return filesName
     */
    public List<String> getFilesList()
    {
        browser.waitUntilElementIsDisplayedWithRetry(filesList, 6);
        List<String> filesName = new ArrayList<>();
        for (WebElement file : browser.findElements(filesList))
        {
            filesName.add(file.getText());
        }
        return filesName;
    }

    public DocumentDetailsPage clickOnFile(String file)
    {
        WebElement fileElement = selectDocumentLibraryItemRow(file);
        Parameter.checkIsMandotary("File", fileElement);
        fileElement.findElement(contentNameSelector).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Click on 'Upload New Version' action for a file
     */
    public UploadFileDialog clickUploadNewVersion(String fileName)
    {
        int counter = 0;
        while (counter < 2)
        {
            try
            {
                mouseOverContentItem(fileName);
                clickMoreMenu(fileName);
                selectDocumentLibraryItemRow(fileName).findElement(uploadNewVersion).click();
            }
            catch (NoSuchElementException | TimeoutException e)
            {
                LOG.error("Action not found:" + e);
            }
            counter++;
            browser.refresh();
            this.renderedPage();
        }
        return (UploadFileDialog) uploadDialog.renderedPage();
    }

    public void clickCreateButton()
    {
        browser.waitUntilElementClickable(createButton, 10).click();
        browser.waitUntilElementVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
    }

    public void clickAction(String libraryItem, String action)
    {
        List<WebElement> availableActions = selectDocumentLibraryItemRow(libraryItem).findElements(actionsSet);
        browser.findFirstElementWithValue(availableActions, action).click();
    }

    public NewContentDialog clickFolderLink()
    {
        browser.waitUntilElementClickable(folderLink, 30).click();
        return (NewContentDialog) newContentDialog.renderedPage();
    }

    /**
     * Get the list of documents name from Exploratory Panel: Library -> Documents section
     *
     * @return documents string list
     */
    public String getExplorerPanelDocuments()
    {
        browser.waitUntilElementsVisible(explorerPanelDocumentsList);
        ArrayList<String> foldersTextList = new ArrayList<>();
        for (WebElement anExplorerPanelDocumentsList : explorerPanelDocumentsList)
        {
            foldersTextList.add(anExplorerPanelDocumentsList.getText());
        }
        return foldersTextList.toString();
    }

    /**
     * @return string list containing elements from Document Library breadcrumb
     */
    public String getBreadcrumbList()
    {
        browser.waitInSeconds(1);
        ArrayList<String> breadcrumbTextList = new ArrayList<>();
        for (WebElement aBreadcrumbList : breadcrumbList)
        {
            breadcrumbTextList.add(aBreadcrumbList.getText());
        }
        breadcrumbTextList.add(breadcumbCurrentFolder.getText());

        return breadcrumbTextList.toString();
    }

    /**
     * Click on filter from explorer panel --> Documents
     *
     * @param filter to be clicked
     */
    public DocumentLibraryPage clickDocumentsFilterOption(String filter)
    {
        browser.findFirstElementWithValue(documentsFilterOptions, filter).click();
        for (DocumentsFilters docFilter : DocumentsFilters.values())
            if (docFilter.title.equals(filter))
            {
                browser.waitUntilElementContainsText(navigationBar, docFilter.header);
                break;
            }
        return (DocumentLibraryPage) this.renderedPage();
    }

    /**
     * Click on folder from Document Library --> explorer panel --> Library --> Documents
     *
     * @param folderName folder to be clicked
     */
    public DocumentLibraryPage clickFolderFromExplorerPanel(String folderName)
    {
        WebElement folder = browser.waitUntilElementVisible(browser.findFirstElementWithExactValue(explorerPanelDocumentsList, folderName));
        folder.click();
        browser.waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return (DocumentLibraryPage) this.renderedPage();
    }

    /**
     * Click on any folder from Document library breadcrumb, except the last one
     *
     * @param folderName folder to be clicked
     */
    public DocumentLibraryPage clickFolderFromBreadcrumb(String folderName)
    {
        browser.findFirstElementWithExactValue(breadcrumbList, folderName).click();
        browser.waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return (DocumentLibraryPage) this.renderedPage();
    }

    /**
     * Click on folderUp icon
     */
    public DocumentLibraryPage clickFolderUpButton()
    {
        folderUpButton.click();
        return (DocumentLibraryPage) this.renderedPage();
    }

    public void clickMoreMenu(String libraryItem)
    {
        if (isMoreMenuDisplayed(libraryItem))
        {
            WebElement moreMenu = selectDocumentLibraryItemRow(libraryItem).findElement(moreMenuSelector);
            browser.waitUntilElementClickable(moreMenu, 30).click();
            browser.waitUntilElementVisible(
                    browser.findElement(By.xpath("//div[contains(@id, 'default-actions-yui') and not(@class='hidden')]/div[contains(@class,'action-set')]")));
        }
    }

    public void clickMore()
    {
        browser.findDisplayedElementsFromLocator(By.cssSelector("#onActionShowMore a span")).get(0).click();
    }

    public boolean isMoreMenuDisplayed(String contentName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(contentName), moreMenuSelector);
    }

    private List<WebElement> getAvailableActions(String libraryItem)
    {
        mouseOverContentItem(libraryItem);
        if (isMoreMenuDisplayed(libraryItem))
        {
            WebElement contentItem = selectDocumentLibraryItemRow(libraryItem);
            List<WebElement> availableActions = contentItem.findElements(By.cssSelector(".action-set>div>a"));
            WebElement actionElement = browser.findFirstElementWithValue(availableActions, "More...");
            actionElement.sendKeys(Keys.ENTER);
        }
        LOG.info("Available actions are: "+ actionsSet);
        return selectDocumentLibraryItemRow(libraryItem).findElements(actionsSet);
    }

    public boolean isActionAvailableForLibraryItem(String libraryItem, String action)
    {
        return browser.findFirstElementWithValue(getAvailableActions(libraryItem), action) != null;
    }

    public boolean areActionsAvailableForLibraryItem(String libraryItem, List<String> actions)
    {
        List<WebElement> availableActions = getAvailableActions(libraryItem);
        boolean found = true;
        for (String action : actions)
        {
            if (browser.findFirstElementWithValue(availableActions, action) == null)
            {
                LOG.info("Available actions are: "+ availableActions.toString());
                LOG.info(String.format("Action '%s' is not available!", action));
                return false;
            }
        }
        return found;
    }

    public boolean areActionsNotAvailableForLibraryItem(String libraryItem, List<String> actions)
    {
        List<WebElement> availableActions = getAvailableActions(libraryItem);
        boolean notFound = true;
        for (String action : actions)
        {
            if (browser.findFirstElementWithValue(availableActions, action) != null)
            {
                LOG.info(String.format("Action '%s' is available!", action));
                return false;
            }
        }
        return notFound;
    }

    /**
     * Click on the given action for an item.
     * This method is used for actions that redirect to a page!
     *
     * @param contentItem content item to apply action
     * @param action to be clicked on
     * @param page to be rendered after click
     * @return render redirected page
     */
    public HtmlPage clickDocumentLibraryItemAction(String contentItem, String action, HtmlPage page)
    {
        WebElement libraryItem = selectDocumentLibraryItemRow(contentItem);
        Parameter.checkIsMandotary(String.format("Library item %s", contentItem), libraryItem);

        mouseOverContentItem(contentItem);
        List<WebElement> availableActions = libraryItem.findElements(By.cssSelector(".action-set>div>a"));
        WebElement actionElement = browser.findFirstElementWithValue(availableActions, action);
        if (actionElement != null)
        {
            LOG.info(String.format("Click on '%s' action.", action));
            actionElement.click();
        }
        else
        {
            actionElement = browser.findFirstElementWithValue(availableActions, "More...");
            if (actionElement != null)
            {
                LOG.info("Click on 'More...'");
                LOG.info("Button to be pressed: "+ actionElement.getText());
                actionElement.sendKeys(Keys.ENTER);
                availableActions = libraryItem.findElements(By.cssSelector(".more-actions>div>a"));
                actionElement = browser.findFirstElementWithValue(availableActions, action);
                Parameter.checkIsMandotary("Action", actionElement);
                LOG.info(String.format("Click on '%s' action.", action));
                actionElement.click();
            }
        }

        return page.renderedPage();
    }

    /**
     * Click on action selected.
     * To be used for actions that do not redirect to a page
     * 
     * @param libraryItem content item to apply action
     * @param action to be clicked on
     */
    public void clickOnAction(String libraryItem, String action)
    {
        if (isMoreMenuDisplayed(libraryItem))
            clickMoreMenu(libraryItem);

        List<WebElement> availableActions = selectDocumentLibraryItemRow(libraryItem).findElements(actionsSet);
        WebElement actionLink = browser.findFirstElementWithValue(availableActions, action);
        actionLink.click();
    }
    public void clickOnLocateFolder()
    {
       getBrowser().waitUntilElementVisible(locateFolder);
       getBrowser().waitUntilElementClickable(locateFolder).click();
    }


    public String getDocumentListHeader()
    {
        return navigationBar.getText();
    }

    public String getDocumentListMessage()
    {
        return documentList.getText();
    }

    public String getFavoriteTooltip(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).getAttribute("title");
    }

    public DocumentLibraryPage clickFavoriteLink(String fileName)
    {
        selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).click();
        return (DocumentLibraryPage) this.renderedPage();
    }

    public boolean isFileFavorite(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).getAttribute("class").contains("enabled");
    }

    public boolean isRenameIconDisplayed(String content) {
        int nrOfTimes = 0;
        mouseOverContentItem(content);
        while (!browser.isElementDisplayed(selectDocumentLibraryItemRow(content), renameIcon) && nrOfTimes < 5) {
            mouseOverContentItem(content);
            browser.isElementDisplayed(selectDocumentLibraryItemRow(content), renameIcon);
            nrOfTimes++;
        }
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(content), renameIcon);
    }

    /**
     * Click on 'Rename' icon from the right of content name
     */
    public void clickRenameIcon(String contentName)
    {
        WebElement renameIconElement = selectDocumentLibraryItemRow(contentName).findElement(renameIcon);
        Parameter.checkIsMandotary("Rename icon", renameIconElement);
        renameIconElement.click();
        browser.waitUntilElementVisible(contentNameInputField);
    }

    /**
     * Verify content name is text input field
     */
    public boolean isContentNameInputField()
    {
        return browser.isElementDisplayed(contentNameInputField);
    }

    /**
     * Type new name in content name text input field
     *
     * @param newContentName
     */
    public void typeContentName(String newContentName)
    {
        WebElement contentNameInput = browser.waitUntilElementVisible(contentNameInputField);
        contentNameInput.clear();
        contentNameInput.sendKeys(newContentName);
        browser.waitInSeconds(1);
    }

    /**
     * Click on button from 'Rename'
     *
     * @param buttonName to be clicked (Save/Cancel)
     */
    public DocumentLibraryPage clickButtonFromRenameContent(String buttonName)
    {
        browser.findFirstElementWithValue(buttonsFromRenameContent, buttonName).click();
        return (DocumentLibraryPage) this.renderedPage();
    }

    /**
     * @param expectedButtons list of expected to be displayed buttons from renaming content by icon
     * @return displayed buttons
     */
    public boolean verifyButtonsFromRenameContent(String... expectedButtons)
    {
        List<String> buttonNames = new ArrayList<>();
        for (WebElement buttonFromRenameContent : buttonsFromRenameContent)
            buttonNames.add(buttonFromRenameContent.getText());
        return DataUtil.areListsEquals(buttonNames, expectedButtons);
    }

    /**
     * Select a content item from Documents list, by click on its corresponding checkbox
     *
     * @param contentName to be selected
     */
    public void clickCheckBox(String contentName)
    {
        selectDocumentLibraryItemRow(contentName).findElement(checkBoxSelector).click();
        browser.waitInSeconds(1);
    }

    public boolean isContentSelected(String contentName)
    {
        browser.waitInSeconds(1);
        return selectDocumentLibraryItemRow(contentName).findElement(checkBoxSelector).isSelected();
    }

    /**
     * Check the selected content list is the one expected
     *
     * @param expectedSelectedContentList content to be selected
     * @return
     */
    public String verifyContentItemsSelected(ArrayList<String> expectedSelectedContentList)
    {
        for (String contentName : expectedSelectedContentList)
        {
            if (!isContentSelected(contentName))
                return contentName + " isn't selected!";
        }
        return expectedSelectedContentList.toString();
    }

    /**
     * Check the notSelected content list is the one expected
     *
     * @param expectedNotSelectedContentList content not to be selected
     * @return
     */
    public String verifyContentItemsNotSelected(ArrayList<String> expectedNotSelectedContentList)
    {
        for (String contentName : expectedNotSelectedContentList)
        {
            if (isContentSelected(contentName))
                return contentName + " is selected!";
        }
        return expectedNotSelectedContentList.toString();
    }

    public boolean isActiveWorkflowsIconDisplayed(String documentLibraryItem)
    {
        browser.waitInSeconds(1);
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(documentLibraryItem).findElement(By.cssSelector("[class=status] img")));
    }

    /**
     * Gets selected categories listed below the given documentLibraryItem name
     *
     * @param documentLibraryItem
     * @return
     */
    public List<String> getItemCategories(String documentLibraryItem)
    {
        List<String> categoriesTexts = new ArrayList<>();
        List<WebElement> categories = selectDocumentLibraryItemRow(documentLibraryItem).findElements(categoriesDetails);
        for (WebElement category : categories)
        {
            categoriesTexts.add(category.getText());
        }
        return categoriesTexts;
    }

    public String getItemTitle(String itemName)
    {
        return selectDocumentLibraryItemRow(itemName).findElement(titleSelector).getText();
    }

    public String getItemDescription(String itemName)
    {
        return selectDocumentLibraryItemRow(itemName).findElement(descriptionSelector).getText();
    }

    public void clickDownloadForItem(String itemName)
    {
        WebElement downloadButton = browser.waitUntilElementVisible(selectDocumentLibraryItemRow(itemName).findElement(downloadButtonSelector));
        downloadButton.click();
    }

    public void clickDownloadAsZipForItem(String itemName)
    {
        WebElement downloadAsZip = browser.waitUntilElementVisible(selectDocumentLibraryItemRow(itemName).findElement(downloadAsZipSelector));
        downloadAsZip.click();
    }

    public String getTags(String contentName)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(browser.findElement(contentTagsSelector), 5);
        List<WebElement> tagsList = selectDocumentLibraryItemRow(contentName).findElements(contentTagsSelector);
        List<String> tagsTextList = new ArrayList<>();
        for (WebElement aTagsList : tagsList)
        {
            tagsTextList.add(aTagsList.getText());
        }
        return tagsTextList.toString();
    }

    public List<String> getAllTagNames()
    {
        List<String> allTagsNames = new ArrayList<>();
        int counter = 0;
        int retryCount = 5;
        while (counter < retryCount)
        {
            try
            {   browser.waitInSeconds(20);
            browser.refresh();
                By tagRows = By.cssSelector("ul.filterLink li span.tag a");
                getBrowser().waitUntilElementsVisible(tagRows);
                for (WebElement tagsElem : browser.findElements(tagRows))
                {
                    allTagsNames.add(tagsElem.getText());
                }
                break;
            }
            catch (TimeoutException | NoSuchElementException e)
            {
                browser.refresh();
                counter++;
            }
        }
        return allTagsNames;
    }

    /**
     * For a content, click on any tag and type a valid tag name
     *
     * @param tagName tag to be edited
     * @param newTagName new value for tag
     */
    public void editTag(String tagName, String newTagName)
    {
        List<WebElement> tagsList = browser.waitUntilElementsVisible(inlineEditTagsSelector);

        WebElement tagElement = browser.findFirstElementWithExactValue(tagsList, tagName);
        Parameter.checkIsMandotary("Tag", tagElement);
        tagElement.click();
        browser.waitUntilElementVisible(tagToBeEdited);
        tagToBeEdited.clear();
        browser.waitInSeconds(1);
        tagToBeEdited.sendKeys(newTagName);
        tagToBeEdited.sendKeys(Keys.ENTER);
    }

    public boolean isNoTagsTextDisplayed(String contentName)
    {
        int counter = 0;
        while (counter < 2)
        {
            try
            {
                WebElement noTags = selectDocumentLibraryItemRow(contentName).findElement(noTagsSelector);
                return browser.isElementDisplayed(noTags);
            }
            catch (TimeoutException | NoSuchElementException e)
            {
                LOG.error("Action not found:" + e);
            }
            counter++;
            browser.refresh();
            this.renderedPage();
        }
        return false;
    }

    public void mouseOverTags(String contentName)
    {
        WebElement contentElement = selectDocumentLibraryItemRow(contentName);
        Parameter.checkIsMandotary("Content selector", contentElement);
        WebElement tagElement = contentElement.findElement(contentTagsSelector);
        Parameter.checkIsMandotary("Tag selector", tagElement);
        browser.mouseOver(tagElement);
        browser.waitUntilElementVisible(contentElement.findElement(editTagSelector));
    }

    /**
     * Hover "No Tags" text for a content item
     *
     * @param contentName
     */
    public void mouseOverNoTags(String contentName)
    {
        WebElement contentElement = selectDocumentLibraryItemRow(contentName);
        Parameter.checkIsMandotary("Content selector", contentElement);
        WebElement tagElement = contentElement.findElement(noTagsSelector);
        Parameter.checkIsMandotary("Tag selector", tagElement);
        browser.mouseOver(tagElement);
        browser.waitUntilElementVisible(contentElement.findElement(editTagSelector));
    }

    public boolean isEditTagInputFieldDisplayed()
    {
        browser.waitUntilElementVisible(editTagInputField);
        return browser.isElementDisplayed(editTagInputField);
    }

    public void typeTagName(String tagName)
    {
        browser.waitInSeconds(5);
        browser.waitUntilElementVisible(editTagInputField);
        editTagInputField.clear();
        editTagInputField.sendKeys(tagName);
        editTagInputField.sendKeys(Keys.RETURN);
    }

    public DocumentLibraryPage clickEditTagLink(String linkName)
    {
        browser.waitInSeconds(1);
        WebElement link = browser.findFirstElementWithValue(editTagButtons, linkName);
        browser.waitUntilElementClickable(link, 50).click();
        browser.waitInSeconds(2);
        return (DocumentLibraryPage) this.renderedPage();
    }

    public void clickOnTag(String tagName)
    {
        browser.waitUntilElementVisible(
                By.xpath("//ul[contains(@class,'filterLink')]/li/span[contains(@class,'tag')]/a[contains(text(),'" + tagName.toLowerCase() + "')]")).click();
    }

    /**
     * Check "Edit Tag" icon presence for a content item
     *
     * @param contentName
     * @return true if icon is displayed, false otherwise
     */
    public boolean isEditTagIconDisplayed(String contentName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(contentName), editTagSelector);
    }

    /**
     * Click on 'Edit Tag' icon for a content item
     *
     * @param contentName
     */
    public void clickEditTagIcon(String contentName)
    {
        WebElement editTagIcon = selectDocumentLibraryItemRow(contentName).findElement(editTagSelector);
        browser.waitUntilElementClickable(editTagIcon, 30).click();
    }

    /**
     * Switch to new window and get page content
     */
    public String switchToNewWindowAngGetContent()
    {
        browser.switchWindow(1);
        String content = browser.findElement(By.xpath("//body")).getText();
        browser.closeWindowAndSwitchBack();
        return content;
    }

    /**
     * For a content item's tag, click 'Remove tag' icon
     *
     * @param tagName to be removed
     * @return name of removed tag
     */
    public void removeTag(String tagName)
    {
        List<WebElement> tagsList = browser.waitUntilElementsVisible(inlineEditTagsSelector);
        List<WebElement> removeIconList = browser.waitUntilElementsVisible(removeTagIconSelector);

        for (int i = 0; i < tagsList.size(); i++)
        {
            if (tagsList.get(i).getText().toLowerCase().equals(tagName))
            {
                removeIconList.get(i).click();
            }
        }
    }

    /**
     * Method to check if the file is opened in Google Maps
     */
    public boolean isFileOpenedInGoogleMaps()
    {
        return googleMap.isDisplayed();
    }

    /**
     * Method to check that the document thumbnail is displayed on Google Maps
     *
     * @return
     */
    public boolean isDocumentThumbnailDisplayedOnGoogleMaps()
    {
        return googleMapPopUp.isDisplayed();
    }

    /**
     * Method to click on the document thumbnail to open the document in preview
     */
    public DocumentDetailsPage clickOnFileInGoogleMaps()
    {
        googleMapPopUp.click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Method to check that the geolocation metadata icon is displayed next to the document
     */
    public boolean isGeolocationMetadataIconDisplayed()
    {
        return geolocationMetadataIcon.isDisplayed();
    }

    /**
     * Method to get the info banner text
     */

    public String getInfoBannerText(String fileName)
    {
        WebElement fileElement = selectDocumentLibraryItemRow(fileName);
        Parameter.checkIsMandotary("Document library file", fileElement);
        WebElement bannerElement = fileElement.findElement(infoBanner);
        Parameter.checkIsMandotary("File banner", bannerElement);
        return bannerElement.getText();
    }

    /**
     * Method to check if the info banner is displayed
     */

    public boolean isInfoBannerDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(fileName), infoBanner);
    }

    /**
     * Method to get the locked by user name
     */
    public String getLockedByUserName()
    {
        return lockedByUser.getText();
    }

    /**
     * Method to check if a view option is present
     */

    public boolean isviewOptionDisplayed(String view)
    {
        return browser.isElementDisplayed(selectViewInOptions(view));
    }

    /**
     * Method to click on the Options menu button
     */

    public void clickOptionsButton()
    {
        optionsMenu.click();
    }

    /**
     * Method to check if Favorite link is present for test file
     */
    public boolean isFavoriteLinkPresent(String fileName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(fileName), favoriteLink);
    }

    public boolean isLikeButtonDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(fileName), likeButton);
    }

    public boolean isCommentButtonDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(fileName), commentButton);
    }

    public boolean isShareButtonDisplayed(String fileName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(fileName), By.cssSelector("a[class='quickshare-action']"));
    }

    public String getOptionsSetDefaultViewText(String text)
    {
        browser.waitUntilElementIsDisplayedWithRetry(optionsMenuDropDown);
        browser.waitUntilElementContainsText(setDefaultView, text);
        return setDefaultView.getText();
    }

    public boolean isItemDisplayedInGalleryView()
    {
        return galleryViewItem.isDisplayed();
    }

    public String getLabelDisplayedInFilmstripView(String contentName)
    {
        return findItemInCarrouselFilmstripView(contentName).getText();
    }

    public boolean isDownArrowPointerDisplayed()
    {
        return downArrowPointer.isDisplayed();
    }

    public boolean isRightArrowPointerDisplayed()
    {
        return rightArrowPointer.isDisplayed();
    }

    public void clickTheRightArrowPointer()
    {
        rightArrowPointer.click();
    }

    public boolean isLeftArrowPointerDisplayed()
    {
        return leftArrowPointer.isDisplayed();
    }

    public void clickSetDefaultView()
    {
        setDefaultView.click();
    }

    public String getRemoveDefaultViewText()
    {
        return removeDefaultView.getText();
    }

    public boolean isSortButtonDisplayed()
    {
        return sortButton.isDisplayed();
    }

    public boolean isCreateButtonDisplayed()
    {
        return createButton.isDisplayed();
    }

    public boolean isSortByFieldButtonDisplayed()
    {
        return sortByFieldButton.isDisplayed();
    }

    public boolean isPaginationDisplayed()
    {
        return currentPage.isDisplayed();
    }

    public String getCreateButtonStatusDisabled()
    {
        return createButton.getAttribute("disabled");
    }

    public String getUploadButtonStatusDisabled()
    {
        return uploadButton.getAttribute("disabled");
    }

    public void clickLinkToFolder(String folderName)
    {
        List<WebElement> linkToFolderList = browser.waitUntilElementsVisible(linkToFolderLocator);
        browser.findFirstElementWithExactValue(linkToFolderList, folderName).click();
    }

    public boolean isCreateContentMenuDisplayed()
    {
        return browser.isElementDisplayed(createContentMenu);
    }

    public void clickCreateButtonWithoutWait()
    {
        browser.waitUntilElementVisible(createButton).click();
    }
    public boolean isFileDisplayed(String fileName)
    {
       try {
           getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("td[class$='yui-dt-col-fileName']"), 5);
           WebElement webElement = selectDocumentLibraryItemRow(fileName);

           return browser.isElementDisplayed(webElement);
       }

       catch(NoSuchElementException ex) {
           LOG.info("Element not found "+ ex.getMessage().toString());
           return false;
       }
    }
}