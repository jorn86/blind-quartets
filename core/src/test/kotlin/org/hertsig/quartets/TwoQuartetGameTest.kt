package org.hertsig.quartets

import org.hertsig.quartets.api.game.Game
import org.hertsig.quartets.api.isCompleted
import org.hertsig.quartets.api.setup
import org.hertsig.quartets.api.setup.Rules
import org.hertsig.quartets.data.PredefinedCategories.Clubs
import org.hertsig.quartets.data.PredefinedCategories.Hearts
import org.hertsig.quartets.data.PredefinedPlayers.p1
import org.hertsig.quartets.data.PredefinedPlayers.p2
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TwoQuartetGameTest : BlindQuartetsTest {
    override lateinit var game: Game

    @Test
    fun simple() {
        game = setup(listOf(p1, p2), p1, Rules(2, 4))

        move(p1, p2, Clubs.category, Clubs.ace, true, 5)
        move(p1, p2, Clubs.category, Clubs.king, true, 6)
        move(p1, p2, Clubs.category, Clubs.queen, true, 7)
        game.declareQuartet(p1, Clubs.category)
        move(p1, p2, Hearts.category, Hearts.ace, true,4)
        game.declareQuartet(p1, Hearts.category)
        assertTrue(game.isCompleted())
    }

    @Test
    fun playersDetermineCorrectAnswers() {
        game = setup(listOf(p1, p2), p1, Rules(2, 2))

        move(p1, p2, Clubs.category, Clubs.ace, false, 3)
        move(p2, p1, Hearts.category, Hearts.ace, false, 3)
        move(p1, p2, Clubs.category, Clubs.king, false, 4)
        move(p2, p1, Hearts.category, Hearts.king, false, 4)
        move(p1, p2, Clubs.category, Clubs.jack, true, 5)
        game.declareQuartet(p1, Clubs.category)

        move(p1, p2, Hearts.category, Hearts.king, true, 2)
        move(p1, p2, Hearts.category, Hearts.ace, true, 3)
        move(p1, p2, Hearts.category, Hearts.queen, true, 4)
        game.declareQuartet(p1, Hearts.category)

        assertTrue(game.isCompleted())
    }
}
