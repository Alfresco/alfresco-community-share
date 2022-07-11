package org.alfresco.po.share.site.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.alfresco.utility.web.annotation.PageObject;
import org.joda.time.DateTime;

@PageObject
public class CalendarUtility
{
    /**
     * Method to generate a random Date (day) for the current Month
     */

    public static String generateRandomDateTime(int max)
    {
        int min = 1;
        Random r = new Random();
        int randomNumber = r.nextInt((max - min) + 1) + min;
        return String.valueOf(randomNumber);
    }

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
        return new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
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

        return calendar.getTime();
    }

    /**
     * Method to get the mid date of the current month
     *
     * @return
     */
    public Calendar midDateOfTheMonth()
    {
        Calendar calendar = Calendar.getInstance();
        int midDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) / 2;
        calendar.set(Calendar.DAY_OF_MONTH, midDay);

        return calendar;
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

        return calendar.getTime();
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

        return calendar.getTime();
    }

    /**
     * Method to get the value of the next day (date)
     *
     * @return
     */
    public static Date tomorrow()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);

        return calendar.getTime();
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

        return calendar.getTime();
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

        return calendar.getTime();
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
        return calendar.getTime();
    }

    /**
     * Method to get the value of the month for the next week
     */
    public int monthOfNextWeek()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        String monthOfNextWeekS = new SimpleDateFormat("M").format(calendar.getTime());
        return Integer.parseInt(monthOfNextWeekS);
    }

    /**
     * Method to check if he current day is in the first week of the month
     */

    public int currentDayWeek()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * Method to get the value of the current month
     *
     * @return
     */

    public int currentMonth()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
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
        return Integer.parseInt(currentYearS);
    }

    /**
     * Method to get the value of the year for next week
     */
    public int nextWeeksYear()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        String nextWeeksYearS = new SimpleDateFormat("yyyy").format(calendar.getTime());
        return Integer.parseInt(nextWeeksYearS);
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
        return today.minusDays(1).toDate();
    }

    /**
     * Get the date of a random day of last week
     *
     * @return
     */
    public Date randomDayOfLastWeek()
    {
        DateTime today = new DateTime();
        return today.minusDays(7).toDate();
    }

    /**
     * Get the date of the first day of the current Month
     *
     * @return
     */
    public Date firstDayOfCurrentMonth()
    {
        return new DateTime().dayOfMonth().withMinimumValue().toDate();
    }

    /**
     * Get date for day from previous month
     *
     * @return
     */

    public Date dayFromPreviousMonth()
    {
        DateTime today = new DateTime();
        return today.minusMonths(1).toDate();
    }

    /**
     * Get the date of the first day of the current week
     *
     * @return
     */
    public String firstDayOfCurrentWeek()
    {
        return new DateTime().dayOfWeek().withMinimumValue().toString("d MMMM yyyy");
    }

    /**
     * Determine the month of yesterday
     *
     * @return
     */
    public int monthOfYesterday()
    {
        DateTime today = new DateTime();
        return today.minusDays(1).getMonthOfYear();
    }

    public String currentMonthReference()

    {
        Locale local = Locale.UK;
        Calendar calendar = Calendar.getInstance(local);
        return new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.getTime());

    }

    public String firstDayOfLastWeek()
    {
        DateTime today = new DateTime();
        return today.minusWeeks(1).dayOfWeek().withMinimumValue().toString("d MMMM yyyy");
    }

    public String currentDay()
    {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("E, dd, MMMM yyyy", Locale.ENGLISH).format(calendar.getTime());
    }
}
