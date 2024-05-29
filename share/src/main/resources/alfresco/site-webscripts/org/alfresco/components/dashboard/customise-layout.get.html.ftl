<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashboard/customise-layout.css" group="dashboard"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashboard/customise-layout.js" group="dashboard"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashboard"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="customise-layout">
         <div id="${el}-hideCurrentLayout-div" style="display: none;"></div>
         <div id="${el}-currentLayout-div" class="currentLayout">
            <h2>${msg("section.currentLayout")}
               <span id="${el}-currentLayoutDescription-span">${currentLayout.description}</span>
            </h2>
            <hr/>
            <div>
               <br />
               <img id="${el}-currentLayoutIcon-img" class="layoutIcon" src="${url.context}/res/components/dashboard/images/${currentLayout.templateId}.png" alt="${msg("img.currentLayout")}" />
               <div id="${el}-changeButtonWrapper-div" class="buttons">
                  <input id="${el}-change-button" type="button" value="${msg("button.showLayouts")}" />
               </div>
            </div>
         </div>
         <div id="${el}-layouts-div" class="layouts" style="display: none;">
            <h2 class="instructions">${msg("section.selectNewLayout")}</h2>
            <hr/>
            <div>
               <div class="text">${msg("label.layoutWarning")}</div>
               <ul id="${el}-layout-ul">
               <#list layouts?values as layout>
                  <li id="${el}-layout-li-${layout.templateId}">
                     <div class="layoutDescription">${layout.description}</div>
                     <div class="layoutBox">
                        <span>
                           <img id="${el}-select-img-${layout.templateId}" class="layoutIcon" src="${url.context}/res/components/dashboard/images/${layout.templateId}.png" alt="${layout.templateId}" />
                           <input id="${el}-select-button-${layout.templateId}" type="button" value="${msg("button.select")}" />
                        </span>
                     </div>
                  </li>
               </#list>
               </ul>
            </div>
            <hr/>
            <div>
               <div class="buttons">
                  <input id="${el}-cancel-button" type="button" value="${msg("button.useCurrent")}" />
               </div>
            </div>
         </div>
      </div>
   </@>
</@>