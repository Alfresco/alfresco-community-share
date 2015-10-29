<#import "invite-response.lib.ftl" as inviteLib />

<#if form.mode =="edit" && formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>
   
<div class="form-container">
   
   <#if form.mode =="edit">
      <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
   </#if>
   
   <div id="${formId}-fields" class="form-fields">
      <div class="yui-gc">
         <div class="yui-u first">
            <div class="invite-task-title">
               <img src="${url.context}/res/components/images/site-24.png" />
               <span>${msg("workflow.task.invite.title", form.data["prop_inwf_resourceTitle"])?html}</span>
            </div>
            <div class="invite-task-subtitle">
               ${msg("workflow.task.invite.subtitle", form.data["prop_inwf_resourceTitle"])?html}
            </div>
            <div class="invite-task-role">
               <#assign roleLabel = msg("role." + form.data["prop_inwf_inviteeRole"]) />
               ${msg("workflow.task.invite.role", roleLabel)?html}
            </div>
         </div>
         <div class="yui-u">
            <div class="invite-task-priority">
               <img src="${url.context}/res/components/images/${inviteLib.getPriorityIcon()}" />
            </div>
         </div>
      </div>
      
      <#if form.mode =="edit">
         <div class="invite-task-controls">
            <@formLib.renderSet set=form.structure[1] />
         </div>
      </#if>
   </div>
   
   <#if form.mode =="edit">
      <@formLib.renderFormButtons formId=formId />
      </form>
   </#if>
</div>
