package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.alfresco.common.Utils;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

@PageObject
public class ImagePreviewDashlet extends Dashlet<ImagePreviewDashlet>
{
    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @RenderWebElement
    @FindBy (xpath = "//div[normalize-space(.) = 'Image Preview']")
    protected HtmlElement dashletContainer;

    @RenderWebElement
    @FindBy (xpath = "//div[starts-with(@class,'dashlet resizable')] // div[@class='title']")
    private WebElement dashletTitle;

    private By viewDetailsIcon = By.cssSelector("img[src*='document-view-details']");
    private By downloadIcon = By.cssSelector("img[src*='download']");
    private static final String imageLink = "//div[@class='thumbnail']/a[contains(@title, '%s')]/..";

    @Override
    public String getDashletTitle()
    {
        return dashletTitle.getText();
    }

    private WebElement getImageThumbnail(String imageName)
    {
        return browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(imageLink, imageName)), 1, WAIT_60);
    }

    public ImagePreviewDashlet assertImagePreviewIsDisplayed(String imageName)
    {
        LOG.info("Assert image name is: {}", imageName);
        assertTrue(browser.isElementDisplayed(getImageThumbnail(imageName)));

        return this;
    }

    public DocumentDetailsPage clickViewDetailsIcon(String imageName)
    {
        LOG.info("Click \"View details icon\"");
        WebElement viewDetailsAction = getImageThumbnail(imageName).findElement(viewDetailsIcon);
        browser.mouseOver(viewDetailsAction);
        browser.waitUntilElementVisible(viewDetailsAction).click();

        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public ImagePreviewDashlet clickDownloadIcon(String imageName)
    {
        LOG.info("Click download icon from image preview dashlet: {}", imageName);
        WebElement downloadAction = getImageThumbnail(imageName).findElement(downloadIcon);
        browser.mouseOver(downloadAction);
        browser.waitUntilElementVisible(downloadAction).click();

        return this;
    }

    public ImagePreviewDashlet assertDownloadedDocumentExists(String fileName, String extension)
    {
        LOG.info("Assert document exists in directory");
        assertTrue(Utils.isFileInDirectory(fileName, extension), "Document not exists in directory");

        return this;
    }
}