package org.alfresco.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.AdvancedTaskAgendaFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ContactListFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.DropDownLists;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventAgendaFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.EventListFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.IssueFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.LocationFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.MeetingAgendaFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.SimpleTaskAgendaFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.ToDoAgendaFields;
import org.alfresco.po.share.site.dataLists.CreateNewItemPopUp.VisitorAgendaFields;
import org.alfresco.utility.web.common.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utility class for generating input data for tests
 * 
 * @author Cristina.Axinte
 */

public class DataUtil
{
    public static String PASSWORD = "password";
    
    @Autowired
    protected UserService userService;
    
    @Autowired
    protected EnvProperties properties;
    
    @Autowired
    protected CreateNewItemPopUp createNewItemPopUp;

    public static String getUniqueIdentifier()
    {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Compare 2 list of objects
     */
    public static boolean areListsEquals(List<?> actualList, String[] expectedList)
    {
        Parameter.checkIsMandotary("Actual list", actualList);
        Parameter.checkIsMandotary("Expected list", expectedList);
        if (actualList.size() != expectedList.length)
        {
            return false;
        }
        return Arrays.equals(actualList.toArray(), expectedList);
    }
    
    public List<UserData> createUsersWithRoles(List<String> usersRoles, String userManager, String siteName)
    {
        List<UserData> users = new ArrayList<>();
        for(String role : usersRoles)
        {
            UserData userData = new UserData("User" + role + DataUtil.getUniqueIdentifier() , role);
            userService.create(properties.getAdminUser(), properties.getAdminPassword(), userData.getUserName(), DataUtil.PASSWORD, userData.getUserName() + "@tests.com", userData.getUserName(), userData.getUserName());
            userService.createSiteMember(userManager, DataUtil.PASSWORD, userData.getUserName(), siteName, userData.getUserRole());
            users.add(userData); 
        }
        return users;
    }
    
    public void fillCreateNewContactItem(List<String> fieldsValue)
    {
        int i=0;
        for(ContactListFields field : ContactListFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
    }
    
    public void fillCreateNewEventAgendaItem(List<String> fieldsValue)
    {
        int i=0;
        for(EventAgendaFields field : EventAgendaFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
    }
    
    public void fillCreateNewEventItem(List<String> fieldsValue, String folder, String file)
    {
        int i=0;
        for(EventListFields field : EventListFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        
        createNewItemPopUp.addAttachments(folder, file);
    }
    
    public void fillCreateNewIssueItem(List<String> fieldsValue, String folder, String file, String userName, String status, String priority)
    {
        int i=0;
        for(IssueFields field : IssueFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        createNewItemPopUp.addAttachments(folder, file);
        createNewItemPopUp.addAssignedTo(userName);
        createNewItemPopUp.selectDropDownItem(status, DropDownLists.issueStatus.toString());
        createNewItemPopUp.selectDropDownItem(priority, DropDownLists.issuePriority.toString());
    }
    
    public void fillCreateNewLocationItem(List<String> fieldsValue, String folder, String file)
    {
        int i=0;
        for(LocationFields field : LocationFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        
        createNewItemPopUp.addAttachments(folder, file);
    }
    
    public void fillCreateNewMeetingAgendaItem(List<String> fieldsValue, String folder, String file)
    {
        int i=0;
        for(MeetingAgendaFields field : MeetingAgendaFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        
        createNewItemPopUp.addAttachments(folder, file);
    }
    
    public void fillCreateNewAdvancedTaskItem(List<String> fieldsValue, String folder, String file, String userName, String status, String priority)
    {
        int i=0;
        for(AdvancedTaskAgendaFields field : AdvancedTaskAgendaFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        
        createNewItemPopUp.addAttachments(folder, file);
        createNewItemPopUp.addAssignedToAdvancedTask(userName);
        createNewItemPopUp.selectDropDownItem(status, DropDownLists.taskStatus.toString());
        createNewItemPopUp.selectDropDownItem(priority, DropDownLists.taskPriority.toString());
    }
    
    public void fillCreateNewSimpleTaskItem(List<String> fieldsValue, String status, String priority)
    {
        int i=0;
        for(SimpleTaskAgendaFields field : SimpleTaskAgendaFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        
        createNewItemPopUp.selectDropDownItem(status, DropDownLists.simpletaskStatus.toString());
        createNewItemPopUp.selectDropDownItem(priority, DropDownLists.simpletaskPriority.toString());
    }
    
    public void fillCreateNewToDoItem(List<String> fieldsValue, String folder, String file, String userName, String status)
    {
        int i=0;
        for(ToDoAgendaFields field : ToDoAgendaFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
        
        createNewItemPopUp.addAttachments(folder, file);
        createNewItemPopUp.addAssignedToToDo(userName);
        createNewItemPopUp.selectDropDownItem(status, DropDownLists.todoStatus.toString());
    }
    
    public void fillCreateNewVisitorItem(List<String> fieldsValue)
    {
        int i=0;
        for(VisitorAgendaFields field : VisitorAgendaFields.values())
        {
            createNewItemPopUp.addContent(field.toString(), fieldsValue.get(i));
            i++;
        }
    }


    public static long parseDate(String date, String dateFormats)
    {
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormats);
            Date parsedDate = simpleDateFormat.parse(date);
            return parsedDate.getTime();
        }
        catch (ParseException e)
        {
            return 0;
        }
    }

    public static String formatDate(Date date, String format)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static boolean areDatesEqual(Date date1, Date date2, long maximumTolerableDifference)
    {
        long max = Math.max(date1.getTime(), date2.getTime());
        long min = Math.min(date1.getTime(), date2.getTime());
        return (max - min) < maximumTolerableDifference;
    }
}
