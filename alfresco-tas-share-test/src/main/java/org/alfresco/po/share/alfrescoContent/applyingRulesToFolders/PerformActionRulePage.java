package org.alfresco.po.share.alfrescoContent.applyingRulesToFolders;

import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class PerformActionRulePage extends SiteCommon<PerformActionRulePage> {

    private EditRulesPage editRulesPage;
    private SelectDestinationDialog selectDestinationDialog;

    private final By mimetypeDropDown = By.cssSelector("div[id*=ruleConfigAction] select[title=Mimetype]");

    public PerformActionRulePage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void transformAndCopy(Mimetype mimetypeTransformation, String targetSite , String targetFolder)
    {
        Select performAction = new Select(findElement(mimetypeDropDown));
        performAction.selectByValue(mimetypeTransformation.getValue());
        editRulesPage.clickCopySelectButton();
        selectDestinationDialog.selectSite(targetSite);
        selectDestinationDialog.selectFolderPath(targetFolder);
        selectDestinationDialog.confirmFolderLocation();
        editRulesPage.clickCreateButton();
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    /**
     * Values of `Transform And Copy Image/Content` rule's dropdown mimetypes.
     */
    //todo: remove to separate file
    public enum Mimetype {
        VIDEO_3G("3G Video", "video/3gpp"),
        VIDEO_3G2("3G2 Video", "video/3gpp2"),
        AIFF_AUDIO("AIFF Audio", "audio/x-aiff"),
        ADOBE_AIR("Adobe AIR", "application/vnd.adobe.air-application-installer-package+zip"),
        ADOBE_ACROBAT_XML_DATA_PACKAGE("Adobe Acrobat XML Data Package", "application/vnd.adobe.xdp+xml"),
        ADOBE_AFTEREFFECTS_PROJECT("Adobe AfterEffects Project", "application/vnd.adobe.aftereffects.project"),
        ADOBE_AFTEREFFECTS_TEMPLATE("Adobe AfterEffects Template", "application/vnd.adobe.aftereffects.template"),
        ADOBE_DIGITAL_NEGATIVE_IMAGE("Adobe Digital Negative Image", "image/x-raw-adobe"),
        ADOBE_FLEX_PROJECT_FILE("Adobe Flex Project File", "application/x-zip"),
        ADOBE_FRAMEMAKER("Adobe FrameMaker", "application/framemaker"),
        ADOBE_ILLUSTRATOR_FILE("Adobe Illustrator File", "application/illustrator"),
        ADOBE_INDESIGN_DOCUMENT("Adobe InDesign Document", "application/x-indesign"),
        ADOBE_PDF_DOCUMENT("Adobe PDF Document", "application/pdf"),
        ADOBE_PAGEMAKER("Adobe PageMaker", "application/pagemaker"),
        ADOBE_PHOTOSHOP("Adobe Photoshop", "image/vnd.adobe.photoshop"),
        ADOBE_PREMIERE("Adobe Premiere", "image/vnd.adobe.premiere"),
        ADOBE_SOUNDBOOTH("Adobe SoundBooth", "audio/vnd.adobe.soundbooth"),
        ALFRESCO_CONTENT_PACKAGE("Alfresco Content Package", "application/acp"),
        ANDROID_PACKAGE("Android Package", "application/vnd.android.package-archive"),
        ANYMAP_IMAGE("Anymap Image", "image/x-portable-anymap"),
        APPLE_IWORK_KEYNOTE("Apple iWork Keynote", "application/vnd.apple.keynote"),
        APPLE_IWORK_NUMBERS("Apple iWork Numbers", "application/vnd.apple.numbers"),
        APPLE_IWORK_PAGES("Apple iWork Pages", "application/vnd.apple.pages"),
        AUTOCAD_DRAWING("AutoCAD Drawing", "image/vnd.dwg"),
        AUTOCAD_TEMPLATE("AutoCAD Template", "image/x-dwt"),
        BASIC_AUDIO("Basic Audio", "audio/basic"),
        BINARY_FILE("Binary File", "application/x-dosexec"),
        BINARY_FILE_OCTET_STREAM("Binary File (Octet Stream)", "application/octet-stream"),
        BITMAP_IMAGE("Bitmap Image", "image/bmp"),
        CGM_IMAGE("CGM Image", "image/cgm"),
        CANON_RAW_IMAGE("Canon RAW Image", "image/x-raw-canon"),
        COMMA_SEPARATED_VALUES_CSV("Comma Separated Values (CSV)", "text/csv"),
        DITA("DITA", "application/dita+xml"),
        EMAIL("EMail", "message/rfc822"),
        EPS_TYPE_POSTSCRIPT("EPS Type PostScript", "application/eps"),
        FLAC_AUDIO("FLAC Audio", "audio/x-flac"),
        FLASH_SOURCE("Flash Source", "application/x-fla"),
        FLASH_VIDEO("Flash Video", "video/x-flv"),
        FUJI_RAW_IMAGE("Fuji RAW Image", "image/x-raw-fuji"),
        GIF_IMAGE("GIF Image", "image/gif"),
        GZIP("GZIP", "application/x-gzip"),
        GZIP_TARBALL("GZIP Tarball", "application/x-gtar"),
        GREYMAP_IMAGE("Greymap Image", "image/x-portable-graymap"),
        HTML("HTML", "text/html"),
        HTML_DOCUMENT_TEMPLATE("HTML Document Template", "application/vnd.oasis.opendocument.text-web"),
        HASSELBLAD_RAW_IMAGE("Hasselblad RAW Image", "image/x-raw-hasselblad"),
        IEF_IMAGE("IEF Image", "image/ief"),
        JPEG_2000_IMAGE("JPEG 2000 Image", "image/jp2"),
        JPEG_IMAGE("JPEG Image", "image/jpeg"),
        JSON("JSON", "application/json"),
        JAVA_ARCHIVE("Java Archive", "application/java-archive"),
        JAVA_CLASS("Java Class", "application/java"),
        JAVA_SERVER_PAGE("Java Server Page", "text/x-jsp"),
        JAVA_SOURCE_FILE("Java Source File", "text/x-java-source"),
        JAVASCRIPT("JavaScript", "application/x-javascript"),
        KODAK_RAW_IMAGE("Kodak RAW Image", "image/x-raw-kodak"),
        LATEX("LaTeX", "application/x-latex"),
        LEICA_RAW_IMAGE("Leica RAW Image", "image/x-raw-leica"),
        MPEG_AUDIO("MPEG Audio", "audio/mpeg"),
        MPEG_TRANSPORT_STREAM("MPEG Transport Stream", "video/mp2t"),
        MPEG_VIDEO("MPEG Video", "video/mpeg"),
        MPEG2_VIDEO("MPEG2 Video", "video/mpeg2"),
        MPEG4_AUDIO("MPEG4 Audio", "audio/mp4"),
        MPEG4_VIDEO("MPEG4 Video", "video/mp4"),
        MPEG4_VIDEO_M4V("MPEG4 Video (m4v)", "video/x-m4v"),
        MS_ASF_STREAMING_VIDEO("MS ASF Streaming Video", "video/x-ms-asf"),
        MS_VIDEO("MS Video", "video/x-msvideo"),
        MS_WMA_STREAMING_AUDIO("MS WMA Streaming Audio", "audio/x-ms-wma"),
        MS_WMV_STREAMING_VIDEO("MS WMV Streaming Video", "video/x-ms-wmv"),
        MAN_PAGE("Man Page", "application/x-troff-man"),
        MARKDOWN("Markdown", "text/x-markdown"),
        MATERIAL_EXCHANGE_FORMAT("Material Exchange Format", "application/mxf"),
        MEDIAWIKI_MARKUP("MediaWiki Markup", "text/mediawiki"),
        MICROSOFT_EXCEL("Microsoft Excel", "application/vnd.ms-excel"),
        MICROSOFT_EXCEL_2007("Microsoft Excel 2007", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        MICROSOFT_EXCEL_2007_ADD_IN("Microsoft Excel 2007 add-in", "application/vnd.ms-excel.addin.macroenabled.12"),
        MICROSOFT_EXCEL_2007_BINARY_WORKBOOK("Microsoft Excel 2007 binary workbook", "application/vnd.ms-excel.sheet.binary.macroenabled.12"),
        MICROSOFT_EXCEL_2007_MACRO_ENABLED_WORKBOOK("Microsoft Excel 2007 macro-enabled workbook", "application/vnd.ms-excel.sheet.macroenabled.12"),
        MICROSOFT_EXCEL_2007_MACRO_ENABLED_WORKBOOK_TEMPLATE("Microsoft Excel 2007 macro-enabled workbook template", "application/vnd.ms-excel.template.macroenabled.12"),
        MICROSOFT_EXCEL_TEMPLATE_2007("Microsoft Excel template 2007", "application/vnd.openxmlformats-officedocument.spreadsheetml.template"),
        MICROSOFT_OUTLOOK_MESSAGE("Microsoft Outlook Message", "application/vnd.ms-outlook"),
        MICROSOFT_POWERPOINT("Microsoft PowerPoint", "application/vnd.ms-powerpoint"),
        MICROSOFT_POWERPOINT_2007_ADD_IN("Microsoft PowerPoint 2007 add-in", "application/vnd.ms-powerpoint.addin.macroenabled.12"),
        MICROSOFT_POWERPOINT_2007_MACRO_ENABLED_PRESENTATION("Microsoft PowerPoint 2007 macro-enabled presentation", "application/vnd.ms-powerpoint.presentation.macroenabled.12"),
        MICROSOFT_POWERPOINT_2007_MACRO_ENABLED_PRESENTATION_TEMPLATE("Microsoft PowerPoint 2007 macro-enabled presentation template", "application/vnd.ms-powerpoint.template.macroenabled.12"),
        MICROSOFT_POWERPOINT_2007_MACRO_ENABLED_SLIDE("Microsoft PowerPoint 2007 macro-enabled slide", "application/vnd.ms-powerpoint.slide.macroenabled.12"),
        MICROSOFT_POWERPOINT_2007_MACRO_ENABLED_SLIDE_SHOW("Microsoft PowerPoint 2007 macro-enabled slide show", "application/vnd.ms-powerpoint.slideshow.macroenabled.12"),
        MICROSOFT_POWERPOINT_2007_PRESENTATION("Microsoft PowerPoint 2007 presentation", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        MICROSOFT_POWERPOINT_2007_SLIDE("Microsoft PowerPoint 2007 slide", "application/vnd.openxmlformats-officedocument.presentationml.slide"),
        MICROSOFT_POWERPOINT_2007_SLIDE_SHOW("Microsoft PowerPoint 2007 slide show", "application/vnd.openxmlformats-officedocument.presentationml.slideshow"),
        MICROSOFT_POWERPOINT_2007_TEMPLATE("Microsoft PowerPoint 2007 template", "application/vnd.openxmlformats-officedocument.presentationml.template"),
        MICROSOFT_PROJECT("Microsoft Project", "application/vnd.ms-project"),
        MICROSOFT_VISIO("Microsoft Visio", "application/vnd.visio"),
        MICROSOFT_VISIO_2013("Microsoft Visio 2013", "application/vnd.visio2013"),
        MICROSOFT_WORD("Microsoft Word", "application/msword"),
        MICROSOFT_WORD_2007("Microsoft Word 2007", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        MICROSOFT_WORD_2007_MACRO_ENABLED_DOCUMENT("Microsoft Word 2007 macro-enabled document", "application/vnd.ms-word.document.macroenabled.12"),
        MICROSOFT_WORD_2007_MACRO_ENABLED_DOCUMENT_TEMPLATE("Microsoft Word 2007 macro-enabled document template", "application/vnd.ms-word.template.macroenabled.12"),
        MICROSOFT_WORD_2007_TEMPLATE("Microsoft Word 2007 template", "application/vnd.openxmlformats-officedocument.wordprocessingml.template"),
        MINOLTA_RAW_IMAGE("Minolta RAW Image", "image/x-raw-minolta"),
        NIKON_RAW_IMAGE("Nikon RAW Image", "image/x-raw-nikon"),
        OGG_AUDIO("Ogg Audio", "audio/ogg"),
        OGG_MULTIPLEX("Ogg Multiplex", "application/ogg"),
        OGG_VIDEO("Ogg Video", "video/ogg"),
        OGG_VORBIS_AUDIO("Ogg Vorbis Audio", "audio/vorbis"),
        OLYMPUS_RAW_IMAGE("Olympus RAW Image", "image/x-raw-olympus"),
        OPENDOCUMENT_CHART("OpenDocument Chart", "application/vnd.oasis.opendocument.chart"),
        OPENDOCUMENT_DATABASE("OpenDocument Database", "application/vnd.oasis.opendocument.database"),
        OPENDOCUMENT_DRAWING("OpenDocument Drawing", "application/vnd.oasis.opendocument.graphics"),
        OPENDOCUMENT_DRAWING_TEMPLATE("OpenDocument Drawing Template", "application/vnd.oasis.opendocument.graphics-template"),
        OPENDOCUMENT_FORMULA("OpenDocument Formula", "application/vnd.oasis.opendocument.formula"),
        OPENDOCUMENT_IMAGE("OpenDocument Image", "application/vnd.oasis.opendocument.image"),
        OPENDOCUMENT_MASTER_DOCUMENT("OpenDocument Master Document", "application/vnd.oasis.opendocument.text-master"),
        OPENDOCUMENT_PRESENTATION("OpenDocument Presentation", "application/vnd.oasis.opendocument.presentation"),
        OPENDOCUMENT_PRESENTATION_TEMPLATE("OpenDocument Presentation Template", "application/vnd.oasis.opendocument.presentation-template"),
        OPENDOCUMENT_SPREADSHEET("OpenDocument Spreadsheet", "application/vnd.oasis.opendocument.spreadsheet"),
        OPENDOCUMENT_SPREADSHEET_TEMPLATE("OpenDocument Spreadsheet Template", "application/vnd.oasis.opendocument.spreadsheet-template"),
        OPENDOCUMENT_TEXT_OPENOFFICE_20("OpenDocument Text (OpenOffice 2.0)", "application/vnd.oasis.opendocument.text"),
        OPENDOCUMENT_TEXT_TEMPLATE("OpenDocument Text Template", "application/vnd.oasis.opendocument.text-template"),
        OPENOFFICE10_STAROFFICE60_CALC60("OpenOffice 1.0/StarOffice6.0 Calc 6.0", "application/vnd.sun.xml.calc"),
        OPENOFFICE10_STAROFFICE60_CALC60_TEMPLATE("OpenOffice 1.0/StarOffice6.0 Calc 6.0 Template", "application/vnd.sun.xml.calc.template"),
        OPENOFFICE10_STAROFFICE60_DRAW60("OpenOffice 1.0/StarOffice6.0 Draw 6.0", "application/vnd.sun.xml.draw"),
        OPENOFFICE10_STAROFFICE60_IMPRESS60("OpenOffice 1.0/StarOffice6.0 Impress 6.0", "application/vnd.sun.xml.impress"),
        OPENOFFICE10_STAROFFICE60_IMPRESS60_TEMPLATE("OpenOffice 1.0/StarOffice6.0 Impress 6.0 Template", "application/vnd.sun.xml.impress.template"),
        OPENOFFICE10_STAROFFICE60_WRITER60("OpenOffice 1.0/StarOffice6.0 Writer 6.0", "application/vnd.sun.xml.writer"),
        OPENOFFICE10_STAROFFICE60_WRITER60_TEMPLATE("OpenOffice 1.0/StarOffice6.0 Writer 6.0 Template", "application/vnd.sun.xml.writer.template"),
        PNG_IMAGE("PNG Image", "image/png"),
        PANASONIC_RAW_IMAGE("Panasonic RAW Image", "image/x-raw-panasonic"),
        PENTAX_RAW_IMAGE("Pentax RAW Image", "image/x-raw-pentax"),
        PIXMAP_IMAGE("Pixmap Image", "image/x-portable-pixmap"),
        PLAIN_TEXT("Plain Text", "text/plain"),
        PORTABLE_BITMAP("Portable Bitmap", "image/x-portable-bitmap"),
        POSTSCRIPT("PostScript", "application/postscript"),
        PRINTER_TEXT_FILE("Printer Text File", "application/remote-printing"),
        QUICKTIME_VIDEO("Quicktime Video", "video/quicktime"),
        RAD_SCREEN_DISPLAY("RAD Screen Display", "video/x-rad-screenplay"),
        RAR_ARCHIVE("RAR Archive", "application/x-rar-compressed"),
        RED_RAW_IMAGE("RED RAW Image", "image/x-raw-red"),
        RGB_IMAGE("RGB Image", "image/x-rgb"),
        RSS("RSS", "application/rss+xml"),
        RASTER_IMAGE("Raster Image", "image/x-cmu-raster"),
        RICH_TEXT("Rich Text", "text/richtext"),
        RICH_TEXT_FORMAT("Rich Text Format", "application/rtf"),
        SGI_VIDEO("SGI Video", "video/x-sgi-movie"),
        SGML_HUMAN_READABLE("SGML (Human Readable)", "text/sgml"),
        SGML_MACHINE_READABLE("SGML (Machine Readable)", "application/sgml"),
        SCALABLE_VECTOR_GRAPHICS_IMAGE("Scalable Vector Graphics Image", "image/svg+xml"),
        SHELL_SCRIPT("Shell Script", "application/x-sh"),
        SHOCKWAVE_FLASH("Shockwave Flash", "application/x-shockwave-flash"),
        SIGMA_RAW_IMAGE("Sigma RAW Image", "image/x-raw-sigma"),
        SONY_RAW_IMAGE("Sony RAW Image", "image/x-raw-sony"),
        STACHART_5X("StaChart 5.x", "application/vnd.stardivision.chart"),
        STARCALC_5X("StarCalc 5.x", "application/vnd.stardivision.calc"),
        STARDRAW_5X("StarDraw 5.x", "application/vnd.stardivision.draw"),
        STARIMPRESS_5X("StarImpress 5.x", "application/vnd.stardivision.impress"),
        STARIMPRESS_PACKED_5X("StarImpress Packed 5.x", "application/vnd.stardivision.impress-packed"),
        STARMATH_5X("StarMath 5.x", "application/vnd.stardivision.math"),
        STARWRITER_5X("StarWriter 5.x", "application/vnd.stardivision.writer"),
        STARWRITER_5X_GLOBAL("StarWriter 5.x global", "application/vnd.stardivision.writer-global"),
        STYLE_SHEET("Style Sheet", "text/css"),
        TIFF_IMAGE("TIFF Image", "image/tiff"),
        TAB_SEPARATED_VALUES("Tab Separated Values", "text/tab-separated-values"),
        TARBALL("Tarball", "application/x-tar"),
        TEX("Tex", "application/x-tex"),
        TEX_INFO("Tex Info", "application/x-texinfo"),
        VRML("VRML", "x-world/x-vrml"),
        WAV_AUDIO("WAV Audio", "audio/x-wav"),
        WEBM_VIDEO("WebM Video", "video/webm"),
        WORDPERFECT("WordPerfect", "application/wordperfect"),
        XBITMAP_IMAGE("XBitmap Image", "image/x-xbitmap"),
        XHTML("XHTML", "application/xhtml+xml"),
        XML("XML", "text/xml"),
        XPIXMAP_IMAGE("XPixmap Image", "image/x-xpixmap"),
        XWINDOW_DUMP("XWindow Dump", "image/x-xwindowdump"),
        Z_COMPRESS("Z Compress", "application/x-compress"),
        ZIP("ZIP", "application/zip"),
        ICALENDAR_FILE("iCalendar File", "text/calendar");

        private String name;
        private String value;

        Mimetype(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }
    }
}
