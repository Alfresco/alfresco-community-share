package org.alfresco.po.share.site.link;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

public class LinkPage extends SiteCommon<LinkPage>
{
    private LinkDetailsViewPage linkDetailsViewPage;
    private CreateLinkPage createLinkPage;
    private EditLinkPage editLinkPage;

    @FindBy (css = "button[id*='default-create-link']")
    private Button newLinkButton;

    @FindBy (css = ".filter.links-filter")
    private WebElement linksFilter;

    @FindBy (css = "[class=list-title]")
    private WebElement linksListTitle;

    @FindAll (@FindBy (css = "[class=link-title]"))
    private List<WebElement> linksTitleList;

    @FindAll (@FindBy (css = "[id*=default-links] tr"))
    private List<WebElement> linksList;

    @FindAll (@FindBy (css = "[class=item] a"))
    private List<WebElement> listOfLinksURL;

    @FindBy (css = "[id*=viewMode-button-button]")
    private WebElement viewModeButton;

    @FindBy (css = "[class=all] a")
    private WebElement allLinksFilter;

    @FindBy (css = "[class=user] a")
    private WebElement myLinksFilter;

    @FindBy (css = "[class=recent] a")
    private WebElement recentLinksFilter;

    @FindBy (css = ".datatable-msg-empty")
    private WebElement dataTableMsgEmpty;

    @FindBy (css = "[id=prompt]")
    private WebElement deleteLinkPrompt;

    @FindBy (css = "[id*=default-selected-i-dd-button]")
    private Button selectedItemsButton;

    @FindBy (css = "[id*=default-select-button-button]")
    private Button selectButton;

    @FindAll (@FindBy (css = "[id*=default-selecItems-menu] li span"))
    private List<WebElement> selectItems;

    @FindBy (css = "[class=links-action-select-all]")
    private WebElement selectAllOption;

    @FindBy (css = "[class=links-action-invert-selection]")
    private WebElement selectInvertSelectionOption;

    @FindBy (css = "[id*=default-selecItems-menu] li:nth-last-child(1)")
    private WebElement selectNoneOption;

    @FindBy (css = "[class=links-action-delete]")
    private WebElement selectDeleteOption;

    @FindBy (css = "[class*=deselect-item] span")
    private WebElement selectDeselectAllDeleteOption;

    @FindBy (css = "td[class*='yui-dt-col-title'] h3[class ='link-title']")
    private WebElement linkTitle;

    private By linkDetails = By.cssSelector("span[class=item]");
    private By linkTags = By.cssSelector(".detail [class=tag] a");
    private By linksList_Title = By.cssSelector("[class=list-title]");
    private By links_TitleList = By.cssSelector("[class=link-title]");
    private By allLinks_Filter = By.cssSelector("[class=all] a");
    private By myLinks_Filter = By.cssSelector("[class=user] a");
    private By recentLinks_Filter = By.cssSelector("[class=recent] a");
    private By links_List = By.cssSelector("[id*=default-links] tr");

    public LinkPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links", getCurrentSiteName());
    }

    public String getLinksListTitle()
    {
        return linksListTitle.getText();
    }

    public String get_LinksListTitle()
    {
        return findElement(linksList_Title).getText();
    }

    /**
     * This method returns the list of links titles
     *
     * @return
     */

    public List<String> getLinksTitlesList()
    {
        waitInSeconds(2);
        List<String> linksTitles = new ArrayList<>();
        for (WebElement linkTitle : findElements(links_TitleList))
        {
            linksTitles.add(linkTitle.getText());
        }
        return linksTitles;
    }

    /**
     * This method returns the list of links URLs
     *
     * @return
     */

    public List<String> getLinksURL()
    {
        List<String> linksURLs = new ArrayList<>();
        for (WebElement linkURL : listOfLinksURL)
        {
            linksURLs.add(linkURL.getText());
        }
        return linksURLs;
    }

    public WebElement selectLinkDetailsRow(String linkTitle)
    {
        return findFirstElementWithValue(linksList, linkTitle);
    }

    public List<String> getLinkTags(String linkTitle)
    {
        List<String> tags = new ArrayList<>();
        List<WebElement> webTags = selectLinkDetailsRow(linkTitle).findElements(linkTags);
        for (WebElement webTag : webTags)
        {
            tags.add(webTag.getText());
        }

        return tags;
    }

    /**
     * This method returns the details about a specific link in a list of strings. On each position there is
     * 0 - URL
     * 1 - date of creation
     * 2 - creator of the link
     * 3 - link description
     *
     * @param linkTitle
     * @return
     */
    private List<String> getDetailsOfLink(String linkTitle)
    {
        List<String> stringDetails = new ArrayList<>();
        List<WebElement> webDetails = selectLinkDetailsRow(linkTitle).findElements(linkDetails);
        if (webDetails.get(0).getText().contains("URL:"))
            stringDetails.add(webDetails.get(0).findElement(By.cssSelector("a")).getText());
        if (webDetails.get(1).getText().contains("Created On:"))
            stringDetails.add(webDetails.get(1).getText().replace("Created On:", "").trim());
        if (webDetails.get(2).getText().contains("Created By:"))
            stringDetails.add(webDetails.get(2).findElement(By.cssSelector("a")).getText());
        if (webDetails.get(3).getText().contains("Description:"))
            stringDetails.add(webDetails.get(3).getText().replace("Description:", "").trim());
        return stringDetails;
    }

    public String getLinkURL(String linkTitle)
    {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(0);
    }

    public String getLinkCreationDate(String linkTitle)
    {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(1);
    }

    public String getLinkAuthor(String linkTitle)
    {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(2);
    }

    public String getLinkDescription(String linkTitle)
    {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(3);
    }

    public void changeViewMode()
    {
        viewModeButton.click();
    }

    /**
     * This method clicks on a specific tag filter
     *
     * @param tagName
     */
    public void clickSpecificTag(String tagName)
    {
        waitInSeconds(3);
        findElement(By.cssSelector("li a[rel='" + tagName + "']")).click();
    }

    /**
     * Click on the specified option from Links filter
     *
     * @param option
     * @return
     */
    public LinkPage filterLinksBy(String option)
    {
        switch (option)
        {
            case "All Links":
                clickElement(allLinks_Filter);
                waitUntilElementContainsText(linksList_Title, "All Links");
                break;
            case "My Links":
                clickElement(myLinks_Filter);
                waitUntilElementContainsText(linksList_Title, "My Links");
                break;
            case "Recently Added":
                clickElement(recentLinks_Filter);
                waitUntilElementContainsText(linksList_Title, "Recently Added Links");
                break;

            default:break;
        }
        return this;
    }

    public boolean isLinkDisplayed(String linkTitle)
    {
        return isElementDisplayed(selectLinkDetailsRow(linkTitle));
    }

    public boolean is_LinkDisplayed(String linkTitle)
    {
        waitInSeconds(2);
        for (WebElement listItems : findElements(links_List))
        {
            if (listItems.getText().contains(linkTitle))
            {
                return true;
            }
        }
        return false;
    }

    public LinkDetailsViewPage clickOnLinkName(String linkTitle)
    {
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=link-title] a")).click();
        return new LinkDetailsViewPage(webDriver);
    }

    public String getNoLinksFoundMsg()
    {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector(".datatable-msg-empty"));
        return dataTableMsgEmpty.getText();
    }

    public CreateLinkPage createLink()
    {
        newLinkButton.click();
        return new CreateLinkPage(webDriver);
    }

    public void clickOnLinkURL(String linkURL)
    {
        findElement(By.xpath("//a[@href ='" + linkURL + "']")).click();
    }

    public EditLinkPage clickEditLink(String linkTitle)
    {
        mouseOver(findFirstElementWithValue(linksList, linkTitle));
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector(".edit-link span")).click();
        return new EditLinkPage(webDriver);
    }

    public boolean clickDeleteLink(String linkTitle)
    {
        mouseOver(findFirstElementWithValue(linksList, linkTitle));
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector(".delete-link span")).click();
        return deleteLinkPrompt.isDisplayed();
    }

    public List<String> getTagsFromTagsSection()
    {
        List<String> tags = new ArrayList<>();
        List<WebElement> tagsList = findElements(By.cssSelector("li [class=tag] a"));
        for (WebElement tag : tagsList)
        {
            tags.add(tag.getText());
        }
        return tags;
    }

    public boolean isSelectedItemsButtonEnabled()
    {
        return selectedItemsButton.isEnabled();
    }

    public boolean selectLinkCheckBox(String linkTitle)
    {
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=checkbox-column]")).click();
        return selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=checkbox-column]")).isSelected();
    }

    public boolean isSelectLinkCheckBoxChecked(String linkTitle)
    {
        return selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=checkbox-column]")).isSelected();
    }

    public void clickSelectButton()
    {
        selectButton.click();
    }

    public void clickInvertSelectionOption()
    {
        selectInvertSelectionOption.click();
    }

    public void clickNoneOption()
    {
        selectNoneOption.click();
    }

    public void clickAllOption()
    {
        selectAllOption.click();
    }

    public void clickOnSelectedItemsButton()
    {
        selectedItemsButton.click();
    }

    public void clickOnDeselectAllOption()
    {
        selectDeselectAllDeleteOption.click();
    }

    public boolean clickOnSelectDeleteOption()
    {
        selectDeleteOption.click();
        return deleteLinkPrompt.isDisplayed();
    }

    public String getLinkTitle()
    {
        return linkTitle.getText();
    }

    public void browserRefresh()
    {
        waitInSeconds(7);
        refresh();
        waitInSeconds(5);
        refresh();
    }
}
