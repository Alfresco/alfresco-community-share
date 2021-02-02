package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.alfresco.common.Wait.*;

/**
 * @author bogdan.bocancea
 */
public class EditUserProfilePage extends SharePage2<EditUserProfilePage>
{
    private String userName;

    private final By firstNameInput = By.cssSelector("input[id$='input-firstName']");
    private final By lastNameInput = By.cssSelector("input[id$='input-lastName']");
    private final By jobTitleInput = By.cssSelector("input[id$='input-jobtitle']");
    private final By locationInput = By.cssSelector("input[id$='input-location']");
    private final By summaryInput = By.cssSelector("textarea[id$='input-bio']");
    private final By telephoneInput = By.cssSelector("input[id$='input-telephone']");
    private final By mobileInput = By.cssSelector("input[id$='input-mobile']");
    private final By emailInput = By.cssSelector("input[id$='input-email']");
    private final By skypeInput = By.cssSelector("input[id$='input-skype']");
    private final By instantmsgInput = By.cssSelector("input[id$='input-instantmsg']");
    private final By googleUserNameInput = By.cssSelector("input[id$='input-googleusername']");
    private final By companyNameInput = By.cssSelector("input[id$='input-organization']");
    private final By companyAddress1Input = By.cssSelector("input[id$='input-companyaddress1']");
    private final By companyAddress2Input = By.cssSelector("input[id$='input-companyaddress2']");
    private final By companyAddress3Input = By.cssSelector("input[id$='input-companyaddress3']");
    private final By companyPostCodeInput = By.cssSelector("input[id$='input-companypostcode']");
    private final By companyTelephoneInput = By.cssSelector("input[id$='input-companytelephone']");
    private final By companyEmailInput = By.cssSelector("input[id$='input-companyemail']");
    private final By companyFaxInput = By.cssSelector("input[id$='input-companyfax']");
    private final By avatar = By.cssSelector("div[id$='editview'] .photoimg");
    private final By useDefaultPhoto = By.cssSelector("button[id$='button-clearphoto-button']");
    private final By uploadPhoto = By.cssSelector("button[id$='default-button-upload-button']");
    private final By cancel = By.cssSelector("button[id$='button-cancel-button']");
    private final By save = By.cssSelector("button[id$='button-save-button']");

    public EditUserProfilePage(ThreadLocal<WebDriver> webDriver)
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

    public EditUserProfilePage navigate(String userName)
    {
        UserProfilePage userProfilePage = new UserProfilePage(webDriver);
        userProfilePage.navigate(userName);
        return userProfilePage.clickEditProfile();
    }

    public EditUserProfilePage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    public EditUserProfilePage setAboutInformation(String firstName, String lastName, String jobTitle, String location, String summary)
    {
        LOG.info("Set About Information details");
        webElementInteraction.waitUntilElementIsVisible(firstNameInput);
        webElementInteraction.clearAndType(firstNameInput, firstName);
        webElementInteraction.clearAndType(lastNameInput, lastName);
        webElementInteraction.clearAndType(jobTitleInput, jobTitle);
        webElementInteraction.clearAndType(locationInput, location);
        webElementInteraction.clearAndType(summaryInput, summary);
        webElementInteraction.clearAndType(firstNameInput, firstName);

        return this;
    }

    public EditUserProfilePage setContactInformation(String telephone, String mobile, String email,
                                      String skype, String im, String googleUserName)
    {
        LOG.info("Set Contact Information details");
        webElementInteraction.clearAndType(telephoneInput, telephone);
        webElementInteraction.clearAndType(mobileInput, mobile);
        webElementInteraction.clearAndType(emailInput, email);
        webElementInteraction.clearAndType(skypeInput, skype);
        webElementInteraction.clearAndType(instantmsgInput, im);
        webElementInteraction.clearAndType(googleUserNameInput, googleUserName);

        return this;
    }

    public EditUserProfilePage setCompanyDetails(String name, String address1, String address2, String address3, String postCode,
                                  String telephone, String fax, String email)
    {
        LOG.info("Set Company Information details");
        webElementInteraction.clearAndType(companyNameInput, name);
        webElementInteraction.clearAndType(companyAddress1Input, address1);
        webElementInteraction.clearAndType(companyAddress2Input, address2);
        webElementInteraction.clearAndType(companyAddress3Input, address3);
        webElementInteraction.clearAndType(companyPostCodeInput, postCode);
        webElementInteraction.clearAndType(companyTelephoneInput, telephone);
        webElementInteraction.clearAndType(companyFaxInput, fax);
        webElementInteraction.clearAndType(companyEmailInput, email);

        return this;
    }

    public EditUserProfilePage clickUseDefaultPhoto()
    {
        webElementInteraction.clickElement(useDefaultPhoto);
        webElementInteraction.waitUntilElementHasAttribute(webElementInteraction.findElement(avatar), "src", "no-user-photo-64.png");
        return this;
    }

    public EditUserProfilePage uploadNewPhoto(String pathToPhoto)
    {
         clickUpload().uploadFile(pathToPhoto);
         return this;
    }

    public UploadFileDialog clickUpload()
    {
        webElementInteraction.clickElement(uploadPhoto);
        return new UploadFileDialog(webDriver);
    }

    public UserProfilePage clickCancel()
    {
        webElementInteraction.scrollToElement(webElementInteraction.findElement(cancel));
        webElementInteraction.clickElement(cancel);
        return new UserProfilePage(webDriver);
    }

    public UserProfilePage clickSave()
    {
        LOG.info("Click Save");
        WebElement saveBtn = webElementInteraction.waitUntilElementIsVisible(save);
        webElementInteraction.mouseOver(saveBtn);
        webElementInteraction.clickJS(saveBtn);
        webElementInteraction.waitUntilElementDisappears(save, WAIT_5.getValue());

        return new UserProfilePage(webDriver);
    }
}
