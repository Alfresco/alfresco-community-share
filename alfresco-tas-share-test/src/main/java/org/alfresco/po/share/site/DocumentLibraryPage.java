package org.alfresco.po.share.site;

import static org.alfresco.common.Utils.retryUntil;
import static org.alfresco.common.Wait.WAIT_3;
import static org.alfresco.common.Wait.WAIT_5;
import static org.testng.Assert.assertTrue;

import com.google.common.base.Function;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.common.Utils;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.common.Parameter;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DocumentLibraryPage extends SiteCommon<DocumentLibraryPage> // TODO to be deleted
{
    public DocumentLibraryPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    //todo: move to separate file
    public enum CreateMenuOption
    {
        FOLDER(By.cssSelector("span.folder-file")),
        PLAIN_TEXT(By.cssSelector("span.text-file")),
        HTML(By.cssSelector("span.html-file")),
        XML(By.cssSelector("span.xml-file")),
        GOOGLE_DOCS_DOCUMENT(By.cssSelector("span.document-file")),
        GOOGLE_DOCS_SPREADSHEET(By.cssSelector("span.spreadsheet-file")),
        GOOGLE_DOCS_PRESENTATION(By.cssSelector("span.presentation-file")),
        CREATE_DOC_FROM_TEMPLATE(By.xpath("//a[contains(@class, 'yuimenuitemlabel-hassubmenu')]//span[text()='Create document from template']")),
        CREATE_FOLDER_FROM_TEMPLATE(By.xpath("//a[contains(@class, 'yuimenuitemlabel-hassubmenu')]//span[text()='Create folder from template']"));

        private By locator;

        CreateMenuOption(By locator)
        {
            this.locator = locator;
        }

        public By getLocator()
        {
            return locator;
        }
    }

    @Autowired
    GoogleDocsCommon googleDocs;
    //@Autowired
    CreateContentPage createContent;
    //@Autowired
    DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private UploadFileDialog uploadDialog;
   // @Autowired
    private NewFolderDialog newContentDialog;

    private static final String ACTION_SELECTOR = "div[id*='default-actions']:not([class*='hidden'])>.action-set .{0}>a";
    private static final String ACTION_SELECTOR_MORE = "div[id*='default-actions']:not([class*='hidden']) div.more-actions>.{0}>a";
    /** more actions */
    private By moreSelector = By.cssSelector("div[id*='default-actions']:not([class*='hidden']) a.show-more");
    private By moreActionsMenu = By.cssSelector("div[id*='default-actions']:not([class*='hidden'])>.action-set>.more-actions");

    public By createContentMenu = By.cssSelector("div[id*='_default-createContent-menu'].visible");
    public By editTagSelector = By.cssSelector("td .detail span[class='insitu-edit']:first-child");
    @FindBy (css = "[id$='default-fileUpload-button-button']")
    protected WebElement uploadButton;
    @FindBy (css = "button[id$='default-options-button-button']")
    protected WebElement optionsMenu;
    @FindBy (css = ".hideFolders")
    protected WebElement hideFoldersMenuOption;
    protected By likeButton = By.cssSelector("a.like-action");

    @FindAll (@FindBy (css = "a.filter-link"))
    private List<WebElement> documentsFilterOptions;
    @FindBy (css = "div[id$='default-navBar']")
    private WebElement navigationBar;
    @FindBy (css = "div[id$='paginatorBottom'] span[class$='current']")
    private WebElement paginator;
    @FindBy (css = "div[id$='_default-dl-body']")
    private WebElement docListContainer;
    @FindBy (css = ".documents[id$='_default-documents']")
    private WebElement documentList;
    @FindBy (css = "button[id*='createContent']")
    private WebElement createButton;
    @FindBy (css = "div[id$='default-options-menu'] span")
    private List<WebElement> optionsList;
    private By optionsMenuDropDown = By.cssSelector("div[id*='default-options-menu'].visible");
    private By displayedOptionsListBy = By.xpath("//div[contains(@id, 'default-options-menu')]//li[not(contains(@class, 'hidden'))]");
    private By foldersList  = By.cssSelector(".filter-change:nth-child(1)");
    private By filesList = By.cssSelector(".filename a[href*='document-details']");
    private By documentLibraryItemsList = By.cssSelector("div[id$='default-documents'] tbody[class$='data'] tr");
    @FindAll (@FindBy (css = ".crumb .folder"))
    private List<WebElement> breadcrumbList;
    @FindBy (css = ".crumb .label a")
    private WebElement breadcumbCurrentFolder;
    @FindBy (css = "button[id*='folderUp']")
    private WebElement folderUpButton;
    private By contentNameInputField = By.cssSelector("input[id*='form-field']");
    @FindBy (css = ".insitu-edit a")
    private List<WebElement> buttonsFromRenameContent;
    @FindBy (css = ".inlineTagEditAutoCompleteWrapper input")
    private WebElement editTagInputField;
    @FindBy (css = "form[class='insitu-edit'] a")
    private List<WebElement> editTagButtons;
    @FindBy (css = ".inlineTagEditAutoCompleteWrapper input")
    private WebElement tagToBeEdited;
    @FindBy (css = "div[class ='google-map']")
    private WebElement googleMap;
    @FindBy (css = "div[id*='_default-info'] div[class='thumbnail'] a[href*='document-details']")
    private WebElement googleMapPopUp;
    @FindBy (css = "div[class ='status'] img[title ='Geolocation metadata available']")
    private WebElement geolocationMetadataIcon;
    @FindBy (css = "span[class ='setDefaultView']")
    private WebElement setDefaultView;
    @FindBy (css = "span[class ='removeDefaultView']")
    private WebElement removeDefaultView;
    @FindBy (css = "div[class ='alf-gallery-item']")
    private WebElement galleryViewItem;
    @FindBy (css = "div[id*='_default-filmstrip-nav-handle']")
    private WebElement downArrowPointer;
    @FindBy (css = "div[class ='alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-next']")
    private WebElement rightArrowPointer;
    @FindBy (css = "div[class ='alf-filmstrip-nav-button alf-filmstrip-main-nav-button alf-filmstrip-nav-prev']")
    private WebElement leftArrowPointer;
    @FindBy (css = "button[id*='_default-sortAscending-button-button']")
    private WebElement sortButton;
    @FindBy (css = "button[id*='_default-sortField-button-button']")
    private WebElement sortByFieldButton;
    @FindBy (css = "span.yui-pg-current")
    private WebElement currentPage;
    @FindAll (@FindBy (css = ".documentDroppable .ygtvlabel"))
    private List<WebElement> explorerPanelDocumentsList;
    @FindBy (css = ".yui-dt-col-fileName")
    private List<WebElement> nrOfSharedElements;

    private By renameIcon = By.cssSelector(".filename span.insitu-edit[style*='visibility: visible']");
    private By linkToFolderLocator = By.cssSelector(".filename [href*='FdocumentLibrary']");
    private By moreMenuSelector = By.cssSelector("div[id*='onActionShowMore'] a span");
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
    private By lockedByUser = By.cssSelector("div.info-banner a");
    private By titleSelector = By.cssSelector("td .title");
    private By descriptionSelector = By.cssSelector("td .detail:nth-child(3) span");
    private By commentButton = By.cssSelector("a.comment");

    public boolean areCreateOptionsAvailable()
    {
        for (CreateMenuOption option : CreateMenuOption.values()) {
            if (!isElementDisplayed(option.getLocator()))
            {
                log.info("Create menu option: %s was not found", option.name() );
                return false;
            }
        }
        return true;
    }

    /**
     * Method to click Plain Text, HTML or XML option from create menu
     */
    public CreateContentPage clickCreateContentOption(CreateMenuOption option)
    {
        clickElement(option.getLocator());
        return new CreateContentPage(webDriver);
    }

    /**
     * Method to click on create google docs options from create menu
     */
    public GoogleDocsCommon clickGoogleDocsOption(CreateMenuOption option)
    {
        clickElement(option.getLocator());
        return (GoogleDocsCommon) googleDocs.renderedPage();
    }

    public NewFolderDialog clickFolderLink()
    {
        clickElement(CreateMenuOption.FOLDER.getLocator());
        return new NewFolderDialog(webDriver);
    }

    /**
     * Method to click on Create Document/Folder from Template
     */
    public void clickCreateFromTemplateOption(CreateMenuOption option)
    {
        Utils.retry(() ->
        {
            WebElement optionElement = waitUntilElementIsVisible(option.getLocator());
            // need sometime to mouse over another element before mouse over create from Template option because the
            // list of templates doesn't appear
            mouseOver(createButton);
            mouseOver(optionElement);
            clickElement(optionElement);
            return waitUntilElementIsVisible(By.cssSelector(".yuimenuitemlabel-hassubmenu-selected+.yuimenu.visible"));
        }, WAIT_3.getValue());
    }

    private WebElement selectTemplate(String templateName)
    {
        return waitUntilElementIsVisible(By.xpath("//a[@class = 'yuimenuitemlabel']//span[text()='" + templateName + "']"));
    }

    /**
     * Method to check if the template is present
     */
    public boolean isTemplateDisplayed(String templateName)
    {
        return isElementDisplayed(selectTemplate(templateName));
    }

    /**
     * Method to select template
     */
    public void clickOnTemplate(String templateName)
    {
        clickElement(selectTemplate(templateName));
       /* if (page instanceof DocumentLibraryPage)
        {
            waitUntilNotificationMessageDisappears();
        }*/
    }

    /**
     * Helper method to get the number of items that should be on the page, according to the item count at the bottom.
     */
    protected int getItemCount()
    {
        // figure out how many items are on the page
        String text = paginator.getText();
        String[] values = text.split(" ");
        // The original string will be something like "1 - 25 of 32".
        if (values.length == 5)
        {
            int maxItemOnPage = Integer.parseInt(values[2]);
            // Handle the special case of "0 - 0 of 0"
            if (maxItemOnPage == 0)
            {
                return 0;
            }
            int minItemOnPage = Integer.parseInt(values[0]);
            return maxItemOnPage - minItemOnPage + 1;
        }
        log.info("Item count could not be determined. Assuming 0 items on this page.");
        return 0;
    }

    /**
     * Wait for all the expected rows to be there
     */
    private void waitForRows()
    {
        // wait predicate
        Function<WebBrowser, Boolean> rowsAvailable = (w) ->
        {
            List<WebElement> rows = w.findElements(documentLibraryItemsList);
            int itemCount = -1;
            try
            {
                itemCount = getItemCount();
            }
            catch (NoSuchElementException e)
            {
                // This can happen because this method is called while the page is rendering.
                log.debug("BrowseList still rendering - item count is not yet displayed");
            }
            return (itemCount == rows.size());
        };
    }


    private WebElement selectViewInOptions(String viewName)
    {
        return findElement(By.xpath("//div[contains(@id, '_default-options-menu')]//ul[@class= 'first-of-type']//span[text()='" + viewName + "']"));
    }

    private WebElement findItemInCarrouselFilmstripView(String contentName)
    {
        return findElement(By.xpath("//div[contains(@class, 'alf-filmstrip-nav-item-thumbnail')]//div[text()='" + contentName + "']"));
    }

    public int getNrOfSharedElements()
    {
        return nrOfSharedElements.size();
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/documentlibrary", getCurrentSiteName());
    }

    public boolean isUploadButtonDisplayed()
    {
        return isElementDisplayed(waitUntilElementIsVisible(uploadButton));
    }

    public void waitForContent(String contentName) throws InterruptedException
    {
        //the content might not be in the Document Library list due to SOLR indexes
        if (selectDocumentLibraryItemRow(contentName) == null)
        {
            //refresh the current page until the file is found in the Document Library list
            Utility.sleep(5000, 30000, () -> {
                refresh();
                assertTrue(selectDocumentLibraryItemRow(contentName) != null);
            });
        }
    }

    public boolean isContentNameDisplayed(String contentName)
    {
        try
        {
            waitForContent(contentName);
            return true;
        }
        catch (AssertionError | InterruptedException ex)
        {
            return false;
        }
    }

    /**
     * Replace the extension of the file. Replace what comes after '.' character with a string representing another extension.
     * @param fileName          the name of the file that will have another extension type
     * @param newFileExtension  the expected extension
     * @return                  the file name with changed extension
     */
    public String replaceFileExtension(String fileName, String newFileExtension){
        return String.format(StringUtils.substringBefore(fileName, ".") + newFileExtension);
    }

    /**
     * Verify presence of content with exact value for it's name
     */
    public boolean isContentWithExactValuePresent(String content)
    {
        WebElement webElement = selectDocLibItemWithExactValue(content);
        return isElementDisplayed(webElement);
    }

    public UploadFileDialog clickUpload()
    {
        waitUntilElementIsVisible(uploadButton).click();
        return new UploadFileDialog(webDriver);
    }

    public DocumentLibraryPage uploadNewImage(String imagePath)
    {
        log.info("Upload image to path: {}", imagePath);
        return (DocumentLibraryPage) clickUpload().uploadFile(imagePath, this);
    }

    public boolean isDocumentListDisplayed()
    {
        return isElementDisplayed(documentList);
    }

    /**
     * Verify presence of "Options" menu
     *
     * @return true if displayed or false if is not.
     */
    public boolean isOptionsMenuDisplayed()
    {
        return isElementDisplayed(optionsMenu);
    }

    public boolean isHideFoldersMenuOptionDisplayed()
    {
        boolean elementDisplayed;
        optionsMenu.click();
        elementDisplayed = isElementDisplayed(hideFoldersMenuOption);
        optionsMenu.click();
        return elementDisplayed;
    }

    public DocumentLibraryPage selectViewFromOptionsMenu(String view)
    {
        optionsMenu.click();
        selectOptionFromFilterOptionsList(view, optionsList);
        return this;
    }

    public List<String> getAllOptionsText()
    {
        List<String> optionsText = new ArrayList<>();
        List<WebElement> options = findElements(displayedOptionsListBy);
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
        waitUntilElementIsDisplayedWithRetry(foldersList, WAIT_5.getValue());
        waitForRows();
        List<String> foldersName = new ArrayList<>();
        for (WebElement folder : findElements(foldersList))
        {
            foldersName.add(folder.getText());
        }
        return foldersName;
    }

    public WebElement selectDocumentLibraryItemRow(String documentItem)
    {
        waitForRows();
        List<WebElement> itemsList = findElements(documentLibraryItemsList);
        return findFirstElementWithValue(itemsList, documentItem);
    }

    /**
     * Select document library item matching exact value of content name
     */

    public WebElement selectDocLibItemWithExactValue(String item)
    {
        waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 2);
        List<WebElement> itemsList = findElements(documentLibraryItemsList);
        return findFirstElementWithExactValue(itemsList, item);
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
        clickElement(folderElement.findElement(contentNameSelector));
        waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return this;
    }

    /**
     * Mouse over a content name link
     *
     * @param contentItem content item's name link to be hovered
     */
    public WebElement mouseOverContentItem(String contentItem)
    {
        try
        {
            waitForContent(contentItem);
        }
        catch (InterruptedException | AssertionError e)
        {
            throw new AssertionError("Content " + contentItem + " was not displayed in library page!");
        }
        WebElement contentItemElement = selectDocumentLibraryItemRow(contentItem);
        Parameter.checkIsMandotary("Content item", contentItemElement);
        mouseOver(contentItemElement.findElement(contentNameSelector));

        return Utils.retry(() ->
        {
            mouseOver(contentItemElement);
            waitUntilElementHasAttribute(contentItemElement,"class", "yui-dt-highlighted");

            return contentItemElement;
        }, WAIT_3.getValue());
    }

    /**
     * This method is used to get the list of files name
     *
     * @return filesName
     */
    public List<String> getFilesList()
    {
        waitUntilElementIsDisplayedWithRetry(filesList, 6);
        List<String> filesName = new ArrayList<>();
        for (WebElement file : findElements(filesList))
        {
            filesName.add(file.getText());
        }
        return filesName;
    }

    public DocumentDetailsPage clickOnFile(String file)
    {
        WebElement fileElement = selectDocumentLibraryItemRow(file);
        Parameter.checkIsMandotary("File", fileElement);
        clickElement(fileElement.findElement(contentNameSelector));
        return new DocumentDetailsPage(webDriver);
    }

    public void clickCreateButton()
    {
        clickElement(createButton);
        waitUntilElementIsVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
    }

    /**
     * Get the list of documents name from Exploratory Panel: Library -> Documents section
     *
     * @return documents string list
     */
    public String getExplorerPanelDocuments()
    {
        waitUntilElementsAreVisible(explorerPanelDocumentsList);
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
        waitInSeconds(1);
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
        findFirstElementWithValue(documentsFilterOptions, filter).click();
        for (DocumentsFilters docFilter : DocumentsFilters.values())
            if (docFilter.title.equals(filter))
            {
                waitUntilElementContainsText(navigationBar, docFilter.header);
                break;
            }
        return this;
    }

    /**
     * Click on folder from Document Library --> explorer panel --> Library --> Documents
     *
     * @param folderName folder to be clicked
     */
    public DocumentLibraryPage clickFolderFromExplorerPanel(String folderName)
    {
        WebElement folder = waitUntilElementIsVisible(
            findFirstElementWithExactValue(explorerPanelDocumentsList, folderName));
        folder.click();
        waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return this;
    }

    /**
     * Click on any folder from Document library breadcrumb, except the last one
     *
     * @param folderName folder to be clicked
     */
    public DocumentLibraryPage clickFolderFromBreadcrumb(String folderName)
    {
        findFirstElementWithExactValue(breadcrumbList, folderName).click();
        waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return this;
    }

    /**
     * Click on folderUp icon
     */
    public DocumentLibraryPage clickFolderUpButton()
    {
        folderUpButton.click();
        return this;
    }

    public boolean isMoreMenuDisplayed(String contentName)
    {
        return isElementDisplayed(selectDocumentLibraryItemRow(contentName), moreMenuSelector);
    }

    /**
     * Method that is getting a list of action  for any content (eg. "Download as Zip", "View Details", "Manage Permissions", etc)
     *
     * @param libraryItem - the content (file/ folder) from where the actions will be collected in the 'list'
     * @return - the name of all available menu action
     */
    private List<WebElement> getAvailableActions(String libraryItem)
    {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        clickOnMoreActions(itemRow);
        return itemRow.findElements(actionsSet);
    }

    public boolean isActionAvailableForLibraryItem(String libraryItem, ItemActions action)
    {
        return isElementDisplayed(
            findFirstElementWithValue(getAvailableActions(libraryItem), action.getActionName()));
    }

    public boolean areActionsAvailableForLibraryItem(String libraryItem, List<String> actions)
    {
        List<String> availableActions = getAvailableActions(libraryItem).stream()
                                                                        .map(action -> action.getText())
                                                                        .collect(Collectors.toList());
        return availableActions.containsAll(actions);
    }

    public boolean areActionsNotAvailableForLibraryItem(String libraryItem, List<String> actions)
    {
        List<WebElement> availableActions = getAvailableActions(libraryItem);
        boolean notFound = true;
        for (String action : actions)
        {
            if (findFirstElementWithValue(availableActions, action) != null)
            {
                log.info(String.format("Action '%s' is available!", action));
                return false;
            }
        }
        return notFound;
    }

    /**
     * Helper method to determine if the specified action is found in more actions container
     */
    private boolean isActionInMoreActionsContainer(ItemActions action)
    {
        WebElement actionElement;
        try
        {
            waitUntilElementIsVisible(moreSelector);
            By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR_MORE, action.getActionLocator()));
            actionElement = waitUntilElementIsPresent(actionSelector);
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            return false;
        }
        return actionElement != null;
    }

    /**
     * Click on the more actions link
     */
    private void clickOnMoreActions(WebElement libraryItem)
    {
        try
        {
            Utils.retry(
                    () -> {
                        WebElement moreAction = waitUntilChildElementIsPresent(libraryItem, moreSelector);
                        mouseOver(moreAction);
                        clickElement(moreAction);
                        // wait for the actions to show
                        return waitUntilChildElementIsPresent(libraryItem, moreActionsMenu);
                    }, WAIT_3.getValue());

        }
        catch (TimeoutException e)
        {
            // do nothing
        }
    }

    /**
     * Click on the given action for an item.
     * This method is used for actions that redirect to a page!
     *
     * @param contentItem content item to apply action
     * @param action      to be clicked on
     * @return render redirected page
     */
    public void clickDocumentLibraryItemAction(String contentItem, ItemActions action)
    {
        WebElement libraryItem = mouseOverContentItem(contentItem);
        WebElement actionElement;
        By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR, action.getActionLocator()));

        if(isActionInMoreActionsContainer(action))
        {
            // click on "more actions" if it is
            clickOnMoreActions(libraryItem);
        }
        try
        {
            actionElement = waitUntilElementIsVisible(actionSelector);
        }
        catch (TimeoutException timeoutException)
        {
            throw new TimeoutException("The action " + action.getActionName() + " could not be found for list item " + contentItem);
        }

        mouseOver(actionElement);
        clickElement(actionElement);
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
        return this;
    }

    public boolean isFileFavorite(String fileName)
    {
        return selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).getAttribute("class").contains("enabled");
    }

    private void mouseOverContentName(String content)
    {
        try
        {
            mouseOver(selectDocumentLibraryItemRow(content).findElement(contentNameSelector));
            waitUntilElementIsVisible(renameIcon);
        }
        catch (TimeoutException e)
        {
            //ignore the exception for the cases when icon should not appear
        }
    }
    public boolean isRenameIconDisplayed(String content)
    {
        try
        {
            return retryUntil(() -> {
                        mouseOverContentName(content);
                        return true;
                    },
                    () -> isElementDisplayed(renameIcon),
                    WAIT_3.getValue());
        }
        catch (RuntimeException runtimeException)
        {
            return false;
        }
    }

    /**
     * Click on 'Rename' icon from the right of content name
     */
    public void clickRenameIcon(String contentName)
    {
        WebElement renameIconElement = selectDocumentLibraryItemRow(contentName).findElement(renameIcon);
        renameIconElement.click();
        waitUntilElementIsVisible(contentNameInputField);
    }

    /**
     * Verify content name is text input field
     */
    public boolean isContentNameInputField()
    {
        return isElementDisplayed(contentNameInputField);
    }

    /**
     * Type new name in content name text input field
     *
     * @param newContentName
     */
    public void typeContentName(String newContentName)
    {
        WebElement contentNameInput = waitUntilElementIsVisible(contentNameInputField);
        Utils.clearAndType(contentNameInput, newContentName);
    }

    /**
     * Click on button from 'Rename'
     *
     * @param buttonName to be clicked (Save/Cancel)
     */
    public DocumentLibraryPage clickButtonFromRenameContent(String buttonName)
    {
        findFirstElementWithValue(buttonsFromRenameContent, buttonName).click();
        return this;
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
        waitInSeconds(1);
    }

    public boolean isContentSelected(String contentName)
    {
        waitInSeconds(1);
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
     * verifyContentItemsNotSelected
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
        WebElement element = waitUntilElementIsVisible(selectDocumentLibraryItemRow(documentLibraryItem).findElement(By.cssSelector("[class=status] img")));
        return isElementDisplayed(element);
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

    public String getTags(String contentName)
    {
        waitInSeconds(8);
        waitUntilWebElementIsDisplayedWithRetry(findElement(contentTagsSelector), 5);
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
            {
                waitInSeconds(20);
                refresh();
                By tagRows = By.cssSelector("ul.filterLink li span.tag a");
                waitUntilElementsAreVisible(tagRows);
                for (WebElement tagsElem : findElements(tagRows))
                {
                    allTagsNames.add(tagsElem.getText());
                }
                break;
            } catch (TimeoutException | NoSuchElementException e)
            {
                refresh();
                counter++;
            }
        }
        return allTagsNames;
    }

    /**
     * For a content, click on any tag and type a valid tag name
     *
     * @param tagName    tag to be edited
     * @param newTagName new value for tag
     */
    public void editTag(String tagName, String newTagName)
    {
        List<WebElement> tagsList = waitUntilElementsAreVisible(inlineEditTagsSelector);

        WebElement tagElement = findFirstElementWithExactValue(tagsList, tagName);
        Parameter.checkIsMandotary("Tag", tagElement);
        tagElement.click();
        waitUntilElementIsVisible(tagToBeEdited);
        tagToBeEdited.clear();
        waitInSeconds(1);
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
                return isElementDisplayed(noTags);
            } catch (TimeoutException | NoSuchElementException e)
            {
                log.error("Action not found:" + e);
            }
            counter++;
            refresh();
        }
        return false;
    }

    public void mouseOverTags(String contentName)
    {
        WebElement contentElement = selectDocumentLibraryItemRow(contentName);
        Parameter.checkIsMandotary("Content selector", contentElement);
        WebElement tagElement = contentElement.findElement(contentTagsSelector);
        Parameter.checkIsMandotary("Tag selector", tagElement);
        mouseOver(tagElement);
        waitUntilElementIsVisible(contentElement.findElement(editTagSelector));
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
        mouseOver(tagElement);
        waitUntilElementIsVisible(contentElement.findElement(editTagSelector));
    }

    public boolean isEditTagInputFieldDisplayed()
    {
        waitUntilElementIsVisible(editTagInputField);
        return isElementDisplayed(editTagInputField);
    }

    public void typeTagName(String tagName)
    {
        waitInSeconds(5);
        Utils.clearAndType(waitUntilElementIsVisible(editTagInputField), tagName);
        editTagInputField.sendKeys(Keys.RETURN);
    }

    public DocumentLibraryPage clickEditTagLink(String linkName)
    {
        waitInSeconds(1);
        WebElement link = findFirstElementWithValue(editTagButtons, linkName);
        clickElement(link);
        waitInSeconds(2);
        return this;
    }

    public void clickOnTag(String tagName)
    {
        waitUntilElementIsVisible(
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
        return isElementDisplayed(selectDocumentLibraryItemRow(contentName), editTagSelector);
    }

    /**
     * Click on 'Edit Tag' icon for a content item
     *
     * @param contentName
     */
    public void clickEditTagIcon(String contentName)
    {
        WebElement editTagIcon = selectDocumentLibraryItemRow(contentName).findElement(editTagSelector);
        clickElement(editTagIcon);
    }

    /**
     * Switch to new window and get page content
     */
    public String switchToNewWindowAngGetContent()
    {
        String content = null;

        if (!getWindowHandles().isEmpty())
        {
            switchWindow(1);
            content = findElement(By.xpath("//body")).getText();
            closeWindowAndSwitchBack();
        }
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
        List<WebElement> tagsList = waitUntilElementsAreVisible(inlineEditTagsSelector);
        List<WebElement> removeIconList = waitUntilElementsAreVisible(removeTagIconSelector);

        for (int i = 0; i < tagsList.size(); i++)
        {
            if (tagsList.get(i).getText().equalsIgnoreCase(tagName))
            {
                removeIconList.get(i);
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
        return new DocumentDetailsPage(webDriver);
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
        WebElement bannerElement = waitUntilChildElementIsPresent(fileElement, infoBanner);
        Parameter.checkIsMandotary("File banner", bannerElement);
        return bannerElement.getText();
    }

    /**
     * Method to check if the info banner is displayed
     */

    public boolean isInfoBannerDisplayed(String fileName)
    {
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), infoBanner);
    }

    /**
     * Method to get the locked by user name
     */
    public String getLockedByUserName(String fileName)
    {
        return waitUntilChildElementIsPresent(selectDocumentLibraryItemRow(fileName),lockedByUser).getText();
    }

    /**
     * Method to check if a view option is present
     */

    public boolean isViewOptionDisplayed(String view)
    {
        return isElementDisplayed(selectViewInOptions(view));
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
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), favoriteLink);
    }

    public boolean isLikeButtonDisplayed(String fileName)
    {
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), likeButton);
    }

    public boolean isCommentButtonDisplayed(String fileName)
    {
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), commentButton);
    }

    public boolean isShareButtonDisplayed(String fileName)
    {
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), By.cssSelector("a[class='quickshare-action']"));
    }

    public String getOptionsSetDefaultViewText(String text)
    {
        waitUntilElementIsDisplayedWithRetry(optionsMenuDropDown);
        waitUntilElementContainsText(setDefaultView, text);
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
        return isElementDisplayed(downArrowPointer);
    }

    public boolean isRightArrowPointerDisplayed()
    {
        return isElementDisplayed(rightArrowPointer);
    }

    public void clickTheRightArrowPointer()
    {
        rightArrowPointer.click();
    }

    public boolean isLeftArrowPointerDisplayed()
    {
        return isElementDisplayed(leftArrowPointer);
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
        return isElementDisplayed(sortButton);
    }

    public boolean isCreateButtonDisplayed()
    {
        return isElementDisplayed(createButton);
    }

    public boolean isSortByFieldButtonDisplayed()
    {
        return isElementDisplayed(sortByFieldButton);
    }

    public boolean isPaginationDisplayed()
    {
        return isElementDisplayed(currentPage);
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
        List<WebElement> linkToFolderList = waitUntilElementsAreVisible(linkToFolderLocator);
        findFirstElementWithExactValue(linkToFolderList, folderName).click();
    }

    public boolean isCreateContentMenuDisplayed()
    {
        return isElementDisplayed(createContentMenu);
    }

    public void clickCreateButtonWithoutWait()
    {
        waitUntilElementIsVisible(createButton);
    }

    public boolean isFileDisplayed(String fileName)
    {
        try
        {
            waitUntilElementIsDisplayedWithRetry(By.cssSelector("td[class$='yui-dt-col-fileName']"), 5);
            WebElement webElement = selectDocumentLibraryItemRow(fileName);

            return isElementDisplayed(webElement);
        } catch (NoSuchElementException ex)
        {
            log.info("Element not found " + ex.getMessage());
            return false;
        }
    }

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
}