package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by Mirela Tifui on 12/5/2016.
 */
@PageObject
public class Export extends ShareDialog

{
    @Autowired
    ModelManagerPage modelManagerPage;

    private File downloadDirectory;
    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    private String downloadPath = srcRoot + "testdata";
    private Alert alert;

    public boolean isFileInDirectory(String fileName, String extension)
    {
        downloadDirectory = new File(downloadPath);
        File[] directoryContent = downloadDirectory.listFiles();

        for (File aDirectoryContent : directoryContent)
        {
            if (extension == null)
            {
                if (aDirectoryContent.getName().equals(fileName))
                    return true;
            } else
            {
                if (aDirectoryContent.getName().equals(fileName + extension))
                    return true;
            }
        }

        return false;
    }

    public void checkIfAlertIsPresentAndIfTrueAcceptAlert()
    {
        if (modelManagerPage.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
            browser.waitInSeconds(4);
        }
    }
}

