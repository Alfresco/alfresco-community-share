package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class EditWikiPage extends SiteCommon<EditWikiPage>
{
    @Autowired
    WikiMainPage wikiPage;

    @Autowired
    SelectDocumentPopupPage selectDocPopUpPage;

    @RenderWebElement
    @FindBy (css = "[id$=tag-input-field]")
    private WebElement tagInputField;

    @FindBy (css = "[id$=default-add-tag-button-button]")
    private WebElement addTagButton;

    @FindBy (css = "[class*=mce-tinymce]")
    private WebElement editWkiContainer;

    @FindBy (css = "[class*=mce-edit-area]")
    private WebElement editWikiArea;

    @FindBy (css = "#tinymce")
    private WebElement editWikiLine;

    @RenderWebElement
    @FindBy (css = "button[id$='default-save-button-button']")
    private WebElement saveButton;

    @FindBy (css = "button[id$='cancel-button-button']")
    private WebElement cancelButton;

    @FindAll (@FindBy (css = "li.onRemoveTag a"))
    private List<WebElement> tagsList;

    @FindBy (css = "[aria-label='Insert Library Image']")
    private WebElement insertLibraryImage;

    @FindBy (css = "[aria-label='Insert Document Link'] button i")
    private WebElement insertDocumentImage;

    @FindAll (@FindBy (css = "[id$=image_results] img"))
    private List<WebElement> imagesList;

    @FindBy (css = "[class*=toolbar-titlebar] h2")
    private WebElement libraryImagesTitlebar;

    // @FindBy(css = "[id$=content-docPicker-cntrl-picker]")
    // private WebElement selectDocPopUp;

    private By wikiPageContent = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private By removeTag = By.cssSelector("span.remove");
    private String imageLink = "//img[contains(@title,'";

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=Main_Page&action=edit", getCurrentSiteName());
    }

    /**
     * Method used to enter and save wiki page content
     */
    public WikiMainPage saveWikiContent(String content)
    {
        browser.switchTo().frame(browser.waitUntilElementVisible(wikiPageContent));
        WebElement editable = browser.switchTo().activeElement();
        editable.sendKeys(content);
        browser.switchTo().defaultContent();
        saveButton.click();
        browser.waitInSeconds(2);
        return (WikiMainPage) wikiPage.renderedPage();
    }

    /**
     * Method used to enter and cancel wiki page content
     */
    public WikiMainPage cancelWikiContent(String content)
    {
        browser.switchTo().frame(browser.waitUntilElementVisible(wikiPageContent));
        WebElement editable = browser.switchTo().activeElement();
        editable.sendKeys(content);
        browser.switchTo().defaultContent();
        cancelButton.click();
        return (WikiMainPage) wikiPage.renderedPage();
    }

    /**
     * Method used to clear wiki page content
     */

    public void clearWikiPageContent()
    {
        browser.refresh();
        browser.switchTo().frame(browser.waitUntilElementVisible(wikiPageContent));
        WebElement editable = browser.switchTo().activeElement();
        editable.clear();
        browser.switchTo().defaultContent();
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
     * Method used to get page content
     *
     * @return
     */

    public String getWikiPageContent()
    {
        browser.switchTo().frame(browser.waitUntilElementVisible(wikiPageContent));
        WebElement editable = browser.switchTo().activeElement();
        return editable.getText();
    }

    public WebElement selectTagDetailsRow(String tagName)
    {
        browser.switchTo().defaultContent();
        return browser.findFirstElementWithValue(tagsList, tagName);
    }

    /**
     * Method used to remove tag
     */
    public void removeTag(String tagName)
    {
        Actions actions = new Actions(browser);
        actions.moveToElement(selectTagDetailsRow(tagName));
        actions.moveToElement(selectTagDetailsRow(tagName).findElement(removeTag));
        actions.click();
        actions.perform();
    }

    /**
     * Method used to click on save button
     *
     * @return
     */
    public WikiMainPage clickOnSaveButton()
    {
        browser.switchTo().defaultContent();
        saveButton.click();
        browser.waitInSeconds(2);
        return (WikiMainPage) wikiPage.renderedPage();
    }

    public void clickInsertLibraryImage()
    {
        browser.refresh();
        this.renderedPage();
        insertLibraryImage.click();
        browser.waitInSeconds(2);
    }

    public SelectDocumentPopupPage clickInsertDocumentLink()
    {
        insertDocumentImage.click();
        return (SelectDocumentPopupPage) selectDocPopUpPage.renderedPage();
    }

    /**
     * Verify if Library Images titlebar is displayed
     *
     * @return true if displayed
     */
    public boolean islibraryImagesTitlebarDisplayed()
    {
        return libraryImagesTitlebar.isDisplayed();
    }

    /**
     * This method is used to verify if an image is displayed on Library Images section
     */

    public boolean isImageDisplayed(String imageName)
    {
        String image = StringUtils.deleteWhitespace(imageLink + imageName + "')]");
        return browser.isElementDisplayed(browser.waitUntilElementVisible(By.xpath(image)));
    }

    /**
     * This method is used to click on image
     */

    public void clickOnImage(String imageName)
    {
        String image = StringUtils.deleteWhitespace(imageLink + imageName + "')]");
        browser.waitUntilElementVisible(By.xpath(image)).click();
    }

    public String getIframeSourceCode()
    {
        browser.switchTo().defaultContent();
        browser.switchTo().frame(browser.findElement(wikiPageContent));
        browser.switchTo().activeElement();
        return browser.getPageSource();
    }
}
