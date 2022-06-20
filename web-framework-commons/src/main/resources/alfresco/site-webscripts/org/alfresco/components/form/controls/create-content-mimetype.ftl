<#-- Renders a hidden form field for create content form containing a valid mimetype -->
<#assign fieldValue = "">
<#if field.control.params.contextProperty??>
   <#if context.properties[field.control.params.contextProperty]??>
      <#assign fieldValue = context.properties[field.control.params.contextProperty]>
   <#elseif args[field.control.params.contextProperty]??>
      <#assign fieldValue = args[field.control.params.contextProperty]>
   </#if>
<#elseif context.properties[field.name]??>
   <#assign fieldValue = context.properties[field.name]>
<#else>
   <#assign fieldValue = field.value>
</#if>

<#assign mimetype = "">
<#if fieldValue?lower_case == "text/plain" || fieldValue?lower_case == "text/xml" || fieldValue?lower_case == "text/html">
  <#assign mimetype = fieldValue>
</#if>

<input type="hidden" name="${field.name}" value="${mimetype?html}" />