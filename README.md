# Flight Booking API

A small Spring Boot REST API for booking seats on flights. Single instance,
in-memory storage, no authentication, no flight search. Clients book against a
flight number they already know, and flights can never be overbooked.

## Requirements

- Java 17+
- Maven (or the bundled `./mvnw` wrapper)

## How to run

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:8080`.

On startup it seeds a few flights so you have inventory to book against:

| Flight | Seats |
|--------|-------|
| AA100  | 2     |
| BA200  | 150   |
| CA300  | 50    |

API docs (Swagger UI): `http://localhost:8080/swagger-ui.html`

## Endpoints

| Method | Path                                      | Description                     |
|--------|-------------------------------------------|---------------------------------|
| POST   | `/api/flights`                            | Register a flight (inventory)   |
| GET    | `/api/flights/{flightNumber}/availability`| Remaining seats on a flight     |
| POST   | `/api/bookings`                           | Book seats on a flight          |
| DELETE | `/api/bookings/{id}`                      | Cancel a booking (release seats)|

## Example requests

Book a seat (success):

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"flightNumber":"AA100","passengerName":"Alice","seats":1}'
```

```
HTTP/1.1 201 Created
Location: http://localhost:8080/api/bookings/ee5d8a69-9ab6-4243-af0a-f3ab70e38fe8

{
  "bookingId": "ee5d8a69-9ab6-4243-af0a-f3ab70e38fe8",
  "flightNumber": "AA100",
  "passengerName": "Alice",
  "seats": 1,
  "createdAt": "2026-07-07T12:30:00Z"
}
```

Overbooking is rejected once the flight is full:

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"flightNumber":"AA100","passengerName":"Carol","seats":1}'
# HTTP/1.1 409 Conflict
```

Unknown flight:

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"flightNumber":"ZZ999","passengerName":"Dan","seats":1}'
# HTTP/1.1 404 Not Found
```

Invalid request (seats must be >= 1, passenger name required):

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"flightNumber":"AA100","seats":0}'
# HTTP/1.1 400 Bad Request
```

Register a new flight:

```bash
curl -i -X POST http://localhost:8080/api/flights \
  -H 'Content-Type: application/json' \
  -d '{"flightNumber":"DL400","totalSeats":10}'
# HTTP/1.1 201 Created
```

Check availability:

```bash
curl -s http://localhost:8080/api/flights/AA100/availability
# {"flightNumber":"AA100","totalSeats":2,"availableSeats":1}
```

Cancel a booking (releases the seats):

```bash
curl -i -X DELETE http://localhost:8080/api/bookings/{bookingId}
# HTTP/1.1 204 No Content
```

## Running the tests

```bash
./mvnw test
```

Includes a concurrency test proving that parallel bookings never oversell a
flight.
