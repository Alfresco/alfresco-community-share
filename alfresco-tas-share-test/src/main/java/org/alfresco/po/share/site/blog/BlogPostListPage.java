package org.alfresco.po.share.site.blog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class BlogPostListPage extends SiteCommon<BlogPostListPage>
{
    @FindBy (css = "[class='listTitle']")
    public WebElement pageTitle;

    private final By blogPageContent = By.cssSelector("tbody.yui-dt-message");
    private final By simpleViewButton = By.cssSelector("button[id$='_default-simpleView-button-button']");

    @FindBy (css = "div.new-blog span[id*='_default-create-button']")
    private WebElement newPostButton;
    @FindBy (css = "div[id$='_default-postlist']")
    private WebElement defaultBlogPostList;
    @FindAll (@FindBy (css = "div[id*='archives'] a.filter-link"))
    private List<WebElement> archivesMonths;

    private final By editButton = By.xpath(".//../div[@class = 'nodeEdit']//div[@class = 'onEditBlogPost']//a//span[text() = 'Edit']");
    private final By blogLinkName = By.id("HEADER_SITE_BLOG-POSTLIST");
    private final By allFilter = By.cssSelector("ul.filterLink span.all>a");
    private final By latestFilter = By.cssSelector("ul.filterLink span.new>a");
    private final By myDraftsFilter = By.cssSelector("ul.filterLink span.mydrafts>a");
    private final By myPublished = By.cssSelector("ul.filterLink span.mypublished>a");

    public BlogPostListPage(ThreadLocal<WebDriver> webDriver)
    {
      super(webDriver);
    }

    public WebElement selectBlogPostWithTitle(String title)
    {
        return findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']/../.."));
    }

    private WebElement selectBlogPostFooter(String title)
    {
        return findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']/../../../.."));
    }

    private WebElement selectTagsByTagName(String tag)
    {
        return findElement(By.xpath(
            "//div[@id = 'alf-filters']//div[contains(@id, '_blog-postlist')]//div[@class = 'filter']//span[@class = 'tag']/a[text() = '" + tag + "']"));
    }

    private WebElement selectArchiveMonth(String month)
    {
        return findFirstElementWithValue(archivesMonths, month);
    }

    public WebElement blogPostTitle(String title)
    {
        return findElement(By.xpath("//div[@class ='nodeContent']//span[@class = 'nodeTitle']//a[text() = '" + title + "']"));
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
        return findElement(blogPageContent).getText();
    }

    /**
     * Method to check if the New Post button is displayed on the Blog Page
     *
     * @return
     */
    public boolean isNewPostButtonDisplayed()
    {
        return isElementDisplayed(newPostButton);
    }

    /**
     * Method to check if the Simple view button is displayed on the Blog Page
     *
     * @return
     */
    public boolean isSimpleViewButtonDisplayed()
    {
        return isElementDisplayed(simpleViewButton);
    }

    /**
     * Method to get the text displayed on the blog link from the site dashboard page.
     *
     * @return
     */
    public String blogPageLinkName()
    {
        return findElement(blogLinkName).getAttribute("aria-label").trim();
    }

    /**
     * Method to click on the blog link while on the site dashboard page.
     */
    public void clickOnBlogLink()
    {
        findElement(By.id("HEADER_SITE_BLOG-POSTLIST")).click();
    }

    /**
     * Method to get the blog content for the first blog post
     */
    public BlogPostListPage clickSimpleViewButton()
    {
        WebElement viewButton = findElement(simpleViewButton);

        String viewButtonText = viewButton.getText();
        viewButton.click();
        if (viewButtonText.equals("Simple View"))
        {
            waitUntilElementIsVisible(By.cssSelector(".node.post.simple"));
        } else
        {
            waitUntilElementDeletedFromDom(By.cssSelector(".node.post.simple"));
        }
        return this;
    }

    /**
     * Method to get the published on date and time for the blog post under test
     *
     * @param title
     * @return
     */
    public String getPublishedOnDateTime(String title)
    {
        return selectBlogPostWithTitle(title).findElement(By.xpath(".//div[@class = 'published']//span[@class = 'nodeAttrValue']")).getText();
    }

    /**
     * Method to check if the edit button is available for the blog post under test
     *
     * @param title
     * @return
     */
    public boolean isEditButtonPresentForBlogPost(String title)
    {
        return selectBlogPostWithTitle(title).findElement(By.xpath("//div[@class = 'onEditBlogPost']")).isDisplayed();
    }

    /**
     * Method to check if the delete button is available for the blog post under test
     *
     * @param title
     * @return
     */
    public boolean isDeleteButtonPresentForBlogPost(String title)
    {
        return selectBlogPostWithTitle(title).findElement(By.xpath("//div[@class = 'onDeleteBlogPost']")).isDisplayed();
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
        WebElement post = selectBlogPostWithTitle(title);
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
        waitUntilWebElementIsDisplayedWithRetry(selectBlogPostWithTitle(title), 6);
        WebElement post = selectBlogPostWithTitle(title);
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

        return selectBlogPostWithTitle(title).findElement(By.xpath(".//div[@class = 'published']//span[text() = 'Published on: ']")).getText();
    }

    /**
     * Method to get the blog post content for the blog post under test
     *
     * @param title
     * @return
     */
    public String getBlogPostContent(String title)
    {
        WebElement post = selectBlogPostWithTitle(title);
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
        return isElementDisplayed(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']"));
    }

    /**
     * Method to click on the All filter
     */
    public void clickAllFilter()
    {
        findElement(allFilter).click();
        waitUntilElementContainsText(pageTitle, "All Posts");
    }

    /**
     * Click on Latest filter
     */
    public void clickLatestFilter()
    {
        findElement(latestFilter).click();
        waitUntilElementContainsText(pageTitle, "New Posts");
    }

    /**
     * Click on My Drafts filter
     */
    public void clickMyDraftsFilter()
    {
        clickElement(myDraftsFilter);
        findElement(myDraftsFilter).click();
        waitUntilElementContainsText(pageTitle, "My Draft Posts");
    }

    /**
     * Click on My Published filter
     */
    public void clickMyPublishedFilter()
    {
        findElement(myPublished).click();
        waitUntilElementContainsText(pageTitle, "My Published Posts");
    }

    /**
     * Click on Tag filter
     */
    public void clickTag(String tag)
    {
        mouseOver(selectTagsByTagName(tag));
        selectTagsByTagName(tag).click();
        waitUntilElementContainsText(pageTitle, "Blog Post List");
    }

    /**
     * Click on Archive Month filter
     *
     * @param month
     */
    public void clickArchiveMonth(String month)
    {
        selectArchiveMonth(month).click();
        waitUntilElementContainsText(pageTitle, "Posts for Month " + month);
    }

    /**
     * Method to click on the Read button for the selected blog post
     */
    public BlogPostViewPage clickReadBlogPost(String title)
    {
        waitUntilWebElementIsDisplayedWithRetry(selectBlogPostFooter(title));
        selectBlogPostFooter(title).findElement(By.xpath(".//div[@class = 'nodeFooter']//span[@class = 'nodeAttrValue']//a[text() ='Read']")).click();
        return new BlogPostViewPage(webDriver);
    }

    /**
     * Method to check if the blog post content is displayed while on the Blog Post List page
     *
     * @param title
     * @return
     */
    public boolean isBlogPostContentDisplayed(String title)
    {
        return isElementDisplayed(By.xpath(".//div[@class = 'content yuieditor']"));
    }

    /**
     * Method to get the button name displayed for Simple View/ Detailed View button
     *
     * @return
     */
    public String getButtonName()
    {
        return findElement(simpleViewButton).getText();
    }

    /**
     * Method to click on the blog post title
     *
     * @param title
     */
    public BlogPostViewPage clickOnThePostTitle(String title)
    {
        findElement(By.xpath("//tr[contains(@class, 'yui-dt-rec')]//div[@class = 'nodeContent']//span/a[text() = '" + title + "']")).click();
        return new BlogPostViewPage(webDriver);
    }

    /**
     * Method to click the Edit button for the selected blog post that needs to be edited. The blog post is selected by title
     *
     * @param title
     */
    public EditBlogPostPage clickEditButton(String title)
    {
        selectBlogPostWithTitle(title).findElement(editButton).click();
        return new EditBlogPostPage(webDriver);
    }

    /**
     * Method to click the New Post button
     */
    public CreateBlogPostPage clickNewPostButton()
    {
        newPostButton.click();
        return new CreateBlogPostPage(webDriver);
    }

    /**
     * Method to click on the Delete button for the selected blog post
     *
     * @param title
     */
    public void clickDeleteButton(String title)
    {
        selectBlogPostWithTitle(title).findElement(By.xpath("//div[@class = 'onDeleteBlogPost']//span[text()='Delete']")).click();
    }
}