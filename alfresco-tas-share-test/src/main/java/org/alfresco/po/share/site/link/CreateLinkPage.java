package org.alfresco.po.share.site.link;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateLinkPage extends SiteCommon<CreateLinkPage>
{
    private final By saveButton = By.cssSelector("button[id$='links-linkedit_x0023_default-ok-button']");
    private final By linkTitle = By.cssSelector("input[id$='links-linkedit_x0023_default-title']");
    private final By linkURL = By.cssSelector("input[id$='links-linkedit_x0023_default-url']");
    private final By linkDescription = By.cssSelector("textarea[id$='links-linkedit_x0023_default-description']");
    private final By linkInternal = By.cssSelector("input[id$='default-internal']");
    private final By linkTag = By.cssSelector("input[id$='links-linkedit_x0023_default-tag-input-field']");
    private final By addTagButton = By.cssSelector("button[id*='links-linkedit_x0023_default-add-tag-button']");
    private final By currentTagList = By.cssSelector("ul[id$='links-linkedit_x0023_default-current-tags'] .taglibrary-action>span");
    private final By popularTagsLink = By.cssSelector("a[id$='links-linkedit_x0023_default-load-popular-tags-link']");
    private final By cancelButton = By.cssSelector("button[id$='links-linkedit_x0023_default-cancel-button']");
    private final By choosePopularTags = By.cssSelector("[id*=default-load-popular-tags-link]");

    public CreateLinkPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/links-linkedit", getCurrentSiteName());
    }

    public LinkDetailsViewPage clickSaveButton()
    {
        webElementInteraction.clickElement(saveButton);
        return new LinkDetailsViewPage(webDriver);
    }

    public CreateLinkPage typeLinkTitle(String title)
    {
        LOG.info("Clear and type link title: {}", title);
        webElementInteraction.clearAndType(linkTitle, title);
        return this;
    }

    public CreateLinkPage typeLinkUrl(String url)
    {
        LOG.info("Clear and type link url: {}", url);
        webElementInteraction.clearAndType(linkURL, url);
        return this;
    }

    public boolean isLinkDescriptionDisplayed()
    {
        return webElementInteraction.isElementDisplayed(linkDescription);
    }

    public String getLinkDescription()
    {
        return webElementInteraction.getElementText(linkDescription);
    }

    public CreateLinkPage typeLinkDescription(String description)
    {
        LOG.info("Clear and type link description");
        webElementInteraction.clearAndType(linkDescription, description);
        return this;
    }

    public boolean isLinkInternalChecked()
    {
        return webElementInteraction.waitUntilElementIsVisible(linkInternal).isSelected();
    }

    public void checkLinkInternal()
    {
        webElementInteraction.clickElement(linkInternal);
    }

    public CreateLinkPage addTag(String tag)
    {
        LOG.info("Add tag: {}", tag);
        webElementInteraction.clearAndType(linkTag, tag);
        webElementInteraction.clickElement(addTagButton);
        return this;
    }

    public LinkPage clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
        return new LinkPage(webDriver);
    }
}
