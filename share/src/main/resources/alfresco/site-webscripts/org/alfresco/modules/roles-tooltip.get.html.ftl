<p><span class="alf-role-tooltip-header">${msg("roles-tooltip.header")}</span></p>
<#list rolesTooltipData as roleInfo>
    <p><span class="alf-role-tooltip-role-name">${roleInfo.roleName}</span>
    <span>${roleInfo.roleDescription}</span></p>
</#list>
<p><a href="${documentationUrl("roles-tooltip.docs-url")}">${msg("roles-tooltip.docs-url-label")}</a></p>
