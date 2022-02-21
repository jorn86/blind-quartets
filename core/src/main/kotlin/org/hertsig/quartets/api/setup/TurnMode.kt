package org.hertsig.quartets.api.setup

enum class TurnMode {
    TOKEN, // pass the turn to the player who answered "no"
    ROUND, // pass the turn to the next player around the table
    FREE_FOR_ALL, // don't check any turn order, anything goes
}
