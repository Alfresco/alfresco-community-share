package org.alfresco.adminconsole;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.adminconsole.ControlObject;
import org.alfresco.po.adminconsole.general.LicensePage;
import org.alfresco.po.adminconsole.general.LicensePages;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class LicenseTests extends BaseTest {
    private LicensePages licensePages;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        licensePages = new LicensePages(webDriver);
        licensePages.navigate();
    }

    @TestRail (id = "C231469")
    @Test (groups = { TestGroup.SANITY })
    public void unlimitedLicense()
    {
        String licenseFolder = "Licenses 62";
        String licenseType = "";
        licensePages = new LicensePages(webDriver);
        try
        {
            licenseFolder += licenseType;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        log.info("STEP 1: Click Upload license button");
        licensePages.clickUploadLicense();
        Assert.assertTrue(licensePages.isDialogDisplayed());
        licensePages.clickClose();

        log.info("STEP 2: Confirm that license details are correct.");
        String maxUsers = LicensePage.LicenseFields.maxUsers.getLabel();
        String validUntil = LicensePage.LicenseFields.validUntil.getLabel();
        String maxContentObjects = LicensePage.LicenseFields.maxContentObjects.getLabel();
        String contentItems = LicensePage.LicenseFields.contentItems.getLabel();

        List<ControlObject> pageFields = licensePages.getPageFields();

        HashMap<String, String> fieldsValues = new HashMap<>();
        for (ControlObject field : pageFields)
        {
            fieldsValues.put(field.getLabel(), field.getInput().getText());
        }

        Assert.assertEquals(fieldsValues.get(maxUsers), "Unlimited");
        Assert.assertFalse(fieldsValues.get(validUntil).contains("2020"));
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
            licenseType = licensePages.getFieldValue(license).replaceAll("[^0-9]", "");
            licenseFolder += licenseType;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        log.info("STEP 1: Click Upload license button");
        licensePages.clickUploadLicense();
        Assert.assertTrue(licensePages.isDialogDisplayed());
        licensePages.clickClose();

        log.info("STEP 2: Confirm that license details are correct.");
        String maxUsers = LicensePage.LicenseFields.maxUsers.getLabel();
        String validUntil = LicensePage.LicenseFields.validUntil.getLabel();
        String maxContentObjects = LicensePage.LicenseFields.maxContentObjects.getLabel();

        List<ControlObject> pageFields = licensePages.getPageFields();

        HashMap<String, String> fieldsValues = new HashMap<>();
        for (ControlObject field : pageFields)
        {
            fieldsValues.put(field.getLabel(), field.getInput().getText());
        }

        Assert.assertEquals(fieldsValues.get(maxUsers), "Unlimited");
        Assert.assertFalse(fieldsValues.get(validUntil).contains("2020"));
        Assert.assertEquals(fieldsValues.get(maxContentObjects).replaceAll("[,.]", ""), "Unlimited");
    }
}
