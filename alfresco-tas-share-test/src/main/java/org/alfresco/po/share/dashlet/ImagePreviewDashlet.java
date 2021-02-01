package org.alfresco.po.share.dashlet;

import org.alfresco.common.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_60;
import static org.testng.Assert.assertTrue;

public class ImagePreviewDashlet extends Dashlet<ImagePreviewDashlet>
{
    private final By dashletContainer = By.xpath("//div[normalize-space(.) = 'Image Preview']");
    private final By dashletTitle = By.xpath("//div[starts-with(@class,'dashlet resizable')] // div[@class='title']");
    private final By viewDetailsIcon = By.cssSelector("img[src*='document-view-details']");
    private final By downloadIcon = By.cssSelector("img[src*='download']");
    private final String imageLink = "//div[@class='thumbnail']/a[contains(@title, '%s')]/..";

    public ImagePreviewDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    private WebElement getImageThumbnail(String imageName)
    {
        return webElementInteraction.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(imageLink, imageName)), WAIT_2.getValue(), WAIT_60.getValue());
    }

    public ImagePreviewDashlet assertImagePreviewIsDisplayed(String imageName)
    {
        LOG.info("Assert image name is: {}", imageName);
        assertTrue(webElementInteraction.isElementDisplayed(getImageThumbnail(imageName)));
        return this;
    }

    public void clickViewDetailsIcon(String imageName)
    {
        LOG.info("Click View details icon");
        WebElement viewDetailsAction = getImageThumbnail(imageName).findElement(viewDetailsIcon);
        webElementInteraction.mouseOver(viewDetailsAction);
        webElementInteraction.clickElement(viewDetailsAction);
    }

    public ImagePreviewDashlet clickDownloadIcon(String imageName)
    {
        LOG.info("Click download icon from image preview dashlet: {}", imageName);
        WebElement downloadAction = getImageThumbnail(imageName).findElement(downloadIcon);
        webElementInteraction.mouseOver(downloadAction);
        webElementInteraction.clickElement(downloadAction);
        return this;
    }

    public ImagePreviewDashlet assertDownloadedDocumentExists(String fileName, String extension)
    {
        LOG.info("Assert document exists in directory");
        assertTrue(Utils.isFileInDirectory(fileName, extension), "Document not exists in directory");

        return this;
    }
}