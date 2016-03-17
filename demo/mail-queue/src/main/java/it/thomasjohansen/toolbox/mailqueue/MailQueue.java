package it.thomasjohansen.toolbox.mailqueue;

import com.google.common.util.concurrent.RateLimiter;
import com.hazelcast.core.HazelcastInstance;
import it.thomasjohansen.toolbox.mailsender.MailSender;

import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.singletonList;

/**
 * @author thomas@thomasjohansen.it
 */
public class MailQueue {

    private final BlockingQueue<String> queue;
    private final MailSender sender;
    private final ExecutorService executor;

    public MailQueue(
            BlockingQueue<String> queue,
            MailSender mailSender
    ) {
        this.queue = queue;
        this.sender = mailSender;
        this.executor = Executors.newSingleThreadExecutor();
        this.executor.submit(this::run);
    }

    private void run() {
        while (true) {
            try {
                String recipient = queue.take();
                sender.send("Queue demo", "Ipsum lorem...", singletonList(recipient));
            } catch (InterruptedException e) {
            }
        }
    }

}
