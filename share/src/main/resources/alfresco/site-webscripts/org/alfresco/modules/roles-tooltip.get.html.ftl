<p><span class="alf-role-tooltip-header">${msg("roles-tooltip.header")}</span></p>
<#list rolesTooltipData as roleInfo>
    <p><span class="alf-role-tooltip-role-name">${roleInfo.roleName}</span>
    <span>${roleInfo.roleDescription}</span></p>
</#list>
<p><a href="${docUrlFtl(msg("roles-tooltip.docs-url"), context.properties["docsEdition"].value)}">${docUrlFtl(msg("roles-tooltip.docs-url-label"))}</a></p>
