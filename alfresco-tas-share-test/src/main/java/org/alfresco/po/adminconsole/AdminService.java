package org.alfresco.po.adminconsole;

import java.io.IOException;

import org.alfresco.dataprep.AlfrescoHttpClient;
import org.alfresco.dataprep.UserService;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.stereotype.Service;

@Service
public class AdminService extends UserService
{

    private static Log logger = LogFactory.getLog(AdminService.class);

    /**
     * Login in alfresco share
     *
     * @param userName login user name
     * @param userPass login user password
     * @return true for successful user login
     */
    @Override
    public HttpState login(final String userName,
                           final String userPass)
    {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPass))
        {
            throw new IllegalArgumentException("Parameter missing");
        }
        AlfrescoHttpClient client = alfrescoHttpClientFactory.getObject();
        HttpState state = null;
        org.apache.commons.httpclient.HttpClient theClient = new org.apache.commons.httpclient.HttpClient();
        String reqURL = client.getAlfrescoUrl() + "share/page/dologin";
        org.apache.commons.httpclient.methods.PostMethod post = new org.apache.commons.httpclient.methods.PostMethod(reqURL);
        NameValuePair[] formParams;
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setCookieStore(cookieStore);
        formParams = (new NameValuePair[] {
            new NameValuePair("username", userName),
            new NameValuePair("password", userPass),
            new NameValuePair("success", "/share/page/user/" + userName + "/dashboard"),
            new NameValuePair("failure", "/share/page/type/login?error=true") });
        post.setRequestBody(formParams);
        try
        {
            int postStatus = theClient.executeMethod(post);
            if (302 == postStatus)
            {
                state = theClient.getState();
                post.releaseConnection();
                org.apache.commons.httpclient.methods.GetMethod get = new org.apache.commons.httpclient.methods.GetMethod(
                    client.getAlfrescoUrl() + "s/enterprise/admin/admin-systemsummary");
                theClient.setState(state);
                theClient.executeMethod(get);
                get.releaseConnection();
            }
        } catch (IOException e)
        {
            throw new RuntimeException("Failed to execute the request");
        }
        return state;
    }
}
