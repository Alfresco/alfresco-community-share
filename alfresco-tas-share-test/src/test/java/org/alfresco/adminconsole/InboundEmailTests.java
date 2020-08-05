package org.alfresco.adminconsole;

import org.alfresco.common.TelnetClientUtil;
import org.alfresco.dataprep.CMISUtil.DocumentAspect;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.adminconsole.emailservices.InboundEmailPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.network.JmxBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class InboundEmailTests extends ContextAwareWebTest
{

    @Autowired
    InboundEmailPage inboundEmailPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    JmxBuilder jmxBuilder;

    final private static String jmxObject = "Alfresco:Category=email,Type=Configuration,id1=inbound";

    private String userName = String.format("UserC231499%s", RandomData.getRandomAlphanumeric());
    private String userEmail = userName + "@alfresco.com";
    private String siteName = String.format("testSite%s", RandomData.getRandomAlphanumeric());
    private String siteDescription = String.format("description%s", RandomData.getRandomAlphanumeric());
    private String folderName = "emailTestFolder";
    private String emailAlias = String.format("emailTest%s", RandomData.getRandomAlphanumeric());
    private String emailSubject = String.format("TestingInbound%s", RandomData.getRandomAlphanumeric());


    @BeforeClass
    public void setupTest()
    {
        inboundEmailPage.setBrowser(getBrowser());
    }

    @AfterClass
    public void cleanupTest()
    {
        siteService.delete(adminUser, adminPassword, siteName);
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail (id = "C231484")
    @Test (groups = { TestGroup.SANITY })
    public void viewEmailInboundSettings() throws Exception
    {
        inboundEmailPage.navigate();
        //Verifying if the displayed values match the ones read with JMX
        LOG.info("STEP 4: Verify default information displayed on the page");
        Assert.assertEquals(inboundEmailPage.getEnabled(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.enabled"));
        Assert.assertEquals(inboundEmailPage.getUnknownUser(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.inbound.unknownUser"));
        Assert.assertEquals(inboundEmailPage.getAllowedSenders(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.allowed.senders"));
        Assert.assertEquals(inboundEmailPage.getOverwriteDuplicates(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.handler.folder.overwriteDuplicates"));
        Assert.assertEquals(inboundEmailPage.getMaximumServerConnections(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.connections.max"));
        Assert.assertEquals(inboundEmailPage.getEmailServerDomain(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.domain"));
        Assert.assertEquals(inboundEmailPage.getBlockedSenders(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.blocked.senders"));
        Assert.assertEquals(inboundEmailPage.getEmailAuthenticationGroup(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.inbound.emailContributorsAuthority"));
        Assert.assertEquals(inboundEmailPage.getSmtpAuthenticationEnabled(), "false");
        Assert.assertEquals(inboundEmailPage.getSmtpAuthenticationEnabled(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.auth.enabled"));
        Assert.assertEquals(inboundEmailPage.getEmailServerPort(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.port"));
        Assert.assertEquals((inboundEmailPage.getTlsSupport().equals("Enabled")) ? "true" : false, jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.enableTLS"));
    }

    @TestRail (id = "C231492")
    @Test (groups = { TestGroup.SANITY })
    public void editEmailInboundSettingsAllowedSenders() throws Exception
    {
        inboundEmailPage.navigate();
        LOG.info("STEP 4: Verify Allowed Senders field data on the Inbound Email page.");
        String defaultAllowedSender = ".*";

        Assert.assertEquals(inboundEmailPage.getAllowedSenders(), defaultAllowedSender);

        LOG.info("STEP 5: Enter any correct value into the Allowed Senders field and click on Save button.");
        String random = RandomData.getRandomAlphanumeric();
        inboundEmailPage.setAllowedSenders(random);
        inboundEmailPage.clickSave();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."));

        LOG.info("STEP 6: Verify that the changes are applied.");
        Assert.assertEquals(inboundEmailPage.getAllowedSenders(), random);
        Assert.assertEquals(inboundEmailPage.getAllowedSenders(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.allowed.senders"));

        LOG.info("Reset Allowed Senders to default value and click Save button");
        inboundEmailPage.setAllowedSenders(defaultAllowedSender);
        inboundEmailPage.clickSave();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."));

        LOG.info("Check that Allowed Senders is .*");
        Assert.assertEquals(inboundEmailPage.getAllowedSenders(), defaultAllowedSender);
    }

    @TestRail (id = "C231487")
    @Test (groups = { TestGroup.SANITY })
    public void editEmailInboundSettingsEnable() throws Exception
    {
        inboundEmailPage.navigate();
        LOG.info("STEP 4: Uncheck Enabled and click Save button");
        if (inboundEmailPage.getEnabled().equals("true"))
        {
            inboundEmailPage.clickEnabled();
        }
        inboundEmailPage.clickSave();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."));

        LOG.info("STEP 5: Check that Enabled is false");
        Assert.assertEquals(inboundEmailPage.getEnabled(), "false");

        LOG.info("Reset Enabled to true and click Save button");
        inboundEmailPage.clickEnabled();
        inboundEmailPage.clickSave();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."));

        LOG.info("Check that Enabled is true");
        Assert.assertEquals(inboundEmailPage.getEnabled(), "true");
        Assert.assertEquals(inboundEmailPage.getEnabled(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.enabled"));
    }

    @TestRail (id = "C231491")
    @Test (groups = { TestGroup.SANITY })
    public void editEmailInboundSettingsEmailServerDomain() throws Exception
    {
        inboundEmailPage.navigate();
        String newEmailServerDomain = "qalab.alfresco.org";
        String defaultEmailServerDomain = "alfresco.com";

        LOG.info("STEP 4: Check default value for Email Server Domain");
        Assert.assertEquals(inboundEmailPage.getEmailServerDomain(), defaultEmailServerDomain);

        LOG.info("STEP 5: Enter any other value into the Email Server Domain field and click on Save button");
        inboundEmailPage.setEmailServerDomain(newEmailServerDomain);
        inboundEmailPage.clickSave();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."));

        LOG.info("STEP 6: Check that the new value of Email Server Domain is set");
        Assert.assertEquals(inboundEmailPage.getEmailServerDomain(), newEmailServerDomain);
        Assert.assertEquals(inboundEmailPage.getEmailServerDomain(), jmxBuilder.getJmxClient().readProperty(jmxObject, "email.server.domain"));

        LOG.info("Reset Email Server Domain to the default value");
        inboundEmailPage.setEmailServerDomain(defaultEmailServerDomain);
        inboundEmailPage.clickSave();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."));

        LOG.info("Check that the default value is set");
        Assert.assertEquals(inboundEmailPage.getEmailServerDomain(), defaultEmailServerDomain);
    }

    @TestRail (id = "C231499")
    @Test (groups = { TestGroup.SANITY })
    public void editInboundEmailService() throws Exception
    {
        final String ENTER = "\n";


        LOG.info("PRECONDITION 1: Inbound userEmail is disabled");
        if (inboundEmailPage.getEnabled().equals("true"))
        {
            inboundEmailPage.clickEnabled();
        }
        inboundEmailPage.clickSaveButton();

        LOG.info("PRECONDITION 2: Any user is created (with any valid userEmail) and added to EMAIL_CONTRIBUTORS group");
        userService.create(adminUser, adminPassword, userName, "password", userEmail, userName + "FirstName", userName + "LastName");
        groupService.addUserToGroup(adminUser, adminPassword, "EMAIL_CONTRIBUTORS", userName);

        LOG.info("PRECONDITION 3: Any folder is created (e.g. emailtest) and at least contributors permissions are granted to the user");
        siteService.create(adminUser, adminPassword, domain, siteName, siteDescription, SiteService.Visibility.PUBLIC);
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        contentAction.setPermissionForGroup(adminUser, adminPassword, siteName, folderName, "EMAIL_CONTRIBUTORS", "Contributor", true);
        contentAspects.addAspect(adminUser, adminPassword, siteName, folderName, DocumentAspect.ALIASABLE_EMAIL);
        contentAspects.addEmailAlias(adminUser, adminPassword, siteName, folderName, emailAlias);

        LOG.info("STEP 1: Check Enabled checkbox and click on Save button");
        inboundEmailPage.navigate();
        inboundEmailPage.clickEnabled();
        inboundEmailPage.clickSaveButton();
        Assert.assertTrue(inboundEmailPage.getMessage().contains("Successfully saved values."), "There has been an error while saving the settings");

        LOG.info("STEP 2:  Verify JMX");
        Assert.assertEquals(jmxBuilder.getJmxClient().readProperty(jmxObject, "email.inbound.enabled"), "true", "Inbound email service is not enabled");
        LOG.info("STEP 3: Send email to the following receiver: " + emailAlias + "@" + properties.getAlfrescoServer());
        TelnetClientUtil tn = new TelnetClientUtil(properties.getAlfrescoServer(), properties.getMailPort());
        tn.connect();

        String commands =
            "mail from:" + userEmail + ENTER +
                "rcpt to:" + emailAlias + "@" + properties.getAlfrescoServer() + ENTER +
                "Data" + ENTER +
                "Subject:" + emailSubject + ENTER + ENTER
                + "this is an email sent by " + userEmail + " to test inbound properties" + ENTER + ENTER
                + "." + ENTER + ENTER;
        tn.sendCommads(commands);

        LOG.info("STEP 4: Log in to Alfresco. Verify the '" + folderName + "' folder");
        setupAuthenticatedSession(adminUser, adminPassword);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(emailSubject), "Email was not sent");

    }
}
