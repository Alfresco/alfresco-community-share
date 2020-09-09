package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteUserDialogPage;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mirela Tifui on 12/8/2016.
 */
@PageObject
public class UserProfileAdminToolsPage extends SharePage<UserProfileAdminToolsPage>
{
    @Autowired
    private EditUserPage editUserPage;

    @Autowired
    private DeleteUserDialogPage deleteUserDialogPage;

    @FindBy (css = "span[id$='_default-view-enabled']")
    public WebElement accountStatus;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-edituser-button-button']")
    private WebElement editUserButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-deleteuser-button-button']")
    private WebElement deleteUserButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-goback-button-button']")
    private WebElement goBackButton;

    @FindBy (css = "div [id$='_default-view-name']")
    private WebElement userNameInAboutSection;

    @FindAll (@FindBy (css = "div.header-bar"))
    private List<WebElement> userProfileHeaderList;

    @FindAll (@FindBy (css = ".field-label-right"))
    private List<WebElement> infoFromSections;

    @FindBy (css = "span[id$='_default-view-quota']")
    private WebElement userQuota;

    @FindBy (css = "span[id$='_default-view-email']")
    private WebElement email;

    @FindBy (css = "span[id*='default-view-groups']")
    private WebElement addedGroups;

    @FindBy (css = "span[id$='_default-view-title']")
    private WebElement userProfileUserName;

    @FindBy (css = "div.photo img.view-photoimg")
    private WebElement userPhoto;

    private By sectionsInfo = By.xpath("//div[@class='field-row']");

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
        Assert.assertEquals(browser.waitUntilElementVisible(userProfileUserName).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public UserProfileAdminToolsPage assertEditUserButtonIsDisplayed()
    {
        LOG.info("Assert Edit User button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(editUserButton), "Edit User button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertDeleteUserButtonIsDisplayed()
    {
        LOG.info("Assert Delete User button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(deleteUserButton), "Delete User button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertGoBackButtonIsDisplayed()
    {
        LOG.info("Assert Go back button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(goBackButton), "Go back button is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertAllSectionsAreDisplayed()
    {
        LOG.info("Assert all sections are displayed");
        List<String> sectionTexts = browser.getTextFromElementList(userProfileHeaderList);
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.about")),
            "About section is displayed");
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.contactInfo")),
            "Contact Information section is displayed");
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.companyDetails")),
            "Company Details section is displayed");
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.moreAboutUser")),
            "More about this user section is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertAllInfoAreDisplayedInSections()
    {
        LOG.info("Assert all filds are displayed in each section");
        List<String> sectionTexts = browser.getTextFromElementList(infoFromSections);
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
        Assert.assertTrue(sectionTexts.containsAll(expectedInfo), "All sections info are displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertUserPhotoIsDisplayed()
    {
        LOG.info("Assert user photo is displayed");
        Assert.assertTrue(browser.isElementDisplayed(userPhoto), "User photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage assertUserFullNameIsDisplayedInAboutSection(UserModel user)
    {
        Assert.assertEquals(userNameInAboutSection.getText(), String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public EditUserPage clickEditUser()
    {
        LOG.info("Click Edit");
        editUserButton.click();
        return (EditUserPage) editUserPage.renderedPage();
    }

    public UserProfileAdminToolsPage assertEmailIs(String expectedEmail)
    {
        LOG.info(String.format("Assert email is: %s", expectedEmail));
        Assert.assertEquals(email.getText(), expectedEmail, "Email is correct");
        return this;
    }

    public DeleteUserDialogPage clickDelete()
    {
        LOG.info("Click Delete");
        deleteUserButton.click();
        return (DeleteUserDialogPage) deleteUserDialogPage.renderedPage();
    }

    public UserProfileAdminToolsPage assertQuotaIs(String expectedQuota)
    {
        LOG.info(String.format("Assert user quota is: %s", expectedQuota));
        Assert.assertEquals(browser.waitUntilElementVisible(userQuota).getText(), expectedQuota, "User quota is correct");
        return this;
    }

    public UserProfileAdminToolsPage assertAccountStatusIsDisabled()
    {
        LOG.info("Assert account is disabled");
        Assert.assertEquals(accountStatus.getText(), language.translate("adminTools.user.status.disabled"));
        return this;
    }

    public UserProfileAdminToolsPage assertAccountStatusIsEnabled()
    {
        LOG.info("Assert account is enabled");
        Assert.assertEquals(accountStatus.getText(), language.translate("adminTools.user.status.enabled"));
        return this;
    }

    public UserProfileAdminToolsPage assertGroupsAreDisplayed(String... groupNames)
    {
        String [] items = browser.waitUntilElementVisible(addedGroups).getText().split("\\s*,\\s*");
        Arrays.sort(items);
        Arrays.sort(groupNames);
        Assert.assertEquals(items, groupNames);
        return this;
    }

    public UserProfileAdminToolsPage assertGroupIsNotDisplayed(String groupName)
    {
        Assert.assertFalse(addedGroups.getText().contains(groupName),
            String.format("Group %s is displayed", groupName));
        return this;
    }
}
