package org.alfresco.po.share;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class MyFilesPage extends DocumentLibraryPage implements AccessibleByMenuBar
{
    @Autowired
    Toolbar toolbar;

    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public MyFilesPage navigateByMenuBar()
    {
        toolbar.clickMyFiles();
        return (MyFilesPage) renderedPage();
    }
}
