package org.alfresco.po.share.site.wiki;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class WikiMainPage extends SiteCommon<WikiMainPage>
{
    private final By newPageButton = By.cssSelector("div.new-page button[id$='default-create-button-button']");
    private final By deletePageButton = By.cssSelector("button[id*='delete-button-button']");
    private final By wikiPageListLink = By.xpath("//a[text()='Wiki Page List']");
    private final By wikiViewPageLink = By.cssSelector("a[href*='view']");
    private final By disabledWikiViewPageLink = By.xpath("//span[contains(text(),'View Page')]");
    private final By wikiDetailsLink = By.cssSelector("a[href*='details']");
    private final By wikiEditPageLink = By.cssSelector("a[href*='edit']");
    private final By wikiPageContent = By.cssSelector("[id$=default-wikipage]");
    private final By renameWikiMainPageButton = By.cssSelector("[id$=default-rename-button-button]");
    private final By renameWikiMainPagePanel = By.cssSelector("[class*=rename-panel]");
    private final By wikiMainPageTtitle = By.cssSelector("[id$=default-viewButtons]");
    private final By mainPageLink = By.xpath("//a[text()='Main Page']");
    private final By hereLink = By.xpath("//a[text()='here']");
    private final By deleteWikiMainPage = By.cssSelector("[id$=default-delete-button-button]");

    private final String imageLink = "//img[contains(@src,'";

    public WikiMainPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=Main_Page", getCurrentSiteName());
    }

    public EditWikiPage clickEditPageLink()
    {
        clickElement(wikiEditPageLink);
        return new EditWikiPage(webDriver);
    }

    public WikiMainPage assertWikiPageContentEquals(String expectedWikiContent)
    {
        log.info("Assert wiki page content equals: {}", expectedWikiContent);
        assertEquals(getElementText(wikiPageContent), expectedWikiContent,
            String.format("Wiki content not equals %s ", expectedWikiContent));

        return this;
    }

    public CreateWikiPage clickWikiNewPage()
    {
        clickElement(newPageButton);
        return new CreateWikiPage(webDriver);
    }

    public WikiListPage clickOnWikiListLink()
    {
        clickElement(wikiPageListLink);
        return new WikiListPage(webDriver);
    }

    public boolean isImageDisplayed(String imageName)

    {
        String image1 = imageLink + imageName + "')]";
        String image = StringUtils.deleteWhitespace(image1);
        return isElementDisplayed(By.xpath(image));
    }

    public boolean clickOnRenameWikiMainPageButton()
    {
        clickElement(renameWikiMainPageButton);
        return isElementDisplayed(renameWikiMainPagePanel);
    }

    public WikiMainPage assertWikiMainPageTitleEquals(String expectedWikiTitle)
    {
        log.info("Assert wiki main page title equals: {}", expectedWikiTitle);
        assertEquals(getElementText(wikiMainPageTtitle), expectedWikiTitle,
            String.format("Wiki main page title not equals %s ", expectedWikiTitle));
        return this;

    }

    public void clickOnMainPageLink()
    {
        clickElement(mainPageLink);
    }

    public void clickOnHereLink()
    {
        clickElement(hereLink);
    }

    public boolean isRenameWikiMainPagePanelDisplayed()
    {
        return isElementDisplayed(renameWikiMainPagePanel);
    }

    public boolean isNewPageButtonDisplayed()
    {
        return isElementDisplayed(newPageButton);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return isElementDisplayed(deletePageButton);
    }

    public boolean isDeleteButtonEnabled()
    {
        return waitUntilElementIsVisible(deletePageButton).isEnabled();
    }

    public boolean isRenameButtonDisplayed()
    {
        return isElementDisplayed(renameWikiMainPageButton);
    }

    public boolean isViewLinkDisplayed()
    {
        return isElementDisplayed(wikiViewPageLink);
    }

    public boolean isDisabledViewLinkDisplayed()
    {
        return isElementDisplayed(disabledWikiViewPageLink);
    }

    public boolean isEditPagelinkDisplayed()
    {
        return isElementDisplayed(wikiEditPageLink);
    }

    public boolean isDetailsLinkDisplayed()
    {
        return isElementDisplayed(wikiDetailsLink);
    }

    public void deleteWikiMainPage()
    {
        clickElement(deleteWikiMainPage);
    }

    public WikiDetailsPage clickOnDetailsPageLink()
    {
        clickElement(wikiDetailsLink);
        return new WikiDetailsPage(webDriver);
    }
}
