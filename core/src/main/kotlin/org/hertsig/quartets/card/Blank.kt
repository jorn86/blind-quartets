package org.hertsig.quartets.card

import org.hertsig.quartets.api.game.CardMatch

internal object Blank: CardState {
    override fun matches(constraint: CardState) = CardMatch.POSSIBLE
    override fun combine(constraint: CardState) = constraint
    override fun toString() = "Blank"
}
