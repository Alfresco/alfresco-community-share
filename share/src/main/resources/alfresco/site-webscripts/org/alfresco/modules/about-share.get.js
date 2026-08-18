// Reads the TinyMCE version from the given path.
function readTinyMCEVersionFromPath(servletContext, path)
{
   var reader = null;
   var stream = null;
   try
   {
      stream = servletContext.getResourceAsStream(path);
      if (stream == null)
      {
         return null;
      }

      reader = new java.io.BufferedReader(
         new java.io.InputStreamReader(stream, "UTF-8"));

      var marker = "TinyMCE version ";
      
	  // read the first 5 lines of the file, not entire one.
      for (var i = 0; i < 5; i++)
      {
         var line = reader.readLine();
         if (line == null)
         {
            break;
         }

         var idx = line.indexOf(marker);
         if (idx >= 0)
         {
            var rest = String(line.substring(idx + marker.length));
            return rest.split(" ")[0];
         }
      }
   }
   finally
   {
      try
      {
          // Close the reader and stream in case they are not null
         // and even if there is an error.
         if (reader != null)
         {
            reader.close();
         }
         if (stream != null)
         {
            stream.close();
         }
      }
      catch (closeEx)
      {
         if (logger.isWarnLoggingEnabled())
         {
            logger.warn("Failed to close TinyMCE version resource for " + path + " - " + closeEx);
         }
      }
   }
   return null;
}

// Fetching exact TinyMCE library version to displaying alfresco footer banner
function getTinyMCEVersion()
{
   try
   {
      var servletContext = org.springframework.web.context.ContextLoader
         .getCurrentWebApplicationContext()
         .getServletContext();

      return readTinyMCEVersionFromPath(servletContext, "/modules/editors/tinymce/tinymce.min.js")
         || readTinyMCEVersionFromPath(servletContext, "/modules/editors/tinymce/tinymce.js");
   }
   catch (e)
   {
      if (logger.isWarnLoggingEnabled())
      {
         logger.warn("Failed to read TinyMCE version - " + e);
      }
   }
   return null;
}

function main()
{
   // Call the repo to collect server meta-data
   var conn = remote.connect("alfresco"),
      res = conn.get("/api/server"),
      json = JSON.parse(res);


   // Create model and defaults
   model.shareVersion = "Unknown";
   model.shareLibs = {};
   model.tinymceVersion = "Unknown";
   model.serverEdition = "Unknown";
   model.serverVersion = "Unknown (Unknown)";
   model.serverSchema = "Unknown";
   model.licenseHolder = "UNKNOWN";
   
   // Check if we got a positive result
   if (json.data)
   {
      model.serverEdition = json.data.edition;
      model.serverVersion = json.data.version;
      model.serverSchema = json.data.schema;
      model.licenseHolder = context.properties["editionInfo"].holder;
   }
   model.shareVersion = shareManifest.getImplementationVersion();
   model.shareLibs = shareManifest.attributesMap("Share Libraries");
   model.shareBuild = "r" + shareManifest.mainAttributeValue("Build-Revision") + 
      "-b" + shareManifest.mainAttributeValue("Build-Number");
   model.tinymceVersion = getTinyMCEVersion() || "Unknown";
}

main();