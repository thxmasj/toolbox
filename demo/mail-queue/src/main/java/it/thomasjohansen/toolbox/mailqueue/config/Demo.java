package it.thomasjohansen.toolbox.mailqueue.config;

import com.hazelcast.core.HazelcastInstance;
import it.thomasjohansen.toolbox.hazelcast.HazelcastBuilder;
import it.thomasjohansen.toolbox.hazelcast.HazelcastClientBuilder;
import it.thomasjohansen.toolbox.mailqueue.MailQueue;
import it.thomasjohansen.toolbox.mailsender.MailSender;
import it.thomasjohansen.toolbox.socket.AvailablePort;
import org.subethamail.wiser.Wiser;

import java.util.Queue;

/**
 * @author thomas@thomasjohansen.it
 */
public class Demo {

    public static void main(String...args) throws InterruptedException {
        int hazelcastPort = AvailablePort.find();
        int smtpPort = AvailablePort.find();
        Wiser wiser = new Wiser();
        wiser.setPort(smtpPort);
        wiser.start();
        HazelcastInstance hazelcast = new HazelcastBuilder()
                .port(hazelcastPort)
                .build();
        MailQueue mailQueue = new MailQueue(
                hazelcast.getQueue("mail-queue"),
                MailSender.builder()
                        .host("localhost")
                        .port(smtpPort)
                        .build()
        );
        HazelcastInstance hazelcastClient = new HazelcastClientBuilder()
                .address("localhost:" + hazelcastPort)
                .build();
        Queue<String> queue = hazelcastClient.getQueue("mail-queue");
        queue.add("test@example.com");
    }
}
