package org.alfresco.po.share.site;

import static org.alfresco.common.Utils.retryUntil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Function;

import org.alfresco.common.DataUtil;
import org.alfresco.common.Timeout;
import org.alfresco.common.Utils;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.common.Parameter;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

@Primary
@PageObject
public class DocumentLibraryPage extends SiteCommon<DocumentLibraryPage>
{
    public enum CreateMenuOption
    {
        FOLDER(By.cssSelector("span.folder-file")),
        PLAIN_TEXT(By.cssSelector("span.text-file")),
        HTML(By.cssSelector("span.html-file")),
        XML(By.cssSelector("span.xml-file")),
        GOOGLE_DOCS_DOCUMENT(By.cssSelector("span.document-file")),
        GOOGLE_DOCS_SPREADSHEET(By.cssSelector("span.spreadsheet-file")),
        GOOGLE_DOCS_PRESENTATION(By.cssSelector("span.presentation-file"));

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
    @Autowired
    CreateContent createContent;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    private UploadFileDialog uploadDialog;
    @Autowired
    private NewContentDialog newContentDialog;

    private static final String ACTION_SELECTOR = "div[id*='default-actions']:not([class*='hidden'])>.action-set .{0}>a";
    private static final String ACTION_SELECTOR_MORE = "div.more-actions>.{0}>a";
    /** more actions */
    private static By moreSelector = By.cssSelector("div[id*='default-actions']:not([class*='hidden']) a.show-more");
    private static By moreActionsMenu = By.cssSelector("div[id*='default-actions']:not([class*='hidden'])>.action-set>.more-actions");

    public By createContentMenu = By.cssSelector("div[id*='_default-createContent-menu'].visible");
    public By editTagSelector = By.cssSelector("td .detail span[class='insitu-edit']:first-child");
    @FindBy (css = "[id$='default-fileUpload-button-button']")
    protected WebElement uploadButton;
    @RenderWebElement
    @FindBy (css = "button[id$='default-options-button-button']")
    protected WebElement optionsMenu;
    @FindBy (css = ".hideFolders")
    protected WebElement hideFoldersMenuOption;
    protected By likeButton = By.cssSelector("a.like-action");

    @FindAll (@FindBy (css = "a.filter-link"))
    private List<WebElement> documentsFilterOptions;
    @RenderWebElement
    @FindBy (css = "div[id$='default-navBar']")
    private WebElement navigationBar;
    @RenderWebElement
    @FindBy (css = "div[id$='paginatorBottom'] span[class$='current']")
    private WebElement paginator;
    @RenderWebElement
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
    @FindAll (@FindBy (css = ".filter-change:nth-child(1)"))
    private List<WebElement> foldersList;
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
            if (!browser.isElementDisplayed(option.getLocator()))
            {
                LOG.info("Create menu option: %s was not found", option.name() );
                return false;
            }
        }
        return true;
    }

    /**
     * Method to click Plain Text, HTML or XML option from create menu
     */
    public CreateContent clickCreateContentOption(CreateMenuOption option)
    {
        browser.waitUntilElementClickable(option.getLocator(), WAIT_15_SEC).click();
        return (CreateContent) createContent.renderedPage();
    }

    /**
     * Method to click on create google docs options from create menu
     */
    public GoogleDocsCommon clickGoogleDocsOption(CreateMenuOption option)
    {
        browser.waitUntilElementClickable(option.getLocator(), WAIT_15_SEC).click();
        return (GoogleDocsCommon) googleDocs.renderedPage();
    }

    public NewContentDialog clickFolderLink()
    {
        browser.waitUntilElementClickable(CreateMenuOption.FOLDER.getLocator(), WAIT_15_SEC).click();
        return (NewContentDialog) newContentDialog.renderedPage();
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
        LOG.info("Item count could not be determined. Assuming 0 items on this page.");
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
                LOG.debug("BrowseList still rendering - item count is not yet displayed");
            }
            return (itemCount == rows.size());
        };

        // wait until we have the expected number of rows
        new FluentWait<>(browser)
                .withTimeout(Timeout.VERY_LONG.getTimeoutSeconds(), TimeUnit.SECONDS)
                .pollingEvery(Timeout.VERY_SHORT.getTimeoutSeconds(), TimeUnit.SECONDS)
                .until(rowsAvailable);
    }


    private WebElement selectViewInOptions(String viewName)
    {
        return browser.findElement(By.xpath("//div[contains(@id, '_default-options-menu')]//ul[@class= 'first-of-type']//span[text()='" + viewName + "']"));
    }

    private WebElement findItemInCarrouselFilmstripView(String contentName)
    {
        return browser.findElement(By.xpath("//div[contains(@class, 'alf-filmstrip-nav-item-thumbnail')]//div[text()='" + contentName + "']"));
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
        return browser.isElementDisplayed(browser.waitUntilElementVisible(uploadButton));
    }

    public boolean isContentNameDisplayed(String contentName)
    {
        try
        {
            //the content might not be in the Document Library list due to SOLR indexes
            if (selectDocumentLibraryItemRow(contentName) == null)
            {
                //refresh the current page until the file is found in the Document Library list
                retryUntil(() -> {
                            browser.refresh();
                            return this.renderedPage();
                        },
                        () -> (selectDocumentLibraryItemRow(contentName) != null),
                        DEFAULT_RETRY);
            }
            return true;
        }
        catch (RuntimeException ex)
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
        return (DocumentLibraryPage) clickUpload().uploadFileAndRenderPage(pathToPhoto, this);
    }

    public boolean isDocumentListDisplayed()
    {
        return browser.isElementDisplayed(documentList);
    }

    /**
     * Verify presence of "Options" menu
     *
     * @return true if displayed or false if is not.
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
        waitForRows();
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
    public WebElement mouseOverContentItem(String contentItem)
    {
        WebElement contentItemElement = selectDocumentLibraryItemRow(contentItem);
        Parameter.checkIsMandotary("Content item", contentItemElement);
        browser.mouseOver(contentItemElement.findElement(contentNameSelector));

        return Utils.retry(() ->
        {
            browser.mouseOver(contentItemElement);
            WebDriverWait wait = new WebDriverWait(getBrowser(), Timeout.MEDIUM.getTimeoutSeconds());
            wait.until(ExpectedConditions.attributeContains(contentItemElement, "class", "yui-dt-highlighted"));

            return contentItemElement;
        }, DEFAULT_RETRY);
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

    public void clickCreateButton()
    {
        browser.waitUntilElementClickable(createButton, 10).click();
        browser.waitUntilElementVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
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

    public boolean isMoreMenuDisplayed(String contentName)
    {
        return browser.isElementDisplayed(selectDocumentLibraryItemRow(contentName), moreMenuSelector);
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
        return browser.isElementDisplayed(browser.findFirstElementWithValue(getAvailableActions(libraryItem), action.getActionName()));
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
            if (browser.findFirstElementWithValue(availableActions, action) != null)
            {
                LOG.info(String.format("Action '%s' is available!", action));
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
            browser.waitUntilElementVisible(moreSelector, Timeout.MEDIUM.getTimeoutSeconds());
            By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR_MORE, action.getActionLocator()));
            actionElement = browser.waitUntilElementIsPresent(actionSelector, Timeout.MEDIUM.getTimeoutSeconds());
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
            WebElement moreAction = browser.waitUntilChildElementIsPresent(libraryItem, moreSelector, Timeout.MEDIUM.getTimeoutSeconds());
            Utils.retry(
                    () -> {
                        browser.mouseOver(moreAction);
                        browser.waitUntilElementClickable(moreAction, Timeout.MEDIUM.getTimeoutSeconds()).click();
                        // wait for the actions to show
                        return browser.waitUntilChildElementIsPresent(libraryItem, moreActionsMenu, Timeout.MEDIUM.getTimeoutSeconds());
                    }, DEFAULT_RETRY);

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
     * @param page        to be rendered after click
     * @return render redirected page
     */
    public HtmlPage clickDocumentLibraryItemAction(String contentItem, ItemActions action, HtmlPage page)
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
            actionElement = browser.waitUntilElementVisible(actionSelector, WAIT_15_SEC);
        }
        catch (TimeoutException timeoutException)
        {
            throw new RuntimeException("The action " + action.getActionName() + " could not be found for list item " + contentItem);
        }

        browser.mouseOver(actionElement);

        if ((action.equals(ItemActions.DOWNLOAD) || action.equals(ItemActions.DOWNLOAD_AS_ZIP)) && properties.isGridEnabled())
        {
            ((RemoteWebDriver)(getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }

        browser.waitUntilElementClickable(actionElement).click();
        if(page instanceof DocumentLibraryPage)
        {
            waitUntilMessageDisappears();
        }
        return page.renderedPage();
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

    private void mouseOverContentName(String content)
    {
        try
        {
            browser.mouseOver(selectDocumentLibraryItemRow(content).findElement(contentNameSelector));
            browser.waitUntilElementVisible(renameIcon, Timeout.MEDIUM.getTimeoutSeconds());
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
                    () -> browser.isElementDisplayed(renameIcon),
                    DEFAULT_RETRY);
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
        Utils.clearAndType(contentNameInput, newContentName);
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
        WebElement element = browser.waitUntilElementVisible(selectDocumentLibraryItemRow(documentLibraryItem).findElement(By.cssSelector("[class=status] img")));
        return browser.isElementDisplayed(element);
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
        getBrowser().waitInSeconds(8);
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
            {
                browser.waitInSeconds(20);
                browser.refresh();
                By tagRows = By.cssSelector("ul.filterLink li span.tag a");
                getBrowser().waitUntilElementsVisible(tagRows);
                for (WebElement tagsElem : browser.findElements(tagRows))
                {
                    allTagsNames.add(tagsElem.getText());
                }
                break;
            } catch (TimeoutException | NoSuchElementException e)
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
     * @param tagName    tag to be edited
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
            } catch (TimeoutException | NoSuchElementException e)
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
        Utils.clearAndType(browser.waitUntilElementVisible(editTagInputField), tagName);
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
        browser.waitUntilElementClickable(editTagIcon).click();
    }

    /**
     * Switch to new window and get page content
     */
    public String switchToNewWindowAngGetContent()
    {
        getBrowser().waitInSeconds((int) properties.getImplicitWait());
        String content = null;

        if (browser.getWindowHandles().size() >= 1)
        {
            browser.switchWindow(1);
            content = browser.findElement(By.xpath("//body")).getText();
            browser.closeWindowAndSwitchBack();
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
        WebElement bannerElement = browser.waitUntilChildElementIsPresent(fileElement, infoBanner);
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
    public String getLockedByUserName(String fileName)
    {
        return browser.waitUntilChildElementIsPresent(selectDocumentLibraryItemRow(fileName),lockedByUser).getText();
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
        return browser.isElementDisplayed(downArrowPointer);
    }

    public boolean isRightArrowPointerDisplayed()
    {
        return browser.isElementDisplayed(rightArrowPointer);
    }

    public void clickTheRightArrowPointer()
    {
        rightArrowPointer.click();
    }

    public boolean isLeftArrowPointerDisplayed()
    {
        return browser.isElementDisplayed(leftArrowPointer);
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
        return browser.isElementDisplayed(sortButton);
    }

    public boolean isCreateButtonDisplayed()
    {
        return browser.isElementDisplayed(createButton);
    }

    public boolean isSortByFieldButtonDisplayed()
    {
        return browser.isElementDisplayed(sortByFieldButton);
    }

    public boolean isPaginationDisplayed()
    {
        return browser.isElementDisplayed(currentPage);
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
        try
        {
            getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("td[class$='yui-dt-col-fileName']"), 5);
            WebElement webElement = selectDocumentLibraryItemRow(fileName);

            return browser.isElementDisplayed(webElement);
        } catch (NoSuchElementException ex)
        {
            LOG.info("Element not found " + ex.getMessage());
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