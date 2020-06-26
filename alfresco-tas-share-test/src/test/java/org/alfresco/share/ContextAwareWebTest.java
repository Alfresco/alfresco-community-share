package org.alfresco.share;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.Timeout;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.GroupService;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.web.AbstractWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;

/**
 * @author bogdan.bocancea
 */
@ContextConfiguration ("classpath:alfresco-share-po-context.xml")
@Scope (value = "prototype")
public abstract class ContextAwareWebTest extends AbstractWebTest
{
    @Autowired
    protected EnvProperties properties;

    @Autowired
    protected UserService userService;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected DataListsService dataListsService;

    @Autowired
    protected SitePagesService sitePagesService;

    @Autowired
    protected ContentActions contentAction;

    @Autowired
    protected ContentAspects contentAspects;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected Language language;


    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;

    protected String adminUser;
    protected String adminPassword;
    protected String adminName;
    protected String domain;
    protected String password;
    protected String mainWindow;

    @BeforeClass (alwaysRun = true)
    public void setup() throws DataPreparationException
    {
        adminUser = properties.getAdminUser();
        adminPassword = properties.getAdminPassword();
        adminName = properties.getAdminName();
        domain = "@test.com";
        password = "password";
        cleanupAuthenticatedSession();
    }

    /**
     * Just authenticate using <username> and <password> provided as parameters
     * And inject the cookies in current browser
     * use this method in a @BeforeClass to pass the login screen
     *
     * @param userName
     * @param password
     */
    protected void setupAuthenticatedSession(String userName, String password)
    {
        getBrowser().authenticatedSession(userService.login(userName, password));
    }

    /**
     * Cleanup authenticated session with all cookies and logout the user
     */
    protected void cleanupAuthenticatedSession()
    {
        userService.logout();
        getBrowser().cleanUpAuthenticatedSession();
    }

    /**
     * Navigate to specific page
     *
     * @param pageUrl e.g. 'share/page/user/admin/profile'
     */
    protected void navigate(String pageUrl)
    {
        try
        {
            getBrowser().navigate().to(properties.getShareUrl().toURI().resolve(pageUrl).toURL());
        } catch (URISyntaxException | MalformedURLException me)
        {
            throw new RuntimeException("Page url: " + pageUrl + " is invalid");
        }
    }

    /**
     * Switches to the newly created window.
     */
    protected void switchWindow()
    {
        getBrowser().switchWindow();
    }

    /**
     * Closes the newly created win and swithes back to main
     */
    protected void closeWindowAndSwitchBack()
    {
        getBrowser().closeWindowAndSwitchBack();
    }

    protected boolean isFileInDirectory(String fileName, String extension)
    {
        String downloadedFile = fileName;
        if (extension != null)
        {
            // it takes a while to download folders
            getBrowser().waitInSeconds((int) Timeout.SHORT.getTimeoutSeconds());
            downloadedFile = fileName + extension;
        }

        LOG.info("Looking downloaded content in: {}", testDataFolder);
        File downloadDirectory = new File(testDataFolder);
        File[] directoryContent = downloadDirectory.listFiles();

        for (File aDirectoryContent : directoryContent)
        {
            if (aDirectoryContent.getName().equals(downloadedFile))
            {
                return true;
            }
        }

        return false;    }

    @Override
    public String getPageObjectRootPackage()
    {
        return "org/alfresco/po/share";
    }
}