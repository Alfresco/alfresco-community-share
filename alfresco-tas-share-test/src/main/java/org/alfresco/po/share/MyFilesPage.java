package org.alfresco.po.share;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class MyFilesPage extends DocumentLibraryPage
{
    @Autowired
    Toolbar toolbar;

    @RenderWebElement
    @FindBy(css = "[id$='default-fileUpload-button-button']")
    private WebElement uploadButton;

    private By documentLibraryItemsList = By.cssSelector("[class*=data] tr");

    private WebElement selectContent(String contentName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(documentLibraryItemsList, 6);
        List<WebElement> itemsList = browser.findElements(documentLibraryItemsList);
        return browser.findFirstElementWithValue(itemsList, contentName);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/context/mine/myfiles";
    }

    public boolean isUploadButtonDisplayed()
    {
        return browser.isElementDisplayed(uploadButton);
    }
    
    
    public MyFilesPage navigateByMenuBarToMyFiles()
    
    {
        toolbar.clickMyFiles();
        return (MyFilesPage) renderedPage();
    }
    
    
}
