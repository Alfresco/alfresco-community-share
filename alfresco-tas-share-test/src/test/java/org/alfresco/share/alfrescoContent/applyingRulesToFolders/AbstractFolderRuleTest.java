package org.alfresco.share.alfrescoContent.applyingRulesToFolders;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.alfresco.dataprep.AlfrescoHttpClient.ALFRESCO_CSRF_TOKEN;
import static org.alfresco.dataprep.AlfrescoHttpClient.COOKIE;
import static org.alfresco.dataprep.AlfrescoHttpClient.MIME_TYPE_JSON;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *  This class represents an API helper to create a rule which will be applied on a folder.
 */
@Slf4j
public abstract class AbstractFolderRuleTest extends BaseTest
{
    /**
     * Method for create a rule which will be applied from Share interface on a folder
     *
     * @implNote Since this method have too many params, could be transformed into
     * a specific class called <b>RuleModel</b> should be passed as a parameter on createFolderRule method.
     * Please see <b>FolderModel</b>
     *
     * @param user user
     * @param folder folder on which will be the rule applied
     * @param action rule action
     * @param ruleTitle rule title
     * @param ruleDescription rule description
     * @param isDisabled is rule disabled
     * @param isAppliedToChildren is applied to folder children
     * @param isAsync is asynchronously
     * @param ruleType rule type
     */
    public void createFolderRule(
        UserModel user,
        FolderModel folder,
        String action,
        String ruleTitle,
        String ruleDescription,
        boolean isDisabled,
        boolean isAppliedToChildren,
        boolean isAsync,
        String ruleType)
    {
        String url = buildUrl(folder);

        JSONObject requestBody = setRequestBody(
            folder.getNodeRef(),
            action, ruleDescription,
            ruleTitle, isDisabled,
            isAppliedToChildren, isAsync, ruleType);

        StringEntity stringEntity = null;

        try
        {
            stringEntity = new StringEntity(requestBody.toString());
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        HttpPost post = new HttpPost(url);
        post.setEntity(stringEntity);
        setRequestWithCSRFToken(post, getUserService().login(user.getUsername(), user.getPassword()));

        try
        {
            HttpResponse response = getClient().execute(post);
            if(response.getStatusLine().getStatusCode() == SC_CREATED)
            {
                log.info("Rule successfully created {}", ruleTitle);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            post.releaseConnection();
            try {
                getClient().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildUrl(FolderModel folder)
    {
        String apiNode = "/proxy/alfresco/api/node/workspace/SpacesStore/";
        String ruleEndpoint = "/ruleset/rules";
        return defaultProperties.getShareUrl() + apiNode + folder.getNodeRef() + ruleEndpoint;
    }

    private String getCsrfToken(HttpState state)
    {
        Cookie[] cookies = state.getCookies();
        return Arrays.stream(cookies).filter(
                cookie -> cookie.getName().equals(ALFRESCO_CSRF_TOKEN))
            .findFirst().map(NameValuePair::getValue).orElse("");
    }

    private void setRequestWithCSRFToken(HttpRequestBase httpRequestBase, HttpState httpState)
    {
        String token = URLDecoder.decode(getCsrfToken(httpState), UTF_8);
        String cookieValue = "";

        for(Cookie cookie : httpState.getCookies())
        {
            cookieValue = cookieValue
                .concat(cookie.getName())
                .concat("=")
                .concat(cookie.getValue()
                    .concat("; "));
        }
        httpRequestBase.setHeader(ALFRESCO_CSRF_TOKEN, token);
        httpRequestBase.setHeader(COOKIE, cookieValue);
        httpRequestBase.setHeader(CONTENT_TYPE, MIME_TYPE_JSON);
    }

    private JSONObject setRequestBody(
        String nodeRef,
        String action,
        String description,
        String title,
        boolean isDisabled,
        boolean isApplyToChildren,
        boolean isAsync,
        String ruleType)
    {
        JSONArray conditions = new JSONArray();
        conditions.put(new JSONObject()
            .put("conditionDefinitionName","no-condition")
            .put("parameterValues", new JSONObject()));

        JSONArray actions = new JSONArray();
        actions.put(new JSONObject()
            .put("actionDefinitionName",action)
            .put("parameterValues", new JSONObject()
                .put("destination-folder","workspace://SpacesStore/"+ nodeRef)
                .put("deep-copy", "false")));

        JSONObject body = new JSONObject();
        body.put("action", new JSONObject()
            .put("actionDefinitionName", "composite-action")
            .put("actions", actions));

        body.put("title", title);
        body.put("description", description);
        body.put("disabled", isDisabled);
        body.put("applyToChildren", isApplyToChildren);
        body.put("executeAsynchronously", isAsync);
        body.put("ruleType", new JSONArray().put(ruleType));

        return body;
    }

    private CloseableHttpClient getClient()
    {
        return HttpClientBuilder.create().build();
    }
}
