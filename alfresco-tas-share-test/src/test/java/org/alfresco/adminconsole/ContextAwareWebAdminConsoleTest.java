package org.alfresco.adminconsole;

import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.utility.web.AbstractWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author iulia.burca
 */
@ContextConfiguration (classes = ShareTestContext.class)
@Scope (value = "prototype")
public abstract class ContextAwareWebAdminConsoleTest extends AbstractWebTest
{
    @Autowired
    protected EnvProperties properties;

    @Autowired
    protected Language language;

    @Override
    public String getPageObjectRootPackage()
    {
        return "org/alfresco/po/adminconsole";
    }
}