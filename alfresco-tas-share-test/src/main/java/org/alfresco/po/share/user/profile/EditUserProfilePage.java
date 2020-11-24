package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.UploadFileDialog;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author bogdan.bocancea
 */
public class EditUserProfilePage extends SharePage2<EditUserProfilePage>
{
    private String userName;

    @RenderWebElement
    private By firstNameInput = By.cssSelector("input[id$='input-firstName']");
    @RenderWebElement
    private By lastNameInput = By.cssSelector("input[id$='input-lastName']");
    private By jobTitleInput = By.cssSelector("input[id$='input-jobtitle']");
    private By locationInput = By.cssSelector("input[id$='input-location']");
    private By summaryInput = By.cssSelector("textarea[id$='input-bio']");
    private By uploadAvatar = By.cssSelector("button[@id$='button-upload-button']");
    private By telephoneInput = By.cssSelector("input[id$='input-telephone']");
    private By mobileInput = By.cssSelector("input[id$='input-mobile']");
    private By emailInput = By.cssSelector("input[id$='input-email']");
    private By skypeInput = By.cssSelector("input[id$='input-skype']");
    private By instantmsgInput = By.cssSelector("input[id$='input-instantmsg']");
    private By googleUserNameInput = By.cssSelector("input[id$='input-googleusername']");
    private By companyNameInput = By.cssSelector("input[id$='input-organization']");
    private By companyAddress1Input = By.cssSelector("input[id$='input-companyaddress1']");
    private By companyAddress2Input = By.cssSelector("input[id$='input-companyaddress2']");
    private By companyAddress3Input = By.cssSelector("input[id$='input-companyaddress3']");
    private By companyPostCodeInput = By.cssSelector("input[id$='input-companypostcode']");
    private By companyTelephoneInput = By.cssSelector("input[id$='input-companytelephone']");
    private By companyEmailInput = By.cssSelector("input[id$='input-companyemail']");
    private By companyFaxInput = By.cssSelector("input[id$='input-companyfax']");
    private By avatar = By.cssSelector("div[id$='editview'] .photoimg");
    private By useDefaultPhoto = By.cssSelector("button[id$='button-clearphoto-button']");
    private By uploadPhoto = By.cssSelector("button[id$='default-button-upload-button']");
    private By imageInstructions = By.cssSelector(".phototxt");
    private By cancel = By.cssSelector("button[id$='button-cancel-button']");
    private By save = By.cssSelector("button[id$='save-button']");

    public EditUserProfilePage(ThreadLocal<WebBrowser> browser)
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

    public EditUserProfilePage navigate(String userName)
    {
        UserProfilePage userProfilePage = new UserProfilePage(browser);
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
        clearAndType(getBrowser().findElement(firstNameInput), firstName);
        clearAndType(getBrowser().findElement(lastNameInput), lastName);
        clearAndType(getBrowser().findElement(jobTitleInput), jobTitle);
        clearAndType(getBrowser().findElement(locationInput), location);
        clearAndType(getBrowser().findElement(summaryInput), summary);
        return this;
    }

    public EditUserProfilePage setContactInformation(String telephone, String mobile, String email,
                                      String skype, String im, String googleUserName)
    {
        LOG.info("Set Contact Information details");
        clearAndType(getBrowser().findElement(telephoneInput), telephone);
        clearAndType(getBrowser().findElement(mobileInput), mobile);
        clearAndType(getBrowser().findElement(emailInput), email);
        clearAndType(getBrowser().findElement(skypeInput), skype);
        clearAndType(getBrowser().findElement(instantmsgInput), im);
        clearAndType(getBrowser().findElement(googleUserNameInput), googleUserName);
        return this;
    }

    public EditUserProfilePage setCompanyDetails(String name, String address1, String address2, String address3, String postCode,
                                  String telephone, String fax, String email)
    {
        LOG.info("Set Company Information details");
        clearAndType(getBrowser().findElement(companyNameInput), name);
        clearAndType(getBrowser().findElement(companyAddress1Input), address1);
        clearAndType(getBrowser().findElement(companyAddress2Input), address2);
        clearAndType(getBrowser().findElement(companyAddress3Input), address3);
        clearAndType(getBrowser().findElement(companyPostCodeInput), postCode);
        clearAndType(getBrowser().findElement(companyTelephoneInput), telephone);
        clearAndType(getBrowser().findElement(companyFaxInput), fax);
        clearAndType(getBrowser().findElement(companyEmailInput), email);
        return this;
    }

    public EditUserProfilePage clickUseDefaultPhoto()
    {
        getBrowser().waitUntilElementClickable(useDefaultPhoto).click();
        getBrowser().waitUntilElementHasAttribute(getBrowser().findElement(avatar), "src", "no-user-photo-64.png");
        return this;
    }

    public EditUserProfilePage uploadNewPhoto(String pathToPhoto)
    {
         clickUpload().uploadFile(pathToPhoto);
         return this;
    }

    public UploadFileDialog clickUpload()
    {
        getBrowser().findElement(uploadPhoto).click();
        return (UploadFileDialog) new UploadFileDialog(browser).renderedPage();
    }

    public String getPhotoInstructions()
    {
        String instructionValue = "";
        List<WebElement> listInstructions = getBrowser().findElements(imageInstructions);
        for (WebElement instruction : listInstructions)
        {
            instructionValue = instructionValue + instruction.getText() + "\n";
        }
        return instructionValue;
    }

    public UserProfilePage clickCancel()
    {
        getBrowser().findElement(cancel).click();
        return (UserProfilePage) new UserProfilePage(browser).renderedPage();
    }

    public UserProfilePage clickSave()
    {
        LOG.info("Click Save");
        WebElement saveBtn = getBrowser().waitUntilElementClickable(save);
        getBrowser().scrollToElement(saveBtn);
        getBrowser().mouseOver(saveBtn);
        getBrowser().waitUntilElementClickable(saveBtn).click();
        return (UserProfilePage) new UserProfilePage(browser).renderedPage();
    }
}
