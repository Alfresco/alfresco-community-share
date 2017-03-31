package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;

@PageObject
public class WikiMainPage extends SiteCommon<WikiMainPage> {
    @Autowired
    EditWikiPage editWikiPage;

    @Autowired
    CreateWikiPage createWikiPage;

    @Autowired
    WikiListPage wikiListPage;

    @Autowired
    WikiDetailsPage wikiDetailsPage;

    @RenderWebElement
    @FindBy(css = "div.new-page button[id$='default-create-button-button']")
    private Button newPageButton;

    @RenderWebElement
    @FindBy(css = "button[id*='delete-button-button']")
    private WebElement deletePageButton;

    @RenderWebElement
    @FindBy(xpath = "//a[text()='Wiki Page List']")
    private WebElement wikiPageListLink;

    @FindBy(css = "a[href*='view']")
    private WebElement wikiViewPageLink;

    @FindBy(xpath = "//span[contains(text(),'View Page')]")
    private WebElement disabledWikiViewPageLink;

    @FindBy(css = "a[href*='details']")
    private WebElement wikiDetailsLink;

    @FindBy(css = "a[href*='edit']")
    private WebElement wikiEditPageLink;

    @FindBy(css = "[id$=default-wikipage]")
    private WebElement wikiPageContent;

    @FindBy(css = "[id$=default-rename-button-button]")
    private WebElement renameWikiMainPageButton;

    @FindBy(css = "[class*=rename-panel]")
    private WebElement renameWikiMainPagePanel;

    @FindBy(css = "[id$=default-viewButtons]")
    private WebElement wikiMainPageTtitle;

    @FindBy(xpath = "//a[text()='Main Page']")
    private WebElement mainPageLink;

    @FindBy(xpath = "//a[text()='here']")
    private WebElement hereLink;

    @FindBy(css = "[id$=default-delete-button-button]")
    private WebElement deleteWikiMainPage;

    @FindBy(css = "[id=prompt]")
    private WebElement deletePopUp;

    private String imageLink = "//img[contains(@src,'";

    /**
     * Click on Edit Page link from Wiki main page
     *
     * @return editWikiPage
     */
    public EditWikiPage clickEditPageLink() {
        wikiEditPageLink.click();
        browser.waitInSeconds(2);
        return (EditWikiPage) editWikiPage.renderedPage();
    }

    /**
     * This method is used to get wiki page content
     *
     * @return wiki page content
     */

    public String getWikiPageContent() {

        browser.waitInSeconds(2);
        return wikiPageContent.getText();
    }

    /**
     * Click on new page link
     *
     * @return create wiki page
     */

    public CreateWikiPage clickWikiNewPage() {
        newPageButton.click();
        return (CreateWikiPage) createWikiPage.renderedPage();
    }

    /**
     * Click on wiki page list link
     *
     * @return wiki page list page
     */

    public WikiListPage clickOnWikiListLink() {
        wikiPageListLink.click();
        return (WikiListPage) wikiListPage.renderedPage();
    }

    public boolean isImageDisplayed(String imageName)

    {
        String image1 = imageLink + imageName + "')]";
        String image = StringUtils.deleteWhitespace(image1);
        return browser.isElementDisplayed(By.xpath(image));
    }

    public boolean clickOnRenameWikiMainPageButton() {
        renameWikiMainPageButton.click();
        return renameWikiMainPagePanel.isDisplayed();
    }

    public String getWikiMainPageTitle()
    {
        browser.waitUntilElementVisible(wikiMainPageTtitle);
        return wikiMainPageTtitle.getText();
    }

    public void clickOnMainPageLink() {
        mainPageLink.click();
    }

    public void clickOnHereLink() {
        hereLink.click();
    }

    public boolean isRenameWikiMainPagePanelDisplayed() {
        return renameWikiMainPagePanel.isDisplayed();
    }

    public boolean isNewPageButtonDisplayed() {
        return newPageButton.isDisplayed();
    }

    public boolean isDeleteButtonDisplayed() {
        return deletePageButton.isDisplayed();
    }

    public boolean isDeleteButtonEnabled() {
        return deletePageButton.isEnabled();
    }

    public boolean isRenameButtonDisplayed() {
        return renameWikiMainPageButton.isDisplayed();
    }

    public boolean isViewLinkDisplayed() {
        return wikiViewPageLink.isDisplayed();
    }

    public boolean isDisabledViewLinkDisplayed() {
        return disabledWikiViewPageLink.isDisplayed();
    }

    public boolean isEditPagelinkDisplayed() {
        return wikiEditPageLink.isDisplayed();
    }

    public boolean isDetailsLinkDisplayed() {
        return wikiDetailsLink.isDisplayed();
    }


    public boolean deleteWikiMainPage() {
        deleteWikiMainPage.click();
        return deletePopUp.isDisplayed();
    }

    public WikiDetailsPage clickOnDetailsPageLink() {
        wikiDetailsLink.click();
        return (WikiDetailsPage) wikiDetailsPage.renderedPage();
    }

    @Override
    public String getRelativePath() {
        return String.format("share/page/site/%s/wiki-page?title=Main_Page", getCurrentSiteName());
    }

}
