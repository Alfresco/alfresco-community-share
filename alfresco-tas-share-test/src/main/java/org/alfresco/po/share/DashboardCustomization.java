package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

/**
 * @author bogdan.bocancea
 */
@Slf4j
public abstract class DashboardCustomization<T> extends SharePage2<DashboardCustomization<T>>
{
    private final int COLUMNS = 4;
    private final int MAX_DASHLETS_IN_COLUMN = 5;

    private final By availableDashlets = By.cssSelector("ul.availableList>li.availableDashlet>span");
    private final By layoutSection = By.cssSelector(".customise-layout");
    private final By dashletsSection = By.cssSelector(".customise-dashlets");
    private final By currentLayout = By.cssSelector("span[id$='_default-currentLayoutDescription-span']");
    private final By changeLayout = By.cssSelector("button[id$='_default-change-button-button']");
    private final By oneColumn = By.cssSelector("button[id$='dashboard-1-column-button']");
    private final By twoColumn_left = By.cssSelector( "button[id$='2-columns-wide-left-button']");
    private final By twoColumn_right = By.cssSelector("button[id$='2-columns-wide-right-button']");
    private final By threeColumn = By.cssSelector("button[id$='3-columns-button']");
    private final By fourColumn = By.cssSelector("button[id$='dashboard-4-columns-button']");
    private final By cancelNewLayout = By.cssSelector(".layouts .buttons button[id$='default-cancel-button-button']");
    private final By okButton = By.cssSelector("button[id$='save-button-button']");
    private final By addDashlets = By.cssSelector("button[id$='addDashlets-button-button']");
    private final By availableColumns = By.cssSelector("div[id$='default-wrapper-div']");
    private final By trashcan = By.cssSelector(".trashcan");
    private final By oneColumnLayout = By.cssSelector("li[id$='layout-li-dashboard-1-column']");
    private final By threeColumnsLayout = By.cssSelector("li[id$='layout-li-dashboard-3-columns']");
    private final By fourColumnsLayout = By.cssSelector("li[id$='layout-li-dashboard-4-columns']");
    private final By twoColumnsWideRightLayout = By.cssSelector("li[id$='layout-li-dashboard-2-columns-wide-right']");
    private final By twoColumnsWideLeftLayout = By.cssSelector("li[id$='layout-li-dashboard-2-columns-wide-left']");
    private final By closeAddDashlets = By.cssSelector(".closeLink");
    private final By availableDashletList = By.cssSelector(".availableList");
    private final By selectNewLayout = By.cssSelector("div[id$='_default-layouts-div']");

    private final String availableDashlet = "li[class='availableDashlet dnd-draggable'] div[title^='%s']";
    private final String dashletsInColumn = "ul[id$='column-ul-%d'] li > span";
    private final String targetColumn = "ul[id$='default-column-ul-%d']";
    private final String addedDashlet = "//ul[contains(@id,'default-column-ul-%d')]/li//span[text()='%s']/..";

    protected DashboardCustomization(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getCurrentLayout()
    {
        return getElementText(currentLayout);
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
        log.info(String.format("Assert current layout is: %s", layoutLabel));
        assertEquals(getElementText(currentLayout), layoutLabel, "Layout label is correct");
        return (T) this;
    }

    public T clickChangeLayout()
    {
        log.info("Click Change Layout");
        clickElement(changeLayout);
        waitUntilElementIsVisible(selectNewLayout);
        return (T) this;
    }

    public T selectLayout(Layout layout)
    {
        log.info(String.format("Select layout %s", layout.description));
        switch (layout)
        {
            case ONE_COLUMN:
                clickElement(oneColumn);
                break;
            case TWO_COLUMNS_WIDE_LEFT:
                clickElement(twoColumn_left);
                break;
            case TWO_COLUMNS_WIDE_RIGHT:
                clickElement(twoColumn_right);
                break;
            case THREE_COLUMNS:
                clickElement(threeColumn);
                break;
            case FOUR_COLUMNS:
                clickElement(fourColumn);
                break;
            default:
                break;
        }
        return (T) this;
    }

    public T clickCancelNewLayout()
    {
        clickElement(cancelNewLayout);
        waitUntilElementDisappears(selectNewLayout);
        return (T) this;
    }

    public T clickAddDashlet()
    {
        log.info("Click Add Dashlet");
        clickElement(addDashlets);
        waitUntilElementIsVisible(availableDashletList);
        return (T) this;
    }

    public void clickOk()
    {
        log.info("Click OK");
        clickElement(okButton);
        waitUntilNotificationMessageDisappears();
    }

    public T addDashlet(Dashlets dashlet, int columnNumber)
    {
        if (columnNumber < 1 || columnNumber > COLUMNS)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }
        if (isElementDisplayed(addDashlets))
        {
            clickAddDashlet();
        }
        String dashletName = dashlet.getDashletName();
        String dashletXpath = String.format(availableDashlet, dashletName);
        WebElement webDashlet = findElement(By.cssSelector(dashletXpath));
        try
        {
            clickElement(By.cssSelector(dashletXpath));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException(dashletName + " not found in Add Dashlets list");
        }
        scrollToElement(webDashlet);
        clickElement(webDashlet);

        String dashletColumns = findElement(availableColumns).getAttribute("class");
        int noOfColumns = Integer.parseInt(dashletColumns.substring(dashletColumns.length() - 1));
        List<WebElement> existingDashletsInColumn = new ArrayList<>();
        if (columnNumber <= noOfColumns)
        {
            try
            {
                existingDashletsInColumn = findElements(By.cssSelector(String.format(dashletsInColumn, columnNumber)));
            }
            catch (TimeoutException te)
            {

            }
            if (existingDashletsInColumn.size() < MAX_DASHLETS_IN_COLUMN)
            {
                WebElement target = waitUntilElementIsVisible(
                    By.cssSelector(String.format(targetColumn, columnNumber)));
                if(!dashlet.getDashletName().equals(Dashlets.CONTENT_I_AM_EDITING.getDashletName()))
                {
                    executeJavaScript("window.scrollBy(0,500)");
                }
                try
                {
                    dragAndDrop(webDashlet, target);
                }
                catch (MoveTargetOutOfBoundsException e)
                {
                    log.warn("Failed to add dashlet {} - retry", dashlet.getDashletName());
                    retryAddDashlet(dashlet, webDashlet, target, columnNumber);
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
        return (T) this;
    }

    public void retryAddDashlet(Dashlets dashlet, WebElement webDashlet, WebElement target, int columnNr)
    {
        int i = 0;
        int retry = 5;
        boolean added = isDashletAddedInColumn(dashlet, columnNr);
        while (i < retry && !added)
        {
            log.error("Retry add dashlet {}", i);
            dragAndDropDashlet(webDashlet, target);
            added = isDashletAddedInColumn(dashlet, columnNr);
            i++;
        }
    }

    public List<String> getDashletsFromColumn(int column)
    {
        List<WebElement> existingDashletsInColumn = findElements(By.cssSelector(String.format(dashletsInColumn, column)));
        return existingDashletsInColumn.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getAvailableDashlets()
    {
        return findElements(availableDashlets).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private WebElement getDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        WebElement dash = null;
        try
        {
            dash = findFirstElementWithValue(By.cssSelector(String.format(dashletsInColumn, columnNumber)),
                dashlet.getDashletName());
        }
        catch (TimeoutException te)
        {
        }
        return dash;
    }

    public boolean isDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        return getDashletAddedInColumn(dashlet, columnNumber) != null;
    }

    public T assertDashletIsAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        log.info("Assert dashlet is added in column: {}, {}", dashlet.getDashletName(), columnNumber);
        assertTrue(isDashletAddedInColumn(dashlet, columnNumber), String.format(
            "Dashlet %s is added in column %s", dashlet.getDashletName(), columnNumber));

        return (T) this;
    }

    public T assertDashletIsNotAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        log.info("Assert dashlet is not added in column {}, {}", dashlet.getDashletName(), columnNumber);
        assertFalse(isDashletAddedInColumn(dashlet, columnNumber), String.format(
            "Dashlet %s is added in column %s", dashlet.getDashletName(), columnNumber));
        return (T) this;
    }

    public T removeDashlet(Dashlets dashlet, int columnNumber)
    {
        log.info("Remove dashlet from column {}, {}", dashlet.getDashletName(), columnNumber);
        WebElement dash = waitUntilElementIsVisible(
            By.xpath(String.format(addedDashlet, columnNumber, dashlet.getDashletName())));
        if (dash != null)
        {
            clickElement(dash);
            dragAndDrop(dash, findElement(trashcan));
        }
        else
        {
            throw new PageOperationException(dashlet.getDashletName() + " not found in column " + columnNumber);
        }
        return (T) this;
    }

    public boolean isOneColumnLayoutDisplayed()
    {
        return isElementDisplayed(oneColumnLayout);
    }

    public T assertOneColumnLayoutIsDisplayed()
    {
        log.info("Assert One column layout is displayed");
        assertTrue(isElementDisplayed(oneColumnLayout), "One column layout is displayed");
        return (T) this;
    }

    public boolean isThreeColumnsLayoutDisplayed()
    {
        return isElementDisplayed(threeColumnsLayout);
    }

    public T assertThreeColumnsLayoutIsDisplayed()
    {
        log.info("Assert Three Columns Layout is displayed");
        assertTrue(isElementDisplayed(threeColumnsLayout), "Three Columns Layout is displayed");
        return (T) this;
    }

    public boolean isFourColumnsLayoutDisplayed()
    {
        return isElementDisplayed(fourColumnsLayout);
    }

    public T assertFourColumnsLayoutIsDisplayed()
    {
        log.info("Assert Four Columns Layout is displayed");
        assertTrue(isElementDisplayed(fourColumnsLayout), "Four Columns Layout is displayed");
        return (T) this;
    }

    public boolean isTwoColumnsLayoutWideRightDisplayed()
    {
        return isElementDisplayed(twoColumnsWideRightLayout);
    }

    public T assertTwoColumnsLayoutWideRightIsDisplayed()
    {
        log.info("Assert Two Columns Layout Wide Right is displayed");
        assertTrue(isElementDisplayed(twoColumnsWideRightLayout), "Two Columns Layout Wide right is displayed");
        return (T) this;
    }

    public boolean isTwoColumnsLayoutWideLeftDisplayed()
    {
        return isElementDisplayed(twoColumnsWideLeftLayout);
    }

    public T assertTwoColumnsLayoutWideLeftIsDisplayed()
    {
        log.info("Assert Two Columns Layout Wide Left is displayed");
        assertTrue(isElementDisplayed(twoColumnsWideLeftLayout), "Two Columns Layout Wide Left is displayed");
        return (T) this;
    }

    public boolean isChangeLayoutButtonDisplayed()
    {
        return isElementDisplayed(changeLayout);
    }

    public T assertChangeLayoutButtonIsDisplayed()
    {
        log.info("Assert change layout button is displayed");
        assertTrue(isElementDisplayed(changeLayout), "Change layout button is displayed");
        return (T) this;
    }

    public boolean isChangeLayoutSectionDisplayed()
    {
        return isElementDisplayed(layoutSection);
    }

    public boolean isDashletSectionDisplayed()
    {
        return isElementDisplayed(dashletsSection);
    }

    public boolean isAddDashletButtonDisplayed()
    {
        return isElementDisplayed(addDashlets);
    }

    public T clickCloseAvailabeDashlets()
    {
        log.info("Close available dashlets");
        clickElement(closeAddDashlets);
        return (T) this;
    }

    public T assertAvailableDashletsSectionIsDisplayed()
    {
        log.info("Assert available dashlet section is displayed");
        assertTrue(isElementDisplayed(availableDashletList), "Available dashlets section is displayed");
        return (T) this;
    }

    public T assertAvailableDashletsSectionIsNotDisplayed()
    {
        log.info("Assert available dashlet section is NOT displayed");
        assertFalse(isElementDisplayed(availableDashletList), "Available dashlets section is displayed");
        return (T) this;
    }

    public T moveAddedDashletInColumn(Dashlets addedDashlet, int fromColumn, int toColumn)
    {
        log.info(String.format("Move dashlet %s from column %s to %s", addedDashlet.getDashletName(), fromColumn, toColumn));
        WebElement dashToMove = getDashletToMove(addedDashlet, fromColumn);
        clickElement(dashToMove);
        waitUntilElementHasAttribute(dashToMove, "class", "dnd-focused");
        WebElement target = findElement(By.cssSelector(String.format(targetColumn, toColumn)));
        clickElement(target);
        dragAndDropDashlet(dashToMove, target);
        retryAddDashlet(addedDashlet, dashToMove, target, toColumn);

        return (T) this;
    }

    private WebElement getDashletToMove(Dashlets dashletToMove, int fromColumn)
    {
        WebElement dashToMove;
        try
        {
            dashToMove = waitUntilElementIsVisible(
                By.xpath(String.format(addedDashlet, fromColumn, dashletToMove.getDashletName())));
        }
        catch (NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToMove.getDashletName() + " not found in column " + fromColumn);
        }
        return dashToMove;
    }

    private void dragAndDropDashlet(WebElement dashletToMove, WebElement target)
    {
        try
        {
            dragAndDrop(dashletToMove, target);
        }
        catch (MoveTargetOutOfBoundsException e)
        {
            log.info("Retry drag&drop dashlet");
            dragAndDrop(dashletToMove, target);
        }
    }

    public T moveDashletDownInColumn(Dashlets dashletToMove, int column)
    {
        WebElement dashlet = getDashletToMove(dashletToMove, column);
        clickElement(dashlet);
        waitUntilElementHasAttribute(dashlet, "class", "dnd-focused");
        Actions action = new Actions(webDriver.get());
        action.sendKeys(Keys.ARROW_DOWN).build().perform();

        return (T) this;
    }
}
