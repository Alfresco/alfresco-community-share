package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 11/16/2017.
 */
@PageObject
public class BulkImportConsolePage extends Console<BulkImportConsolePage>
{
    @RenderWebElement
    @FindBy(id = "sourceDirectory")
    private WebElement importDirectoryPathInput;

    @FindBy(id = "contentStore")
    private WebElement contentStoreInput;

    @FindBy(id = "targetPath")
    private WebElement targetPathInput;

    @FindBy(name = "batchSize")
    private WebElement batchSize;

    @FindBy(name = "numThreads")
    private WebElement numThreads;

    @RenderWebElement
    @FindBy(name = "submit")
    private WebElement submitButton;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/service/bulkfsimport/inplace";
    }

    public BulkImportConsolePage assertImportDirectoryPathInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(importDirectoryPathInput), "Import directory path input is displayed");
        return this;
    }

    public BulkImportConsolePage assertContentStoreInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(contentStoreInput), "Content store input is displayed");
        return this;
    }

    public BulkImportConsolePage assertTargetPathInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(targetPathInput), "Target path input is displayed");
        return this;
    }

    public BulkImportConsolePage assertBatchSizeInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(batchSize), "Batch Size input is displayed");
        return this;
    }

    public BulkImportConsolePage assertNumThreadsInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(numThreads), "Num Threads input is displayed");
        return this;
    }

    public BulkImportConsolePage assertSubmitButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(submitButton), "Submit button is displayed");
        return this;
    }
}
