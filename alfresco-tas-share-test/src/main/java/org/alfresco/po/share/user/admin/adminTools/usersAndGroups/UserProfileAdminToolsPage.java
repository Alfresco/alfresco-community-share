package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.BasePages;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteUserDialogPage;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserProfileAdminToolsPage extends BasePages<UserProfileAdminToolsPage>
{
    private String userName;

    private final By accountStatus = By.cssSelector("span[id$='_default-view-enabled']");
    @RenderWebElement
    private final By editUserButton = By.cssSelector("button[id$='_default-edituser-button-button']");
    @RenderWebElement
    private final By deleteUserButton = By.cssSelector("button[id$='_default-deleteuser-button-button']");
    private final By goBackButton = By.cssSelector("button[id$='_default-goback-button-button']");
    private final By userNameInAboutSection = By.cssSelector("div [id$='_default-view-name']");
    private final By userProfileHeaderList = By.cssSelector("div.header-bar");
    private final By infoFromSections = By.cssSelector(".field-label-right");
    private final By userQuota = By.cssSelector("span[id$='_default-view-quota']");
    private final By email = By.cssSelector("span[id$='_default-view-email']");
    private final By addedGroups = By.cssSelector("span[id*='default-view-groups']");
    private final By userProfileUserName = By.cssSelector("span[id$='_default-view-title']");
    private final By userPhoto = By.cssSelector( "div.photo img.view-photoimg");

    public UserProfileAdminToolsPage(ThreadLocal<WebBrowser> browser)
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
        return "share/page/console/admin-console/users#state=panel%3Dview%26userid%3D" + getUserName() + "%26search%3D";
    }

    public UserProfileAdminToolsPage navigate(String userName)
    {
        setUserName(userName);
        return navigate();
    }

    public UserProfileAdminToolsPage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    public UserProfileAdminToolsPage assertUserIsDisplayedInTitle(UserModel user)
    {
        LOG.info("Assert user first and last name are displayed in page title");
        assertEquals(getBrowser().waitUntilElementVisible(userProfileUserName).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public UserProfileAdminToolsPage assertEditUserButtonIsDisplayed()
    {
        LOG.info("Assert Edit User button is displayed");
        assertTrue(getBrowser().isElementDisplayed(editUserButton), "Edit User button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertDeleteUserButtonIsDisplayed()
    {
        LOG.info("Assert Delete User button is displayed");
        assertTrue(getBrowser().isElementDisplayed(deleteUserButton), "Delete User button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertGoBackButtonIsDisplayed()
    {
        LOG.info("Assert Go back button is displayed");
        assertTrue(getBrowser().isElementDisplayed(goBackButton), "Go back button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertAllSectionsAreDisplayed()
    {
        LOG.info("Assert all sections are displayed");
        List<String> sectionTexts = getBrowser().getTextFromElementList(getBrowser().findElements(userProfileHeaderList));
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.about")),
            "About section is displayed");
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.contactInfo")),
            "Contact Information section is displayed");
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.companyDetails")),
            "Company Details section is displayed");
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.moreAboutUser")),
            "More about this user section is displayed");

        return this;
    }

    public UserProfileAdminToolsPage assertAllInfoAreDisplayedInSections()
    {
        LOG.info("Assert all filds are displayed in each section");
        List<String> sectionTexts = getBrowser().getTextFromElementList(getBrowser().findElements(infoFromSections));
        List<String> expectedInfo = new ArrayList<>();
        expectedInfo.add(language.translate("adminTools.user.email"));
        expectedInfo.add(language.translate("adminTools.user.telephone"));
        expectedInfo.add(language.translate("adminTools.user.mobile"));
        expectedInfo.add(language.translate("adminTools.user.skype"));
        expectedInfo.add(language.translate("adminTools.user.IM"));
        expectedInfo.add(language.translate("adminTools.user.googleUsername"));
        expectedInfo.add(language.translate("adminTools.user.name"));
        expectedInfo.add(language.translate("adminTools.user.address"));
        expectedInfo.add(language.translate("adminTools.user.fax"));
        expectedInfo.add(language.translate("adminTools.user.userName"));
        expectedInfo.add(language.translate("adminTools.user.groups"));
        expectedInfo.add(language.translate("adminTools.user.accountStatus"));
        expectedInfo.add(language.translate("adminTools.user.quota"));
        expectedInfo.add(language.translate("adminTools.user.usage"));
        assertTrue(sectionTexts.containsAll(expectedInfo), "All sections info are displayed");

        return this;
    }

    public UserProfileAdminToolsPage assertUserPhotoIsDisplayed()
    {
        LOG.info("Assert user photo is displayed");
        assertTrue(getBrowser().isElementDisplayed(userPhoto), "User photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertUserFullNameIsDisplayedInAboutSection(UserModel user)
    {
        LOG.info("Assert user full name is displayed in about section {} {}", user.getFirstName(), user.getLastName());
        assertEquals(getBrowser().findElement(userNameInAboutSection).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public EditUserPage clickEditUser()
    {
        LOG.info("Click Edit");
        getBrowser().waitUntilElementClickable(editUserButton).click();
        return (EditUserPage) new EditUserPage(browser).renderedPage();
    }

    public UserProfileAdminToolsPage assertEmailIs(String expectedEmail)
    {
        LOG.info(String.format("Assert email is: %s", expectedEmail));
        assertEquals(getElementText(email), expectedEmail, "Email is correct");
        return this;
    }

    public DeleteUserDialogPage clickDelete()
    {
        LOG.info("Click Delete");
        getBrowser().waitUntilElementClickable(deleteUserButton).click();
        return (DeleteUserDialogPage) new DeleteUserDialogPage(browser).renderedPage();
    }

    public UserProfileAdminToolsPage assertQuotaIs(String expectedQuota)
    {
        LOG.info(String.format("Assert user quota is: %s", expectedQuota));
        assertEquals(getBrowser().waitUntilElementVisible(userQuota).getText(), expectedQuota, "User quota is correct");
        return this;
    }

    public UserProfileAdminToolsPage assertAccountStatusIsDisabled()
    {
        LOG.info("Assert account is disabled");
        WebElement status = getBrowser().waitUntilElementVisible(accountStatus);
        getBrowser().scrollToElement(status);
        assertEquals(status.getText(), language.translate("adminTools.user.status.disabled"));

        return this;
    }

    public UserProfileAdminToolsPage assertAccountStatusIsEnabled()
    {
        LOG.info("Assert account is enabled");
        WebElement status = getBrowser().waitUntilElementVisible(accountStatus);
        getBrowser().scrollToElement(status);
        assertEquals(status.getText(), language.translate("adminTools.user.status.enabled"));

        return this;
    }

    public UserProfileAdminToolsPage assertGroupsAreDisplayed(String... groupNames)
    {
        String [] items = getBrowser().waitUntilElementVisible(addedGroups).getText().split("\\s*,\\s*");
        Arrays.sort(items);
        Arrays.sort(groupNames);
        assertEquals(items, groupNames);

        return this;
    }

    public UserProfileAdminToolsPage assertGroupIsNotDisplayed(String groupName)
    {
        Assert.assertFalse(getElementText(addedGroups).contains(groupName),
            String.format("Group %s is displayed", groupName));
        return this;
    }
}
