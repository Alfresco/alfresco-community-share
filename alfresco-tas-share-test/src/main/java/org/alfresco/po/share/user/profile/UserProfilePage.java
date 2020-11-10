package org.alfresco.po.share.user.profile;

import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserProfilePage extends SharePage<UserProfilePage> implements AccessibleByMenuBar
{

    private static final String EMPTY_SPACE = " ";

    @Autowired
    private EditUserProfilePage editUserProfilePage;

    @FindBy (css = "button[id$='button-edit-button']")
    private WebElement editProfile;

    @RenderWebElement
    @FindBy (css = "a[id$='default-profile-link']")
    private WebElement infoLink;

    @RenderWebElement
    @FindAll (@FindBy (css = "div[id$='readview'] .viewcolumn .header-bar"))
    private List<WebElement> headers;

    @FindBy (css = ".namelabel")
    private WebElement nameLabel;

    @FindBy (css = ".biorow>div")
    private WebElement summary;

    @FindAll (@FindBy (css = ".fieldlabel"))
    private List<WebElement> aboutUserDetails;

    @FindAll (@FindBy (css = ".viewcolumn .row"))
    private List<WebElement> userInformation;

    @RenderWebElement
    @FindBy (css = "a[id$='default-user-sites-link']")
    private WebElement sitesLink;

    @RenderWebElement
    @FindBy (css = "a[id$='user-content-link']")
    private WebElement contentLink;

    @FindBy (css = "a[id$='following-link']")
    private WebElement followingLink;

    @FindBy (css = "a[id$='followers-link']")
    private WebElement followersLink;

    @FindBy (css = "a[id$='change-password-link']")
    private WebElement changePasswordLink;

    @FindBy (css = "a[id$='user-notifications-link']")
    private WebElement notificationLink;

    @FindBy (css = "a[id$='user-trashcan-link']")
    private WebElement trashcanLink;

    @FindBy (css = "div[id$='readview'] .photoimg")
    private WebElement photo;

    private By fieldValue = By.cssSelector(".fieldvalue");
    private String contactInfoDuplicateRow = "(//span[text()= '%s'])[1]/..";
    private String companyInfoDuplicateRow = "(//span[text()= '%s'])[2]/..";
    private String valueRow = "//span[@class='fieldlabelright' and text()='%s']/..";

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/profile", getUserName());
    }

    public UserProfilePage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public UserProfilePage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    @SuppressWarnings ("unchecked")
    @Override
    public UserProfilePage navigateByMenuBar()
    {
        toolbar.clickUserMenu().clickMyProfile();
        return (UserProfilePage) renderedPage();
    }

    public UserProfilePage assertUserProfilePageIsOpened()
    {
        Assert.assertTrue(browser.isElementDisplayed(infoLink), "User profile page is opened");
        return this;
    }

    public UserProfilePage assertInfoLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(infoLink), "Info link is displayed");
        return this;
    }

    public UserProfilePage assertSitesLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(sitesLink), "Sites link is displayed");
        return this;
    }

    public UserProfilePage assertContentLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(contentLink), "Content link is displayed");
        return this;
    }

    public UserProfilePage assertImFollowingLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(followingLink), "Following link is displayed");
        return this;
    }

    public UserProfilePage assertFollowingMeLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(followersLink), "Following Me link is displayed");
        return this;
    }

    public UserProfilePage assertChangePasswordLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(changePasswordLink), "Change password link is displayed");
        return this;
    }

    public UserProfilePage assertNotificationsLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(notificationLink), "Notifications link is displayed");
        return this;
    }

    public UserProfilePage assertTrashcanLinkIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(trashcanLink), "Trashcan link is displayed");
        return this;
    }

    /**
     * Verify if About header is displayed
     *
     * @return true if displayed
     */
    public boolean isAboutHeaderDisplayed()
    {
        return browser.isElementDisplayed(browser.findFirstElementWithValue(headers, language.translate("adminTools.user.about")));
    }

    public UserProfilePage assertAboutHeaderIsDisplayed()
    {
        Assert.assertNotNull(browser.findFirstElementWithValue(headers, language.translate("adminTools.user.about")),
            "About is displayed");
        return this;
    }

    public UserProfilePage assertContactInfoHeaderIsDisplayed()
    {
        Assert.assertNotNull(browser.findFirstElementWithValue(headers, language.translate("adminTools.user.contactInfo")),
            "About is displayed");
        return this;
    }

    public UserProfilePage assertCompanyDetailsHeaderIsDisplayed()
    {
        Assert.assertNotNull(browser.findFirstElementWithValue(headers, language.translate("adminTools.user.companyDetails")),
            "About is displayed");
        return this;
    }

    /**
     * Click Edit Profile button
     */
    public EditUserProfilePage clickEditProfile()
    {
        LOG.info("Click Edit Profile");
        editProfile.click();
        return (EditUserProfilePage) editUserProfilePage.renderedPage();
    }

    public String assertUsernameEquals(String firstName, String lastName)
    {
        assertEquals(nameLabel.getText(), firstName.concat(EMPTY_SPACE).concat(lastName));
        return nameLabel.getText();
    }

    public UserProfilePage assertSummaryIs(String summaryValue)
    {
        LOG.info(String.format("Assert summary value is: %s", summaryValue));
        assertEquals(summary.getText(), summaryValue, "Summary is correct");
        return this;
    }

    private List<String> getAboutUserInfo()
    {
        return aboutUserDetails.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public UserProfilePage assertAboutUserHasValues(String... values)
    {
        LOG.info(String.format("Assert values '%s' are displayed in About User section", Arrays.asList(values)));
        Assert.assertTrue(getAboutUserInfo().containsAll(Arrays.asList(values)), "All values are displayed in About section");
        return this;
    }

    public UserProfilePage assertUserInfoIsEmpty()
    {
        LOG.info("Assert About user has no values");
        Assert.assertTrue(getAboutUserInfo().isEmpty(), "User info has no values");
        return this;
    }

    private UserProfilePage checkValue(String element, String label, String value)
    {
        assertEquals(browser.findElement(By.xpath(String.format(element,
            language.translate(label))))
            .findElement(fieldValue).getText(), value);
        return this;
    }

    public UserProfilePage assertContactInformationEmailIs(String expectedEmail)
    {
        return checkValue(contactInfoDuplicateRow, "adminTools.user.email", expectedEmail);
    }

    public UserProfilePage assertContactInformationTelephoneIs(String expectedTelephone)
    {
        return checkValue(contactInfoDuplicateRow, "adminTools.user.telephone", expectedTelephone);
    }

    public UserProfilePage assertContactInformationMobileIs(String expectedMobile)
    {
        return checkValue(valueRow, "adminTools.user.mobile", expectedMobile);
    }

    public UserProfilePage assertContactInformationSkypeIs(String expectedSkype)
    {
        return checkValue(valueRow, "adminTools.user.skype", expectedSkype);
    }

    public UserProfilePage assertContactInformationIMIs(String expectedIM)
    {
        return checkValue(valueRow, "adminTools.user.IM", expectedIM);
    }

    public UserProfilePage assertContactInformationGoogleUserNameIs(String expectedGoogleUserName)
    {
        return checkValue(valueRow, "adminTools.user.googleUsername", expectedGoogleUserName);
    }

    public UserProfilePage assertCompanyNameIs(String expectedCompanyName)
    {
        return checkValue(valueRow, "adminTools.user.name", expectedCompanyName);
    }

    public UserProfilePage assertCompanyAddressIs(String expectedCompanyAddress)
    {
        return checkValue(valueRow, "adminTools.user.address", expectedCompanyAddress);
    }

    public UserProfilePage assertCompanyTelephoneIs(String expectedCompanyTelephone)
    {
        return checkValue(companyInfoDuplicateRow, "adminTools.user.telephone", expectedCompanyTelephone);
    }

    public UserProfilePage assertCompanyFaxIs(String expectedCompanyFax)
    {
        return checkValue(valueRow, "adminTools.user.fax", expectedCompanyFax);
    }

    public UserProfilePage assertCompanyEmailIs(String expectedCompanyEmail)
    {
        return checkValue(companyInfoDuplicateRow, "adminTools.user.email", expectedCompanyEmail);
    }

    public UserProfilePage assertDefaultAvatarIsDisplayed()
    {
        browser.waitUntilElementHasAttribute(photo, "src", "no-user-photo-64.png");
        Assert.assertTrue(photo.getAttribute("src").contains("no-user-photo-64.png"));
        return this;
    }

    public UserProfilePage assertNewAvatarIsDisplayed()
    {
        Assert.assertTrue(photo.getAttribute("src").contains("/content/thumbnails/avatar"));
        return this;
    }
}