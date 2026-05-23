## Summary

This project started as a working UNO-like CLI game in one large `Main` class. My goal was not to rewrite the game, but to make the current behavior safer to change. I first expanded the characterization checks, then refactored rule logic, scoring, bot decisions, action effects, and human command parsing into clearer responsibilities.

The CLI behavior is intentionally preserved. The project still compiles with plain `javac`, still runs with `java -cp out Main`, and the existing `--self-test` path now runs a larger set of characterization checks.

## Characterization Tests Added

The original self-test had 9 checks. I expanded it to 39 checks in `Main.selfTest()`. These tests describe the current implementation, including its simplified UNO behavior and known quirks.

### Card Parsing Tests

These tests protect the primitive string card format used by the program:

- `R5` is recognized as red.
- `G+2` is recognized as draw two.
- `YS` is recognized as skip.
- `BR` is recognized as reverse.
- `W` is recognized as wild.
- `W4` is recognized as wild draw four.

Why this matters: the whole game still uses compact string codes, so parsing behavior must stay stable during refactoring.

### Scoring Tests

The tests check the implemented score values:

- number cards score their face value, such as `R7 = 7`
- skip cards score `20`
- reverse cards score `20`
- draw two cards score `20`
- wild cards score `50`
- wild draw four cards score `50`
- a winner scores the total value of cards left in the other players' hands

Why this matters: scoring was previously mixed into the win condition inside the main game loop. The tests made it safe to extract scoring into `ScoreCalculator`.

### Legal Play Tests

The tests cover the main legal-play rules:

- same color is legal, such as `R2` on `R9`
- same number is legal, such as `G9` on `R9`
- same action type is legal, such as `GS` on `RS`
- draw two can be played on draw two, such as `G+2` on `R+2`
- reverse can be played on reverse, such as `GR` on `RR`
- `W` is always legal
- `W4` is always legal
- called color after a wild is respected, such as `B3` when blue was called
- unrelated cards remain illegal, such as `B3` on `R9`

Why this matters: legal-play logic was duplicated in the game loop and bot logic. These tests allowed the duplicated conditions to be replaced by one shared rule method.

### Bot Behavior Tests

The tests document the current bot strategy:

- bot plays a normal legal card before using a wild when possible
- bot prefers draw two before other legal cards
- bot prefers skip before number cards
- bot chooses the color it has the most cards of

Why this matters: the assignment asks us to preserve existing behavior, not invent a smarter bot immediately. These tests protect the old bot priority order while moving it into `BotStrategy`.

### Draw Pile Tests

The tests cover edge cases in drawing:

- when the deck is empty, the discard pile is moved back into the deck
- after reshuffling the discard pile, the discard pile becomes empty
- if both deck and discard are empty, drawing returns fallback card `W`

Why this matters: the fallback wild is a documented quirk of the simplified implementation and should not disappear accidentally.

### Turn And Action Effect Tests

The tests cover turn movement and action effects:

- normal `next()` movement advances clockwise
- counterclockwise movement wraps around correctly
- skip advances past the next player
- reverse flips direction
- draw two makes the next player draw two cards and lose their turn
- wild draw four makes the next player draw four cards and lose their turn

Why this matters: action-card behavior was previously embedded directly inside the long game loop. These tests made it safer to extract action effect handling into a separate method.

### Human Input Parsing Tests

The tests cover human command parsing:

- `draw` is parsed as a draw command
- a valid number is parsed as an index command
- a matching card code is parsed as a card-code command
- unknown input is parsed as not found

Why this matters: parsing user input was mixed with validation and console prompting. Extracting parsing makes this part easier to test without running an interactive game.

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

The most important issues to fix first were duplicated legal-play logic, scoring inside the win condition, bot decisions inside `Main`, action effects inside the main loop, and human input parsing mixed with validation.

## Expected Smells Checklist

I compared the final work against `docs/expected-smells.md`. Some smells were fully improved, while others remain as known risks because this assignment asked for incremental refactoring instead of a full rewrite.

### One Large `Main` Class

Found: the original program placed almost everything in `Main`.

Changed: rule logic, scoring, bot decisions, human command parsing, deck creation, player data, characterization tests, and game running were extracted into separate classes.

Still left: `Main` still owns the application entry point, argument parsing, player setup, and final score printing. The turn orchestration is no longer in `Main`.

### Global Mutable State

Found: `Main` originally stored player names, human flags, hands, scores, deck, discard pile, current player, direction, up card, called color, random, scanner, and quiet mode as separate static mutable fields.

Changed: player-related state is grouped into `Player` objects, and game-running state moved from `Main` into `GameRunner`.

Still left: `GameRunner` still owns mutable game state directly. A future refactor could introduce a smaller `GameState` object inside or beside `GameRunner`.

### Long Game Loop

Found: `playGame()` handled deck creation, dealing, input, bot decisions, validation, scoring, card effects, output, and win detection.

Changed: the whole game loop moved to `GameRunner`, and deck creation, scoring, bot decisions, legal rules, human parsing, tests, and action effects were extracted.

Still left: `GameRunner.playGame()` still coordinates several responsibilities, but `Main` is no longer the long procedural class that runs every detail.

### Mixed CLI And Game Logic

Found: `System.out`, `Scanner`, move validation, and rule execution were mixed together.

Changed: human command parsing was extracted to `HumanMoveParser`, scanner-based prompts were moved to `ConsoleInput`, and printing was moved to `ConsoleView`. Rule logic no longer depends on console input/output.

Still left: `GameRunner` still decides when input/output events happen, but it no longer owns the direct console calls.

### Duplicated Legality Checks

Found: legal-play conditions existed in multiple places, especially bot selection and played-card validation.

Changed: legal-play rules are centralized in `CardRules.isLegal()`. `Main` and `BotStrategy` now call that shared method.

Still left: no intentional duplicate legality logic remains.

### Primitive-Heavy Card Representation

Found: cards are represented as strings such as `R5`, `GS`, `G+2`, `W`, and `W4`.

Changed: parsing and rule interpretation were moved into `CardRules`.

Still left: the representation is still string-based to preserve compatibility with the CLI and existing behavior. A future `Card` value object would be safer.

### If-Heavy Action Handling

Found: skip, reverse, draw two, and wild draw four were handled inside the main game loop with conditionals.

Changed: action handling moved into `GameRunner.applyCardEffect()` and `GameRunner.drawCardsForCurrentPlayer()`.

Still left: `applyCardEffect()` still uses conditionals. This is acceptable for the current project size, but polymorphic card effects could reduce this later.

### Hidden Randomness

Found: randomness is stored globally and used for shuffling and choosing the starting player.

Changed: deck construction and shuffling now live in `DeckFactory`, which receives the existing `Random` object from `GameRunner`. The existing `--seed` option was preserved and used for verification.

Still left: random behavior is still part of the game runner flow, but it is no longer a global `Main` field.

### Weak Player Boundaries

Found: players are represented through parallel collections: names, human flags, hands, and scores.

Changed: a `Player` class now groups a player's name, human/bot flag, hand, and score.

Still left: turn orchestration still indexes into `players` by `currentPlayer`, but the previous parallel collections are gone.

### Parsing Mixed With Validation

Found: human input code parsed commands, checked card existence, checked legality, read from `Scanner`, and printed messages.

Changed: raw command parsing moved to `HumanMoveParser`, and scanner/prompt behavior moved to `ConsoleInput`.

Still left: `ConsoleInput` still validates card-code legality before returning a selected card. This preserves the original behavior where illegal card-code input is rejected immediately.

### Scoring Mixed With Game Completion

Found: winner score was calculated directly inside the win condition.

Changed: scoring moved to `ScoreCalculator.scoreRemainingHands()` for hand lists and `ScoreCalculator.scoreRemainingPlayers()` for real player objects.

Still left: game completion still decides when to call the calculator, which is appropriate.

### Bot Decisions Mixed With Rule Knowledge

Found: bot choice code lived in `Main` and duplicated rule checks.

Changed: bot decisions moved to `BotStrategy`, and the strategy now reuses `CardRules.isLegal()`.

Still left: the strategy is still simple, but it is now a clear extension point.

## Refactoring Guide Checklist

I also checked the work against `docs/refactoring-guide.md`.

1. Compile and run the game: done. The project compiles with `javac -d out src/*.java`, and a seeded bot game runs.
2. Run the characterization checks: done. The original self-test path still works.
3. Add checks around behavior before changing it: done. The self-test grew from 9 checks to 39 checks before and during the refactoring.
4. Extract small methods from the game loop: done. `applyCardEffect()` and `drawCardsForCurrentPlayer()` were extracted, deck creation moved to `DeckFactory`, and the loop itself moved to `GameRunner`.
5. Separate user input parsing from move validation: done for console ownership. `HumanMoveParser` parses commands, `ConsoleInput` owns scanner prompts, and `GameRunner` no longer reads from `Scanner` directly.
6. Centralize legal-play rules: done. `CardRules.isLegal()` is the shared legal-play rule.
7. Isolate card effects: partially done. Effects are isolated in `applyCardEffect()`, but not yet in separate effect classes.
8. Add one extension: not implemented, because the midterm brief says the extension does not need to be implemented. Instead, `docs/extension-readiness.md` explains the prepared extension point for a smarter bot strategy.

Useful refactorings applied from the guide:

- Extract Method: `applyCardEffect()`, `drawCardsForCurrentPlayer()`, and existing wrappers around extracted logic.
- Extract Class: `CardRules`, `ScoreCalculator`, `BotStrategy`, `HumanMoveParser`, `DeckFactory`, `Player`, `CharacterizationTests`, `GameRunner`, `ConsoleInput`, and `ConsoleView`.
- Move Method: card rules, scoring, bot selection, deck creation, game running, self-tests, parsing behavior, scanner prompts, and console rendering were moved out of `Main` or `GameRunner`.
- Split Phase: human input parsing is now separated from validation.
- Introduce Parameter Object: lightly applied through `HumanMove`, which represents parsed human input.

Useful refactorings not fully applied:

- Replace Conditional with Polymorphism was not used. The action-card conditional remains in `applyCardEffect()` because the project is small and preserving behavior was more important than adding a larger class hierarchy.

## Refactorings Performed

### 1. Expanded Characterization Tests

I first expanded `Main.selfTest()` before changing the design. This followed the assignment requirement to characterize behavior before risky refactoring.

Result: the project now has 39 checks covering card rules, scoring, bot decisions, draw behavior, action effects, and command parsing.

### 2. Extracted Card Rules Into `CardRules`

I created `CardRules.java` and moved these responsibilities there:

- card color detection
- card rank detection
- number extraction
- legal-play checking

Before this change, `chooseBotCard()` repeated the same legal-play conditions that also existed in `isLegal()`. After this change, bot logic and game logic both reuse `CardRules.isLegal()`.

Why this improves the design: legal-play behavior now has one clearer home. If the rules change later, there is less duplicated condition logic to update.

### 3. Extracted Scoring Into `ScoreCalculator`

I created `ScoreCalculator.java` and moved scoring responsibilities there:

- score for one card
- score for all losing players' remaining hands

Before this change, winner scoring was calculated directly inside the game loop after a player emptied their hand.

Why this improves the design: game completion and score calculation are separate responsibilities. Scoring can now be tested directly without playing a full game.

### 4. Extracted Bot Decisions Into `BotStrategy`

I created `BotStrategy.java` and moved these responsibilities there:

- choosing which card a bot should play
- choosing which color a bot should call after playing a wild

The original priority was preserved:

1. draw two
2. skip
3. number card
4. wild card
5. draw if nothing is playable

Why this improves the design: bot decisions are no longer mixed with the main game loop. This also creates a realistic extension point for a smarter bot.

### 5. Centralized Duplicated Legal-Play Logic

The old bot code recalculated whether a card was legal in several loops. I replaced that duplication with calls to the shared rule method.

Why this improves the design: fewer copies of the same condition reduce the chance of inconsistent behavior between human moves, bot moves, and drawn-card moves.

### 6. Extracted Action Effect Handling

I extracted action-card behavior from the main game loop into:

- `applyCardEffect(String card)`
- `drawCardsForCurrentPlayer(int count)`

This method handles:

- skip
- reverse
- draw two
- wild draw four
- normal turn movement

Why this improves the design: the main loop is easier to read, and action effects can now be characterized directly in tests.

### 7. Extracted Human Move Parsing Into `HumanMoveParser`

I created `HumanMoveParser.java` and a small `HumanMove` data object. This parser handles:

- `draw`
- numeric indexes
- card code input
- unknown input

`ConsoleInput` now uses this parser when asking a human for a move.

Why this improves the design: parsing can be tested without using `Scanner` or requiring interactive input. This moves the code toward the requested MVC-like separation.

### 8. Kept Compatibility Wrappers In `Main`

Some methods in `Main`, such as `isLegal()`, `color()`, `rank()`, `number()`, and `points()`, now delegate to extracted classes.

Why this choice was made: it kept the refactoring incremental and reduced the risk of accidentally breaking many call sites at once.

### 9. Extracted Characterization Tests Into `CharacterizationTests`

I moved the long self-test body out of `Main` and into `CharacterizationTests`.

Why this improves the design: the test suite now has a clear home, and `Main` is focused more on running the CLI game.

### 10. Extracted Deck Creation Into `DeckFactory`

I moved the UNO deck-building and shuffling code out of `playGame()` and into `DeckFactory.createShuffledDeck()`.

Why this improves the design: deck construction is no longer mixed into game orchestration, and the deck can be changed or tested separately later.

### 11. Introduced `Player`

I replaced the parallel collections for names, human flags, hands, and scores with a `Player` class.

Why this improves the design: player data now moves together as one object, reducing the risk of the separate lists getting out of sync.

### 12. Extracted Game Running Into `GameRunner`

I moved the game loop and mutable in-game state out of `Main` and into `GameRunner`.

`GameRunner` now owns:

- deck and discard pile
- current player and direction
- up card and called color
- dealing
- turn loop
- drawing
- applying card effects
- human prompts during a game
- bot move delegation
- win detection and score update

Why this improves the design: `Main` is now mostly responsible for command-line setup and final score printing, while `GameRunner` owns the actual game execution.

### 13. Extracted Console Output Into `ConsoleView`

I moved game output out of `GameRunner` and into `ConsoleView`.

`ConsoleView` now owns:

- game number display
- turn display
- draw/play/call messages
- UNO and win messages
- draw two and draw four messages
- safety-limit message
- final score printing
- human prompt text

Why this improves the design: replacing the CLI output with another view or replay log would no longer require editing the core game loop as heavily.

### 14. Extracted Console Input Into `ConsoleInput`

I moved direct `Scanner` usage out of `GameRunner` and into `ConsoleInput`.

`ConsoleInput` now owns:

- asking for a human move
- asking whether to play a drawn card
- asking for a wild color

Why this improves the design: `GameRunner` no longer calls `scanner.nextLine()` directly, which creates a cleaner boundary between turn orchestration and console input.

## Files Changed

### `src/Main.java`

Main still runs the game, but it now delegates more work:

- parses command-line options
- creates players
- creates `GameRunner`
- runs the requested number of games
- prints final scores
- delegates self-tests to `CharacterizationTests`

### `src/CardRules.java`

New class for card rule behavior:

- `color`
- `rank`
- `number`
- `isLegal`

### `src/ScoreCalculator.java`

New class for scoring behavior:

- card point values
- winner score from remaining hands

### `src/BotStrategy.java`

New class for bot behavior:

- choosing a playable card
- choosing a wild color

### `src/HumanMoveParser.java`

New class for command parsing:

- draw command
- index command
- card-code command
- not-found result

### `src/DeckFactory.java`

New class for deck setup:

- builds the simplified UNO deck
- shuffles it with the seeded `Random`

### `src/Player.java`

New class for player data:

- name
- human/bot flag
- hand
- score

### `src/CharacterizationTests.java`

New class for the self-test suite:

- runs all 39 characterization checks
- keeps test setup out of `Main`

### `src/GameRunner.java`

New class for game execution:

- owns per-game mutable state
- deals cards
- runs turns
- applies card effects
- updates scores when a player wins

### `src\ConsoleView.java`

New class for console output:

- prints game state and events
- handles quiet-mode suppression
- prints final scores
- provides prompt text for human input

### `src\ConsoleInput.java`

New class for console input:

- owns `Scanner`
- asks for human moves
- asks whether to play a drawn card
- asks for wild color
- uses `HumanMoveParser`

### `docs/refactoring-report.md`

Expanded report explaining characterized behavior, design problems, refactorings, preserved behavior, and remaining risks.

### `docs/extension-readiness.md`

Added a note explaining that the current design best supports a smarter bot strategy extension.

## Behavior Intentionally Preserved

The refactoring intentionally preserves the existing CLI behavior:

- the game still uses compact card codes like `R5`, `YS`, `BR`, `G+2`, `W`, and `W4`
- all hands are still visible in the terminal during non-quiet games
- humans can still type `draw` even when they have a legal card
- bot players still automatically play a drawn card when it is legal
- wild and wild draw four are always legal
- called color after a wild still affects which colored cards are legal
- draw two makes the next player draw two cards and lose their turn
- wild draw four makes the next player draw four cards and lose their turn
- reverse with two players still behaves like a skip because of the existing turn logic
- the deck still refills from the discard pile when empty
- if both deck and discard are empty, drawing still returns fallback card `W`
- the CLI still supports the same command-line options

## Behavior Not Intentionally Changed

No gameplay behavior was intentionally changed. The refactoring focused on moving responsibilities and reducing duplication. Any future behavior changes should be made separately and covered by new tests.

## How The Refactoring Matches The Rubric

### Behavior Preservation And Characterization Tests

The self-test suite now covers the required rule behaviors from the midterm brief: matching by color, number, action type, wild behavior, skip, reverse, draw two, drawing from the deck, scoring, and edge cases.

### Incremental Refactoring Discipline

The work was done in small steps:

1. run baseline checks
2. add characterization tests
3. extract card rules
4. remove duplicated legal checks
5. extract scoring
6. extract bot strategy
7. extract action effects
8. extract human command parsing
9. write reports

Each step kept the self-tests passing.

### Design Improvement

The code now has clearer homes for important responsibilities. Rules, scoring, bot strategy, and parsing are no longer all embedded directly in the main loop.

### Code Quality

The new classes are small and focused. The solution does not introduce a large framework or unnecessary dependency.

### Report And Extension Readiness

This report explains what changed and why. The extension-readiness document identifies smarter bot strategy as the most realistic extension supported by the new design.

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

I also ran a quiet seeded bot game:

```powershell
java -cp out Main --bots 3 --games 1 --quiet --seed 1
```

The game completed successfully and printed final scores.

## Full Characterization Test Results

The final self-test result is:

```text
Passed 39 characterization checks.
```

The 39 checks are:

1. `color R5`
2. `rank +2`
3. `rank skip`
4. `rank reverse`
5. `rank wild`
6. `rank wild draw four`
7. `number points`
8. `skip points`
9. `reverse points`
10. `draw two points`
11. `wild points`
12. `wild draw four points`
13. `winner scores other hands`
14. `same color`
15. `same number`
16. `same skip action`
17. `same draw two action`
18. `same reverse action`
19. `wild is always legal`
20. `wild draw four is always legal`
21. `called color`
22. `illegal mismatch`
23. `bot normal before wild`
24. `bot prefers draw two`
25. `bot prefers skip before number`
26. `bot color`
27. `parse draw command`
28. `parse index command`
29. `parse card code command`
30. `parse missing command`
31. `draw reshuffles discard`
32. `discard empty after reshuffle`
33. `draw fallback wild`
34. `next clockwise`
35. `next wraps counterclockwise`
36. `skip advances past next player`
37. `reverse flips direction`
38. `draw two hits next player`
39. `wild draw four hits next player`

These results show that the required midterm behaviors are covered: matching by color, matching by number, matching by action type, wild behavior, skip, reverse, draw two, drawing from the deck, scoring, and edge cases.

## Remaining Risks

`GameRunner` now owns most match state, which is better than keeping it in `Main`, but it still combines state and orchestration in one class. Larger features could benefit from a separate `GameState` object.

The main game loop is improved but still handles several responsibilities: dealing, deciding when to ask for moves, applying moves, checking wins, and advancing turns. Console rendering and scanner input are now outside `GameRunner`, but a future refactor could split turn orchestration further.

The tests are still custom self-tests instead of JUnit tests. This avoids adding dependencies, but a real test framework would give clearer test names and better failure reporting.

The card representation is still string-based. This preserved compatibility with the original CLI, but a future `Card` value object could make invalid card states harder to create.
