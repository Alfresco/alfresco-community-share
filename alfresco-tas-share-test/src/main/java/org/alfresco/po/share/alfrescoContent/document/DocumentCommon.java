package org.alfresco.po.share.alfrescoContent.document;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class DocumentCommon<T> extends SharePage<DocumentCommon<T>> {
  @Autowired
  EditInAlfrescoPage editInAlfresco;

  private String currentDocumentName;

  @FindBy(css = "img[alt='active-workflows']")
  private WebElement activeWorkflowIcon;

  @FindBy(css = "span.faded")
  private List<WebElement> fadedDetails;

  private String currentSiteName;

  public void setCurrentDocumentName(String currentDocumentName) {
    this.currentDocumentName = currentDocumentName;
  }

  @SuppressWarnings("unchecked")
  public T navigate(String documentName) {
    setCurrentDocumentName(documentName);
    return (T) navigate();
  }

  @Override
  public String getRelativePath() {
    return null;
  }

  public String getCurrentSiteName() {
    return currentSiteName;
  }

  public boolean isPropertyValueDisplayed(String propertyValue) {
    String value = "//span[contains(text(), '" + propertyValue + "')]";
    WebElement filePropertyValue = browser.findElement(By.xpath(value));
    return filePropertyValue.isDisplayed();

  }

  public boolean isActiveWorkflowIconDisplayed() {
    return browser.isElementDisplayed(activeWorkflowIcon);
  }

  public boolean isAlertPresent() {
    try {
      browser.switchTo().alert();
      return true;
    } catch (NoAlertPresentException noAlertPresentException) {
      return false;
    }
  }

  /**
   * @return Details for Folders
   */
  public String getFadedDetailsList() 
  
  {
    browser.waitUntilElementVisible(By.xpath("//span[text() = 'No Categories']"));

    ArrayList<String> fadedText = new ArrayList<>();
    for (int i = 0; i < fadedDetails.size(); i++) {
      fadedText.add(fadedDetails.get(i).getText());
    }
    return fadedText.toString();
  }


}
