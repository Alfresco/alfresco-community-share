function main()
{
   var hasConfigPermission = false;

   // Work out if the user has permission to configure the dashlet

   // Site ID and component ID must be passed manually to the web script when reloaded via XHR
   var siteId = page.url.templateArgs.site,
      objectId = instance.object.id;

   if (siteId != null) // Site or user dashboard?
   {
      // Call the repository to see if the user is a site manager or not
      var obj,
         json = remote.call("/api/sites/" + stringUtils.urlEncode(siteId) + "/memberships/" + stringUtils.urlEncode(user.name));
      if (json.status == 200)
      {
         obj = JSON.parse(json);
      }
      if (obj)
      {
         hasConfigPermission = (obj.role == "SiteManager");
      }
   }
   else
   {
      hasConfigPermission = true; // User dashboard
   }

   // Sanitise the persisted notice body before rendering to mitigate a stored XSS
   // where the POST to /modules/dashlet/config/... can be intercepted and the
   // "text" property replaced with a script payload (see ACS Security finding).
   //
   // We deliberately do NOT use stringUtils.stripUnsafeHTML here, because its
   // underlying policy strips the "target" attribute from anchor tags and
   // therefore regresses MNT-24073 / MNT-25236 (the "Open in new window" link
   // target is lost on reload). Instead we use a dedicated, TinyMCE-aligned
   // policy implemented in Java (SiteNoticeTextSanitizer) that preserves the
   // formatting tags and attributes the notice editor produces (a[href|target|
   // name|rel|title], font[face|size|color], span/div[class|align|style],
   // img[src|alt|width|height], lists, tables, etc.) while removing script
   // elements, event-handler attributes and javascript:/data: URIs.
   var text = String(Packages.org.alfresco.web.scripts.SiteNoticeTextSanitizer.sanitize(args.text || ""));
   model.text = text;

   // Component definition
   var dashlet = {
      id: "Notice",
      name: "Alfresco.dashlet.Notice",
      assignTo : "dashlet",                   // Need to reference the generated JS object
      options: {
         siteId : siteId,
         componentId : objectId,         // Reference to allow saving of component properties
         title: stringUtils.stripUnsafeHTML(args.title || ""),
         text: text
      }
   };

   // Dashlet title bar component actions and resizer
   var actions = [];
   if (hasConfigPermission)
   {
      actions.push(
      {
         cssClass: "edit",
         eventOnClick: { _alfValue : "editDashletEvent" + args.htmlid.replace(/-/g, "_"), _alfType: "REFERENCE"},
         tooltip: msg.get("dashlet.edit.tooltip")
      });
   }
   actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   });

   var dashletResizer = {
      id : "DashletResizer",
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"","\"" + objectId + "\""],
      useMessages: false
   };

   var dashletTitleBarActions = {
      id : "DashletTitleBarActions",
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: actions
      }
   };

   model.widgets = [dashlet, dashletResizer, dashletTitleBarActions];
}

main();
