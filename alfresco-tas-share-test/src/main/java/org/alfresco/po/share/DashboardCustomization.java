package org.alfresco.po.share;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * @author bogdan.bocancea
 */
public abstract class DashboardCustomization<T> extends SharePage<DashboardCustomization<T>>
{
    private final int columns = 4;
    private final int maxDashletsInColumn = 5;

    @FindAll (@FindBy (css = "ul.availableList>li.availableDashlet>span"))
    List<WebElement> availableDashlets;

    @FindBy (css = ".sub-title")
    private TextBlock subTitle;

    @FindBy (css = ".customise-layout")
    private WebElement layoutSection;

    @FindBy (css = ".customise-dashlets")
    private WebElement dashletsSection;

    @FindBy (css = "span[id$='_default-currentLayoutDescription-span']")
    private TextBlock currentLayout;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-change-button-button']")
    private WebElement changeLayout;

    @FindBy (css = "button[id$='dashboard-1-column-button']")
    private Button oneColumn;

    @FindBy (css = "button[id$='2-columns-wide-left-button']")
    private Button twoColumn_left;

    @FindBy (css = "button[id$='2-columns-wide-right-button']")
    private Button twoColumn_right;

    @FindBy (css = "button[id$='3-columns-button']")
    private Button threeColumn;

    @FindBy (css = "button[id$='dashboard-4-columns-button']")
    private Button fourColumn;

    @FindBy (css = ".layouts .text")
    private TextBlock newLayoutInstructions;

    @FindBy (css = ".instructions .text")
    private TextBlock dashletsInstructions;

    @FindBy (css = ".layouts .buttons button[id$='default-cancel-button-button']")
    private Button cancelNewLayout;

    @FindBy (css = "button[id$='save-button-button']")
    private Button okButton;

    @FindBy (css = ".customise-dashlets .buttons span[id$='default-cancel-button']")
    private Button cancelButton;

    @FindBy (css = "button[id$='addDashlets-button-button']")
    private WebElement addDashlets;

    private String availableDashlet = "li[class='availableDashlet dnd-draggable'] div[title^='%s']";
    private String dashletsInColumn = "ul[id$='column-ul-%d'] li > span";
    private String targetColumn = "ul[id$='default-column-ul-%d']";
    private String addedDashlet = "//ul[contains(@id,'default-column-ul-%d')]/li//span[text()='%s']/..";

    @FindBy (css = "div[id$='default-wrapper-div']")
    private WebElement availableColumns;

    @RenderWebElement
    @FindBy (css = ".trashcan")
    private WebElement trashcan;

    @FindBy (css = "li[id$='layout-li-dashboard-1-column']")
    private WebElement oneColumnLayout;

    @FindBy (css = "li[id$='layout-li-dashboard-3-columns']")
    private WebElement threeColumnsLayout;

    @FindBy (css = "li[id$='layout-li-dashboard-4-columns']")
    private WebElement fourColumnsLayout;

    @FindBy (css = "li[id$='layout-li-dashboard-2-columns-wide-right']")
    private WebElement twoColumnsWideRightLayout;

    @FindBy (css = "li[id$='layout-li-dashboard-2-columns-wide-left']")
    private WebElement twoColumnsWideLeftLayout;

    @FindBy (css = ".closeLink")
    private WebElement closeAddDashlets;

    @FindBy (css = ".availableList")
    private WebElement availableDashletList;

    private By selectNewLayout = By.cssSelector("div[id$='_default-layouts-div']");
    private String dashletInAddedColumn = "ul[id$='default-column-ul-%s'] > li:nth-child(%s)";

    public enum Layout
    {
        ONE_COLUMN("One column"),
        TWO_COLUMNS_WIDE_LEFT("Two columns: wide left, narrow right"),
        TWO_COLUMNS_WIDE_RIGHT("Two columns: narrow left, wide right"),
        THREE_COLUMNS("Three columns: wide centre"),
        FOUR_COLUMNS("Four columns");
        public final String description;

        Layout(String description)
        {
            this.description = description;
        }
    }

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

    public T assertCurrentLayoutIs(Layout layout)
    {
        String layoutLabel = "";
        switch (layout)
        {
            case ONE_COLUMN:
                layoutLabel = language.translate("dashboardCustomization.layout.oneColumn");
                break;
            case TWO_COLUMNS_WIDE_LEFT:
                layoutLabel = language.translate("dashboardCustomization.layout.twoColumnsWideLeft");
                break;
            case TWO_COLUMNS_WIDE_RIGHT:
                layoutLabel = language.translate("dashboardCustomization.layout.twoColumnsWideRight");
                break;
            case THREE_COLUMNS:
                layoutLabel = language.translate("dashboardCustomization.layout.threeColumns");
                break;
            case FOUR_COLUMNS:
                layoutLabel = language.translate("dashboardCustomization.layout.fourColumns");
                break;
            default:
                break;
        }
        LOG.info(String.format("Assert current layout is: %s", layoutLabel));
        Assert.assertEquals(currentLayout.getText(), layoutLabel, "Layout label is correct");
        return (T) this;
    }

    /**
     * Click Change Layout button
     */
    public T clickChangeLayout()
    {
        LOG.info("Click Change Layout");
        changeLayout.click();
        browser.waitUntilElementVisible(selectNewLayout);
        return (T) this;
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
    public T selectLayout(Layout layout)
    {
        LOG.info(String.format("Select layout %s", layout.description));
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
            case FOUR_COLUMNS:
                fourColumn.click();
                break;
            default:
                break;
        }
        return (T) this;
    }

    /**
     * Cancel New Layout selection
     */
    public T clickCancelNewLayout()
    {
        cancelNewLayout.click();
        browser.waitUntilElementDisappears(selectNewLayout, 30);
        return (T) this;
    }

    /**
     * Click add dashlet button
     */
    public T clickAddDashlet()
    {
        LOG.info("Click Add Dashlet");
        addDashlets.click();
        browser.waitUntilElementVisible(availableDashletList);
        return (T) this;
    }

    /**
     * Click OK button
     */
    public void clickOk()
    {
        LOG.info("Click OK");
        getBrowser().waitUntilElementClickable(okButton).click();
        waitUntilNotificationMessageDisappears();
    }

    /**
     * Add a dashlet to user or site dashboard
     *
     * @param dashlet      to be added
     * @param columnNumber int column number
     */
    public T addDashlet(Dashlets dashlet, int columnNumber)
    {
        if (columnNumber < 1 || columnNumber > columns)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }
        if (addDashlets.isDisplayed())
        {
            clickAddDashlet();
        }
        String strDashlet = dashlet.getDashletName();
        String dashletXpath = String.format(availableDashlet, strDashlet);
        WebElement webDashlet;
        try
        {
            webDashlet = browser.waitUntilElementClickable(By.cssSelector(dashletXpath), 10);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException(strDashlet + " not found in Add Dashlets list");
        }
        browser.scrollToElement(webDashlet);
        webDashlet.click();
        String columns = availableColumns.getAttribute("class");
        int noOfColumns = Integer.valueOf(columns.substring(columns.length() - 1));
        List<WebElement> existingDashletsInColumn = new ArrayList<>();
        if (columnNumber <= noOfColumns)
        {
            try
            {
                existingDashletsInColumn = browser.findElements(By.cssSelector(String.format(dashletsInColumn, columnNumber)));
            }
            catch (TimeoutException te)
            {

            }
            if (existingDashletsInColumn.size() < maxDashletsInColumn)
            {
                WebElement target = browser.waitUntilElementVisible(
                    By.cssSelector(String.format(targetColumn, columnNumber)));
                if(!dashlet.getDashletName().equals(Dashlets.CONTENT_I_AM_EDITING.getDashletName()))
                {
                    ((JavascriptExecutor) getBrowser()).executeScript("window.scrollBy(0,500)");
                }
                try
                {
                    browser.dragAndDrop(webDashlet, target);
                }
                catch (MoveTargetOutOfBoundsException e)
                {
                    LOG.error(String.format("Failed to add dashlet %s. Will retry...", dashlet.getDashletName()));
                }
                retryAddDashlet(dashlet, webDashlet, target, columnNumber);
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
        return (T) this;
    }

    public void retryAddDashlet(Dashlets dashlet, WebElement webDashlet, WebElement target, int columnNr)
    {
        int i = 0;
        int retry = 5;
        boolean added = isDashletAddedInColumn(dashlet, columnNr);
        while (i < retry && !added)
        {
            LOG.error(String.format("Retry add dashlet - %s", i));
            dragAndDropDashlet(webDashlet, target);
            added = isDashletAddedInColumn(dashlet, columnNr);
            i++;
        }
    }

    public List<String> getDashletsFromColumn(int column)
    {
        List<String> dashlets = new ArrayList<>();
        List<WebElement> existingDashletsInColumn = browser
            .findElements(By.cssSelector(String.format(dashletsInColumn, column)));
        for (WebElement dashlet : existingDashletsInColumn)
        {
            dashlets.add(dashlet.getText());
        }
        return dashlets;
    }

    public List<String> getAvailableDashlets()
    {
        List<String> dashlets = new ArrayList<>();
        for (WebElement dashlet : availableDashlets)
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
        catch (TimeoutException te)
        {
        }
        return dash;
    }

    /**
     * Verify if a dashlet is added in a column
     *
     * @param dashlet      that was added
     * @param columnNumber
     * @return true if found
     */
    public boolean isDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        return getDashletAddedInColumn(dashlet, columnNumber) != null;
    }

    public T assertDashletIsAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        LOG.info(String.format("Assert dashlet %s is added in column", dashlet.getDashletName(), columnNumber));
        Assert.assertTrue(isDashletAddedInColumn(dashlet, columnNumber), String.format(
            "Dashlet %s is added in column %s", dashlet.getDashletName(), columnNumber));
        return (T) this;
    }

    public T assertDashletIsNotAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        LOG.info(String.format("Assert dashlet %s is NOT added in column", dashlet.getDashletName(), columnNumber));
        Assert.assertFalse(isDashletAddedInColumn(dashlet, columnNumber), String.format(
            "Dashlet %s is added in column %s", dashlet.getDashletName(), columnNumber));
        return (T) this;
    }

    public boolean isDashletInPositionInColumn(Dashlets dashlet, int columnNumber, int columnPosition)
    {
        WebElement dashletElement = browser.findElement(By.cssSelector
            (String.format(dashletInAddedColumn, columnNumber, columnPosition)));
        if(dashletElement.getText().equals(dashlet.getDashletName()))
        {
            return true;
        }
        return false;
    }

    /**
     * Remove a specific dashlet from a column
     *
     * @param dashlet      to be removed
     * @param columnNumber
     */
    public T removeDashlet(Dashlets dashlet, int columnNumber)
    {
        LOG.info(String.format("Remove dashlet %s from column %s", dashlet.getDashletName(), columnNumber));
        WebElement dash = browser.waitUntilElementVisible(
            By.xpath(String.format(addedDashlet, columnNumber, dashlet.getDashletName())));
        if (dash != null)
        {
            dash.click();
            browser.dragAndDrop(dash, trashcan);
        }
        else
        {
            throw new PageOperationException(dashlet.getDashletName() + " not found in column " + columnNumber);
        }
        return (T) this;
    }

    /**
     * Verify if one column layout is displayed
     *
     * @return true if displayed
     */
    public boolean isOneColumnLayoutDisplayed()
    {
        return browser.isElementDisplayed(oneColumnLayout);
    }

    public T assertOneColumnLayoutIsDisplayed()
    {
        LOG.info("Assert One column layout is displayed");
        Assert.assertTrue(browser.isElementDisplayed(oneColumnLayout), "One column layout is displayed");
        return (T) this;
    }

    public boolean isThreeColumnsLayoutDisplayed()
    {
        return browser.isElementDisplayed(threeColumnsLayout);
    }

    public T assertThreeColumnsLayoutIsDisplayed()
    {
        LOG.info("Assert Three Columns Layout is displayed");
        Assert.assertTrue(browser.isElementDisplayed(threeColumnsLayout), "Three Columns Layout is displayed");
        return (T) this;
    }

    public boolean isFourColumnsLayoutDisplayed()
    {
        return browser.isElementDisplayed(fourColumnsLayout);
    }

    public T assertFourColumnsLayoutIsDisplayed()
    {
        LOG.info("Assert Four Columns Layout is displayed");
        Assert.assertTrue(browser.isElementDisplayed(fourColumnsLayout), "Four Columns Layout is displayed");
        return (T) this;
    }

    public boolean isTwoColumnsLayoutWideRightDisplayed()
    {
        return browser.isElementDisplayed(twoColumnsWideRightLayout);
    }

    public T assertTwoColumnsLayoutWideRightIsDisplayed()
    {
        LOG.info("Assert Two Columns Layout Wide Right is displayed");
        Assert.assertTrue(browser.isElementDisplayed(twoColumnsWideRightLayout), "Two Columns Layout Wide right is displayed");
        return (T) this;
    }

    public boolean isTwoColumnsLayoutWideLeftDisplayed()
    {
        return browser.isElementDisplayed(twoColumnsWideLeftLayout);
    }

    public T assertTwoColumnsLayoutWideLeftIsDisplayed()
    {
        LOG.info("Assert Two Columns Layout Wide Left is displayed");
        Assert.assertTrue(browser.isElementDisplayed(twoColumnsWideLeftLayout), "Two Columns Layout Wide Left is displayed");
        return (T) this;
    }

    public boolean isChangeLayoutButtonDisplayed()
    {
        return browser.isElementDisplayed(changeLayout);
    }

    public T assertChangeLayoutButtonIsDisplayed()
    {
        LOG.info("Assert change layout button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(changeLayout), "Change layout button is displayed");
        return (T) this;
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

    public T clickCloseAvailabeDashlets()
    {
        LOG.info("Close available dashlets");
        closeAddDashlets.click();
        return (T) this;
    }

    public boolean isAvailableDashletListDisplayed()
    {
        return browser.isElementDisplayed(availableDashletList);
    }

    public T assertAvailableDashletsSectionIsDisplayed()
    {
        LOG.info("Assert available dashlet section is displayed");
        Assert.assertTrue(browser.isElementDisplayed(availableDashletList), "Available dashlets section is displayed");
        return (T) this;
    }

    public T assertAvailableDashletsSectionIsNotDisplayed()
    {
        LOG.info("Assert available dashlet section is NOT displayed");
        Assert.assertFalse(browser.isElementDisplayed(availableDashletList), "Available dashlets section is displayed");
        return (T) this;
    }

    /**
     * Move added dashlet in column.
     *
     * @param addedDashlet dashlet to be moved
     * @param fromColumn
     * @param toColumn
     */
    public T moveAddedDashletInColumn(Dashlets addedDashlet, int fromColumn, int toColumn)
    {
        LOG.info(String.format("Move dashlet %s from column %s to %s", addedDashlet.getDashletName(), fromColumn, toColumn));
        WebElement dashToMove = getDashletToMove(addedDashlet, fromColumn);
        dashToMove.click();
        browser.waitUntilElementHasAttribute(dashToMove, "class", "dnd-focused");
        WebElement target = browser.findElement(By.cssSelector(String.format(targetColumn, toColumn)));
        target.click();
        dragAndDropDashlet(dashToMove, target);
        retryAddDashlet(addedDashlet, dashToMove, target, toColumn);
        return (T) this;
    }

    /**
     * Move added dashlet in column.
     *
     * @param dashletToMove    to be moved
     * @param dashletToReplace to be replaced
     * @param column
     */
    public List<WebElement> reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column)
    {
        List<WebElement> dashlets = getDashlets(dashletToMove, dashletToReplace, column);
        dashlets.get(0).click();
        browser.waitUntilElementHasAttribute(dashlets.get(0), "class", "dnd-focused");
        return dashlets;
    }

    private List<WebElement> getDashlets(Dashlets dashletToMove, Dashlets dashletToReplace, int column)
    {
        List<WebElement> elements = new ArrayList<>();
        WebElement dashToMove = getDashletToMove(dashletToMove, column);
        WebElement dashToReplace = getDashletToReplace(dashletToReplace, column);
        elements.add(dashToMove);
        elements.add(dashToReplace);
        return elements;
    }

    private WebElement getDashletToMove(Dashlets dashletToMove, int fromColumn)
    {
        WebElement dashToMove;
        try
        {
            dashToMove = browser.waitUntilElementVisible(
                By.xpath(String.format(addedDashlet, fromColumn, dashletToMove.getDashletName())));
        } catch (NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToMove.getDashletName() + " not found in column " + fromColumn);
        }
        return dashToMove;
    }

    private WebElement getDashletToReplace(Dashlets dashletToReplace, int column)
    {
        WebElement dashToReplace;
        try
        {
            dashToReplace = browser.waitUntilElementVisible(
                By.xpath(String.format(addedDashlet, column, dashletToReplace.getDashletName())));
        } catch (NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToReplace.getDashletName() + " not found in column " + column);
        }
        return dashToReplace;
    }

    private void isNumberOfDashletsExcedeedInColumn(int toColumn)
    {
        if (browser.findElements(By.cssSelector(String.format(dashletsInColumn, toColumn))).size() > 5)
        {
            throw new PageOperationException("Exceeded the number of dashlets in given column.");
        }
    }

    private void dragAndDropDashlet(WebElement dashletToMove, WebElement target)
    {
        try
        {
            browser.dragAndDrop(dashletToMove, target);
        }
        catch (MoveTargetOutOfBoundsException e)
        {
            LOG.info("Retry drag&drop dashlet");
            browser.dragAndDrop(dashletToMove, target);
        }
    }

    public T reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column, int columnPositionToCheck)
    {
        List<WebElement> dashlets = reorderDashletsInColumn(dashletToMove, dashletToReplace, column);
        dashlets.get(1).click();
        browser.dragAndDrop(dashlets.get(0), dashlets.get(1));
        if(!isDashletInPositionInColumn(dashletToMove, column, columnPositionToCheck))
        {
            LOG.info("Retry reorder dashlet");
            browser.dragAndDrop(dashlets.get(0), dashlets.get(1));
        }
        return (T) this;
    }
}
