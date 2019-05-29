package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@PageObject
@Primary
public class DocumentCommon<T> extends SharePage<DocumentCommon<T>>
{
    private String currentDocumentName;

    @FindBy (css = "img[alt='active-workflows']")
    private WebElement activeWorkflowIcon;

    @FindBy (css = "span.faded")
    private List<WebElement> fadedDetails;

    private String currentSiteName;

    public void setCurrentDocumentName(String currentDocumentName)
    {
        this.currentDocumentName = currentDocumentName;
    }

    @SuppressWarnings ("unchecked")
    public T navigate(String documentName)
    {
        setCurrentDocumentName(documentName);
        return (T) navigate();
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public String getCurrentSiteName()
    {
        return currentSiteName;
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

    public boolean isAlertPresent()
    {
        try
        {
            browser.switchTo().alert();
            return true;
        } catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
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
