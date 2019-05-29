package org.alfresco.po.share;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@PageObject
public class SmartFolders extends DocumentLibraryPage
{
    @FindBy (xpath = "//img [@src='/share/res/components/documentlibrary/images/smart-folder-64.png']")
    protected List<WebElement> smartFolderIcons;

    public boolean areSmartFolderIconsDisplayed(int SF)

    {
        int numberOfSmartFolderIcons = smartFolderIcons.size();
        return numberOfSmartFolderIcons == SF;

    }

}
