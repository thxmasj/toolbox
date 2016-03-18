package it.thomasjohansen.toolbox.mailsender;

import org.junit.Test;
import org.subethamail.wiser.Wiser;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class MailSenderTest {

    @Test
    public void test() throws MessagingException, IOException {
        Wiser wiser = new Wiser();
        wiser.setPort(0);
        wiser.start();
        MailSender.builder()
                .from("TestFrom")
                .host("localhost")
                .port(wiser.getServer().getPort())
                .protocol("smtp")
                .build()
                .send(Mail.builder().subject("TestSubject").message("TestBody").recipient("mail@example.com").build());
        assertEquals(1, wiser.getMessages().size());
        MimeMessage message = wiser.getMessages().get(0).getMimeMessage();
        assertEquals("TestBody\r\n", message.getContent());
        assertEquals("TestSubject", message.getSubject());
        assertEquals("TestFrom", message.getFrom()[0].toString());
    }

}
