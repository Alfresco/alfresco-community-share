package org.alfresco.po.adminconsole;

import org.alfresco.po.adminconsole.repositoryservices.SearchServicePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author Iulia.Burca
 */

public class SearchServicePageTests extends ContextAwareWebTest
{

    @Autowired
    SearchServicePage searchServicePage;


    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        //1. Admin user is logged into the Share
        //2. System Administration Console is opened
        //3. Search Service page is opened
        searchServicePage.setBrowser(getBrowser());
        searchServicePage.navigate();
        //4. SOLR search service is chosen (from *Search Service In Use* dropdown)
        searchServicePage.selectType(SearchServicePage.Type.SOLR6); //Select SOLR
        searchServicePage.saveButton.click();//Save the changes
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        //TODO find a way to get to default settings after the tests
        cleanupAuthenticatedSession();
    }

    //TODO the Test page should not have so many clicks and WebElements. For clicks we should create methods and WebElements must be declared also in Page.java but without @Render if those elements are dynamically displayed
    @TestRail (id = "C231607")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void trackingEnabledCheckBox() throws Exception
    {
        LOG.info("STEP1: Check the Tracking Enabled check-box");
        WebElement contentTrackingCheckBox = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']"));

        if (contentTrackingCheckBox.isSelected())
        {
            LOG.info("Checkbox is Toggled On");
        } else
        {
            contentTrackingCheckBox.click();
            Assert.assertTrue(contentTrackingCheckBox.isSelected(), "Checkbox is Toggled Off");
        }

        LOG.info("STEP2: Click on Save button");
        searchServicePage.saveButton.click();
        LOG.info("STEP3: Check the value via JMX");
        Assert.assertTrue(searchServicePage.getContentTrackingEnabledValue().equalsIgnoreCase("true"), "The 'Content Tracking Enabled' checkbox is marked in JMX as 'false' (unchecked)!");
        LOG.info("STEP4: Uncheck the Tracking Enabled check-box");
        if (!getBrowser().isElementDisplayed(contentTrackingCheckBox))
        {
            contentTrackingCheckBox = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']"));
        }
        contentTrackingCheckBox.click();

        LOG.info("STEP5: Click on Save button");
        searchServicePage.saveButton.click();

        if (!getBrowser().isElementDisplayed(contentTrackingCheckBox))
        {
            contentTrackingCheckBox = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']"));
        }

        Assert.assertFalse(contentTrackingCheckBox.isSelected(), "Checkbox couldn't be unchecked. It remained checked after Saving the page.");
        LOG.info("STEP6: Check the value via JMX");
        Assert.assertTrue(searchServicePage.getContentTrackingEnabledValue().equalsIgnoreCase("false"), "The 'Content Tracking Enabled' checkbox is marked in JMX as 'true' (checked)!");
        LOG.info("STEP7: Set the value of the Tracking Enabled property to TRUE via JMX");
        searchServicePage.setContentTrackingEnabledValue(true);
        Assert.assertTrue(searchServicePage.getContentTrackingEnabledValue().equalsIgnoreCase("true"), "The 'Content Tracking Enabled' checkbox value could not be marked in JMX as TRUE!");
        LOG.info("STEP8: Check the value via Search Service page");
        getBrowser().refresh();

        if (!getBrowser().isElementDisplayed(contentTrackingCheckBox))
        {
            contentTrackingCheckBox = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']"));
        }

        Assert.assertTrue(contentTrackingCheckBox.isSelected(), "The 'Content Tracking Enabled' checkbox was set as 'true' on JMX but in Search Service page is unchecked! ");
        LOG.info("STEP9: Set the value of the Tracking Enabled property to FALSE via JMX");
        searchServicePage.setContentTrackingEnabledValue(false);
        Assert.assertTrue(searchServicePage.getContentTrackingEnabledValue().equalsIgnoreCase("false"), "The 'Content Tracking Enabled' checkbox value could not be marked in JMX as FALSE!");
        LOG.info("STEP10: Check the value via Search Service page");
        getBrowser().refresh();

        if (!getBrowser().isElementDisplayed(contentTrackingCheckBox))
        {
            contentTrackingCheckBox = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//div[@class='column-left']//input[@type='checkbox']"));
        }

        Assert.assertFalse(contentTrackingCheckBox.isSelected(), "The 'Content Tracking Enabled' checkbox was set as 'false' on JMX but in Search Service page is checked! ");
    }


    @TestRail (id = "C284873")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void solrSslPortField() throws Exception
    {
        Integer port = 8985;

        LOG.info("STEP1: Fill the Solr SSL Port field with a correct data (should contain a number)");
        WebElement solrSslPort = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//input[contains(@name, 'solr.port.ssl')]"));
        //Delete actual textbox value
        solrSslPort.clear();
        //Write new values in textbox
        solrSslPort.sendKeys("1234");
        LOG.info("STEP2: Click on Save button");
        searchServicePage.saveButton.click();
        LOG.info("STEP3: Check the value via JMX");
        Assert.assertTrue(searchServicePage.getSolrSslPortValue().equalsIgnoreCase("1234"), "Solr SSL Port value could not be changed as per UI.");
        LOG.info("STEP4: Set the value of the Solr SSL Port property to true via JMX ");
        searchServicePage.setSolrSslPortValue(port);
        Assert.assertTrue(searchServicePage.getSolrSslPortValue().equals(port.toString()), "Solr SSL Port value cannot not be changed to " + port + " in JMX.");
        LOG.info("STEP5: Check the value via Search Service page");
        getBrowser().refresh();
        solrSslPort = getBrowser().findElement(By.xpath("//div[@id='solrSearch6']//input[contains(@name, 'solr.port.ssl')]"));
        Assert.assertTrue(solrSslPort.getAttribute("value").equals(port.toString()), "Solr SSL Port value is not 'true' in Search Service page as per JMX changes.");
    }


    @TestRail (id = "C284876")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void solrPortNonSslField() throws Exception
    {
        Integer port = 8983;

        LOG.info("STEP1: Fill the 'Solr Port (Non-SSL)' field with a correct data (should contain a number)");
        WebElement solrPortNonSsl = getBrowser().findElement(By.cssSelector("div[id='solrSearch6'] input[name$='solr.port']"));
        //Delete actual textbox value
        solrPortNonSsl.clear();
        //Write new values in textbox
        solrPortNonSsl.sendKeys("5678");
        LOG.info("STEP2: Click on Save button");
        searchServicePage.saveButton.click();
        LOG.info("STEP3: Check the value via JMX");
        Assert.assertTrue(searchServicePage.getSolrPortNonSslValue().equals("5678"), "'Solr Port (Non-SSL)' value could not be changed as per UI.");
        LOG.info("STEP4: Set the value of the 'Solr Port (Non-SSL)' property to true via JMX ");
        searchServicePage.setSolrPortNonSslValue(port);
        Assert.assertTrue(searchServicePage.getSolrPortNonSslValue().equals(port.toString()), "'Solr Port (Non-SSL)' value cannot not be changed to " + port + " in JMX.");
        LOG.info("STEP5: Check the value via Search Service page");
        getBrowser().refresh();
        solrPortNonSsl = getBrowser().findElement(By.cssSelector("div[id='solrSearch6'] input[name$='solr.port']"));
        Assert.assertTrue(solrPortNonSsl.getAttribute("value").equals(port.toString()), "'Solr Port (Non-SSL)' value is not 'true' in Search Service page as per JMX changes.");
    }


    //TODO finish solrHostnameField test
    @TestRail (id = "C284881Edit")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void solrHostnameField() throws Exception
    {
        Integer port = 8983;

        LOG.info("STEP1: Fill the 'Solr Hostname' field with a correct data.");
        WebElement solrHostnameField = getBrowser().findElement(By.cssSelector("div[id='solrSearch6'] input[name$='solr.host']"));
        //Delete actual textbox value
        solrHostnameField.clear();
        //Write new values in textbox
//        solrHostnameField.sendKeys("5678");
        LOG.info("STEP2: Click on Save button.");
//        searchServicePage.saveButton.click();
        LOG.info("STEP3: Check the value via JMX.");
//        Assert.assertTrue(searchServicePage.getSolrHostnameFieldValue().equals("5678"), "'Solr Port (Non-SSL)' value could not be changed as per UI.");
        LOG.info("STEP4: Set the value of the Solr Hostname property to true via JMX.");
//        searchServicePage.setSolrHostnameFieldValue(port);
//        Assert.assertTrue(searchServicePage.getSolrHostnameFieldValue().equals(port.toString()), "'Solr Port (Non-SSL)' value cannot not be changed to " + port + " in JMX.");
        LOG.info("STEP5: Check the value via Search Service page.");
//        getBrowser().refresh();
//        solrHostnameField = getBrowser().findElement(By.cssSelector("div[id='solrSearch6'] input[name$='solr.host']"));
//        Assert.assertTrue(solrHostnameField.getAttribute("value").equals(port.toString()), "'Solr Port (Non-SSL)' value is not 'true' in Search Service page as per JMX changes.");
    }
}
