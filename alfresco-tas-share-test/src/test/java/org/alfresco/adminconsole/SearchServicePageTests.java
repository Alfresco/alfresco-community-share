package org.alfresco.adminconsole;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.repositoryservices.SearchServicePages;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Iulia.Burca
 */
@Slf4j
public class SearchServicePageTests extends BaseTest
{
    private SearchServicePages searchServicePages;

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        searchServicePages = new SearchServicePages(webDriver);
        searchServicePages.navigate();
        searchServicePages.ClickOnSaveButton();
    }

    @TestRail (id = "C231607")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void trackingEnabledCheckBox() {
        log.info("STEP1: Check the Tracking Enabled check-box");
        if (searchServicePages.contentTrackingCheckBox())
        {
            log.info("Checkbox is Toggled On");
        } else
        {
            searchServicePages.ClickOncontentTrackingCheckBox();
            Assert.assertTrue(searchServicePages.contentTrackingCheckBox(), "Checkbox is Toggled Off");
        }

        log.info("STEP2: Click on Save button");
        searchServicePages.ClickOnSaveButton();
        log.info("STEP4: Uncheck the Tracking Enabled check-box");
        if (!searchServicePages.displayedContentTrackingCheckBox())
        {
            log.info("Checkbox is Toggled On");
        }
        searchServicePages.ClickOncontentTrackingCheckBox();

        log.info("STEP5: Click on Save button");
        searchServicePages.ClickOnSaveButton();
        if (!searchServicePages.contentTrackingCheckBox())
        {
            log.info("Checkbox is Toggled On");
        }

        Assert.assertFalse(searchServicePages.contentTrackingCheckBox(), "Checkbox couldn't be unchecked. It remained checked after Saving the page.");
        log.info("STEP8: Check the value via Search Service page");
        if (!searchServicePages.displayedContentTrackingCheckBox())
        {
            log.info("Checkbox is Toggled On");
        }

        Assert.assertFalse(searchServicePages.contentTrackingCheckBox(), "The 'Content Tracking Enabled' checkbox was set as 'true' on JMX but in Search Service page is unchecked! ");
        log.info("STEP10: Check the value via Search Service page");
        if (!searchServicePages.displayedContentTrackingCheckBox())
        {
            log.info("Checkbox is Toggled On");
        }

        Assert.assertFalse(searchServicePages.contentTrackingCheckBox(), "The 'Content Tracking Enabled' checkbox was set as 'false' on JMX but in Search Service page is checked! ");
    }

    @TestRail (id = "C284873")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void solrSslPortField() {
        Integer port = 1234;
        log.info("STEP1: Fill the Solr SSL Port field with a correct data (should contain a number)");
        searchServicePages.fillValueSolrPort();
        log.info("STEP2: Click on Save button");
        searchServicePages.ClickOnSaveButton();
        log.info("STEP5: Check the value via Search Service page");
        Assert.assertTrue(searchServicePages.getSolrSslPort().equals(port.toString()), "Solr SSL Port value is not 'true' in Search Service page as per JMX changes.");
    }

    @TestRail (id = "C284876")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void solrPortNonSslField() throws Exception
    {
        Integer port = 5678;
        log.info("STEP1: Fill the 'Solr Port (Non-SSL)' field with a correct data (should contain a number)");
        searchServicePages.fillValuesolrPortNonSsl();
        log.info("STEP2: Click on Save button");
        searchServicePages.ClickOnSaveButton();
        log.info("STEP5: Check the value via Search Service page");
        Assert.assertTrue(searchServicePages.getSolrPortNonSsl().equals(port.toString()), "'Solr Port (Non-SSL)' value is not 'true' in Search Service page as per JMX changes.");
    }

    @TestRail (id = "C284881Edit")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void solrHostnameField() throws Exception
    {
        Integer port = 5678;
        log.info("STEP1: Fill the 'Solr Hostname' field with a correct data.");
        searchServicePages.fillValuesolrPortNonSsl();
        log.info("STEP2: Click on Save button.");
        searchServicePages.ClickOnSaveButton();
        log.info("STEP5: Check the value via Search Service page.");
        Assert.assertTrue(searchServicePages.getSolrPortNonSsl().equals(port.toString()), "'Solr Port (Non-SSL)' value is not 'true' in Search Service page as per JMX changes.");
    }
}
