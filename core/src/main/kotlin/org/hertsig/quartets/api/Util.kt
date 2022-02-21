package org.hertsig.quartets.api

import org.hertsig.quartets.BlindQuartets
import org.hertsig.quartets.api.game.Game
import org.hertsig.quartets.api.setup.Rules
import org.hertsig.quartets.api.setup.TurnMode

fun setup(
    players: List<Player>,
    startingPlayer: Player = players.random(),
    rules: Rules = Rules()
): Game = BlindQuartets(players, rules).start(startingPlayer)

fun Game.isCompleted() = completedQuartets.size == rules.quartets

fun Game.printGameState() {
    val builder = StringBuilder()
    builder.append("Players:\n")
    players.forEach {
        val hand = hand(it)
        builder.append("\t${it.name} (${hand.size} cards)")
        if (rules.turnMode != TurnMode.FREE_FOR_ALL && it == currentTurn) builder.append(" (turn)")
        builder.append(":\n")
        hand.joinTo(builder, "\n\t\t", "\t\t", "\n")
    }

    builder.append("Known cards:\n")
    definedCategories.forEach {
        builder.append("\t${it.name}: ")
        definedCards(it).joinTo(builder, postfix = "\n")
    }

    builder.append("Cards left in deck: $deckSize\n")
    cardsInDeck.count { it.isBlank }.also { if (it != 0) builder.append("\t$it Blank\n") }
    cardsInDeck.filter { !it.isBlank }.forEach {
        builder.append("\t$it\n")
    }

    println(builder)
}
