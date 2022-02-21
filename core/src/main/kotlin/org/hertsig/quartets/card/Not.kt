package org.hertsig.quartets.card

import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.game.CardMatch

internal data class Not(val cards: Set<SingleCard> = setOf(), val categories: Set<Category> = setOf()): CardState {
    constructor(vararg cards: SingleCard): this(cards.toSet())
    constructor(vararg categories: Category): this(categories = categories.toSet())

    override fun matches(constraint: CardState): CardMatch {
        return when (constraint) {
            is SingleCard -> if (constraint.category in categories || constraint in cards) CardMatch.NO else CardMatch.POSSIBLE
            is InCategory -> if (constraint.category in categories) CardMatch.NO else CardMatch.POSSIBLE
            is Not -> CardMatch.POSSIBLE
            Blank -> CardMatch.POSSIBLE
        }
    }

    override fun combine(constraint: CardState): CardState {
        return when (constraint) {
            is SingleCard -> constraint
            is InCategory -> InCategory(constraint.category,
                constraint.except + cards.filter { it.category == constraint.category }.map { it.cardName })
            is Not -> Not(cards + constraint.cards, categories + constraint.categories)
            Blank -> this
        }
    }

    override fun toString(): String {
        if (cards.isEmpty()) return "None of categories $categories"
        if (categories.isEmpty()) return "None of $cards"
        return "None of $categories or $cards"
    }
}
