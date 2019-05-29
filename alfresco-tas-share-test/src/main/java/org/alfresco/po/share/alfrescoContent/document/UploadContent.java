package org.alfresco.po.share.alfrescoContent.document;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.FileInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class UploadContent extends SiteCommon<UploadContent>
{

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @FindBy (css = "button[id$='-fileUpload-button-button']")
    private WebElement uploadButton;

    @FindBy (className = "dnd-file-selection-button")
    private FileInput fileInput;

    // private By uploadFilesToDialog =
    // By.cssSelector("div[id$='_default-dialog_c']");

    private By uploadFilesToDialog = By.id("template_x002e_dnd-upload_x002e_documentlibrary_x0023_default-dialog_h");

    public enum Version
    {
        Minor, Major
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/site/%s/documentlibrary";
    }

    private static File newFile(String fileName, String contents)
    {
        File file = new File(fileName);
        try
        {
            if (!file.exists())
            {
                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                        Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                } else
                {
                    file.createNewFile();
                }
            } else
            {
                // to be written
            }
        } catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }

    public void uploadContent(String filePath, String contentsOfFile)
    {
        // click Upload button
        getBrowser().waitInSeconds(5);
        browser.waitUntilElementClickable(uploadButton);
        uploadButton.click();
        getBrowser().waitInSeconds(5);

        // set the file to upload
        File fileToUpload = newFile(filePath, contentsOfFile);
        try
        {
            fileInput.setFileToUpload(fileToUpload.getCanonicalPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // wait for the file to be visible
        String[] strPaths = filePath.split("\\\\");
        String fileName = strPaths[strPaths.length - 1];
        By selector = By.xpath("//a[contains(., '" + fileName + "')]");
        try
        {
            browser.waitUntilElementIsDisplayedWithRetry(selector);
        } catch (TimeoutException exception)
        {
            exception.printStackTrace();
        }
    }

    public void uploadContent(String filePath)
    {
        uploadContent(filePath, "contents");
    }

    public DocumentLibraryPage updateDocumentVersion(String filePath, String comments, Version versionType)
    {
        if (versionType.equals(Version.Major))
        {
            browser.waitUntilElementClickable(By.cssSelector("input[id$='_default-majorVersion-radioButton']"),
                3).click();
        }
        WebElement commentBox = browser
            .waitUntilElementVisible(By.cssSelector("textarea[id$='_default-description-textarea']"));
        commentBox.clear();
        commentBox.sendKeys(comments);
        File fileToUpload = newFile(filePath, "updated by upload new version");
        try
        {
            fileInput.setFileToUpload(fileToUpload.getCanonicalPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        WebElement submitButton = browser
            .waitUntilElementVisible(By.cssSelector("button[id$='_default-upload-button-button']"));
        submitButton.click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public boolean isUploadFilesToDialogDisplayed()
    {
        return browser.isElementDisplayed(uploadFilesToDialog);
    }
}
