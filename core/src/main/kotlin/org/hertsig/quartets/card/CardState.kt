package org.hertsig.quartets.card

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.game.CardMatch

internal sealed interface CardState {
    fun singleCategoryIfAny(): Category? = null
    fun singleNameIfAny(): CardName? = null
    fun matches(constraint: CardState): CardMatch
    fun combine(constraint: CardState): CardState
}
