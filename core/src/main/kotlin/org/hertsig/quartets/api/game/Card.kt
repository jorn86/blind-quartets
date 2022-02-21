package org.hertsig.quartets.api.game

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category

interface Card {
    val category: Category?
    val name: CardName?
    val isBlank: Boolean
}
