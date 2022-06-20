package org.alfresco.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.io.Util;

public class IOUtil
{
    public static final void readWrite(final InputStream remoteInput,
                                       final OutputStream remoteOutput,
                                       final InputStream localInput,
                                       final OutputStream localOutput)
    {
        Thread reader, writer;
        reader = new Thread()
        {
            @Override
            public void run()
            {
                int ch;
                try
                {
                    while (!interrupted() && (ch = localInput.read()) != -1)
                    {
                        remoteOutput.write(ch);
                        remoteOutput.flush();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        ;
        writer = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Util.copyStream(remoteInput, localOutput);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        };
        writer.setPriority(Thread.currentThread().getPriority() + 1);
        writer.start();
        reader.setDaemon(true);
        reader.start();
        try
        {
            writer.join();
            reader.interrupt();
        }
        catch (InterruptedException e)
        {
            // Ignored
        }
    }
}
