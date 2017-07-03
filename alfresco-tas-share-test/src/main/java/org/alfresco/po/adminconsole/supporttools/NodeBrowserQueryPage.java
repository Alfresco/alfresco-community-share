package org.alfresco.po.adminconsole.supporttools;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.po.adminconsole.supporttools.Node.NodeChild;
import org.alfresco.po.adminconsole.supporttools.Node.NodeDetails;
import org.alfresco.po.adminconsole.supporttools.Node.NodeProperty;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Select;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class NodeBrowserQueryPage extends AdminConsolePage<NodeBrowserQueryPage> {
    @Override
    protected String relativePathToURL() {
        return "alfresco/s/admin/admin-nodebrowser";
    }

    @Override
    public String getInfoPage() {
        return "";
    }

    @Override
    public String getIntroPage() {
        return "";
    }

    public enum Store {
        archiveSpaceStore("archive://SpacesStore"),
        systemSystem("system://system"),
        userAlfrescoUserStore("user://alfrescoUserStore"),
        workspaceLightWeightVersionStore("workspace://lightWeightVersionStore"),
        workspaceSpaceStore("workspace://SpacesStore"),
        workspaceVersion2Store("workspace://version2Store");
        private String value;

        private Store(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Actions {
        delete("Delete"),
        forceDelete("Force Delete"),
        takeOwnership("Take Ownership"),
        revertPermissions("Revert Permissions");

        private String action;

        private Actions(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    public enum Query {
        noderef("noderef"), ftsAlfresco("fts-alfresco");
        private String value;

        private Query(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @RenderWebElement
    @FindBy(name = "nodebrowser-store")
    Select selectNode;

    @RenderWebElement
    @FindBy(name = "nodebrowser-search")
    Select selectQuery;

    @RenderWebElement
    @FindBy(id = "query")
    WebElement query;

    @RenderWebElement
    @FindBy(css = "input[value='Execute']")
    WebElement execute;

    @FindBy(css = "input[value='Root List']")
    WebElement rootList;

    @FindBy(css = "a[class='action toggler']")
    WebElement searchAdvancedSettings;

    @FindBy(id = "properties-table")
    Table propertiesTable;

    @FindBy(id = "child-table")
    Table childrenTable;

    @FindBy(id="aspects-table")
    Table aspectsTable;

    public NodeBrowserQueryPage usingStore(Store store) {
        selectNode.selectByValue(store.getValue());
        return this;
    }

    public NodeBrowserQueryPage execute(String query) {
        this.query.clear();
        this.query.sendKeys(query);
        execute.click();
        return this;
    }

    public NodeBrowserQueryPage usingQuery(Query query) {
        selectQuery.selectByValue(query.getValue());
        return this;
    }

    public NodeBrowserQueryPage clickSearchAdvancedSettings() {
        getBrowser().waitUntilElementClickable(searchAdvancedSettings).click();
        return this;
    }

    public List<NodeProperty> getProperties() {
        ArrayList<NodeProperty> properties = new ArrayList<NodeProperty>();
        List<List<WebElement>> rows = propertiesTable.getRows();
        for (List<WebElement> rowInfo : rows) {
            NodeProperty np = new NodeProperty(rowInfo, getBrowser());
            LOG.info("Property: "+np.toString());
            properties.add(np);
        }

        return properties;
    }

    public List<NodeChild> getChildren() {
        ArrayList<NodeChild> child = new ArrayList<NodeChild>();
        List<List<WebElement>> rows = childrenTable.getRows();
        for (List<WebElement> childRow : rows) {
            NodeChild nc = new NodeChild(childRow, getBrowser());
            LOG.info("Child: " + nc.toString());
            child.add(nc);
        }

        return child;
    }

    public void clickRootList()
    {
        getBrowser().waitUntilElementClickable(rootList).click();

    }

    public List<NodeDetails> getAspects() {
        ArrayList<NodeDetails> aspect = new ArrayList<NodeDetails>();
        List<List<WebElement>> rows = aspectsTable.getRows();
        for (List<WebElement> aspectRow : rows) {
            NodeDetails na = new NodeDetails(aspectRow, getBrowser());
            LOG.info("Aspect: " + na.toString());
            aspect.add(na);
        }

        return aspect;
    }
}
