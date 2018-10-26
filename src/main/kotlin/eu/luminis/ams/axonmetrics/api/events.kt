package eu.luminis.ams.axonmetrics.api

import java.time.ZonedDateTime
import java.util.*

data class FlightPlannedEvent(
        val flightId: UUID,
        val seatsAvailable: Int,
        val name: String,
        val departureTime: ZonedDateTime,
        val arrivalTime: ZonedDateTime,
        val from: String,
        val to: String
) {
    constructor(command: PlanFlightCommand) : this(
            command.flightId,
            command.seatsAvailable,
            command.flightName,
            command.departureTime,
            command.arrivalTime,
            command.from,
            command.to
    )
}

data class SeatBookedEvent (val passengerName: String) {
    constructor(command: BookSeatCommand) : this(command.passengerName)
}