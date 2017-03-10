package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class DeleteWikiPagePopUp extends DeleteDialog
{
    @Autowired
    WikiListPage wikiListPage;

    public WikiListPage clickDeleteWikiPage()
    {
        clickDelete();
        return (WikiListPage) wikiListPage.renderedPage();
    }
}
