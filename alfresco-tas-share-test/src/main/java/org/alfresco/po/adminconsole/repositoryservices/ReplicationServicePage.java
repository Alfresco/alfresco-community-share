package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ReplicationServicePage extends AdminConsolePage<ReplicationServicePage>
{
    public static final String REPLICATION_SERVICE_OBJECT = "Alfresco:Type=Configuration,Category=Replication,id1=default";
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-replicationservice";
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

    public enum REPLICATION_SERVICE_LABELS
    {
        REPLICATION_ENABLED("Replication Enabled:", "replication.enabled"),
        READ_ONLY_REPLICATION("Read Only Replication:", "replication.transfer.readonly");

        private String label, jmxAttribute;

        REPLICATION_SERVICE_LABELS(String label, String jmxAttribute)
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