package org.alfresco.po.share.alfrescoContent.aspects;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AspectsForm extends BaseDialogComponent
{
    private final By aspectsFormTitle = By.cssSelector("[id$='default-aspects-title']");
    private final By availableToAddPanel = By.cssSelector("[id$='default-aspects-left']");
    private final By closeButton = By.cssSelector(".container-close");
    private final By cancelButton = By.cssSelector("button[id*='cancel']");
    private final By saveButton = By.cssSelector("button[id*='ok-button']");
    private final By availableAspectsList = By.cssSelector("div[class*='list-left yui-dt'] tr[class*='yui-dt-rec']");
    private final By aspectsSelectedList = By.cssSelector("div[class*='list-right yui-dt'] tr[class*='yui-dt-rec']");
    private final By addButtonsList = By.cssSelector("span[class*='addIcon']");
    private final By removeButtonsList = By.cssSelector("span[class*='removeIcon']");

    public AspectsForm(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public List<WebElement> getAddButtonsList()
    {
        return findElements(addButtonsList);
    }

    public List<WebElement> getRemoveButtonsList()
    {
        return findElements(removeButtonsList);
    }

    public boolean areAddButtonsDisplayed()
    {
        List<WebElement> aspectsToBeAddedList = findElements(availableAspectsList);
        int numberOfAspectsAvailableToAdd = aspectsToBeAddedList.size();
        int numberOfAddButtonsDisplayed = getAddButtonsList().size();

        return numberOfAspectsAvailableToAdd == numberOfAddButtonsDisplayed;
    }

    public boolean areRemoveButtonsDisplayed()
    {
        List<WebElement> selectedAspectsList = findElements(aspectsSelectedList);
        int numberOfSelectedAspects = selectedAspectsList.size();
        int numberOfRemoveButtonsDisplayed = getRemoveButtonsList().size();

        return numberOfSelectedAspects == numberOfRemoveButtonsDisplayed;
    }

    public boolean isAspectsFormTitleDisplayed()
    {
        return isElementDisplayed(aspectsFormTitle);
    }

    public boolean isAvailableToAddPanelDisplayed()
    {
        return isElementDisplayed(availableToAddPanel);
    }

    public boolean isCurrentlySelectedPanel()
    {
        return isElementDisplayed(availableToAddPanel);
    }

    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(cancelButton);
    }

    public boolean isSaveButtonDisplayed()
    {
        return isElementDisplayed(saveButton);
    }

    @Override
    public boolean isCloseButtonDisplayed()
    {
        return isElementDisplayed(closeButton);
    }

    public WebElement getAvailableAspects(final String aspectName)
    {
        return findFirstElementWithValue(availableAspectsList, aspectName);
    }

    public WebElement getSelectedAspects(final String aspectName)
    {
        return findFirstElementWithValue(aspectsSelectedList, aspectName);
    }

    public boolean isAspectPresentOnAvailableAspectList(String aspectName)
    {
        return isElementDisplayed(getAvailableAspects(aspectName));
    }

    public boolean isAspectPresentOnCurrentlySelectedList(String aspectName)
    {
        return isElementDisplayed(getSelectedAspects(aspectName));
    }

    public void addAspect(String aspectName)
    {
        try
        {
            WebElement availableAspect = findFirstElementWithValue(availableAspectsList, aspectName);
            Parameter.checkIsMandotary("Available aspect", availableAspect);
            scrollIntoView(availableAspect);
            availableAspect.findElement(addButtonsList).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            log.error("Add icon for item: " + aspectName + " is not present.", noSuchElementExp);
        }
    }

    public void removeAspect(String aspectName)
    {
        try
        {
            WebElement selectedAspect = findFirstElementWithValue(aspectsSelectedList, aspectName);
            Parameter.checkIsMandotary("Selected aspect", selectedAspect);
            selectedAspect.findElement(removeButtonsList).click();
        } catch (NoSuchElementException noSuchElementExp)
        {
            log.error("Remove icon for item: " + aspectName + " is not present.", noSuchElementExp);
        }
    }

    public void clickApplyChangesButton()
    {
        findElement(saveButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public void clickCloseButton()
    {
        findElement(closeButton).click();
    }

    public void clickCancelButton()
    {
        findElement(cancelButton).click();
    }
}
