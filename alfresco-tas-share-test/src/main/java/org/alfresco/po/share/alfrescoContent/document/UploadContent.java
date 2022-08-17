package org.alfresco.po.share.alfrescoContent.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UploadContent extends SiteCommon<UploadContent>
{
    private final By uploadButton = By.cssSelector("button[id$='-fileUpload-button-button']");
    private final By fileInput = By.className("dnd-file-selection-button");
    private final By uploadFilesToDialog = By.id("template_x002e_dnd-upload_x002e_documentlibrary_x0023_default-dialog_h");

    public UploadContent(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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
        // click Upload button
        clickElement(uploadButton);
        waitUntilNotificationMessageDisappears();

        // set the file to upload
        File fileToUpload = newFile(filePath, contentsOfFile);
        try
        {
            findElement(fileInput).sendKeys(fileToUpload.getAbsolutePath());
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        waitUntilElementDisappears(uploadFilesToDialog);
        // wait for the file to be visible
        String[] strPaths = filePath.split("\\\\");
        String fileName = strPaths[strPaths.length - 1];
        By selector = By.xpath("//a[contains(., '" + fileName + "')]");
        try
        {
            waitUntilElementIsDisplayedWithRetry(selector);
        }
        catch (TimeoutException exception)
        {
            exception.printStackTrace();
        }
    }

    public void uploadContent(String filePath)
    {
        waitInSeconds(3);
        uploadContent(filePath, "contents");
    }

    public DocumentLibraryPage updateDocumentVersion(String filePath, String comments, Version versionType)
    {
        if (versionType.equals(Version.Major))
        {
            clickElement(By.cssSelector("input[id$='_default-majorVersion-radioButton']"));
        }
        WebElement commentBox = waitUntilElementIsVisible(By.cssSelector("textarea[id$='_default-description-textarea']"));
        Utils.clearAndType(commentBox, comments);
        File fileToUpload = newFile(filePath, "updated by upload new version");
        try
        {
            findElement(fileInput).sendKeys(fileToUpload.getAbsolutePath());
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        waitUntilElementIsVisible(By.cssSelector("button[id$='_default-upload-button-button']"));
        clickElement(By.cssSelector("button[id$='_default-upload-button-button']"));
        return new DocumentLibraryPage(webDriver);
    }

    public boolean isUploadFilesToDialogDisplayed()
    {
        return isElementDisplayed(uploadFilesToDialog);
    }

    // todo: move into separate enum
    public enum Version
    {
        Minor, Major
    }
}
