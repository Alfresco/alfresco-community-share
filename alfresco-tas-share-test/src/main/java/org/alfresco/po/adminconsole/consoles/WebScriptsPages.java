package org.alfresco.po.adminconsole.consoles;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import static org.alfresco.utility.report.log.Step.STEP;

public class WebScriptsPages<T> extends SharePage2<WebScriptsPages<T>> {

    public WebScriptsPages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/service/index";
    }

    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public WebScriptsPages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public WebScriptsPages assertPageTitleIs(String expectedPageTitle)
    {
        Assert.assertEquals(getPageTitle(), expectedPageTitle, "Page title is correct");
        return this;
    }
}
