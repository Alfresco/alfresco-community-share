package org.alfresco.po.share.site.wiki;

import java.util.List;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SiteCommon;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class EditWikiPage extends SiteCommon<EditWikiPage>
{
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

    private final By wikiPageContent = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private final By removeTag = By.cssSelector("span.remove");
    private final String imageLink = "//img[contains(@title,'";

    public EditWikiPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

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
        webElementInteraction.switchTo().frame(webElementInteraction.waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        editable.sendKeys(content);
        webElementInteraction.switchTo().defaultContent();
        saveButton.click();
        return new WikiMainPage(webDriver);
    }

    /**
     * Method used to enter and cancel wiki page content
     */
    public WikiMainPage cancelWikiContent(String content)
    {
        webElementInteraction.switchTo().frame(webElementInteraction.waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        editable.sendKeys(content);
        webElementInteraction.switchTo().defaultContent();
        cancelButton.click();
        return new WikiMainPage(webDriver);
    }

    /**
     * Method used to clear wiki page content
     */

    public void clearWikiPageContent()
    {
        webElementInteraction.switchTo().frame(webElementInteraction.waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        editable.clear();
        webElementInteraction.switchTo().defaultContent();
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
        webElementInteraction.switchTo().frame(webElementInteraction.waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = webElementInteraction.switchTo().activeElement();
        return editable.getText();
    }

    public WebElement selectTagDetailsRow(String tagName)
    {
        webElementInteraction.switchTo().defaultContent();
        return webElementInteraction.findFirstElementWithValue(tagsList, tagName);
    }

    /**
     * Method used to remove tag
     */
    public void removeTag(String tagName)
    {
        Actions actions = new Actions(webDriver.get());
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
        webElementInteraction.switchTo().defaultContent();
        saveButton.click();
        return new WikiMainPage(webDriver);
    }

    public void clickInsertLibraryImage()
    {
        insertLibraryImage.click();
    }

    public SelectDocumentPopupPage clickInsertDocumentLink()
    {
        insertDocumentImage.click();
        return new SelectDocumentPopupPage(webDriver);
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
        return webElementInteraction.isElementDisplayed(webElementInteraction.waitUntilElementIsVisible(By.xpath(image)));
    }

    /**
     * This method is used to click on image
     */

    public void clickOnImage(String imageName)
    {
        String image = StringUtils.deleteWhitespace(imageLink + imageName + "')]");
        webElementInteraction.waitUntilElementIsVisible(By.xpath(image));
        webElementInteraction.clickElement(By.xpath(image));
    }

    public String getIframeSourceCode()
    {
        webElementInteraction.switchTo().defaultContent();
        webElementInteraction.switchTo().frame(webElementInteraction.findElement(wikiPageContent));
        webElementInteraction.switchTo().activeElement();
        return webElementInteraction.getPageSource();
    }
}
