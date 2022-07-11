package org.alfresco.po.share.site.blog;

import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class CreateBlogPostPage extends SiteCommon<CreateBlogPostPage>
{
    protected final By titleField = By.cssSelector("input[id*='_default-title']");
    protected final By publishInternallyButton = By.cssSelector("button[id$='_default-publish-button-button']");
    protected final By saveAsDraftButton = By.cssSelector("button[id$='_default-save-button-button']");
    protected final By cancelButton = By.cssSelector("button[id$='_default-cancel-button-button']");
    private final By pageTitle = By.xpath("//div[@id ='bd']//div[@class = 'page-form-header']//h1");
    private final By tagsField = By.xpath("//div[@class = 'taglibrary']//input");
    private final By addTagButton = By.xpath("//div[@class = 'taglibrary']//span[@class = 'yui-button yui-push-button']//button");

    private final String tagPath = "//div[@class = 'taglibrary']//span[text() = '%s']";

    public CreateBlogPostPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postedit", getCurrentSiteName());
    }

    private WebElement findTag(String tag)
    {
        return findElement(By.xpath(String.format(tagPath, tag)));
    }

    public String getPageTitle()
    {
        return getElementText(pageTitle);
    }

    public CreateBlogPostPage assertPageFormHeaderEqualsTo(String expectedHeader)
    {
        log.info("Assert page form header equals to {}", expectedHeader);
        String actualPageTitle = getElementText(pageTitle);
        assertEquals(actualPageTitle, expectedHeader,
            String.format("Page form header not equals %s", expectedHeader));
        return this;
    }

    public CreateBlogPostPage setTitle(String newBlogPostTitle)
    {
        clearAndType(titleField, newBlogPostTitle);
        return this;
    }

    public CreateBlogPostPage setContent(String blogPostContentText)
    {
        switchTo().frame(findElement(By.xpath("//div[@class = 'mce-edit-area mce-container mce-panel mce-stack-layout-item']//iframe")));
        WebElement element = findElement(By.id("tinymce"));
        element.sendKeys(blogPostContentText);
        switchTo().defaultContent();
        return this;
    }

    public CreateBlogPostPage setTag(String tag)
    {
        clearAndType(tagsField, tag);
        return this;
    }

    public CreateBlogPostPage addTag()
    {
        clickElement(addTagButton);
        return this;
    }

    public BlogPostViewPage publishPostInternally()
    {
        clickElement(publishInternallyButton);
        waitUntilNotificationMessageDisappears();
        return new BlogPostViewPage(webDriver);
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
    }

    public BlogPostViewPage savePostAsDraft()
    {
        clickElement(saveAsDraftButton);
        waitUntilNotificationMessageDisappears();
        return new BlogPostViewPage(webDriver);
    }
}
