package org.alfresco.po.adminconsole.directories;

import org.alfresco.common.Utils;
import org.alfresco.po.adminconsole.directories.DirectoryManagement.AuthenticationChain;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.alfresco.utility.report.log.Step.STEP;
import static org.alfresco.utility.web.AbstractWebTest.getBrowser;

public class DirectoryManagementPages<T> extends SharePage2<DirectoryManagementPages<T>> {

    private By nameField = By.id("dm-name");
    private By availableTypes = By.id("dm-type");
    private By synchronizationSettingsButton = By.cssSelector("input[value='Synchronization Settings']");
    private By authenticationTable = By.id("dm-authtable");

    public DirectoryManagementPages(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/s/enterprise/admin/admin-directorymanagement";
    }

    public DirectoryManagementPages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public void setName(String name)
    {
        Utils.clearAndType(findElement(nameField), name);
    }

    public void clickSynchronizationSettings()
    {
        findElement(synchronizationSettingsButton).click();
    }

    public List<AuthenticationChain> getAuthenticationDetails()
    {
        STEP("Get authentication details.");
        ArrayList<AuthenticationChain> row = new ArrayList<>();
        List<List<WebElement>> rows = Collections.singletonList(findElements(authenticationTable));
        for (List<WebElement> details : rows)
        {
            AuthenticationChain authDetail = new AuthenticationChain(details, getBrowser());
            row.add(authDetail);
        }
        return row;
    }
}
