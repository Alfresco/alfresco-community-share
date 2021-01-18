package org.alfresco.po.share.user.profile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.alfresco.po.share.SharePageObject.WAIT_10;
import static org.testng.Assert.*;

/**
 * @author bogdan.bocancea
 */
public class UserProfilePage extends SharePage2<UserProfilePage> implements AccessibleByMenuBar
{
    private String userName;

    private final By editProfile = By.cssSelector("button[id$='button-edit-button']");
    private final By infoLink = By.cssSelector("a[id$='default-profile-link']");
    private final By headers = By.cssSelector("div[id$='readview'] .viewcolumn .header-bar");
    private final By summary = By.cssSelector(".biorow>div");
    private final By aboutUserDetails = By.cssSelector(".fieldlabel");
    private final By sitesLink = By.cssSelector("a[id$='default-user-sites-link']");
    private final By contentLink = By.cssSelector("a[id$='user-content-link']");
    private final By followingLink = By.cssSelector("a[id$='following-link']");
    private final By followersLink = By.cssSelector("a[id$='followers-link']");
    private final By changePasswordLink = By.cssSelector("a[id$='change-password-link']");
    private final By notificationLink = By.cssSelector("a[id$='user-notifications-link']");
    private final By trashcanLink = By.cssSelector("a[id$='user-trashcan-link']");
    private final By photo = By.cssSelector("div[id$='readview'] .photoimg");
    private final By fieldValue = By.cssSelector(".fieldvalue");
    private final String contactInfoDuplicateRow = "(//span[text()= '%s'])[1]/..";
    private final String companyInfoDuplicateRow = "(//span[text()= '%s'])[2]/..";
    private final String valueRow = "//span[@class='fieldlabelright' and text()='%s']/..";

    public UserProfilePage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

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
        return new Toolbar(webDriver).clickUserMenu().clickMyProfile();
    }

    public UserProfilePage assertUserProfilePageIsOpened()
    {
        webElementInteraction.waitUrlContains("/profile", WAIT_10);
        assertTrue(webElementInteraction.getCurrentUrl().contains("/profile"), "User profile page is opened");
        return this;
    }

    public UserProfilePage assertInfoLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(infoLink);
        assertTrue(webElementInteraction.isElementDisplayed(infoLink), "Info link is displayed");
        return this;
    }

    public UserProfilePage assertSitesLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(sitesLink);
        assertTrue(webElementInteraction.isElementDisplayed(sitesLink), "Sites link is displayed");
        return this;
    }

    public UserProfilePage assertContentLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(contentLink);
        assertTrue(webElementInteraction.isElementDisplayed(contentLink), "Content link is displayed");
        return this;
    }

    public UserProfilePage assertImFollowingLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(followingLink);
        assertTrue(webElementInteraction.isElementDisplayed(followingLink), "Following link is displayed");
        return this;
    }

    public UserProfilePage assertFollowingMeLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(followersLink);
        assertTrue(webElementInteraction.isElementDisplayed(followersLink), "Following Me link is displayed");
        return this;
    }

    public UserProfilePage assertChangePasswordLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(changePasswordLink);
        assertTrue(webElementInteraction.isElementDisplayed(changePasswordLink), "Change password link is displayed");
        return this;
    }

    public UserProfilePage assertNotificationsLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(notificationLink);
        assertTrue(webElementInteraction.isElementDisplayed(notificationLink), "Notifications link is displayed");
        return this;
    }

    public UserProfilePage assertTrashcanLinkIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(trashcanLink);
        assertTrue(webElementInteraction.isElementDisplayed(trashcanLink), "Trashcan link is displayed");
        return this;
    }

    public boolean isAboutHeaderDisplayed()
    {
        return webElementInteraction.isElementDisplayed(
            webElementInteraction.findFirstElementWithValue(headers, language.translate("adminTools.user.about")));
    }

    //todo: refactor
    public UserProfilePage assertAboutHeaderIsDisplayed()
    {
        assertNotNull(webElementInteraction.findFirstElementWithValue(headers, language.translate("adminTools.user.about")),
            "About is not displayed");
        return this;
    }

    //todo: refactor
    public UserProfilePage assertContactInfoHeaderIsDisplayed()
    {
        assertNotNull(webElementInteraction.findFirstElementWithValue(headers, language.translate("adminTools.user.contactInfo")),
            "Contact information header is not displayed");
        return this;
    }

    //todo: refactor
    public UserProfilePage assertCompanyDetailsHeaderIsDisplayed()
    {
        assertNotNull(webElementInteraction.findFirstElementWithValue(headers, language.translate("adminTools.user.companyDetails")),
            "Company details is not displayed");
        return this;
    }

    public EditUserProfilePage clickEditProfile()
    {
        LOG.info("Click Edit Profile");
        webElementInteraction.clickElement(editProfile);
        return new EditUserProfilePage(webDriver);
    }

    public UserProfilePage assertSummaryIs(String summaryValue)
    {
        LOG.info("Assert summary value is: {}", summaryValue);
        assertEquals(webElementInteraction.getElementText(summary), summaryValue, "Summary is correct");
        return this;
    }

    private List<String> getAboutUserInfo()
    {
        List<WebElement> aboutDetails = webElementInteraction.waitUntilElementsAreVisible(aboutUserDetails);
        return aboutDetails.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public UserProfilePage assertAboutUserHasValues(String... values)
    {
        LOG.info("Assert values {} are displayed in About User section", Arrays.asList(values));
        assertTrue(getAboutUserInfo().containsAll(Arrays.asList(values)), "All values are displayed in About section");
        return this;
    }

    public UserProfilePage assertUserInfoIsEmpty()
    {
        LOG.info("Assert About user has no values");
        assertFalse(webElementInteraction.isElementDisplayed(aboutUserDetails), "About user info is displayed");
        return this;
    }

    private UserProfilePage checkValue(String element, String label, String value)
    {
        assertEquals(webElementInteraction.findElement(By.xpath(String.format(element,
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
        LOG.info("Assert default avatar is displayed");
        WebElement photoElement = webElementInteraction.waitUntilElementIsVisible(photo);
        webElementInteraction.waitUntilElementHasAttribute(photoElement, "src", "no-user-photo-64.png");
        assertTrue(photoElement.getAttribute("src").contains("no-user-photo-64.png"));
        return this;
    }

    public UserProfilePage assertNewAvatarIsDisplayed()
    {
        LOG.info("Assert new avatar is displayed");
        assertTrue(webElementInteraction.waitUntilElementIsVisible(photo)
            .getAttribute("src").contains("/content/thumbnails/avatar"));
        return this;
    }
}