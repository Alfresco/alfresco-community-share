/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Notice dashlet component.
 *
 * @namespace Alfresco
 * @class Alfresco.dashlet.Notice
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $encodeHTML = Alfresco.util.encodeHTML;

   /**
    * Defence-in-depth sanitiser used for the in-page preview that runs after
    * the user saves the Site Notice. The authoritative sanitiser runs on the
    * server (SiteNoticeTextSanitizer.sanitize); this mirrors that policy on
    * the client so that an attacker who bypasses TinyMCE's content filter
    * (e.g. via the browser console) cannot trigger XSS through the innerHTML
    * assignment below, while still preserving the formatting tags and inline
    * styles produced by TinyMCE.
    *
    * Strips: script/iframe/object/embed/link/meta/base/form/style/frame/frameset
    * elements, all on* event-handler attributes, and non-standard URI schemes
    * on href/src/action/formaction/xlink:href attributes (using an allowlist
    * that mirrors the server-side allowStandardUrlProtocols policy).
    */
   var FORBIDDEN_TAGS =
   {
      SCRIPT: 1, IFRAME: 1, OBJECT: 1, EMBED: 1, LINK: 1, META: 1,
      BASE: 1, FORM: 1, STYLE: 1, FRAME: 1, FRAMESET: 1
   };
   var URI_ATTRS =
   {
      href: 1, src: 1, action: 1, formaction: 1, "xlink:href": 1
   };
   // Allowlist of safe URL schemes that mirrors server-side allowStandardUrlProtocols().
   // Rejects javascript:, data:, vbscript:, and other XSS vectors.
   var SAFE_URL_PATTERN = /^(?:https?|mailto|ftp|ftps|file|tel|sms|news|nntp|feed|webcal|irc|ircs|ssh|sftp|smb|nfs|git|svn|tel|sip|sips|callto|skype|geo|maps):/i;

   var sanitizeNoticeHTML = function Notice_sanitizeNoticeHTML(html)
   {
      if (html === null || typeof html === "undefined" || html === "")
      {
         return "";
      }
      if (typeof DOMParser === "undefined")
      {
         // Older browser fallback: fully encode so no markup is rendered.
         return $encodeHTML(html);
      }
      var doc = new DOMParser().parseFromString("<div>" + html + "</div>", "text/html");
      var root = doc.body && doc.body.firstChild;
      if (!root)
      {
         return "";
      }
      var nodes = root.querySelectorAll("*");
      for (var i = nodes.length - 1; i >= 0; i--)
      {
         var el = nodes[i];
         if (FORBIDDEN_TAGS[el.tagName])
         {
            if (el.parentNode)
            {
               el.parentNode.removeChild(el);
            }
            continue;
         }
         var attrs = el.attributes;
         for (var j = attrs.length - 1; j >= 0; j--)
         {
            var attr = attrs[j],
               name = attr.name.toLowerCase(),
               value = attr.value;
            if (name.indexOf("on") === 0)
            {
               el.removeAttribute(attr.name);
            }
            else if (URI_ATTRS[name])
            {
               // Check if the URI has a protocol scheme
               var trimmedValue = value.replace(/^\s+/, "");
               if (trimmedValue.indexOf(":") !== -1)
               {
                  // Has a scheme - must match the safe URL pattern
                  if (!SAFE_URL_PATTERN.test(trimmedValue))
                  {
                     el.removeAttribute(attr.name);
                  }
               }
               // Relative URLs (no scheme) are allowed
            }
         }
      }
      return root.innerHTML;
   };


   /**
    * Notice dashlet constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.Notice} The new component instance
    * @constructor
    */
   Alfresco.dashlet.Notice = function Notice_constructor(htmlId)
   {
      return Alfresco.dashlet.Notice.superclass.constructor.call(this, "Alfresco.dashlet.Notice", htmlId);
   };

   /**
    * Extend from Alfresco.component.Base and add class implementation
    */
   YAHOO.extend(Alfresco.dashlet.Notice, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The component id.
          *
          * @property componentId
          * @type string
          */
         componentId: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function Notice_onReady()
      {
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Configuration click handler
       *
       * @method onConfigClick
       * @param e {object} HTML event
       */
      onConfigClick: function Notice_onConfigClick(e)
      {
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/dashlet/config/" + encodeURIComponent(this.options.componentId);

         Event.stopEvent(e);

         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/notice/config",
               actionUrl: actionUrl,
               onSuccess:
               {
                  fn: function Notice_onConfigFeed_callback(response)
                  {
                     // Refresh the dashlet
                     var title = Dom.get(this.configDialog.id + "-title").value,
                        text = Dom.get(this.configDialog.id + "-text").value;
                     // Write the title and text into the dashlet. Sanitise the text
                     // to mirror the server-side SiteNoticeTextSanitizer.sanitize policy
                     // and prevent XSS from being injected via the in-page preview
                     // (defence-in-depth for ACS Site Notice stored XSS finding).
                     Dom.get(this.id + "-title").innerHTML = $encodeHTML(YAHOO.lang.trim(title) != "" ? title : this.msg("notice.defaultTitle"));
                     Dom.get(this.id + "-text").innerHTML = text != "" ? sanitizeNoticeHTML(text) : "<p>" + $encodeHTML(this.msg("notice.defaultText")) + "</p>";
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function Notice_doSetupForm_callback(form)
                  {
                     Dom.get(this.configDialog.id + "-title").value = Dom.get(this.id + "-title").innerHTML;
                     Dom.get(this.configDialog.id + "-text").value = Dom.get(this.id + "-text").innerHTML;
                     if (!this.configDialog.editor)
                     {
                        this.configDialog.editor = new Alfresco.util.RichEditor("tinyMCE", this.configDialog.id + "-text",
                        {
                           height: 150,
                           width: 404,
                           menu: {},
                           toolbar: "bold italic underline | bullist numlist | forecolor backcolor | undo redo removeformat | link anchor image code",
                           language: 'en',
                           statusbar: false,
                           extended_valid_elements: "a[href|target|name],font[face|size|color|style],span[class|align|style],div[class|align|style]"
                        });
                        this.configDialog.editor.render();
                        this.configDialog.editor.subscribe("onKeyUp", this._onTextContentChange, this.configDialog, true);
                        this.configDialog.editor.subscribe("onChange", this._onTextContentChange, this.configDialog, true);
                     }
                  },
                  scope: this
               },
               doBeforeFormSubmit:
               {
                  fn: function Notice_doBeforeFormSubmit_callback(form)
                  {
                     this.configDialog.editor.save();
                  },
                  scope: this
               }
            });

            //manually hide/show panel. see MNT-13324"
            var hidePanel = function(p_event, p_args)
            {
               Dom.setStyle(p_args[1].panel.element, "display", "none");
            }
            YAHOO.Bubbling.subscribe("hidePanel", hidePanel, this.configDialog);

            var showPanel = function(p_event, p_args)
            {
               Dom.setStyle(p_args[1].panel.element, "display", "block");
            }
            YAHOO.Bubbling.subscribe("showPanel", showPanel, this.configDialog);
         }
         else
         {
            this.configDialog.setOptions(
            {
               actionUrl: actionUrl
            });
         }
         // Ensure TinyMCE dialogs can receive focus and not captured by parent YUI dialog
         Event.on(document, "focusin", function(e) {
            Event.stopEvent(e);
         });

         this.configDialog.show();
      },

      /**
       * Handles the content being changed in the TinyMCE control.
       *
       * @method _onTextContentChange
       * @param type
       * @param args
       * @param obj
       * @private
       */
      _onTextContentChange: function Notice__onTextContentChange(type, args, obj)
      {
         // save the current contents of the editor to the underlying textarea
         this.editor.save();
      }

   });
})();
