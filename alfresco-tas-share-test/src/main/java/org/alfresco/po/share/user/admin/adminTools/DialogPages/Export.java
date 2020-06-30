package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import java.io.File;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/5/2016.
 */
@PageObject
public class Export extends ShareDialog

{
    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    @Autowired
    ModelManagerPage modelManagerPage;
    private File downloadDirectory;
    private String downloadPath = srcRoot + "testdata" + File.separator;
    private Alert alert;

    public boolean isFileInDirectory(String fileName, String extension)
    {
        downloadDirectory = new File(downloadPath);
        File[] directoryContent = downloadDirectory.listFiles();

        String downloadedFile = (extension == null) ? fileName: fileName + extension;
        for (File aDirectoryContent : directoryContent)
        {
            if (aDirectoryContent.getName().equals(downloadedFile))
            {
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

