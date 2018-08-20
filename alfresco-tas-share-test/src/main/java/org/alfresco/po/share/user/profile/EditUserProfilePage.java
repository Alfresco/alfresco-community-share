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
import org.openqa.selenium.JavascriptExecutor;


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
    private WebElement firstNameInput;
    @RenderWebElement
    @FindBy(css = "input[id$='input-lastName']")
    private WebElement lastNameInput;
    @RenderWebElement
    @FindBy(css = "input[id$='input-jobtitle']")
    private WebElement jobTitleInput;
    @RenderWebElement
    @FindBy(css = "input[id$='input-location']")
    private WebElement locationInput;
    @FindBy(css = "textarea[id$='input-bio']")
    private WebElement summaryInput;
    @FindBy(css = "button[@id$='button-upload-button']")
    private WebElement uploadAvatar;
    @FindBy(css = "input[id$='input-telephone']")
    private WebElement telephoneInput;
    @FindBy(css = "input[id$='input-mobile']")
    private WebElement mobileInput;
    @FindBy(css = "input[id$='input-email']")
    private WebElement emailInput;
    @FindBy(css = "input[id$='input-skype']")
    private WebElement skypeInput;
    @FindBy(css = "input[id$='input-instantmsg']")
    private WebElement instantmsgInput;
    @FindBy(css = "input[id$='input-googleusername']")
    private WebElement googleUserNameInput;
    @FindBy(css = "input[id$='input-organization']")
    private WebElement companyNameInput;
    @FindBy(css = "input[id$='input-companyaddress1']")
    private WebElement companyAddress1Input;
    @FindBy(css = "input[id$='input-companyaddress2']")
    private WebElement companyAddress2Input;
    @FindBy(css = "input[id$='input-companyaddress3']")
    private WebElement companyAddress3Input;
    @FindBy(css = "input[id$='input-companypostcode']")
    private WebElement companyPostCodeInput;
    @FindBy(css = "input[id$='input-companytelephone']")
    private WebElement companyTelephoneInput;
    @FindBy(css = "input[id$='input-companyemail']")
    private WebElement companyEmailInput;
    @FindBy(css = "input[id$='input-companyfax']")
    private WebElement companyFaxInput;
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
    @FindBy(css = "span[id$='button-save']")
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

    private void typeUserDetail(WebElement input, String value)
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
        return browser.isElementDisplayed(firstNameInput);
    }
    
    public boolean isLastNameDisplayed()
    {
        return browser.isElementDisplayed(lastNameInput);
    }
    
    public boolean isJobTitleDisplayed()
    {
        return browser.isElementDisplayed(jobTitleInput);
    }

    public boolean isLocationDisplayed()
    {
        return browser.isElementDisplayed(locationInput);
    }
    
    public boolean isSummaryDisplayed()
    {
        return browser.isElementDisplayed(summaryInput);
    }
    
    public boolean isTelephoneDisplayed()
    {
        return browser.isElementDisplayed(telephoneInput);
    }
    
    public boolean isMobileDisplayed()
    {
        return browser.isElementDisplayed(mobileInput);
    }
    
    public boolean isEmailDisplayed()
    {
        return browser.isElementDisplayed(emailInput);
    }
    
    public boolean isSkypeDisplayed()
    {
        return browser.isElementDisplayed(skypeInput);
    }
    
    public boolean isIMDisplayed()
    {
        return browser.isElementDisplayed(instantmsgInput);
    }
    
    public boolean isGoogleUserNameDisplayed()
    {
        return browser.isElementDisplayed(googleUserNameInput);
    }
    
    public boolean isCompanyNameDisplayed()
    {
        return browser.isElementDisplayed(companyNameInput);
    }
    
    public boolean isCompanyAddress1Displayed()
    {
        return browser.isElementDisplayed(companyAddress1Input);
    }
    
    public boolean isCompanyAddress2Displayed()
    {
        return browser.isElementDisplayed(companyAddress2Input);
    }
    
    public boolean isCompanyAddress3Displayed()
    {
        return browser.isElementDisplayed(companyAddress3Input);
    }
    
    public boolean isCompanyPostCodeDisplayed()
    {
        return browser.isElementDisplayed(companyPostCodeInput);
    }
    
    public boolean isCompanyTelephoneDisplayed()
    {
        return browser.isElementDisplayed(companyTelephoneInput);
    }
    
    public boolean isCompanyFaxDisplayed()
    {
        return browser.isElementDisplayed(companyFaxInput);
    }
    
    public boolean isCompanyEmailDisplayed()
    {
        return browser.isElementDisplayed(companyEmailInput);
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
     //   ((JavascriptExecutor) getBrowser()).executeScript("window.scrollBy(0,500)");
        save.click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }
    
    public String getPhotoSrc()
    {
        return avatar.getAttribute("src");
    }
}
