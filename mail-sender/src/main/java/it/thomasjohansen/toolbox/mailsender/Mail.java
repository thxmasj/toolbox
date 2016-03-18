package it.thomasjohansen.toolbox.mailsender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mail {

    private List<String> recipients = new ArrayList<>();
    private String message;
    private String subject;

    public List<String> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Mail instance = new Mail();

        public Builder recipient(String recipient) {
            instance.recipients.add(recipient);
            return this;
        }

        public Builder message(String message) {
            instance.message = message;
            return this;
        }

        public Builder subject(String subject) {
            instance.subject = subject;
            return this;
        }

        public Mail build() {
            try {
                return instance;
            } finally {
                instance = null;
            }
        }

    }

}
