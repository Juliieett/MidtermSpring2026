# Final Project Report

## Implemented UNO Rules

The final project extends the midterm CLI game toward fuller UNO behavior. The game now supports:

- a full 108-card deck
- legal-play validation by color, number, and action type
- Skip, Reverse, Draw Two, Wild, and Wild Draw Four
- draw-and-maybe-play flow
- UNO call and missed-UNO penalty
- round scoring and play to a target score

See `docs/rules-supported.md` for the full checklist and documented simplifications.

## How To Play From The CLI

Build and run:

```bash
mvn -q -DskipTests package
java -jar target/uno-cli.jar --bots 3 --target 500 --quiet --seed 1
```

Play with a human:

```bash
java -jar target/uno-cli.jar --human --bots 2 --target 500
```

Human commands:

- `0`, `1`, ... card index
- `R5`, `YS`, `W`, ... card code
- `draw` to draw a card
- `uno` to call UNO when down to one card

Use `--games N` for a fixed number of rounds instead of a target score.

## Architecture

Game logic is separated from CLI interaction:

| Area | Classes |
|---|---|
| Rules | `CardRules`, `UnoRules`, `ScoreCalculator` |
| State | `GameState`, `Player`, `DrawPile` |
| Effects | `CardEffectEngine` |
| Orchestration | `GameRunner` |
| CLI input/output | `ConsoleInput`, `ConsoleView`, `HumanMoveParser` |
| Persistence | `persistence.*` from Assignment 5 |

Rule behavior can be tested without the CLI through JUnit tests in `src/test/java/` and the existing characterization suite.

## Tests Added

Final-project rule tests:

- `DeckTests`
- `LegalPlayTests`
- `ActionCardTests`
- `WildTests`
- `DrawPassTests`
- `UnoCallTests`
- `ScoringTargetTests`

Existing checks:

- `CharacterizationTest` runs the 39-check regression suite
- `GamePersistenceTest` covers Assignment 5 persistence

Run all tests:

```bash
mvn test
```

## Limitations

- Wild Draw Four is not restricted to no-matching-color situations
- No Wild Draw Four challenge rule
- No Draw Two stacking
- Bots use a simple priority strategy
- Human players may still draw even when a legal card is available
- All hands are visible in non-quiet CLI mode

These choices keep the project testable and maintainable while covering the main final-project rule menu.
