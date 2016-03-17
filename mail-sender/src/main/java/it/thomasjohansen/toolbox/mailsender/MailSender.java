package it.thomasjohansen.toolbox.mailsender;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static javax.mail.internet.MimeUtility.encodeText;

public class MailSender {

    private String protocol;
    private String host;
    private String userName;
    private String password;
    private InternetAddress fromAddress;
    private boolean enableStartTls;
    private int port;

    public void send(String subject, String message, List<String> recipients) {
        Session session = createSession();
        MimeMessage msg = new MimeMessage(session);
        final Date sentDate = new Date();
        InternetAddress[] toAddress = recipients.stream().map(this::toAddress).toArray(size -> new InternetAddress[size]);

        try {
            msg.setText(message, "UTF-8");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to set Body", e);
        }
        try {
            msg.setFrom(fromAddress);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to set From", e);
        }
        try {
            msg.setRecipients(Message.RecipientType.TO, toAddress);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to set To", e);
        }
        try {
            msg.setSubject(encodeText(subject, "UTF-8", "B"));
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to set Subject", e);
        }
        try {
            msg.setSentDate(sentDate);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to set Date", e);
        }
        try {
            createTransport(session).sendMessage(msg, toAddress);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    private InternetAddress toAddress(String recipient) {
        try {
            return new InternetAddress(recipient);
        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid recipient");
        }
    }

    private Session createSession() {
        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", Integer.toString(port));
        properties.setProperty("mail.smtp.starttls.enable", Boolean.toString(enableStartTls));
        properties.setProperty("mail.smtp.auth", Boolean.toString(userName != null));
        return Session.getInstance(properties, authenticator());
    }

    private Authenticator authenticator() {
        if (userName == null) {
            return new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            };
        } else
            return null;
    }


    private Transport createTransport(final Session session) throws MessagingException {
        Transport transport = session.getTransport(protocol);
        if (userName != null)
            transport.connect(userName, password);
        else
            transport.connect();
        return transport;

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MailSender instance = new MailSender();

        public Builder protocol(String protocol) {
            instance.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            instance.host = host;
            return this;
        }

        public Builder port(int port) {
            instance.port = port;
            return this;
        }

        public Builder from(String from) {
            try {
                instance.fromAddress = new InternetAddress(from);
            } catch (AddressException e) {
                throw new IllegalArgumentException("from", e);
            }
            return this;
        }

        public Builder authenticate(String userName, String password) {
            instance.userName = userName;
            instance.password = password;
            return this;
        }

        public Builder enableStartTls() {
            instance.enableStartTls = true;
            return this;
        }

        public MailSender build() {
            if (instance.protocol == null) instance.protocol = "smtp";
            try {
                return instance;
            } finally {
                instance = null;
            }
        }
    }

}
