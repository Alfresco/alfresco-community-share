/* Gutter Plugin */
Alfresco.gutter = function(myEditor, config)
{
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   config = config || {};

   return (
   {
      status: false,
      gutter: null,
      siteId: config.siteId,
      imagesLoaded: false,
      imagesLoading: false,

      bindImageSelection: function()
      {
         if (this._imageSelectionBound)
         {
            return;
         }
         this._imageSelectionBound = true;

         Event.on('image_results', 'mousedown', function(ev)
         {
            Event.stopEvent(ev);
            var target = Event.getTarget(ev);
            if (target.tagName.toLowerCase() === 'img')
            {
               var longdesc = target.getAttribute('longdesc');
               if (YAHOO.env.ua.ie > 0 && YAHOO.env.ua.ie < 8)
               {
                  longdesc = target.longdesc;
               }
               if (longdesc)
               {
                  var title = target.getAttribute('title');
                  YAHOO.Bubbling.fire('alfresco-imagelibClick',
                  {
                     type: 'alfresco-imagelibClick',
                     img: longdesc,
                     title: title
                  });
               }
            }
         }, myEditor, true);
      },

      loadImages: function()
      {
         var div = Dom.get('image_results');
         if (!div || !this.siteId || this.imagesLoading)
         {
            return;
         }

         this.imagesLoading = true;
         div.innerHTML = '<p class="imagelib-message">' + Alfresco.util.message('message.please-wait') + '</p>';

         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            dataObj: { max: '250' },
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/images/site/' + this.siteId + '/documentLibrary',
            successCallback:
            {
               fn: function(response)
               {
                  this.imagesLoading = false;
                  this.imagesLoaded = true;
                  div.innerHTML = '';

                  var result = YAHOO.lang.JSON.parse(response.serverResponse.responseText);
                  var items = result && result.items ? result.items : [];
                  if (items.length === 0)
                  {
                     div.innerHTML = '<p class="imagelib-message">No images found in the document library.</p>';
                     return;
                  }

                  var item, nodeRef, img;
                  for (var i = 0, j = items.length; i < j; i++)
                  {
                     item = items[i];
                     nodeRef = item.nodeRef.replace(':/', '');
                     img = document.createElement('img');
                     img.setAttribute('src', Alfresco.constants.PROXY_URI + 'api/node/' + nodeRef + '/content/thumbnails/doclib?c=queue&ph=true');
                     img.setAttribute('longdesc', Alfresco.constants.PROXY_URI_RELATIVE + 'api/node/content/' + nodeRef + '/' + config.encodeHTML(item.title));
                     img.setAttribute('title', config.encodeHTML(item.title));
                     div.appendChild(img);
                  }
               },
               scope: this
            },
            failureCallback:
            {
               fn: function()
               {
                  this.imagesLoading = false;
                  div.innerHTML = '<p class="imagelib-message">' + Alfresco.util.message('message.failure') + '</p>';
               },
               scope: this
            }
         });
      },

      createGutter: function()
      {
         if (this.gutter)
         {
            return;
         }

         this.gutter = new YAHOO.widget.Overlay('gutter1',
         {
            height: '372px',
            width: '260px',
            position: 'absolute',
            visible: false,
            zIndex: 100000
         });

         this.gutter.hideEvent.subscribe(function()
         {
            Dom.setStyle('image_results', 'overflow', 'hidden');
            Dom.setStyle('gutter1', 'visibility', 'hidden');
         }, this, true);

         this.gutter.showEvent.subscribe(function()
         {
            var container = myEditor.getContainer ? myEditor.getContainer() : null;
            if (container)
            {
               this.gutter.cfg.setProperty('context', [container, YAHOO.widget.Overlay.TOP_RIGHT, YAHOO.widget.Overlay.TOP_RIGHT]);
            }
            Dom.setStyle('gutter1', 'visibility', 'visible');
            Dom.setStyle('image_results', 'overflow', 'auto');
         }, this, true);

         new YAHOO.util.KeyListener(document, {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function()
            {
               if (this.status)
               {
                  this.toggle();
               }
            },
            scope: this,
            correctScope: true
         }).enable();

         var libraryTitle = Alfresco.util.message('imagelib.title');
         this.gutter.setBody('<div class="yui-toolbar-container"><div class="yui-toolbar-titlebar"><h2>' + libraryTitle + '</h2></div></div><div id="image_results"></div>');
         this.gutter.render(document.body);
         this.bindImageSelection();
         this.loadImages();
      },

      open: function()
      {
         if (!this.gutter)
         {
            this.createGutter();
         }
         this.gutter.show();
         this.status = true;
         if (!this.imagesLoaded && !this.imagesLoading)
         {
            this.loadImages();
         }
      },

      close: function()
      {
         if (this.gutter)
         {
            this.gutter.hide();
         }
         this.status = false;
      },

      toggle: function()
      {
         if (this.status)
         {
            this.close();
         }
         else
         {
            this.open();
         }
      }
   });
};

/**
 * Alfresco top-level util namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.util
 */
Alfresco.util = Alfresco.util || {};

Alfresco.util.createImageEditor = function(id, options)
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Register a toolbar button using TinyMCE 8 ui.registry or legacy addButton API.
    */
   function addEditorButton(ed, name, settings)
   {
      if (ed.ui && ed.ui.registry && ed.ui.registry.addButton)
      {
         ed.ui.registry.addButton(name,
         {
            icon: settings.icon,
            tooltip: settings.title,
            onAction: function()
            {
               if (settings.onclick)
               {
                  settings.onclick();
               }
            }
         });
      }
      else if (ed.addButton)
      {
         ed.addButton(name, settings);
      }
   }

   /**
    * Insert HTML content using TinyMCE 8 insertContent or legacy execCommand API.
    */
   function insertEditorContent(ed, html)
   {
      if (ed.insertContent)
      {
         ed.insertContent(html);
      }
      else
      {
         ed.execCommand('mceInsertContent', false, html);
      }
   }

   var editor, gutter, documentPicker;

   options.setup = function(ed)
   {
      addEditorButton(ed, 'alfresco-imagelibrary',
      {
         title: Alfresco.util.message('imagelib.tooltip'),
         icon: 'gallery',
         onclick: function()
         {
            gutter.toggle.call(gutter);
         }
      });

      addEditorButton(ed, 'alfresco-linklibrary',
      {
         title: Alfresco.util.message('linklib.tooltip'),
         icon: 'new-document',
         onclick: function()
         {
            if (documentPicker)
            {
               documentPicker.onShowPicker();
            }
         }
      });

      YAHOO.Bubbling.on('alfresco-imagelibClick', function(ev, args)
      {
         if (args && args[1].img)
         {
            var title = args[1].title ? args[1].title : '';
            var html = '<img src="' + args[1].img + '" title="' + $html(title) + '"/>';
            insertEditorContent(ed, html);
         }
         gutter.toggle();
      });

      YAHOO.Bubbling.on('onDocumentsSelected', function(eventName, payload)
      {
         if (payload && payload[1].items)
         {
            var selText = ed.selection.getContent();
            for (var i = 0, j = payload[1].items.length; i < j; i++)
            {
               var selectedItem = payload[1].items[i],
                  nodeRef = encodeURIComponent(selectedItem.nodeRef),
                  label = (selText && selText.length > 0) ? selText : selectedItem.name;
               var link = Alfresco.util.siteURL('document-details?nodeRef=' + nodeRef);
               var html = '<a href="' + link + '">' + $html(label) + '</a> ';

               if (YAHOO.env.ua.ie === 8)
               {
                  editor.focus();
               }

               insertEditorContent(ed, html);

               if (documentPicker)
               {
                  documentPicker.resetSelection();
               }
            }
         }
      });
   };

   editor = new Alfresco.util.RichEditor(Alfresco.constants.HTML_EDITOR, id, options);
   gutter = new Alfresco.gutter(editor,
   {
      siteId: options.siteId,
      encodeHTML: $html
   });

   YAHOO.Bubbling.on('editorInitialized', function()
   {
      gutter.createGutter();
   });

   var getDocLibNodeRefUrl = Alfresco.constants.PROXY_URI + 'slingshot/doclib/container/' + options.siteId + '/documentLibrary';
   Alfresco.util.Ajax.jsonGet(
   {
      url: getDocLibNodeRefUrl,
      successCallback:
      {
         fn: function(response)
         {
            var nodeRef = response.json.container.nodeRef;
            documentPicker = new Alfresco.module.DocumentPicker(id + '-docPicker', Alfresco.ObjectRenderer);
            documentPicker.setOptions(
            {
               displayMode: 'items',
               itemFamily: 'node',
               itemType: 'cm:content',
               multipleSelectMode: true,
               parentNodeRef: nodeRef,
               restrictParentNavigationToDocLib: true
            });
            documentPicker.onComponentsLoaded();
         },
         scope: this
      }
   });

   return editor;
};
