package org.alfresco.po.share.alfrescoContent.organizingContent;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Claudia Agache on 9/13/2016.
 */
@PageObject
public class DeleteDocumentOrFolderDialog extends DeleteDialog
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    public DocumentLibraryPage confirmDocumentOrFolderDelete()
    {
        clickDelete();
        browser.waitInSeconds(3);
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }
}
