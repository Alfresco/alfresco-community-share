package org.alfresco.po.share.dashlet;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SiteActivitiesDashlet extends MyActivitiesDashlet
{
    @FindAll (@FindBy (css = ".content .detail"))
    List<WebElement> activityLinks;
    @RenderWebElement
    @FindBy (css = ".time.relativeTime")
    private List<WebElement> relativeTime;
    private LocalDate dateToCompare;

    public void setDateToCompare(String dateString)
    {
        this.dateToCompare = LocalDate.parse(dateString);
    }

    /**
     * Verifies that each activity has the relativeTime according to the selected filter
     *
     * @param noOfDays       filter chosen
     * @param dateForCompare used to compare against relativeTime. If set to "now" current date is used
     * @return true if 'relativeTime' is before 'dateForCompare', false otherwise
     */
    public boolean isTimeRangeAccurateForAllActivities(int noOfDays, String dateForCompare)
    {
        browser.waitInSeconds(2);

        int counter = 0;

        if (dateForCompare.equals("now"))
            dateToCompare = LocalDate.now();
        else
            setDateToCompare(dateForCompare);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (WebElement aRelativeTime : relativeTime)
        {
            String substr = aRelativeTime.getAttribute("title");
            String date = substr.substring(0, substr.indexOf("T"));
            LocalDate activityDate = LocalDate.parse(date, formatter);
            if (noOfDays <= 28)
                if (DAYS.between(activityDate, dateToCompare) <= noOfDays && MONTHS.between(activityDate, dateToCompare) == 0
                    && YEARS.between(activityDate, dateToCompare) == 0)
                    counter++;
                else if (DAYS.between(activityDate, dateToCompare) <= noOfDays && MONTHS.between(activityDate, dateToCompare) == 1
                    && YEARS.between(activityDate, dateToCompare) == 0)
                    counter++;
        }
        return counter == relativeTime.size();
    }

    @Override
    public Boolean isActivityPresentInActivitiesDashlet(String entry)
    {
        int counter = 0;
        boolean found = false;
        while (!found && counter < 5)
        {
            for (WebElement link : activityLinks)
            {
                if (link.getText().equalsIgnoreCase(entry))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                browser.refresh();
                counter++;
            }
        }
        return found;

    }

}
