package org.alfresco.share;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.dataprep.AlfrescoHttpClientFactory;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.ContentAspects;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.GroupService;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.ToolbarUserMenu;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.Utility;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.AbstractWebTest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * @author bogdan.bocancea
 */
@ContextConfiguration(classes = ShareTestContext.class)
@Scope(value = "prototype")
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
    public CmisWrapper cmisApi;

    @Autowired
    public RestWrapper restApi;

    @Autowired
    public UserDashboardPage userDashboardPage;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private AIMSPage aimsPage;
    
    @Autowired
    protected UserDashboardPage userDashboard; 

    @Autowired
    protected ToolbarUserMenu toolbarUserMenu;

    @Autowired
    protected AlfrescoHttpClientFactory alfrescoHttpClientFactory;

    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;
    public static final GroupModel ALFRESCO_ADMIN_GROUP = new GroupModel("ALFRESCO_ADMINISTRATORS");
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
     * Just authenticate using <username> and <password> provided as parameters
     * And inject the cookies in current browser use this method in
     * a @BeforeClass to pass the login screen
     *
     * @param userName
     * @param password
     */
    protected void setupAuthenticatedSession(String userName, String password)
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
    	 userDashboard.waitForSharePageBodyToLoad();
    }

    /**
     * Cleanup authenticated session with all cookies and logout the user
     */
    protected void cleanupAuthenticatedSession()
    {
    	dataUser.logout();
        getBrowser().cleanUpAuthenticatedSession();
    }
    
    protected void logoutViaBrowser()
    {
    	toolbarUserMenu.clickLogout();
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

    protected HttpCookie doLogin(UserModel userModel)
    {
        if (StringUtils.isEmpty(userModel.getUsername()) || StringUtils.isEmpty(userModel.getPassword()))
        {
            throw new IllegalArgumentException("Parameter missing");
        }

        HttpCookie httpCookie = null;

        try
        {
            URL obj = new URL("http://localhost:8080/share/page/");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(1000 * 2);
            con.setReadTimeout(1000 * 5);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            String redirectLocation = con.getHeaderField("Location");

            if (redirectLocation != null && responseCode == 302)
            {

                URL target = new URL(con.getURL(), redirectLocation);
                con.disconnect();

                HttpURLConnection connection = (HttpURLConnection) target.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.setRequestMethod("POST");
                String POST_PARAMS = "username=admin&password=admin";

                OutputStream os = connection.getOutputStream();
                os.write(POST_PARAMS.getBytes());
                os.flush();
                os.close();
                connection.connect();

                responseCode = con.getResponseCode();

                Map<String, List<String>> headerFields = con.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                if (cookiesHeader != null)
                {
                    String cookie = cookiesHeader.get(0);
                    httpCookie = HttpCookie.parse(cookie).get(0);
                }

                con.disconnect();
            }

            URL target1 = new URL("http://localhost:8080/share/page/user/admin/dashboard");

            HttpURLConnection connection = (HttpURLConnection) target1.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestMethod("GET");
            responseCode = con.getResponseCode();

        }
        catch (IOException e)
        {

        }
        return httpCookie;

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
            return aimsPage;
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
            Utility.waitToLoopTime(1, String.format("Wait for '%s' to get downloaded"));
        }
        return Files.exists(Paths.get(filePath));
    }

    public FolderModel getUserHomeFolder(UserModel userModel)
    {
        FolderModel userFolder = new FolderModel(userModel.getUsername());
        userFolder.setCmisLocation(Utility.buildPath(cmisApi.getUserHomesPath(), userModel.getUsername()));
        return userFolder;
    }

    public void removeUserFromAlfresco(UserModel... users)
    {
        for (UserModel user : users)
        {
            dataUser.usingAdmin().deleteUser(user);
            FolderModel userFolder = getUserHomeFolder(user);
            cmisApi.authenticateUser(dataUser.getAdminUser()).usingResource(userFolder).deleteFolderTree();
        }
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