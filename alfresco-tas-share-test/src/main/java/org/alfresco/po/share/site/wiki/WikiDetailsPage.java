package org.alfresco.po.share.site.wiki;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@Slf4j
public class WikiDetailsPage extends SiteCommon<WikiDetailsPage>
{
    @FindBy (css = "a[href*='edit']")
    private WebElement wikiEditPageLink;

    @FindBy (css = "a[href*='view']")
    private WebElement wikiViewPageLink;

    @FindAll (@FindBy (css = "[class=tag]"))
    private List<WebElement> tagsList;

    @FindAll (@FindBy (css = "[class=links] a"))
    private List<WebElement> linkedPagesList;

    @FindBy (css = "[class*=meta-section-label]")
    private WebElement version;

    @FindBy (css = "[class=tags]")
    private WebElement tagsSection;

    @FindAll (@FindBy (css = "div.bd ul.first-of-type li a"))
    private List<WebElement> dropDownVersionsList;

    @FindBy (css = "[id$=default-selectVersion-button-button]")
    private WebElement selectVersionButton;

    @FindBy (css = "[class=revert] a")
    private WebElement revertVersionButton;

    @FindBy (css = "[id$=revertWikiVersion-instance-dialog_c]")
    private WebElement revertPopUp;

    @FindBy (css = "[class=details-page-content]")
    private WebElement pageContentDetails;

    @FindAll (@FindBy (css = "[id*=default-expand-div] [class*=meta-section-label]"))
    private List<WebElement> versionsList;

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

        List<String> wikiPageTags = new ArrayList<>();
        for (WebElement wikiPageTag : tagsList)
        {
            wikiPageTags.add(wikiPageTag.getText());
        }
        return wikiPageTags;
    }

    public List<String> getLinkedPagesList()
    {

        List<String> linkedPages = new ArrayList<>();
        for (WebElement linkedPage : linkedPagesList)
        {
            linkedPages.add(linkedPage.getText());
        }
        return linkedPages;
    }

    public String getVersion()
    {
        return version.getText();
    }

    public String getTag()
    {
        return tagsSection.getText();
    }

    public WikiDetailsPage selectVersion(String version)
    {
        try
        {
            selectVersionButton.click();
            selectOptionFromFilterOptionsList(version, dropDownVersionsList);
            Assert.assertTrue(selectVersionButton.getText().contains(version), "Incorrect filter selected");

            return new WikiDetailsPage(webDriver);
        } catch (NoSuchElementException nse)
        {
            log.info("Option not present {}", nse.getMessage());
            throw new PageOperationException(version + " option not present.");
        }
    }

    public boolean clickOnRevert()
    {
        revertVersionButton.click();
        return revertPopUp.isDisplayed();
    }

    public String getPageContentDetails()
    {
        return pageContentDetails.getText();
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
        for (WebElement version : versionsList)
        {

            list.add(version.getText());
        }
        return list;
    }

    public ViewWikiPage clickOnViewPageLink()
    {
        wikiViewPageLink.click();
        return new ViewWikiPage(webDriver);
    }
}