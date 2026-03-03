# Event Management System

A multi-language JVM application for managing events, venues, attendees, and scheduling. Built with **Kotlin** for core business logic, **Scala** for the scheduling algorithm, and **Java Swing** for the graphical user interface.

## Overview

The system allows users to create and manage events, register attendees, assign venues, and generate conflict-free schedules. It demonstrates interoperability across three JVM languages, each used where its strengths are most applicable:

- **Kotlin** — Domain models, business logic, data persistence, validation, and unit tests
- **Scala** — Greedy scheduling algorithm using functional programming (immutable state, pattern matching, `foldLeft`)
- **Java** — Swing-based GUI with form panels for events, attendees, venues, and statistics

## Architecture

```
src/
├── Main.kt                          # Application entry point
├── LogicHandling/                   # Kotlin — Core business logic
│   ├── Event.kt                     # Event data model
│   ├── Attendee.kt                  # Attendee data model
│   ├── Venue.kt                     # Venue data model
│   ├── DataPersistence.kt           # JSON serialisation (Gson)
│   └── HandlingServices/
│       ├── EventManager.kt          # Facade — single entry point for all operations
│       ├── EventHandler.kt          # Event CRUD operations
│       ├── AttendeeService.kt       # Attendee management
│       ├── VenueHandler.kt          # Venue management
│       ├── RegistrationHandler.kt   # Event-attendee registration
│       ├── ValidationHandler.kt     # Input validation
│       └── StatisticsHandler.kt     # Aggregate statistics
├── scala/                           # Scala — Scheduling
│   ├── SchedulingAlgorithm.scala    # Greedy venue-assignment algorithm
│   └── SlotFinder.scala             # Time slot availability
├── userinterface/                   # Java — Swing GUI
│   ├── MainApplicationWindow.java   # Main frame with tabbed panels
│   ├── EventPanel.java              # Event management panel
│   ├── AttendeePanel.java           # Attendee management panel
│   ├── VenuePanel.java              # Venue management panel
│   └── StatisticsPanel.java         # Statistics and scheduling display
└── unitTests/                       # Kotlin + Scala unit tests
    ├── LogicHandling/               # Kotlin tests (JUnit 5)
    └── scala/                       # Scala tests
data/
├── events.json                      # Persisted event data
├── attendees.json                   # Persisted attendee data
└── venues.json                      # Persisted venue data
```

## Scheduling Algorithm

The Scala scheduling component implements a **greedy best-fit algorithm**:

1. Events are sorted by start time (earliest first)
2. For each event, venues are filtered by capacity and sorted by best fit (smallest suitable venue)
3. The first venue without a time conflict is assigned
4. Unassignable events and conflicts are tracked and reported

The algorithm uses immutable state threading via `foldLeft`, processing each event through a `SchedulingState` accumulator that tracks assignments, venue bookings, and conflicts — a purely functional approach with no mutable variables.

## Key Features

- **Event Management** — Create, update, and delete events with time, capacity, and venue details
- **Attendee Registration** — Register attendees to events with validation and capacity checks
- **Venue Management** — Add and manage venues with capacity and location information
- **Conflict-Free Scheduling** — Automatically assign events to venues without time overlaps
- **Data Persistence** — JSON-based storage using Gson, with auto-save on application exit
- **Statistics Dashboard** — Aggregate views of events, attendees, and venue utilisation
- **Input Validation** — Comprehensive validation for all user inputs

## Requirements

- **JDK 17**
- **Scala 3.1.3**
- **Gson 2.10.1** — JSON serialisation
- **JUnit 4.13.1 / JUnit 5.14.0** — Unit testing

## Setup

1. Clone the repository
2. Open in IntelliJ IDEA
3. Go to **File > Invalidate Caches... > Invalidate and Restart**
4. Add Gson and JUnit libraries via **File > Project Structure > Libraries > From Maven...**
   - `com.google.code.gson:gson:2.10.1`
   - `junit:junit:4.13.1`
   - `org.junit.jupiter:junit-jupiter:5.14.0`
5. Build and run (`Main.kt`)

## Testing

Unit tests cover domain models, business logic services, and the scheduling algorithm:

- **Kotlin tests** (`unitTests/LogicHandling/`) — Event, Attendee, Venue models and all handler services
- **Scala tests** (`unitTests/scala/`) — Scheduling algorithm and slot finder logic
