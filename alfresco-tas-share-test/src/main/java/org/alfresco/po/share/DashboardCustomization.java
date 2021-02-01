package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.exception.PageOperationException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

/**
 * @author bogdan.bocancea
 */
public abstract class DashboardCustomization<T> extends SharePage2<DashboardCustomization<T>>
{
    private final int columns = 4;
    private final int maxDashletsInColumn = 5;

    private final By availableDashlets = By.cssSelector("ul.availableList>li.availableDashlet>span");

    private final By subTitle = By.cssSelector(".sub-title");
    private final By layoutSection = By.cssSelector(".customise-layout");
    private final By dashletsSection = By.cssSelector(".customise-dashlets");
    private final By currentLayout = By.cssSelector("span[id$='_default-currentLayoutDescription-span']");
    private final By changeLayout = By.cssSelector("button[id$='_default-change-button-button']");
    private final By oneColumn = By.cssSelector("button[id$='dashboard-1-column-button']");
    private final By twoColumn_left = By.cssSelector( "button[id$='2-columns-wide-left-button']");
    private final By twoColumn_right = By.cssSelector("button[id$='2-columns-wide-right-button']");
    private final By threeColumn = By.cssSelector("button[id$='3-columns-button']");
    private final By fourColumn = By.cssSelector("button[id$='dashboard-4-columns-button']");
    private final By newLayoutInstructions = By.cssSelector(".layouts .text");
    private final By dashletsInstructions = By.cssSelector(".instructions .text");
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
    private final String dashletInAddedColumn = "ul[id$='default-column-ul-%s'] > li:nth-child(%s)";
    private final String availableDashlet = "li[class='availableDashlet dnd-draggable'] div[title^='%s']";
    private final String dashletsInColumn = "ul[id$='column-ul-%d'] li > span";
    private final String targetColumn = "ul[id$='default-column-ul-%d']";
    private final String addedDashlet = "//ul[contains(@id,'default-column-ul-%d')]/li//span[text()='%s']/..";

    public DashboardCustomization(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

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
        return webElementInteraction.getElementText(subTitle);
    }

    public String getCurrentLayout()
    {
        return webElementInteraction.getElementText(currentLayout);
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
        assertEquals(webElementInteraction.getElementText(currentLayout), layoutLabel, "Layout label is correct");
        return (T) this;
    }

    public T clickChangeLayout()
    {
        LOG.info("Click Change Layout");
        webElementInteraction.clickElement(changeLayout);
        webElementInteraction.waitUntilElementIsVisible(selectNewLayout);
        return (T) this;
    }

    public String getNewLayoutInstructions()
    {
        return webElementInteraction.getElementText(newLayoutInstructions);
    }

    public String getDashletsInstructions()
    {
        return webElementInteraction.getElementText(dashletsInstructions);
    }

    public T selectLayout(Layout layout)
    {
        LOG.info(String.format("Select layout %s", layout.description));
        switch (layout)
        {
            case ONE_COLUMN:
                webElementInteraction.clickElement(oneColumn);
                break;
            case TWO_COLUMNS_WIDE_LEFT:
                webElementInteraction.clickElement(twoColumn_left);
                break;
            case TWO_COLUMNS_WIDE_RIGHT:
                webElementInteraction.clickElement(twoColumn_right);
                break;
            case THREE_COLUMNS:
                webElementInteraction.clickElement(threeColumn);
                break;
            case FOUR_COLUMNS:
                webElementInteraction.clickElement(fourColumn);
                break;
            default:
                break;
        }
        return (T) this;
    }

    public T clickCancelNewLayout()
    {
        webElementInteraction.clickElement(cancelNewLayout);
        webElementInteraction.waitUntilElementDisappears(selectNewLayout);
        return (T) this;
    }

    public T clickAddDashlet()
    {
        LOG.info("Click Add Dashlet");
        webElementInteraction.clickElement(addDashlets);
        webElementInteraction.waitUntilElementIsVisible(availableDashletList);
        return (T) this;
    }

    public void clickOk()
    {
        LOG.info("Click OK");
        webElementInteraction.clickElement(okButton);
    }

    public T addDashlet(Dashlets dashlet, int columnNumber)
    {
        if (columnNumber < 1 || columnNumber > columns)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }
        if (webElementInteraction.isElementDisplayed(addDashlets))
        {
            clickAddDashlet();
        }
        String dashletName = dashlet.getDashletName();
        String dashletXpath = String.format(availableDashlet, dashletName);
        WebElement webDashlet = webElementInteraction.findElement(By.cssSelector(dashletXpath));
        try
        {
            webElementInteraction.clickElement(By.cssSelector(dashletXpath));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException(dashletName + " not found in Add Dashlets list");
        }
        webElementInteraction.scrollToElement(webDashlet);
        webElementInteraction.clickElement(webDashlet);

        String dashletColumns = webElementInteraction.findElement(availableColumns).getAttribute("class");
        int noOfColumns = Integer.parseInt(dashletColumns.substring(dashletColumns.length() - 1));
        List<WebElement> existingDashletsInColumn = new ArrayList<>();
        if (columnNumber <= noOfColumns)
        {
            try
            {
                existingDashletsInColumn = webElementInteraction.findElements(By.cssSelector(String.format(dashletsInColumn, columnNumber)));
            }
            catch (TimeoutException te)
            {

            }
            if (existingDashletsInColumn.size() < maxDashletsInColumn)
            {
                WebElement target = webElementInteraction.waitUntilElementIsVisible(
                    By.cssSelector(String.format(targetColumn, columnNumber)));
                if(!dashlet.getDashletName().equals(Dashlets.CONTENT_I_AM_EDITING.getDashletName()))
                {
                    webElementInteraction.executeJavaScript("window.scrollBy(0,500)");
                }
                try
                {
                    webElementInteraction.dragAndDrop(webDashlet, target);
                }
                catch (MoveTargetOutOfBoundsException e)
                {
                    LOG.error(String.format("Failed to add dashlet %s. Will retry...", dashlet.getDashletName()));
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
            LOG.error("Retry add dashlet {}", i);
            dragAndDropDashlet(webDashlet, target);
            added = isDashletAddedInColumn(dashlet, columnNr);
            i++;
        }
    }

    public List<String> getDashletsFromColumn(int column)
    {
        List<WebElement> existingDashletsInColumn = webElementInteraction.findElements(By.cssSelector(String.format(dashletsInColumn, column)));
        return existingDashletsInColumn.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getAvailableDashlets()
    {
        return webElementInteraction.findElements(availableDashlets).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private WebElement getDashletAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        WebElement dash = null;
        try
        {
            dash = webElementInteraction.findFirstElementWithValue(By.cssSelector(String.format(dashletsInColumn, columnNumber)),
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
        LOG.info("Assert dashlet is added in column: {}, {}", dashlet.getDashletName(), columnNumber);
        assertTrue(isDashletAddedInColumn(dashlet, columnNumber), String.format(
            "Dashlet %s is added in column %s", dashlet.getDashletName(), columnNumber));

        return (T) this;
    }

    public T assertDashletIsNotAddedInColumn(Dashlets dashlet, int columnNumber)
    {
        LOG.info("Assert dashlet is not added in column {}, {}", dashlet.getDashletName(), columnNumber);
        assertFalse(isDashletAddedInColumn(dashlet, columnNumber), String.format(
            "Dashlet %s is added in column %s", dashlet.getDashletName(), columnNumber));
        return (T) this;
    }

    public boolean isDashletInPositionInColumn(Dashlets dashlet, int columnNumber, int columnPosition)
    {
        WebElement dashletElement = webElementInteraction.findElement(By.cssSelector
            (String.format(dashletInAddedColumn, columnNumber, columnPosition)));
        return dashletElement.getText().equals(dashlet.getDashletName());
    }

    public T removeDashlet(Dashlets dashlet, int columnNumber)
    {
        LOG.info("Remove dashlet from column {}, {}", dashlet.getDashletName(), columnNumber);
        WebElement dash = webElementInteraction.waitUntilElementIsVisible(
            By.xpath(String.format(addedDashlet, columnNumber, dashlet.getDashletName())));
        if (dash != null)
        {
            webElementInteraction.clickElement(dash);
            webElementInteraction.dragAndDrop(dash, webElementInteraction.findElement(trashcan));
        }
        else
        {
            throw new PageOperationException(dashlet.getDashletName() + " not found in column " + columnNumber);
        }
        return (T) this;
    }

    public boolean isOneColumnLayoutDisplayed()
    {
        return webElementInteraction.isElementDisplayed(oneColumnLayout);
    }

    public T assertOneColumnLayoutIsDisplayed()
    {
        LOG.info("Assert One column layout is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(oneColumnLayout), "One column layout is displayed");
        return (T) this;
    }

    public boolean isThreeColumnsLayoutDisplayed()
    {
        return webElementInteraction.isElementDisplayed(threeColumnsLayout);
    }

    public T assertThreeColumnsLayoutIsDisplayed()
    {
        LOG.info("Assert Three Columns Layout is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(threeColumnsLayout), "Three Columns Layout is displayed");
        return (T) this;
    }

    public boolean isFourColumnsLayoutDisplayed()
    {
        return webElementInteraction.isElementDisplayed(fourColumnsLayout);
    }

    public T assertFourColumnsLayoutIsDisplayed()
    {
        LOG.info("Assert Four Columns Layout is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(fourColumnsLayout), "Four Columns Layout is displayed");
        return (T) this;
    }

    public boolean isTwoColumnsLayoutWideRightDisplayed()
    {
        return webElementInteraction.isElementDisplayed(twoColumnsWideRightLayout);
    }

    public T assertTwoColumnsLayoutWideRightIsDisplayed()
    {
        LOG.info("Assert Two Columns Layout Wide Right is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(twoColumnsWideRightLayout), "Two Columns Layout Wide right is displayed");
        return (T) this;
    }

    public boolean isTwoColumnsLayoutWideLeftDisplayed()
    {
        return webElementInteraction.isElementDisplayed(twoColumnsWideLeftLayout);
    }

    public T assertTwoColumnsLayoutWideLeftIsDisplayed()
    {
        LOG.info("Assert Two Columns Layout Wide Left is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(twoColumnsWideLeftLayout), "Two Columns Layout Wide Left is displayed");
        return (T) this;
    }

    public boolean isChangeLayoutButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(changeLayout);
    }

    public T assertChangeLayoutButtonIsDisplayed()
    {
        LOG.info("Assert change layout button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(changeLayout), "Change layout button is displayed");
        return (T) this;
    }

    public boolean isChangeLayoutSectionDisplayed()
    {
        return webElementInteraction.isElementDisplayed(layoutSection);
    }

    public boolean isDashletSectionDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dashletsSection);
    }

    public boolean isAddDashletButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(addDashlets);
    }

    public T clickCloseAvailabeDashlets()
    {
        LOG.info("Close available dashlets");
        webElementInteraction.clickElement(closeAddDashlets);
        return (T) this;
    }

    public boolean isAvailableDashletListDisplayed()
    {
        return webElementInteraction.isElementDisplayed(availableDashletList);
    }

    public T assertAvailableDashletsSectionIsDisplayed()
    {
        LOG.info("Assert available dashlet section is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(availableDashletList), "Available dashlets section is displayed");
        return (T) this;
    }

    public T assertAvailableDashletsSectionIsNotDisplayed()
    {
        LOG.info("Assert available dashlet section is NOT displayed");
        assertFalse(webElementInteraction.isElementDisplayed(availableDashletList), "Available dashlets section is displayed");
        return (T) this;
    }

    public T moveAddedDashletInColumn(Dashlets addedDashlet, int fromColumn, int toColumn)
    {
        LOG.info(String.format("Move dashlet %s from column %s to %s", addedDashlet.getDashletName(), fromColumn, toColumn));
        WebElement dashToMove = getDashletToMove(addedDashlet, fromColumn);
        webElementInteraction.clickElement(dashToMove);
        webElementInteraction.waitUntilElementHasAttribute(dashToMove, "class", "dnd-focused");
        WebElement target = webElementInteraction.findElement(By.cssSelector(String.format(targetColumn, toColumn)));
        webElementInteraction.clickElement(target);
        dragAndDropDashlet(dashToMove, target);
        retryAddDashlet(addedDashlet, dashToMove, target, toColumn);

        return (T) this;
    }

    private WebElement getDashletToMove(Dashlets dashletToMove, int fromColumn)
    {
        WebElement dashToMove;
        try
        {
            dashToMove = webElementInteraction.waitUntilElementIsVisible(
                By.xpath(String.format(addedDashlet, fromColumn, dashletToMove.getDashletName())));
        }
        catch (NoSuchElementException ns)
        {
            throw new PageOperationException(dashletToMove.getDashletName() + " not found in column " + fromColumn);
        }
        return dashToMove;
    }

    private void isNumberOfDashletsExcedeedInColumn(int toColumn)
    {
        if (webElementInteraction.findElements(By.cssSelector(String.format(dashletsInColumn, toColumn))).size() > 5)
        {
            throw new PageOperationException("Exceeded the number of dashlets in given column.");
        }
    }

    private void dragAndDropDashlet(WebElement dashletToMove, WebElement target)
    {
        try
        {
            webElementInteraction.dragAndDrop(dashletToMove, target);
        }
        catch (MoveTargetOutOfBoundsException e)
        {
            LOG.info("Retry drag&drop dashlet");
            webElementInteraction.dragAndDrop(dashletToMove, target);
        }
    }

    public T moveDashletDownInColumn(Dashlets dashletToMove, int column)
    {
        WebElement dashlet = getDashletToMove(dashletToMove, column);
        webElementInteraction.clickElement(dashlet);
        webElementInteraction.waitUntilElementHasAttribute(dashlet, "class", "dnd-focused");
        Actions action = new Actions(webDriver.get());
        action.sendKeys(Keys.ARROW_DOWN).build().perform();

        return (T) this;
    }
}
