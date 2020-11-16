package org.alfresco.po.share.user.profile;


import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * @author bogdan.bocancea
 */
public class UserProfilePage extends SharePage2<UserProfilePage> implements AccessibleByMenuBar
{
    private String userName;
    private static final String EMPTY_SPACE = " ";

    private By editProfile = By.cssSelector("button[id$='button-edit-button']");
    @RenderWebElement
    private By infoLink = By.cssSelector("a[id$='default-profile-link']");
    private By headers = By.cssSelector("div[id$='readview'] .viewcolumn .header-bar");
    private By nameLabel = By.cssSelector(".namelabel");
    private By summary = By.cssSelector(".biorow>div");
    private By aboutUserDetails = By.cssSelector(".fieldlabel");
    private By userInformation = By.cssSelector(".viewcolumn .row");
    @RenderWebElement
    private By sitesLink = By.cssSelector("a[id$='default-user-sites-link']");
    private By contentLink = By.cssSelector("a[id$='user-content-link']");
    private By followingLink = By.cssSelector("a[id$='following-link']");
    private By followersLink = By.cssSelector("a[id$='followers-link']");
    private By changePasswordLink = By.cssSelector("a[id$='change-password-link']");
    private By notificationLink = By.cssSelector("a[id$='user-notifications-link']");
    private By trashcanLink = By.cssSelector("a[id$='user-trashcan-link']");
    private By photo = By.cssSelector("div[id$='readview'] .photoimg");
    private By fieldValue = By.cssSelector(".fieldvalue");
    private String contactInfoDuplicateRow = "(//span[text()= '%s'])[1]/..";
    private String companyInfoDuplicateRow = "(//span[text()= '%s'])[2]/..";
    private String valueRow = "//span[@class='fieldlabelright' and text()='%s']/..";

    public UserProfilePage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
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
        return (UserProfilePage) new Toolbar(browser).clickUserMenu().clickMyProfile().renderedPage();
    }

    public UserProfilePage assertUserProfilePageIsOpened()
    {
        assertTrue(getBrowser().isElementDisplayed(infoLink), "User profile page is opened");
        return this;
    }

    public UserProfilePage assertInfoLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(infoLink), "Info link is displayed");
        return this;
    }

    public UserProfilePage assertSitesLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(sitesLink), "Sites link is displayed");
        return this;
    }

    public UserProfilePage assertContentLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(contentLink), "Content link is displayed");
        return this;
    }

    public UserProfilePage assertImFollowingLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(followingLink), "Following link is displayed");
        return this;
    }

    public UserProfilePage assertFollowingMeLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(followersLink), "Following Me link is displayed");
        return this;
    }

    public UserProfilePage assertChangePasswordLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(changePasswordLink), "Change password link is displayed");
        return this;
    }

    public UserProfilePage assertNotificationsLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(notificationLink), "Notifications link is displayed");
        return this;
    }

    public UserProfilePage assertTrashcanLinkIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(trashcanLink), "Trashcan link is displayed");
        return this;
    }

    public boolean isAboutHeaderDisplayed()
    {
        return getBrowser().isElementDisplayed(getBrowser().findFirstElementWithValue(headers, language.translate("adminTools.user.about")));
    }

    public UserProfilePage assertAboutHeaderIsDisplayed()
    {
        assertNotNull(getBrowser().findFirstElementWithValue(headers, language.translate("adminTools.user.about")),
            "About is not displayed");
        return this;
    }

    public UserProfilePage assertContactInfoHeaderIsDisplayed()
    {
        assertNotNull(getBrowser().findFirstElementWithValue(headers, language.translate("adminTools.user.contactInfo")),
            "Contact information header is not displayed");
        return this;
    }

    public UserProfilePage assertCompanyDetailsHeaderIsDisplayed()
    {
        assertNotNull(getBrowser().findFirstElementWithValue(headers, language.translate("adminTools.user.companyDetails")),
            "Company details is not displayed");
        return this;
    }

    public EditUserProfilePage clickEditProfile()
    {
        LOG.info("Click Edit Profile");
        getBrowser().waitUntilElementVisible(editProfile).click();
        return (EditUserProfilePage) new EditUserProfilePage(browser).renderedPage();
    }

    public UserProfilePage assertUsernameEquals(String firstName, String lastName)
    {
        LOG.info("Assert username equals: {}, {}", firstName, lastName);
        assertEquals(getElementText(nameLabel), firstName.concat(EMPTY_SPACE).concat(lastName),
                String.format("Username not equals %s %s ", firstName, lastName));
        return this;
    }

    public UserProfilePage assertSummaryIs(String summaryValue)
    {
        LOG.info(String.format("Assert summary value is: %s", summaryValue));
        assertEquals(getElementText(summary), summaryValue, "Summary is correct");
        return this;
    }

    private List<String> getAboutUserInfo()
    {
        List<WebElement> aboutDetails = getBrowser().findElements(aboutUserDetails);
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
        assertTrue(getAboutUserInfo().isEmpty(), "User info has no values");
        return this;
    }

    private UserProfilePage checkValue(String element, String label, String value)
    {

        assertEquals(getBrowser().findElement(By.xpath(String.format(element,
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
        WebElement photoElement = getBrowser().findElement(photo);
        getBrowser().waitUntilElementHasAttribute(photoElement, "src", "no-user-photo-64.png");
        assertTrue(photoElement.getAttribute("src").contains("no-user-photo-64.png"));
        return this;
    }

    public UserProfilePage assertNewAvatarIsDisplayed()
    {
        LOG.info("Assert new avatar is displayed");
        assertTrue(getBrowser().findElement(photo).getAttribute("src").contains("/content/thumbnails/avatar"));
        return this;
    }
}