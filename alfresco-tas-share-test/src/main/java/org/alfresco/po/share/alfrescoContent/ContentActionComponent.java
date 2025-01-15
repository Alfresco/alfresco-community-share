package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_15;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_5;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FolderModel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class ContentActionComponent extends AlfrescoContentPage<ContentActionComponent> {
    private final By contentNameSelector = By.cssSelector(".filename a");
    private final By moreActionLink = By.cssSelector(".show-more");
    private final By moreActionsArea = By.cssSelector(".more-actions");
    private final By copyToAction = By.id("onActionCopyTo");
    private final By moveToAction = By.id("onActionMoveTo");
    private final By deleteAction = By.id("onActionDelete");
    private final By addToFavoritesLink = By.className("favourite-action");
    private final By removeFavoriteLink = By.cssSelector("a[class='favourite-action enabled']");
    private final By locateAction = By.id("onActionLocate");
    private final By renameIcon = By.cssSelector(".filename span.insitu-edit[style*='visibility: visible']");
    private final By renameForm = By.cssSelector("form[class='insitu-edit']");
    private final By renameInput = By.cssSelector("form[class='insitu-edit']>input");
    private final By renameSaveButton = By.cssSelector("form[class='insitu-edit']>a:nth-of-type(1)");
    private final By renameCancelButton = By.cssSelector("form[class='insitu-edit']>a:nth-of-type(2)");
    private final By linkThumbnail = By.cssSelector(".thumbnail .link");
    private final By editProperties = By.id("onActionDetails");
    private final By addedTagsLocator = By.cssSelector(".detail .tag-link");
    private final By tagAreaLocator = By.cssSelector("td[headers$='th-fileName '] div:nth-of-type(3) .item");
    private final By tagEditIconLocator = By.cssSelector("span.insitu-edit[style*='visibility: visible']");
    private final By tagInputLocator = By.cssSelector(".inlineTagEdit input");
    private final By highlightLocator = By.cssSelector("tr[class$='yui-dt-highlighted']");
    private final By addedCategoriesLink = By.cssSelector(".detail .filter-change");
    private final By noCategoriesLocator = By.xpath("//span[@class='category-item item']/..//span[@class='faded']");
    private final By actionsSet = By.cssSelector(".action-set>div:not([id='onActionShowMore']) a span");

    private final String highlightContent = "yui-dt-highlighted";
    private final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";
    private final String existingTag = "//span[@class='inlineTagEditTag']//span[text()='%s']";
    private final String removeTagIcon = "/..//img[1]";

    private final ContentModel contentModel;

    public ContentActionComponent(ThreadLocal<WebDriver> webDriver, ContentModel contentModel) {
        super(webDriver);
        this.contentModel = contentModel;
    }

    private WebElement getContentRow() {
        return getContentRow(contentModel.getName());
    }

    public ContentActionComponent assertContentIsDisplayed() {
        log.info("Assert content is displayed");
        assertTrue(isElementDisplayed(getContentRow()),
            String.format("Content '%s' is not displayed", contentModel.getName()));

        return this;
    }

    public ContentActionComponent assertContentIsNotDisplayed() {
        log.info("Assert content is not displayed");
        By content = By.xpath(String.format(contentRow, contentModel.getName()));
        try {
            waitUntilElementDisappears(content, WAIT_5.getValue());
        } catch (TimeoutException e) {
            log.error("Content {} is still displayed in Alfresco Content page", contentModel.getName());
        }

        assertFalse(isElementDisplayed(content), String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    @SuppressWarnings("rawtypes")
    public AlfrescoContentPage selectFolder() {
        log.info("Select Folder");
        WebElement contentRowElement = getContentRow();
        WebElement content = waitUntilChildElementIsPresent(contentRowElement, contentNameSelector);
        mouseOver(content);
        clickElement(content);
        waitForCurrentFolderBreadcrumb((FolderModel) contentModel);

        return new AlfrescoContentPage(webDriver);
    }

    public DocumentDetailsPage selectFile() {
        log.info("Select file");
        clickElement(getContentRow().findElement(contentNameSelector));
        return new DocumentDetailsPage(webDriver);
    }

    public ContentActionComponent assertContentIsHighlighted() {
        By contentRowElement = By.xpath(String.format(contentRow, contentModel.getName()));
        WebElement content = waitUntilElementIsVisible(contentRowElement);
        assertTrue(content.getAttribute("class").contains(highlightContent), "Content is not highlighted");
        return this;
    }

    private ContentActionComponent mouseOverContent() {
        log.info("Mouse over content");
        mouseOver(getContentRow().findElement(contentNameSelector));
        waitUntilElementHasAttribute(getContentRow(), "class", highlightContent);

        return this;
    }

    private ContentActionComponent clickMore() {
        log.info("Click More");
        WebElement moreAction = waitUntilChildElementIsPresent(getContentRow(), moreActionLink);
        mouseOver(moreAction);
        clickElement(moreAction);
        waitUntilChildElementIsPresent(getContentRow(), moreActionsArea);

        return this;
    }

    public CopyMoveUnzipToDialog clickCopyTo() {
        log.info("Click Copy To...");
        mouseOverContent();
        clickMore();
        clickElement(getContentRow().findElement(copyToAction));

        return new CopyMoveUnzipToDialog(webDriver);
    }

    public CopyMoveUnzipToDialog clickMoveTo() {
        log.info("Click Move To...");
        mouseOverContent();
        clickMore();
        clickElement(getContentRow().findElement(moveToAction));

        return new CopyMoveUnzipToDialog(webDriver);
    }

    public DeleteDialog clickDelete() {
        log.info("Click Delete");
        mouseOverContent();
        clickMore();
        clickElement(getContentRow().findElement(deleteAction));
        return new DeleteDialog(webDriver);
    }

    public ContentActionComponent assertAddFileToFavoritesTooltipEqualsWithExpected() {
        String tooltipValue = language.translate("documentLibrary.contentAction.addDocumentToFavorites");
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertAddFolderToFavoritesTooltipEqualsWithExpected() {
        String tooltipValue = language.translate("documentLibrary.contentAction.addFolderToFavorites");
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertRemoveFileFromFavoritesTooltipEqualsWithExpected() {
        String tooltipValue = language.translate("documentLibrary.contentAction.removeDocumentFromFavorites");
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertRemoveFolderFromFavoritesTooltipEqualsWithExpected() {
        String tooltipValue = language.translate("documentLibrary.contentAction.removeFolderFromFavorites");
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertAddToFavoritesIsDisplayed() {
        log.info("Assert Add to favorites is displayed");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        assertTrue(isElementDisplayed(contentRowElement.findElement(addToFavoritesLink)),
            "Add to favorites link is not displayed");

        return this;
    }

    public ContentActionComponent assertRemoveFromFavoritesIsDisplayed() {
        log.info("Assert remove from favorites is displayed");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        assertTrue(isElementDisplayed(contentRowElement.findElement(removeFavoriteLink)),
            "Remove from favorites link is not displayed");

        return this;
    }

    public ContentActionComponent addToFavorites() {
        log.info("Add to favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        clickElement(contentRow.findElement(addToFavoritesLink));
        waitUntilChildElementIsPresent(contentRow, removeFavoriteLink);

        return this;
    }

    public ContentActionComponent removeFromFavorites() {
        log.info("Remove from favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        clickElement(contentRow.findElement(removeFavoriteLink));
        waitUntilChildElementIsPresent(contentRow, addToFavoritesLink);

        return this;
    }

    public AlfrescoContentPage clickLocate() {
        log.info("Select Locate action");
        mouseOverContent();
        if (contentModel instanceof FolderModel) {
            clickMore();
        }
        clickElement(getContentRow().findElement(locateAction));
        return new AlfrescoContentPage(webDriver);
    }

    public ContentActionComponent clickRenameIcon() {
        log.info("Click Rename icon");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        WebElement rename = waitUntilElementIsVisible(renameIcon);
        int retryCount = 0;
        while (!contentRowElement.findElement(renameForm).getAttribute("style").equals("display: inline;")
            && retryCount < WAIT_15.getValue()) {
            clickElement(rename);
            retryCount++;
        }
        waitUntilChildElementIsPresent(contentRowElement, renameInput);
        return this;
    }

    public ContentActionComponent typeNewName(String newName) {
        log.info("Rename with value {}", newName);
        WebElement contentRowElement = getContentRow();
        WebElement input = contentRowElement.findElement(renameInput);
        clearAndType(input, newName);

        return this;
    }

    public ContentActionComponent clickSave() {
        log.info("Click Save");
        WebElement input = getContentRow().findElement(renameInput);
        clickElement(getContentRow().findElement(renameSaveButton));
        waitUntilElementDisappears(input);
        try {
            waitUntilElementDisappears(highlightLocator, WAIT_5.getValue());
        } catch (TimeoutException e) {
            log.error("Content has not been highlighted");
        }
        return this;
    }

    public ContentActionComponent clickCancel() {
        log.info("Click Cancel");
        WebElement input = getContentRow().findElement(renameInput);
        clickElement(getContentRow().findElement(renameCancelButton));
        waitUntilElementDisappears(input);

        return this;
    }

    public ContentActionComponent assertThumbnailLinkTypeIsDisplayed() {
        log.info("Assert thumbnail link type is displayed");
        waitUntilElementIsVisible(linkThumbnail);
        assertTrue(isElementDisplayed(getContentRow().findElement(linkThumbnail)),
            String.format("Content %s doesn't have thumbnail link type", contentModel.getName()));

        return this;
    }

    public EditPropertiesDialog clickEditProperties() {
        log.info("Click Edit Properties");
        mouseOverContent();
        clickElement(getContentRow().findElement(editProperties));

        return new EditPropertiesDialog(webDriver);
    }

    public ContentActionComponent assertTagIsDisplayed(String tag) {
        log.info("Assert tag {} is displayed", tag);
        WebElement content = getContentRow();
        waitUntilElementIsVisible(addedTagsLocator);
        List<String> tags = getTextFromElementList(content.findElements(addedTagsLocator));
        assertTrue(tags.contains(tag), String.format("Tag %s is not displayed", tag));

        return this;
    }

    public ContentActionComponent assertTagIsNotDisplayed(String tag) {
        log.info("Assert tag {} is not displayed", tag);
        WebElement content = getContentRow();
        List<String> tags = getTextFromElementList(content.findElements(addedTagsLocator));
        assertFalse(tags.contains(tag), String.format("Tag %s is displayed", tag));

        return this;
    }

    public ContentActionComponent clickTagEditIcon() {
        log.info("Click tag icon");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        mouseOver(contentRowElement.findElement(tagAreaLocator));
        WebElement tagIcon = waitUntilElementIsVisible(tagEditIconLocator);
        clickTagIconWithRetry(contentRowElement, tagIcon);

        return this;
    }

    private void clickTagIconWithRetry(WebElement contentRow, WebElement tagIcon) {
        int retryCount = 0;
        while (!isElementDisplayed(tagInputLocator) && retryCount < RETRY_TIME_80.getValue()) {
            log.warn("Retry click tag icon for content {} - retry: {}", contentModel.getName(), retryCount);
            mouseOver(contentRow.findElement(tagAreaLocator));
            waitToLoopTime(WAIT_2.getValue());
            clickElement(tagIcon);
            retryCount++;
        }
    }

    public ContentActionComponent setTag(String tag) {
        log.info("Set tag {}", tag);
        WebElement contentRowElement = getContentRow();
        WebElement tagInput = contentRowElement.findElement(tagInputLocator);
        tagInput.sendKeys(tag);
        sendKeys(Keys.ENTER);

        return this;
    }

    public ContentActionComponent clickTag(String tag) {
        log.info("Click tag {}", tag);
        By existingTagLocator = By.xpath(String.format(existingTag, tag));
        mouseOver(existingTagLocator);
        clickElement(existingTagLocator);
        return this;
    }

    public ContentActionComponent removeTag(String tag) {
        log.info("Delete tag {}", tag);
        By removeTagLocator = By.xpath(String.format(existingTag.concat(removeTagIcon), tag));
        try {
            waitUntilElementIsVisible(removeTagLocator);
        } catch (TimeoutException e) {
            log.warn("Failed to load tag {}. Refreshing page", tag);
            refresh();
            waitForContentPageToBeLoaded();
        }
        clickElement(removeTagLocator);

        return this;
    }

    public ContentActionComponent assertCategoryIsDisplayed(String category) {
        log.info("Assert category {} is displayed", category);
        WebElement content = getContentRow();
        waitUntilElementIsVisible(addedCategoriesLink);
        List<String> tags = getTextFromElementList(content.findElements(addedCategoriesLink));
        assertTrue(tags.contains(category), String.format("Category %s is not displayed", category));

        return this;
    }

    public ContentActionComponent assertNoCategoriesIsDisplayed() {
        log.info("Assert No Categories is displayed for content {}", contentModel.getName());
        waitUntilElementIsVisible(noCategoriesLocator);
        assertTrue(isElementDisplayed(noCategoriesLocator), "No categories label is not displayed");
        return this;
    }

    public ContentActionComponent assertActionsAreAvailable(String... expectedActions) {
        log.info("Assert available actions are: {}", Arrays.asList(expectedActions));
        waitInSeconds(3);
        List<String> actions = getMoreActions();
        assertTrue(actions.stream().anyMatch(element -> Arrays.asList(expectedActions).contains(element)), "Not all actions were found");

        return this;
    }

    private List<String> getMoreActions() {
        mouseOverContent();
        clickMore();
        return getTextFromLocatorList(actionsSet);
    }

    public ContentActionComponent assertActionsAvailable(String... expectedActions) {
        log.info("Assert available actions are: {}", Arrays.asList(expectedActions));
        waitInSeconds(3);
        List<String> actions = getMoreActions();
        assertTrue(actions.stream().anyMatch(element -> Arrays.asList(expectedActions).contains(element)), "Not all actions were found");

        return this;
    }
}
