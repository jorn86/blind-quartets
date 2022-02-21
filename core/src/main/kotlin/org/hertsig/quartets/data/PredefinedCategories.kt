package org.hertsig.quartets.data

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category

abstract class PredefinedCategory(name: String) {
    val category = Category(name)
    val cards; get() = cardNames.toSet()
    private val cardNames = mutableSetOf<CardName>()

    init { PredefinedCategories.internal.add(this) }

    internal fun card(name: String) = CardName(name).also(cardNames::add)
}

object PredefinedCategories {
    internal val internal = mutableSetOf<PredefinedCategory>()
    val categories; get() = internal.toSet()

    object Clubs: PredefinedCategory("Clubs") {
        val jack = card("Jack")
        val queen = card("Queen")
        val king = card("King")
        val ace = card("Ace")
    }

    object Hearts: PredefinedCategory("Hearts") {
        val jack = card("Jack")
        val queen = card("Queen")
        val king = card("King")
        val ace = card("Ace")
    }

    object Diamonds: PredefinedCategory("Diamonds") {
        val jack = card("Jack")
        val queen = card("Queen")
        val king = card("King")
        val ace = card("Ace")
    }

    object Spades: PredefinedCategory("Spades") {
        val jack = card("Jack")
        val queen = card("Queen")
        val king = card("King")
        val ace = card("Ace")
    }
}
