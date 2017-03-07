package org.alfresco.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paul.Brodner
 */
public class LogFactory
{
    public static Logger getLogger()
    {
        StackTraceElement myCaller = Thread.currentThread().getStackTrace()[2];
        return LoggerFactory.getLogger(myCaller.getClassName());
    }
}
