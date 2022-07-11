package org.alfresco.po;

import org.alfresco.po.share.alfrescoContent.applyingRulesToFolders.PerformActionRulePage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * @author iulia.burca
 */
@PageObject
public class DocumentTransformationEnginePage extends HtmlPage {

    @RenderWebElement
    @FindBy(className = "intro")
    WebElement intro;

    public By rowsPerPage = By.cssSelector("button[id*='rowsPerPage'] em");
    public By row = By.cssSelector("#transformation-table tbody[class*='data'] tr");
    public String fileNameColumn = "#transformation-table tbody[class*='data'] tr[id='yui-rec%s'] [class*='col-filename'] div";
    public String toTransformationColumn = "#transformation-table tbody[class*='data'] tr[id='yui-rec%s'] td:nth-of-type(7) img";
    public String userNameColumn = "#transformation-table tbody[class*='data'] tr[id='yui-rec%s'] [class*='col-user'] div";

    /**
     * This method verifies if the transformation of a document (performed through a rule) in Alfresco Share has been made through DTE (Document Transformation Server).
     * If DTE was used, the transformed file must appear in DTE Server page -> History tab -> Transformation Table.
     * The method takes each row of the table which contains the list of transformed files (images or document) and it's checking if on the same row is present: the name of the uploaded file that will be transformed, the target mimetype and the name of the user that has tun the test.
     *
     * @param uploadedFile           the uploaded file that has to be transformed
     * @param mimetypeTransformation the target mimetype mentioned in the rule; the <value> tag of the selected rule and the mimetype of the transformed type is the same
     * @param username               the username of the user that has run the test; this parameter is needed to eliminate the possibility of finding a transformed file with the same name but performed by another user
     * @param numberOfRowsPerPage    the number of all the rows from the table that are displayed; needed to know when to stop searching the wanted row
     * @return <true> if all the three parameters are found with expected values on the same row
     * <false> if at least one of all the three parameters is not found with expected value on the same row
     * The method will run until the expected row is found.
     */
    public boolean searchTransformation(WebDriver driver, String uploadedFile, PerformActionRulePage.Mimetype mimetypeTransformation, String username){

        boolean isFound = false;
        Integer numberOfRowsPerPage = Integer.parseInt(driver.findElement(rowsPerPage).getText());

        for (int i = 0; i < numberOfRowsPerPage; i++) {

            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(String.format(fileNameColumn, i))));

            if (driver.findElement(By.cssSelector(String.format(fileNameColumn, i))).getText().equals(uploadedFile)
                && driver.findElement(By.cssSelector(String.format(toTransformationColumn, i))).getAttribute("title").equals(mimetypeTransformation.getValue())
                && driver.findElement(By.cssSelector(String.format(userNameColumn, i))).getText().equals(username)) {
                isFound = true;
                break;
            }
        }
        return isFound;
    }
}