package org.alfresco.po.alfresco.admin.supporttools;

import org.alfresco.po.alfresco.admin.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Select;

@PageObject
public class NodeBrowserQueryPage extends AdminConsolePage<NodeBrowserQueryPage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/admin/admin-nodebrowser";
    }

    @Override
    public String getInfoPage()
    {
        return "";
    }

    @Override
    public String getIntroPage()
    {
        return "";
    }

    public enum Store
    {
        archiveSpaceStore("archive://SpacesStore"),
        systemSystem("system://system"),
        userAlfrescoUserStore("user://alfrescoUserStore"),
        workspaceLightWeightVersionStore("workspace://lightWeightVersionStore"),
        workspaceSpaceStore("workspace://SpacesStore"),
        workspaceVersion2Store("workspace://version2Store");
        private String value;

        private Store(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public enum Query
    {
        noderef("noderef"), ftsAlfresco("fts-alfresco");
        private String value;

        private Query(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
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

    public NodeBrowserQueryPage usingStore(Store store)
    {
        selectNode.selectByValue(store.getValue());
        return this;
    }

    public NodeBrowserQueryPage execute(String query)
    {
        this.query.clear();
        this.query.sendKeys(query);
        execute.click();
        return this;
    }

    public NodeBrowserQueryPage usingQuery(Query query)
    {
        selectQuery.selectByValue(query.getValue());
        return this;
    }

}
