package org.alfresco.po.share.site.link;

import java.util.List;
import org.alfresco.common.Utils;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
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
    private LinkDetailsViewPage linkDetailsViewPage;

    @Autowired
    private LinkPage linkPage;

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

    public LinkDetailsViewPage clickSaveButton()
    {
        saveButton.click();
        return (LinkDetailsViewPage) linkDetailsViewPage.renderedPage();
    }

    public CreateLinkPage typeLinkTitle(String title)
    {
        LOG.info("Clear and type link title: {}", title);
        Utils.clearAndType(linkTitle, title);
        return this;
    }

    public CreateLinkPage typeLinkUrl(String url)
    {
        LOG.info("Clear and type link url: {}", url);
        Utils.clearAndType(linkURL, url);
        return this;
    }

    public CreateLinkPage typeLinkDescription(String description)
    {
        LOG.info("Clear and type link description");
        clearAndType(linkDescription, description);
        return this;
    }

    public boolean isLinkInternalChecked()
    {
        return linkInternal.isSelected();
    }

    public void checkLinkInternal()
    {
        linkInternal.select();
    }

    public CreateLinkPage addTag(String tag)
    {
        LOG.info("Add tag: {}", tag);
        clearAndType(linkTag, tag);
        addTagButton.click();

        return this;
    }

    public LinkPage clickCancelButton()
    {
        cancelButton.click();
        return (LinkPage) linkPage.renderedPage();
    }
}
