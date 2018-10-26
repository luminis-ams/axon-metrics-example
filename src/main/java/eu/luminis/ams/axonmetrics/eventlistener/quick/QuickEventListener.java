package eu.luminis.ams.axonmetrics.eventlistener.quick;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class QuickEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger( QuickEventListener.class );

    private final Random random = new Random();

    @EventHandler
    public void handle(Message<?> message) throws InterruptedException {
        Thread.sleep(random.nextInt(25));
        LOGGER.info("Processed message {}", message);
    }
}
