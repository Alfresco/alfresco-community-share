package org.alfresco.po.share.site;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Utils.retryUntil;
import static org.alfresco.common.Wait.WAIT_3;
import static org.alfresco.common.Wait.WAIT_5;
import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import com.google.common.base.Function;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.common.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

@Slf4j
public class DocumentLibraryPage extends SiteCommon<DocumentLibraryPage> // TODO to be deleted
{
    protected static final String ACTION_SELECTOR = "div[id*='default-actions']:not([class*='hidden'])>.action-set .{0}>a";
    protected static final String ACTION_SELECTOR_MORE = "div[id*='default-actions']:not([class*='hidden']) div.more-actions>.{0}>a";
    private final By createButton = By.cssSelector("button[id$='createContent-button-button']");
    private final String contentTableRow = "//td[contains(@class, 'yui-dt-col-name')]//a[text()='%s']/../../../..";
    private final String contentNameInTableRow = "//td[contains(@class, 'yui-dt-col-name')]//a[text()='%s']";
    private final String contentNameInRow = "//td[contains(@class, 'yui-dt-col-fileName')]//a[text()='%s']";
    private final String contentNameInGalleryView = "//div[contains(@class, 'alf-gallery-item-thumbnail')]//a[text()='%s']";
    public By createContentMenu = By.cssSelector("div[id*='_default-createContent-menu'].visible");
    public By editTagSelector = By.cssSelector("td .detail span[class='insitu-edit']:first-child");
    public By uploadButton = By.cssSelector("[id$='default-fileUpload-button-button']");
    public By tagValue = By.xpath("//*[@id=\"template_x002e_document-tags_x002e_document-details_x0023_default-heading\"]/span/a");
    public By tagSelectButton = By.id("yui-gen7-button");
    public By createNewTag = By.id("alf-id0");
    public By tagCorrectIcon = By.className("createNewIcon");
    public By removeTagIcon = By.className("removeIcon");
    public By tagOkButton = By.id("template_x002e_edit-metadata_x002e_edit-metadata_x0023_default_prop_cm_taggable-cntrl-ok");

    @FindAll(@FindBy(css = ".documentDroppable .ygtvlabel"))
    public List<WebElement> explorerPanelDocumentsList;
    /**
     * more actions
     */
    protected By moreSelector = By.cssSelector("div[id*='default-actions']:not([class*='hidden']) a.show-more");
    @FindBy(css = "button[id$='default-options-button-button']")
    protected WebElement optionsMenu;
    @FindBy(css = ".hideFolders")
    protected WebElement hideFoldersMenuOption;
    protected By likeButton = By.cssSelector("a.like-action");
    protected By commentButton = By.cssSelector("a.comment");
    private GoogleDocsCommon googleDocs;
    private CreateContentPage createContent;
    private DocumentDetailsPage documentDetailsPage;
    private UploadFileDialog uploadDialog;
    private NewFolderDialog newContentDialog;
    private By dataList = By.cssSelector("#HEADER_SITE_DATA-LISTS_text > a");
    private By contactList = By.xpath("//a[text()=\"Contact List\"]");
    private By TypeTitle = By.xpath("//input[@name=\"prop_cm_title\"]");
    private By clickSaveButton = By.xpath("//button[text()=\"Save\"]");
    private By moreActionsMenu = By.cssSelector("div[id*='default-actions']:not([class*='hidden'])>.action-set>.more-actions");
    private By uploadButton_ = By.cssSelector("[id$='default-fileUpload-button-button']");
    private By viewDetailAction = By.xpath("//div[contains(@class, 'alf-gallery-item alf-hover')]//a[@class='alf-show-detail']");
    @FindAll(@FindBy(css = "a.filter-link"))
    private List<WebElement> documentsFilterOptions;
    @FindBy(css = "div[id$='default-navBar']")
    private WebElement navigationBar;
    @FindBy(css = "div[id$='paginatorBottom'] span[class$='current']")
    private WebElement paginator;
    @FindBy(css = "div[id$='_default-dl-body']")
    private WebElement docListContainer;
    @FindBy(css = ".documents[id$='_default-documents']")
    private WebElement documentList;
    @FindBy(css = "div[id$='default-options-menu'] span")
    private List<WebElement> optionsList;
    private By optionsMenuDropDown = By.cssSelector("div[id*='default-options-menu'].visible");
    private By displayedOptionsListBy = By.xpath("//div[contains(@id, 'default-options-menu')]//li[not(contains(@class, 'hidden'))]");
    private By foldersList = By.cssSelector(".filter-change:nth-child(1)");
    private By filesList = By.cssSelector(".filename a[href*='document-details']");
    private By documentLibraryItemsList = By.cssSelector("div[id$='default-documents'] tbody[class$='data'] tr");
    private By geolocationMetadataIcon_ = By.cssSelector("div[class ='status'] img[title ='Geolocation metadata available']");
    private By googleMap_ = By.cssSelector("div[class ='google-map']");
    private By googleMapPopUp_ = By.cssSelector("div[id*='_default-info'] div[class='thumbnail'] a[href*='document-details']");
    private By documentList_ = By.cssSelector(".documents[id$='_default-documents']");
    private By optionMenu_ = By.cssSelector("button[id$='default-options-button-button']");
    private By hideFoldersMenuOption_ = By.cssSelector(".hideFolders");
    private By explorerPanel_Documents = By.cssSelector(".documentDroppable .ygtvlabel");
    private List<WebElement> breadcrumbList = findElements((By.xpath("//div[contains(@class, 'crumb')]/a[@class='folder']")));
    private By breadcrumbList1 = By.xpath("//div[contains(@class, 'crumb')]/a[@class='folder']");
    private By breadcumbCurrentFolder = By.cssSelector(".crumb .label a");
    @FindBy(css = "button[id*='folderUp']")
    private WebElement folderUpButton;
    private By contentNameInputField = By.cssSelector("input[id*='form-field']");
    @FindBy(css = ".insitu-edit a")
    private List<WebElement> buttonsFromRenameContent;
    @FindBy(css = ".inlineTagEditAutoCompleteWrapper input")
    private WebElement editTagInputField;
    private By editTagInputField_ = By.cssSelector(".inlineTagEditAutoCompleteWrapper input");
    private By editTagButtons = By.cssSelector("form[class='insitu-edit'] a");
    private By moreActionButton = By.xpath("//span[text()='More...']");
    private By tagToBeEdited = By.cssSelector(".inlineTagEditAutoCompleteWrapper input");
    private By clickCancelButton = By.id("template_x002e_edit-metadata_x002e_edit-metadata_x0023_default-form-cancel-button");
    private By commentsCount = By.cssSelector("[class=comment-count]");
    @FindBy(css = "div[class ='google-map']")
    private WebElement googleMap;
    @FindBy(css = "div[id*='_default-info'] div[class='thumbnail'] a[href*='document-details']")
    private WebElement googleMapPopUp;
    @FindBy(css = "div[class ='status'] img[title ='Geolocation metadata available']")
    private WebElement geolocationMetadataIcon;
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
    @FindBy(css = ".yui-dt-col-fileName")
    private List<WebElement> nrOfSharedElements;
    private By renameIcon = By.cssSelector(".filename span.insitu-edit[style*='visibility: visible']");
    private By renameIconInList = By.xpath("//span[contains(@style, 'visibility: visible')]");
    private By linkToFolderLocator = By.cssSelector(".filename [href*='FdocumentLibrary']");
    private By moreMenuSelector = By.cssSelector("div[class='action-set detailed'] div[id*='onActionShowMore'] a span");
    private By noTagsSelector = By.cssSelector("td[class*='fileName'] .detail .item .faded");
    private By contentTagsSelector = By.cssSelector(".item .tag-link");
    private By inlineEditTagsSelector = By.cssSelector(".inlineTagEditTag span");
    private By removeTagIconSelector = By.cssSelector(".inlineTagEditTag img[src*='delete-item-off']");
    private By deleteFileFolderPrompt = By.xpath("//div[@id='prompt']/div[contains(text(),'Delete')]");
    private By deleteButtonPrompt = By.xpath("//div[@id='prompt']//button[contains(text(),'Delete')]");
    private By contentNameSelector = By.cssSelector(".filename a");
    private By checkBoxSelector = By.cssSelector("tbody[class='yui-dt-data'] input[id*='checkbox']");
    private By favoriteLink = By.className("favourite-action");
    private By actionsSet = By.cssSelector(".action-set a span");
    private By categoriesDetails = By.cssSelector("div.detail span.category");
    private By infoBanner = By.cssSelector("div[class='info-banner']");
    private By lockedByUser = By.cssSelector("div.info-banner a");
    private By titleSelector = By.cssSelector("td .title");
    private By descriptionSelector = By.cssSelector("td .detail:nth-child(3) span");
    private By listItemData = By.className("yui-dt-liner");
    private By tagToggleIcon = By.id("template_x002e_document-tags_x002e_document-details_x0023_default-heading");
    private By tag = By.xpath("//*[@id=\"template_x002e_document-tags_x002e_document-details_x0023_default-body\"]/div");
    private By galleryViewDocumentList = By.cssSelector("div[class ='alf-gallery-item']");
    private By addCommentButton = By.id("yui-gen8-button");
    private By perviousCommentPage = By.xpath("(//a[@class='yui-pg-previous'])[1]");
    private By nextCommentPage = By.xpath("//a[@class='yui-pg-next' and @title='Next Page']");
    private By saveComment = By.id("template_x002e_comments_x002e_document-details_x0023_default-add-submit-button");
    private final By commentContentIframe = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private By commentCount = By.xpath("(//span[@class='yui-pg-current'])[1]");
    private By documentActionIcon = By.xpath("//*[@id=\"template_x002e_document-actions_x002e_document-details_x0023_default-heading\"][1]");

    public DocumentLibraryPage(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
        PageFactory.initElements(getWebDriver(), this);
    }

    public boolean areCreateOptionsAvailable() {
        for (CreateMenuOption option : CreateMenuOption.values()) {
            if (!isElementDisplayed(option.getLocator())) {
                log.info("Create menu option: %s was not found", option.name());
                return false;
            }
        }
        return true;
    }

    public DocumentLibraryPage assertareCreateOptionsAvailable() {
        log.info("Assert Create menu options are available ");
        assertTrue(areCreateOptionsAvailable(), "Create menu options are not available");
        return this;
    }

    public UploadFileDialog createContactDataList(String listName) {
        waitUntilElementIsVisible(dataList).click();
        findElement(contactList).click();
        findElement(TypeTitle).sendKeys(listName);
        clickElement(clickSaveButton);
        return new UploadFileDialog(webDriver);
    }

    /**
     * Method to click Plain Text, HTML or XML option from create menu
     */
    public CreateContentPage clickCreateContentOption(CreateMenuOption option) {
        clickElement(option.getLocator());
        return new CreateContentPage(webDriver);
    }

    public WebDriver getWebDriver() {
        return webDriver.get();
    }

    /**
     * Method to click on create google docs options from create menu
     */
    public GoogleDocsCommon clickGoogleDocsOption(CreateMenuOption option) {
        clickElement(option.getLocator());
        return (GoogleDocsCommon) googleDocs.renderedPage();
    }

    public NewFolderDialog clickFolderLink() {
        clickElement(CreateMenuOption.FOLDER.getLocator());
        return new NewFolderDialog(webDriver);
    }

    /**
     * Method to click on Create Document/Folder from Template
     */
    public void clickCreateFromTemplateOption(CreateMenuOption option) {
        Utils.retry(() -> {
            WebElement optionElement = waitUntilElementIsVisible(option.getLocator());
            // need sometime to mouse over another element before mouse over create from Template option because the
            // list of templates doesn't appear
            mouseOver(createButton);
            mouseOver(optionElement);
            clickElement(optionElement);
            return waitUntilElementIsVisible(By.cssSelector(".yuimenuitemlabel-hassubmenu-selected+.yuimenu.visible"));
        }, WAIT_3.getValue());
    }

    private WebElement selectTemplate(String templateName) {
        return waitUntilElementIsVisible(By.xpath("//a[@class = 'yuimenuitemlabel']//span[text()='" + templateName + "']"));
    }

    /**
     * Method to check if the template is present
     */
    public boolean isTemplateDisplayed(String templateName) {
        return isElementDisplayed(selectTemplate(templateName));
    }

    /**
     * Method to select template
     */
    public void clickOnTemplate(String templateName) {
        clickElement(selectTemplate(templateName));
       /* if (page instanceof DocumentLibraryPage)
        {
            waitUntilNotificationMessageDisappears();
        }*/
    }

    /**
     * Helper method to get the number of items that should be on the page, according to the item count at the bottom.
     */
    protected int getItemCount() {
        // figure out how many items are on the page
        String text = paginator.getText();
        String[] values = text.split(" ");
        // The original string will be something like "1 - 25 of 32".
        if (values.length == 5) {
            int maxItemOnPage = Integer.parseInt(values[2]);
            // Handle the special case of "0 - 0 of 0"
            if (maxItemOnPage == 0) {
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
    private void waitForRows() {
        // wait predicate
        Function<WebBrowser, Boolean> rowsAvailable = (w) -> {
            List<WebElement> rows = w.findElements(documentLibraryItemsList);
            int itemCount = -1;
            try {
                itemCount = getItemCount();
            } catch (NoSuchElementException e) {
                // This can happen because this method is called while the page is rendering.
                log.debug("BrowseList still rendering - item count is not yet displayed");
            }
            return (itemCount == rows.size());
        };
    }

    private WebElement selectViewInOptions(String viewName) {
        return findElement(By.xpath(
            "//div[contains(@id, '_default-options-menu')]//ul[@class= 'first-of-type']//span[text()='"
                + viewName + "']"));
    }

    private WebElement findItemInCarrouselFilmstripView(String contentName) {
        return findElement(By.xpath(
            "//div[contains(@class, 'alf-filmstrip-nav-item-thumbnail')]//div[text()='" + contentName + "']"));
    }

    public int getNrOfSharedElements() {
        return nrOfSharedElements.size();
    }

    @Override
    public String getRelativePath() {
        return String.format("share/page/site/%s/documentlibrary", getCurrentSiteName());
    }

    public boolean isUploadButtonDisplayed() {
        return isElementDisplayed(waitUntilElementIsVisible(uploadButton));
    }

    public void waitForContent(String contentName) throws InterruptedException {
        //the content might not be in the Document Library list due to SOLR indexes
        if (selectDocumentLibraryItemRow(contentName) == null) {
            //refresh the current page until the file is found in the Document Library list
            Utility.sleep(5000, 30000, () -> {
                refresh();
                assertTrue(selectDocumentLibraryItemRow(contentName) != null);
            });
        }
    }

    public boolean isContentNameDisplayed(String contentName) {
        waitInSeconds(3);
        try {
            waitInSeconds(3);
            waitForContent(contentName);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean isFileNameDisplayed(String fileName) {
        if (selectDocumentLibraryItemRow(fileName) != null) {
            return true;
        } else {
            refresh();
            waitInSeconds(WAIT_5.getValue());
            if (selectDocumentLibraryItemRow(fileName) != null) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Replace the extension of the file. Replace what comes after '.' character with a string representing another extension.
     *
     * @param fileName         the name of the file that will have another extension type
     * @param newFileExtension the expected extension
     * @return the file name with changed extension
     */
    public String replaceFileExtension(String fileName, String newFileExtension) {
        return String.format(StringUtils.substringBefore(fileName, ".") + newFileExtension);
    }

    /**
     * Verify presence of content with exact value for it's name
     */
    public boolean isContentWithExactValuePresent(String content) {
        WebElement webElement = selectDocLibItemWithExactValue(content);
        return isElementDisplayed(webElement);
    }

    public UploadFileDialog clickUpload() {
        waitInSeconds(WAIT_1.getValue());
        waitUntilElementIsVisible(uploadButton_).click();
        return new UploadFileDialog(webDriver);
    }

    public DocumentLibraryPage uploadNewImage(String imagePath) {
        log.info("Upload image to path: {}", imagePath);
        waitInSeconds(WAIT_1.getValue());
        return (DocumentLibraryPage) clickUpload().uploadFile(imagePath, this);
    }

    public boolean isDocumentListDisplayed() {
        return isElementDisplayed(documentList);
    }

    /**
     * Verify presence of "Options" menu
     *
     * @return true if displayed or false if is not.
     */
    public boolean isOptionsMenuDisplayed() {
        return isElementDisplayed(optionsMenu);
    }

    public boolean isHideFoldersMenuOptionDisplayed() {
        boolean elementDisplayed;
        optionsMenu.click();
        elementDisplayed = isElementDisplayed(hideFoldersMenuOption);
        optionsMenu.click();
        return elementDisplayed;
    }

    public DocumentLibraryPage selectViewFromOptionsMenu(String view) {
        optionsMenu.click();
        selectOptionFromFilterOptionsList(view, optionsList);
        return this;
    }

    public List<String> getAllOptionsText() {
        List<String> optionsText = new ArrayList<>();
        List<WebElement> options = findElements(displayedOptionsListBy);

        for (WebElement option : options) {
            optionsText.add(option.getText());
        }
        return optionsText;
    }

    /**
     * This method is used to get the list of folders name
     *
     * @return foldersName
     */
    public List<String> getFoldersList() {
        waitInSeconds(3);
        waitUntilElementIsDisplayedWithRetry(foldersList, WAIT_5.getValue());
        waitForRows();
        List<String> foldersName = new ArrayList<>();
        for (WebElement folder : findElements(foldersList)) {
            foldersName.add(folder.getText());
        }
        return foldersName;
    }

    public WebElement selectDocumentLibraryItemRow(String documentItem) {
        waitForRows();
        List<WebElement> itemsList = findElements(documentLibraryItemsList);
        return findFirstElementWithValue(itemsList, documentItem);
    }

    /**
     * Select document library item matching exact value of content name
     */

    public WebElement selectDocLibItemWithExactValue(String item) {
        waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 2);
        List<WebElement> itemsList = findElements(documentLibraryItemsList);
        return findFirstElementWithExactValue(itemsList, item);
    }

    /**
     * Open any folder from Document Library
     *
     * @param folderName to be clicked on
     */
    public DocumentLibraryPage clickOnFolderName(String folderName) {
        waitInSeconds(3);
        WebElement folderElement = selectDocumentLibraryItemRow(folderName);
        clickElement(folderElement.findElement(contentNameSelector));
        return this;
    }

    /**
     * Mouse over a content name link
     *
     * @param contentItem content item's name link to be hovered
     */
    public WebElement mouseOverContentItem(String contentItem) {
        try {
            waitForContent(contentItem);
        } catch (InterruptedException | AssertionError e) {
            throw new AssertionError("Content " + contentItem + " was not displayed in library page!");
        }
        WebElement contentItemElement = selectDocumentLibraryItemRow(contentItem);
        Parameter.checkIsMandotary("Content item", contentItemElement);
        mouseOver(contentItemElement.findElement(contentNameSelector));

        return Utils.retry(() -> {
            mouseOver(contentItemElement);
            waitUntilElementHasAttribute(contentItemElement, "class", "yui-dt-highlighted");

            return contentItemElement;
        }, WAIT_3.getValue());
    }

    /**
     * This method is used to get the list of files name
     *
     * @return filesName
     */
    public List<String> getFilesList() {
        waitUntilElementIsDisplayedWithRetry(filesList, 6);
        List<String> filesName = new ArrayList<>();
        for (WebElement file : findElements(filesList)) {
            filesName.add(file.getText());
        }
        return filesName;
    }

    public DocumentDetailsPage clickOnFile(String file) {
        waitInSeconds(5);
        WebElement fileElement = selectDocumentLibraryItemRow(file);
        Parameter.checkIsMandotary("File", fileElement);
        clickElement(fileElement.findElement(contentNameSelector));
        return new DocumentDetailsPage(webDriver);
    }

    public void clickCreateButton() {
        clickElement(createButton);
        waitUntilElementIsVisible(By.cssSelector("div[id$='_default-createContent-menu'][style*='visible']"));
    }

    /**
     * Get the list of documents name from Exploratory Panel: Library -> Documents section
     *
     * @return documents string list
     */
    public String getExplorerPanelDocuments() {
        ArrayList<String> foldersTextList = new ArrayList<>();
        List<WebElement> explorerPanelDocumentList = findElements(explorerPanel_Documents);
        for (WebElement anExplorerPanelDocumentsList : explorerPanelDocumentList) {
            foldersTextList.add(anExplorerPanelDocumentsList.getText());
        }
        return foldersTextList.toString();
    }

    /**
     * @return string list containing elements from Document Library breadcrumb
     */
    public String getBreadcrumbList() {
        waitInSeconds(1);
        ArrayList<String> breadcrumbTextList = new ArrayList<>();
        for (WebElement aBreadcrumbList : breadcrumbList) {
            breadcrumbTextList.add(aBreadcrumbList.getText());
        }
        breadcrumbTextList.add(findElement(breadcumbCurrentFolder).getText());

        return breadcrumbTextList.toString();
    }

    /**
     * Click on filter from explorer panel --> Documents
     *
     * @param filter to be clicked
     */
    public DocumentLibraryPage clickDocumentsFilterOption(String filter) {
        findFirstElementWithValue(documentsFilterOptions, filter).click();
        for (DocumentsFilters docFilter : DocumentsFilters.values())
            if (docFilter.title.equals(filter)) {
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
    public DocumentLibraryPage clickFolderFromExplorerPanel(String folderName) {
        waitInSeconds(2);
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
    public DocumentLibraryPage clickFolderFromBreadcrumb(String folderName) {
        findFirstElementWithExactValue(breadcrumbList, folderName).click();
        waitUntilElementContainsText(breadcumbCurrentFolder, folderName);
        return this;
    }

    /**
     * Click on folderUp icon
     */
    public DocumentLibraryPage clickFolderUpButton() {
        folderUpButton.click();
        return this;
    }

    public boolean isMoreMenuDisplayed(String contentName) {
        mouseOverContentItem(contentName);
        return isElementDisplayed(selectDocumentLibraryItemRow(contentName), moreMenuSelector);
    }

    /**
     * Method that is getting a list of action  for any content (eg. "Download as Zip", "View Details", "Manage Permissions", etc)
     *
     * @param libraryItem - the content (file/ folder) from where the actions will be collected in the 'list'
     * @return - the name of all available menu action
     */
    private List<WebElement> getAvailableActions(String libraryItem) {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        waitInSeconds(5);
        clickOnMoreActions(itemRow);
        return itemRow.findElements(actionsSet);
    }

    private List<WebElement> get_AvailableActions(String libraryItem) {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        if (isElementDisplayed(moreActionsMenu)) {
            clickOnMoreActions(itemRow);
        }
        return itemRow.findElements(actionsSet);
    }

    public boolean isActionAvailableForLibraryItem(String libraryItem, ItemActions action) {
        waitInSeconds(3);
        return isElementDisplayed(
            findFirstElementWithValue(getAvailableActions(libraryItem), action.getActionName()));
    }

    public boolean checkActionAvailableForLibraryItem(String libraryItem, ItemActions action) {
        waitInSeconds(3);
        return isElementDisplayed(
            findFirstElementWithValue(get_AvailableActions(libraryItem), action.getActionName()));
    }

    public boolean areActionsAvailableForLibraryItem(String libraryItem, List<String> actions) {
        List<String> availableActions = getAvailableActions(libraryItem).stream()
            .map(action -> action.getText()).collect(Collectors.toList());
        return availableActions.containsAll(actions);
    }

    public boolean areActionsAvailableInLibrary(String libraryItem, List<String> actions) {
        waitInSeconds(3);
        List<String> availableActions = getActions().stream()
            .map(action -> action.getText()).collect(Collectors.toList());
        return availableActions.containsAll(actions);
    }

    private List<WebElement> getActions(String libraryItem) {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        waitInSeconds(5);
        clickOnMoreActions(itemRow);
        return itemRow.findElements(actionsSet);
    }

    public boolean areActionsNotAvailableForLibraryItem(String libraryItem, List<String> actions) {
        List<WebElement> availableActions = getAvailableActions(libraryItem);
        boolean notFound = true;
        for (String action : actions) {
            if (findFirstElementWithValue(availableActions, action) != null) {
                log.info(String.format("Action '%s' is available!", action));
                return false;
            }
        }
        return notFound;
    }

    /**
     * Helper method to determine if the specified action is found in more actions container
     */
    private boolean isActionInMoreActionsContainer(ItemActions action) {
        WebElement actionElement;
        try {
            waitUntilElementIsVisible(moreSelector);
            By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR_MORE, action.getActionLocator()));
            actionElement = waitUntilElementIsPresent(actionSelector);
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
        return actionElement != null;
    }

    /**
     * Click on the more actions link
     */
    private void clickOnMoreActions(WebElement libraryItem) {
        try {
            Utils.retry(() -> {
                WebElement moreAction = waitUntilChildElementIsPresent(libraryItem, moreSelector);
                mouseOver(moreAction);
                clickElement(moreAction);
                // wait for the actions to show
                return waitUntilChildElementIsPresent(libraryItem, moreActionsMenu);
            }, WAIT_3.getValue());

        } catch (TimeoutException e) {
            // do nothing
        }
    }

    public void selectItemAction(String contentItem, ItemActions action) {
        waitInSeconds(3);
        WebElement libraryItem = mouseOverContentItem(contentItem);
        By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR, action.getActionLocator()));
        WebElement actionElement;

        if (isActionInMoreActionsContainer(action)) {
            clickOnMoreActions(libraryItem);
        }

        try {
            actionElement = waitUntilElementIsVisible(actionSelector);
        } catch (TimeoutException timeoutException) {
            throw new TimeoutException(
                "The action " + action.getActionName() + " could not be found for list item " + contentItem);
        }

        mouseOver(actionElement);
        clickElement(actionElement);
    }

    public void selectItemActionFormFirstThreeAvailableOptions(String contentItem, ItemActions action) {
        waitInSeconds(WAIT_3.getValue());
        WebElement libraryItem = mouseOverContentItem(contentItem);
        By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR, action.getActionLocator()));
        WebElement actionElement;

        try {
            actionElement = waitUntilElementIsVisible(actionSelector);
        } catch (TimeoutException timeoutException) {
            throw new TimeoutException(
                "The action " + action.getActionName() + " could not be found for list item " + contentItem);
        }

        mouseOver(actionElement);
        clickElement(actionElement);
    }

    public String getDocumentListHeader() {
        return navigationBar.getText();
    }

    public String getDocumentListMessage() {
        waitInSeconds(2);
        return documentList.getText();
    }

    public String getFavoriteTooltip(String fileName) {
        waitInSeconds(2);
        return selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).getAttribute("title");
    }

    public DocumentLibraryPage clickFavoriteLink(String fileName) {
        selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).click();
        return this;
    }

    public boolean isFileFavorite(String fileName) {
        return selectDocumentLibraryItemRow(fileName).findElement(favoriteLink).getAttribute("class").contains("enabled");
    }

    private void mouseOverContentName(String content) {
        try {
            mouseOver(selectDocumentLibraryItemRow(content).findElement(contentNameSelector));
            waitUntilElementIsVisible(renameIcon);
        } catch (TimeoutException e) {
            //ignore the exception for the cases when icon should not appear
        }
    }

    public boolean isRenameIconDisplayed(String content) {
        try {
            return retryUntil(() -> {
                mouseOverContentName(content);
                return true;
            }, () -> isElementDisplayed(renameIcon), WAIT_3.getValue());
        } catch (RuntimeException runtimeException) {
            return false;
        }
    }

    /**
     * Click on 'Rename' icon from the right of content name
     */
    public void clickRenameIcon(String contentName) {
        mouseOverContentName(contentName);
        WebElement renameIconElement = selectDocumentLibraryItemRow(contentName).findElement(renameIcon);
        renameIconElement.click();
        waitUntilElementIsVisible(contentNameInputField);
    }

    /**
     * Verify content name is text input field
     */
    public boolean isContentNameInputField() {
        return isElementDisplayed(contentNameInputField);
    }

    /**
     * Type new name in content name text input field
     *
     * @param newContentName
     */
    public DocumentLibraryPage typeContentName(String newContentName) {
        WebElement contentNameInput = waitUntilElementIsVisible(contentNameInputField);
        Utils.clearAndType(contentNameInput, newContentName);
        return this;
    }

    /**
     * Click on button from 'Rename'
     *
     * @param buttonName to be clicked (Save/Cancel)
     */
    public DocumentLibraryPage clickButtonFromRenameContent(String buttonName) {
        findFirstElementWithValue(buttonsFromRenameContent, buttonName).click();
        return this;
    }

    /**
     * @param expectedButtons list of expected to be displayed buttons from renaming content by icon
     * @return displayed buttons
     */
    public boolean verifyButtonsFromRenameContent(String... expectedButtons) {
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
    public void clickCheckBox(String contentName) {
        waitInSeconds(3);
        selectDocumentLibraryItemRow(contentName).findElement(checkBoxSelector).click();
        waitInSeconds(1);
    }

    public boolean isContentSelected(String contentName) {
        waitInSeconds(1);
        return selectDocumentLibraryItemRow(contentName).findElement(checkBoxSelector).isSelected();
    }

    /**
     * Check the selected content list is the one expected
     *
     * @param expectedSelectedContentList content to be selected
     * @return
     */
    public String verifyContentItemsSelected(ArrayList<String> expectedSelectedContentList) {
        for (String contentName : expectedSelectedContentList) {
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
    public String verifyContentItemsNotSelected(ArrayList<String> expectedNotSelectedContentList) {
        for (String contentName : expectedNotSelectedContentList) {
            if (isContentSelected(contentName))
                return contentName + " is selected!";
        }
        return expectedNotSelectedContentList.toString();
    }

    public DocumentLibraryPage assertContentItemsNotSelected(ArrayList<String> expectedContentList) {
        log.info("Assert Content Items Not Selected");
        assertEquals(verifyContentItemsNotSelected(expectedContentList), expectedContentList.toString(), "Selected content = ");
        return this;
    }

    public DocumentLibraryPage assertContentItemsSelected(ArrayList<String> expectedContentList) {
        log.info("Assert Content Items Selected");
        assertEquals(verifyContentItemsSelected(expectedContentList), expectedContentList.toString(), "Selected content = ");
        return this;
    }

    public boolean isActiveWorkflowsIconDisplayed(String documentLibraryItem) {
        waitInSeconds(WAIT_1.getValue());
        WebElement element = waitUntilElementIsVisible(
            selectDocumentLibraryItemRow(documentLibraryItem).findElement(By.cssSelector("[class=status] img")));
        return isElementDisplayed(element);
    }

    /**
     * Gets selected categories listed below the given documentLibraryItem name
     *
     * @param documentLibraryItem
     * @return
     */
    public List<String> getItemCategories(String documentLibraryItem) {
        List<String> categoriesTexts = new ArrayList<>();
        List<WebElement> categories = selectDocumentLibraryItemRow(documentLibraryItem).findElements(categoriesDetails);
        for (WebElement category : categories) {
            categoriesTexts.add(category.getText());
        }
        return categoriesTexts;
    }

    public String getItemTitle(String itemName) {
        return selectDocumentLibraryItemRow(itemName).findElement(titleSelector).getText();
    }

    public String getItemDescription(String itemName) {
        return selectDocumentLibraryItemRow(itemName).findElement(descriptionSelector).getText();
    }

    public String getTags(String contentName) {
        waitInSeconds(8);
        waitUntilWebElementIsDisplayedWithRetry(findElement(contentTagsSelector), 5);
        List<WebElement> tagsList = selectDocumentLibraryItemRow(contentName).findElements(contentTagsSelector);
        List<String> tagsTextList = new ArrayList<>();
        for (WebElement aTagsList : tagsList) {
            tagsTextList.add(aTagsList.getText());
        }
        return tagsTextList.toString();
    }

    public List<String> getAllTagNames() {
        List<String> allTagsNames = new ArrayList<>();
        int counter = 0;
        int retryCount = 6;
        while (counter < retryCount) {
            try {
                waitInSeconds(30);
                refresh();
                By tagRows = By.cssSelector("ul.filterLink li span.tag a");
                waitUntilElementsAreVisible(tagRows);
                for (WebElement tagsElem : findElements(tagRows)) {
                    allTagsNames.add(tagsElem.getText());
                }
                break;
            } catch (TimeoutException | NoSuchElementException e) {
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
    public void editTag(String tagName, String newTagName) {
        List<WebElement> tagsList = waitUntilElementsAreVisible(inlineEditTagsSelector);

        WebElement tagElement = findFirstElementWithExactValue(tagsList, tagName);
        Parameter.checkIsMandotary("Tag", tagElement);
        tagElement.click();
        waitUntilElementIsVisible(findElement(tagToBeEdited));
        findElement(tagToBeEdited).clear();
        waitInSeconds(1);
        findElement(tagToBeEdited).sendKeys(newTagName);
        findElement(tagToBeEdited).sendKeys(Keys.ENTER);
    }

    public boolean isNoTagsTextDisplayed(String contentName) {
        int counter = 0;
        while (counter < 2) {
            try {
                WebElement noTags = selectDocumentLibraryItemRow(contentName).findElement(noTagsSelector);
                return isElementDisplayed(noTags);
            } catch (TimeoutException | NoSuchElementException e) {
                log.error("Action not found:" + e);
            }
            counter++;
            refresh();
        }
        return false;
    }

    public void mouseOverTags(String contentName) {
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
    public void mouseOverNoTags(String contentName) {
        waitInSeconds(2);
        WebElement contentElement = selectDocumentLibraryItemRow(contentName);
        Parameter.checkIsMandotary("Content selector", contentElement);
        waitInSeconds(4);
        WebElement tagElement = contentElement.findElement(noTagsSelector);
        Parameter.checkIsMandotary("Tag selector", tagElement);
        mouseOver(tagElement);
        waitUntilElementIsVisible(contentElement.findElement(editTagSelector));
    }

    public void mouseOverNoTagsWithNoEditIcon(String contentName) {
        waitInSeconds(2);
        WebElement contentElement = selectDocumentLibraryItemRow(contentName);
        Parameter.checkIsMandotary("Content selector", contentElement);
        WebElement tagElement = contentElement.findElement(noTagsSelector);
        Parameter.checkIsMandotary("Tag selector", tagElement);
        mouseOver(tagElement);
    }

    public boolean isEditTagInputFieldDisplayed() {
        waitUntilElementIsVisible(findElement(editTagInputField_));
        return isElementDisplayed(findElement(editTagInputField_));
    }

    public DocumentLibraryPage assertEditTagInputFieldDisplayed(String fileName) {
        log.info("Assert EditTag Input Field is Displayed {}", fileName);
        assertTrue(isEditTagInputFieldDisplayed(), fileName + " -> Edit tag text input field is displayed.");
        return this;
    }

    public DocumentLibraryPage assertTagNamesDisplayed(String fileName, String actTanames, String tagName) {
        log.info("Assert Tag Names are Displayed {}", fileName);
        assertEquals(getTags(fileName), actTanames, tagName + " -> tags=");
        return this;
    }

    public void typeTagName(String tagName) {
        waitInSeconds(1);
        Utils.clearAndType(waitUntilElementIsVisible(findElement(editTagInputField_)), tagName);
        findElement(editTagInputField_).sendKeys(Keys.RETURN);
    }

    public DocumentLibraryPage clickEditTagLink(String linkName) {
        waitInSeconds(1);
        WebElement link = findFirstElementWithValue(findElements(editTagButtons), linkName);
        clickElement(link);
        waitInSeconds(2);
        return this;
    }

    public void clickOnTag(String tagName) {
        waitUntilElementIsVisible(By.xpath(
            "//ul[contains(@class,'filterLink')]/li/span[contains(@class,'tag')]/a[contains(text(),'"
                + tagName.toLowerCase() + "')]")).click();
        waitInSeconds(4);
    }

    /**
     * Check "Edit Tag" icon presence for a content item
     *
     * @param contentName
     * @return true if icon is displayed, false otherwise
     */
    public boolean isEditTagIconDisplayed(String contentName) {
        return isElementDisplayed(selectDocumentLibraryItemRow(contentName), editTagSelector);
    }

    /**
     * Click on 'Edit Tag' icon for a content item
     *
     * @param contentName
     */
    public void clickEditTagIcon(String contentName) {
        waitInSeconds(3);
        WebElement editTagIcon = selectDocumentLibraryItemRow(contentName).findElement(editTagSelector);
        clickElement(editTagIcon);
    }

    /**
     * Switch to new window and get page content
     */
    public String switchToNewWindowAngGetContent() {
        String content = null;
        waitInSeconds(3);
        if (!getWindowHandles().isEmpty()) {
            //   switchWindow(1);
            Alert alert = webDriver.get().switchTo().alert();
            alert.accept();
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
    public void removeTag(String tagName) {
        List<WebElement> tagsList = waitUntilElementsAreVisible(inlineEditTagsSelector);
        List<WebElement> removeIconList = waitUntilElementsAreVisible(removeTagIconSelector);

        for (int i = 0; i < tagsList.size(); i++) {
            if (tagsList.get(i).getText().equalsIgnoreCase(tagName)) {
                removeIconList.get(i).click();
            }
        }
    }

    /**
     * Method to check if the file is opened in Google Maps
     */
    public boolean isFileOpenedInGoogleMaps() {
        return findElement(googleMap_).isDisplayed();
    }

    /**
     * Method to check that the document thumbnail is displayed on Google Maps
     *
     * @return
     */
    public boolean isDocumentThumbnailDisplayedOnGoogleMaps() {
        if (findElement(By.cssSelector("button.dismissButton")).isDisplayed()) {
            findElement(By.cssSelector("button.dismissButton")).click();
        }
        return findElement(googleMapPopUp_).isDisplayed();
    }

    /**
     * Method to click on the document thumbnail to open the document in preview
     */
    public DocumentDetailsPage clickOnFileInGoogleMaps() {
        findElement(googleMapPopUp_).click();
        return new DocumentDetailsPage(webDriver);
    }

    public void clickOnDeleteButtonOnDeletePrompt() {
        waitUntilElementIsVisible(findElement(deleteFileFolderPrompt));
        findElement(deleteButtonPrompt).click();
    }

    /**
     * Method to check that the geolocation metadata icon is displayed next to the document
     */
    public boolean isGeolocationMetadataIconDisplayed() {
        return findElement(geolocationMetadataIcon_).isDisplayed();
    }

    /**
     * Method to get the info banner text
     */

    public String getInfoBannerText(String fileName) {
        WebElement fileElement = selectDocumentLibraryItemRow(fileName);
        Parameter.checkIsMandotary("Document library file", fileElement);
        waitInSeconds(3);
        Parameter.checkIsMandotary("File banner", findElement(infoBanner));
        return findElement(infoBanner).getText();
    }

    /**
     * Method to check if the info banner is displayed
     */

    public boolean isInfoBannerDisplayed(String fileName) {
        waitInSeconds(5);
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), infoBanner);
    }

    /**
     * Method to get the locked by user name
     */
    public String getLockedByUserName(String fileName) {
        return waitUntilChildElementIsPresent(selectDocumentLibraryItemRow(fileName), lockedByUser).getText();
    }

    public boolean isLikeButtonDisplayed(String fileName) {
        waitInSeconds(3);
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), likeButton);
    }

    public boolean isCommentButtonDisplayed(String fileName) {
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), commentButton);
    }

    public boolean isShareButtonDisplayed(String fileName) {
        return isElementDisplayed(selectDocumentLibraryItemRow(fileName), By.cssSelector("a[class='quickshare-action']"));
    }

    public boolean isCreateButtonDisplayed() {
        return isElementDisplayed(createButton);
    }

    public String getCreateButtonStatusDisabled() {
        return findElement(createButton).getAttribute("disabled");
    }

    public DocumentLibraryPage assertCreateButtonStatusDisabled() {
        log.info("Verify Create button is disabled");
        Assert.assertEquals(getCreateButtonStatusDisabled(), "true", "The Create Button is not disabled");
        return this;
    }

    public DocumentLibraryPage assertCreateButtonStatusEnabled() {
        log.info("Verify Create button is disabled");
        Assert.assertEquals(getCreateButtonStatusDisabled(), null, "The Create Button is disabled");
        return this;
    }

    public String getUploadButtonStatusDisabled() {
        return findElement(uploadButton).getAttribute("disabled");
    }

    public DocumentLibraryPage assertUploadButtonStatusDisabled() {
        log.info("Verify Upload button is disabled");
        Assert.assertEquals(getUploadButtonStatusDisabled(), "true", "The Upload Button is not disabled");
        return this;
    }

    public DocumentLibraryPage assertUploadButtonStatusEnabled() {
        log.info("Verify Upload button is disabled");
        Assert.assertEquals(getUploadButtonStatusDisabled(), null, "The Upload Button is disabled");
        return this;
    }

    public void clickLinkToFolder(String folderName) {
        List<WebElement> linkToFolderList = waitUntilElementsAreVisible(linkToFolderLocator);
        findFirstElementWithExactValue(linkToFolderList, folderName).click();
    }

    public boolean isCreateContentMenuDisplayed() {
        return isElementDisplayed(createContentMenu);
    }

    public DocumentLibraryPage assertCreateContentMenuIsNotDisplayed() {
        log.info("Verify Create Content Menu Displayed disabled");
        Assert.assertFalse(isCreateContentMenuDisplayed(), "Create Content menu is displayed when the Create button is clicked");
        return this;
    }

    public DocumentLibraryPage assertCreateContentMenuIsDisplayed() {
        log.info("Verify Create Content Menu Displayed disabled");
        assertTrue(isCreateContentMenuDisplayed(), "Create Content menu is not displayed when the Create button is clicked");
        return this;
    }

    public void clickCreateButtonWithoutWait() {
        waitUntilElementIsVisible(createButton);
        findElement(createButton).click();
    }

    public boolean isFileDisplayed(String fileName) {
        try {
            waitUntilElementIsDisplayedWithRetry(By.cssSelector("td[class$='yui-dt-col-fileName']"),
                5);
            WebElement webElement = selectDocumentLibraryItemRow(fileName);

            return isElementDisplayed(webElement);
        } catch (NoSuchElementException ex) {
            log.info("Element not found " + ex.getMessage());
            return false;
        }
    }

    public DocumentLibraryPage assertDocumentLibraryPageTitleEquals(String expectedTittle) {
        log.info("Verify Document Library Page Title");
        assertEquals(getPageTitle(), expectedTittle, String.format("Page Title not equal %s ", expectedTittle));
        return this;
    }

    public DocumentLibraryPage assertFileIsNotDisplayed(String fileName) {
        log.info("Assert file is not displayed in Document Library {}", fileName);
        waitInSeconds(3);
        assertFalse(isFileNameDisplayed(fileName), fileName + " is displayed in Document Library");
        return this;
    }

    public DocumentLibraryPage assertFileIsDisplayed(String fileName) {
        log.info("Assert file is displayed in Document Library {}", fileName);
        waitInSeconds(3);
        assertTrue(isFileNameDisplayed(fileName), fileName + " is not displayed");
        return this;
    }

    public DocumentLibraryPage assertLockedBannerIsDisplayed(String fileName) {
        log.info("Assert Locked banner displayed on Document {}", fileName);
        assertTrue(isInfoBannerDisplayed(fileName),
            "Document is Locked Info banner on File" + fileName + " is not displayed");
        return this;
    }

    public DocumentLibraryPage assertLikeButtonIsDisplayed(String fileName) {
        log.info("Assert Like button is displayed for the file/Folder {} ", fileName);
        assertTrue(isLikeButtonDisplayed(fileName),
            "file/Folders link is not present for " + fileName);
        return this;
    }

    public DocumentLibraryPage assertLikeButtonNotDisplayed(String fileName) {
        log.info("Assert Like button is not displayed for the file/Folder {} ", fileName);
        assertFalse(isLikeButtonDisplayed(fileName),
            "file/Folders link is present for " + fileName);
        return this;
    }

    public DocumentLibraryPage assertCommentButtonNotDisplayed(String fileName) {
        log.info("Assert Comment button is not displayed for the file/Folder {} ", fileName);
        assertFalse(isCommentButtonDisplayed(fileName),
            "file/Folders comment button is present for " + fileName);
        return this;
    }

    public DocumentLibraryPage assertShareButtonNotDisplayed(String fileName) {
        log.info("Assert share button is not displayed for the file/Folder {} ", fileName);
        assertFalse(isShareButtonDisplayed(fileName),
            "file/Folders share button is present for " + fileName);
        return this;
    }

    public DocumentLibraryPage assertIsGioLocationMetadataIconDisplayed() {
        log.info("Verify that the Gio location Metadata Icon is Displayed");
        assertTrue(isGeolocationMetadataIconDisplayed(),
            "Geolocation Metadata icon is not displayed");
        return this;
    }

    public DocumentLibraryPage assertAreActionsAvailableForLibraryItemsInPreviewPage(String contantName) {
        log.info(
            "Verify that the actions 'Download', 'View In Browser', 'Edit in Google Docs™' & 'View on Google Maps' are available for the content {}", contantName);
        List<String> expectedActions = Arrays
            .asList("Download", "View In Browser", "Edit in Google Docs™", "View on Google Maps");
        Assert.assertTrue(areActionsAvailableForLibraryItem(contantName, expectedActions),
            "Expected actions");
        return this;
    }

    public DocumentLibraryPage assertAreActionsAvailableForLibraryItems(String contantName) {
        log.info(
            "Verify that the actions 'Download', 'View In Browser', 'Edit Properties™' are available for the content {}", contantName);
        List<String> expectedActions = Arrays
            .asList("Download", "View In Browser", "Edit Properties");
        Assert.assertTrue(areActionsAvailableForLibraryItem(contantName, expectedActions),
            "Expected actions");
        return this;
    }

    public DocumentLibraryPage assertIsFileOpenInGoogleMap() {
        log.info("Verify that the content open in the google map");
        assertTrue(isFileOpenedInGoogleMaps(), "File is not opened in Google Maps");
        return this;
    }

    public DocumentLibraryPage assertisDocumentThumbnailDisplayedOnGoogleMaps() {
        log.info("Verify that the content Thumbnail image displayed on the google map");
        assertTrue(isDocumentThumbnailDisplayedOnGoogleMaps(),
            "Document thumbnail is not displayed in Google Maps");
        return this;
    }

    public DocumentLibraryPage assertisMoreMenuDisplayed(String filename) {
        log.info("More Menu is Displaying");
        assertTrue(isMoreMenuDisplayed(filename), "More Menu is not displayed");
        return this;
    }

    public DocumentLibraryPage assertisMoreMenuNotDisplayed(String filename) {
        log.info("More Menu is Displaying");
        assertFalse(isMoreMenuDisplayed(filename), "More Menu is displayed");
        return this;
    }

    public DocumentLibraryPage assertActionsNoteAvailableForLibrary(String contantName, List<String> actions) {
        log.info(
            "Verify that the given actions are not available in more  ");
        Assert.assertFalse(areActionsAvailableForLibraryItem(contantName, actions),
            "Not Expected actions");
        return this;
    }

    public boolean isActionItemAvailableInTheDocumentLibraryItems(String content, ItemActions actionItem) {

        boolean actionAvailable;
        if (isMoreMenuDisplayed(content)) {
            actionAvailable = isActionAvailableForLibraryItem(content, actionItem);
        } else {
            actionAvailable = false;
        }
        return actionAvailable;
    }

    public DocumentLibraryPage assertActionItem_Not_AvailableInTheDocumentLibraryItems(String content, ItemActions actionItem) {
        log.info("Verify that the Action Item is Available in the list for the Document Library Itme {}", content);
        assertFalse(isActionItemAvailableInTheDocumentLibraryItems(content, actionItem), "Action is available");
        return this;
    }

    public DocumentLibraryPage assertVerifyFileContentInNewBrowserWindow(String description) {
        log.info("File content is correct in new window");
        assertEquals(switchToNewWindowAngGetContent(), description,
            "File content is not correct or file has not be opened in new window");
        return this;
    }

    public DocumentLibraryPage assertVerifyDocumentListDisplayed() {
        log.info("Document list is Displaying");
        assertTrue(isElementDisplayed(documentList_), "There is no file added in the file list");
        return this;
    }

    public DocumentLibraryPage assertVerifyOptionsMenuDisplayed() {
        log.info("Option Menu is Displaying");
        assertTrue(isElementDisplayed(optionMenu_), "Option menu is not Displaying");
        return this;
    }

    public DocumentLibraryPage assertVerifyHideFoldersMenuOptionDisplayed() {
        log.info("Hide Folder Menu is Displaying");
        clickElement(optionMenu_);
        assertTrue(isElementDisplayed(hideFoldersMenuOption_), "Hide Folder is not Displaying");
        clickElement(optionMenu_);
        return this;
    }

    public DocumentLibraryPage assertVerifyDocumentRelativePath(String site) {
        log.info("Verify the document relative path {}");
        String expectedRelativePath = "share/page/site/" + site + "/documentlibrary";
        assertEquals(getRelativePath(), expectedRelativePath, "User is not redirected to Document Library");
        return this;
    }

    public DocumentLibraryPage assertIsContantNameDisplayed(String contantName) {
        log.info("Verify file/folder name displayed {}", contantName);
        assertTrue(isContentNameDisplayed(contantName), String.format("file/folder name not matched with %s ", contantName));
        return this;
    }

    public DocumentLibraryPage assertItemTitleEquals(String itemName, String itemTitle) {
        log.info("Verify that the item title is displayed {}", itemTitle);
        assertEquals(getItemTitle(itemName), "(" + itemTitle + ")", String.format("Item Title is not matched with %s ", itemTitle));
        return this;
    }

    public DocumentLibraryPage assertItemDescriptionEquals(String itemName, String itemDescription) {
        log.info("Verify that the item description is displayed {}", itemDescription);
        assertEquals(getItemDescription(itemName), itemDescription, String.format("Item description is not matched with %s ", itemDescription));
        return this;
    }

    public DocumentLibraryPage assertItemTagEquals(String itemName, String itemTag) {
        log.info("Verify that the added tag displayed for item {}", itemTag);
        assertEquals(getTags(itemName), "[" + itemTag + "]", String.format("The tag of the item is not matched with %s ", itemTag));
        return this;
    }

    public DocumentLibraryPage assertBreadCrumbEquals(String... breadCrumb) {
        log.info("Verify the breadcrumb of the folder");
        ArrayList<String> breadcrumb = new ArrayList<>(Arrays.asList(breadCrumb));
        assertEquals(getBreadcrumbList(), breadcrumb.toString(), "BreadCrumb of folder not mathced");
        return this;
    }

    public DocumentLibraryPage assertIsEditTagIconDisplayed(String contentName) {
        log.info("Verify that the Edit Tag Icon is displayed");
        assertTrue(isEditTagIconDisplayed(contentName), "Edit Tag icon is not displayed");
        return this;
    }

    public DocumentLibraryPage assertIsEditTagIconNotDisplayed(String contentName) {
        log.info("Verify that the Edit Tag Icon is displayed");
        assertFalse(isEditTagIconDisplayed(contentName), "Edit Tag icon is not displayed");
        return this;
    }

    public DocumentLibraryPage assertIsEditTagInputFieldDisplayed() {
        log.info("Verify that the Edit Tag Input Field is displayed");
        assertTrue(isEditTagInputFieldDisplayed(), "Edit Tag Input Field is not displayed");
        return this;
    }

    public DocumentLibraryPage assertCheckAddedTagsList(String tagName, String contentName) {
        log.info("Verify the added tag/tags for the content.");
        ArrayList<String> tagsList = new ArrayList<>(Collections.singletonList(tagName.toLowerCase()));
        assertEquals(getTags(contentName), tagsList.toString(), contentName + " -> tag/tags=");
        return this;
    }

    public DocumentLibraryPage assertIsNoTagsTextDisplayed(String contentName) {
        log.info("Verify that no tags is displayed.");
        assertTrue(isNoTagsTextDisplayed(contentName), "Tag is still displayed.");
        return this;
    }

    private List<WebElement> getAvailableConsumerActions(String libraryItem) {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        return itemRow.findElements(actionsSet);
    }

    public boolean isActionAvailableForConsumerLibraryItem(String libraryItem, ItemActions action) {
        waitInSeconds(4);
        return isElementDisplayed(
            findFirstElementWithValue(getAvailableConsumerActions(libraryItem), action.getActionName()));
    }

    public void selectConsumerItemAction(String contentItem, ItemActions action) {
        waitInSeconds(3);
        WebElement libraryItem = mouseOverContentItem(contentItem);
        By actionSelector = By.cssSelector(MessageFormat.format(ACTION_SELECTOR, action.getActionLocator()));
        WebElement actionElement;

        try {
            actionElement = waitUntilElementIsVisible(actionSelector);
        } catch (TimeoutException timeoutException) {
            throw new TimeoutException(
                "The action " + action.getActionName() + " could not be found for list item " + contentItem);
        }
        scrollToElement(actionElement);
        clickElement(actionElement);
    }

    public void browserRefresh() {
        waitInSeconds(20);
        refresh();
    }

    public DocumentLibraryPage clickDocumentsConsumerFilterOption(String filter) {
        findFirstElementWithValue(documentsFilterOptions, filter).click();
        waitInSeconds(10);
        return this;
    }

    public String getBreadcrumb() {
        waitInSeconds(1);
        ArrayList<String> breadcrumbTextList = new ArrayList<>();
        breadcrumbTextList.add(findElement(breadcrumbList1).getText());
        breadcrumbTextList.add(findElement(breadcumbCurrentFolder).getText());
        return breadcrumbTextList.toString();
    }

    public DocumentLibraryPage clickAction(String option) {
        List<WebElement> clickOption = findElements(editTagButtons);
        waitInSeconds(3);
        for (WebElement clickButton : clickOption) {
            if (clickButton.getText().equals(option))
                clickButton.click();
        }
        return this;
    }

    private WebElement getContentTableRow(String contentName) {
        By contentRowElement = By.xpath(String.format(contentTableRow, contentName));

        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(contentRowElement)) {
            log.warn("Content {} not displayed - retry: {}", contentName, retryCount);
            refresh();
            waitToLoopTime(WAIT_2.getValue());
            retryCount++;
        }
        return waitUntilElementIsVisible(contentRowElement);
    }

    public DocumentLibraryPage mouseOverContentRow(String contentName) {
        try {
            By contentRowElement = By.xpath(String.format(contentNameInTableRow, contentName));
            mouseOver(selectDocumentLibraryItemRow(contentName).findElement(contentRowElement));
            waitUntilElementIsVisible(renameIconInList);
        } catch (TimeoutException e) {
        }
        return this;
    }

    public DocumentLibraryPage clickNameRenameIcon(String contentName) {
        mouseOverContentRow(contentName);
        WebElement renameIconElement = selectDocumentLibraryItemRow(contentName).findElement(renameIconInList);
        renameIconElement.click();
        waitUntilElementIsVisible(contentNameInputField);
        return this;
    }

    public DocumentLibraryPage assertContentIsDisplayed(ContentModel contentModel) {
        log.info("Assert content {} is displayed", contentModel.getName());
        assertTrue(isElementDisplayed(getContentTableRow(contentModel.getName())),
            String.format("Content %s is not displayed", contentModel.getName()));
        return this;
    }

    public DocumentLibraryPage clickFolderNameOnTableView(String folderName) {
        waitInSeconds(3);
        By contentRowElement = By.xpath(String.format(contentNameInTableRow, folderName));
        WebElement folderElement = selectDocumentLibraryItemRow(folderName);
        clickElement(folderElement.findElement(contentRowElement));
        return this;
    }

    public DocumentLibraryPage mouseOverLibraryViewRow(String contentName) {
        try {
            By contentRowElement = By.xpath(String.format(contentNameInGalleryView, contentName));
            mouseOver(contentRowElement);
        } catch (TimeoutException e) {
        }
        return this;
    }

    public DocumentLibraryPage assertActionsAvailableForLibraryItems(String contantName, List<String> actions) {
        log.info("Verify that the given actions are available in more  ");
        Assert.assertTrue(areActionsAvailableForLibraryItem(contantName, actions),
            "Not Expected actions");
        return this;
    }

    public DocumentLibraryPage assertActionsAvailableForLibrary(String contantName, List<String> actions) {
        log.info("Verify that the given actions are available in more  ");
        waitInSeconds(3);
        Assert.assertTrue(areActionsAvailableInLibrary(contantName, actions),
            "Not Expected actions");
        return this;
    }

    public DocumentLibraryPage assertActionsAvailableForLibraryInGalleryView(String contantName, List<String> actions) {
        log.info("Verify that the given actions are available in more  ");
        waitInSeconds(4);
        mouseOverLibraryViewRow(contantName);
        waitInSeconds(4);
        clickElement(viewDetailAction);
        clickMoreAction("More...");
        Assert.assertTrue(areActionsAvailableInLibrary(contantName, actions), "Not Expected actions");
        return this;
    }

    public DocumentLibraryPage clickMoreAction(String option) {
        List<WebElement> clickOption = findElements(moreActionButton);
        waitInSeconds(3);
        for (WebElement clickButton : clickOption) {
            if (clickButton.getText().equals(option))
                clickButton.click();
        }
        return this;
    }

    public List<String> getFilterTypeList() {
        List<String> listItems = new ArrayList<>();
        waitInSeconds(2);
        for (WebElement listItemNames : findElements(listItemData)) {
            listItems.add(listItemNames.getText());
        }
        return listItems;
    }

    private List<WebElement> getActions() {
        waitInSeconds(2);
        return findElements(actionsSet);
    }

    public DocumentDetailsPage clickOnFileInGalleryView(String contentName) {
        waitInSeconds(2);
        By contentRowElement = By.xpath(String.format(contentNameInGalleryView, contentName));
        WebElement fileElement = selectDocumentLibraryItemRowFromGalleryView(contentName);
        Parameter.checkIsMandotary("File", fileElement);
        clickElement(fileElement.findElement(contentRowElement));
        return new DocumentDetailsPage(webDriver);
    }

    public WebElement selectDocumentLibraryItemRowFromGalleryView(String documentItem) {
        waitForRows();
        List<WebElement> itemsList = findElements(galleryViewDocumentList);
        return findFirstElementWithValue(itemsList, documentItem);
    }

    public boolean isFileNameDisplayedForGalleryView(String fileName) {
        if (selectDocumentLibraryItemRowFromGalleryView(fileName) != null) {
            return true;
        } else {
            refresh();
            waitInSeconds(WAIT_5.getValue());
            if (selectDocumentLibraryItemRowFromGalleryView(fileName) != null) {
                return true;
            } else {
                return false;
            }
        }
    }

    public enum CreateMenuOption {
        FOLDER(By.cssSelector("span.folder-file")), PLAIN_TEXT(
            By.cssSelector("span.text-file")), HTML(By.cssSelector("span.html-file")), XML(
            By.cssSelector("span.xml-file")), GOOGLE_DOCS_DOCUMENT(
            By.cssSelector("span.document-file")), GOOGLE_DOCS_SPREADSHEET(
            By.cssSelector("span.spreadsheet-file")), GOOGLE_DOCS_PRESENTATION(
            By.cssSelector("span.presentation-file")), CREATE_DOC_FROM_TEMPLATE(By.xpath(
            "//a[contains(@class, 'yuimenuitemlabel-hassubmenu')]//span[text()='Create document from template']")), CREATE_FOLDER_FROM_TEMPLATE(
            By.xpath(
                "//a[contains(@class, 'yuimenuitemlabel-hassubmenu')]//span[text()='Create folder from template']"));

        private By locator;

        CreateMenuOption(By locator) {
            this.locator = locator;
        }

        public By getLocator() {
            return locator;
        }
    }

    public enum DocumentsFilters {
        All("All Documents", "All Documents in the Document Library"), EditingMe("I'm Editing",
            "Documents I'm Editing(working copies)"), EditingOthers("Others are Editing",
            "Documents Others are Editing(working copies)"), RecentlyModified("Recently Modified",
            "Documents Recently Modified"), RecentlyAdded("Recently Added",
            "Documents Added Recently"), Favorites("My Favorites", "My Favorite Documents and Folders");

        public final String title;
        public final String header;

        DocumentsFilters(String title, String header) {
            this.title = title;
            this.header = header;
        }
    }

    public DocumentLibraryPage clickOnTagToggle() {
        clickElement(findElement(tagToggleIcon));
        return this;
    }

    public boolean assertTagDisplayed()
    {
        waitInSeconds(2);
        return isElementDisplayed(tag);
    }

    public void clickOnEditTagIcon() {
        waitInSeconds(3);
        clickElement(tagValue);
    }

    public void updateTag() {
        waitInSeconds(3);
        clickElement(tagSelectButton);
        clickElement(removeTagIcon);
        findElement(createNewTag).sendKeys("update tag");
        clickElement(tagCorrectIcon);
        waitInSeconds(3);
        clickElement(tagOkButton);
    }

    public String getTagValue() {
        waitInSeconds(3);
        return findElement(tag).getText();
    }

    public void clickOnSaveTag() {
        clickElement(clickSaveButton);
    }

    public void ClickOnCancelTag() {
        clickElement(clickCancelButton);
    }

    public int getCommentsNo()
    {
        return Integer.parseInt(getElementText(commentsCount));
    }

    public DocumentLibraryPage assertVerifyNoOfComments(int noOfComments)
    {
        log.info("Verify No of likes on the content {}", noOfComments);
        assertEquals(getCommentsNo(), noOfComments, "No of likes not matched with %s " +noOfComments);
        return this;
    }

    public DocumentLibraryPage assertVerifyNoCommentNumbers()
    {
        log.info("Verify No of likes on the content");
        assertFalse(isElementDisplayed(commentsCount), "Check Comment numbers ");
        return this;
    }

    public void addComment() {
        String multipleComment = "comment" + System.currentTimeMillis();
        clickElement(addCommentButton);
        switchTo().frame(findElement(commentContentIframe));
        WebElement commentTextArea = switchTo().activeElement();
        waitUntilElementIsVisible(commentTextArea);
        clickElement(commentTextArea);
        commentTextArea.sendKeys(multipleComment);
        switchToDefaultContent();
        clickElement(saveComment);
        waitInSeconds(2);
    }

    public void addMultileComment() {
        for (int i = 0; i < 12; i++)
        {
            addComment();
        }
    }

    public void ClickOnNextCommentPage() {
        clickElement(nextCommentPage);
    }

    public void ClickOnPerviousCommentPage() {
        clickElement(perviousCommentPage);
    }

    public String getCommentCount() {
        waitInSeconds(4);
        scrollToElement(findElement(commentCount));
        return findElement(commentCount).getText();
    }

    public void collapsedocumentAction() {
        waitInSeconds(3);
        clickElement(documentActionIcon);
    }

    public boolean isEditTagDisplayed()
    {
        return isElementDisplayed(tagValue);
    }

    public void clickOnHideFolder() {
        clickElement(optionMenu_);
        clickElement(hideFoldersMenuOption_);
    }
}

