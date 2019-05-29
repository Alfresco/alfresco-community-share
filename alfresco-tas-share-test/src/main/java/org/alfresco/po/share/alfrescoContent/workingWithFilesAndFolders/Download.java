package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by Mirela Tifui on 11/25/2016.
 */
@PageObject
public class Download extends SiteCommon<Download>
{
    @Autowired
    DocumentCommon documentCommon;

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

    /**
     * If alert is displayed, accept it
     */
    public void acceptAlertIfDisplayed()
    {
        if (documentCommon.isAlertPresent())
        {
            alert = browser.switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
            browser.waitInSeconds(4);
        }
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }
}
