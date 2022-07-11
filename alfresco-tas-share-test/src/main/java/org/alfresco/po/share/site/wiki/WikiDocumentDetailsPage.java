package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class WikiDocumentDetailsPage extends SiteCommon<WikiDocumentDetailsPage>
{
    @RenderWebElement
    @FindBy (css = "[id$=default-next-button]")
    private WebElement nextButton;

    @FindBy (css = "default-previous-button")
    private WebElement previousButton;

    public WikiDocumentDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/document-details?", getCurrentSiteName());
    }
}
