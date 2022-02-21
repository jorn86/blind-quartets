package org.hertsig.quartets

import org.hertsig.quartets.api.game.GameStateException
import org.hertsig.quartets.api.isCompleted
import org.hertsig.quartets.api.setup
import org.hertsig.quartets.api.setup.Rules
import org.hertsig.quartets.api.setup.TurnMode
import org.hertsig.quartets.data.PredefinedCategories.Clubs
import org.hertsig.quartets.data.PredefinedCategories.Hearts
import org.hertsig.quartets.data.PredefinedPlayers.p1
import org.hertsig.quartets.data.PredefinedPlayers.p2
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SingleQuartetGameTest : BlindQuartetsTest{
    override val game = setup(listOf(p1, p2), p1, Rules(1, 2, TurnMode.TOKEN))

    @Test
    fun canDeclareQuartet_whenOneNameMissing() {
        game.move(p1, p2, Clubs.category, Clubs.ace, true)
        game.pass(p1, p2)
        game.move(p2, p1, Clubs.category, Clubs.ace, true)
        game.move(p2, p1, Clubs.category, Clubs.king, true)
        game.move(p2, p1, Clubs.category, Clubs.queen, true)
        game.declareQuartet(p2, Clubs.category)
        assertTrue(game.isCompleted())
    }

    @Test
    fun canDeclareQuartet_whenTwoNamesMissing() {
        game.move(p1, p2, Clubs.category, Clubs.ace, true)
        game.move(p1, p2, Clubs.category, Clubs.king, true)
        game.declareQuartet(p1, Clubs.category)
        assertTrue(game.isCompleted())
    }

    @Test
    fun declareTooManyCategories_throwsGameStateException() {
        game.move(p1, p2, Clubs.category, Clubs.ace, true)
        assertThrows<GameStateException> { game.move(p1, p2, Hearts.category, Hearts.ace, true) }
        assertFalse(game.isCompleted())
    }

    @Test
    fun wrongAnswer_throwsException() {
        assertThrows<GameStateException> { game.move(p1, p2, Clubs.category, Clubs.ace, false) }
        assertFalse(game.isCompleted())
    }

    @Test
    fun wrongPlayerToMove_throwsException() {
        assertThrows<IllegalStateException> { game.move(p2, p1, Clubs.category, Clubs.ace) }
        assertFalse(game.isCompleted())
    }

    @Test
    fun moveToSelf_throwsException() {
        assertThrows<IllegalArgumentException> { game.move(p1, p1, Clubs.category, Clubs.ace) }
        assertFalse(game.isCompleted())
    }
}
