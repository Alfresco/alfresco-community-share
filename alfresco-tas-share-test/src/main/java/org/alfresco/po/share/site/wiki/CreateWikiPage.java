package org.alfresco.po.share.site.wiki;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreateWikiPage extends SiteCommon<CreateWikiPage>
{
    private final By wikiPageTagsList = By.cssSelector("[id$='default-current-tags'] li a span");
    private final By addTagButton = By.cssSelector("[id$=default-add-tag-button-button]");
    private final By tagInputField = By.cssSelector("[id$=default-tag-input-field]");
    private final By cancelButton = By.cssSelector("span.first-child a");
    private final By saveButton = By.cssSelector("button[id$=default-save-button-button]");
    private final By wikiPageTitle = By.cssSelector("[id$='default-title']");
    private final By pageHeader = By.cssSelector("div.page-form-header h1");
    private final By wikiPageContent = By.xpath("//iframe[contains(@title,'Rich Text Area')]");

    public CreateWikiPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    /**
     * Method used to get wiki page header
     *
     * @return wiki page header
     */
    public String getWikiPageTitle()
    {
        return findElement(pageHeader).getText();
    }

    /**
     * Method used to type wiki page title
     */
    public void typeWikiPageTitle(String title)
    {
        findElement(wikiPageTitle).sendKeys(title);
    }

    /**
     * Method used to type wiki page content
     */

    public void typeWikiPageContent(String content)
    {
        switchTo().frame(findElement(wikiPageContent));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(content);
        switchTo().defaultContent();
    }

    /**
     * Click on save button
     *
     * @return wiki page
     */

    public WikiPage saveWikiPage()
    {
        findElement(saveButton).click();
        return new WikiPage(webDriver);
    }

    /**
     * Click on cancel button
     *
     * @return wiki pages list
     */

    public WikiListPage cancelWikiPageAndLeavePage()
    {
        findElement(cancelButton).click();
        Alert confirmationBox = switchTo().alert();
        confirmationBox.accept();
        return new WikiListPage(webDriver);
    }


    public void cancelWikiPage()
    {
        findElement(cancelButton).click();
    }

    /**
     * Method used to add tag
     */
    public void addTag(String tagName)
    {
        findElement(tagInputField).sendKeys(tagName);
        findElement(addTagButton).click();
    }

    /**
     * This method returns the list of wiki page tags
     *
     * @return list of wiki page titles
     */

    public List<String> getWikiPageTagsList()
    {
        List<String> wikiPageTags = new ArrayList<>();
        for (WebElement wikiPageTag : findElements(wikiPageTagsList))
        {
            wikiPageTags.add(wikiPageTag.getText());
        }
        return wikiPageTags;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-create", getCurrentSiteName());
    }
}
