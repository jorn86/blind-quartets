package org.hertsig.quartets.card

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.game.CardMatch

internal data class InCategory(val category: Category, val except: Set<CardName>): CardState {
    constructor(category: Category, vararg except: CardName): this(category, except.toSet())

    override fun matches(constraint: CardState): CardMatch {
        when (constraint) {
            is SingleCard -> {
                if (category != constraint.category) return CardMatch.NO
                if (except.contains(constraint.cardName)) return CardMatch.NO
                return CardMatch.POSSIBLE
            }
            is InCategory -> {
                if (category != constraint.category) return CardMatch.NO
                return CardMatch.POSSIBLE
            }
            is Not -> return constraint.matches(this)
            is Blank -> return CardMatch.POSSIBLE
        }
    }

    override fun combine(constraint: CardState): CardState {
        return when (constraint) {
            is SingleCard -> constraint
            is InCategory -> InCategory(category, except + constraint.except)
            is Not -> constraint.combine(this)
            is Blank -> this
        }
    }

    override fun singleCategoryIfAny() = category

    override fun toString() = if (except.isEmpty()) "<any> of $category" else "<some> of $category, but not $except"
}