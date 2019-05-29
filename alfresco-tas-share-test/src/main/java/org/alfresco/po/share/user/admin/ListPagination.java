package org.alfresco.po.share.user.admin;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Created by Claudia Agache on 7/4/2016.
 */
@PageObject
public class ListPagination extends HtmlPage
{
    @FindBy (id = "DOCLIB_PAGINATION_MENU")
    private WebElement paginationGroup;

    @FindBy (id = "DOCLIB_PAGINATION_MENU_PAGE_SELECTOR")
    private WebElement pageSelector;

    @FindBy (id = "DOCLIB_PAGINATION_MENU_PAGE_BACK")
    private Button previousButton;

    @FindBy (id = "DOCLIB_PAGINATION_MENU_PAGE_FORWARD")
    private WebElement nextButton;

    @FindBy (id = "DOCLIB_PAGINATION_MENU_PAGE_MARKER")
    private WebElement pageMarker;

    @FindBy (id = "DOCLIB_PAGINATION_MENU_RESULTS_PER_PAGE_SELECTOR")
    private WebElement resultsPerPageSelector;

    /**
     * Checks if there is a next page available.
     *
     * @return true, if successful
     */
    public boolean hasNextPage()
    {
        return nextButton.getAttribute("aria-disabled").equals("false");
    }

    /**
     * Click the next page button.
     */
    public void clickNextButton()
    {
        if (hasNextPage())
        {
            nextButton.click();
        }
    }
}
