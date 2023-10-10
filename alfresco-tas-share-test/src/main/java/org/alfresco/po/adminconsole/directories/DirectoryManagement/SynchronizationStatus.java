package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;

/**
 * Created by Claudia Agache on 8/24/2017.
 * This class is modeling the "Status" table found on /alfresco/s/enterprise/admin/admin-directorymanagement when Status link is clicked
 */
public class SynchronizationStatus
{
    WebBrowser browser;
    private List<WebElement> rowInfo;

    public SynchronizationStatus(List<WebElement> rowInfo, WebBrowser browser)
    {
        this.rowInfo = rowInfo;
        this.browser = browser;
    }

    public String getBeanName()
    {
        return rowInfo.get(0).getText();
    }

    public String getTime()
    {
        return rowInfo.get(1).getText();
    }

    public String getEndTime() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ;
        Date endDate = sdf.parse(getTime().split(": ")[2]);
        return sdf2.format(endDate);
    }

    public String getStartTime() throws ParseException
    {
        String text = getTime().split(": ")[1];
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ;
        Date startDate = sdf.parse(text.substring(0, text.indexOf("\n")));
        return sdf2.format(startDate);
    }

    public String getSuccessfulFailed()
    {
        return rowInfo.get(2).getText();
    }

    public String getPercentCompleteAndTotalResults()
    {
        return rowInfo.get(3).getText();
    }

    public int getSuccessfulValue()
    {
        String text = getSuccessfulFailed().split(":")[1];
        return Integer.valueOf(text.substring(1, text.indexOf("F") - 1));
    }

    public int getFailedValue()
    {
        return Integer.parseInt(getSuccessfulFailed().split(": ")[2]);
    }

    public int getPercentage()
    {
        String text = getPercentCompleteAndTotalResults().split(":")[1];
        return Integer.valueOf(text.substring(1, text.indexOf("%")));
    }

    public int getTotalResults()
    {
        return Integer.parseInt(getPercentCompleteAndTotalResults().split(": ")[2]);
    }

    public enum BeanNames
    {
        groupAnalysis("1 Group Analysis"),
        groupCreation("3 Group Creation and Association Deletion"),
        userCreation("6 User Creation and Association"),
        authorityDeletion("7 Authority Deletion");

        private String name;

        BeanNames(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }
}
