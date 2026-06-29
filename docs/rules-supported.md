# Supported UNO Rules

This document maps the final project rule reference to the current implementation.

## Implemented Rules

| Rule | Status | Notes |
|---|---|---|
| Deck composition (108 cards) | Implemented | `DeckFactory` |
| Legal play by color | Implemented | `CardRules.isLegal` |
| Legal play by number | Implemented | `CardRules.isLegal` |
| Legal play by action type | Implemented | `CardRules.isLegal` |
| Wild always playable | Implemented | |
| Wild Draw Four always playable | Implemented | Simplified: no color-match restriction |
| Skip | Implemented | `CardEffectEngine` |
| Reverse | Implemented | `CardEffectEngine` |
| Draw Two | Implemented | next player draws 2 and loses turn |
| Wild color choice | Implemented | human prompt or bot strategy |
| Wild Draw Four | Implemented | color choice + next player draws 4 |
| Draw one card when unable to play | Implemented | type `draw` |
| Play drawn card if legal | Implemented | human `y/n`, bots auto-play |
| UNO call | Implemented | prompt after reaching one card; `uno` command |
| Missed UNO penalty | Implemented | draw 2 at start of next turn |
| Round scoring | Implemented | `ScoreCalculator` |
| Multi-round target score | Implemented | `--target 500` (or custom target) |

## Documented Simplifications

| Topic | Variant used |
|---|---|
| Wild Draw Four challenge | Not implemented |
| Draw Two / Draw Four stacking | Not implemented |
| Wild Draw Four restriction | Wild and W4 are always legal |
| Human draw with legal play available | Human may still type `draw` |
| Two-player Reverse | Treated like Skip |
| Starting discard card | Wild/action starting cards are redrawn |
| Hands visible in CLI | All hands are printed in non-quiet mode |
| Bot behavior | Simple priority bot in `BotStrategy` |
| UNO timing | Human is prompted immediately after reaching one card; missed calls are penalized at the next turn start |
| Target score default | `500` when using `--target` without a value |

## CLI Commands

```bash
java -jar target/uno-cli.jar --bots 3 --target 500 --quiet --seed 1
java -jar target/uno-cli.jar --human --bots 2 --target 300
java -jar target/uno-cli.jar --bots 3 --games 3 --quiet
```

During a human turn:

- card index or card code to play
- `draw` to draw
- `uno` to call UNO when down to one card
