package org.alfresco.po.share.site.wiki;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ViewWikiPage extends SiteCommon<ViewWikiPage>
{
    @RenderWebElement
    @FindBy(css = "a[href*='details']")
    private WebElement wikiPageDetailsLink;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/wiki-page?title=Main_Page&action=view", getCurrentSiteName());
    }

}
