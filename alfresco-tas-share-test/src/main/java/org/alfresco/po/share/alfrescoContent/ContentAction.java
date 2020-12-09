package org.alfresco.po.share.alfrescoContent;

import static org.alfresco.common.Wait.WAIT_15;
import static org.testng.Assert.*;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentAction
{
    private final Logger LOG = LoggerFactory.getLogger(ContentAction.class);

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
    private final By linkThumbnail = By.cssSelector(".thumbnail .link");
    private final By deleteAction = By.id("onActionDelete");
    private final By addToFavoritesLink = By.className("favourite-action");
    private final By removeFavoriteLink = By.cssSelector("a[class='favourite-action enabled']");
    private final By locateAction = By.id("onActionLocate");
    private final By renameIcon = By.cssSelector(".filename span.insitu-edit[style*='visibility: visible']");
    private final By renameForm = By.cssSelector("form[class='insitu-edit']");
    private final By renameInput = By.cssSelector("form[class='insitu-edit']>input");
    private final By renameSaveButton = By.cssSelector("form[class='insitu-edit']>a:nth-of-type(1)");
    private final By renameCancelButton = By.cssSelector("form[class='insitu-edit']>a:nth-of-type(2)");

    private final String highlightContent = "yui-dt-highlighted";
    private final String contentRow = "//h3[@class='filename']//a[text()='%s']/../../../../..";

    public ContentAction(ContentModel contentModel,
                         AlfrescoContentPage contentPage,
                         DocumentDetailsPage documentDetailsPage,
                         CopyMoveUnzipToDialog copyMoveDialog,
                         DeleteDialog deleteDialog)
    {
        this.contentModel = contentModel;
        this.alfrescoContentPage = contentPage;
        this.documentDetailsPage = documentDetailsPage;
        this.copyMoveDialog = copyMoveDialog;
        this.deleteDialog = deleteDialog;

        LOG.info(String.format("Using content: '%s'", contentModel.getName()));
    }

    private WebBrowser getBrowser()
    {
        return alfrescoContentPage.getBrowser();
    }

    private WebElement getContentRow()
    {
        return alfrescoContentPage.getContentRow(contentModel.getName());
    }

    public ContentAction assertContentIsDisplayed()
    {
        LOG.info("Assert is displayed");
        assertTrue(getBrowser().isElementDisplayed(getContentRow()),
            String.format("Content '%s' is not displayed", contentModel.getName()));

        return this;
    }

    public ContentAction assertContentIsNotDisplayed()
    {
        LOG.info("Assert content is not displayed");
        By content = By.xpath(String.format(contentRow, contentModel.getName()));
        assertFalse(getBrowser().isElementDisplayed(content), String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    public AlfrescoContentPage selectFolder()
    {
        LOG.info("Select Folder");
        WebElement contentRow = getContentRow();
        WebElement content = getBrowser().waitUntilChildElementIsPresent(contentRow, contentNameSelector);
        getBrowser().mouseOver(content);
        getBrowser().waitUntilElementClickable(content).click();
        alfrescoContentPage.waitForCurrentFolderBreadcrumb((FolderModel) contentModel);

        return alfrescoContentPage;
    }

    public DocumentDetailsPage selectFile()
    {
        LOG.info("Select file");
        getContentRow().findElement(contentNameSelector).click();
        documentDetailsPage.renderedPage();

        return documentDetailsPage;
    }

    public ContentAction assertContentIsHighlighted()
    {
        LOG.info("Assert content is highlighted");
        assertTrue(getContentRow().getAttribute("class").contains(highlightContent), "Content is not highlighted");
        return this;
    }

    private ContentAction mouseOverContent()
    {
        LOG.info("Mouse over content");
        getBrowser().mouseOver(getContentRow().findElement(contentNameSelector));
        getBrowser().waitUntilElementHasAttribute(getContentRow(), "class", highlightContent);

        return this;
    }

    private ContentAction clickMore()
    {
        LOG.info("Click More");
        WebElement moreAction = getBrowser().waitUntilChildElementIsPresent(getContentRow(), moreActionLink);
        getBrowser().mouseOver(moreAction);
        moreAction.click();
        getBrowser().waitUntilChildElementIsPresent(getContentRow(), moreActionsArea);

        return this;
    }

    public CopyMoveUnzipToDialog clickCopyTo()
    {
        LOG.info("Click Copy To...");
        mouseOverContent();
        clickMore();
        getContentRow().findElement(copyToAction).click();

        return (CopyMoveUnzipToDialog) copyMoveDialog.renderedPage();
    }

    public CopyMoveUnzipToDialog clickMoveTo()
    {
        LOG.info("Click Move To...");
        mouseOverContent();
        clickMore();
        getContentRow().findElement(moveToAction).click();

        return (CopyMoveUnzipToDialog) copyMoveDialog.renderedPage();
    }

    public ContentAction assertThumbnailLinkTypeIsDisplayed()
    {
        LOG.info("Assert thumbnail link type is displayed");
        assertTrue(getBrowser().isElementDisplayed(getContentRow().findElement(linkThumbnail)),
            String.format("Content %s doesn't have thumbnail link type", contentModel.getName()));

        return this;
    }

    public DeleteDialog clickDelete()
    {
        LOG.info("Click Delete");
        mouseOverContent();
        clickMore();
        getContentRow().findElement(deleteAction).click();

        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public ContentAction assertAddFileToFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.addDocumentToFavorites");
        LOG.info("Assert add file to favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tootip not equals to %s", tooltipValue));
        return this;
    }

    public ContentAction assertAddFolderToFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.addFolderToFavorites");
        LOG.info("Assert add folder to favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(addToFavoritesLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentAction assertRemoveFileFromFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.removeDocumentFromFavorites");
        LOG.info("Assert remove file from favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentAction assertRemoveFolderFromFavoritesTooltipEqualsWithExpected()
    {
        String tooltipValue = alfrescoContentPage.language.translate("documentLibrary.contentAction.removeFolderFromFavorites");
        LOG.info("Assert remove folder from favorites tooltip equals to {}", tooltipValue);
        assertEquals(getContentRow().findElement(removeFavoriteLink).getAttribute("title"), tooltipValue,
            String.format("Add to favorite tooltip not equals to %s", tooltipValue));
        return this;
    }

    public ContentAction assertAddToFavoritesIsDisplayed()
    {
        LOG.info("Assert Add to favorites is displayed");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        assertTrue(getBrowser().isElementDisplayed(contentRow.findElement(addToFavoritesLink)),
        "Add to favorites link is not displayed");

        return this;
    }

    public ContentAction assertRemoveFromFavoritesIsDisplayed()
    {
        LOG.info("Assert remove from favorites is displayed");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        assertTrue(getBrowser().isElementDisplayed(contentRow.findElement(removeFavoriteLink)),
                "Remove from favorites link is not displayed");

        return this;
    }

    public ContentAction addToFavorites()
    {
        LOG.info("Add to favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        contentRow.findElement(addToFavoritesLink).click();
        getBrowser().waitUntilChildElementIsPresent(contentRow, removeFavoriteLink);

        return this;
    }

    public ContentAction removeFromFavorites()
    {
        LOG.info("Remove from favorites");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        contentRow.findElement(removeFavoriteLink).click();
        getBrowser().waitUntilChildElementIsPresent(contentRow, addToFavoritesLink);

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
        getContentRow().findElement(locateAction).click();
        return alfrescoContentPage;
    }

    public ContentAction assertContentIsChecked()
    {
        LOG.info("Assert content is checked");
        assertTrue(getContentRow().findElement(alfrescoContentPage.selectCheckBox).isSelected(),
            String.format("Content %s is not checked", contentModel.getName()));
        return this;
    }

    public ContentAction assertContentIsNotChecked()
    {
        LOG.info("Assert content is not checked");
        assertTrue(getContentRow().findElement(alfrescoContentPage.selectCheckBox).isSelected(),
            String.format("Content %s is checked", contentModel.getName()));
        return this;
    }

    /**
     * Method to click on rename icon
     *
     * @return instance of ContentAction
     *
     * @implNote Depends on test environments, style attribute of renameForm web element is empty or not.
     * The workaround is to perform a click on rename icon web element, while style attribute is empty
     */
    public ContentAction clickRenameIcon()
    {
        LOG.info("Click Rename icon");
        WebElement contentRow = getContentRow();
        mouseOverContent();
        WebElement rename = getBrowser().waitUntilElementVisible(renameIcon);
        int i = 0;
        while (!contentRow.findElement(renameForm).getAttribute("style").equals("display: inline;")
            && i < WAIT_15.getValue())
        {
            getBrowser().waitUntilElementClickable(rename).click();
            i++;
        }
        getBrowser().waitUntilChildElementIsPresent(contentRow, renameInput);
        return this;
    }

    public ContentAction typeNewName(String newName)
    {
        LOG.info("Rename with value {}", newName);
        WebElement contentRow = getContentRow();
        WebElement input = contentRow.findElement(renameInput);
        alfrescoContentPage.clearAndType(input, newName);

        return this;
    }

    public ContentAction clickSave()
    {
        LOG.info("Click Save");
        WebElement input = getContentRow().findElement(renameInput);
        getContentRow().findElement(renameSaveButton).click();
        getBrowser().waitUntilElementDisappears(input);

        return this;
    }

    public ContentAction clickCancel()
    {
        LOG.info("Click Cancel");
        WebElement input = getContentRow().findElement(renameInput);
        getContentRow().findElement(renameCancelButton).click();
        getBrowser().waitUntilElementDisappears(input);

        return this;
    }
}
