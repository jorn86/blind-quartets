package org.hertsig.quartets.card

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.game.CardMatch

internal data class SingleCard(val category: Category, val cardName: CardName): CardState {
    override fun matches(constraint: CardState): CardMatch {
        return when (constraint) {
            is SingleCard -> if (this == constraint) CardMatch.YES else CardMatch.NO
            is InCategory -> constraint.matches(this)
            is Not -> constraint.matches(this)
            is Blank -> CardMatch.POSSIBLE
        }
    }

    override fun combine(constraint: CardState) = this
    override fun singleCategoryIfAny() = category
    override fun singleNameIfAny() = cardName

    override fun toString() = "$cardName of $category"
}
