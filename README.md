# Football World Cup Score Board Library

## How it works

Step by step game state is updated and stored:

1. User interacts with a library via `ScoreBoardService` interface.
    1. process an event (`GameOpenedEvent`, `GoalEvent`, `GameClosedEvent`)
    2. get list of stored games, ordered based on total number of goal and time of creation
    3. get a state of a specific game
2. After processing start event is handled by a `SportEventProcessor` that handles game modifying events according to a behavioral `Visitor` pattern.
3. Every event has its own handler, that based on an event prepares an update of a game state.
4. Game update is returned back to `SportEventProcessor` that performs an actual save to a game state store `GameStateRepository`.
5. Updated game is returned to user after every successful processing.

## How to use it

Create instance of `ScoreBoardService`

```java
final var service = ScoreBoardServiceFactory.getInstance();
```

Create a `GameOpenedEvent` event and process it, returned game represents current snapshot of your game.

```java
final var gameStartEvent = GameOpenedEvent.builder()
    .withGameKey(GameKey.builder()
        .withTeam1(new Team("PL"))
        .withTeam2(new Team("GER"))
        .build())
    .build();

final var gamePLvGER = service.process(gameStartEvent).get();
```

To update score of your game use `GoalEvent`, make sure to use game key from game snapshot

```java
final var goalScoredEvent = GoalEvent.builder()
    .withGameKey(gamePLvGER.key())
    .withScoringTeam(gamePLvGER.key().team1())
    .build();

final var gamePLvGERUpdated = service.process(goalScoredEvent).get();
```

to check for all current games use

```java
final var gameStates = service.getAll();
```

to remove a game use

```java
final var gameClosedEvent = GameClosedEvent.builder()
    .withGameKey(gamePLvGER.key())
    .build();

// should be empty Optional
final var removed = service.process(goalScoredEvent);
```

## Decisions

1. Usage of `Visitor` pattern and division of handling of particular event to separate sub-processor aimed toward splitting main processing logic
   into easily testable units, that can be used, maintained and tested independently.
2. Throwing over logging, as it is a library it should throw as to allow user to handle those situations as they please.
3. Package structure was derived from Domain Driven Architecture.
    - `domain` contains actual business logic
    - `infrastrcture` contains simple read/write to a datasource without considering business logic
    - `interface.incoming` interfaces used to interact with a domain from outside (e.g. by a user)
    - `interface.outgoing` everything used by a domain that is not related to a business logic (e.g. save to a datastore)
4. All Collections used by a game state classes are stored as immutable to prevent possibility of manipulating state of game objects by their
   reference.
5. Usage of `lombok` is a no-brainer that allows for removal of boilerplate code from a project.
