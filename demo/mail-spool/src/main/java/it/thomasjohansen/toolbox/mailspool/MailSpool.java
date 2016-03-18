package it.thomasjohansen.toolbox.mailspool;

import com.google.common.util.concurrent.RateLimiter;
import it.thomasjohansen.toolbox.mailsender.Mail;
import it.thomasjohansen.toolbox.mailsender.MailSender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author thomas@thomasjohansen.it
 */
public class MailSpool<T> {

    private BlockingQueue<T> queue;
    private MailSender sender;
    private final ExecutorService executor = newSingleThreadExecutor();
    private RateLimiter rateLimiter;
    private Function<T, Mail> mailBuilder;

    private MailSpool() {
        // Use builder
    }

    private void run() {
        while (true) {
            rateLimiter.acquire();
            try {
                T data = queue.take();
                Mail mail = mailBuilder.apply(data);
                sender.send(mail);
            } catch (InterruptedException e) {
            }
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private MailSpool<T> instance = new MailSpool<>();

        public Builder<T> queue(BlockingQueue<T> queue) {
            instance.queue = queue;
            return this;
        }

        public Builder<T> sender(MailSender sender) {
            instance.sender = sender;
            return this;
        }

        public Builder<T> perSecondRate(double perSecondRate) {
            instance.rateLimiter = RateLimiter.create(perSecondRate);
            return this;
        }

        public Builder<T> mailBuilder(Function<T, Mail> mailBuilder) {
            instance.mailBuilder = mailBuilder;
            return this;
        }

        public MailSpool<T> build() {
            if (instance.queue == null) throw new IllegalArgumentException("queue");
            if (instance.sender == null) throw new IllegalArgumentException("sender");
            if (instance.mailBuilder == null) throw new IllegalArgumentException("mailBuilder");
            instance.executor.submit(instance::run);
            try {
                return instance;
            } finally {
                instance = null;
            }
        }

    }

}
