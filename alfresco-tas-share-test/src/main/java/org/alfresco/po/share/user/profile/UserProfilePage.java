package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.ToolbarUserMenu;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UserProfilePage extends SharePage<UserProfilePage> implements AccessibleByMenuBar
{
    @Autowired
    ToolbarUserMenu toolbarUserMenu;

    @FindBy(css = "button[id$='button-edit-button']")
    private Button editProfile;
    
    @RenderWebElement
    @FindBy(css = "[id*=default-profile-link]")
    private WebElement infoLink;
    
    @RenderWebElement
    @FindBy(css = "[id*=default-user-sites-link]")
    private WebElement sitesLink;

    @FindBy(css = ".header-bar:nth-child(1)")
    private HtmlElement aboutHeader;

    @RenderWebElement
    @FindBy(css = ".alfresco-layout-LeftAndRight__left")
    private HtmlElement userProfilePageHeader;

    @FindBy(xpath = "//div[contains(text(), 'Contact Information')]")
    private HtmlElement contactInfoHeader;

    @FindBy(xpath = "//div[contains(text(), 'Company Details')]")
    private HtmlElement companyDetails;

    @RenderWebElement
    @FindBy(css = ".namelabel")
    private WebElement nameLabel;

    @FindBy(css = ".biorow>div")
    private WebElement summary;

    @FindAll(@FindBy(css = ".fieldlabel"))
    private List<WebElement> aboutUserDetails;

    // Contact and Company Information
    @FindAll(@FindBy(css = ".viewcolumn .row"))
    private List<WebElement> userInformations;

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/profile", getUserName());
    }

    public UserProfilePage navigate(String userName)
    {
        setUserName(userName);
        return (UserProfilePage) navigate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage navigateByMenuBar()
    {
        toolbarUserMenu.clickMyProfile();
        return (UserProfilePage) renderedPage();
    }

    /**
     * Open User Info page from the my profile navigation links
     * 
     * @param myProfileNavigation
     * @return {@link UserProfilePage}
     */
    public UserProfilePage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickInfo();
        return (UserProfilePage) this.renderedPage();
    }

    /**
     * Verify if About header is displayed
     * 
     * @return true if displayed
     */
    public boolean isAboutHeaderDisplayed()
    {
        return browser.isElementDisplayed(aboutHeader);
    }

    /**
     * Verify if Contact Information header is displayed
     * 
     * @return true if displayed
     */
    public boolean isContactInfoHeaderDisplayed()
    {
        return browser.isElementDisplayed(contactInfoHeader);
    }

    /**
     * Verify if Company Details header is displayed
     * 
     * @return true if displayed
     */
    public boolean isCompanyDetailsHeaderDisplayed()
    {
        return browser.isElementDisplayed(companyDetails);
    }

    /**
     * Verify if "User Profile Page" header is displayed
     * 
     * @return true if displayed
     */
    public boolean isUserProfilePageHeaderDisplayed()
    {
        return browser.isElementDisplayed(userProfilePageHeader);
    }

    /**
     * Click Edit Profile button
     */
    public void clickEditProfile()
    {
        editProfile.click();
    }

    public String getNameLabel()
    {
        return nameLabel.getText();
    }

    public String getUserSummary()
    {
        return summary.getText();
    }

    public List<String> getAboutUserInfo()
    {
        List<String> userInfo = new ArrayList<String>();
        for (int i = 0; i < aboutUserDetails.size(); i++)
        {
            userInfo.add(aboutUserDetails.get(i).getText());
        }
        return userInfo;
    }

    /**
     * Get the Contact Informations and Company Details
     * 
     * @return Map<String, String> field label and value
     */
    public Map<String, String> getUserInformation()
    {
        Map<String, String> userInfo = new LinkedHashMap<String, String>();
        for (WebElement info : userInformations)
        {
            String key = info.findElement(By.cssSelector(".fieldlabelright")).getText().replace(":", "");
            String value = info.findElement(By.cssSelector(".fieldvalue")).getText();
            // manage duplicate keys (for Telephone and Email)
            if (key.equals("Telephone"))
            {
                if (userInfo.get("Telephone") != null)
                {
                    userInfo.put("CompanyTelephone", value);
                }
                else
                {
                    userInfo.put(key, value);
                }
            }
            else if (key.equals("Email"))
            {
                if (userInfo.get("Email") != null)
                {
                    userInfo.put("CompanyEmail", value);
                }
                else
                {
                    userInfo.put(key, value);
                }
            }
            else
            {
                userInfo.put(key, value);
            }

        }
        return userInfo;
    }
}