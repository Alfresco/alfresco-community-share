package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SearchServicePage extends AdminConsolePage<SearchServicePage>
{
    public static final String IMAP_OBJECT = "Alfresco:Type=Configuration,Category=Search,id1=manager";
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-searchservice";
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

}
