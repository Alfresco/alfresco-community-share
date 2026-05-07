function main()
{
   model.contentTypes =
   [{
      id: "cm:content",
      value: "cm_content"
   }];


   //Widget instantiation metadata...
   var dndUpload = {
      id : "DNDUpload",
      name : "Alfresco.DNDUpload",
      assignTo : "dndUpload"
   };
   model.widgets = [dndUpload];

   // Get the file-upload settings...
   var _inMemoryLimit, _maximumFileSizeLimit, _batchSize ;
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var fileUpload = docLibConfig["file-upload"];
      if (fileUpload != null)
      {
         _inMemoryLimit = fileUpload.getChildValue("in-memory-limit");
         _maximumFileSizeLimit = fileUpload.getChildValue("maximum-file-size-limit");
         _batchSize = fileUpload.getChildValue("batch-size");
      }
   }
   model.inMemoryLimit = (_inMemoryLimit != null) ? _inMemoryLimit : "262144000";
   model.fileUploadSizeLimit = (_maximumFileSizeLimit != null) ? _maximumFileSizeLimit : "0";
   model.batchSize = (_batchSize != null) ? _batchSize : "1000";
}

main();
