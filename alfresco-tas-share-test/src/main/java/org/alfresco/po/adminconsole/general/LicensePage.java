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
    @Autowired
    private UploadNewLicenseDialog uploadNewLicenseDialog;

    @Autowired
    private ApplyNewLicenseDialog applyNewLicenseDialog;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/enterprise/admin/admin-license";
    }

    @RenderWebElement
    @FindBy(className = "intro-tall")
    WebElement intro;

    @RenderWebElement
    @FindBy(id = "upload-license")
    Button uploadLicenseButton;

    @RenderWebElement
    @FindBy(id = "apply-new-license")
    Button applyNewLicenseButton;

    @FindBy(css = ".info>a")
    Link uploadingNewLicense;

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
        return (UploadNewLicenseDialog) uploadNewLicenseDialog.renderedPage();
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
}
