package org.alfresco.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

public class TelnetClientUtil
{
    public static TelnetClient telnet = new TelnetClient();
    private static String host;
    private static int port;

    public TelnetClientUtil(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public static void connect() throws SocketException
    {

        try
        {
            telnet.connect(host, port);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void sendCommads(String commands) throws IOException
    {

        telnet.getInputStream().mark(commands.getBytes().length);
        InputStream stream = new ByteArrayInputStream((commands).getBytes());
        IOUtil.readWrite(telnet.getInputStream(), telnet.getOutputStream(),
            stream, System.out);
        telnet.getInputStream().reset();
        telnet.getOutputStream().flush();
        System.out.flush();
    }

    public static void disconnect()
    {
        try
        {
            telnet.disconnect();
        } catch (IOException e)
        {
            e.printStackTrace();
//            System.exit(1);
        }
    }
}
