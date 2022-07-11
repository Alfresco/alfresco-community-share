package org.alfresco.po.share.alfrescoContent.pageCommon;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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

    public TableView(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement findItemInTableInTableView(String contentName)
    {
        return findElement(By.xpath("//table[contains(@id,'yuievtautoid')]//tbody[@class ='yui-dt-data']//td[contains(@class, 'yui-dt-col-name')]//a[text()='"
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
        return isElementDisplayed(tableInTableView);
    }

    public String getContentNameTableView(String contentName)
    {
        return findItemInTableInTableView(contentName).getText();
    }

    public boolean isSelectedColumnDisplayed()
    {
        return isElementDisplayed(selectedColumnTitle);
    }

    public boolean isStatusColumnDisplayed()
    {
        return isElementDisplayed(statusColumnTitle);
    }

    public boolean isThumbnailColumnDisplayed()
    {
        return isElementDisplayed(thumbnailColumnTitle);
    }

    public boolean isNameColumnDisplayed()
    {
        return isElementDisplayed(nameColumnTitle);
    }

    public boolean isTitleColumnDisplayed()
    {
        return isElementDisplayed(titleColumnTitle);
    }

    public boolean isDescriptionColumnDisplayed()
    {
        return isElementDisplayed(descriptionColumnTitle);
    }

    public boolean isCreatorColumnDisplayed()
    {
        return isElementDisplayed(creatorColumnTitle);
    }

    public boolean isCreatedColumnDisplayed()
    {
        return isElementDisplayed(createdColumnTitle);
    }

    public boolean isModifierColumnDisplayed()
    {
        return isElementDisplayed(modifierColumnTitle);
    }

    public boolean isModifiedColumnDisplayed()
    {
        return isElementDisplayed(modifiedColumnTitle);
    }

    public boolean isActionsColumnDisplayed()
    {
        return isElementDisplayed(actionsColumnTitle);
    }
}
