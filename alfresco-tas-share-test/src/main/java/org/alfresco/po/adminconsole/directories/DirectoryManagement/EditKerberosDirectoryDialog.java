package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Razvan Dorobantu on 8/30/2017.
 */
@PageObject
public class EditKerberosDirectoryDialog extends EditDirectoryDialog
{
    public static final String KERBEROS_AUTHENTICATION_OBJECT = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=alfrescoKerb1";

    public enum KERBEROSFields
    {
        userConfigEntryName("User Config Entry Name:", "kerberos.authentication.user.configEntryName"),
        cifsConfigEntryName("CIFS Config Entry Name:", "kerberos.authentication.cifs.configEntryName"),
        cifsPassword("CIFS Password:", "kerberos.authentication.cifs.password"),
        administratorUserNames("Administrator User Names:", "kerberos.authentication.defaultAdministratorUserNames"),
        kerberosAuthenticationRealm("Kerberos Authentication Realm:", "kerberos.authentication.realm"),
        httpConfigEntryName("HTTP Config Entry Name:", "kerberos.authentication.http.configEntryName"),
        httpPassword("HTTP Password:", "kerberos.authentication.http.password"),
        stripUsernameSuffix("Strip Username Suffix:", "kerberos.authentication.stripUsernameSuffix"),
        authenticateFTP("Authenticate FTP:", "kerberos.authentication.authenticateFTP");

        private String label, jmxAttribute;

        KERBEROSFields(String label, String jmxAttribute)
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
