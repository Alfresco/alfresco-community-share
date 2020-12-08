package org.alfresco.po.share.alfrescoContent.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.env.Environment;

public class UploadContent extends SiteCommon<UploadContent>
{
    private Environment env;

    private final By uploadButton = By.cssSelector("button[id$='-fileUpload-button-button']");
    private final By fileInput = By.className("dnd-file-selection-button");
    private final By uploadFilesToDialog = By.id("template_x002e_dnd-upload_x002e_documentlibrary_x0023_default-dialog_h");

    public UploadContent(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)(getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        // click Upload button
        getBrowser().waitUntilElementClickable(uploadButton);
        getBrowser().findElement(uploadButton).click();
        waitUntilNotificationMessageDisappears();

        // set the file to upload
        File fileToUpload = newFile(filePath, contentsOfFile);
        try
        {
            getBrowser().findElement(fileInput).sendKeys(fileToUpload.getAbsolutePath());
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        getBrowser().waitUntilElementDisappears(uploadFilesToDialog);
        // wait for the file to be visible
        String[] strPaths = filePath.split("\\\\");
        String fileName = strPaths[strPaths.length - 1];
        By selector = By.xpath("//a[contains(., '" + fileName + "')]");
        try
        {
            getBrowser().waitUntilElementIsDisplayedWithRetry(selector);
        }
        catch (TimeoutException exception)
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
            ((RemoteWebDriver)(getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        if (versionType.equals(Version.Major))
        {
            getBrowser().waitUntilElementClickable(By.cssSelector("input[id$='_default-majorVersion-radioButton']")).click();
        }
        WebElement commentBox = getBrowser().waitUntilElementVisible(By.cssSelector("textarea[id$='_default-description-textarea']"));
        Utils.clearAndType(commentBox, comments);
        File fileToUpload = newFile(filePath, "updated by upload new version");
        try
        {
            getBrowser().findElement(fileInput).sendKeys(fileToUpload.getAbsolutePath());
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        getBrowser().waitUntilElementVisible(By.cssSelector("button[id$='_default-upload-button-button']")).click();
        return (DocumentLibraryPage) new DocumentLibraryPage(browser).renderedPage();
    }

    public boolean isUploadFilesToDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(uploadFilesToDialog);
    }

    public enum Version
    {
        Minor, Major
    }
}
