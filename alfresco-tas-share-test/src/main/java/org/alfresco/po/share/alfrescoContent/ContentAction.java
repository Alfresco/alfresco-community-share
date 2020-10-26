package org.alfresco.po.share.alfrescoContent;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ContentAction
{
    private final Logger LOG = LoggerFactory.getLogger(ContentAction.class);

    private AlfrescoContentPage alfrescoContentPage;
    private DocumentDetailsPage documentDetailsPage;
    private CopyMoveUnzipToDialog copyMoveDialog;
    private ContentModel contentModel;

    private By contentNameSelector = By.cssSelector(".filename a");
    private By moreActionLink = By.cssSelector(".show-more");
    private By moreActionsArea = By.cssSelector(".more-actions");
    private By copyToAction = By.id("onActionCopyTo");
    private By moveToAction = By.id("onActionMoveTo");
    private By linkThumbnail = By.cssSelector(".thumbnail .link");
    private String highlightContent = "yui-dt-highlighted";

    public ContentAction(ContentModel contentModel, AlfrescoContentPage contentPage,
                         DocumentDetailsPage documentDetailsPage,
                         CopyMoveUnzipToDialog copyMoveDialog)
    {
        this.contentModel = contentModel;
        this.alfrescoContentPage = contentPage;
        this.documentDetailsPage = documentDetailsPage;
        this.copyMoveDialog = copyMoveDialog;

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
        WebElement content = getContentRow();
        getBrowser().waitUntilElementVisible(content, 5);
        assertTrue(getBrowser().isElementDisplayed(getContentRow()),
            String.format("Content '%s' is not displayed", contentModel.getName()));
        return this;
    }

    public ContentAction assertContentIsNotDisplayed()
    {
        LOG.info("Assert is NOT displayed");
        assertFalse(getBrowser().isElementDisplayed(
            By.xpath(String.format(alfrescoContentPage.contentRow, contentModel.getName()))),
                String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    public AlfrescoContentPage selectFolder()
    {
        LOG.info("Select Folder");
        WebElement contentRow = getContentRow();
        WebElement content = getBrowser().waitUntilChildElementIsPresent(contentRow, contentNameSelector);
        getBrowser().mouseOver(content);
        content.click();
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

    private ContentAction mouseOverContent()
    {
        LOG.info("Mouse over content");
        WebElement contentRow = getContentRow();
        getBrowser().mouseOver(contentRow);
        getBrowser().waitUntilElementHasAttribute(contentRow, "class", highlightContent);
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
}
