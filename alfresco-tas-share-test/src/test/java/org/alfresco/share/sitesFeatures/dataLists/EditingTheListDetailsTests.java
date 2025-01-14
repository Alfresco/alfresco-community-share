package org.alfresco.share.sitesFeatures.dataLists;

import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.site.dataLists.CreateDataListDialog;
import org.alfresco.po.share.site.dataLists.DataListsPage;
import org.alfresco.po.share.site.dataLists.EditListDetailsPopUpPages;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class EditingTheListDetailsTests extends BaseTest
{

    @Autowired
    SiteService siteService;
    @Autowired
    DataListsService dataListsService;
    @Autowired
    protected UserService userService;
    private DataListsPage dataListsPage;
    private String contactList;
    private CreateDataListDialog createDataListDialog;
    private EditListDetailsPopUpPages editListDetailsPopUpPages;
    private final String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private final ThreadLocal<UserModel> contributor = new ThreadLocal<>();
    private final ThreadLocal<UserModel> consumer = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String password = "password";
    private String listName = "first list";

    @BeforeMethod (alwaysRun = true)
    public void preConditions()
    {
        log.info("Precondition: Any Test user is created");
        userName.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        contributor.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        consumer.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(userName.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(userName.get());
        siteService.addPageToSite(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), Page.DATALISTS, null);
        authenticateUsingLoginPage(userName.get());

        dataListsPage = new DataListsPage(webDriver);
        createDataListDialog = new CreateDataListDialog(webDriver);
        editListDetailsPopUpPages = new EditListDetailsPopUpPages(webDriver);

        log.info("Preconditions for Data List Table Actions test");
        contactList = String.format("ContactList%s", RandomData.getRandomAlphanumeric());
        dataListsService.createDataList(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), DataListsService.DataList.CONTACT_LIST, listName, description);
        dataListsService.addContactListItem(userName.get().getUsername(), userName.get().getPassword(), siteName.get().getId(), listName, "FirstName", "LastName", "E-mail", "Company", "JobTitle", "PhoneOffice",
            "PhoneMobile", "Notes");
        dataListsPage.navigate(siteName.get().getId());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userName.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userName.get());
    }

    @TestRail (id = "C5894")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void modifyTitleAndDescriptionOfAnExistingList()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        log.info("Step 2: Check available editing options.");
        Assert.assertTrue("The 'Cancel' button is not displayed.", editListDetailsPopUpPages.isCancelButtonDisplayed());
        Assert.assertTrue("The 'Close' button is not displayed.", editListDetailsPopUpPages.isCloseButtonDisplayed());
        Assert.assertTrue("The 'Edit' button is not displayed.", editListDetailsPopUpPages.isSaveButtonDisplayed());

        log.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUpPages.modifyTitle("new Title");
        editListDetailsPopUpPages.modifyDescription("new description");

        log.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUpPages.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        log.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsItemsTitle().contains("new Title"), true);
    }

    @TestRail (id = "C5895")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelModifyingTitleAndDescriptionOfAnExistingList()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        log.info("Step 2: Modify the text for Title and Description.");
        editListDetailsPopUpPages.modifyTitle("new Title");
        editListDetailsPopUpPages.modifyDescription("new description");

        log.info("Step 3: Click on the 'Cancel' button.");
        editListDetailsPopUpPages.clickCancelButton();

        log.info("Step 4: Check and confirm that the list not has been updated.");
        Assert.assertEquals("The updated list is displayed.", dataListsPage.getListsItemsTitle().contains("new Title"), false);
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsItemsTitle().contains(listName), true);
    }

    @TestRail (id = "C5896")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void closeEditFormWithoutSavingChanges()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        log.info("Step 2: Modify the text for Title and Description.");
        editListDetailsPopUpPages.modifyTitle("new Title");
        editListDetailsPopUpPages.modifyDescription("new description");

        log.info("Step 3: Click on the 'X' button.");
        editListDetailsPopUpPages.clickCloseButton();

        log.info("Step 4: Check and confirm that the list not has been updated.");
        Assert.assertEquals("The updated list is displayed.", dataListsPage.getListsItemsTitle().contains("new Title"), false);
        Assert.assertEquals("The list is not displayed.", dataListsPage.getListsItemsTitle().contains(listName), true);
    }

    @TestRail (id = "C5898")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editingExistingListWithSiteCollaboratorUser()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        log.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUpPages.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUpPages.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUpPages.isSaveButtonDisplayed(), true);

        log.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUpPages.modifyTitle("new Title");
        editListDetailsPopUpPages.modifyDescription("new description");

        log.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUpPages.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        log.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsItemsTitle().contains("new Title"), true);
    }

    @TestRail (id = "C5899")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void contributorRoleIsNotAbleToEditExistingList()
    {
        log.info("Preconditions: Create a user with 'Contributor' role");
        userService.createSiteMember(userName.get().getUsername(), password, contributor.get().getUsername(), siteName.get().getId(), "SiteContributor");
        authenticateUsingCookies(contributor.get());
        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        Assert.assertEquals("The edit button is enabled", dataListsPage.isEditButtonDisabled(listName), true);

        log.info("Step 2: Click on the Edit button.");
        dataListsPage.clickOnDisabledEditButton(listName);
    }

    @TestRail (id = "C5900")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void consumerRoleIsNotAbleToEditExistingList()
    {
        log.info("Preconditions: Create a user with 'Consumer' role");
        userService.createSiteMember(userName.get().getUsername(), password, consumer.get().getUsername(), siteName.get().getId(), "SiteConsumer");
        authenticateUsingCookies(consumer.get());
        dataListsPage.navigate(siteName.get().getId());

        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        Assert.assertEquals("The edit button is enabled", dataListsPage.isEditButtonDisabled(listName), true);

        log.info("Step 2: Click on the Edit button.");
        dataListsPage.clickOnDisabledEditButton(listName);
    }

    @TestRail (id = "C5901")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void contributorRoleIsAbleToEditListCreatedBySameUser()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        log.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUpPages.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUpPages.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUpPages.isSaveButtonDisplayed(), true);

        log.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUpPages.modifyTitle("new Title");
        editListDetailsPopUpPages.modifyDescription("new description");

        log.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUpPages.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        log.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsItemsTitle().contains("new Title"), true);
    }

    @TestRail (id = "C5904")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void siteManagerIsAbleToEditList()
    {
        log.info("Step 1: On the Data Lists page hoover mouse over the List from the Lists panel and click on the Edit button.");
        dataListsPage.clickEditButtonForList(listName);

        log.info("Step 2: Check available editing options.");
        Assert.assertEquals("The 'Cancel' button is not displayed.", editListDetailsPopUpPages.isCancelButtonDisplayed(), true);
        Assert.assertEquals("The 'Close' button is not displayed.", editListDetailsPopUpPages.isCloseButtonDisplayed(), true);
        Assert.assertEquals("The 'Edit' button is not displayed.", editListDetailsPopUpPages.isSaveButtonDisplayed(), true);

        log.info("Step 3: Modify the text for Title and Description.");
        editListDetailsPopUpPages.modifyTitle("new Title");
        editListDetailsPopUpPages.modifyDescription("new description");

        log.info("Step 4: Click on the 'Save' button.");
        editListDetailsPopUpPages.clickSaveButton();
        Assert.assertEquals("The current message wasn't as expected.", dataListsPage.successfullyCreatedDataListMessage(), "List details successfully updated for 'new Title'.");

        log.info("Step 5: Check and confirm that the list now displays the new title and description.");
        Assert.assertEquals("The updated list is not displayed.", dataListsPage.getListsItemsTitle().contains("new Title"), true);
    }
}
