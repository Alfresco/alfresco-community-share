package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.share.DashboardCustomizationImpl.Layout;
import org.alfresco.po.share.dashlet.Dashlets;

/**
 * 
 * @author bogdan.bocancea
 *
 */
public interface DashboardCustomization
{
    public String getSubTitle();
    
    /**
     * Get current layout text
     * 
     * @return String current layout
     */
    public String getCurrentLayout();
    
    /**
     * Click Change Layout button
     */
    public void clickChangeLayout();
    
    /**
     * Get the instruction text from Select new layout
     * 
     * @return String instructions
     */
    public String getNewLayoutInstructions();
    
    /**
     * Get the instruction text for dashlets
     * 
     * @return String instrctions
     */
    public String getDashletsInstructions();
    
    /**
     * Select dashboard layout
     * 
     * @param layout
     */
    public void selectLayout(Layout layout);
    
    /**
     * Cancel New Layout selection
     */
    public void clickCancelNewLayout();
    
    /**
     * Click add dashlet button
     */
    public void clickAddDashlet();
    
    /**
     * Click OK button
     */
    public void clickOk();
    
    /**
     * Add a dashlet to user or site dashboard
     * @param dashlet to be added
     * @param columnNumber int column number
     */
    public void addDashlet(Dashlets dashlet, int columnNumber);
    
    /**
     * Verify if a dashlet is added in a column
     * @param dashlet that was added
     * @param columnNumber
     * @return true if found
     */
    public boolean isDashletAddedInColumn(Dashlets dashlet, int columnNumber);
    
    /**
     * Remove a specific dashlet from a column
     * @param dashlet to be removed
     * @param columnNumber
     */
    public void removeDashlet(Dashlets dashlet, int columnNumber);
    
    /**
     * Verify if one column layout is displayed
     * @return true if displayed
     */
    public boolean isOneColumnLayoutDisplayed();
    
    /**
     * Verify if two columns wide right layout is displayed
     * @return true if displayed
     */
    public boolean isTwoColumnsLayoutWideRightDisplayed();
    /**
     * Verify if two columns wide left layout is displayed
     * @return true if displayed
     */
    public boolean isTwoColumnsLayoutWideLeftDisplayed();
    
    /**
     * Verify if three columns layout is displayed
     * @return true if displayed
     */
    public boolean isThreeColumnsLayoutDisplayed();
    
    /**
     * Verify if four columns layout is displayed
     * @return true if displayed
     */
    public boolean isFourColumnsLayoutDisplayed();
    
    /**
     * Verify if change layout button is displayed
     * @return true if displayed
     */
    public boolean isChangeLayoutButtonDisplayed();
    
    /**
     * Verify if Layout section is displayed
     * @return true if displayed
     */
    public boolean isChangeLayoutSectionDisplayed();
    
    /**
     * Verify if dashlets section is displayed
     * @return true if displayed
     */
    public boolean isDashletSectionDisplayed();
    
    /**
     * Verify if Add Dashlet button is displayed
     * @return true if displayed
     */
    public boolean isAddDashletButtonDisplayed();
    
    /**
     * Get a list of added dashlet names from a given column
     * @param column number
     * @return List<String> of dashlet names
     */
    public List<String> getDashletsFromColumn(int column);
    
    /**
     * Get a list of available dashlet names
     * @return List<String> of dashlet names
     */
    public List<String> getAvailableDashlets();
    
    /**
     * Click 'Close' link on available dashles
     */
    public void clickCloseAvailabeDashlets();
    
    /**
     * Verify if the list of available dashlets is displayed
     * @return true if displayed
     */
    public boolean isAvailableDashletListDisplayed();
    
    /**
     * Move an added dashlet from one column to another
     * @param dashlet Dashlets dashlet to move
     * @param fromColumn int from column number
     * @param toColumn int to column number
     */
    public void moveAddedDashletInColumn(Dashlets dashlet, int fromColumn, int toColumn);
    
    public void reorderDashletsInColumn(Dashlets dashletToMove, Dashlets dashletToReplace, int column);
}
