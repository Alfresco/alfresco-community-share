package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;

@PageObject
public class ModelAndMessagesConsolePage extends ConsolePage<ModelAndMessagesConsolePage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/admin/admin-repoconsole";
    }
}
