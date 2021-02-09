package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.Wait.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Timeout;
import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.dataLists.Content;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FolderModel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

@Slf4j
public class ContentActionComponent
{
    private final WebElementInteraction webElementInteraction;
    private final AlfrescoContentPage alfrescoContentPage;
    private final DocumentDetailsPage documentDetailsPage;
    private final CopyMoveUnzipToDialog copyMoveDialog;
    private final DeleteDialog deleteDialog;
    private final EditPropertiesDialog editPropertiesDialog;
    private final ContentModel contentModel;

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

    private final String highlightContent = "yui-dt-highlighted";
    private final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";

    public ContentActionComponent(ContentModel contentModel,
                                  WebElementInteraction webElementInteraction,
                                  AlfrescoContentPage contentPage,
                                  DocumentDetailsPage documentDetailsPage,
                                  CopyMoveUnzipToDialog copyMoveDialog,
                                  DeleteDialog deleteDialog,
                                  EditPropertiesDialog editPropertiesDialog)
    {
        this.contentModel = contentModel;
        this.webElementInteraction = webElementInteraction;
        this.alfrescoContentPage = contentPage;
        this.documentDetailsPage = documentDetailsPage;
        this.copyMoveDialog = copyMoveDialog;
        this.deleteDialog = deleteDialog;
        this.editPropertiesDialog = editPropertiesDialog;

        log.info("Using content: {}", contentModel.getName());
    }

    private WebElement getContentRow()
    {
        return alfrescoContentPage.getContentRow(contentModel.getName());
    }

    public ContentActionComponent assertContentIsDisplayed()
    {
        log.info("Assert content is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getContentRow()),
            String.format("Content '%s' is not displayed", contentModel.getName()));

        return this;
    }

    public ContentActionComponent assertContentIsNotDisplayed()
    {
        log.info("Assert content is not displayed");
        By content = By.xpath(String.format(contentRow, contentModel.getName()));
        try
        {
            webElementInteraction.waitUntilElementDisappears(content, WAIT_5.getValue());
        }
        catch (TimeoutException e)
        {
            log.error("Content {} is still displayed in Alfresco Content page", contentModel.getName());
        }

        assertFalse(webElementInteraction.isElementDisplayed(content), String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    @SuppressWarnings("rawtypes")
	public AlfrescoContentPage selectFolder()
    {
        log.info("Select Folder");
        WebElement contentRowElement = getContentRow();
        WebElement content = webElementInteraction.waitUntilChildElementIsPresent(contentRowElement, contentNameSelector);
        webElementInteraction.mouseOver(content);
        webElementInteraction.clickElement(content);
        alfrescoContentPage.waitForCurrentFolderBreadcrumb((FolderModel) contentModel);

        return alfrescoContentPage;
    }

    public DocumentDetailsPage selectFile()
    {
        log.info("Select file");
        webElementInteraction.clickElement(getContentRow().findElement(contentNameSelector));
        return documentDetailsPage;
    }

    public ContentActionComponent assertContentIsHighlighted()
    {
        By contentRowElement = By.xpath(String.format(contentRow, contentModel.getName()));
        WebElement content = webElementInteraction.waitUntilElementIsPresent(contentRowElement);
        assertTrue(content.getAttribute("class").contains(highlightContent), "Content is not highlighted");
        return this;
    }

    private ContentActionComponent mouseOverContent()
    {
        log.info("Mouse over content");
        webElementInteraction.mouseOver(getContentRow().findElement(contentNameSelector));
        webElementInteraction.waitUntilElementHasAttribute(getContentRow(), "class", highlightContent);

        return this;
    }

    private ContentActionComponent clickMore()
    {
        log.info("Click More");
        WebElement moreAction = webElementInteraction.waitUntilChildElementIsPresent(getContentRow(), moreActionLink);
        webElementInteraction.mouseOver(moreAction);
        webElementInteraction.clickElement(moreAction);
        webElementInteraction.waitUntilChildElementIsPresent(getContentRow(), moreActionsArea);

        return this;
    }

    public CopyMoveUnzipToDialog clickCopyTo()
    {
        log.info("Click Copy To...");
        mouseOverContent();
        clickMore();
        webElementInteraction.clickElement(getContentRow().findElement(copyToAction));

        return copyMoveDialog;
    }

    public CopyMoveUnzipToDialog clickMoveTo()
    {
        log.info("Click Move To...");
        mouseOverContent();
        clickMore();
        webElementInteraction.clickElement(getContentRow().findElement(moveToAction));

        return copyMoveDialog;
    }

    public DeleteDialog clickDelete()
    {
        log.info("Click Delete");
        mouseOverContent();
        clickMore();
        webElementInteraction.clickElement(getContentRow().findElement(deleteAction));
        return deleteDialog;
    }

    public ContentActionComponent assertAddFileToFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.addDocumentToFavorites");
        log.info("Assert add file to favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertAddFolderToFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.addFolderToFavorites");
        log.info("Assert add folder to favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertRemoveFileFromFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.removeDocumentFromFavorites");
        log.info("Assert remove file from favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.removeFolderFromFavorites");
        log.info("Assert remove folder from favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertAddToFavoritesIsDisplayed()
    {
        log.info("Assert Add to favorites is displayed");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        assertTrue(webElementInteraction.isElementDisplayed(contentRowElement.findElement(addToFavoritesLink)),
        "Add to favorites link is not displayed");

        return this;
    }

    public ContentActionComponent assertRemoveFromFavoritesIsDisplayed()
    {
        log.info("Assert remove from favorites is displayed");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        assertTrue(webElementInteraction.isElementDisplayed(contentRowElement.findElement(removeFavoriteLink)),
                "Remove from favorites link is not displayed");

        return this;
    }

    public ContentActionComponent addToFavorites()
    {
        log.info("Add to favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        webElementInteraction.clickElement(contentRow.findElement(addToFavoritesLink));
        webElementInteraction.waitUntilChildElementIsPresent(contentRow, removeFavoriteLink);

        return this;
    }

    public ContentActionComponent removeFromFavorites()
    {
        log.info("Remove from favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        webElementInteraction.clickElement(contentRow.findElement(removeFavoriteLink));
        webElementInteraction.waitUntilChildElementIsPresent(contentRow, addToFavoritesLink);

        return this;
    }

    public AlfrescoContentPage clickLocate()
    {
        log.info("Select Locate action");
        mouseOverContent();
        if (contentModel instanceof FolderModel)
        {
            clickMore();
        }
        webElementInteraction.clickElement(getContentRow().findElement(locateAction));
        return alfrescoContentPage;
    }

    public ContentActionComponent clickRenameIcon()
    {
        log.info("Click Rename icon");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        WebElement rename = webElementInteraction.waitUntilElementIsVisible(renameIcon);
        int i = 0;
        while (!contentRowElement.findElement(renameForm).getAttribute("style").equals("display: inline;")
            && i < WAIT_15.getValue())
        {
            webElementInteraction.clickElement(rename);
            i++;
        }
        webElementInteraction.waitUntilChildElementIsPresent(contentRowElement, renameInput);
        return this;
    }

    public ContentActionComponent typeNewName(String newName)
    {
        log.info("Rename with value {}", newName);
        WebElement contentRowElement = getContentRow();
        WebElement input = contentRowElement.findElement(renameInput);
        webElementInteraction.clearAndType(input, newName);

        return this;
    }

    public ContentActionComponent clickSave()
    {
        log.info("Click Save");
        WebElement input = getContentRow().findElement(renameInput);
        webElementInteraction.clickElement(getContentRow().findElement(renameSaveButton));
        webElementInteraction.waitUntilElementDisappears(input);
        try
        {
            webElementInteraction.waitUntilElementDisappears(highlightLocator, WAIT_5.getValue());
        }
        catch (TimeoutException e)
        {
            log.error("Content has not been highlighted");
        }

        return this;
    }

    public ContentActionComponent clickCancel()
    {
        log.info("Click Cancel");
        WebElement input = getContentRow().findElement(renameInput);
        webElementInteraction.clickElement(getContentRow().findElement(renameCancelButton));
        webElementInteraction.waitUntilElementDisappears(input);

        return this;
    }

    public ContentActionComponent assertThumbnailLinkTypeIsDisplayed()
    {
        log.info("Assert thumbnail link type is displayed");
        webElementInteraction.waitUntilElementIsVisible(linkThumbnail);
        assertTrue(webElementInteraction.isElementDisplayed(getContentRow().findElement(linkThumbnail)),
            String.format("Content %s doesn't have thumbnail link type", contentModel.getName()));

        return this;
    }

    public EditPropertiesDialog clickEditProperties()
    {
        log.info("Click Edit Properties");
        mouseOverContent();
        webElementInteraction.clickElement(getContentRow().findElement(editProperties));

        return editPropertiesDialog;
    }

    public ContentActionComponent assertTagIsDisplayed(String tag)
    {
        log.info("Assert tag {} is displayed", tag);
        WebElement content = getContentRow();
        List<String> tags = webElementInteraction.getTextFromElementList(content.findElements(addedTagsLocator));
        assertTrue(tags.contains(tag), String.format("Tag %s is not displayed", tag));

        return this;
    }

    public ContentActionComponent clickTagIcon()
    {
        log.info("Click tag icon");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        webElementInteraction.mouseOver(contentRowElement.findElement(tagAreaLocator));
        WebElement tagIcon = webElementInteraction.waitUntilElementIsVisible(tagEditIconLocator);
        int i = 0;
        while (!webElementInteraction.isElementDisplayed(tagInputLocator) && i < WAIT_15.getValue())
        {
            log.error("Retry click tag icon for content {}", contentModel.getName());
            webElementInteraction.clickElement(tagIcon);
            i++;
        }

        return this;
    }

    public ContentActionComponent addTag(String tag)
    {
        WebElement contentRowElement = getContentRow();
        WebElement tagInput = contentRowElement.findElement(tagInputLocator);
        tagInput.sendKeys(tag);
        webElementInteraction.sendKeys(Keys.ENTER);

        return this;
    }
}
