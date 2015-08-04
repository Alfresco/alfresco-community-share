/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.steps;

/**
 * Test Class to test SiteActions > utils
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.ACTION;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.DESTINATION;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class SiteActionsTest extends AbstractTest
{
    private SiteActions siteActions = new SiteActions();
    private String  siteName = "swsdp";
    private String newSite = "site" + System.currentTimeMillis();
    private String[] hyerarcy = new String[]{"parent", "childA", "childB"};

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs(username, password);
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testopenSiteDashBoard() throws Exception
    {
            SiteDashboardPage siteDashPage = siteActions.openSiteDashboard(drone, siteName);
            Assert.assertNotNull(siteDashPage);
    }
    
    @Test(groups = "Enterprise-only", priority=2)
    public void testopenSitesContentLibrary() throws Exception
    {
            DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(drone, siteName);
            Assert.assertNotNull(docLibPage);
    }
    
    @Test(groups = "Enterprise-only", priority=3)
    public void testCreateSite() throws Exception
    {
            siteActions.createSite(drone, newSite, newSite, "Public");
            DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(drone, newSite);
            Assert.assertNotNull(docLibPage);
    }
    
    @Test(groups = "Enterprise-only", priority=4)
    public void testAddRemoveAspect() throws Exception
    {
        File file = SiteUtil.prepareFile();
        DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(drone, siteName);
        docLibPage = siteActions.uploadFile(drone, file).render();
        docLibPage.selectFile(file.getName()).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(DocumentAspect.VERSIONABLE.getValue());
        siteActions.addAspects(drone, aspects);
        
        SelectAspectsPage aspectsPage = siteActions.getAspectsPage(drone);  
        
        Assert.assertTrue(aspectsPage.isAspectAdded(aspects.get(0)));
        
        aspectsPage.clickCancel().render();        
        
        siteActions.removeAspects(drone, aspects);
        
        aspectsPage = siteActions.getAspectsPage(drone);
        
        Assert.assertTrue(aspectsPage.isAspectAvailable(aspects.get(0)));
        
        aspectsPage.clickCancel().render();
    }
    
    @Test(groups = "Enterprise-only", priority=5)
    public void testViewDetailsForFolder() throws Exception
    {
        String folderName = "folder" + System.currentTimeMillis();
        
        siteActions.openSitesDocumentLibrary(drone, siteName);
        
        siteActions.createFolder(drone, folderName, folderName, folderName);
        
        DetailsPage detailsPage = siteActions.viewDetails(drone, folderName).render();
        
        Assert.assertNotNull(detailsPage, "Error during View Folder Details");
        Assert.assertTrue(detailsPage instanceof FolderDetailsPage, "Error during View Folder Details");
        
    }
       
    @Test(groups = "Enterprise-only", priority=6)
    public void testViewDetailsForFile() throws Exception
    {
        File file = SiteUtil.prepareFile();
        siteActions.openSitesDocumentLibrary(drone, siteName);
        siteActions.uploadFile(drone, file).render();

        DetailsPage detailsPage = siteActions.viewDetails(drone, file.getName()).render();

        Assert.assertNotNull(detailsPage, "Error during View Document Details");
        Assert.assertTrue(detailsPage instanceof DocumentDetailsPage, "Error during View Document Details");

    }
    
    @Test(groups = "Enterprise-only", priority=7)
    public void createHyerarcyFolders() {
	  siteActions.openSitesDocumentLibrary(drone, newSite);
  	  for (int i = 0; i < hyerarcy.length; i++) {
  		  siteActions.createFolder(drone, hyerarcy[i], hyerarcy[i], hyerarcy[i]);
  		  siteActions.navigateToFolder(drone, hyerarcy[i]);
  	  }
    }
    
    @Test(groups = "Enterprise-only", priority=8, dataProvider="tempFilesData")
    public void testCopyArtifact(File file) throws Exception
    {
	  copyOrMoveAction(file,ACTION.COPY, new String[] {});
	  Assert.assertTrue(siteActions.isFileVisible(drone, file.getName()),"File: " + file.getName() + " was copied in document library of site ");  
    }
     
    @Test(groups = "Enterprise-only", priority=9, dataProvider="tempFilesData")
    public void testCopyArtifactInFolderHyerarcy(File file) throws Exception
    {
	  String fullHyerarcy ="";
	  copyOrMoveAction(file, ACTION.COPY, hyerarcy);
	  
	  for (int i = 0; i < hyerarcy.length; i++) {
		  fullHyerarcy += "/" + hyerarcy[i];
		  siteActions.navigateToFolder(drone, hyerarcy[i]);
	  }
	  	  
	  Assert.assertTrue(siteActions.isFileVisible(drone, file.getName()),String.format("File: [%s[ was copied in document library[%s] of site %s ", file.getName(), fullHyerarcy, newSite));  
    }
    
    @Test(groups = "Enterprise-only", priority=10, dataProvider="tempFilesData")
    public void testMoveArtifact(File file) throws Exception
    {
	  copyOrMoveAction(file,ACTION.MOVE, new String[] {});	  
	  Assert.assertTrue(siteActions.isFileVisible(drone, file.getName()),"File: " + file.getName() + " was moved in document library of site ");  
    }
    
    @Test(groups = "Enterprise-only", priority=11, dataProvider="tempFilesData")
    public void testMoveArtifactInFolderHyerarcy(File file) throws Exception
    {
	  String fullHyerarcy ="";
	  copyOrMoveAction(file, ACTION.MOVE, hyerarcy);
	  
	  for (int i = 0; i < hyerarcy.length; i++) {
		  fullHyerarcy += "/" + hyerarcy[i];
		  siteActions.navigateToFolder(drone, hyerarcy[i]);
	  }
	  Assert.assertTrue(siteActions.isFileVisible(drone, file.getName()),String.format("File: [%s] was moved in document library[%s] of site %s ",file.getName(),fullHyerarcy, newSite));  
    }
    
    /**
     * Private method used for copying/moving artifacts between sites.
     * @param file
     * @param action
     */
    private void copyOrMoveAction(File file, ACTION action, String[] structure) {
	  siteActions.openSitesDocumentLibrary(drone, siteName);
	  siteActions.uploadFile(drone, file);
	  siteActions.copyOrMoveArtifact(drone, DESTINATION.ALL_SITES, newSite, file.getName(), action, structure);
	  siteActions.openSitesDocumentLibrary(drone, newSite);
    }
    
    @DataProvider
    public Object[][] tempFilesData() {
		return new Object[][] { {SiteUtil.prepareFile()}, {SiteUtil.prepareFile()}, {SiteUtil.prepareFile()} };  
    }    
}
