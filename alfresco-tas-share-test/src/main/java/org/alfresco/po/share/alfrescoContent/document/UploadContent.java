package org.alfresco.po.share.alfrescoContent.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import ru.yandex.qatools.htmlelements.element.FileInput;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class UploadContent extends SiteCommon<UploadContent>
{

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    private Environment env;

    @FindBy (css = "button[id$='-fileUpload-button-button']")
    private WebElement uploadButton;

    @FindBy (className = "dnd-file-selection-button")
    private WebElement fileInput;

    // private By uploadFilesToDialog =
    // By.cssSelector("div[id$='_default-dialog_c']");

    private By uploadFilesToDialog = By.id("template_x002e_dnd-upload_x002e_documentlibrary_x0023_default-dialog_h");

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
                // TODO to be written
            }
        } catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/site/%s/documentlibrary";
    }

    public void uploadContent(String filePath, String contentsOfFile)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)(documentLibraryPage.getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        // click Upload button
        browser.waitUntilElementClickable(uploadButton);
        uploadButton.click();
        waitUntilMessageDisappears();

        // set the file to upload
        File fileToUpload = newFile(filePath, contentsOfFile);
        try
        {
            fileInput.sendKeys(fileToUpload.getAbsolutePath());
        } catch (IllegalArgumentException e)
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
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)(documentLibraryPage.getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
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
            fileInput.sendKeys(fileToUpload.getAbsolutePath());
        } catch (IllegalArgumentException e)
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

    public enum Version
    {
        Minor, Major
    }
}
