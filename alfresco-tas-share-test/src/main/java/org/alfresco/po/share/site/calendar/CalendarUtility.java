package org.alfresco.po.share.site.calendar;

import java.text.SimpleDateFormat;
import java.util.*;

import org.alfresco.po.annotation.PageObject;
import org.joda.time.DateTime;

@PageObject
public class CalendarUtility
{
    /**
     * Method to get the next Month value
     * 
     * @return String next Month in MMMM yyyy format
     */
    public String refferenceNextMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, +1);
        return new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
    }

    /**
     * Method to get the previous month value
     * 
     * @return String previous Month in MMMM yyyy format
     */

    public String refferencePreviousMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.getTime() );
    }

    /**
     * Method to generate a random Date (day) for the current Month
     */

    public static String generateRandomDateTime(int max)
    {
        int min = 1;
        Random r = new Random();
        int randomNumber =  r.nextInt((max - min) + 1) + min;
        return String.valueOf(randomNumber);
    }

    /**
     * Method to get the value of the first day of the current week
     * 
     * @return Date value for Monday of the current week
     */
    public Date firstDayOfCW()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date firstDay = calendar.getTime();

        return firstDay;
    }

    /**
     * Method to get the value of the first day of the current month
     * 
     * @return
     */

    public Date firstDayOfCM()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dayInMonth = calendar.getTime();

        return dayInMonth;
    }

    /**
     * Method to get the value of the first day of the next month
     * 
     * @return
     */

    public Date firstDayOfNextMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, +1);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date dayInNextMonth = calendar.getTime();

        return dayInNextMonth;
    }

    /**
     * Method to get the value of the next day (date)
     * 
     * @return
     */

    public Date tomorrow()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);

        Date tomorrow = calendar.getTime();

        return tomorrow;
    }

    /**
     * Method to get the value of the date for the day after tomorrow
     * 
     * @return
     */
    public Date dayAfterTomorrow()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +2);

        Date dayAfterTomorrow = calendar.getTime();

        return dayAfterTomorrow;
    }

    /**
     * Method to get a day date value for a day next week
     * 
     * @return
     */

    public Date dayOfNextWeek()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, +1);

        Date dayOfNextWeek = calendar.getTime();

        return dayOfNextWeek;
    }

    /**
     * Method to get the value of the date for the first day of the next week
     * 
     * @return
     */
    public Date firstDayOfNextWeek()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date firstDayOfNextWeek = calendar.getTime();
        return firstDayOfNextWeek;
    }

    /**
     * Method to get the value of the month for the next week
     */
    public int monthOfNextWeek()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        String monthOfNextWeekS = new SimpleDateFormat("M").format(calendar.getTime());
        int monthOfNextWeek = Integer.parseInt(monthOfNextWeekS);
        return monthOfNextWeek;
    }

    /**
     * Method to check if he current day is in the first week of the month
     */

    public int currentDayWeek()
    {
        Calendar calendar = Calendar.getInstance();
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return weekOfMonth;
    }

    /**
     * Method to get the value of the current month
     * 
     * @return
     */

    public int currentMonth()
    {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        return currentMonth;
    }

    /**
     * Method to get the value of the current year
     * 
     * @return
     */

    public int currentYear()
    {
        Calendar calendar = Calendar.getInstance();
        String currentYearS = new SimpleDateFormat("yyyy").format(calendar.getTime());
        int currentYear = Integer.parseInt(currentYearS);
        return currentYear;
    }

    /**
     * Method to get the value of the year for next week
     */
    public int nextWeeksYear()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        String nextWeeksYearS = new SimpleDateFormat("yyyy").format(calendar.getTime());
        int nextWeeksYear = Integer.parseInt(nextWeeksYearS);
        return nextWeeksYear;
    }

    /**
     * Method to get the value of the month after the next month (two months from current month)
     * 
     * @return
     */
    public String monthAfterNextMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, +2);
        return new SimpleDateFormat("MMMM yyyy").format(calendar.getTime());
    }

    /**
     * Get Monday from current week
     *
     * @return
     */
    public String getMondayFromCurrentWeek()
    {
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().toString("dd MMMM yyyy");
    }

    /**
     * Get Thursday from current week
     *
     * @return
     */
    public DateTime getThursdayFromCurrentWeek()
    {
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().plusDays(3);
    }

    /**
     * Get the date of yesterday
     * 
     * @return
     */

    public Date yesterday()
    {
        DateTime today = new DateTime();
        Date yesterday = today.minusDays(1).toDate();
        return yesterday;
    }

    /**
     * Get the date of a random day of last week
     * 
     * @return
     */
    public Date randomDayOfLastWeek()
    {
        DateTime today = new DateTime();
        Date randomDayOfLastWeek = today.minusDays(7).toDate();
        return randomDayOfLastWeek;
    }

    /**
     * Get the date of the first day of the current Month
     * 
     * @return
     */
    public Date firstDayOfCurrentMonth()
    {
        Date firstDayOfCurrentMonth = new DateTime().dayOfMonth().withMinimumValue().toDate();
        return firstDayOfCurrentMonth;
    }

    /**
     * Get date for day from previous month
     * 
     * @return
     */

    public Date dayFromPreviousMonth()
    {
        DateTime today = new DateTime();
        Date dayFromPreviousMonth = today.minusMonths(1).toDate();
        return dayFromPreviousMonth;
    }

    /**
     * Get the date of the first day of the current week
     * 
     * @return
     */
    public String firstDayOfCurrentWeek()
    {
        String firstDayOfCurrentWeek = new DateTime().dayOfWeek().withMinimumValue().toString("d MMMM yyyy");
        return firstDayOfCurrentWeek;
    }

    /**
     * Determine the month of yesterday
     * 
     * @return
     */
    public int monthOfYesterday()
    {
        DateTime today = new DateTime();
        int monthOfYesterday = today.minusDays(1).getMonthOfYear();
        return monthOfYesterday;
    }

    public String currentMonthReference()

    {
        Locale local = Locale.UK;
        Calendar calendar = Calendar.getInstance(local);
        String currentMonthReference = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
        return currentMonthReference;

    }

    public String firstDayOfLastWeek()
    {
        DateTime today = new DateTime();
        String firstDayOfLastWeek = today.minusWeeks(1).dayOfWeek().withMinimumValue().toString("d MMMM yyyy");
        return firstDayOfLastWeek;
    }

    public String currentDay()
    {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("E, dd, MMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
    }
}
