package org.alfresco.po.share.alfrescoContent.aspects;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@PageObject
public class AspectsForm extends ShareDialog
{
    @RenderWebElement
    @FindBy(css = "[id$='default-aspects-title']")
    protected WebElement aspectsFormTitle;

    @RenderWebElement
    @FindBy(css = "[id$='default-aspects-left']")
    protected WebElement availableToAddPanel;

    @RenderWebElement
    @FindBy(css = "[id$='default-aspects-right']")
    protected WebElement currentlySelectedtPanel;

    @FindBy(css = ".container-close")
    protected WebElement closeButton;

    @FindBy(css = "button[id*='cancel']")
    protected WebElement cancelButton;

    @FindBy(css = "button[id*='ok-button']")
    protected WebElement saveButton;

    @FindBy(xpath = "//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='Restrictable']")
    protected WebElement restrictableArea;

    protected By availableAspectsList = By.cssSelector("div[class*='list-left yui-dt'] tr[class*='yui-dt-rec']");
    protected By aspectsSelectedList = By.cssSelector("div[class*='list-right yui-dt'] tr[class*='yui-dt-rec']");
    protected By addButtonsList = By.cssSelector("span[class*='addIcon']");
    protected By removeButtonsList = By.cssSelector("span[class*='removeIcon']");

    /**
     * Get list of aspects from "Available to add" panel
     */
    public List<WebElement> getAspectsToBeAddedList()
    {
        return browser.findElements(availableAspectsList);
    }

    /**
     * Get list of aspects from "Currently Selected" panel
     */
    public List<WebElement> getSelectedAspectsList()
    {
        return browser.findElements(aspectsSelectedList);
    }

    /**
     * Get list of add buttons from "Available to add" panel
     */
    public List<WebElement> getAddButtonsList()
    {
        return browser.findElements(addButtonsList);
    }

    /**
     * Get list of remove buttons from "Currently Selected" panel
     */
    public List<WebElement> getRemoveButtonsList()
    {
        return browser.findElements(removeButtonsList);
    }

    public boolean areAddButtonsDisplayed()
    {
        int numberOfAspectsAvailableToAdd = getAspectsToBeAddedList().size();
        int numberOfAddButtonsDisplayed = getAddButtonsList().size();

        return numberOfAspectsAvailableToAdd == numberOfAddButtonsDisplayed;
    }

    public boolean areRemoveButtonsDisplayed()
    {
        int numberOfSelectedAspects = getSelectedAspectsList().size();
        int numberOfRemoveButtonsDisplayed = getRemoveButtonsList().size();

        return numberOfSelectedAspects == numberOfRemoveButtonsDisplayed;
    }

    public boolean isAspectsFormTitleDisplayed()
    {
        return aspectsFormTitle.isDisplayed();
    }

    public boolean isAvailableToAddPanelDisplayed()
    {
        return availableToAddPanel.isDisplayed();
    }

    public boolean isCurrentlySelectedPanel()
    {
        return availableToAddPanel.isDisplayed();
    }

    public boolean isCancelButtonDisplayed()
    {
        return cancelButton.isDisplayed();
    }

    public boolean isSaveButtonDisplayed()
    {
        return saveButton.isDisplayed();
    }

    public boolean isCloseButtonDisplayed()
    {
        return closeButton.isDisplayed();
    }

    public WebElement getAvailableAspects(final String aspectName)
    {
        return browser.findFirstElementWithValue(availableAspectsList, aspectName);
    }

    public WebElement getSelectedAspects(final String aspectName)
    {
        return browser.findFirstElementWithValue(aspectsSelectedList, aspectName);
    }

    public boolean isAspectPresentOnAvailableAspectList(String aspectName)
    {
        int counter = 0;
        boolean found = false;
        while (!found && counter < 17)
        {
            found = browser.isElementDisplayed(getAvailableAspects(aspectName));

            if (!found)
            {
                counter++;
            }
        }
        return found;
    }

    public boolean isAspectPresentOnCurrentlySelectedList(String aspectName)
    {
        int counter = 0;
        boolean found = false;
        while (!found && counter < 3)
        {
            found = browser.isElementDisplayed(getSelectedAspects(aspectName));

            if (!found)
            {
                counter++;
            }
        }
        return found;
    }

    public void addElement(int aspectNumber)
    {
        getAddButtonsList().get(aspectNumber).click();
    }

    public void removeElement(int aspectNumber)
    {
        getRemoveButtonsList().get(aspectNumber).click();
    }

    public void clickApplyChangesButton()
    {
        saveButton.click();
    }

    public boolean isRestrictableAspectAdded()
    {
        return restrictableArea.isDisplayed();
    }

    public void clickCloseButton()
    {
        closeButton.click();
    }
}
