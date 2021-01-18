package org.alfresco.po.share;

import java.util.List;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SmartFolders extends DocumentLibraryPage
{
    @FindBy (xpath = "//img [@src='/share/res/components/documentlibrary/images/smart-folder-64.png']")
    protected List<WebElement> smartFolderIcons;

    public SmartFolders(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    //todo: rename parameter SF
    public boolean areSmartFolderIconsDisplayed(int SF)

    {
        int numberOfSmartFolderIcons = smartFolderIcons.size();
        return numberOfSmartFolderIcons == SF;

    }

}
