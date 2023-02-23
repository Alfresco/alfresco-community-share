package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteUserDialogPage;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;

@Slf4j
public class UserProfileAdminToolsPage extends SharePage2<UserProfileAdminToolsPage>
{
    private String userName;

    private final By accountStatus = By.cssSelector("span[id$='_default-view-enabled']");
    private final By editUserButton = By.cssSelector("button[id$='_default-edituser-button-button']");
    private final By deleteUserButton = By.cssSelector("button[id$='_default-deleteuser-button-button']");
    private final By goBackButton = By.cssSelector("button[id$='_default-goback-button-button']");
    private final By userNameInAboutSection = By.cssSelector("div [id$='_default-view-name']");
    private final By userProfileHeaderList = By.cssSelector("div.header-bar");
    private final By infoFromSections = By.cssSelector(".field-label-right");
    private final By userQuota = By.cssSelector("span[id$='_default-view-quota']");
    private final By email = By.cssSelector("span[id$='_default-view-email']");
    private final By userLink = By.xpath("//a[@title=\"User Management\"]");
    private final By newUserLink = By.xpath("//button[text()=\"New User\"]");
    private final By addedGroups = By.cssSelector("span[id*='default-view-groups']");
    private final By userProfileUserName = By.cssSelector("div[class='title'] span[id$='default-view-title']");
    private final By userPhoto = By.cssSelector( "div.photo img.view-photoimg");

    public UserProfileAdminToolsPage(ThreadLocal<WebDriver> webDriver)
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
        return "share/page/console/admin-console/users#state=panel%3Dview%26userid%3D" + getUserName() + "%26search%3D";
    }

    public synchronized UserProfileAdminToolsPage navigate(String userName)
    {
        setUserName(userName);
        super.navigate();
        try
        {
            waitUntilElementIsVisible(editUserButton);
        }
        catch (TimeoutException e)
        {
            log.error("Retry navigate to user profile admin page");
            super.navigate();
        }
        return this;
    }
    public ApplicationPage users()
    {
        log.info("Click Admin Tools");
        clickElement(userLink);
        return new ApplicationPage(webDriver);
    }
    public ApplicationPage newUsers()
    {
        log.info("Click Admin Tools");
        clickElement(newUserLink);
        return new ApplicationPage(webDriver);
    }

    public synchronized UserProfileAdminToolsPage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    public UserProfileAdminToolsPage assertUserIsDisplayedInTitle(UserModel user)
    {
        log.info("Assert user first and last name are displayed in page title");
        assertEquals(getElementText(userProfileUserName),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public UserProfileAdminToolsPage assertEditUserButtonIsDisplayed()
    {
        log.info("Assert Edit User button is displayed");
        assertTrue(isElementDisplayed(editUserButton), "Edit User button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertDeleteUserButtonIsDisplayed()
    {
        log.info("Assert Delete User button is displayed");
        assertTrue(isElementDisplayed(deleteUserButton), "Delete User button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertGoBackButtonIsDisplayed()
    {
        log.info("Assert Go back button is displayed");
        assertTrue(isElementDisplayed(goBackButton), "Go back button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertAllSectionsAreDisplayed()
    {
        log.info("Assert all sections are displayed");
        List<String> sectionTexts = getTextFromElementList(findElements(userProfileHeaderList));
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
        log.info("Assert all fields are displayed in each section");
        List<String> sectionTexts = getTextFromElementList(findElements(infoFromSections));
        List<String> expectedInfo = Collections.synchronizedList(sectionTexts);
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
        log.info("Assert user photo is displayed");
        waitUntilElementIsVisible(userPhoto);
        assertTrue(isElementDisplayed(userPhoto), "User photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertUserFullNameIsDisplayedInAboutSection(UserModel user)
    {
        log.info("Assert user full name is displayed in about section {} {}", user.getFirstName(), user.getLastName());
        assertEquals(getElementText(userNameInAboutSection),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public EditUserPage clickEditUser()
    {
        log.info("Click Edit");
        clickElement(editUserButton);
        return new EditUserPage(webDriver);
    }

    public UserProfileAdminToolsPage assertEmailIs(String expectedEmail)
    {
        log.info("Assert email is: {}", expectedEmail);
        assertEquals(getElementText(email), expectedEmail, "Email is correct");
        return this;
    }

    public DeleteUserDialogPage clickDelete()
    {
        log.info("Click Delete");
        clickElement(deleteUserButton);
        return new DeleteUserDialogPage(webDriver);
    }

    public UserProfileAdminToolsPage assertQuotaIs(String expectedQuota)
    {
        log.info("Assert user quota is: {}", expectedQuota);
        assertEquals(waitUntilElementIsVisible(userQuota).getText(), expectedQuota, "User quota is correct");
        return this;
    }

    public UserProfileAdminToolsPage assertAccountStatusIsDisabled()
    {
        log.info("Assert account is disabled");
        WebElement status = waitUntilElementIsVisible(accountStatus);
        scrollToElement(status);
        assertEquals(status.getText(), language.translate("adminTools.user.status.disabled"));

        return this;
    }

    public UserProfileAdminToolsPage assertAccountStatusIsEnabled()
    {
        log.info("Assert account is enabled");
        WebElement status = waitUntilElementIsVisible(accountStatus);
        scrollToElement(status);
        assertEquals(status.getText(), language.translate("adminTools.user.status.enabled"));

        return this;
    }

    public UserProfileAdminToolsPage assertGroupsAreDisplayed(String... groupNames)
    {
        String [] items = waitUntilElementIsVisible(addedGroups).getText().split("\\s*,\\s*");
        Arrays.sort(items);
        Arrays.sort(groupNames);
        assertEquals(items, groupNames);

        return this;
    }

    public UserProfileAdminToolsPage assertGroupIsNotDisplayed(String groupName)
    {
        assertFalse(findElement(addedGroups).getText().contains(groupName),
            String.format("Group %s is displayed", groupName));
        return this;
    }
}
