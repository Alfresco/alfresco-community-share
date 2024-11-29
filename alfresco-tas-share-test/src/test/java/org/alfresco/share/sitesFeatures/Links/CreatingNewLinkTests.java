package org.alfresco.share.sitesFeatures.Links;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.link.CreateLinkPage;
import org.alfresco.po.share.site.link.LinkDetailsViewPage;
import org.alfresco.po.share.site.link.LinkPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
@Slf4j
public class CreatingNewLinkTests extends BaseTest {
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    @Autowired
    SitePagesService sitePagesService;
    @Autowired
    SiteService siteService;
    DateTime currentDate;
    private LinkPage linkPage;
    private CreateLinkPage createLinkPage;
    private LinkDetailsViewPage linkDetailsViewPage;
    private String linkTitle = String.format("Link%s", RandomData.getRandomAlphanumeric());
    private String linkWildcardTitle = "`~!^@#$№;,%()_&-{}[]'*\".<>/|?:";
    private String linkWildcardDescription = "`~!^@#$№;,%()_&-{}[]'*\".<>/|?:";
    private String linkWildcardTag = "`~!@#$№;%()_-{}[]'";
    private String linkWildcardInvalidTag = "*\".<>/|?";
    private String linkURL = "https://www.google.com";
    private String linkDescription =
        String.format("Link description%s", RandomData.getRandomAlphanumeric());

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("Precondition: Any Test user is created");
        user1.set(getDataUser().usingAdmin()
            .createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(user1.get())
            .createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());
        siteService.addPageToSite(user1.get()
            .getUsername(), user1.get()
            .getPassword(), siteName.get()
            .getId(), DashboardCustomization.Page.LINKS, null);

        linkPage = new LinkPage(webDriver);
        createLinkPage = new CreateLinkPage(webDriver);
        linkDetailsViewPage = new LinkDetailsViewPage(webDriver);
        currentDate = new DateTime();

        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(),
            "/User Homes/" + user1.get()
                .getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6180")
    public void createExternalLink() {
        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get()
            .getId());
        Assert.assertTrue(linkPage.getNoLinksFoundMsg()
            .equals("No links found."), "No link should be displayed!");

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();
        Assert.assertTrue(createLinkPage.isLinkTitleDisplayed(), "Link title input is displayed!");
        Assert.assertTrue(createLinkPage.isLinkURLDisplayed(), "Link URL input is displayed!");
        Assert.assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "Link description input is displayed!");
        Assert.assertTrue(createLinkPage.isLinkInternalChecked(), "Internal checkbox should be unchecked by default!");
        Assert.assertTrue(createLinkPage.isLinkTagDisplayed(), "Tags area is displayed!");
        Assert.assertTrue(createLinkPage.isAddTagButtonDisplayed(), "Add tag button is displayed!");
        Assert.assertTrue(createLinkPage.isChoosePopularTagsLinkDisplayed(),
            "'Choose from popular tags in this list' link is displayed!");
        Assert.assertTrue(createLinkPage.isSaveButtonDisplayed(), "Save button is displayed!");
        Assert.assertTrue(createLinkPage.isCancelButtonDisplayed(), "Cancel button is displayed!");

        log.info("STEP 3: Enter a title, URL, description and click on 'Save' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.typeLinkDescription(linkDescription);
        createLinkPage.clickSaveButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle()
            .equals(linkTitle), "Wrong link title! expected " + linkTitle + "but found "
            + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL()
                .equals(linkURL),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate()
            .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertFalse(linkDetailsViewPage.getCreatedBy()
            .equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription()
                .equals(linkDescription),
            "Wrong link description! expected " + linkDescription + "but found"
                + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage()
            .equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 4: Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList()
            .contains(linkTitle), "Link title is not in the list!");

        log.info("STEP 5: Click on link's URL");
        linkPage.switchWindow();
        Assert.assertTrue(linkPage.getCurrentUrl()
                .contains("google"),
            "After clicking on the link, the title is: " + linkPage.getCurrentUrl());
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6182")
    public void createInternalLink() {
        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get()
            .getId());
        Assert.assertTrue(linkPage.getNoLinksFoundMsg()
            .equals("No links found."), "No link should be displayed!");

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();
        Assert.assertTrue(createLinkPage.isLinkTitleDisplayed(), "Link title input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkURLDisplayed(), "Link URL input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkDescriptionDisplayed(), "Link description input is not displayed!");
        Assert.assertTrue(createLinkPage.isLinkInternalChecked(), "Internal checkbox should be unchecked by default!");
        Assert.assertTrue(createLinkPage.isLinkTagDisplayed(), "Tags area is not displayed!");
        Assert.assertTrue(createLinkPage.isAddTagButtonDisplayed(), "Add tag button is not displayed!");
        Assert.assertTrue(createLinkPage.isChoosePopularTagsLinkDisplayed(),
            "'Choose from popular tags in this list' link is not displayed!");
        Assert.assertTrue(createLinkPage.isSaveButtonDisplayed(), "Save button is not displayed!");
        Assert.assertTrue(createLinkPage.isCancelButtonDisplayed(), "Cancel button is not displayed!");

        log.info("STEP 3: Enter a title, URL, description and click on 'Save' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.typeLinkDescription(linkDescription);
        createLinkPage.checkLinkInternal();
        createLinkPage.clickSaveButton();

        Assert.assertTrue(linkDetailsViewPage.getLinkTitle()
            .equals(linkTitle), "Wrong link title! expected " + linkTitle + "but found "
            + linkDetailsViewPage.getLinkTitle());
        Assert.assertTrue(linkDetailsViewPage.getLinkURL()
                .equals(linkURL),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate()
            .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        Assert.assertFalse(linkDetailsViewPage.getCreatedBy()
            .equals("firstName" + " " + "lastName"), "Wrong author of the link!");
        Assert.assertTrue(linkDetailsViewPage.getDescription()
                .equals(linkDescription),
            "Wrong link description! expected " + linkDescription + "but found"
                + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getNoCommentsMessage()
            .equals("No comments"), "'No comments' message should be displayed!");

        log.info("STEP 4: Click 'Links List' link.");
        linkDetailsViewPage.clickOnLinksListLink();
        Assert.assertTrue(linkPage.getLinksTitlesList()
            .contains(linkTitle), "Link title is not in the list!");

        log.info("STEP 5: Click on link's URL");
        linkPage.switchWindow();
        Assert.assertTrue(linkPage.getCurrentUrl()
                .contains("google"),
            "After clicking on the link, the title is: " + linkPage.getCurrentUrl());

        log.info("STEP 6: Click Back in browser and then click 'Links List' link.");
        linkPage.navigateBackBrowser();
        Assert.assertEquals(linkPage.getLinkTitle(), linkTitle, "Link not displayed in Liks list");
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6182")
    public void cancelCreatingNewLink() {
        log.info("STEP 1: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get()
            .getId());
        Assert.assertTrue(linkPage.getNoLinksFoundMsg()
            .equals("No links found."), "No link should be displayed!");

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();

        log.info("STEP 3: Enter a title, URL, description and click on 'Cancel' button");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.clickCancelButton();
        Assert.assertTrue(linkPage.getNoLinksFoundMsg()
            .equals("No links found."), "No link should be displayed!");
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6880")
    public void CannotCreateLinkWithSpacesInMandatoryFields() {
        log.info("Precondition: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get()
            .getId());

        log.info("STEP 1: Click 'New Link' button");
        linkPage.createLink();

        log.info(
            "STEP 2: Fill in Title field with spaces, URL field with any correct data (e.g: google.com) and press 'Save' button.");
        createLinkPage.typeLinkTitle("    ");
        createLinkPage.typeLinkUrl("www.google.com");
        createLinkPage.clickSaveAndVerifyMandatoryFieldBalloonMessage("The value cannot be empty.");
        createLinkPage.clickCancelButton();

        log.info(
            "STEP 3: Fill in Title field with any correct data (e.g.: a title), URL field with spaces and press 'Save' button.");
        linkPage.createLink();
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.typeLinkUrl("    ");
        createLinkPage.clickSaveAndVerifyMandatoryFieldBalloonMessage("Field contains an error.");

    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6882")
    public void createLinkFillingInTheWildcards() throws InterruptedException {
        log.info("Precondition: Navigate to 'Links' page for siteName");
        linkPage.navigate(siteName.get()
            .getId());

        log.info("STEP 1: Click 'New Link' button");
        linkPage.createLink();

        log.info("STEP 2: Type into 'Title', 'URL', 'Description' fields wildcards");
        createLinkPage.typeLinkTitle(linkWildcardTitle)
            .typeLinkUrl(linkURL)
            .typeLinkDescription(linkWildcardDescription);

        log.info("STEP 3: Type into 'Tags' field wildcards");
        createLinkPage.addingTag(linkWildcardInvalidTag);
        Assert.assertEquals(createLinkPage.getBalloonMessage(),
            "The characters * < > ? / : | are not allowed. The name cannot end with a dot.");
        Assert.assertFalse(createLinkPage.VerifyAddTagButtonVisibility(), "Add tag button should be diabled");

        log.info("STEP 4: Clean 'Tags' field, type into 'Tags' field wildcards (such as ^&,)");
        createLinkPage.addingTag("^&,");
        Assert.assertTrue(createLinkPage.VerifyAddTagButtonVisibility(), "Add tag Button should be enabled");

        log.info("STEP 5: Clean 'Tags' field, type into 'Tags' field a text which ends with a dot. (such as tag.)");
        createLinkPage.addingTag("tag.");
        Assert.assertEquals(createLinkPage.getBalloonMessage(),
            "The characters * < > ? / : | are not allowed. The name cannot end with a dot.");
        Assert.assertFalse(createLinkPage.VerifyAddTagButtonVisibility(), "Add tag button should be diabled");

        log.info(
            "STEP 6: Clean 'Tags' field, type into it any string containing wilcards and press 'Add' button. For example: \"st^ri&n,g\".");
        createLinkPage.addTag("st^ri&n,g");
        Assert.assertTrue(createLinkPage.isTagDisplayedInCreateLists("st"));
        Assert.assertTrue(createLinkPage.isTagDisplayedInCreateLists("ri&n,g"));
        createLinkPage.assertTagCount(2);

        log.info(
            "STEP 7: Remove added tags, type into 'Tags' field any string containing 'space' and press 'Add' button. For example: \"str ing\".");
        createLinkPage.removeTag("ri&n,g");
        createLinkPage.removeTag("st");
        createLinkPage.addTag("str ing");
        Assert.assertTrue(createLinkPage.isTagDisplayedInCreateLists("str"));
        Assert.assertTrue(createLinkPage.isTagDisplayedInCreateLists("ing"));
        createLinkPage.assertTagCount(2);

        log.info("STEP 8: Remove added tags, type into 'Tags' field wildcards and press 'Add' button;");
        createLinkPage.removeTag("ing");
        createLinkPage.removeTag("str");
        createLinkPage.addTag(linkWildcardTag);

        log.info("STEP 9: Press Save Button");
        createLinkPage.clickSaveButton();

        assertEquals(linkWildcardTitle, linkDetailsViewPage.getLinkTitle(),
            "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        assertEquals(linkURL, linkDetailsViewPage.getLinkURL(),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        assertEquals(linkWildcardDescription, linkDetailsViewPage.getDescription(),
            "Wrong link description! expected " + linkDescription + "but found"
                + linkDetailsViewPage.getDescription());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate()
            .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        assertEquals(user1.get()
            .getFirstName() + " " + user1.get()
            .getLastName(), linkDetailsViewPage.getCreatedBy(), "Wrong author of the link!");
        assertEquals(linkWildcardTag, linkDetailsViewPage.getLinkTag(),
            "Wrong link tag! expected " + linkWildcardTag + "but found " + linkDetailsViewPage.getLinkTag());
    }

    @Test(groups = {TestGroup.SANITY, TestGroup.SITES_FEATURES})
    @TestRail(id = "C6181")
    public void checkMandatoryFields() throws InterruptedException {
        log.info("STEP 1: Navigate to 'Links' page for Site1.");
        linkPage.navigate(siteName.get()
            .getId());

        log.info("STEP 2: Click 'New Link' button");
        linkPage.createLink();

        log.info(
            "STEP 3: click 'Save' button - Title input is red and 'The value cannot be empty.' balloon appears above it.");
        createLinkPage.clickSaveAndVerifyMandatoryFieldBalloonMessage("The value cannot be empty.");

        log.info("STEP 4: Enter any 'Title' (e.g. \"TestLink\") and click on 'Save' button.");
        createLinkPage.typeLinkTitle(linkTitle);
        createLinkPage.clickSaveAndVerifyMandatoryFieldBalloonMessage("The value cannot be empty.");

        log.info("STEP 5: Enter any 'URL' (e.g. \"google.com\"), add tag 'test' and click on 'Save' button.");
        createLinkPage.typeLinkUrl(linkURL);
        createLinkPage.addTag("tag1");
        createLinkPage.clickSaveButton();

        log.info("STEP 6: verify link view displayed are correct");
        assertEquals(linkTitle, linkDetailsViewPage.getLinkTitle(),
            "Wrong link title! expected " + linkTitle + "but found " + linkDetailsViewPage.getLinkTitle());
        assertEquals(linkURL, linkDetailsViewPage.getLinkURL(),
            "Wrong link URL! expected" + linkURL + "but found " + linkDetailsViewPage.getLinkURL());
        Assert.assertTrue(linkDetailsViewPage.getCreationDate()
            .contains(currentDate.toString("EEE d MMM yyyy")), "Wrong link creation date!");
        assertEquals(user1.get()
            .getFirstName() + " " + user1.get()
            .getLastName(), linkDetailsViewPage.getCreatedBy(), "Wrong author of the link!");
        assertEquals("tag1", linkDetailsViewPage.getLinkTag(),
            "Wrong link tag! expected " + "tag1" + "but found " + linkDetailsViewPage.getLinkTag());

    }
}
