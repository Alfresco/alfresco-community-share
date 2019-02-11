package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.FileInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

@PageObject
public class UsersPage extends AdminToolsPage
{
    @Autowired
    private CreateUsers createUsers;

    @Autowired
    private UserProfilePage userProfile;

    @Autowired
    private AdminToolsUserProfile adminToolsUserProfile;

    @RenderWebElement
    @FindBy(css = "button[id$='_default-newuser-button-button']")
    private WebElement newUserButton;

    @RenderWebElement
    @FindBy(css = "button[id*='uploadusers']")
    private WebElement uploadUsersButton;

    @FindBy(css = "input[id*='search-text']")
    private WebElement userSearchInputField;

    @FindBy(css = "button[id*='search']")
    private WebElement searchButton;

    @FindAll(@FindBy(xpath = "//div[contains(@class, 'yui-dt-liner')]"))
    protected List<WebElement> usersData;

    @FindAll(@FindBy(css = "img[src*='account_disabled.png']"))
    private List<WebElement> accountsDisabled;

    @FindAll(@FindBy(css = "td[class*='userName']"))
    protected List<WebElement> usersList;

    @FindAll(@FindBy(css = "td[class*='fullName']"))
    protected List<WebElement> usersNamesList;

    @FindBy(css = "input[id*='default-filedata-file']")
    private FileInput fileInput;

    @FindBy(css = "#template_x002e_html-upload_x002e_console_x0023_default-upload-button-button")
    private WebElement uploadButton;

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users";

    }

    public CreateUsers clickNewUser()
    {

        browser.waitUntilElementClickable(newUserButton, 20);
        newUserButton.click();
        browser.refresh();
        return (CreateUsers) createUsers.renderedPage();

    }

    public void searchUser(String user)
    {
        userSearchInputField.clear();
        userSearchInputField.sendKeys(user);
        int counter = 0;
        do
        {
            LOG.info("Wait for element :" + counter);
            searchButton.click();
            this.renderedPage();
            counter++;
            getBrowser().waitInSeconds(4);
        }
        while (!verifyUserIsFound(user) && counter <= 5);
    }

    /**
     * Method to check whether the user is found
     *
     * @param user String
     * @return true if the user is found, else false
     */
    public boolean verifyUserIsFound(String user)
    {
        return selectUser(user) != null;
    }

    /**
     * Retrieves the user that matches the text from the search box
     *
     * @param user String
     * @return WebElement that matches the username
     */
    public WebElement selectUser(final String username)
    {
        return browser.findFirstElementWithValue(usersList, username);
    }

    public WebElement selectName(final String fullname)
    {
        return browser.findFirstElementWithValue(usersNamesList, fullname);
    }

    /**
     * Retrieves the user's full name that matches the text from the search box
     *
     * @param fullname full name String
     * @return
     * @return WebElement that matches the user's full name
     */
    public WebElement selectUserName(final String fullname)
    {
        browser.waitUntilElementsVisible(usersNamesList);
        return browser.findFirstElementWithValue(usersNamesList, fullname);

    }

    /**
     * Method used to click on a user link
     */
    public AdminToolsUserProfile clickUserLink(String fullname)
    {
        browser.waitUntilElementClickable(selectUserName(fullname).findElement(By.cssSelector("a"))).click();
        //selectUserName(fullname).findElement(By.cssSelector("a")).click();
        return (AdminToolsUserProfile) adminToolsUserProfile.renderedPage();
    }

    public boolean isUserDisabled(int userNumber)

    {

        return accountsDisabled.get(userNumber).isDisplayed();

    }

    /**
     * Method used to verify if specific user data is displayed on the users table
     */

    public boolean isSpecificUserDataDisplayed(final String data)
    {
        return browser.findFirstElementWithValue(usersData, data) != null;
    }

    public boolean isSearchBoxDisplayed()

    {
        return userSearchInputField.isDisplayed();
    }

    public boolean isSearchButtonDisplayed()

    {
        return searchButton.isDisplayed() && searchButton.isEnabled();
    }

    public boolean isNewUserButtonDisplayed()

    {
        return newUserButton.isDisplayed() && newUserButton.isEnabled();
    }

    public boolean isImportUsersButtonDisplayed()
    {
        return uploadUsersButton.isDisplayed() && uploadUsersButton.isEnabled();
    }

    public void uploadUsers(String filePath, String contentsOfFile)
    {

        uploadUsersButton.click();

        File fileToUpload = newFile(filePath, contentsOfFile);
        try
        {
            fileInput.setFileToUpload(fileToUpload.getCanonicalPath());
            uploadButton.click();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static File newFile(String fileName, String contents)
    {
        File file = new File(fileName);
        try
        {
            if (!file.exists())
            {
                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                }
                else
                {
                    file.createNewFile();
                }
            }
            else
            {
                // to be written
            }
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }

}
