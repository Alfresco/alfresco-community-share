package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;

import java.util.List;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class EditUserProfilePage extends SharePage<EditUserProfilePage>
{
    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    private UploadFileDialog uploadDialog;

    @RenderWebElement
    @FindBy(css = "input[id$='input-firstName']")
    private TextInput firstNameInput;
    @RenderWebElement
    @FindBy(css = "input[id$='input-lastName']")
    private TextInput lastNameInput;
    @RenderWebElement
    @FindBy(css = "input[id$='input-jobtitle']")
    private TextInput jobTitleInput;
    @RenderWebElement
    @FindBy(css = "input[id$='input-location']")
    private TextInput locationInput;
    @FindBy(css = "textarea[id$='input-bio']")
    private TextInput summaryInput;
    @FindBy(css = "button[@id$='button-upload-button']")
    private Button uploadAvatar;
    @FindBy(css = "input[id$='input-telephone']")
    private TextInput telephoneInput;
    @FindBy(css = "input[id$='input-mobile']")
    private TextInput mobileInput;
    @FindBy(css = "input[id$='input-email']")
    private TextInput emailInput;
    @FindBy(css = "input[id$='input-skype']")
    private TextInput skypeInput;
    @FindBy(css = "input[id$='input-instantmsg']")
    private TextInput instantmsgInput;
    @FindBy(css = "input[id$='input-googleusername']")
    private TextInput googleUserNameInput;
    @FindBy(css = "input[id$='input-organization']")
    private TextInput companyNameInput;
    @FindBy(css = "input[id$='input-companyaddress1']")
    private TextInput companyAddress1Input;
    @FindBy(css = "input[id$='input-companyaddress2']")
    private TextInput companyAddress2Input;
    @FindBy(css = "input[id$='input-companyaddress3']")
    private TextInput companyAddress3Input;
    @FindBy(css = "input[id$='input-companypostcode']")
    private TextInput companyPostCodeInput;
    @FindBy(css = "input[id$='input-companytelephone']")
    private TextInput companyTelephoneInput;
    @FindBy(css = "input[id$='input-companyemail']")
    private TextInput companyEmailInput;
    @FindBy(css = "input[id$='input-companyfax']")
    private TextInput companyFaxInput;
    @FindBy(css = ".photoimg")
    private WebElement avatar;

    @FindBy(css = "button[id$='button-clearphoto-button']")
    private Button useDefaultPhoto;
    @FindBy(css = "button[id$='default-button-upload-button']")
    private Button uploadPhoto;

    @FindAll(@FindBy(css = ".phototxt"))
    private List<WebElement> imageInstructions;
    
    @FindBy(css = "button[id$='button-cancel-button']")
    private Button cancel;
    @FindBy(css = "button[id$='save-button']")
    private Button save;

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

    private void typeUserDetail(TextInput input, String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    public void setAboutInformation(String firstName, String lastName, String jobTitle, String location, String summary)
    {
        AboutUserDetails aboutUser = new AboutUserDetails(firstName, lastName, jobTitle, location, summary);
        typeUserDetail(firstNameInput, aboutUser.getFirstName());
        typeUserDetail(lastNameInput, aboutUser.getLastName());
        typeUserDetail(jobTitleInput, aboutUser.getJobTitle());
        typeUserDetail(locationInput, aboutUser.getLocation());
        typeUserDetail(summaryInput, aboutUser.getSummary());
    }
    
    public void setContactInformation(String telephone, String mobile, String email, 
                                      String skype, String im, String googleUserName)
    {
        ContactInformation contactInfo = new ContactInformation(telephone, mobile, email, skype, im, googleUserName);
        typeUserDetail(telephoneInput, contactInfo.getTelephone());
        typeUserDetail(mobileInput, contactInfo.getMobile());
        typeUserDetail(emailInput, contactInfo.getEmail());
        typeUserDetail(skypeInput, contactInfo.getSkype());
        typeUserDetail(instantmsgInput, contactInfo.getIm());
        typeUserDetail(googleUserNameInput, contactInfo.getGoogleUserName());
    }
    
    public void setCompanyDetails(String name, String address1, String address2, String address3, String postCode,
                                  String telephone, String fax, String email)
    {
        CompanyDetails companyDetails = new CompanyDetails(name, address1, address2, address3, postCode, telephone, fax, email);
        typeUserDetail(companyNameInput, companyDetails.getName());
        typeUserDetail(companyAddress1Input, companyDetails.getAddress1());
        typeUserDetail(companyAddress2Input, companyDetails.getAddress2());
        typeUserDetail(companyAddress3Input, companyDetails.getAddress3());
        typeUserDetail(companyPostCodeInput, companyDetails.getPostCode());
        typeUserDetail(companyTelephoneInput, companyDetails.getTelephone());
        typeUserDetail(companyFaxInput, companyDetails.getFax());
        typeUserDetail(companyEmailInput, companyDetails.getEmail());
    }
    
    public boolean isFirstNameDisplayed()
    {
        return firstNameInput.isDisplayed();
    }
    
    public boolean isLastNameDisplayed()
    {
        return lastNameInput.isDisplayed();
    }
    
    public boolean isJobTitleDisplayed()
    {
        return jobTitleInput.isDisplayed();
    }

    public boolean isLocationDisplayed()
    {
        return locationInput.isDisplayed();
    }
    
    public boolean isSummaryDisplayed()
    {
        return summaryInput.isDisplayed();
    }
    
    public boolean isTelephoneDisplayed()
    {
        return telephoneInput.isDisplayed();
    }
    
    public boolean isMobileDisplayed()
    {
        return mobileInput.isDisplayed();
    }
    
    public boolean isEmailDisplayed()
    {
        return emailInput.isDisplayed();
    }
    
    public boolean isSkypeDisplayed()
    {
        return skypeInput.isDisplayed();
    }
    
    public boolean isIMDisplayed()
    {
        return instantmsgInput.isDisplayed();
    }
    
    public boolean isGoogleUserNameDisplayed()
    {
        return googleUserNameInput.isDisplayed();
    }
    
    public boolean isCompanyNameDisplayed()
    {
        return companyNameInput.isDisplayed();
    }
    
    public boolean isCompanyAddress1Displayed()
    {
        return companyAddress1Input.isDisplayed();
    }
    
    public boolean isCompanyAddress2Displayed()
    {
        return companyAddress2Input.isDisplayed();
    }
    
    public boolean isCompanyAddress3Displayed()
    {
        return companyAddress3Input.isDisplayed();
    }
    
    public boolean isCompanyPostCodeDisplayed()
    {
        return companyPostCodeInput.isDisplayed();
    }
    
    public boolean isCompanyTelephoneDisplayed()
    {
        return companyTelephoneInput.isDisplayed();
    }
    
    public boolean isCompanyFaxDisplayed()
    {
        return companyFaxInput.isDisplayed();
    }
    
    public boolean isCompanyEmailDisplayed()
    {
        return companyEmailInput.isDisplayed();
    }

    public void clickUseDefaultPhoto()
    {
        useDefaultPhoto.click();
    }
    
    public boolean isPhotoDisplayed()
    {
        return avatar.isEnabled();
    }

    public EditUserProfilePage uploadNewPhoto(String pathToPhoto)
    {
        clickUpload().uploadFile(pathToPhoto);
        return (EditUserProfilePage) this.renderedPage();
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
        save.click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }
    
    public String getPhotoSrc()
    {
        return avatar.getAttribute("src");
    }
}
