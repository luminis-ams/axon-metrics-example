package eu.luminis.ams.axonmetrics.commandmodel;


import eu.luminis.ams.axonmetrics.api.BookSeatCommand;
import eu.luminis.ams.axonmetrics.api.FlightPlannedEvent;
import eu.luminis.ams.axonmetrics.api.PlanFlightCommand;
import eu.luminis.ams.axonmetrics.api.SeatBookedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Flight {

    @AggregateIdentifier
    private UUID flightId;

    private int availableSeats;

    private Flight() {
    }

    @CommandHandler
    public Flight(PlanFlightCommand command){
        apply(new FlightPlannedEvent(command));
    }

    @EventSourcingHandler
    public void handle(FlightPlannedEvent event){
        flightId = event.getFlightId();
        availableSeats = event.getSeatsAvailable();
    }

    @CommandHandler
    public void handle(BookSeatCommand command){
        Assert.isTrue(availableSeats > 0, "No seats available");

        apply(new SeatBookedEvent(command));
    }

    @EventSourcingHandler
    public void handle(SeatBookedEvent event){
        availableSeats--;
    }


}
