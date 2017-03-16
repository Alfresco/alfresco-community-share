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

    @RenderWebElement
    @FindBy(css = "[id$='default-fileUpload-button-button']")
    private WebElement uploadButton;

    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    public boolean isUploadButtonDisplayed()
    {
        return browser.isElementDisplayed(uploadButton);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyFilesPage navigateByMenuBar()
    {
        toolbar.clickMyFiles();
        return (MyFilesPage) renderedPage();
    }
}
