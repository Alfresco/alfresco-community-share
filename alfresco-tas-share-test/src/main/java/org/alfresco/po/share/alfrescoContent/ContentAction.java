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
import org.testng.Assert;

public class ContentAction<T>
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
        Assert.assertTrue(getBrowser().isElementDisplayed(getContentRow()),
            String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    public ContentAction assertContentIsNotDisplayed()
    {
        LOG.info("Assert is NOT displayed");
        Assert.assertFalse(getBrowser().isElementDisplayed(
            By.xpath(String.format(alfrescoContentPage.contentRow, contentModel.getName()))),
            String.format("Content '%s' is displayed", contentModel.getName()));
        return this;
    }

    public AlfrescoContentPage selectFolder()
    {
        LOG.info("Select Folder");
        getContentRow().findElement(contentNameSelector).click();
        alfrescoContentPage.waitForCurrentBreadcrumb((FolderModel) contentModel);
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
        getBrowser().mouseOver(getContentRow());
        getBrowser().waitUntilElementHasAttribute(getContentRow(), "class", "yui-dt-highlighted");
        return this;
    }

    private ContentAction clickMore()
    {
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

    public ContentAction assertThumbnailIsLinkType()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(getContentRow().findElement(linkThumbnail)),
            String.format("Content %s has link thumbnail type", contentModel.getName()));
        return this;
    }
}
