package org.alfresco.po.share.alfrescoContent;

import org.alfresco.common.WebElementInteraction;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FolderModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.alfresco.common.Wait.WAIT_15;
import static org.testng.Assert.*;

public class ContentActionComponent
{
    private final Logger LOG = LoggerFactory.getLogger(ContentActionComponent.class);

    private final WebElementInteraction webElementInteraction;
    private final AlfrescoContentPage alfrescoContentPage;
    private final DocumentDetailsPage documentDetailsPage;
    private final CopyMoveUnzipToDialog copyMoveDialog;
    private final DeleteDialog deleteDialog;
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

    private final String highlightContent = "yui-dt-highlighted";
    private final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";

    public ContentActionComponent(ContentModel contentModel,
                                  WebElementInteraction webElementInteraction,
                                  AlfrescoContentPage contentPage,
                                  DocumentDetailsPage documentDetailsPage,
                                  CopyMoveUnzipToDialog copyMoveDialog,
                                  DeleteDialog deleteDialog)
    {
        this.contentModel = contentModel;
        this.webElementInteraction = webElementInteraction;
        this.alfrescoContentPage = contentPage;
        this.documentDetailsPage = documentDetailsPage;
        this.copyMoveDialog = copyMoveDialog;
        this.deleteDialog = deleteDialog;


        LOG.info("Using content: {}", contentModel.getName());
    }

    private WebElement getContentRow()
    {
        return alfrescoContentPage.getContentRow(contentModel.getName());
    }

    public ContentActionComponent assertContentIsDisplayed()
    {
        LOG.info("Assert content is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(getContentRow()),
            String.format("Content '%s' is not displayed", contentModel.getName()));

        return this;
    }

    public ContentActionComponent assertContentIsNotDisplayed()
    {
        LOG.info("Assert content is not displayed");
        By content = By.xpath(String.format(contentRow, contentModel.getName()));
        webElementInteraction.waitUntilElementDisappears(content);
        assertFalse(webElementInteraction.isElementDisplayed(content), String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    public AlfrescoContentPage selectFolder()
    {
        LOG.info("Select Folder");
        WebElement contentRowElement = getContentRow();
        WebElement content = webElementInteraction.waitUntilChildElementIsPresent(contentRowElement, contentNameSelector);
        webElementInteraction.mouseOver(content);
        webElementInteraction.clickElement(content);
        alfrescoContentPage.waitForCurrentFolderBreadcrumb((FolderModel) contentModel);

        return alfrescoContentPage;
    }

    public DocumentDetailsPage selectFile()
    {
        LOG.info("Select file");
        webElementInteraction.clickElement(getContentRow().findElement(contentNameSelector));
        return documentDetailsPage;
    }

    public ContentActionComponent assertContentIsHighlighted()
    {
        By contentRowElement = By.xpath(String.format(contentRow, contentModel.getName()));
        webElementInteraction.waitUntilElementIsPresent(contentRowElement);
        WebElement content = webElementInteraction.waitUntilElementIsVisible(contentRowElement);
        assertTrue(content.getAttribute("class").contains(highlightContent), "Content is not highlighted");
        return this;
    }

    private ContentActionComponent mouseOverContent()
    {
        LOG.info("Mouse over content");
        webElementInteraction.mouseOver(getContentRow().findElement(contentNameSelector));
        webElementInteraction.waitUntilElementHasAttribute(getContentRow(), "class", highlightContent);

        return this;
    }

    private ContentActionComponent clickMore()
    {
        LOG.info("Click More");
        WebElement moreAction = webElementInteraction.waitUntilChildElementIsPresent(getContentRow(), moreActionLink);
        webElementInteraction.mouseOver(moreAction);
        webElementInteraction.clickElement(moreAction);
        webElementInteraction.waitUntilChildElementIsPresent(getContentRow(), moreActionsArea);

        return this;
    }

    public CopyMoveUnzipToDialog clickCopyTo()
    {
        LOG.info("Click Copy To...");
        mouseOverContent();
        clickMore();
        webElementInteraction.clickElement(getContentRow().findElement(copyToAction));

        return copyMoveDialog;
    }

    public CopyMoveUnzipToDialog clickMoveTo()
    {
        LOG.info("Click Move To...");
        mouseOverContent();
        clickMore();
        webElementInteraction.clickElement(getContentRow().findElement(moveToAction));

        return copyMoveDialog;
    }

    public DeleteDialog clickDelete()
    {
        LOG.info("Click Delete");
        mouseOverContent();
        clickMore();
        webElementInteraction.clickElement(getContentRow().findElement(deleteAction));
        return deleteDialog;
    }

    public ContentActionComponent assertAddFileToFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.addDocumentToFavorites");
        LOG.info("Assert add file to favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertAddFolderToFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.addFolderToFavorites");
        LOG.info("Assert add folder to favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertRemoveFileFromFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.removeDocumentFromFavorites");
        LOG.info("Assert remove file from favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.removeFolderFromFavorites");
        LOG.info("Assert remove folder from favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentActionComponent assertAddToFavoritesIsDisplayed()
    {
        LOG.info("Assert Add to favorites is displayed");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        assertTrue(webElementInteraction.isElementDisplayed(contentRowElement.findElement(addToFavoritesLink)),
        "Add to favorites link is not displayed");

        return this;
    }

    public ContentActionComponent assertRemoveFromFavoritesIsDisplayed()
    {
        LOG.info("Assert remove from favorites is displayed");
        WebElement contentRowElement = getContentRow();
        mouseOverContent();
        assertTrue(webElementInteraction.isElementDisplayed(contentRowElement.findElement(removeFavoriteLink)),
                "Remove from favorites link is not displayed");

        return this;
    }

    public ContentActionComponent addToFavorites()
    {
        LOG.info("Add to favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        webElementInteraction.clickElement(contentRow.findElement(addToFavoritesLink));
        webElementInteraction.waitUntilChildElementIsPresent(contentRow, removeFavoriteLink);

        return this;
    }

    public ContentActionComponent removeFromFavorites()
    {
        LOG.info("Remove from favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        webElementInteraction.clickElement(contentRow.findElement(removeFavoriteLink));
        webElementInteraction.waitUntilChildElementIsPresent(contentRow, addToFavoritesLink);

        return this;
    }

    public AlfrescoContentPage clickLocate()
    {
        LOG.info("Select Locate action");
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
        LOG.info("Click Rename icon");
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
        LOG.info("Rename with value {}", newName);
        WebElement contentRowElement = getContentRow();
        WebElement input = contentRowElement.findElement(renameInput);
        webElementInteraction.clearAndType(input, newName);

        return this;
    }

    public ContentActionComponent clickSave()
    {
        LOG.info("Click Save");
        WebElement input = getContentRow().findElement(renameInput);
        webElementInteraction.clickElement(getContentRow().findElement(renameSaveButton));
        webElementInteraction.waitUntilElementDisappears(input);
        return this;
    }

    public ContentActionComponent clickCancel()
    {
        LOG.info("Click Cancel");
        WebElement input = getContentRow().findElement(renameInput);
        webElementInteraction.clickElement(getContentRow().findElement(renameCancelButton));
        webElementInteraction.waitUntilElementDisappears(input);

        return this;
    }
}
