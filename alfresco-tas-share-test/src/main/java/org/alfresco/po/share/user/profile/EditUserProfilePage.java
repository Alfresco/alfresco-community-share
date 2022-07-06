package org.alfresco.po.share.user.profile;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Set About Information details");
        waitUntilElementIsVisible(firstNameInput);
        clearAndType(firstNameInput, firstName);
        clearAndType(lastNameInput, lastName);
        clearAndType(jobTitleInput, jobTitle);
        clearAndType(locationInput, location);
        clearAndType(summaryInput, summary);
        clearAndType(firstNameInput, firstName);

        return this;
    }

    public EditUserProfilePage setContactInformation(String telephone, String mobile, String email,
                                      String skype, String im, String googleUserName)
    {
        log.info("Set Contact Information details");
        clearAndType(telephoneInput, telephone);
        clearAndType(mobileInput, mobile);
        clearAndType(emailInput, email);
        clearAndType(skypeInput, skype);
        clearAndType(instantmsgInput, im);
        clearAndType(googleUserNameInput, googleUserName);

        return this;
    }

    public EditUserProfilePage setCompanyDetails(String name, String address1, String address2, String address3, String postCode,
                                  String telephone, String fax, String email)
    {
        log.info("Set Company Information details");
        clearAndType(companyNameInput, name);
        clearAndType(companyAddress1Input, address1);
        clearAndType(companyAddress2Input, address2);
        clearAndType(companyAddress3Input, address3);
        clearAndType(companyPostCodeInput, postCode);
        clearAndType(companyTelephoneInput, telephone);
        clearAndType(companyFaxInput, fax);
        clearAndType(companyEmailInput, email);

        return this;
    }

    public EditUserProfilePage clickUseDefaultPhoto()
    {
        clickElement(useDefaultPhoto);
        waitUntilElementHasAttribute(findElement(avatar), "src", "no-user-photo-64.png");
        return this;
    }

    public EditUserProfilePage uploadNewPhoto(String pathToPhoto)
    {
         clickUpload().uploadFile(pathToPhoto);
         return this;
    }

    public UploadFileDialog clickUpload()
    {
        clickElement(uploadPhoto);
        return new UploadFileDialog(webDriver);
    }

    public UserProfilePage clickCancel()
    {
        scrollToElement(findElement(cancel));
        clickElement(cancel);
        return new UserProfilePage(webDriver);
    }

    public UserProfilePage clickSave()
    {
        log.info("Click Save");
        WebElement saveBtn = waitUntilElementIsVisible(save);
        mouseOver(saveBtn);
        clickJS(saveBtn);
        waitUntilElementDisappears(save, WAIT_5.getValue());

        return new UserProfilePage(webDriver);
    }
}
