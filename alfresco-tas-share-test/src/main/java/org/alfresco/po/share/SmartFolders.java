package org.alfresco.po.share;

import java.util.List;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SmartFolders extends DocumentLibraryPage
{
    private final By smartFolderIcons = By.xpath("//img [@src='/share/res/components/documentlibrary/images/smart-folder-64.png']");

    public SmartFolders(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    //todo: rename parameter SF
    public boolean areSmartFolderIconsDisplayed(int SF)

    {
        waitInSeconds(3);
        int numberOfSmartFolderIcons = findElements(smartFolderIcons).size();
        return numberOfSmartFolderIcons == SF;

    }

}
