package org.alfresco.po.share.alfrescoContent.aspects;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.List;

@PageObject
public class AspectsForm extends SiteCommon<AspectsForm>
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

    @FindBy(xpath = "//span[contains(@class, 'removeIcon')]")
    protected WebElement removeButton;

    @FindBy(xpath = "//div[contains(@class, 'set-bordered-panel') and normalize-space(.)='Restrictable']")
    protected WebElement restrictableArea;

    @FindAll(@FindBy(css = "div[class*='list-left yui-dt'] tr[class*='yui-dt-rec']"))
    protected List<WebElement> availableAspectsList;

    @FindAll(@FindBy(css = "span[class*='addIcon']"))
    protected List<HtmlElement> addButtonsList;

    @FindAll(@FindBy(css = "div[class*='list-right yui-dt'] tr[class*='yui-dt-rec']"))
    protected List<WebElement> aspectsSelectedList;

    @FindAll(@FindBy(css = "span[class*='removeIcon']"))
    protected List<HtmlElement> removeButtonsList;

    /**
     * Get list of aspects from "Available to add" panel
     */
    public List<WebElement> getAspectsToBeAddedList()
    {
        return availableAspectsList;
    }

    /**
     * Get list of aspects from "Currently Selected" panel
     */
    public List<WebElement> getSelectedAspectsList()
    {
        return aspectsSelectedList;
    }

    public boolean areAddButtonsDisplayed()
    {
        int numberOfAspectsavailabletoAdd = availableAspectsList.size();
        int numberOfAddButtonsDisplayed = addButtonsList.size();

        return numberOfAspectsavailabletoAdd == numberOfAddButtonsDisplayed;

    }

    public boolean areRemoveButtonsDisplayed()
    {
        int numberOfSelectedAspects = aspectsSelectedList.size();
        int numberOfRemoveButtonsDisplayed = removeButtonsList.size();

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

    public boolean isCurrentlySelectedtPanel()

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

        addButtonsList.get(aspectNumber).click();

    }

    public void removeElement(int aspectNumber)

    {

        removeButtonsList.get(aspectNumber).click();

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

    @Override
    public String getRelativePath()
    {

        return null;
    }

}
