# Database And Persistence

Assignment 5 adds JPA persistence for UNO game history using **H2** and **Hibernate (JPA)**.

## Selected Tools

| Component | Choice |
|---|---|
| Database | H2 file database |
| ORM | JPA with Hibernate |
| Access layer | Repository classes + persistence services |

## Schema

Tables:

- `players` — player names
- `games` — session start/end timestamps, rounds played, final winner
- `rounds` — per-round winner, winner points, round end timestamp
- `scores` — per-player total score for each game session

Reference DDL: `src/main/resources/schema.sql`

Hibernate also creates/updates tables automatically with `hibernate.hbm2ddl.auto=update`.

## Configuration

Database connection is configured through environment variables:

| Variable | Default |
|---|---|
| `UNO_DB_URL` | `jdbc:h2:file:./data/uno` |
| `UNO_DB_USER` | `sa` |
| `UNO_DB_PASSWORD` | *(empty)* |

Credentials are **not** stored in source code.

For tests, persistence uses an isolated in-memory database:

```text
jdbc:h2:mem:uno_persistence_test;DB_CLOSE_DELAY=-1
```

## What Gets Persisted

After each CLI session, the application stores:

- player names
- session start and end timestamps
- number of rounds played
- per-round winner and winner points
- per-player total scores
- final session winner (highest total score)

## Query / Report Commands

Run these after playing at least one session:

```bash
java -jar target/uno-cli.jar --recent-games
java -jar target/uno-cli.jar --player-wins
java -jar target/uno-cli.jar --high-scores
```

Limit report output:

```bash
java -jar target/uno-cli.jar --recent-games --report-limit 5
java -jar target/uno-cli.jar --high-scores --report-limit 5
```

Show all three reports:

```bash
java -jar target/uno-cli.jar --recent-games --player-wins --high-scores
```

## Persistence Tests

```bash
mvn test
```

Persistence tests live in:

```text
src/test/java/persistence/GamePersistenceTest.java
```

They use an in-memory H2 database and do not depend on a developer's local `./data/uno` file.

Run only persistence tests:

```bash
mvn -Dtest=GamePersistenceTest test
```

## Typical Workflow

1. Play a session and persist results:

```bash
mvn -q -DskipTests package
java -jar target/uno-cli.jar --bots 3 --games 2 --quiet --seed 1
```

2. View history:

```bash
java -jar target/uno-cli.jar --recent-games --player-wins --high-scores
```

The database files are stored under `./data/` by default.

## Project Structure

```text
src/main/java/persistence/
  PersistenceConfig.java
  entity/          JPA entities
  repository/      DAO/repository classes
  service/         save + statistics services
src/main/resources/
  META-INF/persistence.xml
  schema.sql
src/test/java/persistence/
  GamePersistenceTest.java
```

Game logic does not contain raw SQL. Queries are expressed through JPA repositories and services.
