package org.alfresco.po.share.site.link;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.CheckBox;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
@PageObject
public class CreateLinkPage extends SiteCommon<CreateLinkPage>
{
    @Autowired
    LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    LinkPage linkPage;

    @RenderWebElement
    @FindBy (css = "button[id$='links-linkedit_x0023_default-ok-button']")
    private Button saveButton;

    @RenderWebElement
    @FindBy (css = "input[id$='links-linkedit_x0023_default-title']")
    private TextInput linkTitle;

    @RenderWebElement
    @FindBy (css = "input[id$='links-linkedit_x0023_default-url']")
    private TextInput linkURL;

    @FindBy (css = "textarea[id$='links-linkedit_x0023_default-description']")
    private WebElement linkDescription;

    @FindBy (css = "input[id$='default-internal']")
    private CheckBox linkInternal;

    @FindBy (css = "input[id$='links-linkedit_x0023_default-tag-input-field']")
    private TextInput linkTag;

    @FindBy (css = "button[id*='links-linkedit_x0023_default-add-tag-button']")
    private Button addTagButton;

    @FindAll (@FindBy (css = "ul[id$='links-linkedit_x0023_default-current-tags'] .taglibrary-action>span"))
    private List<WebElement> currentTagList;

    @FindBy (css = "a[id$='links-linkedit_x0023_default-load-popular-tags-link']")
    private Link popularTagsLink;

    @FindBy (css = "button[id$='links-linkedit_x0023_default-cancel-button']")
    private Button cancelButton;

    @FindBy (css = "[id*=default-load-popular-tags-link]")
    private Button choosePopularTags;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-linkedit", getCurrentSiteName());
    }

    public boolean isSaveButtonDisplayed()
    {
        return saveButton.isDisplayed();
    }

    public LinkDetailsViewPage clickSaveButton()
    {
        saveButton.click();
        return (LinkDetailsViewPage) linkDetailsViewPage.renderedPage();
    }

    public boolean isLinkTitleDisplayed()
    {
        return linkTitle.isDisplayed();
    }

    public boolean isLinkTitleMandatory()
    {
        return linkTitle.getWrappedElement().findElement(By.xpath("../*[text()='*']")).isDisplayed();
    }

    public String getLinkTitle()
    {
        return linkTitle.getText();
    }

    public void typeLinkTitle(String title)
    {
        Utils.clearAndType(linkTitle, title);
    }

    public boolean isLinkURLDisplayed()
    {
        return linkURL.isDisplayed();
    }

    public boolean isLinkURLMandatory()
    {
        return linkURL.getWrappedElement().findElement(By.xpath("../*[text()='*']")).isDisplayed();
    }

    public String getLinkURL()
    {
        return linkURL.getText();
    }

    public void typeLinkURL(String url)
    {
        Utils.clearAndType(linkURL, url);
    }

    public boolean isLinkDescriptionDisplayed()
    {
        return browser.isElementDisplayed(linkDescription);
    }

    public String getLinkDescription()
    {
        return linkDescription.getText();
    }

    public void typeLinkDescription(String description)
    {
        linkDescription.clear();
        linkDescription.sendKeys(description);
    }

    public boolean isLinkInternalDisplayed()
    {
        return linkInternal.isDisplayed();
    }

    public boolean isLinkInternalChecked()
    {
        return linkInternal.isSelected();
    }

    public void checkLinkInternal()
    {
        linkInternal.select();
    }

    public boolean isLinkTagDisplayed()
    {
        return linkTag.isDisplayed();
    }

    public boolean isAddTagButtonDisplayed()
    {
        return addTagButton.isDisplayed();
    }

    public void addTag(String tag)
    {
        Utils.clearAndType(linkTag, tag);
        addTagButton.click();
    }

    public boolean isPopularTagsLinkDisplayed()
    {
        return popularTagsLink.isDisplayed();
    }

    public void clickPopularTagsLink()
    {
        popularTagsLink.click();
    }

    public boolean isCancelButtonDisplayed()
    {
        return cancelButton.isDisplayed();
    }

    public LinkPage clickCancelButton()
    {
        cancelButton.click();
        return (LinkPage) linkPage.renderedPage();
    }

    public boolean isChoosePopularTagsLinkDisplayed()
    {
        return choosePopularTags.isDisplayed();
    }
}
