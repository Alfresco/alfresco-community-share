package org.alfresco.po.adminconsole.consoles;

import org.alfresco.common.ShareTestContext;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;

import static org.alfresco.utility.report.log.Step.STEP;

@ContextConfiguration(classes = ShareTestContext.class)
public class BulkImportConsolePages<T> extends SharePage2<BulkImportConsolePages<T>> {

    private By importDirectoryPathInput = By.id("sourceDirectory");
    private By contentStoreInput = By.id("contentStore");
    private By targetPathInput = By.id("targetPath");
    private By batchSize = By.name("batchSize");
    private By submitButton = By.name("submit");
    private By numThreads = By.name("numThreads");

    public BulkImportConsolePages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/service/bulkfsimport/inplace";
    }

    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public BulkImportConsolePages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public BulkImportConsolePages assertPageTitleIs(String expectedPageTitle)
    {
        Assert.assertEquals(getPageTitle(), expectedPageTitle, "Page title is correct");
        return this;
    }

    public BulkImportConsolePages assertImportDirectoryPathInputIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(importDirectoryPathInput), "Import directory path input is displayed");
        return this;
    }

    public BulkImportConsolePages assertContentStoreInputIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(contentStoreInput), "Content store input is displayed");
        return this;
    }

    public BulkImportConsolePages assertTargetPathInputIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(targetPathInput), "Target path input is displayed");
        return this;
    }

    public BulkImportConsolePages assertBatchSizeInputIsDisplayed()
    {
        waitInSeconds(3);
        Assert.assertTrue(isElementDisplayed(findElement(batchSize)), "Batch Size input is displayed");
        return this;
    }

    public BulkImportConsolePages assertNumThreadsInputIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(numThreads), "Num Threads input is displayed");
        return this;
    }

    public BulkImportConsolePages assertSubmitButtonIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(submitButton), "Submit button is displayed");
        return this;
    }
}
