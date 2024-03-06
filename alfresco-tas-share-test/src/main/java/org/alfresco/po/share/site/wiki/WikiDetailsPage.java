package org.alfresco.po.share.site.wiki;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@Slf4j
public class WikiDetailsPage extends SiteCommon<WikiDetailsPage>
{
    @FindBy (css = "a[href*='edit']")
    private WebElement wikiEditPageLink;
    private final By wikiViewPageLink = By.cssSelector("a[href*='view']");
    private final By tagsList = By.cssSelector("[class=tag]");
    private final By linkedPagesList = By.cssSelector("[class=links] a");
    private final By version = By.cssSelector("[class*=meta-section-label]");
    private final By tagsSection = By.cssSelector("[class=tags]");
    private final By dropDownVersionsList = By.cssSelector("div.bd ul.first-of-type li a");
    private final By selectVersionButton = By.cssSelector("[id$=default-selectVersion-button-button]");
    private final By revertVersionButton = By.cssSelector("[class=revert] a");
    private final By revertPopUp = By.cssSelector("[id$=revertWikiVersion-instance-dialog_c]");
    private final By pageContentDetails = By.cssSelector("[class=details-page-content]");
    private final By versionsList = By.cssSelector("[id*=default-expand-div] [class*=meta-section-label]");
    private final By revertOkButton = By.cssSelector("[id*=revertWikiVersion-instance-ok]");
    private final By revertMessage = By.cssSelector("[id*=revertWikiVersion-instance-prompt]");

    @FindBy (css = "[class=bd] span")
    private WebElement revertNotification;
    public WikiDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=Main_Page&action=details", getCurrentSiteName());
    }

    public List<String> getTagsList()
    {

        List<WebElement> listOfTags = findElements(tagsList);
        List<String> wikiPageTags = new ArrayList<>();
        for (WebElement wikiPageTag : listOfTags)
        {
            wikiPageTags.add(wikiPageTag.getText());
        }
        return wikiPageTags;
    }

    public List<String> getLinkedPagesList()
    {

        List<WebElement> listOfLinkedPages = findElements(linkedPagesList);
        List<String> linkedPages = new ArrayList<>();
        for (WebElement linkedPage : listOfLinkedPages)
        {
            linkedPages.add(linkedPage.getText());
        }
        return linkedPages;
    }

    public String getVersion()
    {
        return findElement(version).getText();
    }

    public String getTag()
    {
        return findElement(tagsSection).getText();
    }

    public WikiDetailsPage selectVersion(String version)
    {
        try
        {
            clickElement(selectVersionButton);
            List<WebElement> versionsListInDropDown = findElements(dropDownVersionsList);
            selectOptionFromFilterOptionsList(version, versionsListInDropDown);
            waitInSeconds(3);
            Assert.assertTrue(findElement(selectVersionButton).getText().contains(version), "Incorrect filter selected");

            return new WikiDetailsPage(webDriver);
        } catch (NoSuchElementException nse)
        {
            log.info("Option not present {}", nse.getMessage());
            throw new PageOperationException(version + " option not present.");
        }
    }

    public boolean clickOnRevert()
    {
        clickElement(revertVersionButton);
        return findElement(revertPopUp).isDisplayed();
    }

    public String getPageContentDetails()
    {
        return findElement(pageContentDetails).getText();
    }

    public WebElement selectVersionDetails(String version)
    {
        return findFirstElementWithValue(versionsList, version);
    }

    public void clickOnVersion(String version)
    {
        selectVersionDetails(version).click();
    }

    public List<String> getVersionsList()
    {
        List<String> list = new ArrayList<>();
        List<WebElement> versionListDetails = findElements(versionsList);
        for (WebElement version : versionListDetails)
        {

            list.add(version.getText());
        }
        return list;
    }

    public ViewWikiPage clickOnViewPageLink()
    {
        clickElement(wikiViewPageLink);
        return new ViewWikiPage(webDriver);
    }

    public String getRevertMessage()
    {
        return findElement(revertMessage).getText();
    }

    public void clickRevertOk()
    {
        clickElement(revertOkButton);
        waitInSeconds(2);
    }
}