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
    private final By addTagButton = By.cssSelector("[id$=default-add-tag-button-button]");
    private final By tagInputField = By.cssSelector("[id$=tag-input-field]");
    private final By saveButton = By.cssSelector("button[id$='default-save-button-button']");

    @FindBy (css = "[class*=mce-tinymce]")
    private WebElement editWkiContainer;

    @FindBy (css = "[class*=mce-edit-area]")
    private WebElement editWikiArea;

    @FindBy (css = "#tinymce")
    private WebElement editWikiLine;

    @FindAll (@FindBy (css = "li.onRemoveTag a"))
    private List<WebElement> tagsList;
    private final By tags_List = By.cssSelector("li.onRemoveTag a");
    private final By cancelButton = By.cssSelector("button[id$='cancel-button-button']");
    @FindAll (@FindBy (css = "[id$=image_results] img"))
    private List<WebElement> imagesList;
    private final By insertDocumentImage = By.cssSelector("[aria-label='Insert Document Link'] button i");
    private final By libraryImagesTitlebar = By.cssSelector("[class*=toolbar-titlebar] h2");
    private final By wikiPageContent = By.xpath("//iframe[contains(@title,'Rich Text Area')]");
    private final By removeTag = By.cssSelector("span.remove");
    private final By insertLibraryImage = By.cssSelector("[aria-label='Insert Library Image']");
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
        switchTo().frame(waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(content);
        switchTo().defaultContent();
        clickElement(saveButton);
        return new WikiMainPage(webDriver);
    }

    /**
     * Method used to enter and cancel wiki page content
     */
    public WikiMainPage cancelWikiContent(String content)
    {
        switchTo().frame(waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = switchTo().activeElement();
        editable.sendKeys(content);
        switchTo().defaultContent();
        clickElement(cancelButton);
        return new WikiMainPage(webDriver);
    }

    /**
     * Method used to clear wiki page content
     */

    public void clearWikiPageContent()
    {
        switchTo().frame(waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = switchTo().activeElement();
        editable.clear();
        switchTo().defaultContent();
    }

    /**
     * Method used to add tag
     */
    public void addTag(String tagName)
    {
        findElement(tagInputField).sendKeys(tagName);
        clickElement(addTagButton);
    }

    /**
     * Method used to get page content
     *
     * @return
     */

    public String getWikiPageContent()
    {
        switchTo().frame(waitUntilElementIsVisible(wikiPageContent));
        WebElement editable = switchTo().activeElement();
        return editable.getText();
    }

    public WebElement selectTagDetailsRow(String tagName)
    {
        switchTo().defaultContent();
        return findFirstElementWithValue(tags_List, tagName);
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
        switchTo().defaultContent();
        clickElement(saveButton);
        return new WikiMainPage(webDriver);
    }

    public void clickInsertLibraryImage()
    {
        waitInSeconds(7);
        refreshBrowser();
        clickElement(insertLibraryImage);
    }

    public void refreshBrowser(){
        waitInSeconds(2);
        getWebDriver().navigate().refresh();
    }

    public SelectDocumentPopupPage clickInsertDocumentLink()
    {
        clickElement(insertDocumentImage);
        return new SelectDocumentPopupPage(webDriver);
    }

    /**
     * Verify if Library Images titlebar is displayed
     *
     * @return true if displayed
     */
    public boolean islibraryImagesTitlebarDisplayed()
    {
        return findElement(libraryImagesTitlebar).isDisplayed();
    }

    /**
     * This method is used to verify if an image is displayed on Library Images section
     */

    public boolean isImageDisplayed(String imageName)
    {
        waitInSeconds(2);
        String image = StringUtils.deleteWhitespace(imageLink + imageName + "')]");
        return isElementDisplayed(waitUntilElementIsVisible(By.xpath(image)));
    }

    public boolean is_ImageDisplayed(String imageName) {
        for (int i = 0; i < 6; i++) {
            if (isElementDisplayed(By.xpath(StringUtils.deleteWhitespace(imageLink + imageName + "')]")))) {
                return true;
            }
            else {
                waitInSeconds(5);
                refreshBrowser();
                clickInsertLibraryImage();
            }
        }
        return false;
    }


    /**
     * This method is used to click on image
     */

    public void clickOnImage(String imageName)
    {
        String image = StringUtils.deleteWhitespace(imageLink + imageName + "')]");
        waitUntilElementIsVisible(By.xpath(image));
        clickElement(By.xpath(image));
    }

    public String getIframeSourceCode()
    {
        switchTo().defaultContent();
        switchTo().frame(findElement(wikiPageContent));
        switchTo().activeElement();
        return getPageSource();
    }
}
