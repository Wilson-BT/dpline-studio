

package com.dpline.alert.plugin.email;

public final class MailParamsConstants {

    public static final String PLUGIN_DEFAULT_EMAIL_RECEIVERS = "$t('receivers')";
    public static final String NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERS = "receivers";

    public static final String PLUGIN_DEFAULT_EMAIL_RECEIVERCCS = "$t('receiverCcs')";
    public static final String NAME_PLUGIN_DEFAULT_EMAIL_RECEIVERCCS = "receiverCcs";

    public static final String MAIL_PROTOCOL = "transport.protocol";
    public static final String NAME_MAIL_PROTOCOL = "mail.protocol";

    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String NAME_MAIL_SMTP_HOST = "serverHost";

    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String NAME_MAIL_SMTP_PORT = "serverPort";

    public static final String MAIL_SENDER = "mail.sender";
    public static final String NAME_MAIL_SENDER = "sender";

    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String NAME_MAIL_SMTP_AUTH = "enableSmtpAuth";

    public static final String MAIL_USER = "mail.user";
    public static final String NAME_MAIL_USER = "user";

    public static final String MAIL_PASSWD = "mail.passwd";
    public static final String NAME_MAIL_PASSWD = "passwd";

    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String NAME_MAIL_SMTP_STARTTLS_ENABLE = "starttlsEnable";

    public static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    public static final String NAME_MAIL_SMTP_SSL_ENABLE = "sslEnable";

    public static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
    public static final String NAME_MAIL_SMTP_SSL_TRUST = "smtpSslTrust";

    private MailParamsConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
