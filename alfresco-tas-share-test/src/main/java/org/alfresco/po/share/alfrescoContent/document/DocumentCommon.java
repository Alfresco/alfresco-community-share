package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@PageObject
@Primary
public abstract class DocumentCommon<T> extends SharePage<DocumentCommon<T>>
{
    private FileModel currentFile;

    @FindBy (css = "img[alt='active-workflows']")
    private WebElement activeWorkflowIcon;

    @FindBy (css = "span.faded")
    private List<WebElement> fadedDetails;

    public void setCurrentFileModel(FileModel currentFileModel)
    {
        this.currentFile = currentFileModel;
    }

    public FileModel getCurrentFile()
    {
        return currentFile;
    }

    @SuppressWarnings ("unchecked")
    public T navigate(FileModel file)
    {
        LOG.info(String.format("Navigate to document details of file: %s", file.getCmisLocation()));
        setCurrentFileModel(file);
        return (T) navigate();
    }

    public boolean isPropertyValueDisplayed(String propertyValue)
    {
        String value = "//span[contains(text(), '" + propertyValue + "')]";
        WebElement filePropertyValue = browser.findElement(By.xpath(value));
        return filePropertyValue.isDisplayed();

    }

    public boolean isActiveWorkflowIconDisplayed()
    {
        return browser.isElementDisplayed(activeWorkflowIcon);
    }

    /**
     * @return Details for Folders
     */
    public String getFadedDetailsList()
    {
        browser.waitUntilElementVisible(By.xpath("//span[text() = 'No Categories']"));
        List<WebElement> fadedDetails = browser.findElements(By.cssSelector("span.faded"));

        ArrayList<String> fadedText = new ArrayList<>();
        for (WebElement fadedDetail : fadedDetails)
        {
            fadedText.add(fadedDetail.getText());
        }
        return fadedText.toString();
    }

}
