package org.alfresco.po.share.alfrescoContent.aspects;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
        waitInSeconds(3);
        return isElementDisplayed(aspectsFormTitle);
    }

    public boolean isAvailableToAddPanelDisplayed()
    {
        waitInSeconds(2);
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

    public AspectsForm assertAspactPresentInAvailableList(String aspactName)
    {
        log.info("Verify that the Aspact is present in the Available List {}", aspactName);
        assertTrue(isAspectPresentOnAvailableAspectList(aspactName), String.format("Aspact %s is not present in the Available panel list", aspactName));
        return this;
    }

    public AspectsForm assertAspactPresentInCurrentlySelectedList(String aspactName)
    {
        log.info("Verify that the Aspact is present in the Currently Selected List {}", aspactName);
        assertTrue(isAspectPresentOnCurrentlySelectedList(aspactName), String.format("Aspact %s is not present in the currently selected list", aspactName));
        return this;
    }

    public AspectsForm assertIsAspectsFormTitleDisplayed()
    {
        log.info("Verify that the aspects form title displayed");
        assertTrue(isAspectsFormTitleDisplayed(), "Aspects for the file form is not diplayed");
        return this;
    }

    public AspectsForm assertIsAspectsFormTitleNotDisplayed()
    {
        log.info("Verify that the aspects form title is not displayed");
        assertFalse(isAspectsFormTitleDisplayed(), "Aspects for the file form is diplayed");
        return this;
    }

    public AspectsForm assertIsAvailableToAddPanelDisplayed()
    {
        log.info("Verify that the Available to Add Pabel is displayed");
        assertTrue(isAvailableToAddPanelDisplayed(), "Available to Add panel is not diaplyed");
        return this;
    }

    public AspectsForm assertIsCurrentlySelectedPanel()
    {
        log.info("Verify that the Currently Selected Pabel is displayed");
        assertTrue(isCurrentlySelectedPanel(), "Currently Selected panel is not diaplyed");
        return this;
    }

    public AspectsForm assertAreAddButtonsDisplayed()
    {
        log.info("Verify that Add buttons are displayed in the Available panel list");
        assertTrue(areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        return this;
    }

    public AspectsForm assertAreRemoveButtonsDisplayed()
    {
        log.info("Verify that Remove buttons are displayed in the Currently selected panel list");
        assertTrue(areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        return this;
    }

    public AspectsForm assertmIsApplyChangesButtonDisplayed()
    {
        log.info("Verify that Apply Changes button is displayed on Aspects Form");
        assertTrue(isSaveButtonDisplayed(), "Apply Chnages button is not displayed");
        return this;
    }

    public AspectsForm assertmIsCancelButtonDisplayed()
    {
        log.info("Verify that Cancel button is displayed on Aspects Form");
        assertTrue(isCancelButtonDisplayed(), "Cancel button is not displayed");
        return this;
    }

    public AspectsForm assertmIsCloseButtonDisplayed()
    {
        log.info("Verify that Close[X] button is displayed on Aspects Form");
        assertTrue(isCloseButtonDisplayed(), "Close button is not displayed");
        return this;
    }
}
