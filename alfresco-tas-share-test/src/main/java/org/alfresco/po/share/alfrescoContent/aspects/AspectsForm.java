package org.alfresco.po.share.alfrescoContent.aspects;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class AspectsForm extends ShareDialog
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @RenderWebElement
    @FindBy(css = "[id$='default-aspects-title']")
    protected WebElement aspectsFormTitle;

    @RenderWebElement
    @FindBy(css = "[id$='default-aspects-left']")
    protected WebElement availableToAddPanel;

    @RenderWebElement
    @FindBy(css = "[id$='default-aspects-right']")
    protected WebElement currentlySelectedPanel;

    @FindBy(css = ".container-close")
    protected WebElement closeButton;

    @FindBy(css = "button[id*='cancel']")
    protected WebElement cancelButton;

    @FindBy(css = "button[id*='ok-button']")
    protected WebElement saveButton;

    protected By availableAspectsList = By.cssSelector("div[class*='list-left yui-dt'] tr[class*='yui-dt-rec']");
    protected By aspectsSelectedList = By.cssSelector("div[class*='list-right yui-dt'] tr[class*='yui-dt-rec']");
    protected By addButtonsList = By.cssSelector("span[class*='addIcon']");
    protected By removeButtonsList = By.cssSelector("span[class*='removeIcon']");

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
        List<WebElement> aspectsToBeAddedList = browser.findElements(availableAspectsList);
        int numberOfAspectsAvailableToAdd = aspectsToBeAddedList.size();
        int numberOfAddButtonsDisplayed = getAddButtonsList().size();

        return numberOfAspectsAvailableToAdd == numberOfAddButtonsDisplayed;
    }

    public boolean areRemoveButtonsDisplayed()
    {
        List<WebElement> selectedAspectsList = browser.findElements(aspectsSelectedList);
        int numberOfSelectedAspects = selectedAspectsList.size();
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
        return browser.isElementDisplayed(getAvailableAspects(aspectName));
    }

    public boolean isAspectPresentOnCurrentlySelectedList(String aspectName)
    {
        return browser.isElementDisplayed(getSelectedAspects(aspectName));
    }

    public void addAspect(String aspectName)
    {
        try
        {
            WebElement availableAspect = browser.findFirstElementWithValue(availableAspectsList, aspectName);
            Parameter.checkIsMandotary("Available aspect", availableAspect);
            availableAspect.findElement(addButtonsList).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Add icon for item: " + aspectName + " is not present.", noSuchElementExp);
        }
    }

    public void removeAspect(String aspectName)
    {
        try
        {
            WebElement selectedAspect = browser.findFirstElementWithValue(aspectsSelectedList, aspectName);
            Parameter.checkIsMandotary("Selected aspect", selectedAspect);
            selectedAspect.findElement(removeButtonsList).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error("Remove icon for item: " + aspectName + " is not present.", noSuchElementExp);
        }
    }

    public HtmlPage clickApplyChangesButton(HtmlPage pageToBeRendered)
    {
        saveButton.click();
        return pageToBeRendered.renderedPage();
    }

    public void clickCloseButton()
    {
        closeButton.click();
    }
}
