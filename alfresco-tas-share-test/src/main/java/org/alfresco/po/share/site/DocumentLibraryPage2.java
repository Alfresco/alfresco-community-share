package org.alfresco.po.share.site;

import org.alfresco.po.share.alfrescoContent.AlfrescoContentPage;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.WebDriver;

public class DocumentLibraryPage2 extends AlfrescoContentPage<DocumentLibraryPage2>
{
    private String siteId;

    public DocumentLibraryPage2(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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

    public synchronized DocumentLibraryPage2 navigate(SiteModel site)
    {
        setSiteId(site.getId());
        return (DocumentLibraryPage2) navigate();
    }
}
