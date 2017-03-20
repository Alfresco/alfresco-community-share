package org.alfresco.po.share;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * @author bogdan.bocancea
 */
@PageObject
public class DashboardCustomizationImpl extends HtmlPage implements DashboardCustomization
{
    private final int columns = 4;
    private final int maxDashletsInColumn = 5;

    public enum Layout
    {
        ONE_COLUMN("One column"), 
        TWO_COLUMNS_WIDE_LEFT("Two columns: wide left, narrow right"), 
        TWO_COLUMNS_WIDE_RIGHT("Two columns: narrow left, wide right"), 
        THREE_COLUMNS("Three columns: wide centre"), 
        FOUR_COLUMS("Four columns");
        public final String description;
        Layout(String description)
        {
            this.description = description;
        }
    }

    @FindBy(css = ".sub-title")
    private TextBlock subTitle;
    
    @FindBy(css = ".customise-layout")
    private WebElement layoutSection;
    
    @FindBy(css = ".customise-dashlets")
    private WebElement dashletsSection;

    @FindBy(css = "span[id$='_default-currentLayoutDescription-span']")
    private TextBlock currentLayout;

    @FindBy(css = "button[id$='_default-change-button-button']")
    private WebElement changeLayout;

    @FindBy(css = "button[id$='dashboard-1-column-button']")
    private Button oneColumn;

    @FindBy(css = "button[id$='2-columns-wide-left-button']")
    private Button twoColumn_left;
    
    @FindBy(css = "button[id$='2-columns-wide-right-button']")
    private Button twoColumn_right;

    @FindBy(css = "button[id$='3-columns-button']")
    private Button threeColumn;

    @FindBy(css = "button[id$='dashboard-4-columns-button']")
    private Button fourColumn;

    @FindBy(css = ".layouts .text")
    private TextBlock newLayoutInstructions;

    @FindBy(css = ".instructions .text")
    private TextBlock dashletsInstructions;

    @FindBy(css = ".layouts .buttons button[id$='default-cancel-button-button']")
    private Button cancelNewLayout;

    @FindBy(css = "button[id$='save-button-button']")
    private Button okButton;

    @FindBy(css = ".customise-dashlets .buttons span[id$='default-cancel-button']")
    private Button cancelButton;

    @FindBy(css = "button[id$='addDashlets-button-button']")
    private WebElement addDashlets;

    private String availableDashlet = "//li[@class='availableDashlet dnd-draggable']/span[text()='%s']/..";
    private String dashletsInColumn = "ul[id$='column-ul-%d'] li > span";
    private String targetColumn = "ul[id$='default-column-ul-%d']";
    private String addedDashlet = "//ul[contains(@id,'default-column-ul-%d')]/li//span[text()='%s']/following-sibling::div";

    @FindAll(@FindBy(css = "ul.availableList>li.availableDashlet>span"))
    List<WebElement> availableDashlets;

    @FindBy(css = "div[id$='default-wrapper-div']")
    private WebElement availableColumns;
    
    @RenderWebElement
    @FindBy(css = ".trashcan")
    private WebElement trashcan;
    
    @FindBy(css = "li[id$='layout-li-dashboard-1-column']")
    private WebElement oneColumnLayout;
    @FindBy(css = "li[id$='layout-li-dashboard-3-columns']")
    private WebElement threeColumnsLayout;
    @FindBy(css = "li[id$='layout-li-dashboard-4-columns']")
    private WebElement fourColumnsLayout;
    @FindBy(css = "li[id$='layout-li-dashboard-2-columns-wide-right']")
    private WebElement twoColumnsWideRightLayout;
    @FindBy(css = "li[id$='layout-li-dashboard-2-columns-wide-left']")
    private WebElement twoColumnsWideLeftLayout;
    
    @FindBy(css = ".closeLink")
    private WebElement closeAddDashlets;
    
    @FindBy(css = ".availableList")
    private WebElement availableDashletList;

    private By selectNewLayout = By.cssSelector("div[id$='_default-layouts-div']");

    public String getSubTitle()
    {
        return subTitle.getText();
    }

    /**
     * Get current layout text
     * 
     * @return String current layout
     */
    public String getCurrentLayout()
    {
        return currentLayout.getText();
    }

    /**
     * Click Change Layout button
     */
    public void clickChangeLayout()
    {
        changeLayout.click();
        browser.waitUntilElementVisible(selectNewLayout);
    }

    /**
     * Get the instrction text from Select new layout
     * 
     * @return String instructions
     */
    public String getNewLayoutInstructions()
    {
        return newLayoutInstructions.getText();
    }

    /**
     * Get the instruction text for dashlets
     * 
     * @return String instrctions
     */
    public String getDashletsInstructions()
    {
        return dashletsInstructions.getText();
    }

    /**
     * Select dashboard layout
     * 
     * @param layout
     */
    public void selectLayout(Layout layout)
    {
        switch (layout)
        {
            case ONE_COLUMN:
                oneColumn.click();
                break;
            case TWO_COLUMNS_WIDE_LEFT:
                twoColumn_left.click();
                break;
            case TWO_COLUMNS_WIDE_RIGHT:
                twoColumn_right.click();
                break;
            case THREE_COLUMNS:
                threeColumn.click();
                break;
            case FOUR_COLUMS:
                fourColumn.click();
                break;
            default:
                break;
        }
    }

    /**
     * Cancel New Layout selection
     */
    public void clickCancelNewLayout()
    {
        cancelNewLayout.click();
        browser.waitUntilElementDisappears(selectNewLayout, 30);
    }

    /**
     * Click add dashlet button
     */
    public void clickAddDashlet()
    {
        addDashlets.click();
    }

    /**
     * Click OK button
     */
    public void clickOk()
    {
        okButton.click();
        browser.waitInSeconds(3);
    }
    
    /**
     * Add a dashlet to user or site dashboard
     * @param dashlet to be added
     * @param columnNumber int column number
     */
    public void addDashlet(Dashlets dashlet, int columnNumber)
    {
        if (columnNumber < 1 || columnNumber > columns)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }
        int noOfColumns = 0;
        if(addDashlets.isDisplayed())
        {
            clickAddDashlet();
        }
        browser.waitUntilElementVisible(By.cssSelector(".availableList"));
        String strDashlet = dashlet.getDashletName();
        String dashletXpath = String.format(availableDashlet, strDashlet);
        WebElement webDashlet;
        try
        {
            webDashlet = browser.waitUntilElementClickable(By.xpath(dashletXpath), 10);
        }
        catch(TimeoutException te)
        {
            throw new PageOperationException(strDashlet + " not found in Add Dashlets list");
        }
        browser.scroolToElement(webDashlet);
        webDashlet.click();
        String columns = availableColumns.getAttribute("class");
        noOfColumns = Integer.valueOf(columns.substring(columns.length() - 1));
        List<WebElement> existingDashletsInColumn = new ArrayList<WebElement>();
        if (columnNumber <= noOfColumns)
        {
            try
            {
                existingDashletsInColumn = browser.findElements(By.cssSelector(String.format(dashletsInColumn, columnNumber)));
            }
            catch(TimeoutException te)
            {
                
            }
            if (existingDashletsInColumn.size() < maxDashletsInColumn)
            {
                WebElement target = browser.waitUntilElementVisible(By.cssSelector(String.format(targetColumn, columnNumber)));
                browser.dragAndDrop(webDashlet, target);
                if(!isDashletAddedInColumn(dashlet, columnNumber))
                {
                    browser.dragAndDrop(webDashlet, target);
                }
            }
            else
            {
                throw new PageOperationException("Exceeded the number of dashlets in given column.");
            }
        }
        else
        {
            throw new PageOperationException("Expected column does not exist in available columns list.");
        }
    }
    
    public List<String> getDashletsFromColumn(int column)
    {
        List<String> dashlets = new ArrayList<String>();
        List<WebElement> existingDashletsInColumn = browser.findElements(By.cssSelector(String.format(dashletsInColumn, column)));
        for(WebElement dashlet : existingDashletsInColumn)
        {
            dashlets.add(dashlet.getText());
        }
        return dashlets;
    }
    
    public List<String> getAvailableDashlets()
    {
        List<String> dashlets = new ArrayList<String>();
        for(WebElement dashlet : availableDashlets)
        {
            dashlets.add(dashlet.getText());
        }
        return dashlets;
    }
    
    private WebElement getDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        WebElement dash = null;
        try
        {
            dash = browser.findFirstElementWithValue(By.cssSelector(String.format(dashletsInColumn, columnNumber)),
                    dashlet.getDashletName());
        }
        catch(TimeoutException te)
        {
        }
        return dash;
    }
    
    /**
     * Verify if a dashlet is added in a column
     * @param dashlet that was added
     * @param columnNumber
     * @return true if found
     */
    public boolean isDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        return getDashletAddedInColumn(dashlet, columnNumber) != null;
    }
    
    /**
     * Remove a specific dashlet from a column
     * @param dashlet to be removed
     * @param columnNumber
     */
    public void removeDashlet(Dashlets dashlet, int columnNumber)
    {
        WebElement dash = browser.waitUntilElementVisible(
				By.xpath(String.format(addedDashlet, columnNumber, dashlet.getDashletName())));
        if(dash != null)
        {
            dash.click();
            browser.dragAndDrop(dash, trashcan);
        }
        else
        {
            throw new PageOperationException(dashlet.getDashletName() + " not found in column " + columnNumber);
        }
    }
    
    /**
     * Verify if one column layout is displayed
     * @return true if displayed
     */
    public boolean isOneColumnLayoutDisplayed()
    {
        return browser.isElementDisplayed(oneColumnLayout);
    }
    
    /**
     * Verify if three columns layout is displayed
     * @return true if displayed
     */
    public boolean isThreeColumnsLayoutDisplayed()
    {
        return browser.isElementDisplayed(threeColumnsLayout);
    }
    
    /**
     * Verify if four columns layout is displayed
     * @return true if displayed
     */
    public boolean isFourColumnsLayoutDisplayed()
    {
        return browser.isElementDisplayed(fourColumnsLayout);
    }

    /**
     * Verify if two columns wide right layout is displayed
     * @return true if displayed
     */
    public boolean isTwoColumnsLayoutWideRightDisplayed()
    {
        return browser.isElementDisplayed(twoColumnsWideRightLayout);
    }

    /**
     * Verify if two columns wide left layout is displayed
     * @return true if displayed
     */
    public boolean isTwoColumnsLayoutWideLeftDisplayed()
    {
        return browser.isElementDisplayed(twoColumnsWideLeftLayout);
    }

    public boolean isChangeLayoutButtonDisplayed()
    {
        return browser.isElementDisplayed(changeLayout);
    }

    public boolean isChangeLayoutSectionDisplayed()
    {
        return browser.isElementDisplayed(layoutSection);
    }

    public boolean isDashletSectionDisplayed()
    {
        return browser.isElementDisplayed(dashletsSection);
    }

    public boolean isAddDashletButtonDisplayed()
    {
        return browser.isElementDisplayed(addDashlets);
    }

    public void clickCloseAvailabeDashlets()
    {
        closeAddDashlets.click();
    }

    public boolean isAvailableDashletListDisplayed()
    {
        return browser.isElementDisplayed(availableDashletList);
    }
    
    public void moveAddedDashletInColumn(Dashlets dashlet, int fromColumn, int toColumn)
    {
        WebElement dashletToMove;
        try
        {
            dashletToMove = browser.waitUntilElementVisible(By.xpath(String.format(addedDashlet, fromColumn, dashlet.getDashletName())));
        }
        catch(NoSuchElementException ns)
        {
            throw new PageOperationException(dashlet.getDashletName() + " not found in column " +  fromColumn);
        }
        dashletToMove.click();
        if(browser.findElements(By.cssSelector(String.format(dashletsInColumn, toColumn))).size() > 5)
        {
            throw new PageOperationException("Exceeded the number of dashlets in given column.");
        }
        WebElement target = browser.findElement(By.cssSelector(String.format(targetColumn, toColumn)));
        browser.dragAndDrop(dashletToMove, target);
        if(!isDashletAddedInColumn(dashlet, toColumn))
        {
            browser.dragAndDrop(dashletToMove, target);
        }
    }
    
    public void reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column)
    {
        WebElement dashToMove = null;
        WebElement dashToReplace = null;
        try
        {
            dashToMove = browser.waitUntilElementVisible(By.xpath(String.format(addedDashlet, column, dashletToMove.getDashletName())));
        }
        catch(NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToMove.getDashletName() + " not found in column " + column);
        }
        try
        {
            dashToReplace = browser.waitUntilElementVisible(By.xpath(String.format(addedDashlet, column, dashletToReplace.getDashletName())));
        }
        catch(NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToReplace.getDashletName() + " not found in column " +  column);
        }
        dashToMove.click();
        browser.dragAndDrop(dashToMove, dashToReplace);
    }

}
