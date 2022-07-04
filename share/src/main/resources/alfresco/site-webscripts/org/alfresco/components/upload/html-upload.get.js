/**
 * Custom content types
 */
function getContentTypes()
{
   // TODO: Data webscript call to return list of available types
   var contentTypes = [
      {
         id: "cm:content",
         value: "cm_content"
      }];

   return contentTypes;
}

model.contentTypes = getContentTypes();

function main()
{
   // Widget instantiation metadata...
   var htmlUpload = {
      id : "HtmlUpload", 
      name : "Alfresco.HtmlUpload",
      assignTo : "htmlUpload"
   };
   model.widgets = [htmlUpload];

   //Get the file-upload settings...
   var _maximumFileSizeLimit;
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var fileUpload = docLibConfig["file-upload"];
      if (fileUpload != null)
      {
         _maximumFileSizeLimit = fileUpload.getChildValue("maximum-file-size-limit");
      }
   }
   model.fileUploadSizeLimit = (_maximumFileSizeLimit != null) ? _maximumFileSizeLimit : "0";
}

main();

