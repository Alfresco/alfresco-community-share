package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ViewWikiPage extends SiteCommon<ViewWikiPage>
{
    @RenderWebElement
    @FindBy (css = "a[href*='details']")
    private WebElement wikiPageDetailsLink;

    public ViewWikiPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=Main_Page&action=view", getCurrentSiteName());
    }
}
