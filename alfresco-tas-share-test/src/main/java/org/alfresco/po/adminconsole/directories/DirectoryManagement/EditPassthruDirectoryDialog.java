package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Razvan Dorobantu on 8/30/2017.
 */
@PageObject
public class EditPassthruDirectoryDialog extends EditDirectoryDialog
{
    public static final String PASSTHRU_AUTHENTICATION_OBJECT = "Alfresco:Category=Authentication,Type=Configuration,id1=managed,id2=passthru1";

    public enum PASSTHRUFields
    {
        useLocalServer("Use Local Server:", "passthru.authentication.useLocalServer"),
        allowGuestLogin("Allow Guest Login:", "passthru.authentication.guestAccess"),
        authenticateFTP("Authenticate FTP:", "passthru.authentication.authenticateFTP"),
        authenticateDomain("Authenticate Domain:", "passthru.authentication.domain"),
        authenticateProtocolOrder("Authenticate Protocol Order:", "passthru.authentication.protocolOrder"),
        mapUnknownUserToGuest("Map Unknown User to Guest:", "ntlm.authentication.mapUnknownUserToGuest"),
        administratorUserNames("Administrator User Names:", "passthru.authentication.defaultAdministratorUserNames"),
        authenticateServers("Authenticate Servers:", "passthru.authentication.servers"),
        connectionTimeout("Connection Timeout:", "passthru.authentication.connectTimeout"),
        offlineCheckInterval("Offline Check Interval:", "passthru.authentication.offlineCheckInterval");

        private String label, jmxAttribute;

        PASSTHRUFields(String label, String jmxAttribute)
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
