package org.alfresco.ftp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.SocketException;

import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FTPTests extends ContextAwareWebTest
{
    int port;
    String server;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        server = properties.getAlfrescoServer();
        port = Integer.parseInt(properties.getFtpPort());
    }

    @Test (groups = { TestGroup.FTP})
    public void nonSecureLogonWhenFTPSIsConfigured()
    {
        FTPClient ftpClient = new FTPClient();
        try
        {
            LOG.info(String.format("Connecting to ftp server %s at port %d", server, port));
            ftpClient.connect(server, port);
            boolean isConnected = ftpClient.isConnected();
            int replyCode = ftpClient.getReplyCode();

            assertTrue(isConnected, "Not connected to the FTPS");
            assertEquals(replyCode, FTPReply.SERVICE_READY, "The replay code after connect is incorrect");

            LOG.info(String.format("Try login with user: %s and password: %s", adminUser, adminPassword));
            boolean success = ftpClient.login(adminUser, adminPassword);
            assertFalse(success, "Could login even if it should'nt");
            replyCode = ftpClient.getReplyCode();
            assertEquals(replyCode, FTPReply.NOT_LOGGED_IN, "The replay code after connect is incorrect");
        } catch (SocketException e)
        {
            LOG.error("Got Socket Exception");
            e.printStackTrace();
        } catch (IOException e)
        {
            LOG.error("Got IOException");
            e.printStackTrace();
        }

    }
}
