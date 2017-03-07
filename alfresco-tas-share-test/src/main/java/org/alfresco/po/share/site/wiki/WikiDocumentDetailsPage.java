package org.alfresco.po.share.site.wiki;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class WikiDocumentDetailsPage extends SiteCommon<WikiDocumentDetailsPage>
{

    @RenderWebElement
    @FindBy(css = "[id$=default-next-button]")
    private WebElement nextButton;

    @FindBy(css = "default-previous-button")
    private WebElement previousButton;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/document-details?", getCurrentSiteName());
    }

}
