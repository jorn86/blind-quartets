package org.hertsig.quartets.api.game

import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.Player
import org.hertsig.quartets.api.setup.Rules

interface Game {
    val rules: Rules
    val players: List<Player>
    val currentTurn: Player
    val deckSize: Int

    val definedCategories: Set<Category>
    fun definedCards(category: Category): Set<CardName>
    val completedQuartets: Set<Category>

    val allCards: Set<Card>
    val cardsInDeck: Set<Card>
    fun hand(player: Player = currentTurn): Set<Card>

    /**
     * Resolve a single question. Uses known state to determine the best answer.
     * @param origin: The asking player, defaults to currentTurn
     * @param target: The player being asked
     * @param category & cardName: The card they're asking for
     * @return the player whose turn it is now
     * @throws GameStateException if this question would lead to invalid game state
     */
    fun move(origin: Player = currentTurn, target: Player, category: Category, cardName: CardName): Player

    /**
     * Resolve a single question
     * @param origin: The asking player, defaults to currentTurn
     * @param target: The player being asked
     * @param category & cardName: The card they're asking for
     * @param response: The specific response by the target player. Could cause invalid game state if specified incorrectly.
     * @return the player whose turn it is now
     * @throws GameStateException if this question would lead to invalid game state
     */
    fun move(origin: Player = currentTurn, target: Player, category: Category, cardName: CardName, response: Boolean): Player

    fun declareQuartet(origin: Player = currentTurn, category: Category)

    /**
     * Pass the turn without asking a question. Equivalent to asking a question you know must be answered "no".
     */
    fun pass(origin: Player = currentTurn, target: Player? = null): Player

    fun mustHave(player: Player, category: Category, cardName: CardName): Boolean
    fun canHave(player: Player, category: Category, cardName: CardName): Boolean
}
