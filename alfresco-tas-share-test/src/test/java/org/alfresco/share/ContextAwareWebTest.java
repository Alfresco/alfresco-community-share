package org.alfresco.share;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.common.Timeout;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.GroupService;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.LoginPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.Utility;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.AbstractWebTest;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.httpclient.HttpState;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;

/**
 * @author bogdan.bocancea
 */
@ContextConfiguration (classes = ShareTestContext.class)
@Scope (value = "prototype")
public abstract class ContextAwareWebTest extends AbstractWebTest
{
    @Autowired
    public EnvProperties properties;

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

    @Autowired
    public DataUser dataUser;

    @Autowired
    public DataSite dataSite;

    @Autowired
    public DataGroup dataGroup;

    @Autowired
    public CmisWrapper cmisApi;

    @Autowired
    public RestWrapper restApi;

    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;

    protected String adminUser;
    protected String adminPassword;
    protected String adminName;
    protected String domain;
    protected String password = "password";
    protected String mainWindow;

    @BeforeClass (alwaysRun = true)
    public void setup() throws DataPreparationException
    {
        adminUser = properties.getAdminUser();
        adminPassword = properties.getAdminPassword();
        adminName = properties.getAdminName();
        domain = "@test.com";
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
        loginViaCookies(userName, password);
    }

    public void setupAuthenticatedSession(UserModel userModel)
    {
        loginViaCookies(userModel.getUsername(), userModel.getPassword());
    }

    private void loginViaCookies(String userName, String password)
    {
        cleanupAuthenticatedSession();
        HttpState httpState = userService.login(userName, password);
        getBrowser().navigate().to(properties.getShareUrl());
        getBrowser().manage().deleteAllCookies();
        getBrowser().manage().addCookie(new Cookie(httpState.getCookies()[0].getName(), httpState.getCookies()[0].getValue()));
        getBrowser().refresh();
        getBrowser().waitInSeconds(1);
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
        LOG.info(String.format("Navigate to: '%s'", pageUrl));
        try
        {
            getBrowser().navigate().to(properties.getShareUrl().toURI().resolve(pageUrl).toURL());
        }
        catch (URISyntaxException | MalformedURLException me)
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

        File downloadDirectory = new File(testDataFolder);
        File[] directoryContent = downloadDirectory.listFiles();

        for (File aDirectoryContent : directoryContent)
        {
            if (aDirectoryContent.getName().equals(downloadedFile))
            {
                return true;
            }
        }
        return false;
    }

    public FolderModel getUserHomeFolder(UserModel userModel)
    {
        FolderModel userFolder = new FolderModel(userModel.getUsername());
        userFolder.setCmisLocation(Utility.buildPath(cmisApi.getUserHomesPath(), userModel.getUsername()));
        return userFolder;
    }

    public void removeUserFromAlfresco(UserModel user)
    {
        dataUser.usingAdmin().deleteUser(user);
        FolderModel userFolder = new FolderModel(user.getUsername());
        userFolder.setCmisLocation(Utility.buildPath(cmisApi.getUserHomesPath(), user.getUsername()));
        cmisApi.authenticateUser(dataUser.getAdminUser()).usingResource(userFolder).deleteFolderTree();
    }

    @Override
    public String getPageObjectRootPackage()
    {
        return "org/alfresco/po/share";
    }
}