# UNO CLI

A CLI UNO-like game built with Maven. The project supports local build/test/run, packaged execution, logging, and Docker.

## Prerequisites

- Java 17 or later
- Apache Maven 3.9+ **or** use the included Maven Wrapper (`mvnw` / `mvnw.cmd`)
- Docker (optional, for container runs)

## Local Build

```bash
mvn compile
```

On Windows without Maven installed:

```powershell
.\mvnw.cmd compile
```

Or use the helper script:

```bash
scripts/compile.sh
```

## Local Test

```bash
mvn test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Or:

```bash
scripts/test.sh
```

Tests run the existing 39 characterization checks through JUnit. You can also run them directly:

```bash
mvn -q -DskipTests package
java -jar target/uno-cli.jar --self-test
```

## Local Run

Package and run bot games:

```bash
mvn -q -DskipTests package
java -jar target/uno-cli.jar --bots 3 --games 5 --quiet --seed 1
```

Run an interactive game with one human and two bots:

```bash
java -jar target/uno-cli.jar --human --bots 2 --games 1
```

Or use the helper script:

```bash
scripts/run.sh --bots 3 --games 5 --quiet
```

Card input examples:

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
```

## Package Creation

```bash
mvn package
```

This creates `target/uno-cli.jar` with `Main` as the entry point.

To build without running tests:

```bash
mvn -DskipTests package
```

## Docker Build

```bash
docker build -t uno-cli .
```

## Docker Run

Run a quiet bot game:

```bash
docker run --rm uno-cli --bots 3 --games 1 --quiet --seed 1
```

Run with custom arguments:

```bash
docker run --rm uno-cli --bots 2 --games 3 --quiet
```

For an interactive human game, attach stdin:

```bash
docker run --rm -it uno-cli --human --bots 2 --games 1
```

## Logging

The game uses `java.util.logging` for important events:

- session and game start
- player turns
- cards played and drawn
- invalid input
- round and session end

Logs go to stderr and do not replace normal CLI output for players.

## Project Layout

```text
src/main/java/     application source
src/main/resources/logging.properties
src/test/java/     JUnit tests
pom.xml            Maven build configuration
mvnw / mvnw.cmd    Maven Wrapper
Dockerfile         container build
scripts/           compile, test, and run helpers
docs/              rules, reports, and assignment materials
```

## Rules

See `docs/rules.html` for the implemented game rules.

## Midterm Materials

- `docs/midterm-exam.md`: midterm brief
- `docs/rubric.md`: grading rubric
- `docs/refactoring-guide.md`: suggested refactoring path
- `docs/refactoring-report.md`: refactoring report
- `docs/extension-readiness.md`: extension readiness note
