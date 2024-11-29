package org.alfresco.po.share.site.link;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
@Slf4j
public class CreateLinkPage extends SiteCommon<CreateLinkPage>
{
    private final By saveButton            = By.cssSelector("button[id$='links-linkedit_x0023_default-ok-button']");
    private final By linkTitle             = By.cssSelector("input[id$='links-linkedit_x0023_default-title']");
    private final By linkURL               = By.cssSelector("input[id$='links-linkedit_x0023_default-url']");
    private final By linkDescription       = By.cssSelector("textarea[id$='links-linkedit_x0023_default-description']");
    private final By linkInternal          = By.cssSelector("input[id$='default-internal']");
    private final By linkTag               =
        By.cssSelector("input[id$='links-linkedit_x0023_default-tag-input-field']");
    private final By addTagButton          =
        By.cssSelector("button[id*='links-linkedit_x0023_default-add-tag-button']");
    private final By cancelButton          = By.cssSelector("button[id$='links-linkedit_x0023_default-cancel-button']");
    private final By page_Header           = By.cssSelector(".page-form-header>h1");
    private final By createURL             = By.name("url");
    private final By description           = By.name("description");
    private final By internal              = By.name("internal");
    private final By tags                  = By.cssSelector(".taglibrary>input");
    private final By tagLibrary            = By.className("taglibrary-action");
    private final By removeTagIconSelector = By.className("onRemoveTag");
    private final By addButton             = By.cssSelector(".taglibrary .first-child>button");
    private final By tagList               = By.cssSelector(".bottom_taglist>a");
    private final By balloon               = By.cssSelector(".balloon>.text>div");

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
        clickElement(saveButton);
        return new LinkDetailsViewPage(webDriver);
    }

    public CreateLinkPage typeLinkTitle(String title)
    {
        log.info("Clear and type link title: {}", title);
        clearAndType(linkTitle, title);
        return this;
    }

    public CreateLinkPage typeLinkUrl(String url)
    {
        log.info("Clear and type link url: {}", url);
        clearAndType(linkURL, url);
        return this;
    }

    public CreateLinkPage typeLinkDescription(String description)
    {
        log.info("Clear and type link description");
        clearAndType(linkDescription, description);
        return this;
    }

    public void checkLinkInternal()
    {
        clickElement(linkInternal);
    }

    public CreateLinkPage addTag(String tag)
    {
        log.info("Add tag: {}", tag);
        clearAndType(linkTag, tag);
        clickElement(addTagButton);
        return this;
    }

    public LinkPage clickCancelButton()
    {
        clickElement(cancelButton);
        return new LinkPage(webDriver);
    }

    public boolean isLinkTitleDisplayed()
    {
        return findElement(page_Header).getText()
            .equals("Create Link");
    }

    public boolean isLinkURLDisplayed()
    {
        return isElementDisplayed(createURL);
    }

    public boolean isLinkDescriptionDisplayed()
    {
        return isElementDisplayed(description);
    }

    public boolean isLinkInternalChecked()
    {
        return isElementDisplayed(internal);
    }

    public boolean isLinkTagDisplayed()
    {
        return isElementDisplayed(tags);
    }

    public boolean isAddTagButtonDisplayed()
    {
        return isElementDisplayed(addButton);
    }

    public boolean isChoosePopularTagsLinkDisplayed()
    {
        return findElement(tagList).getText()
            .equals("Choose from popular tags in this site");
    }

    public boolean isSaveButtonDisplayed()
    {
        return isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(cancelButton);
    }

    public String getBalloonMessage()
    {
        return findElement(balloon).getText();
    }

    public void clickSaveAndVerifyMandatoryFieldBalloonMessage(String BallonMessage)
    {
        clickSaveButton();
        for (int i = 0; i < 3; i++)
        {
            if (!getBalloonMessage().equals(BallonMessage))
            {
                clickSaveButton();
            }
        }
    }

    public void addingTag(String tag)
    {
        log.info("Add tag: {}", tag);
        clearAndType(linkTag, tag);
    }

    public boolean VerifyAddTagButtonVisibility()
    {
        if (findElement(addTagButton).isEnabled())
        {
            return true;
        }
        return false;
    }

    public boolean isTagDisplayedInCreateLists(String tag)
    {
        waitInSeconds(2);
        for (WebElement addTag : findElements(tagLibrary))
        {
            if (addTag.getText()
                .contains(tag))
            {
                return true;
            }
        }
        return false;
    }

    public void assertTagCount(int NumberOfTagsAdded)
    {
        List<WebElement> tagCount = findElements(tagLibrary);
        Assert.assertEquals(tagCount.size(), NumberOfTagsAdded, "Counting no. of tags added");
    }

    public void removeTag(String tagName)
    {
        List<WebElement> tagsList = waitUntilElementsAreVisible(tagLibrary);
        List<WebElement> removeIconList = waitUntilElementsAreVisible(removeTagIconSelector);

        for (int i = 0; i < tagsList.size(); i++)
        {
            if (tagsList.get(i)
                .getText()
                .equals(tagName))
            {
                removeIconList.get(i)
                    .click();
            }
        }
    }
}
