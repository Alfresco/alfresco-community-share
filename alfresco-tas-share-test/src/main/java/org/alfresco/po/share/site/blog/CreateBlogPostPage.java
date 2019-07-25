package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

@Primary
@PageObject
public class CreateBlogPostPage extends SiteCommon<CreateBlogPostPage>
{
    @RenderWebElement
    @FindBy (css = "input[id*='_default-title']")
    protected WebElement titleField;
    @FindBy (css = "button[id$='_default-publish-button-button']")
    protected WebElement publishInternallyButton;
    @FindBy (css = "button[id$='_default-save-button-button']")
    protected WebElement saveAsDraftButton;
    @FindBy (css = "button[id$='_default-cancel-button-button']")
    protected WebElement cancelButton;
    @Autowired
    BlogPostViewPage blogPostViewPage;
    private By pageTitle = By.xpath("//div[@id ='bd']//div[@class = 'page-form-header']//h1");
    @RenderWebElement
    @FindBy (xpath = "//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']")
    private WebElement frame;
    @FindBy (xpath = "//div[@class = 'taglibrary']//input")
    private WebElement tagsField;
    @FindBy (xpath = "//div[@class = 'taglibrary']//span[@class = 'yui-button yui-push-button']//button[text()='Add']")
    private WebElement addTagButton;
    private By deleteTagButton = By.xpath("//div[@class = 'taglibrary']//a[@class = 'taglibrary-action']//span[@class = 'remove']");

    public WebElement findTag(String Tag)
    {
        return browser.findElement(By.xpath("//div[@class = 'taglibrary']//span[text() = '" + Tag + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postedit", getCurrentSiteName());
    }

    /**
     * Method to ge the Page title
     */
    public String getPageTitle()
    {
        return browser.findElement(pageTitle).getText();
    }

    /**
     * Method to send input to the Title field
     *
     * @param newBlogPostTitle
     */
    public void sendTitleInput(String newBlogPostTitle)
    {
        titleField.sendKeys(newBlogPostTitle);
    }

    /**
     * Method to send input to the Content field
     *
     * @param blogPostContentText
     */
    public void sendBlogPostTextInput(String blogPostContentText)
    {
        browser.switchTo().frame(browser.findElement(By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe")));
        WebElement element = browser.findElement(By.id("tinymce"));
        element.sendKeys(blogPostContentText);
        browser.switchTo().defaultContent();
    }

    /**
     * Method to send input to the Tag field
     *
     * @param Tag
     */
    public void sendTagsInput(String Tag)
    {
        tagsField.sendKeys(Tag);
    }

    /**
     * Method to click the Add tag button
     */
    public void clickAddTagButton()
    {
        addTagButton.click();
    }

    /**
     * Method to click the delete tag button for the selected tag
     *
     * @param Tag
     */
    public void clickDeleteTag(String Tag)
    {
        browser.mouseOver(findTag(Tag));
        browser.findElement(By.xpath("//div[@class = 'taglibrary']//a[@class = 'taglibrary-action']")).click();
    }

    /**
     * Method to get the Tag text
     *
     * @param Tag
     * @return
     */
    public String getTagText(String Tag)
    {
        return findTag(Tag).getText();
    }

    /**
     * Method to check if Tag is present
     *
     * @param Tag
     * @return
     */
    public boolean isTagPresent(String Tag)
    {
        return browser.isElementDisplayed(By.xpath("//div[@class = 'taglibrary']//span[text() = '" + Tag + "']"));
    }

    /**
     * Method to check if the Delete button is available for the selected tag
     *
     * @param Tag
     * @return
     */
    public boolean isDeleteButtonAvailable(String Tag)
    {
        browser.mouseOver(findTag(Tag));
        return browser.isElementDisplayed(By.xpath("//div[@class = 'taglibrary']//a[@class = 'taglibrary-action']//span[@class = 'remove']"));
    }

    /**
     * Method to click Publish Internally button
     */
    public BlogPostViewPage clickPublishInternally()
    {
        publishInternallyButton.click();
        return (BlogPostViewPage) blogPostViewPage.renderedPage();
    }

    /**
     * Method to click the Cancel button
     */
    public void clickCancelButton()
    {
        getBrowser().waitUntilElementClickable(cancelButton).click();
    }

    /**
     * Method to click Save As Draft button
     */
    public BlogPostViewPage clickSaveAsDraftButton()
    {
        saveAsDraftButton.click();
        return (BlogPostViewPage) blogPostViewPage.renderedPage();
    }
}
