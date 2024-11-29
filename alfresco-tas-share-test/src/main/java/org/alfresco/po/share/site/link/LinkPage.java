package org.alfresco.po.share.site.link;

import static org.alfresco.common.Wait.WAIT_3;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LinkPage extends SiteCommon<LinkPage> {
    private By linksTitleList = By.cssSelector("[class=link-title]");
    private By linksListTitle = By.cssSelector("[class=list-title]");
    private By newLinkButton = By.cssSelector("button[id*='default-create-link']");
    private By recentLinksFilter = By.cssSelector("[class=recent] a");
    private By myLinksFilter = By.cssSelector("[class=user] a");
    private By allLinksFilter = By.cssSelector("[class=all] a");
    private By viewModeButton = By.cssSelector("[id*=viewMode-button-button]");
    private By listOfLinksURL = By.cssSelector("[class=item] a");
    private By linksList = By.cssSelector("[id*=default-links] tr");
    private By linkDetails = By.cssSelector("span[class=item]");
    private By linkTags = By.cssSelector(".detail [class=tag] a");
    private By linkTitle = By.cssSelector("td[class*='yui-dt-col-title'] h3[class ='link-title']");
    private By selectDeselectAllDeleteOption = By.cssSelector("[class*=deselect-item] span");
    private By selectDeleteOption = By.cssSelector("[class=links-action-delete]");
    private By selectNoneOption = By.cssSelector("[id*=default-selecItems-menu] li:nth-last-child(1)");
    private By selectInvertSelectionOption = By.cssSelector("[class=links-action-invert-selection]");
    private By selectAllOption = By.cssSelector("[class=links-action-select-all]");
    private By selectButton = By.cssSelector("[id*=default-select-button-button]");
    private By selectedItemsButton = By.cssSelector("[id*=default-selected-i-dd-button]");
    private By deleteLinkPrompt = By.cssSelector("[id=prompt]");
    private By dataTableMsgEmpty = By.cssSelector(".datatable-msg-empty");
    private By linksList_Title = By.cssSelector("[class=list-title]");
    private By links_TitleList = By.cssSelector("[class=link-title]");
    private By allLinks_Filter = By.cssSelector("[class=all] a");
    private By myLinks_Filter = By.cssSelector("[class=user] a");
    private By recentLinks_Filter = By.cssSelector("[class=recent] a");
    private By links_List = By.cssSelector("[id*=default-links] tr");
    private By linksItemsList = By.cssSelector("div[id$='default-links'] tbody[class$='data'] tr");
    private By linkNameSelector = By.className("link-title");
    private By linkActionsSet = By.cssSelector(".yui-dt-liner a span");

    public LinkPage(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return String.format("share/page/site/%s/links", getCurrentSiteName());
    }

    public String getLinksListTitle() {
        return findElement(linksListTitle).getText();
    }

    public String get_LinksListTitle() {
        return findElement(linksList_Title).getText();
    }

    /**
     * This method returns the list of links titles
     *
     * @return
     */

    public List<String> getLinksTitlesList() {
        waitInSeconds(2);
        List<String> linksTitles = new ArrayList<>();
        for (WebElement linkTitle : findElements(links_TitleList)) {
            linksTitles.add(linkTitle.getText());
        }
        return linksTitles;
    }

    /**
     * This method returns the list of links URLs
     *
     * @return
     */

    public List<String> getLinksURL() {
        List<String> linksURLs = new ArrayList<>();
        for (WebElement linkURL : findElements(listOfLinksURL)) {
            linksURLs.add(linkURL.getText());
        }
        return linksURLs;
    }

    public WebElement selectLinkDetailsRow(String linkTitle) {
        return findFirstElementWithValue(findElements(linksList), linkTitle);
    }

    public List<String> getLinkTags(String linkTitle) {
        List<String> tags = new ArrayList<>();
        List<WebElement> webTags = selectLinkDetailsRow(linkTitle).findElements(linkTags);
        for (WebElement webTag : webTags) {
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
    private List<String> getDetailsOfLink(String linkTitle) {
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

    public String getLinkURL(String linkTitle) {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(0);
    }

    public String getLinkCreationDate(String linkTitle) {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(1);
    }

    public String getLinkAuthor(String linkTitle) {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(2);
    }

    public String getLinkDescription(String linkTitle) {
        List<String> details = getDetailsOfLink(linkTitle);
        return details.get(3);
    }

    public void changeViewMode() {
        findElement(viewModeButton).click();
    }

    /**
     * This method clicks on a specific tag filter
     *
     * @param tagName
     */
    public void clickSpecificTag(String tagName) {
        waitInSeconds(3);
        findElement(By.cssSelector("li a[rel='" + tagName + "']")).click();
    }

    /**
     * Click on the specified option from Links filter
     *
     * @param option
     * @return
     */
    public LinkPage filterLinksBy(String option) {
        switch (option) {
            case "All Links":
                findElement(allLinksFilter).click();
                waitUntilElementContainsText(linksListTitle, "All Links");
                break;
            case "My Links":
                findElement(myLinksFilter).click();
                waitUntilElementContainsText(findElement(linksListTitle), "My Links");
                break;
            case "Recently Added":
                findElement(recentLinksFilter).click();
                waitUntilElementContainsText(findElement(linksListTitle), "Recently Added Links");
                clickElement(allLinks_Filter);
                waitUntilElementContainsText(linksList_Title, "All Links");
                break;

            default:
                break;
        }
        return this;
    }

    public boolean isLinkDisplayed(String linkTitle) {
        return isElementDisplayed(selectLinkDetailsRow(linkTitle));
    }

    public boolean is_LinkDisplayed(String linkTitle) {
        waitInSeconds(2);
        for (WebElement listItems : findElements(links_List)) {
            if (listItems.getText()
                .contains(linkTitle)) {
                return true;
            }
        }
        return false;
    }

    public LinkDetailsViewPage clickOnLinkName(String linkTitle) {
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=link-title] a"))
            .click();
        return new LinkDetailsViewPage(webDriver);
    }

    public String getNoLinksFoundMsg() {
        waitUntilElementIsDisplayedWithRetry(By.cssSelector(".datatable-msg-empty"));
        return findElement(dataTableMsgEmpty).getText();
    }

    public String get_NoLinksFoundMsg() {
        waitInSeconds(2);
        return findElement(dataTableMsgEmpty).getText();
    }

    public CreateLinkPage createLink() {
        findElement(newLinkButton).click();
        return new CreateLinkPage(webDriver);
    }

    public void clickOnLinkURL(String linkURL) {
        findElement(By.xpath("//a[@href ='" + linkURL + "']")).click();
    }

    public EditLinkPage clickEditLink(String linkTitle) {
        waitInSeconds(2);
        mouseOver(findFirstElementWithValue(findElements(linksList), linkTitle));
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector(".edit-link span"))
            .click();
        return new EditLinkPage(webDriver);
    }

    public boolean clickDeleteLink(String linkTitle) {
        mouseOver(findFirstElementWithValue(findElements(linksList), linkTitle));
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector(".delete-link span"))
            .click();
        return findElement(deleteLinkPrompt).isDisplayed();
    }

    public List<String> getTagsFromTagsSection() {
        List<String> tags = new ArrayList<>();
        List<WebElement> tagsList = findElements(By.cssSelector("li [class=tag] a"));
        for (WebElement tag : tagsList) {
            tags.add(tag.getText());
        }
        return tags;
    }

    public boolean isSelectedItemsButtonEnabled() {
        return findElement(selectedItemsButton).isEnabled();
    }

    public boolean selectLinkCheckBox(String linkTitle) {
        selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=checkbox-column]"))
            .click();
        return selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=checkbox-column]"))
            .isSelected();
    }

    public boolean isSelectLinkCheckBoxChecked(String linkTitle) {
        return selectLinkDetailsRow(linkTitle).findElement(By.cssSelector("[class=checkbox-column]"))
            .isSelected();
    }

    public void clickSelectButton() {
        findElement(selectButton).click();
    }

    public void clickInvertSelectionOption() {
        findElement(selectInvertSelectionOption).click();
    }

    public void clickNoneOption() {
        findElement(selectNoneOption).click();
    }

    public void clickAllOption() {
        findElement(selectAllOption).click();
    }

    public void clickOnSelectedItemsButton() {
        findElement(selectedItemsButton).click();
    }

    public void clickOnDeselectAllOption() {
        findElement(selectDeselectAllDeleteOption).click();
    }

    public boolean clickOnSelectDeleteOption() {
        findElement(selectDeleteOption).click();
        return findElement(deleteLinkPrompt).isDisplayed();
    }

    public String getLinkTitle() {
        return findElement(linkTitle).getText();
    }

    public void browserRefresh() {
        waitInSeconds(7);
        refresh();
        waitInSeconds(5);
        refresh();
    }

    public String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    public void navigateBackBrowser() {
        getWebDriver().navigate()
            .back();
    }

    public void closeCurrentTabAndswitchToDefaultBrowserTab() {
        getWebDriver().close();
        waitInSeconds(4);
        ArrayList<String> tabs = new ArrayList(getWebDriver().getWindowHandles());
        getWebDriver().switchTo()
            .window(tabs.get(0));
    }

    public void switchWindow() {
        String currentWindow = getWindowHandles().toString();
        clickOnLinkURL("https://www.google.com");
        waitInSeconds(5);

        // Switch to new window opened
        for (String winHandle : getWindowHandles()) {
            switchTo().window(winHandle);
            if (getCurrentUrl().contains("google")) {
                break;
            }
        }
    }

    public void switchToWindow(String URL) {
        waitInSeconds(2);
        for (String winHandle : getWindowHandles()) {
            switchTo().window(winHandle);
            if (getCurrentUrl().contains(URL)) {
                break;
            }
        }
    }

    public WebElement mouseOverContentItem(String contentItem) {
        WebElement contentItemElement = selectDocumentLibraryItemRow(contentItem);
        mouseOver(contentItemElement.findElement(linkNameSelector));

        return Utils.retry(() -> {
            mouseOver(contentItemElement);
            return contentItemElement;
        }, WAIT_3.getValue());
    }

    public WebElement selectDocumentLibraryItemRow(String linkItem) {
        List<WebElement> itemsList = findElements(linksItemsList);
        return findFirstElementWithValue(itemsList, linkItem);
    }

    public boolean checkActionAvailableForLibraryItem(String libraryItem, ItemActions action) {
        waitInSeconds(3);
        return isElementDisplayed(findFirstElementWithValue(get_AvailableActions(libraryItem), action.getActionName()));
    }

    private List<WebElement> get_AvailableActions(String libraryItem) {
        WebElement itemRow = mouseOverContentItem(libraryItem);
        return itemRow.findElements(linkActionsSet);
    }
}
