package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Claudia Agache on 8/29/2017.
 */
@PageObject
public class EditInternalDirectoryDialog extends EditDirectoryDialog
{
    public static final String INTERNAL_AUTHENTICATION_OBJECT = "Alfresco:Category=Authentication,Type=Configuration,id1=managed,id2=alfrescoNtlm1";

    public enum InternalFields
    {
        allowGuestLogin("Allow Guest Login:", "alfresco.authentication.allowGuestLogin"),
        mapUnknownUserToGuest("Map Unknown User to Guest:", "ntlm.authentication.mapUnknownUserToGuest"),
        authenticateFTP("Authenticate FTP:", "alfresco.authentication.authenticateFTP");

        private String label, jmxAttribute;

        InternalFields(String label, String jmxAttribute)
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
