package org.alfresco.po.share.alfrescoContent.pageCommon;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TableView extends SiteCommon<TableView>
{
    @FindBy (css = "span[class ='removeDefaultView']")
    public WebElement removeDefaultView;
    @FindBy (css = "span[class ='setDefaultView']")
    public WebElement setDefaultView;
    public By tableView = By.cssSelector("div[class ='documents yui-dt alf-table']");
    @FindBy (css = "div[class ='documents yui-dt alf-table']")
    private WebElement tableInTableView;
    @FindBy (css = "th[id*='-th-nodeRef']")
    private WebElement selectedColumnTitle;
    @FindBy (css = "th[id*='-th-status']")
    private WebElement statusColumnTitle;
    @FindBy (css = "th[id*='-th-thumbnail']")
    private WebElement thumbnailColumnTitle;
    @FindBy (css = "th[id*='-th-name']")
    private WebElement nameColumnTitle;
    @FindBy (css = "th[id*='-th-cmtitle']")
    private WebElement titleColumnTitle;
    @FindBy (css = "th[id*='-th-cmdescription']")
    private WebElement descriptionColumnTitle;
    @FindBy (css = "th[id*='-th-cmcreator']")
    private WebElement creatorColumnTitle;
    @FindBy (css = "th[id*='-th-cmcreated']")
    private WebElement createdColumnTitle;
    @FindBy (css = "th[id*='-th-cmmodifier']")
    private WebElement modifierColumnTitle;
    @FindBy (css = "th[id*='-th-modified']")
    private WebElement modifiedColumnTitle;
    @FindBy (css = "th[id*='-th-actions']")
    private WebElement actionsColumnTitle;

    public TableView(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    private WebElement findItemInTableInTableView(String contentName)
    {
        return getBrowser()
            .findElement(By.xpath("//table[contains(@id,'yuievtautoid')]//tbody[@class ='yui-dt-data']//td[contains(@class, 'yui-dt-col-name')]//a[text()='"
                + contentName + "']"));
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isTableViewDisplayed()
    {
        return getBrowser().isElementDisplayed(tableInTableView);
    }

    public String getContentNameTableView(String contentName)
    {
        return findItemInTableInTableView(contentName).getText();
    }

    public boolean isSelectedColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(selectedColumnTitle);
    }

    public boolean isStatusColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(statusColumnTitle);
    }

    public boolean isThumbnailColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(thumbnailColumnTitle);
    }

    public boolean isNameColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(nameColumnTitle);
    }

    public boolean isTitleColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(titleColumnTitle);
    }

    public boolean isDescriptionColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(descriptionColumnTitle);
    }

    public boolean isCreatorColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(creatorColumnTitle);
    }

    public boolean isCreatedColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(createdColumnTitle);
    }

    public boolean isModifierColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(modifierColumnTitle);
    }

    public boolean isModifiedColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(modifiedColumnTitle);
    }

    public boolean isActionsColumnDisplayed()
    {
        return getBrowser().isElementDisplayed(actionsColumnTitle);
    }
}
