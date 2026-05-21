# Extension Readiness

## Best Supported Extension

The current refactoring best supports adding a smarter bot strategy.

## Where The Change Would Go

A smarter bot would mainly be implemented in `BotStrategy`. That class now owns bot card choice and bot color choice instead of keeping those decisions inside the main game loop.

For example, a future strategy could prefer winning moves, avoid wasting wild cards, choose colors based on stronger hand analysis, or react differently when the next player has one card left.

## Why The Design Helps

Bot decisions can now reuse `CardRules.isLegal` instead of copying rule conditions. Scoring is available through `ScoreCalculator`, so a smarter bot could estimate the value of cards left in its hand. Player data is grouped in `Player`, so hand and score data are no longer spread across parallel lists. `GameRunner` can ask the strategy for a move without knowing how that move was chosen.

This makes the bot strategy easier to replace or improve without changing legal-play rules, scoring rules, or console input handling.

## What Is Still Difficult

The main game state now lives mostly in `GameRunner`. A more advanced bot may need richer information about the whole game, such as all players, direction, discard history, or previous moves. Passing that information cleanly would be easier if the remaining runner state were extracted into a dedicated game-state object.

The turn loop also still owns win detection and several output messages, so extensions like replay logs or a new UI would need more separation between game events and console printing.
