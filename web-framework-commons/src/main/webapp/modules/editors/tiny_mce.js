/**
 * Adapter for TinyMCE html editor (https://www.tiny.cloud/).
 * Compatible with TinyMCE 8.x while preserving Alfresco Share editor behaviour.
 */
Alfresco.util.RichEditorManager.addEditor('tinyMCE', function(id, config)
{
   var editor;

   var TINYMCE_BASE_URL = (Alfresco.constants && Alfresco.constants.URL_RESCONTEXT)
      ? Alfresco.constants.URL_RESCONTEXT + 'modules/editors/tinymce/'
      : '/share/res/modules/editors/tinymce/';

   // Alfresco locale id -> TinyMCE language code (must match langs/{code}.js)
   var LOCALE_MAP = {
      'fr': 'fr-FR',
      'nb': 'nb-NO',
      'pt_BR': 'pt-BR',
      'zh_CN': 'zh-CN'
   };

   var REMOVED_PLUGINS = {
      paste: true,
      textcolor: true,
      contextmenu: true,
      compat3x: true,
      legacyoutput: true,
      fullpage: true,
      colorpicker: true,
      textpattern: true
   };

   function mapLocale(language)
   {
      if (!language)
      {
         return language;
      }

      language = String(language);

      if (LOCALE_MAP[language])
      {
         return LOCALE_MAP[language];
      }

      return language.indexOf('_') > -1 ? language.replace('_', '-') : language;
   }

   function mapToolbar(toolbar)
   {
      if (!toolbar)
      {
         return toolbar;
      }

      return toolbar
         .replace(/\bstyleselect\b/g, 'blocks')
         .replace(/\binserttime\b/g, 'insertdatetime')
         .replace(/\|\s*\|/g, '|')
         .replace(/^\|/, '')
         .replace(/\|$/, '')
         .replace(/\s+/g, ' ')
         .trim();
   }

   function mapPlugins(plugins)
   {
      var pluginList = [];

      if (typeof plugins === 'string')
      {
         pluginList = plugins.split(/\s+/);
      }
      else if (YAHOO.lang.isArray(plugins))
      {
         pluginList = plugins.join(' ').split(/\s+/);
      }

      var mapped = [];
      for (var i = 0, j = pluginList.length; i < j; i++)
      {
         var plugin = pluginList[i];
         if (plugin && !REMOVED_PLUGINS[plugin] && mapped.indexOf(plugin) === -1)
         {
            mapped.push(plugin);
         }
      }

      return mapped.join(' ');
   }

   function mapMenu(menu)
   {
      if (!menu)
      {
         return menu;
      }

      var mapped = YAHOO.lang.merge({}, menu);

      if (mapped.file && mapped.file.items)
      {
         mapped.file.items = mapped.file.items.replace(/\|\s*\|/g, '|').replace(/^\|/, '').replace(/\|$/, '');
      }
      if (mapped.insert && mapped.insert.items)
      {
         mapped.insert.items = mapped.insert.items.replace(/\binserttime\b/g, 'insertdatetime');
      }
      if (mapped.format && mapped.format.items)
      {
         mapped.format.items = mapped.format.items.replace(/\bformats\b/g, 'blocks');
      }

      return mapped;
   }

   function applyTinyMCE8Defaults(cfg)
   {
      cfg.license_key = cfg.license_key || 'gpl';
      if (cfg.promotion === undefined)
      {
         cfg.promotion = false;
      }
      if (cfg.branding === undefined)
      {
         cfg.branding = false;
      }
      cfg.base_url = cfg.base_url || TINYMCE_BASE_URL;
      cfg.suffix = cfg.suffix || '.min';

      if (!cfg.theme || cfg.theme === 'modern' || cfg.theme === 'inlite' || cfg.theme === 'mobile')
      {
         cfg.theme = 'silver';
      }

      if (cfg.language)
      {
         cfg.language = mapLocale(cfg.language);
      }

      if (cfg.toolbar)
      {
         cfg.toolbar = mapToolbar(cfg.toolbar);
      }

      cfg.plugins = mapPlugins(cfg.plugins);
      cfg.menu = mapMenu(cfg.menu);

      if (cfg.relative_urls === undefined)
      {
         cfg.relative_urls = true;
      }
      if (cfg.convert_urls === undefined)
      {
         cfg.convert_urls = false;
      }
      if (cfg.image_uploadtab === undefined)
      {
         cfg.image_uploadtab = false;
      }

      delete cfg.theme_advanced_resize_horizontal;

      var extValidElements = cfg.extended_valid_elements;
      extValidElements = (extValidElements && extValidElements !== '') ? (extValidElements + ',') : '';
      cfg.extended_valid_elements = extValidElements + 'embed[src|type|width|height|flashvars|wmode]';

      if (!cfg.init_instance_callback)
      {
         cfg.init_instance_callback = function(o)
         {
            return function(inst)
            {
               YAHOO.Bubbling.fire('editorInitialized', o);
            };
         }(this);
      }

      return cfg;
   }

   return (
   {
      init: function RichEditorManager_tinyMCE_init(id, config)
      {
         config.theme = 'silver';
         if (!config.toolbar)
         {
            config.toolbar = 'blocks | bold italic | forecolor backcolor | alignleft aligncenter alignright alignjustify | ltr rtl | bullist numlist outdent indent | link image emoticons codesample | preview print fullscreen';
         }
         if (!config.menu)
         {
            config.menu = {
               // TODO: I18N
               file   : {title : 'File'  , items : 'newdocument | print'},
               edit   : {title : 'Edit'  , items : 'undo redo | cut copy paste pastetext | selectall | searchreplace'},
               insert : {title : 'Insert', items : 'link image emoticons codesample | charmap hr anchor pagebreak insertdatetime nonbreaking'},
               view   : {title : 'View'  , items : 'fullscreen preview visualblocks code'},
               format : {title : 'Format', items : 'bold italic underline strikethrough superscript subscript | blocks | ltr rtl | removeformat'},
               table  : {title : 'Table' , items : 'inserttable tableprops deletetable | cell row column'}
            };
         }
         if (!config.plugins)
         {
            config.plugins = [
               'advlist autolink link image lists charmap preview anchor pagebreak',
               'searchreplace code fullscreen insertdatetime nonbreaking',
               'table visualblocks directionality emoticons',
               'wordcount tabfocus codesample'
            ];
         }

         applyTinyMCE8Defaults(config);
         editor = new tinymce.Editor(id, config, tinymce.EditorManager);

         return this;
      },

      getEditor: function RichEditorManager_tinyMCE_getEditor()
      {
         return editor;
      },

      clear: function RichEditorManager_tinyMCE_clear()
      {
         YAHOO.util.Dom.get(editor.id).value = '';
         editor.setContent('');
      },

      render: function RichEditorManager_tinyMCE_render()
      {
         editor.render();
         window.tinyMCE = window.tinymce;
      },

      execCommand: 'execCommand',

      disable: function RichEditorManager_tinyMCE_disable()
      {
         editor.hide();
      },

      enable: function RichEditorManager_tinyMCE_enable()
      {
         editor.show();
      },

      focus: function RichEditorManager_tinyMCE_focus()
      {
         editor.focus();
      },

      getContent: function RichEditorManager_tinyMCE_getContent()
      {
         return editor.getContent();
      },

      setContent: function RichEditorManager_tinyMCE_setContent(html)
      {
         editor.setContent(html);
      },

      save: function RichEditorManager_tinyMCE_save()
      {
         editor.save();
      },

      getContainer: function RichEditorManager_tinyMCE_getContainer()
      {
         return editor.getContainer();
      },

      activateButton: function RichEditorManager_tinyMCE_activateButton(buttonId)
      {
         if (editor && editor.ui && editor.ui.registry && editor.ui.registry.getAll)
         {
            var buttons = editor.ui.registry.getAll().buttons;
            if (buttons && buttons[buttonId] && buttons[buttonId].setActive)
            {
               buttons[buttonId].setActive(true);
            }
         }
      },

      deactivateButton: function RichEditorManager_tinyMCE_deactivateButton(buttonId)
      {
         if (editor && editor.ui && editor.ui.registry && editor.ui.registry.getAll)
         {
            var buttons = editor.ui.registry.getAll().buttons;
            if (buttons && buttons[buttonId] && buttons[buttonId].setActive)
            {
               buttons[buttonId].setActive(false);
            }
         }
      },

      isDirty: function RichEditorManager_tinyMCE_isDirty()
      {
         return editor.isDirty();
      },

      clearDirtyFlag: function RichEditorManager_tinyMCE_clearDirtyFlag()
      {
         editor.setDirty(false);
      },

      addPageUnloadBehaviour: function RichEditorManager_tinyMCE_addUnloadBehaviour(message, callback)
      {
         window.onbeforeunload = function(e)
         {
            if (YAHOO.lang.isFunction(callback) && callback())
            {
               var evt = e || window.event;
               if (editor.isDirty())
               {
                  if (evt)
                  {
                     evt.returnValue = message;
                  }
                  return message;
               }
            }
         };
      },

      addSaveKeyBehaviour: function RichEditorManager_tinyMCE_addSaveKeyBehaviour(fn)
      {
         new YAHOO.util.KeyListener(editor.getDoc(), { keys: 83, ctrl: true }, {
            fn: fn,
            scope: this,
            correctScope: true
         }).enable();
         new YAHOO.util.KeyListener(document, { keys: 83, ctrl: true }, {
            fn: function(id, e) {
               Event.stopEvent(e[1]);
            },
            scope: this,
            correctScope: true
         }).enable();
      }
   });
});
