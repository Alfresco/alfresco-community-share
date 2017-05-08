package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;

@PageObject
public class WorkflowConsolePage extends ConsolePage<WorkflowConsolePage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/admin/admin-workflowconsole";
    }
}
