package org.alfresco.po.adminconsole.repositoryservices;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ProcessEnginesPage extends AdminConsolePage<ProcessEnginesPage>
{
    public static final String PROCESS_ENGINES_OBJECT = "Alfresco:Name=WorkflowInformation";
    @RenderWebElement
    @FindBy (className = "intro")
    WebElement intro;

    @FindBy (css = ".column-full>a")
    WebElement activitiWorkflowConsole;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-processengines";
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

    public void clickActivitiWorkflowConsole()
    {
        browser.waitUntilElementClickable(activitiWorkflowConsole).click();
    }

    public enum ProcessEnginesFields
    {
        activitiWorkflowEnabled("Activiti Workflow Enabled:", "ActivitiEngineEnabled"),
        processDefinitionsVisible("Process Definitions Visible:", "ActivitiWorkflowDefinitionsVisible"),
        currentlyRunningProcessInstances("Currently Running Process Instances:", "NumberOfActivitiWorkflowInstances"),
        currentlyRunningTaskInstances("Currently Running Task Instances:", "NumberOfActivitiTaskInstances"),
        processDefinitionsDeployed("Process Definitions Deployed:", "NumberOfActivitiWorkflowDefinitionsDeployed");

        private String label, jmxAttribute;

        ProcessEnginesFields(String label, String jmxAttribute)
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
