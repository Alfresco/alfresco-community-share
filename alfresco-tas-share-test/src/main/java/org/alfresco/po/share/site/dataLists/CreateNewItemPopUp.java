package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
@PageObject
public class CreateNewItemPopUp extends ShareDialog
{
    @Autowired
    DataListsPage dataListsPage;

    @Autowired
    SelectDocumentPopupPage selectDocumentPopupPage;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @RenderWebElement
    @FindBy (css = "button[id$='submit-button']")
    protected WebElement saveButton;

    @FindBy (css = "button[id$='cancel-button']")
    protected WebElement cancelButton;

    protected String fieldLocator = "div[class*='form-field'] [id*='%s']";
    protected String dropDownLocator = "select[id*='%s']";
    protected String selectAttachmentButtonLocator = "div[id*='attachments-cntrl-itemGroupActions'] button";
    protected String selectAssignedToButtonLocator = "div[id*='cntrl-itemGroupActions'] button";

    public enum ContactListFields
    {
        FirstName, LastName, Email, Company, JobTitle, PhoneOffice, PhoneMobile, Notes
    }

    public enum EventAgendaFields
    {
        Reference
            {
                public String toString()
                {
                    return "eventAgendaRef";
                }
            },

        StartTime
            {
                public String toString()
                {
                    return "eventAgendaStartTime";
                }
            },

        EndTime
            {
                public String toString()
                {
                    return "eventAgendaEndTime";
                }
            },

        SessionName
            {
                public String toString()
                {
                    return "eventAgendaSessionName";
                }
            },

        Presenter
            {
                public String toString()
                {
                    return "eventAgendaPresenter";
                }
            },

        Audience
            {
                public String toString()
                {
                    return "eventAgendaAudience";
                }
            },

        Notes
            {
                public String toString()
                {
                    return "eventAgendaNotes";
                }
            }
    }

    public enum EventListFields
    {
        Title
            {
                public String toString()
                {
                    return "cm_title";
                }
            },

        Description
            {
                public String toString()
                {
                    return "cm_description";
                }
            },

        Location
            {
                public String toString()
                {
                    return "eventLocation";
                }
            },

        StartDate
            {
                public String toString()
                {
                    return "eventStartDate-cntrl-date";
                }
            },

        StartTime
            {
                public String toString()
                {
                    return "eventStartDate-cntrl-time";
                }
            },

        EndDate
            {
                public String toString()
                {
                    return "eventEndDate-cntrl-date";
                }
            },

        EndTime
            {
                public String toString()
                {
                    return "eventEndDate-cntrl-time";
                }
            },

        Registrations
            {
                public String toString()
                {
                    return "eventRegistrations";
                }
            },

        Notes
            {
                public String toString()
                {
                    return "eventNote";
                }
            }
    }

    public enum IssueFields
    {
        Id
            {
                public String toString()
                {
                    return "issueID";
                }
            },

        Title
            {
                public String toString()
                {
                    return "cm_title";
                }
            },

        Description
            {
                public String toString()
                {
                    return "cm_description";
                }
            },

        DueDate
            {
                public String toString()
                {
                    return "issueDueDate-cntrl-date";
                }
            },

        Comments
            {
                public String toString()
                {
                    return "issueComments";
                }
            }
    }

    public enum LocationFields
    {
        Title
            {
                public String toString()
                {
                    return "cm_title";
                }
            },

        AddressLine1
            {
                public String toString()
                {
                    return "locationAddress1";
                }
            },

        AddressLine2
            {
                public String toString()
                {
                    return "locationAddress2";
                }
            },

        AddressLine3
            {
                public String toString()
                {
                    return "locationAddress3";
                }
            },

        ZipCode
            {
                public String toString()
                {
                    return "locationZip";
                }
            },

        State
            {
                public String toString()
                {
                    return "locationState";
                }
            },

        Country
            {
                public String toString()
                {
                    return "locationCountry";
                }
            },

        Description
            {
                public String toString()
                {
                    return "cm_description";
                }
            }
    }

    public enum MeetingAgendaFields
    {
        Reference
            {
                public String toString()
                {
                    return "meetingAgendaRef";
                }
            },

        Title
            {
                public String toString()
                {
                    return "cm_title";
                }
            },

        Description
            {
                public String toString()
                {
                    return "cm_description";
                }
            },

        Time
            {
                public String toString()
                {
                    return "meetingAgendaTime";
                }
            },

        Owner
            {
                public String toString()
                {
                    return "meetingAgendaOwner";
                }
            }
    }

    public enum AdvancedTaskAgendaFields
    {
        Title
            {
                public String toString()
                {
                    return "cm_title";
                }
            },

        Description
            {
                public String toString()
                {
                    return "cm_description";
                }
            },

        StartDate
            {
                public String toString()
                {
                    return "ganttStartDate-cntrl-date";
                }
            },

        EndDate
            {
                public String toString()
                {
                    return "ganttEndDate-cntrl-date";
                }
            },

        Complete
            {
                public String toString()
                {
                    return "ganttPercentComplete";
                }
            },

        Comments
            {
                public String toString()
                {
                    return "taskComments";
                }
            }
    }

    public enum SimpleTaskAgendaFields
    {
        Title
            {
                public String toString()
                {
                    return "cm_title";
                }
            },

        Description
            {
                public String toString()
                {
                    return "cm_description";
                }
            },

        DueDate
            {
                public String toString()
                {
                    return "simpletaskDueDate-cntrl-date";
                }
            },

        Comments
            {
                public String toString()
                {
                    return "simpletaskComments";
                }
            }
    }

    public enum ToDoAgendaFields
    {
        Title
            {
                public String toString()
                {
                    return "todoTitle";
                }
            },

        DueDate
            {
                public String toString()
                {
                    return "todoDueDate-cntrl-date";
                }
            },

        DueTime
            {
                public String toString()
                {
                    return "todoDueDate-cntrl-time";
                }
            },

        Priority
            {
                public String toString()
                {
                    return "todoPriority";
                }
            },

        Notes
            {
                public String toString()
                {
                    return "todoNotes";
                }
            }
    }

    public enum VisitorAgendaFields
    {
        Email
            {
                public String toString()
                {
                    return "visitorEmail";
                }
            },

        FeedbackType
            {
                public String toString()
                {
                    return "feedbackType";
                }
            },

        FeedbackSubject
            {
                public String toString()
                {
                    return "feedbackSubject";
                }
            },

        FeedbackComment
            {
                public String toString()
                {
                    return "feedbackComment";
                }
            },

        Rating
            {
                public String toString()
                {
                    return "rating";
                }
            },

        VisitorName
            {
                public String toString()
                {
                    return "visitorName";
                }
            },

        VisitorWebsite
            {
                public String toString()
                {
                    return "visitorWebsite";
                }
            }
    }

    public enum DropDownLists
    {
        todoStatus, simpletaskStatus, simpletaskPriority, taskStatus, taskPriority, issueStatus, issuePriority
    }

    public void addContent(String field, String content)
    {
        browser.waitUntilElementVisible(By.cssSelector(String.format(fieldLocator, field)));
        browser.findElement(By.cssSelector(String.format(fieldLocator, field))).sendKeys(content);
    }

    /**
     * Remove attachment
     *
     * @param filename
     */
    public void removeAttachments(String filename)
    {
        browser.findElement(By.cssSelector(selectAttachmentButtonLocator)).click();
        selectDocumentPopupPage.renderedPage();
        selectDocumentPopupPage.clickRemoveIcon(filename);
        selectDocumentPopupPage.clickOkButton();
    }

    public void addAttachments(String folderName, String fileName)
    {
        if (folderName != null)
        {
            browser.findElement(By.cssSelector(selectAttachmentButtonLocator)).click();
//            selectDocumentPopupPage.renderedPage();
            selectDocumentPopupPage.clickItem(folderName);
            selectDocumentPopupPage.clickAddIcon(fileName);
            selectDocumentPopupPage.clickOkButton();
        }
    }

    /**
     * Select document from 'Select' pop up -> 'Document Library' folder
     *
     * @param fileName to be added
     */
    public void addAttachmentFromDocumentLibrary(String fileName)
    {
        browser.findElement(By.cssSelector(selectAttachmentButtonLocator)).click();
        selectDocumentPopupPage.renderedPage();
        selectDocumentPopupPage.clickAddIcon(fileName);
        selectDocumentPopupPage.clickOkButton();
    }

    public void addAssignedTo(String userName)
    {
        if (userName != null)
        {
            browser.findElement(By.cssSelector(selectAssignedToButtonLocator)).click();
            selectPopUpPage.renderedPage();
            selectPopUpPage.search(userName);
            selectPopUpPage.clickAddIcon(userName);
            selectPopUpPage.clickOkButton();
        }
    }

    public void addAssignedToAdvancedTask(String userName)
    {
        if (userName != null)
        {
            browser.findElement(By.cssSelector(selectAssignedToButtonLocator)).click();
            selectPopUpPage.renderedPage();
            selectPopUpPage.search(userName);
            selectPopUpPage.clickAddIcon(userName);
            selectPopUpPage.clickOkButton();
        }
    }

    public void addAssignedToToDo(String userName)
    {
        if (userName != null)
        {
            browser.findElement(By.cssSelector(selectAssignedToButtonLocator)).click();
            selectPopUpPage.renderedPage();
            selectPopUpPage.search(userName);
            selectPopUpPage.clickAddIcon(userName);
            selectPopUpPage.clickOkButton();
        }
    }

    /**
     * Select
     *
     * @param item         value of option
     * @param dropDownList dropdown id
     */
    public void selectDropDownItem(String item, String dropDownList)
    {
        if (item != null)
        {
            browser.waitUntilElementsVisible(By.cssSelector("option"));
            Select dropdown = new Select(browser.findElement(By.cssSelector(String.format(dropDownLocator, dropDownList))));
            dropdown.selectByValue(item);
        }
    }

    public DataListsPage clickCancel()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(cancelButton, 5);
        cancelButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public DataListsPage clickSave()
    {
        saveButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public DataListsPage clickCloseButton()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(closeButton, 5);
        closeButton.click();
        return (DataListsPage) dataListsPage.renderedPage();
    }

    public void fillCreateNewContactItem(List<String> fieldsValue)
    {
        int i = 0;
        for (ContactListFields field : ContactListFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
    }

    public void fillCreateNewEventAgendaItem(List<String> fieldsValue)
    {
        int i = 0;
        for (EventAgendaFields field : EventAgendaFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
    }

    public void fillCreateNewEventItem(List<String> fieldsValue, String folder, String file)
    {
        int i = 0;
        for (EventListFields field : EventListFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        addAttachments(folder, file);
    }

    public void fillCreateNewIssueItem(List<String> fieldsValue, String folder, String file, String userName, String status, String priority)
    {
        int i = 0;
        for (IssueFields field : IssueFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        addAttachments(folder, file);
        getBrowser().waitInSeconds(5);
        addAssignedTo(userName);
        selectDropDownItem(status, DropDownLists.issueStatus.toString());
        selectDropDownItem(priority, DropDownLists.issuePriority.toString());
    }

    public void fillCreateNewLocationItem(List<String> fieldsValue, String folder, String file)
    {
        int i = 0;
        for (LocationFields field : LocationFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        addAttachments(folder, file);
    }

    public void fillCreateNewMeetingAgendaItem(List<String> fieldsValue, String folder, String file)
    {
        int i = 0;
        for (MeetingAgendaFields field : MeetingAgendaFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        addAttachments(folder, file);
    }

    public void fillCreateNewAdvancedTaskItem(List<String> fieldsValue, String folder, String file, String userName, String status, String priority)
    {
        int i = 0;
        for (AdvancedTaskAgendaFields field : AdvancedTaskAgendaFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        addAttachments(folder, file);
        addAssignedToAdvancedTask(userName);
        selectDropDownItem(status, DropDownLists.taskStatus.toString());
        selectDropDownItem(priority, DropDownLists.taskPriority.toString());
    }

    public void fillCreateNewSimpleTaskItem(List<String> fieldsValue, String status, String priority)
    {
        int i = 0;
        for (SimpleTaskAgendaFields field : SimpleTaskAgendaFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        selectDropDownItem(status, DropDownLists.simpletaskStatus.toString());
        selectDropDownItem(priority, DropDownLists.simpletaskPriority.toString());
    }

    public void fillCreateNewToDoItem(List<String> fieldsValue, String folder, String file, String userName, String status)
    {
        int i = 0;
        for (ToDoAgendaFields field : ToDoAgendaFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        addAttachments(folder, file);
        getBrowser().waitInSeconds(5);
        addAssignedToToDo(userName);
        selectDropDownItem(status, DropDownLists.todoStatus.toString());
    }

    public void fillCreateNewVisitorItem(List<String> fieldsValue)
    {
        int i = 0;
        for (VisitorAgendaFields field : VisitorAgendaFields.values())
        {
            addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
    }

}