package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class AdminToolsPage extends SharePage<AdminToolsPage> implements AccessibleByMenuBar
{
    @Autowired
    private Toolbar toolbar;

    @RenderWebElement
    @FindAll(@FindBy(css = ".tool-link"))
    private List<WebElement> toolsLinksList;

    @RenderWebElement
    @FindBy(css = "#alf-filters>div[id$='admin-console']")
    private WebElement adminToolsDiv;

    private By toolsList = By.cssSelector(".tool-link");

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/application";
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdminToolsPage navigateByMenuBar()
    {
        toolbar.clickAdminTools();
        return (AdminToolsPage) renderedPage();
    }

    public boolean isAdminToolsDivDisplayed()
    {
        return browser.isElementDisplayed(adminToolsDiv);
    }

    /**
     * Navigate in Tools panel by click on the specified tree node
     * 
     * @param treeNode to click on
     */
    public void navigateToNodeFromToolsPanel(String treeNode)
    {
        for (int i = 0; i < toolsLinksList.size(); i++)
        {
            WebElement treeNodeElement = toolsLinksList.get(i);
            if (treeNodeElement.getText().equals(treeNode))
                treeNodeElement.click();
        }
    }

    public WebElement selectTool(String toolName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(toolsList, 6);
        List<WebElement> itemsList = browser.findElements(toolsList);
        return browser.findFirstElementWithValue(itemsList, toolName);
    }

    public boolean isToolAvailable(String toolName)
    {
        return browser.isElementDisplayed(selectTool(toolName));
    }

    public void clickOnAvailableTool(String toolName)
    {

        browser.findFirstElementWithValue(toolsList, toolName).click();
    }
}