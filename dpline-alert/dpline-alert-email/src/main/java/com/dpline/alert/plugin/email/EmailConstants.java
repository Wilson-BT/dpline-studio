

package com.dpline.alert.plugin.email;

public final class EmailConstants {
    public static final String XLS_FILE_PATH = "xls.file.path";

    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    public static final int NUMBER_1000 = 1000;

    public static final String TR = "<tr>";

    public static final String TD = "<td>";

    public static final String TD_END = "</td>";

    public static final String TR_END = "</tr>";

    public static final String TH = "<th>";

    public static final String TH_END = "</th>";

    public static final String HTML_HEADER_PREFIX = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>"
        + "<html>"
        + "<head>"
        + "<title>dpline</title>"
        + "<meta name='Keywords' content=''>"
        + "<meta name='Description' content=''>"
        + "<style type=\"text/css\">"
        + "table {margin-top:0px;padding-top:0px;border:1px solid;font-size: 14px;color: #333333;border-width: 1px;border-color: #666666;border-collapse: collapse;}"
        + "table th {border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;text-align: left;}"
        + "table td {border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;text-align: left;}"
        + "</style>"
        + "</head>"
        + "<body style=\"margin:0;padding:0\"><table border=\"1px\" cellpadding=\"5px\" cellspacing=\"-10px\"> ";

    public static final String TABLE_BODY_HTML_TAIL = "</table></body></html>";

    public static final String UTF_8 = "UTF-8";

    public static final String EXCEL_SUFFIX_XLSX = ".xlsx";

    public static final String SINGLE_SLASH = "/";

    private EmailConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
