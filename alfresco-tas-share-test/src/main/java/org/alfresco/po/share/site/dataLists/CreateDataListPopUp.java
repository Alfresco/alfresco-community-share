package org.alfresco.po.share.site.dataLists;

import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class CreateDataListPopUp extends ShareDialog
{
    protected static By balloon = By.cssSelector("div[style*='visible'] div[class='balloon'] div[class='text']");
    protected static By balloonCloseButton = By.cssSelector("div[style*='visible'] div[class='balloon'] div[class='closeButton']");
    @FindBy (css = "div[id$='itemTypesContainer'] a")
    protected List<WebElement> typesOfList;
    @FindBy (css = "input[id$='newList_prop_cm_title']")
    protected WebElement titleField;
    @FindBy (css = "textarea[id$='newList_prop_cm_description']")
    protected WebElement descriptionField;
    @RenderWebElement
    @FindBy (css = "button[id$='form-submit-button']")
    protected WebElement saveButton;
    @RenderWebElement
    @FindBy (css = "button[id$='form-cancel-button']")
    protected WebElement cancelButton;
    protected By typeSelected = By.cssSelector("div[class='theme-bg-selected'] a");
    @Autowired
    DataListsPage dataListsPage;

    public void selectType(String type)
    {
        browser.findFirstElementWithValue(typesOfList, type).click();
    }

    public boolean isExpectedTypeSelected(String expectedType)
    {
        return browser.findElement(typeSelected).getText().equals(expectedType);
    }

    public void typeTitleName(String titleName)
    {
        titleField.sendKeys(titleName);
    }

    public void typeDescription(String description)
    {
        descriptionField.sendKeys(description);
    }

    public String getInvalidDataListBalloonMessage()
    {
        return browser.findElement(balloon).getText();
    }

    public String invalidTitleBalloonMessage()
    {
        return titleField.getAttribute("title");
    }

    public DataListsPage clickSaveButton()
    {
        saveButton.click();
        browser.waitInSeconds(3);
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public DataListsPage clickCancelFormButton()
    {
        cancelButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public enum DataListTypes
    {
        ContactList
            {
                public String toString()
                {
                    return "Contact List";
                }
            },

        EventAgenda
            {
                public String toString()
                {
                    return "Event Agenda";
                }
            },

        EventList
            {
                public String toString()
                {
                    return "Event List";
                }
            },

        IssueList
            {
                public String toString()
                {
                    return "Issue List";
                }
            },

        LocationList
            {
                public String toString()
                {
                    return "Location List";
                }
            },

        MeetingAgenda
            {
                public String toString()
                {
                    return "Meeting Agenda";
                }
            },

        TaskListAdvanced
            {
                public String toString()
                {
                    return "Task List (Advanced)";
                }
            },

        TaskListSimple
            {
                public String toString()
                {
                    return "Task List (Simple)";
                }
            },

        ToDoList
            {
                public String toString()
                {
                    return "To Do List";
                }
            },

        VisitorFeedbackList
            {
                public String toString()
                {
                    return "Visitor Feedback List";
                }
            }
    }
}
