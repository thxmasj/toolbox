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

    public void send(Mail mail) throws InvalidAddress, MessageBuildingFailed, SendingFailed {
        Session session = createSession();
        Message message = createMessage(session, mail);
        try {
            createTransport(session).sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            throw new SendingFailed(e);
        }
    }

    private InternetAddress toInternetAddress(String address) throws InvalidAddress {
        try {
            return new InternetAddress(address);
        } catch (AddressException e) {
            throw new InvalidAddress(address, e);
        }
    }

    private Message createMessage(Session session, Mail mail) throws MessageBuildingFailed {
        MimeMessage msg = new MimeMessage(session);
        Date sentDate = new Date();
        try {
            msg.setContent(mail.getMessage(), "text/plain; charset=UTF-8");
            msg.setFrom(fromAddress);
            InternetAddress[] toAddress = mail.getRecipients().stream()
                    .map(this::toInternetAddress).toArray(InternetAddress[]::new);
            msg.setRecipients(Message.RecipientType.TO, toAddress);
            msg.setSubject(encodeText(mail.getSubject(), "UTF-8", "B"));
            msg.setSentDate(sentDate);
            return msg;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MessageBuildingFailed(e);
        }
    }

    public static class SendingFailed extends RuntimeException {

        public SendingFailed(Throwable cause) {
            super("Failed to send message", cause);
        }

    }

    public static class InvalidAddress extends RuntimeException {

        public InvalidAddress(String address, Throwable cause) {
            super("Invalid address: " + address, cause);
        }

    }

    public static class MessageBuildingFailed extends RuntimeException {

        public MessageBuildingFailed(Throwable cause) {
            super("Failed to build message", cause);
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
