package eu.luminis.ams.axonmetrics.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.springframework.util.Assert
import java.time.ZonedDateTime
import java.util.*

data class PlanFlightCommand (
        @TargetAggregateIdentifier val flightId: UUID,
        val seatsAvailable: Int,
        val flightName: String,
        val departureTime: ZonedDateTime,
        val arrivalTime: ZonedDateTime,
        val from: String,
        val to: String
) {
    init {
        Assert.isTrue(seatsAvailable > 0, "Seats available must be larger than zero");
    }
}

data class BookSeatCommand (
        @TargetAggregateIdentifier val flightId: UUID,
        val passengerName : String
)