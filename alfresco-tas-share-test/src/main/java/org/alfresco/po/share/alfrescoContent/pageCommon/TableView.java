package org.alfresco.po.share.alfrescoContent.pageCommon;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class TableView extends SiteCommon<TableView>
{
    @FindBy(css = "div[class ='documents yui-dt alf-table']")
    private WebElement tableInTableView;
        
    @FindBy (css = "span[class ='removeDefaultView']")
    public WebElement removeDefaultView;
    
    @FindBy (css = "span[class ='setDefaultView']")
    public WebElement setDefaultView;
    
    @FindBy(css = "th[id*='-th-nodeRef']")
    private WebElement selectedColumnTitle;

    @FindBy(css = "th[id*='-th-status']")
    private WebElement statusColumnTitle;

    @FindBy(css = "th[id*='-th-thumbnail']")
    private WebElement thumbnailColumnTitle;

    @FindBy(css = "th[id*='-th-name']")
    private WebElement nameColumnTitle;

    @FindBy(css = "th[id*='-th-cmtitle']")
    private WebElement titleColumnTitle;

    @FindBy(css = "th[id*='-th-cmdescription']")
    private WebElement descriptionColumnTitle;

    @FindBy(css = "th[id*='-th-cmcreator']")
    private WebElement creatorColumnTitle;

    @FindBy(css = "th[id*='-th-cmcreated']")
    private WebElement createdColumnTitle;

    @FindBy(css = "th[id*='-th-cmmodifier']")
    private WebElement modifierColumnTitle;

    @FindBy(css = "th[id*='-th-modified']")
    private WebElement modifiedColumnTitle;

    @FindBy(css = "th[id*='-th-actions']")
    private WebElement actionsColumnTitle;

    public By tableView = By.cssSelector("div[class ='documents yui-dt alf-table']");
    
    private WebElement findItemInTableInTableView(String contentName)
    {
        return browser
                .findElement(By.xpath("//table[contains(@id,'yuievtautoid')]//tbody[@class ='yui-dt-data']//td[contains(@class, 'yui-dt-col-name')]//a[text()='"
                        + contentName + "']"));
    }

    @Override
    public String getRelativePath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method to check if the table view has been applied and content is displayed in table view
     * 
     * @return
     */
    public boolean isTableViewDisplayed()
    {
        return tableInTableView.isDisplayed();
    }

    /**
     * Method to check that the content is displayed while table view is selected
     * 
     * @param contentName
     * @return
     */
    public String getContentNameTableView(String contentName)
    {
        return findItemInTableInTableView(contentName).getText();
    }

    /**
     * Method to check that the Selected column is displayed
     * 
     * @return
     */
    public boolean isSelectedColumnDisplayed()
    {
        return selectedColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Status column is displayed
     * 
     * @return
     */
    public boolean isStatusColumnDisplayed()
    {
        return statusColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Thumbnail column is displayed
     * 
     * @return
     */
    public boolean isThumbnailColumnDisplayed()
    {
        return thumbnailColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Name column is displayed
     * 
     * @return
     */
    public boolean isNameColumnDisplayed()
    {
        return nameColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Title column is displayed
     * 
     * @return
     */
    public boolean isTitleColumnDisplayed()
    {
        return titleColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Description column is displayed
     * 
     * @return
     */
    public boolean isDescriptionColumnDisplayed()
    {
        return descriptionColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Creator column is displayed
     * 
     * @return
     */
    public boolean isCreatorColumnDisplayed()
    {
        return creatorColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Created column is displayed
     * 
     * @return
     */
    public boolean isCreatedColumnDisplayed()
    {
        return createdColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Modifier column is displayed
     * 
     * @return
     */
    public boolean isModifierColumnDisplayed()
    {
        return modifierColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Modified column is displayed
     * 
     * @return
     */
    public boolean isModifiedColumnDisplayed()
    {
        return modifiedColumnTitle.isDisplayed();
    }

    /**
     * Method to check that the Actions column is displayed
     * 
     * @return
     */
    public boolean isActionsColumnDisplayed()
    {
        return actionsColumnTitle.isDisplayed();
    }
}
