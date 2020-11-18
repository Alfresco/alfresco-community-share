package org.alfresco.po.share.site;

import org.alfresco.po.share.alfrescoContent.AlfrescoContentPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebBrowser;

public class DocumentLibraryPage2 extends AlfrescoContentPage<DocumentLibraryPage2>
{
    private String siteId;

    public DocumentLibraryPage2(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getSiteId()
    {
        return siteId;
    }

    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/documentlibrary", getSiteId());
    }

    public DocumentLibraryPage2 navigate(String siteId)
    {
        setSiteId(siteId);
        return (DocumentLibraryPage2) navigate().renderedPage();
    }

    public DocumentLibraryPage2 navigate(SiteModel site)
    {
        return navigate(site.getId());
    }
}
