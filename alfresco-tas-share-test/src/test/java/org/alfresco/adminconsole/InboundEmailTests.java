package org.alfresco.adminconsole;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.GroupService;
import org.alfresco.po.adminconsole.emailservices.InboundEmailPages;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.network.JmxBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class InboundEmailTests extends BaseTest
{
    private InboundEmailPages inboundEmailPages;
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    GroupService groupService;
    @Autowired
    JmxBuilder jmxBuilder;

    final private static String jmxObject = "Alfresco:Category=email,Type=Configuration,id1=inbound";
    private final ThreadLocal<UserModel> userName = new ThreadLocal<>();
    private String userEmail = userName + "@alfresco.com";
    private String folderName = "emailTestFolder";
    private String emailSubject = String.format("TestingInbound%s", RandomData.getRandomAlphanumeric());


    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        inboundEmailPages = new InboundEmailPages(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        inboundEmailPages.navigate();
    }

    @TestRail (id = "C231484")
    @Test(groups = { TestGroup.SANITY })
    public void viewEmailInboundSettings() {
        log.info("STEP 4: Verify default information displayed on the page");
        Assert.assertEquals(inboundEmailPages.getAllowedSenders(), ".*");
        Assert.assertEquals(inboundEmailPages.getUnknownUser(), "anonymous");
        Assert.assertEquals(inboundEmailPages.getMaximumServerConnections(), "3");
        Assert.assertEquals(inboundEmailPages.getEmailServerDomain(), "example.com");
        Assert.assertEquals(inboundEmailPages.getBlockedSenders(), "");
        Assert.assertEquals(inboundEmailPages.getEmailAuthenticationGroup(), "EMAIL_CONTRIBUTORS");
        Assert.assertEquals(inboundEmailPages.getEmailServerPort(), "25");
    }

    @TestRail (id = "C231492")
    @Test (groups = { TestGroup.SANITY })
    public void editEmailInboundSettingsAllowedSenders() {
        log.info("STEP 4: Verify Allowed Senders field data on the Inbound Email page.");
        String defaultAllowedSender = ".*";

        Assert.assertEquals(inboundEmailPages.getAllowedSenders(), defaultAllowedSender);

        log.info("STEP 5: Enter any correct value into the Allowed Senders field and click on Save button.");
        String random = RandomData.getRandomAlphanumeric();
        inboundEmailPages.setAllowedSenders(random);
        inboundEmailPages.clickSave();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."));

        log.info("STEP 6: Verify that the changes are applied.");
        Assert.assertEquals(inboundEmailPages.getAllowedSenders(), random);

        log.info("Reset Allowed Senders to default value and click Save button");
        inboundEmailPages.setAllowedSenders(defaultAllowedSender);
        inboundEmailPages.clickSave();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."));

        log.info("Check that Allowed Senders is .*");
        Assert.assertEquals(inboundEmailPages.getAllowedSenders(), defaultAllowedSender);
    }

    @TestRail (id = "C231487")
    @Test (groups = { TestGroup.SANITY })
    public void editEmailInboundSettingsEnable() {
        log.info("STEP 4: Uncheck Enabled and click Save button");
        if (inboundEmailPages.getEnabled().equals("true"))
        {
            inboundEmailPages.clickEnabled();
        }
        inboundEmailPages.clickSave();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."));

        log.info("STEP 5: Check that Enabled is false");
        Assert.assertEquals(inboundEmailPages.getEnabled(), "false");

        log.info("Reset Enabled to true and click Save button");
        inboundEmailPages.clickEnabled();
        inboundEmailPages.clickSave();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."));

        log.info("Check that Enabled is true");
        Assert.assertEquals(inboundEmailPages.getEnabled(), "true");
    }

    @TestRail (id = "C231491")
    @Test (groups = { TestGroup.SANITY })
    public void editEmailInboundSettingsEmailServerDomain() {
        String newEmailServerDomain = "qalab.alfresco.org";
        String defaultEmailServerDomain = "example.com";

        log.info("STEP 4: Check default value for Email Server Domain");
        Assert.assertEquals(inboundEmailPages.getEmailServerDomain(), defaultEmailServerDomain);

        log.info("STEP 5: Enter any other value into the Email Server Domain field and click on Save button");
        inboundEmailPages.setEmailServerDomain(newEmailServerDomain);
        inboundEmailPages.clickSave();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."));

        log.info("STEP 6: Check that the new value of Email Server Domain is set");
        Assert.assertEquals(inboundEmailPages.getEmailServerDomain(), newEmailServerDomain);

        log.info("Reset Email Server Domain to the default value");
        inboundEmailPages.setEmailServerDomain(defaultEmailServerDomain);
        inboundEmailPages.clickSave();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."));

        log.info("Check that the default value is set");
        Assert.assertEquals(inboundEmailPages.getEmailServerDomain(), defaultEmailServerDomain);
    }

    @TestRail (id = "C231499")
    @Test (groups = { TestGroup.SANITY }, enabled = false)
    public void editInboundEmailService() throws Exception
    {
        final String ENTER = "\n";
        log.info("PRECONDITION 1: Inbound userEmail is disabled");
        if (inboundEmailPages.getEnabled().equals("true"))
        {
            inboundEmailPages.clickEnabled();
        }
        inboundEmailPages.clickSaveButton();

        log.info("STEP 1: Check Enabled checkbox and click on Save button");
        inboundEmailPages.navigate();
        inboundEmailPages.clickEnabled();
        inboundEmailPages.clickSaveButton();
        Assert.assertTrue(inboundEmailPages.getMessage().contains("Successfully saved values."), "There has been an error while saving the settings");

        log.info("STEP 2:  Verify JMX");
        Assert.assertEquals(jmxBuilder.getJmxClient().readProperty(jmxObject, "email.inbound.enabled"), "true", "Inbound email service is not enabled");


        String commands =
            "mail from:" + userEmail + ENTER +
                "Data" + ENTER +
                "Subject:" + emailSubject + ENTER + ENTER
                + "this is an email sent by " + userEmail + " to test inbound properties" + ENTER + ENTER
                + "." + ENTER + ENTER;

        log.info("STEP 4: Log in to Alfresco. Verify the '" + folderName + "' folder");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(emailSubject), "Email was not sent");

    }
}
