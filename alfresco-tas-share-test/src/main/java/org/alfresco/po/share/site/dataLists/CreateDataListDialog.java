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

    private String typeOfListDescription = "//div[contains(@id, 'itemTypesContainer')]//a[text()='Contact List']//ancestor::*//div[contains(@id, 'itemTypesContainer')]//span";

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

        for (WebElement item : webElementInteraction.waitUntilElementsAreVisible(typesOfList))
        {
            typesOfListString.add(item.getText());
        }

        return typesOfListString;
    }

    public boolean isNewListPopupDisplayed()
    {
        return webElementInteraction.isElementDisplayed(newListPopup);
    }

    public String getTypeOfListDescription(DataListTypes listType)
    {
        return webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(typeOfListDescription,
                listType.toString()))).getText();
    }

    public String getTitleLabelText()
    {
        return webElementInteraction.waitUntilElementIsVisible(titleLabel).getText();
    }

    public boolean isTitleMandatoryIndicatorDisplayed()
    {
        return webElementInteraction.isElementDisplayed(titleMandatoryIndicator);
    }

    public boolean isTitleFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(titleField);
    }

    public String getDescriptionLabelText()
    {
        return webElementInteraction.waitUntilElementIsVisible(descriptionLabel).getText();
    }

    public boolean isDescriptionFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(descriptionField);
    }

    public CreateDataListDialog selectType(String type)
    {
        webElementInteraction.findFirstElementWithValue(typesOfList, type).click();
        return this;
    }

    public boolean isExpectedTypeSelected(String expectedType)
    {
        return webElementInteraction.findElement(typeSelected).getText().equals(expectedType);
    }

    public CreateDataListDialog typeTitle(String title)
    {
        log.info("Clear and type title: {}", title);
        webElementInteraction.clearAndType(titleField, title);
        return this;
    }

    public String getTitleValue()
    {
        return webElementInteraction.findElement(titleField).getAttribute("value");
    }

    public CreateDataListDialog typeDescription(String description)
    {
        log.info("Clear and type description: {}", description);
        webElementInteraction.clearAndType(descriptionField, description);
        return this;
    }

    public String getDescriptionValue()
    {
        return webElementInteraction.waitUntilElementIsVisible(titleField).getAttribute("value");
    }

    public String getInvalidDataListBalloonMessage()
    {
        return webElementInteraction.getElementText(balloon);
    }

    public String invalidTitleBalloonMessage()
    {
        return webElementInteraction.waitUntilElementIsVisible(titleField).getAttribute("title");
    }

    public boolean isSaveButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public void clickSaveButton()
    {
        log.info("Click Save button");
        webElementInteraction.clickElement(saveButton);
        waitUntilNotificationMessageDisappears();
    }

    public DataListsPage clickCancelButton()
    {
        log.info("Click Cancel button");
        webElementInteraction.clickElement(cancelButton);

        return new DataListsPage(webDriver);
    }
}