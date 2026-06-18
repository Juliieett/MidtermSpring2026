# Refactoring Report

## Summary

This project started as a working UNO-like CLI game in one large `Main` class. My goal was not to rewrite the game, but to make the current behavior safer to change. I expanded the characterization checks, then refactored rule logic, scoring, bot decisions, action effects, deck creation, player data, game running, console input, console output, and human command parsing into clearer responsibilities.

The CLI behavior is intentionally preserved. The project still compiles with plain `javac`, still runs with `java -cp out Main`, and the existing `--self-test` path now runs a larger set of characterization checks.

## Characterization Tests Added

The original self-test had 9 checks. I expanded it to 39 checks, now located in `CharacterizationTests`. These tests describe the current implementation, including simplified UNO behavior and known quirks.

The tests cover:

- card parsing for `R5`, `G+2`, `YS`, `BR`, `W`, and `W4`
- scoring for number cards, skip, reverse, draw two, wild, wild draw four, and winner scoring from remaining hands
- legal play by color, number, action type, draw two, reverse, wild, wild draw four, called color, and illegal mismatches
- bot behavior, including normal-card-before-wild behavior, draw-two preference, skip preference, and choosing the color with the most cards
- draw pile behavior, including reshuffling discard into the deck, emptying the discard pile, and returning fallback card `W` when both piles are empty
- turn and action effects, including clockwise movement, counterclockwise wrapping, skip, reverse, draw two, and wild draw four
- human input parsing for `draw`, numeric index commands, card-code commands, and unknown input

These checks made it safer to extract scoring into `ScoreCalculator`, legal rules into `CardRules`, bot choices into `BotStrategy`, match state into `GameState`, drawing into `DrawPile`, action effects into `CardEffectEngine`, and command parsing into `HumanMoveParser`. Turn, draw-pile, and effect tests now call those extracted pieces directly instead of going only through `GameRunner`.

## Worst Design Problems Found

The starting code had the expected design smells from the assignment:

- one large `Main` class
- global mutable state
- long game loop
- mixed CLI and game logic
- duplicated legality checks
- primitive-heavy card representation
- if-heavy action handling
- hidden randomness
- weak player boundaries
- parsing mixed with validation
- scoring mixed with game completion
- bot decisions mixed with rule knowledge

The most important problems to address first were duplicated legal-play logic, scoring inside the win condition, bot decisions inside `Main`, action effects inside the main loop, and human input parsing mixed with validation.

## Refactorings Performed

I extracted small responsibilities from the original procedural design:

- `CardRules` now owns card color detection, rank detection, number extraction, and legal-play checking.
- `ScoreCalculator` now owns card point values and winner scoring from remaining hands.
- `BotStrategy` now owns bot card choice and wild color choice while reusing `CardRules.isLegal()`.
- `HumanMoveParser` and `HumanMove` now represent parsed human commands such as draw, index, card code, and not found.
- `DeckFactory` now builds and shuffles the simplified UNO deck using the existing seeded `Random`.
- `Player` now groups name, human/bot flag, hand, and score instead of using parallel collections.
- `CharacterizationTests` now owns the self-test suite instead of keeping it in `Main`.
- `GameState` now owns mutable match state: deck, discard, current player, direction, up card, and called color.
- `DrawPile` now owns drawing and discard-pile reshuffling.
- `CardEffectEngine` now owns skip, reverse, draw two, and wild draw four turn effects.
- `GameRunner` now coordinates setup, turn orchestration, win detection, and score updates without owning all runtime state directly.
- `ConsoleView` now owns console output, quiet-mode suppression, event messages, prompts, and final score printing.
- `ConsoleInput` now owns `Scanner` usage and asks for human moves, drawn-card choices, and wild colors.

I also moved action-card behavior into `CardEffectEngine` and drawing logic into `DrawPile`. `Main` now mostly handles command-line options, player setup, game creation, running games, final scores, and delegating self-tests.

Compatibility wrappers such as `isLegal()`, `color()`, `rank()`, `number()`, and `points()` were kept in `Main` while delegating to extracted classes. This kept the refactoring incremental and reduced the risk of breaking call sites.

## Expected Smells Checklist

Most expected smells were improved:

- The original large `Main` class was reduced by moving rules, scoring, bot decisions, parsing, deck creation, player data, tests, game running, input, and output into separate classes.
- Global mutable state was reduced by grouping player data in `Player` and moving game state into `GameRunner`.
- The long game loop was moved out of `Main` and supported by extracted helper methods and classes.
- CLI logic was separated into `ConsoleInput` and `ConsoleView`, while rules no longer depend on console input/output.
- Duplicated legal-play logic was centralized in `CardRules.isLegal()`.
- Scoring was separated from game completion through `ScoreCalculator`.
- Bot decisions were separated from rule knowledge through `BotStrategy`.
- Hidden randomness was reduced by moving deck construction to `DeckFactory` while preserving the existing `--seed` behavior.

Some limitations remain intentionally:

- Cards are still represented as strings like `R5`, `GS`, `G+2`, `W`, and `W4` to preserve CLI compatibility.
- `GameRunner` still coordinates turn orchestration even though match state and effects now live in smaller objects.
- `CardEffectEngine.apply()` still uses conditionals instead of polymorphic card effects because the project is small and behavior preservation was more important than adding a larger hierarchy.
- `ConsoleInput` still validates card-code legality before returning a selected card to preserve the original behavior.

## Refactoring Guide Checklist

I followed the suggested refactoring path:

1. Compiled and ran the game with `javac -d out src/*.java`.
2. Ran the characterization checks through the existing self-test path.
3. Expanded the self-test from 9 checks to 39 checks before and during refactoring.
4. Extracted methods such as `applyCardEffect()` and `drawCardsForCurrentPlayer()`.
5. Separated command parsing into `HumanMoveParser` and console prompting into `ConsoleInput`.
6. Centralized legal-play rules in `CardRules.isLegal()`.
7. Isolated card effects in `CardEffectEngine`.
8. Did not implement a new extension because the midterm brief says the extension does not need to be implemented. `docs/extension-readiness.md` instead explains the prepared smarter-bot extension point.

The main refactorings used were Extract Method, Extract Class, Move Method, Split Phase, and a light Introduce Parameter Object through `HumanMove`. Replace Conditional with Polymorphism was not used because the current action-card conditional is small enough for this project.

## Files Changed

- `src/Main.java`: command-line setup, player setup, game creation, final scores, and self-test delegation
- `src/CardRules.java`: card parsing and legal-play rules
- `src/ScoreCalculator.java`: card points and winner scoring
- `src/BotStrategy.java`: bot card choice and wild color choice
- `src/HumanMoveParser.java`: human command parsing
- `src/DeckFactory.java`: deck creation and shuffling
- `src/Player.java`: player name, human/bot flag, hand, and score
- `src/CharacterizationTests.java`: 39 characterization checks
- `src/GameState.java`: mutable match state and turn movement
- `src/DrawPile.java`: deck drawing and discard reshuffling
- `src/CardEffectEngine.java`: skip, reverse, draw two, and wild draw four effects
- `src/GameRunner.java`: turn orchestration, win detection, and score updates
- `src/ConsoleView.java`: console output and quiet-mode behavior
- `src/ConsoleInput.java`: scanner-based input prompts
- `docs/refactoring-report.md`: this report
- `docs/extension-readiness.md`: smarter-bot extension readiness note

## Behavior Intentionally Preserved

No gameplay behavior was intentionally changed. The refactoring preserves:

- compact card codes like `R5`, `YS`, `BR`, `G+2`, `W`, and `W4`
- visible hands during non-quiet games
- humans being allowed to type `draw` even when they have a legal card
- bots automatically playing a drawn card when it is legal
- wild and wild draw four always being legal
- called color after a wild affecting legal colored cards
- draw two and wild draw four making the next player draw cards and lose their turn
- reverse with two players behaving like a skip because of the existing turn logic
- the deck refilling from the discard pile when empty
- fallback card `W` when both deck and discard are empty
- the same command-line options, including `--self-test`, `--quiet`, and `--seed`

Any future gameplay changes should be made separately and covered by new tests.

## Rubric Match

The work matches the rubric by adding meaningful characterization tests, refactoring incrementally, improving design boundaries, keeping the solution small, and documenting the remaining risks. The code now has clearer homes for rules, scoring, bot strategy, parsing, deck creation, player data, game execution, console input, and console output without adding a large framework or dependency.

## Verification

I verified the project with:

```powershell
javac -d out src/*.java
java -cp out Main --self-test
```

The result was:

```text
Passed 39 characterization checks.
```

I also ran:

```powershell
java -cp out Main --bots 3 --games 1 --quiet --seed 1
```

The game completed successfully and printed final scores.

The 39 characterization checks cover: `color R5`, `rank +2`, `rank skip`, `rank reverse`, `rank wild`, `rank wild draw four`, `number points`, `skip points`, `reverse points`, `draw two points`, `wild points`, `wild draw four points`, `winner scores other hands`, `same color`, `same number`, `same skip action`, `same draw two action`, `same reverse action`, `wild is always legal`, `wild draw four is always legal`, `called color`, `illegal mismatch`, `bot normal before wild`, `bot prefers draw two`, `bot prefers skip before number`, `bot color`, `parse draw command`, `parse index command`, `parse card code command`, `parse missing command`, `draw reshuffles discard`, `discard empty after reshuffle`, `draw fallback wild`, `next clockwise`, `next wraps counterclockwise`, `skip advances past next player`, `reverse flips direction`, `draw two hits next player`, and `wild draw four hits next player`.

## Remaining Risks

`GameRunner` now coordinates components instead of owning all match state directly, but it still handles dealing, asking for moves, applying moves, checking wins, and advancing turns in one loop. Console rendering and scanner input are outside `GameRunner`, and state, drawing, and effects are extracted, but turn orchestration could be split further later.

The tests are still custom self-tests instead of JUnit tests. This avoids adding dependencies, but a real test framework would provide clearer test names and better failure reporting.

The card representation is still string-based. This preserved compatibility with the original CLI, but a future `Card` value object could make invalid card states harder to create.
