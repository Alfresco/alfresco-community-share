package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
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
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    private WebElement getImageThumbnail(String imageName)
    {
        return waitWithRetryAndReturnWebElement(
            By.xpath(String.format(imageLink, imageName)), WAIT_2.getValue(), RETRY_TIME_80.getValue());
    }

    public ImagePreviewDashlet assertImagePreviewIsDisplayed(String imageName)
    {
        log.info("Assert image name is: {}", imageName);
        assertTrue(isElementDisplayed(getImageThumbnail(imageName)));
        return this;
    }

    public void clickViewDetailsIcon(String imageName)
    {
        log.info("Click View details icon");
        WebElement viewDetailsAction = getImageThumbnail(imageName).findElement(viewDetailsIcon);
        mouseOver(viewDetailsAction);
        clickElement(viewDetailsAction);
    }

    public ImagePreviewDashlet clickDownloadIcon(String imageName)
    {
        log.info("Click download icon from image preview dashlet: {}", imageName);
        WebElement downloadAction = getImageThumbnail(imageName).findElement(downloadIcon);
        mouseOver(downloadAction);
        clickElement(downloadAction);
        return this;
    }

    public void assertDownloadedDocumentExists(String fileName, String extension)
    {
        log.info("Assert document exists in directory");
        assertTrue(Utils.isFileInDirectory(fileName, extension), "Document not exists in directory");

    }
}