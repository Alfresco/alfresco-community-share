package org.alfresco.po.share.user.profile;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class EditUserProfilePage extends SharePage<EditUserProfilePage>
{
    @Autowired
    private UserProfilePage userProfilePage;

    @Autowired
    private UploadFileDialog uploadDialog;

    @RenderWebElement
    @FindBy (css = "input[id$='input-firstName']")
    private WebElement firstNameInput;

    @RenderWebElement
    @FindBy (css = "input[id$='input-lastName']")
    private WebElement lastNameInput;

    @RenderWebElement
    @FindBy (css = "input[id$='input-jobtitle']")
    private WebElement jobTitleInput;

    @RenderWebElement
    @FindBy (css = "input[id$='input-location']")
    private WebElement locationInput;

    @FindBy (css = "textarea[id$='input-bio']")
    private WebElement summaryInput;

    @FindBy (css = "button[@id$='button-upload-button']")
    private WebElement uploadAvatar;

    @FindBy (css = "input[id$='input-telephone']")
    private WebElement telephoneInput;

    @FindBy (css = "input[id$='input-mobile']")
    private WebElement mobileInput;

    @FindBy (css = "input[id$='input-email']")
    private WebElement emailInput;

    @FindBy (css = "input[id$='input-skype']")
    private WebElement skypeInput;

    @FindBy (css = "input[id$='input-instantmsg']")
    private WebElement instantmsgInput;

    @FindBy (css = "input[id$='input-googleusername']")
    private WebElement googleUserNameInput;

    @FindBy (css = "input[id$='input-organization']")
    private WebElement companyNameInput;

    @FindBy (css = "input[id$='input-companyaddress1']")
    private WebElement companyAddress1Input;

    @FindBy (css = "input[id$='input-companyaddress2']")
    private WebElement companyAddress2Input;

    @FindBy (css = "input[id$='input-companyaddress3']")
    private WebElement companyAddress3Input;

    @FindBy (css = "input[id$='input-companypostcode']")
    private WebElement companyPostCodeInput;

    @FindBy (css = "input[id$='input-companytelephone']")
    private WebElement companyTelephoneInput;

    @FindBy (css = "input[id$='input-companyemail']")
    private WebElement companyEmailInput;

    @FindBy (css = "input[id$='input-companyfax']")
    private WebElement companyFaxInput;

    @FindBy (css = ".photoimg")
    private WebElement avatar;

    @FindBy (css = "button[id$='button-clearphoto-button']")
    private Button useDefaultPhoto;

    @FindBy (css = "button[id$='default-button-upload-button']")
    private Button uploadPhoto;

    @FindAll (@FindBy (css = ".phototxt"))
    private List<WebElement> imageInstructions;

    @FindBy (css = "button[id$='button-cancel-button']")
    private WebElement cancel;

    @FindBy (css = "button[id$='save-button']")
    private WebElement save;

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/profile", getUserName());
    }

    public EditUserProfilePage navigate(String userName)
    {
        userProfilePage.navigate(userName);
        userProfilePage.clickEditProfile();
        return (EditUserProfilePage) renderedPage();
    }

    public EditUserProfilePage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    private void typeUserDetail(WebElement input, String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    public EditUserProfilePage setAboutInformation(String firstName, String lastName, String jobTitle, String location, String summary)
    {
        LOG.info("Set About Information details");
        clearAndType(firstNameInput, firstName);
        clearAndType(lastNameInput, lastName);
        clearAndType(jobTitleInput, jobTitle);
        clearAndType(locationInput, location);
        clearAndType(summaryInput, summary);
        return this;
    }

    public EditUserProfilePage setContactInformation(String telephone, String mobile, String email,
                                      String skype, String im, String googleUserName)
    {
        LOG.info("Set Contact Information details");
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
        LOG.info("Set Company Information details");
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
        useDefaultPhoto.click();
        return this;
    }

    public EditUserProfilePage uploadNewPhoto(String pathToPhoto)
    {
        return (EditUserProfilePage) clickUpload().uploadFileAndRenderPage(pathToPhoto, this);
    }

    public UploadFileDialog clickUpload()
    {
        uploadPhoto.click();
        return (UploadFileDialog) uploadDialog.renderedPage();
    }

    public String getPhotoInstructions()
    {
        String instructionValue = "";
        for (WebElement instruction : imageInstructions)
        {
            instructionValue = instructionValue + instruction.getText() + "\n";
        }
        return instructionValue;
    }

    public UserProfilePage clickCancel()
    {
        cancel.click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public UserProfilePage clickSave()
    {
        getBrowser().waitUntilElementClickable(save).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }
}
