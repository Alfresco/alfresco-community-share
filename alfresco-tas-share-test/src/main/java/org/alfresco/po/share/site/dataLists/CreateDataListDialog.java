package org.alfresco.po.share.site.dataLists;

import static org.alfresco.common.DataUtil.isEnumContainedByList;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.DataListTypes;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class CreateDataListDialog extends BaseDialogComponent
{
    private final By balloon = By.cssSelector("div[style*='visible'] div[class='balloon'] div[class='text']");
    private final By typesOfList = By.cssSelector("div[id$='itemTypesContainer'] a");
    private final By titleField = By.cssSelector("input[id$='newList_prop_cm_title']");
    private final By descriptionField = By.cssSelector("textarea[id$='newList_prop_cm_description']");
    private final By saveButton = By.cssSelector("button[id$='form-submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private final By newListPopup = By.cssSelector("div[id$='newList-dialog']");
    private final By titleLabel = By.cssSelector("label[for*='default-newList'][for$='title']");
    private final By titleMandatoryIndicator = By.cssSelector("label[for*='default-newList'][for$='title'] span[class='mandatory-indicator']");
    private final By descriptionLabel = By.cssSelector("label[for*='default-newList'][for$='description']");
    private final By typeSelected = By.cssSelector("div[class='theme-bg-selected'] a");

    private final String typeOfListDescription = "//div[contains(@id, 'itemTypesContainer')]//a[text()='Contact List']//ancestor::*//div[contains(@id, 'itemTypesContainer')]//span";

    public CreateDataListDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public boolean isDataListComplete()
    {
        return isEnumContainedByList(DataListTypes.class, getValuesFromElements());
    }

    private List<String> getValuesFromElements()
    {
        List<String> typesOfListString = new ArrayList<>();

        for (WebElement item : waitUntilElementsAreVisible(typesOfList))
        {
            typesOfListString.add(item.getText());
        }

        return typesOfListString;
    }

    public boolean isNewListPopupDisplayed()
    {
        waitUntilElementIsVisible(newListPopup);
        return isElementDisplayed(newListPopup);
    }

    public String getTypeOfListDescription(DataListTypes listType)
    {
        return getElementText(By.xpath(String.format(typeOfListDescription, listType.toString())));
    }

    public String getTitleLabelText()
    {
        return waitUntilElementIsVisible(titleLabel).getText();
    }

    public boolean isTitleMandatoryIndicatorDisplayed()
    {
        return isElementDisplayed(titleMandatoryIndicator);
    }

    public boolean isTitleFieldDisplayed()
    {
        return isElementDisplayed(titleField);
    }

    public String getDescriptionLabelText()
    {
        return waitUntilElementIsVisible(descriptionLabel).getText();
    }

    public boolean isDescriptionFieldDisplayed()
    {
        return isElementDisplayed(descriptionField);
    }

    public CreateDataListDialog selectType(String type)
    {
        findFirstElementWithValue(typesOfList, type).click();
        return this;
    }

    public boolean isExpectedTypeSelected(String expectedType)
    {
        return findElement(typeSelected).getText().equals(expectedType);
    }

    public CreateDataListDialog typeTitle(String title)
    {
        log.info("Clear and type title: {}", title);
        clearAndType(titleField, title);
        return this;
    }

    public String getTitleValue()
    {
        return findElement(titleField).getAttribute("value");
    }

    public CreateDataListDialog typeDescription(String description)
    {
        log.info("Clear and type description: {}", description);
        clearAndType(descriptionField, description);
        return this;
    }

    public String getDescriptionValue()
    {
        return waitUntilElementIsVisible(titleField).getAttribute("value");
    }

    public String getInvalidDataListBalloonMessage()
    {
        return getElementText(balloon);
    }

    public String invalidTitleBalloonMessage()
    {
        return waitUntilElementIsVisible(titleField).getAttribute("title");
    }

    public boolean isSaveButtonDisplayed()
    {
        return isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(cancelButton);
    }

    public void clickSaveButton()
    {
        log.info("Click Save button");
        clickElement(saveButton);
        waitUntilNotificationMessageDisappears();
    }

    public DataListsPage clickCancelButton()
    {
        log.info("Click Cancel button");
        clickElement(cancelButton);

        return new DataListsPage(webDriver);
    }

    public void clickSave()
    {
        log.info("Click Save button");
        clickElement(saveButton);
    }
}