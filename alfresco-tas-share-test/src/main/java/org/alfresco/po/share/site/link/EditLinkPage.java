package org.alfresco.po.share.site.link;

import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.CheckBox;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.TextInput;

public class EditLinkPage extends SiteCommon<EditLinkPage>
{
    //@Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    //@Autowired
    LinkPage linkPage;

    @RenderWebElement
    @FindBy (css = "[id*=default-ok-button]")
    private Button updateButton;

    @RenderWebElement
    @FindBy (css = "[id*=default-title]")
    private TextInput linkTitle;

    @RenderWebElement
    @FindBy (css = "[id*=default-url]")
    private TextInput linkURL;

    @FindBy (css = "[id*=default-description]")
    private WebElement linkDescription;

    @FindBy (css = "input[id$='default-internal']")
    private CheckBox linkInternal;

    @FindBy (css = "[id*=default-tag-input-field]")
    private TextInput linkTag;

    @FindBy (css = "[id*=default-add-tag-button-button]")
    private Button addTagButton;

    @FindAll (@FindBy (css = "ul[id$='links-linkedit_x0023_default-current-tags'] .taglibrary-action>span"))
    private List<WebElement> currentTagList;

    @FindBy (css = "[id*=default-load-popular-tags-link]")
    private Link popularTagsLink;

    @FindBy (css = "[id*=default-cancel-button]")
    private Button cancelButton;
    @FindAll (@FindBy (css = "li.onRemoveTag a"))
    private List<WebElement> tagsList;

    private final By removeTag = By.cssSelector("span.remove");

    public EditLinkPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-linkedit?linkId", getCurrentSiteName());
    }

    public void updateLinkTitle(String newLinkTitle)
    {
        linkTitle.clear();
        linkTitle.sendKeys(newLinkTitle);
    }

    public void updateLinkURL(String newLinkURL)
    {
        linkURL.clear();
        linkURL.sendKeys(newLinkURL);
    }

    public void updateLinkDescription(String newLinkDescription)
    {
        linkDescription.clear();
        linkDescription.sendKeys(newLinkDescription);
    }

    public void checkLinkInternal()
    {
        linkInternal.select();
    }

    public LinkDetailsViewPage clickOnUpdateButton()
    {
        updateButton.click();
        return (LinkDetailsViewPage) linkDetailsViewPage.renderedPage();
    }

    public WebElement selectTagDetailsRow(String tagName)
    {
        getBrowser().switchTo().defaultContent();
        return getBrowser().findFirstElementWithValue(tagsList, tagName);
    }

    /**
     * Method used to remove tag
     */
    public void removeTag(String tagName)
    {
        Actions actions = new Actions(getBrowser());
        actions.moveToElement(selectTagDetailsRow(tagName));
        actions.moveToElement(selectTagDetailsRow(tagName).findElement(removeTag));
        actions.click();
        actions.perform();
    }

    public void uncheckLinkInternal()
    {
        linkInternal.deselect();
    }

    public LinkPage clickOnCancelButton()
    {
        cancelButton.click();
        return (LinkPage) linkPage.renderedPage();
    }
}
