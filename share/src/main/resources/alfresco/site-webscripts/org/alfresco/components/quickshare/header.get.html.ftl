<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/quickshare/header.css" />
   </@>

   <@markup id="js"/>

   <@markup id="widgets"/>

   <@markup id="html">
      <@uniqueIdDiv>
         <div class="quickshare-header">

            <div class="quickshare-header-brand-colors">
               <div class="brand-bgcolor-6"></div>
               <div class="brand-bgcolor-5"></div>
               <div class="brand-bgcolor-4"></div>
               <div class="brand-bgcolor-3"></div>
               <div class="brand-bgcolor-2"></div>
               <div class="brand-bgcolor-1"></div>
               <div class="clear"></div>
            </div>

            <div class="quickshare-header-left">
               <img width="180" src="${url.context}/res/components/images/alfresco-logo.svg">
            </div>

            <div class="quickshare-header-right">
               <@markup id="linkButtons">
                  <#list linkButtons as linkButton>
                     <a href="${linkButton.href}" class="brand-button ${linkButton.cssClass!""}" tabindex="0">${linkButton.label?html}</a>
                  </#list>
               </@markup>
            </div>
            
            <#if page.url.args.error! == "true">
               <script>
                  Alfresco.util.PopupManager.displayMessage({
                     text: "${authfailureMessage?js_string}"
                  });
               </script>
            </#if>

            <div class="clear"></div>

         </div>
      </@>
   </@>
</@>
