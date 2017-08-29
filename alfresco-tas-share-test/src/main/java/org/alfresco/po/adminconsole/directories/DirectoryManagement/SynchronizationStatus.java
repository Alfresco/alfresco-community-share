package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.alfresco.utility.report.log.Step.STEP;

/**
 * Created by Claudia Agache on 8/24/2017.
 * This class is modeling the "Status" table found on /alfresco/s/enterprise/admin/admin-directorymanagement when Status link is clicked
 */
public class SynchronizationStatus
{
    public enum BeanNames
    {
        groupAnalysis("1 Group Analysis"),
        groupCreation("3 Group Creation and Association Deletion"),
        userCreation("6 User Creation and Association"),
        authorityDeletion("7 Authority Deletion");

        public String getName() {
            return name;
        }

        private String name;

        BeanNames(String name)
        {
            this.name = name;
        }
    }

    private List<WebElement> rowInfo;
    WebBrowser browser;

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
        String text = rowInfo.get(2).getText().split(":")[1];
        return Integer.valueOf(text.substring(1, text.indexOf("F") - 1));
    }

    public int getPercentage()
    {
        String text = rowInfo.get(3).getText().split(":")[1];
        STEP(String.format("Percentage:[%s]", text));
        STEP(String.format("Percentage:[%s]", text.substring(1, text.indexOf("%"))));
        return Integer.valueOf(text.substring(1, text.indexOf("%")));
    }
}
