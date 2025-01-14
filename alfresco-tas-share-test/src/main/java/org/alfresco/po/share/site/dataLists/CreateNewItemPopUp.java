package org.alfresco.po.share.site.dataLists;

import java.util.List;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class CreateNewItemPopUp extends BaseDialogComponent
{
    private final By saveButton = By.cssSelector("button[id$='submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    protected final String fieldLocator = "div[class*='form-field'] [id*='%s']";
    private final String dropDownLocator = "select[id*='%s']";
    private final String selectAttachmentButtonLocator = "div[id*='attachments-cntrl-itemGroupActions'] button";
    private final String selectAttachmentFolder = "h3.item-name a";
    private final String selectAssignedToButtonLocator = "div[id*='cntrl-itemGroupActions'] button";
    private final By selectAttachmentButton = By.cssSelector("div[id*='attachments-cntrl-itemGroupActions'] button");

    public CreateNewItemPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void addContent(String field, String content)
    {
        WebElement fieldElement = waitUntilElementIsVisible(By.cssSelector(String.format(fieldLocator, field)));
        fieldElement.clear();
        fieldElement.sendKeys(content);
    }

    public void addAttachments(String folderName, String fileName)
    {
        if (folderName != null)
        {
            clickElement(By.cssSelector(selectAttachmentButtonLocator));
            SelectDocumentPopupPage selectDocumentPopupPage = new SelectDocumentPopupPage(webDriver);
            selectDocumentPopupPage.clickItem(folderName);
            selectDocumentPopupPage.clickAddIcon(fileName);
            selectDocumentPopupPage.clickOkButton();
        }
    }

    public void addAttachment(String folderName, String fileName)
    {
        if (folderName != null)
        {
            clickElement(By.cssSelector(selectAttachmentButtonLocator));
            SelectDocumentPopupPage selectDocumentPopupPage = new SelectDocumentPopupPage(webDriver);
            mouseOver(By.cssSelector(selectAttachmentFolder));
            clickElement(By.cssSelector(selectAttachmentFolder));
            selectDocumentPopupPage.clickAddIcon(fileName);
            selectDocumentPopupPage.clickOkButton();
        }
    }

    public void addAttachmentFromDocumentLibrary(String fileName)
    {
        clickElement(By.cssSelector(selectAttachmentButtonLocator));
        SelectDocumentPopupPage selectDocumentPopupPage = new SelectDocumentPopupPage(webDriver);
        selectDocumentPopupPage.clickAddIcon(fileName);
        selectDocumentPopupPage.clickOkButton();
    }

    public void removeAttachments(String fileName)
    {
        clickElement(By.cssSelector(selectAttachmentButtonLocator));
        SelectDocumentPopupPage selectDocumentPopupPage = new SelectDocumentPopupPage(webDriver);
        selectDocumentPopupPage.clickRemoveIcon(fileName);
        selectDocumentPopupPage.clickOkButton();
    }

    public void addAssignedTo(String userName)
    {
        if (userName != null)
        {
            clickElement(By.cssSelector(selectAssignedToButtonLocator));
            SelectPopUpPage selectPopUpPage = new SelectPopUpPage(webDriver);
            selectPopUpPage.search(userName);
            selectPopUpPage.clickAddIcon(userName);
            selectPopUpPage.clickOkButton();
        }
    }

    public void addAssignedToAdvancedTask(String userName)
    {
        if (userName != null)
        {
            clickElement(By.cssSelector(selectAssignedToButtonLocator));
            SelectPopUpPage selectPopUpPage = new SelectPopUpPage(webDriver);
            selectPopUpPage.search(userName);
            selectPopUpPage.clickAddIcon(userName);
            selectPopUpPage.clickOkButton();
        }
    }

    public void addAssignedToToDo(String userName)
    {
        if (userName != null)
        {
            clickElement(By.cssSelector(selectAssignedToButtonLocator));
            SelectPopUpPage selectPopUpPage = new SelectPopUpPage(webDriver);
            selectPopUpPage.search(userName);
            selectPopUpPage.clickAddIcon(userName);
            selectPopUpPage.clickOkButton();
        }
    }

    public void selectDropDownItem(String item, String dropDownList)
    {
        if (item != null)
        {
            waitUntilElementsAreVisible(By.cssSelector("option"));
            Select dropdown = new Select(findElement(By.cssSelector(String.format(dropDownLocator, dropDownList))));
            dropdown.selectByValue(item);
        }
    }

    public void clickCancel()
    {
        clickElement(cancelButton);
    }

    public void clickSave()
    {
        clickElement(saveButton);
    }

    public void clickCloseButton()
    {
        clickElement(closeButton);
    }

    public void fillCreateNewContactItem(List<String> fieldsValue)
    {
        fillCreateNewItemPopupFields(ContactListFields.class, fieldsValue);
    }

    public void fillCreateNewEventAgendaItem(List<String> fieldsValue)
    {
        fillCreateNewItemPopupFields(EventAgendaFields.class, fieldsValue);
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Event List' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     * @param folder      - list of strings. Each input field needs his own string in this list;
     * @param file        - file name (from the previous folder) that will be attached;
     */
    public void fillCreateNewEventItem(List<String> fieldsValue, String folder, String file)
    {
        fillCreateNewItemPopupFields(EventListFields.class, fieldsValue);
        addAttachment(folder, file);
    }

    public void fillCreateNewEventItemFields(List<String> fieldsValue, String file )
    {
        fillCreateNewItemPopupFields(EventListFields.class, fieldsValue);
        addAttachmentFromDocumentLibrary(file);
    }

    public void fillCreateNewIssueListItemFields(List<String> fieldsValue, String file, String ItemStatus, String ItemPriority, String username )
    {
        fillCreateNewItemPopupFields(IssueFields.class, fieldsValue);
        selectDropDownItem(ItemStatus, "issueStatus");
        selectDropDownItem(ItemPriority, "issuePriority");
        addAttachmentFromDocumentLibrary(file);
        addAssignedTo(username);
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Issue List' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     * @param folder      - list of strings. Each input field needs his own string in this list;
     * @param file        - file name (from the previous folder) that will be attached;
     * @param userName    - the name of the user to be assigned;
     * @param status      - the value from 'Status' dropdown;
     * @param priority    - the value from 'Priority' dropdown;
     */
    public void fillCreateNewIssueItem(List<String> fieldsValue, String folder, String file, String userName, String status, String priority)
    {
        fillCreateNewItemPopupFields(IssueFields.class, fieldsValue);
        addAttachment(folder, file);
        addAssignedTo(userName);
        selectDropDownItem(status, DropDownLists.issueStatus.toString());
        selectDropDownItem(priority, DropDownLists.issuePriority.toString());
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Meeting Agenda' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     * @param folder      - folder name from where will be selected a 'file' to be attached;
     * @param file        - file name (from the previous folder) that will be attached;
     */
    public void fillCreateNewLocationItem(List<String> fieldsValue, String folder, String file)
    {
        fillCreateNewItemPopupFields(LocationFields.class, fieldsValue);
        addAttachment(folder, file);
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Meeting Agenda' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     * @param folder      - folder name from where will be selected a 'file' to be attached;
     * @param file        - file name (from the previous folder) that will be attached;
     */
    public void fillCreateNewMeetingAgendaItem(List<String> fieldsValue, String folder, String file)
    {
        fillCreateNewItemPopupFields(MeetingAgendaFields.class, fieldsValue);
        addAttachment(folder, file);
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Advanced Task' type.
     *
     * @param fieldsValue - ist of strings. Each input field needs his own string in this list;
     * @param folder      - folder name from where will be selected a 'file' to be attached;
     * @param file        - file name (from the previous folder) that will be attached;
     * @param userName    - the name of the user to be assigned;
     * @param status      - the value from 'Status' dropdown;
     * @param priority    - the value from 'Priority' dropdown;
     */
    public void fillCreateNewAdvancedTaskItem(List<String> fieldsValue, String folder, String file, String userName, String status, String priority)
    {
        fillCreateNewItemPopupFields(AdvancedTaskAgendaFields.class, fieldsValue);
        addAttachment(folder, file);
        addAssignedToAdvancedTask(userName);
        selectDropDownItem(status, DropDownLists.taskStatus.toString());
        selectDropDownItem(priority, DropDownLists.taskPriority.toString());
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Simple Task' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     * @param status      - the value from 'Status' dropdown;
     * @param priority    - the value from 'Priority' dropdown;
     */
    public void fillCreateNewSimpleTaskItem(List<String> fieldsValue, String status, String priority)
    {
        fillCreateNewItemPopupFields(SimpleTaskAgendaFields.class, fieldsValue);
        selectDropDownItem(status, DropDownLists.simpletaskStatus.toString());
        selectDropDownItem(priority, DropDownLists.simpletaskPriority.toString());
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'To Do List' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     * @param folder      - folder name from where will be selected a 'file' to be attached;
     * @param file        - file name (from the previous folder) that will be attached;
     * @param userName    - the name of the user to be assigned;
     * @param status      - the value from 'Status' dropdown;
     */
    public void fillCreateNewToDoItem(List<String> fieldsValue, String folder, String file, String userName, String status)
    {
        fillCreateNewItemPopupFields(ToDoAgendaFields.class, fieldsValue);
        addAttachment(folder, file);
        addAssignedToToDo(userName);
        selectDropDownItem(status, DropDownLists.todoStatus.toString());
    }

    /**
     * Method used to fill the fields from 'Create New Item' popup for 'Visitor List' type.
     *
     * @param fieldsValue - list of strings. Each input field needs his own string in this list;
     */
    public void fillCreateNewVisitorItem(List<String> fieldsValue)
    {
        fillCreateNewItemPopupFields(VisitorAgendaFields.class, fieldsValue);
    }

    /**
     * Method that fills all the fields from 'Create New Item' popup.
     *
     * @param enumData     - the Enum name of the Data List type used, from where the WebElement will take his id;
     * @param textToInsert - the text that will be inserted in the field;
     * @param <E>          - the type of class; in this case, Enum.
     */
    private <E extends Enum<E>> void fillCreateNewItemPopupFields(Class<E> enumData, List<String> textToInsert)
    {
        int i = 0;
        for (Enum<E> enumValue : enumData.getEnumConstants())
        {
            addContent(enumValue.toString(), textToInsert.get(i));
            i++;
        }
    }

    /**
     * Checking if *all* the fields from 'Create New Item' popup are displayed.
     *
     * @param enumData   - the enum name from where will be extracted information
     * @param webElement - an WebElement that receive every String from the given enum in his css/xpath Selector;
     *                   - it is pointing to every inputText from the form, depending on what enum String is receiving;
     * @param <E>        - the Class type that here is Enum
     * @return - true if all the fields are displayed.
     * - false if at least one of them is missing.
     */
    private <E extends Enum<E>> boolean areNewItemFieldsDisplayed(Class<E> enumData, String webElement)
    {
        boolean areAllFieldsDisplayed = true;

        for (Enum<E> enumValue : enumData.getEnumConstants())
        {
            WebElement formField = findElement(By.cssSelector(String.format(webElement, enumValue)));

            areAllFieldsDisplayed = areAllFieldsDisplayed && isElementDisplayed(formField);
        }
        return areAllFieldsDisplayed;
    }

    /**
     * Checking if all the fields from 'Create New Item' popup for 'Event Agenda' are displayed.
     *
     * @return - true if all the fields are displayed
     * - false if at least one of them is missing.
     */
    public boolean areNewItemEventAgendaFieldsDisplayed()
    {
        return areNewItemFieldsDisplayed(EventAgendaFields.class, fieldLocator);
    }

    /**
     * Checking if *all* the fields from 'Create New Item' popup for 'Meeting Agenda' are displayed.
     *
     * @return - true if all the fields are displayed
     * - false if at least one of them is missing.
     */
    public boolean areNewItemMeetingAgendaFieldsDisplayed()
    {
        return areNewItemFieldsDisplayed(MeetingAgendaFields.class, fieldLocator);
    }

    /**
     * Checking if *all* the fields from 'Create New Item' popup are filled with the given String.
     *
     * @param searchedText - the String that should be displayed in all the inputs.
     * @param webElement   - an WebElement that receive every String from the given enum in his css/xpath Selector;
     *                     - it is pointing to every inputText from the form, depending on what enum String is receiving;
     * @param <E>          - the Class type that here is Enum
     * @return - true if the String is present in all the fields;
     * - false if at least one field is not having the expected String.
     */
    private <E extends Enum<E>> boolean areAllNewItemFieldsFilledWithSpecificString(Class<E> enumData, String webElement, String searchedText)
    {
        boolean areFieldsFilled = true;

        for (Enum<E> enumValue : enumData.getEnumConstants())
        {
            String fieldText = findElement(By.cssSelector(String.format(webElement, enumValue))).getAttribute("value");

            areFieldsFilled = areFieldsFilled && fieldText.equals(searchedText);

            if (!fieldText.equals(searchedText))
            {
                System.out.println("The field '" + enumValue.name() + "' is not filled with expected '" + searchedText + "' string in 'New Item' popup.");
            }
        }
        return areFieldsFilled;
    }

    /**
     * Checking if *all* the fields from 'Create New Item' popup for 'Event Agenda' are filled with the given String.
     *
     * @param insertedText - the String that should be displayed in all the inputs.
     * @return - true if the String is present in all the fields;
     * - false if at least one field is not having the expected String.
     */
    public boolean areAllEventAgendaFieldsFilledWithSpecificString(String insertedText)
    {
        return areAllNewItemFieldsFilledWithSpecificString(EventAgendaFields.class, fieldLocator, insertedText);
    }

    /**
     * Checking if *all* the fields from 'Create New Item' popup for 'Meeting Agenda' are filled with the given String.
     *
     * @param insertedText - the String that should be displayed in all the inputs.
     * @return - true if the String is present in all the fields;
     * - false if at least one field is not having the expected String.
     */
    public boolean areAllMeetingAgendaFieldsFilledWithSpecificString(String insertedText)
    {
        return areAllNewItemFieldsFilledWithSpecificString(MeetingAgendaFields.class, fieldLocator, insertedText);
    }

    /**
     * Checking if in 'Create New Event Agenda Item' popup is a 'Select' button present.
     *
     * @return true if the button is displayed, else false.
     */
    public boolean isSelectAttachmentButtonLocatorDisplayed()
    {
        return isElementDisplayed(selectAttachmentButton);
    }

    /**
     * Checking if in 'Create New Item' popup is 'Save' button present.
     *
     * @return true if the button is displayed, else false.
     */
    public boolean isSaveButtonDisplayed()
    {
        return isElementDisplayed(saveButton);
    }

    /**
     * Checking if in 'Create New Item' popup is 'Cancel' button present.
     *
     * @return true if the button is displayed, else false.
     */
    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(cancelButton);
    }


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


    public enum DropDownLists
    {
        todoStatus, simpletaskStatus, simpletaskPriority, taskStatus, taskPriority, issueStatus, issuePriority
    }


    public enum NewItemPopupForm
    {
        ContactList("contact"),
        EventAgenda("eventAgenda"),
        EventList("event"),
        IssueList("issue"),
        LocationList("location"),
        MeetingAgenda("meetingAgenda"),
        TaskListAdvanced("task"),
        TaskListSimple("simpletask"),
        ToDoList("todoList");

        public final String name;

        NewItemPopupForm(String name)
        {
            this.name = name;
        }

    }
}