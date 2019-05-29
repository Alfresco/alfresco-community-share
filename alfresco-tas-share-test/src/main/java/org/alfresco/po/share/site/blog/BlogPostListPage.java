package org.alfresco.po.share.site.blog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class BlogPostListPage extends SiteCommon<BlogPostListPage>
{
    @RenderWebElement
    @FindBy (className = "listTitle")
    public WebElement pageTitle;
    public By blogPageContent = By.cssSelector("tbody.yui-dt-message");
    public By simpleViewButton = By.cssSelector("button[id$='_default-simpleView-button-button']");
    @Autowired
    CreateBlogPostPage createBlogPostPage;
    @Autowired
    BlogPostViewPage blogPostViewPage;
    @Autowired
    EditBlogPostPage editBlogPostPage;
    @RenderWebElement
    @FindBy (css = "div.new-blog span[id*='_default-create-button']")
    private WebElement newPostButton;
    @RenderWebElement
    @FindBy (css = "div[id$='_default-postlist']")
    private WebElement defaultBlogPostList;
    @FindAll (@FindBy (css = "div[id*='archives'] a.filter-link"))
    private List<WebElement> archivesMonths;
    private By editButton = By.xpath(".//../div[@class = 'nodeEdit']//div[@class = 'onEditBlogPost']//a//span[text() = 'Edit']");
    private By blogLinkName = By.id("HEADER_SITE_BLOG-POSTLIST");
    private By allFilter = By.cssSelector("ul.filterLink span.all>a");
    private By latestFilter = By.cssSelector("ul.filterLink span.new>a");
    private By myDraftsFilter = By.cssSelector("ul.filterLink span.mydrafts>a");
    private By myPublished = By.cssSelector("ul.filterLink span.mypublished>a");

    public WebElement selectBlogPostWithtitle(String title)
    {
        return browser.findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']/../.."));
    }

    private WebElement selectBlogPostFooter(String title)
    {
        return browser.findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']/../../../.."));
    }

    private WebElement selectTagsByTagName(String tag)
    {
        return browser.findElement(By.xpath(
            "//div[@id = 'alf-filters']//div[contains(@id, '_blog-postlist')]//div[@class = 'filter']//span[@class = 'tag']/a[text() = '" + tag + "']"));
    }

    private WebElement selectArchiveMonth(String month)
    {
        return browser.findFirstElementWithValue(archivesMonths, month);
    }

    public WebElement blogPostTitle(String title)
    {
        return browser.findElement(By.xpath("//div[@class ='nodeContent']//span[@class = 'nodeTitle']//a[text() = '" + title + "']"));
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/blog-postlist", getCurrentSiteName());
    }

    /**
     * Method to get the Blog page content or if no content message: "No blog posts found" is returned
     *
     * @return
     */
    public String getBlogContentText()
    {
        return browser.findElement(blogPageContent).getText();
    }

    /**
     * Method to check if the New Post button is displayed on the Blog Page
     *
     * @return
     */
    public boolean isNewPostButtonDisplayed()
    {
        return browser.isElementDisplayed(newPostButton);
    }

    /**
     * Method to check if the Simple view button is displayed on the Blog Page
     *
     * @return
     */
    public boolean isSimpleViewButtonDisplayed()
    {
        return browser.isElementDisplayed(simpleViewButton);
    }

    /**
     * Method to get the text displayed on the blog link from the site dashboard page.
     *
     * @return
     */
    public String blogPageLinkName()
    {
        return browser.findElement(blogLinkName).getAttribute("aria-label").trim();
    }

    /**
     * Method to click on the blog link while on the site dashboard page.
     */
    public void clickOnBlogLink()
    {
        browser.findElement(By.id("HEADER_SITE_BLOG-POSTLIST")).click();
    }

    /**
     * Method to get the blog content for the first blog post
     */
    public BlogPostListPage clickSimpleViewButton()
    {
        WebElement viewButton = browser.findElement(simpleViewButton);

        String viewButtonText = viewButton.getText();
        viewButton.click();
        if (viewButtonText.equals("Simple View"))
        {
            browser.waitUntilElementVisible(By.cssSelector(".node.post.simple"));
        } else
        {
            browser.waitUntilElementDeletedFromDom(By.cssSelector(".node.post.simple"));
        }
        return (BlogPostListPage) this.renderedPage();
    }

    /**
     * Method to get the published on date and time for the blog post under test
     *
     * @param title
     * @return
     */
    public String getPublishedOnDateTime(String title)
    {
        return selectBlogPostWithtitle(title).findElement(By.xpath(".//div[@class = 'published']//span[@class = 'nodeAttrValue']")).getText();
    }

    /**
     * Method to check if the edit button is available for the blog post under test
     *
     * @param title
     * @return
     */
    public boolean isEditButtonPresentForBlogPost(String title)
    {
        return selectBlogPostWithtitle(title).findElement(By.xpath("//div[@class = 'onEditBlogPost']")).isDisplayed();
    }

    /**
     * Method to check if the delete button is available for the blog post under test
     *
     * @param title
     * @return
     */
    public boolean isDeleteButtonPresentForBlogPost(String title)
    {
        return selectBlogPostWithtitle(title).findElement(By.xpath("//div[@class = 'onDeleteBlogPost']")).isDisplayed();
    }

    /**
     * Method to check if the blog post published date and time is correct. The method is not exact as can not get the timestamp for when the event is created.
     * The method checks if between the reference timestamp and the published on date and time is a difference of under 60 seconds.
     *
     * @param title
     * @return
     */
    public boolean blogDateTimeComparator(String title)
    {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE d MMM yyyy HH:mm:ss");
        LocalDateTime BlogDate = LocalDateTime.parse(getPublishedOnDateTime(title), formatter);

        // Get my date
        LocalDateTime MyDate = LocalDateTime.now();

        // Compare them
        if (MyDate.isAfter(BlogDate.plusSeconds(60)))
        {
            System.out.println("Blog Date older. BlogDate: " + BlogDate + " MyDate: " + MyDate);
            return false;
        } else
        {
            System.out.println("Blog Date is ok " + BlogDate);
            return true;
        }
    }

    /**
     * Method to get the blog post author for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostAuthor(String title)
    {
        WebElement post = selectBlogPostWithtitle(title);
        List<WebElement> listLabels = post.findElements(By.xpath(".//span[@class='nodeAttrLabel']"));
        int index;
        for (index = 0; index < listLabels.size(); index++)
        {
            if (listLabels.get(index).getText().trim().equals("Author:"))
                break;
        }
        if (index == listLabels.size())
            throw new PageOperationException("Element not found");

        List<WebElement> listAttribute = post.findElements(By.xpath(".//span[@class='nodeAttrValue']"));
        return listAttribute.get(index).getText();
    }

    /**
     * Method to get the blog post title for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostTitle(String title)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(selectBlogPostWithtitle(title), 6);
        WebElement post = selectBlogPostWithtitle(title);
        return post.findElement(By.xpath(".//span[@class = 'nodeTitle']")).getText();
    }

    /**
     * Method to get the blog post status for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostStatus(String title)
    {

        return selectBlogPostWithtitle(title).findElement(By.xpath(".//div[@class = 'published']//span[text() = 'Published on: ']")).getText();
    }

    /**
     * Method to get the blog post content for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostContent(String title)
    {
        WebElement post = selectBlogPostWithtitle(title);
        return post.findElement(By.xpath(".//div[@class = 'content yuieditor']")).getText();
    }

    /**
     * Method to get the blog post number of replies for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostNumberOfReplies(String title)
    {
        return selectBlogPostFooter(title).findElement(By.xpath(".//div[@class = 'nodeFooter' ]//span[position()=2]")).getText();
    }

    /**
     * Method to get the blog post tags label text when no tags are available for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostTagsWhenNoTagsAreAvailable(String title)
    {
        return selectBlogPostFooter(title).findElement(By.xpath(".//div[@class = 'nodeFooter' ]/span[text() = '(None)']")).getText();
    }

    /**
     * Method to get the blog post tags when tags are available for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostTagsWhenTagsAreAvailable(String title)
    {
        return selectBlogPostFooter(title).findElement(By.xpath(".//div[@class = 'nodeFooter' ]//span[@class ='tag']/a")).getText();
    }

    /**
     * Method to get the page title
     */
    public String getPageTitle()
    {
        return pageTitle.getText();
    }

    /**
     * Method to check if the blog post is displayed
     *
     * @param title
     * @return
     */
    public boolean isBlogPostDisplayed(String title)
    {
        return browser.isElementDisplayed(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']"));
    }

    /**
     * Method to click on the All filter
     */
    public void clickAllFilter()
    {
        browser.findElement(allFilter).click();
        this.renderedPage();
        browser.waitUntilElementContainsText(pageTitle, "All Posts");
    }

    /**
     * Click on Latest filter
     */
    public void clickLatestFilter()
    {
        browser.findElement(latestFilter).click();
        this.renderedPage();
        browser.waitUntilElementContainsText(pageTitle, "New Posts");
    }

    /**
     * Click on My Drafts filter
     */
    public void clickMyDraftsFilter()
    {
        browser.waitUntilElementClickable(myDraftsFilter, 10L);
        browser.findElement(myDraftsFilter).click();
        this.renderedPage();
        browser.waitUntilElementContainsText(pageTitle, "My Draft Posts");
    }

    /**
     * Click on My Published filter
     */
    public void clickMyPublishedFilter()
    {
        browser.findElement(myPublished).click();
        this.renderedPage();
        browser.waitUntilElementContainsText(pageTitle, "My Published Posts");
    }

    /**
     * Click on Tag filter
     */
    public void clickTag(String tag)
    {
        selectTagsByTagName(tag).click();
        this.renderedPage();
        browser.waitUntilElementContainsText(pageTitle, "Blog Post List");
    }

    /**
     * Click on Archive Month filter
     *
     * @param month
     */
    public void clickArchiveMonth(String month)
    {
        selectArchiveMonth(month).click();
        this.renderedPage();
        browser.waitUntilElementContainsText(pageTitle, "Posts for Month " + month);
    }

    /**
     * Method to click on the Read button for the selected blog post
     */
    public BlogPostViewPage clickReadBlogPost(String title)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(selectBlogPostFooter(title));
        selectBlogPostFooter(title).findElement(By.xpath(".//div[@class = 'nodeFooter']//span[@class = 'nodeAttrValue']//a[text() ='Read']")).click();
        return (BlogPostViewPage) blogPostViewPage.renderedPage();
    }

    /**
     * Method to check if the blog post content is displayed while on the Blog Post List page
     *
     * @param title
     * @return
     */
    public boolean isBlogPostContentDisplayed(String title)
    {
        return browser.isElementDisplayed(By.xpath(".//div[@class = 'content yuieditor']"));
    }

    /**
     * Method to get the button name displayed for Simple View/ Detailed View button
     *
     * @return
     */
    public String getButtonName()
    {
        return browser.findElement(simpleViewButton).getText();
    }

    /**
     * Method to click on the blog post title
     *
     * @param title
     */
    public BlogPostViewPage clickOnThePostTitle(String title)
    {
        browser.findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']")).click();
        return (BlogPostViewPage) blogPostViewPage.renderedPage();
    }

    /**
     * Method to click the Edit button for the selected blog post that needs to be edited. The blog post is selected by title
     *
     * @param title
     */
    public EditBlogPostPage clickEditButton(String title)
    {
        selectBlogPostWithtitle(title).findElement(editButton).click();
        return (EditBlogPostPage) editBlogPostPage.renderedPage();
    }

    /**
     * Method to click the New Post button
     */
    public CreateBlogPostPage clickNewPostButton()
    {
        newPostButton.click();
        return (CreateBlogPostPage) createBlogPostPage.renderedPage();
    }

    /**
     * Method to click on the Delete button for the selected blog post
     *
     * @param title
     */
    public void clickDeleteButton(String title)
    {
        selectBlogPostWithtitle(title).findElement(By.xpath("//div[@class = 'onDeleteBlogPost']//span[text()='Delete']")).click();
    }
}