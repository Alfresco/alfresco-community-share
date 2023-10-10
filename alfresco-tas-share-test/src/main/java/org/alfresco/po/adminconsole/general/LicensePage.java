package org.alfresco.po.adminconsole.general;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class LicensePage extends AdminConsolePage<LicensePage>
{
    public static final String LICENSE_OBJECT = "Alfresco:Name=License";
    @RenderWebElement
    @FindBy (className = "intro-tall")
    WebElement intro;
    @RenderWebElement
    @FindBy (id = "upload-license")
    Button uploadLicenseButton;
    @RenderWebElement
    @FindBy (id = "apply-new-license")
    Button applyNewLicenseButton;
    @FindBy (css = ".info>a")
    Link uploadingNewLicense;
    @Autowired
    private UploadNewLicenseDialog uploadNewLicenseDialog;
    @Autowired
    private ApplyNewLicenseDialog applyNewLicenseDialog;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-license";
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

    public UploadNewLicenseDialog clickUploadLicense()
    {
        uploadLicenseButton.click();
//        return (UploadNewLicenseDialog) uploadNewLicenseDialog.renderedPage();
        return uploadNewLicenseDialog;
    }

    public ApplyNewLicenseDialog clickApplyNewLicense()
    {
        applyNewLicenseButton.click();
        return (ApplyNewLicenseDialog) applyNewLicenseDialog.renderedPage();
    }

    public void clickUploadingNewLicense()
    {
        uploadingNewLicense.click();
    }

    public enum LicenseFields
    {
        license("License:", "Subject"),
        licenseType("License Type:", "LicenseMode"),
        issued("Issued:", "Issued"),
        issuer("Issuer:", "Issuer"),
        licenseHolder("License Holder:", "Holder"),
        days("Days:", "Days"),
        validUntil("Valid Until:", "ValidUntil"),
        maxUsers("Max Users:", "MaxUsers"),
        remainingDays("Remaining Days:", "RemainingDays"),
        maxContentObjects("Max Content Objects:", "MaxDocs"),
        users("Users:", "CurrentUsers"),
        contentItems("Content Items:", "CurrentDocs"),
        heartBeat("Heart Beat:", "HeartBeatDisabled"),
        cloudSync("Cloud Sync:", "CloudSyncKeyAvailable"),
        clusteringPermitted("Clustering Permitted:", "ClusterEnabled"),
        encryptingPermitted("Encrypting Permitted:", "CryptodocEnabled");

        private String label, jmxAttribute;

        LicenseFields(String label, String jmxAttribute)
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