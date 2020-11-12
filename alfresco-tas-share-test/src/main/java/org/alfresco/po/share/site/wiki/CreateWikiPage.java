package org.alfresco.po.share.site.wiki;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateWikiPage extends SiteCommon<CreateWikiPage>
{
    //@Autowired
    WikiPage wikiPage;

   // @Autowired
    WikiListPage wikiListPage;

    @RenderWebElement
    @FindBy (css = "div.page-form-header h1")
    private WebElement pageHeader;

    @RenderWebElement
    @FindBy (css = "[id$='default-title']")
    private WebElement wikiPageTitle;

    @FindBy (css = "button[id$=default-save-button-button]")
    private WebElement saveButton;

    @FindBy (css = "span.first-child a")
    private WebElement cancelButton;

    @FindBy (css = "[id$=default-tag-input-field]")
    private WebElement tagInputField;

    @FindBy (css = "[id$=default-add-tag-button-button]")
    private WebElement addTagButton;

    @FindAll (@FindBy (css = "[id$='default-current-tags'] li a span"))
    private List<WebElement> wikiPageTagsList;

    private By wikiPageContent = By.xpath("//iframe[contains(@title,'Rich Text Area')]");

    public CreateWikiPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    /**
     * Method used to get wiki page header
     *
     * @return wiki page header
     */
    public String getWikiPageTitle()
    {
        return pageHeader.getText();
    }

    /**
     * Method used to type wiki page title
     */
    public void typeWikiPageTitle(String title)
    {
        wikiPageTitle.sendKeys(title);
    }

    /**
     * Method used to type wiki page content
     */

    public void typeWikiPageContent(String content)
    {
        getBrowser().switchTo().frame((WebElement) getBrowser().findElement(wikiPageContent));
        WebElement editable = getBrowser().switchTo().activeElement();
        editable.sendKeys(content);
        getBrowser().switchTo().defaultContent();
    }

    /**
     * Click on save button
     *
     * @return wiki page
     */

    public WikiPage saveWikiPage()
    {
        saveButton.click();
        return (WikiPage) wikiPage.renderedPage();
    }

    /**
     * Click on cancel button
     *
     * @return wiki pages list
     */

    public WikiListPage cancelWikiPageAndLeavePage()
    {
        cancelButton.click();
        Alert confirmationBox = getBrowser().switchTo().alert();
        confirmationBox.accept();
        return (WikiListPage) wikiListPage.renderedPage();
    }


    public void cancelWikiPage()
    {
        cancelButton.click();
    }

    /**
     * Method used to add tag
     */
    public void addTag(String tagName)
    {
        tagInputField.sendKeys(tagName);
        addTagButton.click();
    }

    /**
     * This method returns the list of wiki page tags
     *
     * @return list of wiki page titles
     */

    public List<String> getWikiPageTagsList()
    {
        List<String> wikiPageTags = new ArrayList<>();
        for (WebElement wikiPageTag : wikiPageTagsList)
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
