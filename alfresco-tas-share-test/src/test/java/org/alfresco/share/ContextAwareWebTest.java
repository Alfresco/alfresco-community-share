package org.alfresco.share;

import static org.alfresco.common.Utils.testDataFolder;

import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.dataprep.*;
import org.alfresco.po.share.LoginAimsPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.Utility;
import org.alfresco.utility.data.*;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.AbstractWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author bogdan.bocancea
 */
@ContextConfiguration(classes = ShareTestContext.class)
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
    protected DataUserAIS dataUser;

    @Autowired
    protected DataAIS dataAIS;

    @Autowired
    public DataSite dataSite;

    @Autowired
    public DataGroup dataGroup;

    @Autowired
    public DataWorkflow dataWorkflow;

    @Autowired
    public CmisWrapper cmisApi;

    @Autowired
    public RestWrapper restApi;

    //@Autowired
    private LoginPage loginPage; //todo: remove

    //@Autowired
    private LoginAimsPage loginAimsPage;

    @Autowired
    protected DataContent dataContent;
    
   // @Autowired
    protected UserDashboardPage userDashboard;

    //@Autowired
    public Toolbar toolbar;

    public static final GroupModel ALFRESCO_ADMIN_GROUP = new GroupModel("ALFRESCO_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SITE_ADMINISTRATORS = new GroupModel("SITE_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SEARCH_ADMINISTRATORS = new GroupModel("ALFRESCO_SEARCH_ADMINISTRATORS");
    public static String FILE_CONTENT = "Share file content";

    protected String adminUser;
    protected String adminPassword;
    protected String adminName;
    protected String domain;
    protected String password = "password";

    @BeforeClass(alwaysRun = true)
    public void setup() throws DataPreparationException
    {
        adminUser = properties.getAdminUser();
        adminPassword = properties.getAdminPassword();
        adminName = properties.getAdminName();
        domain = "@test.com";
        cleanupAuthenticatedSession();
    }

    public UserModel getAdminUser()
    {
        return dataUser.getAdminUser();
    }

    /**
     * Authenticate user in Share
     *
     * @param userName
     * @param password
     */
    public void setupAuthenticatedSession(String userName, String password)
    {
        loginViaBrowser(userName, password);
    }

    public void setupAuthenticatedSession(UserModel userModel)
    {
        loginViaBrowser(userModel.getUsername(), userModel.getPassword());
    }

    private void loginViaBrowser(String userName, String password)
    {
    	 cleanupAuthenticatedSession();
    	 UserModel validUser = new UserModel(userName, password);
    	 getLoginPage().navigate().login(validUser);
    	 userDashboard.waitForSharePageToLoad();
    }

    /**
     * Cleanup authenticated session with all cookies and logout the user
     */
    public void cleanupAuthenticatedSession()
    {
    	dataUser.logout();
        getBrowser().cleanUpAuthenticatedSession();
    }
    
    protected void logoutViaBrowser()
    {
    	toolbar.clickUserMenu().clickLogout();
    	getBrowser().cleanUpAuthenticatedSession();
    }

    /**
     * Navigate to specific page
     *
     * @param pageUrl
     *            e.g. 'share/page/user/admin/profile'
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

    public CommonLoginPage getLoginPage()
    {
        if (dataAIS.isEnabled())
        {
            return loginAimsPage;
        }

        return loginPage;
    }

    protected boolean isFileInDirectory(String fileName, String extension)
    {
        int retry = 0;
        int seconds = 10;
        if (extension != null)
        {
            fileName = fileName + extension;
        }
        String filePath = testDataFolder + File.separator + fileName;
        while (retry <= seconds && !Files.exists(Paths.get(filePath)))
        {
            retry++;
            Utility.waitToLoopTime(1, String.format("Wait for '%s' to get downloaded", fileName));
        }
        return Files.exists(Paths.get(filePath));
    }

    public FolderModel getUserHomeFolder(UserModel userModel)
    {
        FolderModel userFolder = new FolderModel(userModel.getUsername());
        userFolder.setCmisLocation(Utility.buildPath(cmisApi.getUserHomesPath(), userModel.getUsername()));
        return userFolder;
    }

    public String getDocumentLibraryPath(SiteModel site)
    {
        return Utility.buildPath(String.format("/Sites/%s/documentLibrary", site.getId()));
    }

    public void removeUserFromAlfresco(UserModel... users)
    {
        Arrays.stream(users).forEach(user -> {
            dataUser.usingAdmin().deleteUser(user);
        });
    }

    public void deleteSites(SiteModel... sites)
    {
        Arrays.stream(sites).forEach(site -> dataSite.usingAdmin().deleteSite(site));
    }

    public void assertCurrentUrlContains(String value)
    {
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(value), String.format("%s is displayed in current url", value));
    }

    @Override
    public String getPageObjectRootPackage()
    {
        return "org/alfresco/po/share";
    }
}