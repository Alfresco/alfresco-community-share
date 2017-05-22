package org.alfresco.common;

import org.alfresco.dataprep.UserService;
import org.alfresco.utility.web.common.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
