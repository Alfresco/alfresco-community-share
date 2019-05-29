package org.alfresco.po.share.site;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

public abstract class SiteCommon<T> extends SharePage<SiteCommon<T>>
{
    @FindBy (css = "img.alf-user-icon")
    protected Button addUser;
    @FindBy (css = "#HEADER_SITE_DASHBOARD a")
    protected Link dashboard;
    @FindBy (css = "#HEADER_SITE_DOCUMENTLIBRARY a")
    protected Link documentLibrary;
    @FindBy (css = "#HEADER_SITE_MEMBERS a")
    protected Link members;
    @FindBy (id = "HEADER_SITE_CONFIGURATION_DROPDOWN")
    protected WebElement siteConfiguration;
    @FindBy (css = "span[id='HEADER_MY_FILES_text'] a")
    protected WebElement myFilesButton;
    @FindBy (css = "span[id='HEADER_REPOSITORY_text'] a")
    protected WebElement repositoryButton;
    @FindBy (css = "#yui-gen48")
    protected WebElement errorButton;
    private String currentSiteName;

    public String getCurrentSiteName()
    {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName)
    {
        this.currentSiteName = currentSiteName;
    }

    public void clickSiteConfiguration()
    {
        siteConfiguration.click();
    }

    public SiteMembersPage clickSiteMembers()
    {
        members.click();
        return new SiteMembersPage();
    }

    /**
     * Check if Site Members link is displayed
     *
     * @return true if it is displayed
     */
    public boolean isSiteMembersLinkDisplayed()
    {
        return members.isDisplayed();
    }

    /**
     * Click on the Site Dashboard Link
     *
     * @return SiteDashboardPage
     */
    public SiteDashboardPage clickSiteDashboard()
    {
        dashboard.click();
        return new SiteDashboardPage();
    }

    /**
     * Check if Site Dashboard link is displayed
     *
     * @return true if it is displayed
     */
    public boolean isSiteDashboardLinkDisplayed()
    {
        return dashboard.isDisplayed();
    }

    /**
     * Click on the Document Library Link
     *
     * @return DocumentLibraryPage
     */
    public DocumentLibraryPage clickDocumentLibrary()
    {
        documentLibrary.click();
        return new DocumentLibraryPage();
    }

    /**
     * Check if Document Library link is displayed
     *
     * @return true if it is displayed
     */
    public boolean isDocumentLibraryLinkDisplayed()
    {
        return documentLibrary.isDisplayed();
    }

    public AddSiteUsersPage clickAddUsersIcon()
    {
        addUser.click();
        return new AddSiteUsersPage();
    }

    @SuppressWarnings ("unchecked")
    public T navigate(String siteId)
    {
        setCurrentSiteName(siteId);
        return (T) navigate().renderedPage();
    }

    /**
     * Click on the My Files Link
     *
     * @return MyFilesPage
     */
    public MyFilesPage clickMyFilesLink()
    {
        myFilesButton.click();
        return new MyFilesPage();
    }

    /**
     * Click on the Repository Link
     *
     * @return Repository
     */
    public RepositoryPage clickRepositoryLink()
    {
        repositoryButton.click();
        return new RepositoryPage();
    }

    public void navigateErrorClick()
    {
        if (getBrowser().isElementDisplayed(errorButton))
            errorButton.click();
    }


}