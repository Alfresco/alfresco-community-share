package org.alfresco.adminconsole.alfrescoConsoleTest;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.consoles.WebScriptsPages;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
@Slf4j
public class WebScriptsPageTests extends BaseTest
{
    private WebScriptsPages webScriptsPages;

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void checkWebScriptsPage()
    {
        webScriptsPages = new WebScriptsPages(webDriver);
        webScriptsPages.navigate();
        webScriptsPages.assertPageTitleIs("Web Scripts Home");
    }
}

