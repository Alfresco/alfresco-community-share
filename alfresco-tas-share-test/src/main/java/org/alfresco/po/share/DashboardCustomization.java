package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.share.DashboardCustomizationImpl.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.openqa.selenium.WebElement;

/**
 * @author bogdan.bocancea
 */
public interface DashboardCustomization
{
    String getSubTitle();

    /**
     * Get current layout text
     *
     * @return String current layout
     */
    String getCurrentLayout();

    /**
     * Click Change Layout button
     */
    void clickChangeLayout();

    /**
     * Get the instruction text from Select new layout
     *
     * @return String instructions
     */
    String getNewLayoutInstructions();

    /**
     * Get the instruction text for dashlets
     *
     * @return String instrctions
     */
    String getDashletsInstructions();

    /**
     * Select dashboard layout
     *
     * @param layout
     */
    void selectLayout(Layout layout);

    /**
     * Cancel New Layout selection
     */
    void clickCancelNewLayout();

    /**
     * Click add dashlet button
     */
    void clickAddDashlet();

    /**
     * Click OK button
     */
    void clickOk();

    /**
     * Add a dashlet to user or site dashboard
     *
     * @param dashlet      to be added
     * @param columnNumber int column number
     */
    void addDashlet(Dashlets dashlet, int columnNumber);

    /**
     * Verify if a dashlet is added in a column
     *
     * @param dashlet      that was added
     * @param columnNumber
     * @return true if found
     */
    boolean isDashletAddedInColumn(Dashlets dashlet, int columnNumber);

    /**
     * Remove a specific dashlet from a column
     *
     * @param dashlet      to be removed
     * @param columnNumber
     */
    void removeDashlet(Dashlets dashlet, int columnNumber);

    /**
     * Verify if one column layout is displayed
     *
     * @return true if displayed
     */
    boolean isOneColumnLayoutDisplayed();

    /**
     * Verify if two columns wide right layout is displayed
     *
     * @return true if displayed
     */
    boolean isTwoColumnsLayoutWideRightDisplayed();

    /**
     * Verify if two columns wide left layout is displayed
     *
     * @return true if displayed
     */
    boolean isTwoColumnsLayoutWideLeftDisplayed();

    /**
     * Verify if three columns layout is displayed
     *
     * @return true if displayed
     */
    boolean isThreeColumnsLayoutDisplayed();

    /**
     * Verify if four columns layout is displayed
     *
     * @return true if displayed
     */
    boolean isFourColumnsLayoutDisplayed();

    /**
     * Verify if change layout button is displayed
     *
     * @return true if displayed
     */
    boolean isChangeLayoutButtonDisplayed();

    /**
     * Verify if Layout section is displayed
     *
     * @return true if displayed
     */
    boolean isChangeLayoutSectionDisplayed();

    /**
     * Verify if dashlets section is displayed
     *
     * @return true if displayed
     */
    boolean isDashletSectionDisplayed();

    /**
     * Verify if Add Dashlet button is displayed
     *
     * @return true if displayed
     */
    boolean isAddDashletButtonDisplayed();

    /**
     * Get a list of added dashlet names from a given column
     *
     * @param column number
     * @return List<String> of dashlet names
     */
    List<String> getDashletsFromColumn(int column);

    /**
     * Get a list of available dashlet names
     *
     * @return List<String> of dashlet names
     */
    List<String> getAvailableDashlets();

    /**
     * Click 'Close' link on available dashles
     */
    void clickCloseAvailabeDashlets();

    /**
     * Verify if the list of available dashlets is displayed
     *
     * @return true if displayed
     */
    boolean isAvailableDashletListDisplayed();

    /**
     * Move an added dashlet from one column to another
     *
     * @param dashlet    Dashlets dashlet to move
     * @param fromColumn int from column number
     * @param toColumn   int to column number
     */
    void moveAddedDashletInColumn(Dashlets dashlet, int fromColumn, int toColumn);

    List<WebElement> reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column);

}
