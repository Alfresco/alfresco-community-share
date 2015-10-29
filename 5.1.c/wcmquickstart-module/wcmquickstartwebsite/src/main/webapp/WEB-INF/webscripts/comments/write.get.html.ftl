<a id="comment"></a>

<#if successAsset??>
    <@streamasset asset=successAsset/>
<#else>
    <form action="#comment" method="post">
        <fieldset class="blog-comment-fieldset">
            <input type="hidden" name="feedbackType" value="Comment"/>
            <input type="hidden" name="successPage" value="${uri}"/>
            <input type="hidden" name="formId" value="${formId}"/>
            <h3>${msg('comments.write.title')}</h3>
            <#if errors??>
                <div class="contact-error"><p>${msg('comments.write.errors')}</p></div>
            </#if> 
            <ul>
                <li>
                    <input type="text" name="visitorName" class="bc-input" value="${visitorName!''}" maxlength="70"/>
                    <label for="bc-name">${msg('comments.write.name')}</label> *
                    <#if errors?? && errors['visitorName']??>
                        <span class="contact-error-value">${msg(errors['visitorName'])}</span>
                    </#if>
                </li>
                <li>
                    <input type="text" name="visitorEmail" class="bc-input" value="${visitorEmail!''}" maxlength="100"/>
                    <label for="bc-email">${msg('comments.write.email')}</label> *
                    <#if errors?? && errors['visitorEmail']??>
                        <span class="contact-error-value">${msg(errors['visitorEmail'])}</span>
                    </#if>
                </li>
                <li>
                    <input type="text" name="visitorWebsite" class="bc-input" value="${visitorWebsite!''}" maxlength="100"/>
                    <label for="bc-website">${msg('comments.write.website')}</label>
                </li>
                <li>
                    <textarea name="feedbackComment" rows="6" cols="54" class="bc-textarea">${feedbackComment!''}</textarea>
                     <#if errors?? && errors['comment']??>
                        <span class="contact-error-value contact-error-comment">${msg(errors['comment'])}</span>
                    </#if>
                </li>  
                <li><input type="submit" value="${msg('comments.write.post')}" name="post" class="bc-submit" /></li>
            </ul>
        </fieldset>
    </form>
</#if>