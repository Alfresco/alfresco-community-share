package org.alfresco.po.share.alfrescoContent;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.DocumentsFilter;
import org.alfresco.utility.model.FolderModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class ContentFiltersComponent extends AlfrescoContentPage
{
    private final By categoryRootIcon = By.cssSelector("#ygtvtableel3 .ygtvspacer");
    private final By categoriesTable = By.id("ygtvtableel3");
    private final By categoriesChildren = By.id("ygtvc3");
    private final By documentsFilter = By.cssSelector("a.filter-link");
    private final By selectedFilter = By.cssSelector(".filterLink .selected");

    private final String categoriesCollapsed = "ygtv-collapsed";
    private final String categoriesExpanded = "ygtv-expanded";
    private final String categoryIcon = "//div[@class='category']//span[text()='%s']/../..//a";
    private final String categorySelector = "//div[@class='category']//span[text()='%s']/../..//span[@class='ygtvlabel']";
    private final String libraryFolderFilter = "//div[@class='treeview filter']//span[text()='%s']/../..//span[@class='ygtvlabel']";
    private final String folderInFilterElement = "//tr[starts-with(@class,'ygtvrow documentDroppable')]//span[text()='%s']";

    protected ContentFiltersComponent(ThreadLocal webDriver)
    {
        super(webDriver);
    }

    public ContentFiltersComponent assertCategoriesAreNotExpanded()
    {
        log.info("Assert Categories are not expanded");
        WebElement table = waitUntilElementIsVisible(categoriesTable);
        assertTrue(table.getAttribute("class").contains(categoriesCollapsed), "Categories are expanded");

        return this;
    }

    public ContentFiltersComponent assertCategoriesAreExpanded()
    {
        log.info("Assert Categories are not expanded");
        WebElement table = waitUntilElementIsVisible(categoriesTable);
        assertTrue(table.getAttribute("class").contains(categoriesExpanded), "Categories are expanded");

        return this;
    }

    public ContentFiltersComponent expandCategoryRoot()
    {
        log.info("Expand Category Root");
        clickElement(categoryRootIcon);
        waitUntilElementIsVisible(categoriesChildren);

        return this;
    }

    public ContentFiltersComponent expandCategory(String category)
    {
        log.info("Expand category {}", category);
        By categoryIconSelector = By.xpath(String.format(categoryIcon, category));
        waitUntilElementIsVisible(categoryIconSelector);
        clickElement(categoryIconSelector);
        waitUntilElementIsVisible(categoriesChildren);

        return this;
    }

    public ContentFiltersComponent collapseCategoryRoot()
    {
        log.info("Collapse Category Root");
        clickElement(categoryRootIcon);
        return this;
    }

    public ContentFiltersComponent selectCategory(String category)
    {
        log.info("Select category {}", category);
        By categorySelect = By.xpath(String.format(categorySelector, category));
        waitUntilElementIsVisible(categorySelect);
        clickElement(categorySelect);

        return this;
    }

    public ContentFiltersComponent selectFolderFromLibraryFilter(FolderModel folder)
    {
        log.info("Select folder {} from Library filter", folder.getName());
        By folderFilter = By.xpath(String.format(libraryFolderFilter, folder.getName()));
        waitUntilElementIsVisible(folderFilter);
        clickElement(folderFilter);

        return this;
    }

    public ContentFiltersComponent assertFolderIsDisplayedInFilter(FolderModel folder)
    {
        log.info("Assert folder {} is displayed in documents filter from left side", folder.getName());
        WebElement folderLink = waitUntilElementIsVisible(
            By.xpath(String.format(folderInFilterElement, folder.getName())));
        assertTrue(isElementDisplayed(folderLink),
            String.format("Folder %s is displayed in filter", folder.getName()));

        return this;
    }

    public ContentFiltersComponent clickFolderFromFilter(FolderModel folder)
    {
        log.info("Click folder {} from filter", folder.getName());
        clickElement(By.xpath(String.format(folderInFilterElement, folder.getName())));
        waitForCurrentFolderBreadcrumb(folder);

        return this;
    }

    public ContentFiltersComponent selectFromDocumentsFilter(DocumentsFilter documentFilter)
    {
        log.info("Select document filter {}", documentFilter.toString());
        List<WebElement> filters = waitUntilElementsAreVisible(documentsFilter);
        clickElement(findFirstElementWithValue(filters, getDocumentsFilterValue(documentFilter)));
        waitUntilElementIsVisible(selectedFilter);

        return this;
    }

    private String getDocumentsFilterValue(DocumentsFilter documentsFilter)
    {
        String filterValue = "";
        switch (documentsFilter)
        {
            case ALL_DOCUMENTS:
                filterValue = language.translate("documentLibrary.documentsFilter.all");
                break;
            case EDITING_ME:
                filterValue = language.translate("documentLibrary.documentsFilter.editingMe");
                break;
            case EDITING_OTHERS:
                filterValue = language.translate("documentLibrary.documentsFilter.othersEditing");
                break;
            case RECENTLY_MODIFIED:
                filterValue = language.translate("documentLibrary.documentsFilter.recentlyModified");
                break;
            case RECENTLY_ADDED:
                filterValue = language.translate("documentLibrary.documentsFilter.recentlyAdded");
                break;
            case FAVORITES:
                filterValue = language.translate("documentLibrary.documentsFilter.favorites");
                break;
            default:
                break;
        }
        return filterValue;
    }
}
