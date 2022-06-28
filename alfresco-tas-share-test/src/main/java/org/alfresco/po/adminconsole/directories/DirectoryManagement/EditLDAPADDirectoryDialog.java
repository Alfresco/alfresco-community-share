package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Claudia Agache on 8/29/2017.
 */
@PageObject
public class EditLDAPADDirectoryDialog extends EditDirectoryDialog
{
    public static final String LDAPAD_AUTHENTICATION_OBJECT = "Alfresco:Category=Authentication,Type=Configuration,id1=managed,id2=ldapad1";

    public enum LDAPADFields
    {
        authenticationEnabled("Authentication Enabled:", "ldap.authentication.active"),
        userNameFormat("User Name Format:", "ldap.authentication.userNameFormat"),
        ldapServerUrl("LDAP Server URL:", "ldap.authentication.java.naming.provider.url"),
        security("Security:", "ldap.authentication.java.naming.security.authentication"),
        defaultAdministratorUserNames("Default Administrator User Names:", "ldap.authentication.defaultAdministratorUserNames"),
        authenticateFTP("Authenticate FTP:", "ldap.authentication.authenticateFTP"),
        synchronizationEnabled("Synchronization Enabled:", "ldap.synchronization.active"),
        securityPrincipalName("Security Principal Name:", "ldap.synchronization.java.naming.security.principal"),
        synchronizationSecurity("Security:", "ldap.synchronization.java.naming.security.authentication"),
        groupQuery("Group query:", "ldap.synchronization.groupQuery"),
        groupSearchBase("Group Search Base:", "ldap.synchronization.groupSearchBase"),
        userSearchBase("User Search Base:", "ldap.synchronization.userSearchBase"),
        personQuery("Person Query:", "ldap.synchronization.personQuery"),
        personDifferentialQuery("Person Differential Query:", "ldap.synchronization.personDifferentialQuery");

        private String label, jmxAttribute;

        LDAPADFields(String label, String jmxAttribute)
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
