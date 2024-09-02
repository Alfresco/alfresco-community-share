package org.alfresco.adminconsole;

import java.util.ArrayList;
import org.alfresco.po.adminconsole.directories.DirectoryManagementPages;
import org.alfresco.po.adminconsole.supporttools.NodeBrowserQueryPages;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.exception.TestConfigurationException;
import org.testng.annotations.Test;

public class ConsoleExampleTest extends BaseTest
{
    private NodeBrowserQueryPages nodeBrowserQueryPages;
    private DirectoryManagementPages directoryManagementPages;

    @Test (groups = { "unit" })
    public void testingAdminConsole()
    {
        nodeBrowserQueryPages = new NodeBrowserQueryPages(webDriver);
        nodeBrowserQueryPages.navigate();
    }

    @Test (groups = { "unit" })
    public void testRows() throws TestConfigurationException
    {
        nodeBrowserQueryPages = new NodeBrowserQueryPages(webDriver);
        nodeBrowserQueryPages.navigate();
        nodeBrowserQueryPages.clickSearchAdvancedSettings();
        nodeBrowserQueryPages.setMaxResults(10);
        nodeBrowserQueryPages.setSkipCount(30);
        nodeBrowserQueryPages.clickRootList();
        nodeBrowserQueryPages.getProperties();
        nodeBrowserQueryPages.clickDelete();
        ArrayList<String> expectedAspects = new ArrayList<String>();
        expectedAspects.add("sys:aspect_root");
        expectedAspects.add("sys:jj");
        expectedAspects.add("sys:localized");
        nodeBrowserQueryPages.assertAspectsArePresent(expectedAspects);
    }

    @Test (groups = { "unit" })
    public void testingDirectoryManagement()
    {
        directoryManagementPages = new DirectoryManagementPages(webDriver);
        directoryManagementPages.navigate();
        directoryManagementPages.getAuthenticationDetails();
        directoryManagementPages.setName("Test123");
        directoryManagementPages.clickSynchronizationSettings();
        directoryManagementPages.getAuthenticationDetails().get(0);
    }
}