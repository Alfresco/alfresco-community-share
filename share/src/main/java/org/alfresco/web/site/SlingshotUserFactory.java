/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.alfresco.web.site.servlet.SlingshotLoginController;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AlfrescoUser;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.extensions.webscripts.json.JSONWriter;

/**
 * Slingshot User Factory makes use of the slingshot REST API to persist
 * modified user details back to the repo.
 *
 * @author kevinr
 */
public class SlingshotUserFactory extends AlfrescoUserFactory
{
    public static final String ALF_USER_LOADED = "alfUserLoaded";
    public static final String ALF_USER_GROUPS = "alfUserGroups";

    // Alfresco 3.4 user status properties
    public static final String CM_USERSTATUS = "{http://www.alfresco.org/model/content/1.0}userStatus";
    public static final String CM_USERSTATUSTIME = "{http://www.alfresco.org/model/content/1.0}userStatusTime";
    public static final String CM_USERHOME = "{http://www.alfresco.org/model/content/1.0}homeFolder";
    public static final String PROP_USERSTATUS = "userStatus";
    public static final String PROP_USERSTATUSTIME = "userStatusTime";
    public static final String PROP_USERHOME = "userHome";

    public static final String CM_PREFERENCEVALUES = "{http://www.alfresco.org/model/content/1.0}preferenceValues";
    public static final String PROP_USERHOMEPAGE = "userHomePage";
    public static final String PREFERENCE_USERHOMEPAGE = "org.alfresco.share.user.homePage";

    public static final String ACTIVITI_ADMIN_ENDPOINT_ID = "activiti-admin";

    /**
     * @see org.springframework.extensions.surf.UserFactory#authenticate(jakarta.servlet.http.HttpServletRequest, String, String)
     */
    @Override
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        // special case to disallow Guest user authentication in Share
        // TODO: add basic Guest user support
        boolean authenticated = false;
        if (!AuthenticationUtil.isGuest(username))
        {
            authenticated = super.authenticate(request, username, password);

            if (authenticated)
            {
                // Add activiti-admin credentials to the vault as well.
                CredentialVault vault = frameworkUtils.getCredentialVault(request.getSession(), username);
                Credentials credentials = vault.newCredentials(ACTIVITI_ADMIN_ENDPOINT_ID);
                credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
            }
        }
        return authenticated;
    }

    @Override
    protected AlfrescoUser constructUser(JSONObject properties, Map<String, Boolean> capabilities,
            Map<String, Boolean> immutability) throws JSONException
    {
        AlfrescoUser user = new SlingshotUser(properties.getString(CM_USERNAME), capabilities, immutability);
        user.setProperty(PROP_USERSTATUS, properties.has(CM_USERSTATUS) ? properties.getString(CM_USERSTATUS) : null);
        user.setProperty(PROP_USERSTATUSTIME, properties.has(CM_USERSTATUSTIME) ? properties.getString(CM_USERSTATUSTIME) : null);
        user.setProperty(PROP_USERHOME, properties.has(CM_USERHOME) ? properties.getString(CM_USERHOME) : null);

        if (properties.has(CM_PREFERENCEVALUES))
        {
            String preferenceValues = properties.getString(CM_PREFERENCEVALUES);
            if (preferenceValues.trim().length() != 0)
            {
                try
                {
                    JSONObject preferences = new JSONObject(preferenceValues);
                    String defaultPage = preferences.getString(PREFERENCE_USERHOMEPAGE);
                    if (defaultPage != null && !defaultPage.trim().equals(""))
                    {
                        user.setProperty(PROP_USERHOMEPAGE, defaultPage);
                    }
                }
                catch (JSONException e)
                {
                    // No default page set, that's fine
                }
            }
        }

        return user;
    }

    @Override
    public User loadUser(RequestContext context, String userId, String endpointId) throws UserFactoryException
    {
        User user = super.loadUser(context, userId, endpointId);

        // set a value indicating time the user was constructed
        user.setProperty(ALF_USER_LOADED, new Date().getTime());

        // When a user logs in the SlingshotLoginController will store the groups that they are a member of as a comma delimited string
        // This string needs to be retrieved from the HttpSession and set as a property on the User object.
        // The property can then be easily used in WebScripts using user.properties["alfUserGroups"].split(,) to get an array of the groups
        // that the user is a member of...
        HttpSession session = ServletUtil.getSession(false);
        if (session != null)
        {
            String groups = (String) session.getAttribute(SlingshotLoginController.SESSION_ATTRIBUTE_KEY_USER_GROUPS);
            if (groups != null)
            {
                user.setProperty(ALF_USER_GROUPS, groups);
            }
        }

        return user;
    }

    /**
     * Gets the home page for the given user, for example:
     * <code>/page/site/swsdp/documentlibrary</code>
     * <p>
     * The default value if none has been set is the user's dashboard:
     * <code>/page/user/{username}/dashboard</code>
     *
     * @param context
     * @param userId
     * @return the user's home page
     * @throws UserFactoryException
     */
    public String getUserHomePage(RequestContext context, String userId) throws UserFactoryException
    {
        String homePage = "/page/user/" + URLEncoder.encode(userId) + "/dashboard";
        User user = context.getUser();
        if (user != null)
        {
            String userHomePage = (String) user.getProperty(PROP_USERHOMEPAGE);
            if (userHomePage != null && !userHomePage.trim().equals(""))
            {
                homePage = userHomePage;
            }
        }
        return homePage;
    }

    /**
     * Persist the user back to the Alfresco repository
     *
     * @param user  to persist
     *
     * @throws UserFactoryException
     */
    public void saveUser(AlfrescoUser user) throws UserFactoryException
    {
        RequestContext context = (RequestContext)ThreadLocalRequestContext.getRequestContext();
        if (!context.getUserId().equals(user.getId()))
        {
            throw new UserFactoryException("Unable to persist user with different Id that current Id.");
        }

        StringBuilderWriter buf = new StringBuilderWriter(512);
        JSONWriter writer = new JSONWriter(buf);

        try
        {
            writer.startObject();

            writer.writeValue("username", user.getId());

            writer.startValue("properties");
            writer.startObject();
            writer.writeValue(CM_FIRSTNAME, user.getFirstName());
            writer.writeValue(CM_LASTNAME, user.getLastName());
            writer.writeValue(CM_JOBTITLE, user.getJobTitle());
            writer.writeValue(CM_ORGANIZATION, user.getOrganization());
            writer.writeValue(CM_LOCATION, user.getLocation());
            writer.writeValue(CM_EMAIL, user.getEmail());
            writer.writeValue(CM_TELEPHONE, user.getTelephone());
            writer.writeValue(CM_MOBILE, user.getMobilePhone());
            writer.writeValue(CM_SKYPE, user.getSkype());
            writer.writeValue(CM_INSTANTMSG, user.getInstantMsg());
            writer.writeValue(CM_GOOGLEUSERNAME, user.getGoogleUsername());
            writer.writeValue(CM_COMPANYADDRESS1, user.getCompanyAddress1());
            writer.writeValue(CM_COMPANYADDRESS2, user.getCompanyAddress2());
            writer.writeValue(CM_COMPANYADDRESS3, user.getCompanyAddress3());
            writer.writeValue(CM_COMPANYPOSTCODE, user.getCompanyPostcode());
            writer.writeValue(CM_COMPANYFAX, user.getCompanyFax());
            writer.writeValue(CM_COMPANYEMAIL, user.getCompanyEmail());
            writer.writeValue(CM_COMPANYTELEPHONE, user.getCompanyTelephone());
            writer.endObject();
            writer.endValue();

            writer.startValue("content");
            writer.startObject();
            writer.writeValue(CM_PERSONDESCRIPTION, user.getBiography());
            writer.endObject();
            writer.endValue();

            writer.endObject();

            Connector conn = FrameworkUtil.getConnector(context, ALFRESCO_ENDPOINT_ID);
            ConnectorContext c = new ConnectorContext(HttpMethod.POST);
            c.setContentType("application/json");
            Response res = conn.call("/slingshot/profile/userprofile", c,
                    new ByteArrayInputStream(buf.toString().getBytes()));
            if (Status.STATUS_OK != res.getStatus().getCode())
            {
                throw new UserFactoryException("Remote error during User save: " + res.getStatus().getMessage());
            }
        }
        catch (IOException ioErr)
        {
            throw new UserFactoryException("IO error during User save: " + ioErr.getMessage(), ioErr);
        }
        catch (ConnectorServiceException cse)
        {
            throw new UserFactoryException("Configuration error during User save: " + cse.getMessage(), cse);
        }
    }
}
