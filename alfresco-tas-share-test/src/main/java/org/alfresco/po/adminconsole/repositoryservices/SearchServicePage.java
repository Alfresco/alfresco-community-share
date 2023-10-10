package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.common.EnvProperties;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.AuthenticationChain;
import org.alfresco.utility.network.JmxBuilder;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Select;


@PageObject
public class SearchServicePage extends AdminConsolePage<AuthenticationChain>
{
    public static final String IMAP_OBJECT = "Alfresco:Type=Configuration,Category=Search,id1=managed";
    @RenderWebElement
    @FindBy (xpath = "//input[@value='Save'][@type='submit']")
    public WebElement saveButton;
    @RenderWebElement
    @FindBy (xpath = "//input[@value='Cancel'][@type='button']")
    public WebElement cancelButton;
    @FindBy (id = "searchService")
    public Select searchServiceDropDown;
    @Autowired
    protected EnvProperties properties;
    String jmxSolrPath = ",id2=solr6";
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;
    @Autowired
    private JmxBuilder jmxBuilder;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-searchservice";
    }

    //JMX Actions
    public String getContentTrackingEnabledValue() throws Exception
    {
        String contentTrackingEnabledCheckBoxValue = (String) jmxBuilder.getJmxClient().readProperty(IMAP_OBJECT + jmxSolrPath, SearchService.contentTrackingEnabled.jmxAttribute);
        return contentTrackingEnabledCheckBoxValue;
    }

    public void setContentTrackingEnabledValue(boolean value) throws Exception
    {
        jmxBuilder.getJmxClient().writeProperty(IMAP_OBJECT + jmxSolrPath, SearchService.contentTrackingEnabled.jmxAttribute, value);
    }

    public String getSolrSslPortValue() throws Exception
    {
        return (String) jmxBuilder.getJmxClient().readProperty(IMAP_OBJECT + jmxSolrPath, SearchService.solrSSLPort.jmxAttribute);
    }

    public void setSolrSslPortValue(int port) throws Exception
    {
        jmxBuilder.getJmxClient().writeProperty(IMAP_OBJECT + jmxSolrPath, SearchService.solrSSLPort.jmxAttribute, port);
    }

    public String getSolrPortNonSslValue() throws Exception
    {
        return (String) jmxBuilder.getJmxClient().readProperty(IMAP_OBJECT + jmxSolrPath, SearchService.solrPort.jmxAttribute);
    }

    public void setSolrPortNonSslValue(int port) throws Exception
    {
        jmxBuilder.getJmxClient().writeProperty(IMAP_OBJECT + jmxSolrPath, SearchService.solrPort.jmxAttribute, port);
    }

    public String getSolrHostnameFieldValue() throws Exception
    {
        return (String) jmxBuilder.getJmxClient().readProperty(IMAP_OBJECT + jmxSolrPath, SearchService.solrHost.jmxAttribute);
    }

    public void setSolrHostnameFieldValue(int port) throws Exception
    {
        jmxBuilder.getJmxClient().writeProperty(IMAP_OBJECT + jmxSolrPath, SearchService.solrHost.jmxAttribute, port);
    }

    //Select the item from the drop-down
    public SearchServicePage selectType(Type type)
    {
        searchServiceDropDown.selectByValue(type.getValue());
        return this;
    }

    @Override
    public String getInfoPage()
    {
        return "";
    }

    @Override
    public String getIntroPage()
    {
        return intro.getText();
    }

    public enum SearchService
    {
        searchServiceInUse("Search Service In Use:", "sourceBeanName"),
        contentTrackingEnabled("Content Tracking Enabled:", "search.solrTrackingSupport.enabled"),
        solrPort("Solr Port (Non-SSL):", "solr.port"),
        solrHost("Solr Hostname", "solr.hos"),
        solrBaseURL("Solr base URL:", "solr.baseUrl"),
        solrSSLPort("Solr SSL Port:", "solr.port.ssl"),
        autoSuggestEnabled("Auto Suggest Enabled:", "solr.suggester.enabled"),
        mainIndexingInProgress("Indexing in Progress:", "tracker.alfresco.active"),
        mainLastIndexedTransaction("Last Indexed Transaction:", "tracker.alfresco.last.indexed.txn"),
        mainApproxIndexTimeRemaining("Approx Index Time Remaining:", "tracker.alfresco.approx.indexing.time.remaining"),
        mainDiskUsage("Disk Usage (GB):", "tracker.alfresco.disk"),
        mainIndexLag("Index Lag:", "tracker.alfresco.lag"),
        mainApproxTransactionsToIndex("Approx Transactions to Index:", "tracker.alfresco.approx.txns.remaining"),
        mainMemoryUsage("Memory Usage (GB): ", "tracker.alfresco.memory"),
        archiveIndexingInProgress("Indexing in Progress:", "tracker.archive.active"),
        archiveLastIndexedTransaction("Last Indexed Transaction:", "tracker.archive.last.indexed.txn"),
        archiveApproxIndexTimeRemaining("Approx Index Time Remaining:", "tracker.archive.approx.indexing.time.remaining"),
        archiveDiskUsage("Disk Usage (GB):", "tracker.archive.disk"),
        archiveIndexLag("Index Lag:", "tracker.archive.lag"),
        archiveApproxTransactionsToIndex("Approx Transactions to Index:", "tracker.archive.approx.txns.remaining"),
        archiveMemoryUsage("Memory Usage (GB): ", "tracker.archive.memory"),
        mainBackupLocation("Backup Location:", "solr.backup.alfresco.remoteBackupLocation"),
        mainBackupCronExpression("Backup Cron Expression:", "solr.backup.alfresco.cronExpression"),
        mainBackupsToKeep("Backups To Keep: ", "solr.backup.alfresco.numberToKeep"),
        archiveBackupLocation("Backup Location:", "solr.backup.archive.remoteBackupLocation"),
        archiveBackupCronExpression("Backup Cron Expression:", "solr.backup.archive.cronExpression"),
        archiveBackupsToKeep("Backups To Keep: ", "solr.backup.archive.numberToKeep"),
        cmisQuery("CMIS Query:", "solr.query.cmis.queryConsistency"),
        alfrescoFullTextSearch("Alfresco Full Text Search:", "solr.query.fts.queryConsistency");

        private String label, jmxAttribute;

        SearchService(String label, String jmxAttribute)
        {
            this.label = label;
            this.jmxAttribute = jmxAttribute;
        }

        public String getLabel()
        {
            return label;
        }

        public String getJmxAttribute()
        {
            return jmxAttribute;
        }
    }

    //Get the list of items from the drop-down
    public enum Type
    {
        NOINDEX("noindex", "No Index"),
        SOLR6("solr6", "Solr 6"),
        SOLR4("sorl4", "Solr 4"),
        SOLR1("solr", "Solr 1 (use only for migration to Solr 4)");

        private String value, displayedText;

        Type(String value, String displayedText)
        {
            this.value = value;
            this.displayedText = displayedText;
        }

        public String getValue()
        {
            return value;
        }

        public String getDisplayedText()
        {
            return displayedText;
        }
    }
}