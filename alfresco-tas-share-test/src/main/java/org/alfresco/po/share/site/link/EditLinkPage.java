package org.alfresco.po.share.site.link;

import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.TextInput;

public class EditLinkPage extends SiteCommon<EditLinkPage>
{
    private By updateButton = By.cssSelector("[id*=default-ok-button]");
    private By linkTitle = By.cssSelector("[id*=default-title]");
    private By linkURL = By.cssSelector("[id*=default-url]");
    private By linkDescription = By.cssSelector("[id*=default-description]");
    private By linkInternal = By.cssSelector("input[id$='default-internal']");
    private By cancelButton = By.cssSelector("[id*=default-cancel-button]");

    @FindBy (css = "[id*=default-tag-input-field]")
    private TextInput linkTag;

    @FindBy (css = "[id*=default-add-tag-button-button]")
    private Button addTagButton;

    @FindAll (@FindBy (css = "ul[id$='links-linkedit_x0023_default-current-tags'] .taglibrary-action>span"))
    private List<WebElement> currentTagList;

    @FindBy (css = "[id*=default-load-popular-tags-link]")
    private Link popularTagsLink;

    private final By removeTag = By.cssSelector("span.remove");
    private final By tagsList = By.cssSelector("li.onRemoveTag a");

    public EditLinkPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-linkedit?linkId", getCurrentSiteName());
    }

    public void updateLinkTitle(String newLinkTitle)
    {
        findElement(linkTitle).clear();
        clearAndType(linkTitle, newLinkTitle);
    }

    public void updateLinkURL(String newLinkURL)
    {
        clearAndType(linkURL, newLinkURL);
    }

    public void updateLinkDescription(String newLinkDescription)
    {
        clearAndType(linkDescription, newLinkDescription);
    }

    public void checkLinkInternal()
    {
        clickElement(linkInternal);
    }

    public void clickOnUpdateButton()
    {
        clickElement(updateButton);
    }

    public WebElement selectTagDetailsRow(String tagName)
    {
        switchTo().defaultContent();
        return findFirstElementWithValue(tagsList, tagName);
    }

    /**
     * Method used to remove tag
     */
    public void removeTag(String tagName)
    {
        waitInSeconds(3);
        Actions actions = new Actions(webDriver.get());
        actions.moveToElement(selectTagDetailsRow(tagName));
        actions.moveToElement(selectTagDetailsRow(tagName).findElement(removeTag));
        actions.click();
        actions.perform();
    }

    public void uncheckLinkInternal()
    {
        clickElement(linkInternal);
    }

    public void clickOnCancelButton()
    {
        clickElement(cancelButton);
    }
}
