package org.hertsig.quartets

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.Player
import org.hertsig.quartets.api.game.Game
import org.junit.jupiter.api.Assertions.assertEquals

interface BlindQuartetsTest {
    val game: Game

    fun move(origin: Player, target: Player, category: Category, cardName: CardName, expectedAnswer: Boolean) {
        val nextPlayer = game.move(origin, target, category, cardName)
        val actualAnswer = nextPlayer == origin
        assertEquals(expectedAnswer, actualAnswer) { "Unexpected outcome from move" }
    }

    fun move(origin: Player, target: Player, category: Category, cardName: CardName, expectedAnswer: Boolean, expectedHandSize: Int) {
        move(origin, target, category, cardName, expectedAnswer)
        assertEquals(expectedHandSize, game.hand(origin).size) { "Unexpected hand size after move" }
    }
}
