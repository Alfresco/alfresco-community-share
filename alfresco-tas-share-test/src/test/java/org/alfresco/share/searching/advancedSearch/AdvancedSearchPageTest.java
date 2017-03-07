package org.alfresco.share.searching.advancedSearch;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdvancedSearchPageTest extends ContextAwareWebTest
{
    @Autowired
    AdvancedSearchPage advancedSearchPage;

    @Autowired
    SearchPage searchPage;

    private void createPreconditions(String userName, String siteName, String identifier)
    {
        List<Page> sitePages = new ArrayList<>();
        DateTime today = new DateTime();
        DateTime tomorrow = today.plusDays(1);

        sitePages.add(Page.WIKI);
        sitePages.add(Page.BLOG);
        sitePages.add(Page.CALENDAR);
        sitePages.add(Page.DISCUSSIONS);
        sitePages.add(Page.LINKS);
        sitePages.add(Page.DATALISTS);

        siteService.addPagesToSite(userName, DataUtil.PASSWORD, siteName, sitePages);

        sitePagesService.createWiki(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " wiki 1", "test" + identifier, null);
        sitePagesService.createWiki(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " wiki 2", "hello" + identifier, null);
        sitePagesService.createWiki(userName, DataUtil.PASSWORD, siteName, identifier + " wiki 3", "test" + identifier, null);
        sitePagesService.createWiki(userName, DataUtil.PASSWORD, siteName, identifier + " wiki 4", "hello" + identifier, null);

        sitePagesService.createBlogPost(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " blog 1", "test" + identifier, false, null);
        sitePagesService.createBlogPost(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " blog 2", "hello" + identifier, false, null);
        sitePagesService.createBlogPost(userName, DataUtil.PASSWORD, siteName, identifier + " blog 3", "test" + identifier, false, null);
        sitePagesService.createBlogPost(userName, DataUtil.PASSWORD, siteName, identifier + " blog 4", "hello" + identifier, false, null);

        sitePagesService.addCalendarEvent(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " event 1", "", "test" + identifier, today.toDate(),
                tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " event 2", "", "hello" + identifier, today.toDate(),
                tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, DataUtil.PASSWORD, siteName, identifier + " event 3", "", "test" + identifier, today.toDate(),
                tomorrow.toDate(), "", "", false, null);
        sitePagesService.addCalendarEvent(userName, DataUtil.PASSWORD, siteName, identifier + " event 4", "", "hello" + identifier, today.toDate(),
                tomorrow.toDate(), "", "", false, null);

        sitePagesService.createDiscussion(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " topic 1", "test" + identifier, null);
        sitePagesService.createDiscussion(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " topic 2", "hello" + identifier, null);
        sitePagesService.createDiscussion(userName, DataUtil.PASSWORD, siteName, identifier + " topic 3", "test" + identifier, null);
        sitePagesService.createDiscussion(userName, DataUtil.PASSWORD, siteName, identifier + " topic 4", "hello" + identifier, null);

        sitePagesService.createLink(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " link 1", "https://www.alfresco.com", "test" + identifier,
                false, null);
        sitePagesService.createLink(userName, DataUtil.PASSWORD, siteName, "test" + identifier + " link 2", "https://www.alfresco.com", "hello" + identifier,
                false, null);
        sitePagesService.createLink(userName, DataUtil.PASSWORD, siteName, identifier + " link 3", "https://www.alfresco.com", "test" + identifier, false, null);
        sitePagesService.createLink(userName, DataUtil.PASSWORD, siteName, identifier + " link 4", "https://www.alfresco.com", "hello" + identifier, false,
                null);

        datalistService.createDataList(userName, DataUtil.PASSWORD, siteName, DataListsService.DataList.TODO_LIST, "test" + identifier + " list 1", "test" + identifier);
        datalistService.createDataList(userName, DataUtil.PASSWORD, siteName, DataListsService.DataList.TODO_LIST, "test" + identifier + " list 2", "hello" + identifier);
        datalistService.createDataList(userName, DataUtil.PASSWORD, siteName, DataListsService.DataList.TODO_LIST, identifier + " list 3", "test" + identifier);
        datalistService.createDataList(userName, DataUtil.PASSWORD, siteName, DataListsService.DataList.TODO_LIST, identifier + " list 4", "hello" + identifier);
    }

    @TestRail(id = "C5888")
    @Test
    public void verifyAdvancedSearchPage()
    {
        String userName = "User1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        advancedSearchPage.navigate();

        LOG.info("STEP 1 - Verify page title");
        Assert.assertEquals(advancedSearchPage.getPageTitle(), language.translate("advancedSearchPage.pageTitle"), "Page title");

        LOG.info("STEP 2 - Verify buttons");
        Assert.assertTrue(advancedSearchPage.isTopSearchButtonDisplayed(), "Top search button is displayed");
        Assert.assertTrue(advancedSearchPage.isBottomSearchButtonDisplayed(), "Bottom search button is displayed");

        LOG.info("STEP 3 - Verify \"look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdown();
        Assert.assertTrue(advancedSearchPage.isLookForDropdownOptionDisplayed(language.translate("advancedSearchPage.lookForDropDown.content.label"),
                language.translate("advancedSearchPage.lookForDropDown.content.description")));
        Assert.assertTrue(advancedSearchPage.isLookForDropdownOptionDisplayed(language.translate("advancedSearchPage.lookForDropDown.folders.label"),
                language.translate("advancedSearchPage.lookForDropDown.folders.description")));

        LOG.info("STEP 4 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));
        Assert.assertTrue(advancedSearchPage.isKeywordsInputDisplayed(), "Keywords input is displayed");
        Assert.assertTrue(advancedSearchPage.isNameInputDisplayed(), "Name input is displayed");
        Assert.assertTrue(advancedSearchPage.isTitleTextareaDisplayed(), "Title textarea is displayed");
        Assert.assertTrue(advancedSearchPage.isDescriptionTextareaDisplayed(), "Description textarea is displayed");
        Assert.assertTrue(advancedSearchPage.isMimetypeDropDownDisplayed(), "Mimetype input is displayed");
        Assert.assertTrue(advancedSearchPage.isDateFromPickerDisplayed(), "Date From picker is displayed");
        Assert.assertTrue(advancedSearchPage.isDateToPickerDisplayed(), "Date To picker is displayed");
        Assert.assertTrue(advancedSearchPage.isModifierInputDisplayed(), "Modifier input is displayed");

        LOG.info("STEP 5 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        browser.waitInSeconds(5);
        Assert.assertTrue(advancedSearchPage.isKeywordsInputDisplayed(), "Keywords input is displayed");
        Assert.assertTrue(advancedSearchPage.isNameInputDisplayed(), "Name input is displayed");
        Assert.assertTrue(advancedSearchPage.isTitleTextareaDisplayed(), "Title textarea is displayed");
        Assert.assertTrue(advancedSearchPage.isDescriptionTextareaDisplayed(), "Description textarea is displayed");
        Assert.assertFalse(advancedSearchPage.isMimetypeDropDownDisplayed(), "Mimetype input is not displayed");
        Assert.assertFalse(advancedSearchPage.isDateFromPickerDisplayed(), "Date From picker is not displayed");
        Assert.assertFalse(advancedSearchPage.isDateToPickerDisplayed(), "Date To picker is not displayed");
        Assert.assertFalse(advancedSearchPage.isModifierInputDisplayed(), "Modifier input is not displayed");
    }

    @TestRail(id = "C5891")
    @Test
    public void searchByKeyword()
    {
        String identifier = DataUtil.getUniqueIdentifier();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        createPreconditions(userName, siteName, identifier);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        advancedSearchPage.navigate();

        LOG.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));

        LOG.info("STEP 2 - Fill in \"Keyword\" field with \"test\" and click \"Search\" button");
        advancedSearchPage.typeKeywords("test" + identifier);
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " wiki 1"), "test" + identifier + " wiki 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " wiki 2"), "test" + identifier + " wiki 2 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " wiki 3"), identifier + " wiki 3 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " blog 1"), "test" + identifier + " blog 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " blog 2"), "test" + identifier + " blog 2 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " blog 3"), identifier + " blog 3 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " event 1"), "test" + identifier + " event 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " event 2"), "test" + identifier + " event 2 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " event 3"), identifier + " event 3 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " link 1"), "test" + identifier + " link 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " link 2"), "test" + identifier + " link 2 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " link 3"), identifier + " link 3 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " topic 1"), "test" + identifier + " topic 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " topic 2"), "test" + identifier + " topic 2 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " topic 3"), identifier + " topic 3 is displayed");

        LOG.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        browser.waitInSeconds(5);

        LOG.info("STEP 4 - Fill in \"Keyword\" field with \"test\" and click \"Search\" button");
        advancedSearchPage.typeKeywords("test" + identifier);
        advancedSearchPage.click1stSearch();

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " list 1"), "test" + identifier + " list 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " list 2"), "test" + identifier + " list 2 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " list 3"), identifier + " list 3 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " topic 1"), "test" + identifier + " topic 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " topic 2"), "test" + identifier + " topic 2 is displayed");
    }

    @TestRail(id = "C5907")
    @Test
    public void searchByName()
    {
        String identifier = DataUtil.getUniqueIdentifier();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        createPreconditions(userName, siteName, identifier);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        advancedSearchPage.navigate();

        LOG.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));

        LOG.info("STEP 2 - Fill in \"Name\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage.typeName("test" + identifier + "*");
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " wiki 1"), "test" + identifier + " wiki 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " wiki 2"), "test" + identifier + " wiki 2 is displayed");

        LOG.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        browser.waitInSeconds(5);

        LOG.info("STEP 4 - Fill in \"Name\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage.typeName("test" + identifier + "*");
        advancedSearchPage.click1stSearch();
        Assert.assertEquals(searchPage.getNumberOfResultsText(), language.translate("searchPage.noResultsFound"), "No results found");
    }

    @TestRail(id = "C5908")
    @Test
    public void searchByTitle()
    {
        String identifier = DataUtil.getUniqueIdentifier();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        createPreconditions(userName, siteName, identifier);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        advancedSearchPage.navigate();

        LOG.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));

        LOG.info("STEP 2 - Fill in \"Title\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage.typeTitle("test" + identifier);
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " wiki 1"), "test" + identifier + " wiki 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " wiki 2"), "test" + identifier + " wiki 2 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " blog 1"), "test" + identifier + " blog 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " blog 2"), "test" + identifier + " blog 2 is displayed");

        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " topic 1"), "test" + identifier + " topic 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " topic 2"), "test" + identifier + " topic 2 is displayed");

        LOG.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        browser.waitInSeconds(5);

        LOG.info("STEP 4 - Fill in \"Title\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage.typeTitle("test" + identifier);
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " list 1"), "test" + identifier + " list 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " list 2"), "test" + identifier + " list 2 is displayed");
    }

    @TestRail(id = "C5909")
    @Test
    public void searchByDescription()
    {
        String identifier = DataUtil.getUniqueIdentifier();
        String userName = "User1" + identifier;
        String siteName = "Site1" + identifier;
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        createPreconditions(userName, siteName, identifier);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        advancedSearchPage.navigate();

        LOG.info("STEP 1 - Choose \"Content\" from \"Look for\" drop-down");
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.content.label"));

        LOG.info("STEP 2 - Fill in \"Description\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage.typeDescription("test" + identifier);
        advancedSearchPage.click1stSearch();
        Assert.assertEquals(searchPage.getNumberOfResultsText(), language.translate("searchPage.noResultsFound"), "No results found");

        LOG.info("STEP 3 - Choose \"Folders\" from \"Look for\" drop-down");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        browser.waitInSeconds(5);

        LOG.info("STEP 4 - Fill in \"Description\" field with \"test*\"and click \"Search\" button");
        advancedSearchPage.typeDescription("test" + identifier);
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFound("test" + identifier + " list 1"), "test" + identifier + " list 1 is displayed");
        Assert.assertTrue(searchPage.isResultFound(identifier + " list 3"), identifier + " list 3 is displayed");
    }
}
