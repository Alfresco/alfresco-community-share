package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class MyDocumentsDashlet extends Dashlet<MyDocumentsDashlet>
{
    @RenderWebElement
    @FindBy (css = "div.dashlet.my-documents")
    protected WebElement dashletContainer;
    @FindBy (css = "div[id$='default-documents']")
    protected WebElement documentsListContainer;
    @FindAll (@FindBy (css = "h3.filename > a"))
    protected List<WebElement> documentsLinksList;
    @FindAll (@FindBy (css = "div[id$='default-documents'] tr[class*='yui-dt-rec']"))
    protected List<WebElement> documentRowList;
    @FindAll (@FindBy (css = "div[class*='my-documents'] div.bd ul li"))
    protected List<WebElement> filterOptions;
    @FindBy (css = "div[class*='my-documents'] button[id$='default-filters-button']")
    protected WebElement filterButton;
    @FindBy (css = "div[class*='my-documents'] div[class*='empty']")
    protected WebElement defaultDocumentsText;
    @FindAll (@FindBy (css = "div[class*='my-documents'] div div div span button"))
    protected List<WebElement> viewButtons;
    @FindAll (@FindBy (css = "div[id$='default-documents'] .thumbnail a img"))
    protected List<WebElement> thumbnails;
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    @Override
    public String getRelativePath()
    {
        return super.getRelativePath();
    }

    /**
     * Get list of document links displayed in my documents dashlet.
     */
    public List<String> getDocumentsLinks()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("h3.filename > a"), 3);
        List<String> documentLinks = new ArrayList<>();
        for (WebElement element : documentsLinksList)
        {
            documentLinks.add(element.getText());
        }

        return documentLinks;
    }

    /**
     * Retrieves the link that match the document name.
     *
     * @param name identifier
     * @return {@link Link} that matches documentName
     */
    public WebElement selectDocument(final String documentName)
    {
        return browser.findFirstElementWithValue(documentsLinksList, documentName);
    }

    /**
     * Get list of entire document sections (that contains document details) displayed in my documents dashlet.
     */
    public List<WebElement> getDocumentsSections()
    {
        return documentRowList;
    }

    /**
     * Retrieves the link that match the document name.
     *
     * @param name identifier
     * @return {@link Link} that matches documentName
     */
    public WebElement selectDocumentDetailsRow(final String documentName)
    {
        return browser.findFirstElementWithValue(documentRowList, documentName);
    }

    /**
     * Select an option from filter dropdown from My Documents Dashlets.
     *
     * @param String filter
     * @return MyDocumentsDashlet
     */
    public MyDocumentsDashlet filterMyDocuments(String filter)
    {
        try
        {
            filterButton.click();
            browser.selectOptionFromFilterOptionsList(filter, filterOptions);

            Assert.assertTrue(browser.findElement(By.cssSelector("div.dashlet.my-documents button[id*='default-filters']")).getText().contains(filter),
                "Incorrect filter selected");

            browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[id$='default-documents'] tr[class*='yui-dt-rec'] h3.filename"));
            browser.selectOptionFromFilterOptionsList(filter, filterOptions);
            return (MyDocumentsDashlet) this.renderedPage();
        } catch (NoSuchElementException nse)
        {
            LOG.error("My documents option not present" + nse.getMessage());
            throw new PageOperationException(filter + " option not present.");
        }

    }

    public boolean isMyDocumentsDashletPopulated()
    {
        return browser.findDisplayedElementsFromLocator(By.cssSelector("div[id$='default-documents'] tr[class*='yui-dt-rec'] h3.filename")).size() > 0;
    }

    /**
     * Method to check if a document name is displayed in My Documents Dashlet
     *
     * @param documentName String
     * @return True if Document exists
     */
    public boolean isDocumentPresent(String documentName)
    {
        try
        {
            String filterOption = getSelectedOption();
            browser.selectOptionFromFilterOptionsList(filterOption, filterOptions);

            WebElement documentLink = selectDocument(documentName);
            if (documentLink != null)
            {
                return true;
            }
        } catch (TimeoutException | NoSuchElementException e)
        {
            LOG.error("Time out while finding document", e);
            return false;
        }
        return false;
    }

    /**
     * Check that all document filters are available in My Documents dashlet
     *
     * @return true if filter has proper values
     */
    public boolean isMyDocumentsFilterDisplayed()
    {
        boolean isFilterValid = false;

        filterButton.click();

        for (WebElement typeFav : filterOptions)
        {
            isFilterValid = EnumUtils.isValidEnum(DocumentsFilter.class, typeFav.getText());
        }

        return isFilterValid;
    }

    /**
     * Get displayed text when there is no document listed
     *
     * @return
     */
    public String getDefaultDocumentsText()
    {
        return defaultDocumentsText.getText();
    }

    public MyDocumentsDashlet setDocumentView(String viewOption)
    {
        try
        {
            for (WebElement viewButton : viewButtons)
                if (viewButton.getAttribute("title").equalsIgnoreCase(viewOption))
                {
                    viewButton.click();
                    break;
                }

            browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div.dashlet.my-documents h3.filename > a"));
        } catch (NoSuchElementException nse)
        {
            LOG.error("Document view option is not present.");
        }

        return (MyDocumentsDashlet) this.renderedPage();
    }

    /**
     * Checks whether comment link is displayed or not for a document
     *
     * @return
     */
    public boolean isCommentLinkDisplayed(String documentName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div.dashlet.my-documents a[class='comment']"));
        return browser.isElementDisplayed(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a[class='comment']")));

    }

    /**
     * Get modified information for a document
     *
     * @return
     */
    public String getModifiedInformation(String documentName)
    {
        return selectDocumentDetailsRow(documentName).findElement(By.cssSelector("div[id$='default-documents'] span.item")).getText();
    }

    /**
     * Get number of likes for a document
     *
     * @param documentName
     * @return
     */
    public int getNumberOfLikes(String documentName)
    {
        return Integer.parseInt(selectDocumentDetailsRow(documentName).findElement(By.cssSelector(".likes-count")).getText());
    }

    /**
     * Wait for documents dashlet to be populated
     *
     * @param documentName
     */
    public MyDocumentsDashlet waitForDocument()
    {
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div.dashlet.my-documents h3.filename > a"));
        return new MyDocumentsDashlet();
    }

    /**
     * Returns if the document is favorite.
     *
     * @param documentName Document Name checked for is Favorite.
     * @return boolean
     */
    public boolean isDocumentFavourite(String documentName)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(
            selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a.favourite-action.favourite3.enabled")));
        return browser.isElementDisplayed(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a.favourite-action.favourite3.enabled")));
    }

    /**
     * Returns if add to favorites link is present
     *
     * @param documentName Document Name checked for is Favorite.
     * @return boolean
     */
    public boolean isAddToFavoritesPresent(String documentName)
    {
        return browser.isElementDisplayed(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a[class^='favourite-action']")));
    }

    /**
     * Click on "Favorite" link for a document in My Documents Dashlet.
     *
     * @param documentName String
     */
    public MyDocumentsDashlet addDocumentToFavorites(String documentName)
    {
        try
        {
            selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a.favourite-action.favourite3")).click();
            browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("a.favourite-action.favourite3.enabled"));
        } catch (NoSuchElementException nse)
        {
            LOG.error("My Documents Dashlet is not present", nse);
        }

        return (MyDocumentsDashlet) this.renderedPage();
    }

    /**
     * Remove a document from Favorites
     *
     * @param documentName String
     */
    public MyDocumentsDashlet removeDocumentFromFavorites(String documentName)
    {
        try
        {
            int i = 0;
            while (browser.isElementDisplayed(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a.favourite-action.favourite3.enabled"))) && i < 5)
            {
                selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a.favourite-action.favourite3.enabled")).click();
                i++;
            }

            browser.refresh();
            browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("a[class='favourite-action.favourite3']"));
        } catch (NoSuchElementException nse)
        {
            LOG.error("My Documents Dashlet is not present", nse);
        }

        return (MyDocumentsDashlet) this.renderedPage();
    }

    /**
     * Get description for a document
     *
     * @param documentName
     * @return
     */
    public String getDocumentDescription(String documentName)
    {
        return selectDocumentDetailsRow(documentName).findElement(By.cssSelector("div[id$='default-documents'] tr[class*='yui-dt-rec'] span.faded")).getText();
    }

    /**
     * Get size of a document
     *
     * @param documentName
     * @return
     */
    public String getDocumentSize(String documentName)
    {
        return selectDocumentDetailsRow(documentName).findElement(By.cssSelector("div[id$='default-documents'] div[class='detail'] span.item:nth-of-type(2)"))
                                                     .getText();
    }

    /**
     * Get version of a document
     *
     * @param documentName
     * @return
     */
    public String getDocumentVersion(String documentName)
    {
        WebElement doc = selectDocumentDetailsRow(documentName);
        browser.mouseOver(doc);
        return doc.findElement(By.cssSelector("div[id$='default-documents'] .document-version")).getText();
    }

    /**
     * Checks whether large thumbnail is displayed on detailed view for a document
     *
     * @return
     */
    public boolean isLargeThumbnailDisplayed(String documentName)
    {
        try
        {
            return selectDocumentDetailsRow(documentName).findElement(By.cssSelector("div[id$='default-documents'] .thumbnail")).isDisplayed();
        } catch (TimeoutException detailedViewThumbnail)
        {

        }
        return false;
    }

    /**
     * Checks whether small thumbnail is displayed on simple view for a document
     *
     * @return
     */
    public boolean isSmallThumbnailDisplayed(String documentName)
    {
        try
        {
            return selectDocumentDetailsRow(documentName).findElement(By.cssSelector("div[id$='default-documents'] .icon32")).isDisplayed();
        } catch (TimeoutException simpleViewThumbnail)
        {

        }
        return false;
    }

    /**
     * @param documentName
     * @return
     */
    public DocumentDetailsPage accessDocument(final String documentName)
    {
        selectDocumentDetailsRow(documentName).findElement(By.cssSelector("h3.filename > a")).click();
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-social']"), 5);
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * @param documentName
     * @return
     */
    public MyDocumentsDashlet likeDocument(String documentName)
    {
        browser.findFirstElementWithValue(By.cssSelector("div[id$='default-documents'] tr[class*='yui-dt-rec']"), documentName)
               .findElement(By.cssSelector(".like-action")).click();
        browser.refresh();

        return (MyDocumentsDashlet) this.renderedPage();
    }

    /**
     * Press Add Comment link for a specific document
     *
     * @param documentName
     * @return
     */
    public DocumentDetailsPage addComment(String documentName)
    {
        browser.findFirstElementWithValue(By.cssSelector("div[id$='default-documents'] tr[class*='yui-dt-rec']"), documentName)
               .findElement(By.cssSelector(".comment")).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Access document details page by pressing document thumbnail
     *
     * @param documentName
     * @return
     */
    public DocumentDetailsPage pressDocumentThumbnail(String documentName)
    {
        selectDocumentDetailsRow(documentName).findElement(By.cssSelector("span[class='thumbnail'] a")).click();
        browser.waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[class='node-social']"), 5);
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    /**
     * Checks whether liked icon is displayed or not for a document
     *
     * @return
     */
    public boolean isLikedIconDisplayed(String documentName)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a[class*='enabled']")));
        return browser.isElementDisplayed(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a[class*='enabled']")));
    }

    /**
     * Checks whether like link is displayed or not for a document
     *
     * @return
     */
    public boolean isLikeLinkDisplayed(String documentName)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a[class*='like-action']")));
        return browser.isElementDisplayed(selectDocumentDetailsRow(documentName).findElement(By.cssSelector("a[class*='like-action']")));
    }

    /**
     * Get number of documents
     *
     * @return
     */
    public int getNumberOfDocuments()
    {
        return getDocumentsLinks().size();
    }

    /**
     * Get the current selected option from filter
     *
     * @return
     */
    public String getSelectedOption()
    {
        String actualOption = filterButton.getText();
        actualOption = actualOption.substring(0, actualOption.length() - 2);
        return actualOption;
    }

    /**
     * Check that expected number of documents are listed
     *
     * @param noOfDocs
     * @return
     */
    public boolean areNumberOfDocumentsDisplayed(int noOfDocs)
    {

        boolean bool = dashletContainer.findElements(By.cssSelector("h3.filename > a")).size() == noOfDocs;
        int i = 0;
        while (!bool && i < 5)
        {
            browser.refresh();
            bool = dashletContainer.findElements(By.cssSelector("h3.filename > a")).size() == noOfDocs;
            i++;
        }
        return bool;
    }

    /**
     * Refreshes the browser while Error loading documents is displayed
     */
    public void refreshWhileErrorLoadingDocumentsIsDisplayed()
    {
        By by = By.xpath("//*[contains(text(), 'Error')]");
        int attempt = 0;
        boolean errorIsDisplayed = browser.findElements(by).size() > 0;
        while (errorIsDisplayed && attempt < 5)
        {
            errorIsDisplayed = browser.findElements(by).size() == 0;
            attempt++;
            browser.refresh();
        }
    }

    public enum DocumentsFilter
    {
        RecentlyModified
            {
                public String toString()
                {
                    return "I've Recently Modified";
                }
            },

        Editing
            {
                public String toString()
                {
                    return "I'm Editing";
                }
            },

        MyFavorites
            {
                public String toString()
                {
                    return "My Favorites";
                }
            }
    }

    public enum FavoriteLinks
    {
        Add
            {
                public String toString()
                {
                    return "Add document to favorites";
                }
            },

        Remove
            {
                public String toString()
                {
                    return "Remove document from favorites";
                }
            }
    }

    public enum DocumentView
    {
        SimpleView
            {
                public String toString()
                {
                    return "Simlpe View";
                }
            },
        DetailedView
            {
                public String toString()
                {
                    return "Detailed View";
                }
            },

    }
}