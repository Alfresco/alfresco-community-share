package org.alfresco.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.WebElement;

/**
 * Utility class for generating input data for tests
 *
 * @author Cristina.Axinte
 */

public class DataUtil
{

    public static final String DATE_FORMAT = "YYYY-MM-dd";

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

    /**
     * Checks if a string is present in a list of WebElements
     *
     * @param toCheck
     * @param list
     * @return isPresent
     */

    public static boolean isStringPresentInWebElementList(String toCheck, List<WebElement> list)
    {
        return list.stream()
                   .filter(element -> element.getText().contains(toCheck))
                   .findFirst()
                   .isPresent();
    }

    public static long parseDate(String date, String dateFormats)
    {
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormats);
            Date parsedDate = simpleDateFormat.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e)
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

    /**
     * Compare an enum with a list.
     * Checking if every element from 'enumData' is present in the list of elements received in 'aList' parameter.
     *
     * @param aList    - a list of elements/ Strings. It wil be populated when is called.
     * @param enumData - the enum from where the information is collected (e.g. from CreateDataListPopUp.DataListTypes, 'title' or 'description' can be collected)
     * @param <E>      - the type of 'enumData', which is 'Enum' type;
     * @return - true if all the elements from the enumData are displayed
     * - false if at least one element is missing
     */
    public static <E extends Enum<E>> boolean isEnumContainedByList(Class<E> enumData, List<String> aList)
    {
        boolean isListComplete = true;

        for (Enum<E> enumVal : enumData.getEnumConstants())
        {
            isListComplete = isListComplete && aList.contains(enumVal.toString());

            if (!aList.contains(enumVal.toString()))
            {
                System.out.println("'" + enumVal.toString() + "' was not found.");
            }
        }
        return isListComplete;
    }
}
