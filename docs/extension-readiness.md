# Extension Readiness

## Best Supported Extension

The current refactoring best supports adding a smarter bot strategy.

## Where The Change Would Go

A smarter bot would mainly be implemented in `BotStrategy`. That class now owns bot card choice and bot color choice instead of keeping those decisions inside the main game loop.

For example, a future strategy could prefer winning moves, avoid wasting wild cards, choose colors based on stronger hand analysis, or react differently when the next player has one card left.

## Why The Design Helps

Bot decisions can now reuse `CardRules.isLegal` instead of copying rule conditions. Scoring is available through `ScoreCalculator`, so a smarter bot could estimate the value of cards left in its hand. Player data is grouped in `Player`, so hand and score data are no longer spread across parallel lists. `GameRunner` can ask the strategy for a move without knowing how that move was chosen.

Match state now lives in `GameState`, so a smarter bot could receive direction, up card, called color, and player information through a narrower state object instead of reaching into the full turn loop. Drawing and card effects are also separated into `DrawPile` and `CardEffectEngine`, which makes it easier to change bot behavior without touching deck logic or action-card handling.

Console behavior is separated into `ConsoleInput` and `ConsoleView`, so improving the UI or adding a replay-style output is also more realistic.

## What Is Still Difficult

`GameRunner` still coordinates the turn loop and decides when game events happen. A replay log or GUI would be easier now because console printing is isolated in `ConsoleView`, but a fuller event system would make those extensions cleaner.

Turn orchestration is thinner than before, but win detection and move resolution still live inside `GameRunner`. A future refactor could move those responsibilities behind a dedicated turn coordinator while keeping `GameState`, `DrawPile`, and `CardEffectEngine` as the runtime building blocks.
