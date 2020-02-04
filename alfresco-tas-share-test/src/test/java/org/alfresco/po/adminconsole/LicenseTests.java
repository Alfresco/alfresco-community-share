package org.alfresco.po.adminconsole;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.alfresco.po.adminconsole.general.LicensePage;
import org.alfresco.po.adminconsole.general.UploadNewLicenseDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LicenseTests extends ContextAwareWebTest
{
    @Autowired
    LicensePage licensePage;

    @BeforeClass
    public void setupTest()
    {
        licensePage.setBrowser(getBrowser());
        licensePage.navigate();
    }

    @AfterClass
    public void cleanupTest()
    {
        licensePage.getBrowser().cleanUpAuthenticatedSession();
    }

    //TODO I do believe these tests can be better written

    @TestRail (id = "C231469")
    @Test (groups = { TestGroup.SANITY })
    public void unlimitedLicense()
    {
        String license = LicensePage.LicenseFields.license.getLabel();
        String licenseFolder = "Licenses ";
        String licenseType = "";
        try
        {
            licenseType = licensePage.getFieldValue(license).replaceAll("[^0-9]", "");
            licenseFolder += licenseType;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        String licensePath = testDataFolder + licenseFolder + File.separator + "alf" + licenseType + "-allenabled-till-01-01-20.lic";
        LOG.info("STEP 4: Click Upload license button");
        UploadNewLicenseDialog newLicenseDialog = licensePage.clickUploadLicense();
        newLicenseDialog.setBrowser(getBrowser());
        Assert.assertTrue(newLicenseDialog.isDialogDisplayed());

        LOG.info("STEP 5: Choose file from Alfresco license directory");
        newLicenseDialog.uploadLicense(licensePath);
        String result = newLicenseDialog.getResult();

        Assert.assertEquals(result, "The license was successfully uploaded and applied to the repository.");
        newLicenseDialog.clickClose(licensePage);

        LOG.info("STEP 7: Confirm that license details are correct.");
        String maxUsers = LicensePage.LicenseFields.maxUsers.getLabel();
        String validUntil = LicensePage.LicenseFields.validUntil.getLabel();
        String maxContentObjects = LicensePage.LicenseFields.maxContentObjects.getLabel();
        String contentItems = LicensePage.LicenseFields.contentItems.getLabel();

        List<ControlObject> pageFields = licensePage.getPageFields();

        HashMap<String, String> fieldsValues = new HashMap<>();
        for (ControlObject field : pageFields)
        {
            fieldsValues.put(field.getLabel(), field.getInput().getText());
        }

        Assert.assertEquals(fieldsValues.get(maxUsers), "Unlimited");
        Assert.assertTrue(fieldsValues.get(validUntil).contains("2020"));
        Assert.assertEquals(fieldsValues.get(maxContentObjects), "Unlimited");
        Assert.assertEquals(fieldsValues.get(contentItems), "Unlimited");
    }

    @TestRail (id = "C231474")
    @Test (groups = { TestGroup.SANITY })
    public void restrictedLicense()
    {
        String license = LicensePage.LicenseFields.license.getLabel();
        String licenseFolder = "Licenses ";
        String licenseType = "";
        try
        {
            licenseType = licensePage.getFieldValue(license).replaceAll("[^0-9]", "");
            licenseFolder += licenseType;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        String licensePath = testDataFolder + licenseFolder + File.separator + "alf" + licenseType + "-5user-1000doc-allenabled-till-01-01-20.lic";
        LOG.info("STEP 4: Click Upload license button");
        UploadNewLicenseDialog newLicenseDialog = licensePage.clickUploadLicense();
        newLicenseDialog.setBrowser(getBrowser());
        Assert.assertTrue(newLicenseDialog.isDialogDisplayed());

        LOG.info("STEP 5: Choose file from Alfresco license directory");
        newLicenseDialog.uploadLicense(licensePath);
        String result = newLicenseDialog.getResult();

        Assert.assertEquals(result, "The license was successfully uploaded and applied to the repository.");
        newLicenseDialog.clickClose(licensePage);

        LOG.info("STEP 7: Confirm that license details are correct.");
        String maxUsers = LicensePage.LicenseFields.maxUsers.getLabel();
        String validUntil = LicensePage.LicenseFields.validUntil.getLabel();
        String maxContentObjects = LicensePage.LicenseFields.maxContentObjects.getLabel();

        List<ControlObject> pageFields = licensePage.getPageFields();

        HashMap<String, String> fieldsValues = new HashMap<>();
        for (ControlObject field : pageFields)
        {
            fieldsValues.put(field.getLabel(), field.getInput().getText());
        }

        Assert.assertEquals(fieldsValues.get(maxUsers), "5");
        Assert.assertTrue(fieldsValues.get(validUntil).contains("2020"));

        // the value of Max Content Objects can be "1.000" or "1,000"
        Assert.assertEquals(fieldsValues.get(maxContentObjects).replaceAll("[,.]", ""), "1000");

    }
}
