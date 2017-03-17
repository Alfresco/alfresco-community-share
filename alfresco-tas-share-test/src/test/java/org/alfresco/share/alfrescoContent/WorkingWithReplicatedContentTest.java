package org.alfresco.share.alfrescoContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.admin.CreateEditReplicationJobPage;
import org.alfresco.po.share.user.admin.ReplicationJobsPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class WorkingWithReplicatedContentTest extends ContextAwareWebTest
{
    @Autowired private LoginPage loginPage;

    @Autowired private CreateSiteDialog createSiteDialog;

    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private SiteDashboardPage siteDashboardPage;

    @Autowired private NewContentDialog newContentDialog;

    @Autowired private AdminToolsPage adminToolsPage;

    @Autowired private RepositoryPage repositoryPage;

    @Autowired private EditPropertiesDialog editPropertiesDialog;

    @Autowired private ReplicationJobsPage replicationJobsPage;

    @Autowired private CreateEditReplicationJobPage createEditReplicationJobPage;

    private final String uniqueIdentifier = DataUtil.getUniqueIdentifier();
    private final String site = "site-C7600-" + uniqueIdentifier;
    private final String folder = "C7600-folder";
    private final String document = "C7600-doc";
    private final List<String> explorerPanelPath = new ArrayList<>(
            Arrays.asList("Repository", "Data Dictionary", "Transfers", "Transfer Target Groups", "Default Group"));
    private final String pathForTransferTargetFolder = "Data Dictionary/Transfers/Transfer Target Groups/Default Group";
    private final String transferTargetFolder = "TransferFolder-" + uniqueIdentifier;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        siteService.create(adminUser, adminPassword, domain, site, site, Site.Visibility.PUBLIC);
        contentService.createFolder(adminUser, adminPassword, folder, site);
        contentService.createDocumentInFolder(adminUser, adminPassword, site, folder, CMISUtil.DocumentType.TEXT_PLAIN, document, "");
        contentService.createFolderInRepository(adminUser, adminPassword, transferTargetFolder, pathForTransferTargetFolder);

        LOG.info("ServerB: Any site" + site + " with a folder " + folder + " in it is created on ServerB, " + properties.getShare2Url());
        getBrowser().navigate().to(properties.getShare2Url());
        loginPage.login(adminUser, adminPassword);
        createSiteDialog.navigateByMenuBar();
        createSiteDialog.typeDetails(site, "");
        createSiteDialog.clickSaveButton();
        siteDashboardPage.renderedPage();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        documentLibraryPage.setCurrentSiteName(site);
        getBrowser().navigate().to(properties.getShare2Url().toString().replace("share", "") + documentLibraryPage.getRelativePath());
        documentLibraryPage.clickCreateButton();
        documentLibraryPage.clickFolderLink();
        newContentDialog.fillInNameField(folder);
        newContentDialog.clickSaveButton();
        assertTrue(documentLibraryPage.isContentNameDisplayed(folder), folder + " is displayed in Document Library.");

        LOG.info("ServerA: Navigate to " + explorerPanelPath);
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();
        for (String anExplorerPanelPath : explorerPanelPath)
        {
            repositoryPage.clickFolderFromExplorerPanel(anExplorerPanelPath);
        }
        assertEquals(repositoryPage.getBreadcrumbList(), explorerPanelPath.toString(), "Breadcrumb=");

        LOG.info("Edit properties for " + transferTargetFolder);
        repositoryPage.mouseOverContentItem(transferTargetFolder);
        repositoryPage.clickEditProperties(transferTargetFolder);
        editPropertiesDialog.updateFolderDetailsForReplication(properties.getServer2Url(), properties.getServer2Port(), adminUser, adminPassword);
    }

    @TestRail(id = "C7600")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT}, enabled = false)
    public void verifyContentAfterReplication()
    {
        String name = "C7600-jobName-" + uniqueIdentifier;
        String description = "C7600 Job Description " + uniqueIdentifier;
        ArrayList<String> payloadDestinationPath = new ArrayList<>(Arrays.asList("Sites", site, "documentLibrary", folder));

        adminToolsPage.navigate();

        LOG.info("STEP1: Navigate to 'Admin Tools -> Repository -> Replication jobs'");
        adminToolsPage.navigateToNodeFromToolsPanel(language.translate("adminTools.replicationJobs"), replicationJobsPage);
        assertEquals(adminToolsPage.getCurrentUrl(), properties.getShareUrl() + replicationJobsPage.getRelativePath(), "User is redirected to=");

        LOG.info("STEP2: Click 'Create Job' button");
        replicationJobsPage.clickCreateJobButton();
        assertEquals(createEditReplicationJobPage.getHeaderTitle(), language.translate("adminTools.replicationJobs.create"), "Displayed page=");

        LOG.info("STEP3: Fill in input fields, name and description");
        createEditReplicationJobPage.fillInInputFields(name, description);

        LOG.info("STEP4: At the 'Payload' section, click 'Select' button");
        createEditReplicationJobPage.clickSelectPayloadButton();

        LOG.info("STEP5: Navigate to '" + payloadDestinationPath + "' and click 'Add' icon for" + folder + " .Click 'OK' button");
        createEditReplicationJobPage.selectItemFromDialog(payloadDestinationPath);
        createEditReplicationJobPage.clickPayloadOkButton();
        assertEquals(createEditReplicationJobPage.getSelectedPayload(), folder, "Payload: Souce Items=");

        LOG.info("STEP6: At the 'Transfer Target' section, click 'Select' button");
        createEditReplicationJobPage.clickSelectTransferTargetButton();

        LOG.info("STEP7: Navigate to 'Data Dictionary->Transfers->Transfer Target Groups->Default Group' and click 'Add' button for" + transferTargetFolder
                + " .Click 'OK' button");
        createEditReplicationJobPage.clickAddIconFromList(transferTargetFolder);
        createEditReplicationJobPage.clickTargetOkButton();
        assertEquals(createEditReplicationJobPage.getSelectedTransferTarget(), transferTargetFolder, "Payload: Source Items=");

        LOG.info("STEP8: Check 'Enabled' checkbox under 'Other Options' section");
        createEditReplicationJobPage.clickEnabledCheckbox();
        assertTrue(createEditReplicationJobPage.isEnabledChecked(), "'Enabled' is checked.");

        LOG.info("STEP9: Click 'Create Job' button");
        createEditReplicationJobPage.clickCreateJobButton();
        assertEquals(adminToolsPage.getCurrentUrl(), properties.getShareUrl() + replicationJobsPage.getRelativePath() + "?jobName=" + name,
                "User is redirected to=");
        assertTrue(replicationJobsPage.isJobDisplayedInList(name), name + " job is displayed in 'Jobs' section.");

        LOG.info("STEP10: Click 'Run Job' button");
        replicationJobsPage.clickRunJobButton();
        //getBrowser().waitInSeconds(5);
        assertTrue(replicationJobsPage.getJobStatus().contains("This job is currently running"), name + " status contains: This job is currently running");

        LOG.info("STEP11: Go to ServerB and verify the content for " + folder);
        getBrowser().navigate().to(properties.getShare2Url());
        documentLibraryPage.navigate(site);
        documentLibraryPage.clickFolderFromExplorerPanel(folder);
        assertTrue(documentLibraryPage.isContentNameDisplayed(document), document + " is displayed in " + folder);

        LOG.info("STEP12: Click on \"View in Source Repository\" icon");
    }
}