package eu.luminis.ams.axonmetrics;

import eu.luminis.ams.axonmetrics.api.BookSeatCommand;
import eu.luminis.ams.axonmetrics.api.PlanFlightCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

@Component
@ConditionalOnProperty(value = "enableFlightGenerator", havingValue = "true")
public class FlightDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger( FlightDataGenerator.class );

    private final CommandGateway commandGateway;
    private final Random random = new Random();

    public FlightDataGenerator(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Scheduled(fixedRate = 5000)
    public void generateFlights() {
        UUID flightId = UUID.randomUUID();
        PlanFlightCommand command = new PlanFlightCommand(flightId, 10, "KL " + random.nextInt(1000),
                                                          ZonedDateTime.now(ZoneId.of("Europe/Amsterdam")),
                                                          ZonedDateTime.now(ZoneId.of("Europe/London")).plusHours(2),
                                                          "AMS", "LHR");

        commandGateway.sendAndWait(command);

        boolean overbooking = random.nextBoolean();
        int numSeatsToBook = overbooking ? 11 : 10;
        for (int i = 1; i <= numSeatsToBook; i++) {
            try{
                commandGateway.sendAndWait(new BookSeatCommand(flightId,"Passenger_ " + i));
            } catch (IllegalArgumentException e){
                LOGGER.warn("Failed to book seat");
            }
        }
    }
}
