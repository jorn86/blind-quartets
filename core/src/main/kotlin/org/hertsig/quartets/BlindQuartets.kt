package org.hertsig.quartets

import com.google.common.collect.HashMultimap
import org.hertsig.quartets.api.CardName
import org.hertsig.quartets.api.Category
import org.hertsig.quartets.api.Player
import org.hertsig.quartets.api.game.Card
import org.hertsig.quartets.api.game.CardMatch
import org.hertsig.quartets.api.game.Game
import org.hertsig.quartets.api.game.GameStateException
import org.hertsig.quartets.api.printGameState
import org.hertsig.quartets.api.setup.Rules
import org.hertsig.quartets.api.setup.TurnMode
import org.hertsig.quartets.card.*
import java.util.*

internal class BlindQuartets(override val players: List<Player>, override val rules: Rules) : Game {
    override lateinit var currentTurn: Player; private set
    private val playerHands = HashMultimap.create<Player, RealCard>()
    override fun hand(player: Player): Set<Card> = playerHands.get(player).toSet()
    override fun mustHave(player: Player, category: Category, cardName: CardName) =
        playerHands.get(player).any { it.matches(SingleCard(category, cardName)) == CardMatch.YES }

    override fun canHave(player: Player, category: Category, cardName: CardName) =
        canHave(player, SingleCard(category, cardName))

    @OptIn(ExperimentalStdlibApi::class)
    override val allCards = buildSet(rules.quartets * rules.quartetSize) {
        repeat(rules.quartets * rules.quartetSize) { add(RealCard()) }
    }
    private val deck = Stack<RealCard>().apply {
        addAll(allCards)
    }
    override val cardsInDeck; get() = deck.toSet()
    override val deckSize; get() = deck.size

    private val knownCategories = mutableSetOf<Category>()
    private val knownCards = HashMultimap.create<Category, CardName>()
    private val completedCategories = mutableSetOf<Category>()
    override val completedQuartets; get() = completedCategories.toSet()

    override val definedCategories; get() = knownCategories.toSet()
    override fun definedCards(category: Category) = knownCards.get(category).toSet()

    override fun move(origin: Player, target: Player, category: Category, cardName: CardName) =
        move(origin, target, SingleCard(category, cardName))

    override fun move(origin: Player, target: Player, category: Category, cardName: CardName, response: Boolean) =
        move(origin, target, SingleCard(category, cardName), response)

    private fun dealEachPlayer(amount: Int) {
        if (deck.size < amount * players.size)
            throw GameStateException("Can't deal $amount to each player: only ${deck.size} cards remain")
        players.forEach { deal(it, amount) }
    }

    private fun deal(player: Player = currentTurn, amount: Int = 1) {
        if (player !in players) throw IllegalArgumentException("Unknown player $player")
        if (amount < 0) throw IllegalArgumentException("Can't deal $amount cards")
        if (deck.size < amount)
            throw GameStateException("Can't deal $amount to {$player.name}: only ${deck.size} cards remain")
        repeat(amount) { player.addToHand(deck.pop()) }
    }

    private fun Player.addToHand(card: RealCard) = require(playerHands.put(this, card))

    private fun move(origin: Player, target: Player, card: SingleCard): Player {
        prepareMove(origin, target, card)
        return finishMove(origin, target, card, mustHave(target, card.category, card.cardName))
    }

    private fun move(origin: Player, target: Player, card: SingleCard, response: Boolean): Player {
        prepareMove(origin, target, card)
        return finishMove(origin, target, card, response)
    }

    private fun prepareMove(origin: Player, target: Player, card: SingleCard) {
        checkTurn(origin)
        if (origin == target) throw IllegalArgumentException("Can't ask yourself")
        knownCards.put(ensureCategory(card.category), ensureCardName(card))
        playerHands.get(origin).forEach { it.apply(Not(card)) }
        constrainOne(origin, InCategory(card.category))
    }

    private fun finishMove(origin: Player, target: Player, card: SingleCard, response: Boolean): Player {
        if (response) {
            val realCard = constrainOne(target, card)
            require(playerHands.remove(target, realCard))
            origin.addToHand(realCard)
            return origin
        }

        playerHands.get(target).forEach { it.apply(Not(card)) }
        if (!deck.isEmpty()) deal(origin, 1)

        checkCategoryComplete(card.category)
        return nextTurn(target)
    }

    private fun canHave(player: Player, card: CardState) =
        playerHands.get(player).any { it.matches(card) != CardMatch.NO }

    private fun constrainOne(player: Player, constraint: CardState): RealCard {
        val sorted = playerHands.get(player).sortedBy { it.matches(constraint) }
        val bestMatch = sorted.firstOrNull()
        if (bestMatch != null && bestMatch.matches(constraint) != CardMatch.NO) {
            bestMatch.apply(constraint)
            return bestMatch
        }
        throw GameStateException("$this has no cards that can take $constraint")
    }

    override fun declareQuartet(origin: Player, category: Category) {
        checkTurn(origin)
        checkCategory(category)
        val owned = playerHands.get(origin).filter { it.category == category }.toSet()
        if (owned.size != rules.quartetSize) throw GameStateException("Can't declare quartet: $origin doesn't have all cards")
        playerHands.get(origin).removeAll(owned)
        completedCategories.add(category)
        println("$origin completed $category\n")
        if (DEBUG) printGameState()
    }

    override fun pass(origin: Player, target: Player?): Player {
        checkTurn(origin)
        if (!deck.isEmpty()) deal(origin, 1)
        return nextTurn(target)
    }

    private fun checkTurn(origin: Player) {
        if (rules.turnMode != TurnMode.FREE_FOR_ALL && origin != currentTurn)
            throw IllegalStateException("It's not ${origin.name}'s turn")
    }

    private fun nextTurn(target: Player? = null): Player {
        currentTurn = when (rules.turnMode) {
            TurnMode.TOKEN -> target
                ?: throw IllegalArgumentException("Turn mode ${rules.turnMode} requires specific next player")
            TurnMode.FREE_FOR_ALL,
            TurnMode.ROUND -> players[(players.indexOf(currentTurn) + 1) % players.size]
        }
        return currentTurn
    }

    private fun checkCategory(category: Category) = knownCategories.singleOrNull { it == category }
        ?: throw IllegalArgumentException("Unknow category $category")

    private fun ensureCardName(card: SingleCard): CardName {
        val inCategory = knownCards.get(card.category)
        val existing = inCategory.singleOrNull { it == card.cardName }
        if (existing != null) return existing
        if (inCategory.size >= rules.quartetSize) {
            throw GameStateException("Trying to define $card, but ${card.category} already has $inCategory")
        }
        knownCards.put(card.category, card.cardName)
        return card.cardName
    }

    private fun ensureCategory(category: Category): Category {
        val existing = knownCategories.singleOrNull { it == category }
        if (existing != null) return existing

        if (knownCategories.size >= rules.quartets) {
            throw GameStateException("Trying to create new category $category, but ${rules.quartets} categories already exist: $knownCategories")
        }
        return category.also(knownCategories::add)
    }

    internal fun start(startingPlayer: Player): Game {
        require(startingPlayer in players)
        dealEachPlayer(rules.initialHandSize)
        currentTurn = startingPlayer
        return this
    }

    private fun notifyStateChange(card: RealCard, previous: CardState, next: CardState) {
        if (next is SingleCard) {
            allCards.filter { it != card }.forEach { it.apply(Not(next)) }
            checkCategoryComplete(next.category)
        }

        if (next is InCategory) {
            if (next.except.size >= rules.quartetSize) throw GameStateException("$next cannot exist")
            checkCategoryComplete(next.category)
            knownCards.get(next.category).forEach { checkSingleCard(SingleCard(next.category, it)) }
        }

        if (next is Not) {
            card.apply(simplify(next))
            next.categories.forEach(::checkCategoryComplete)
            next.cards.forEach(::checkSingleCard)
        }
        if (previous is Not) {
            previous.categories.forEach(::checkCategoryComplete)
            previous.cards.forEach(::checkSingleCard)
        }
    }

    private fun simplify(not: Not): Not {
        val cardsWithoutRedundant = not.cards.filter { it.category !in not.categories }.toSet()
        val fullCategories = cardsWithoutRedundant.groupBy { it.category }
            .filter { (_, cards) -> cards.size == rules.quartetSize }
            .map { (category, _) -> category }
        return Not(cardsWithoutRedundant.filter { it.category !in fullCategories }.toSet(), not.categories + fullCategories)
    }

    private fun checkSingleCard(card: SingleCard) {
        if (deck.isEmpty()) players.singleOrNull { canHave(it, card) }?.let { constrainOne(it, card) }
        val options = allCards.filter { it.matches(card) != CardMatch.NO }
        if (options.isEmpty()) {
            if (DEBUG) printGameState()
            throw GameStateException("No card can be $card")
        }
        options.singleOrNull()?.apply(card)
    }

    private fun checkCategoryComplete(category: Category) {
        val definitelyInCategory = allCards.filter { it.category == category }
        if (definitelyInCategory.size == rules.quartetSize) {
            allCards.filter { it !in definitelyInCategory }.forEach { it.apply(Not(category)) }
        }

        val maybeInCategory = allCards.filter { it.matches(InCategory(category)) != CardMatch.NO }
        if (maybeInCategory.size == rules.quartetSize) {
            maybeInCategory.forEach { it.apply(InCategory(category)) }
        }

        val names = knownCards.get(category)
        val inCategory = allCards.filter { it.category == category }
        if (names.size == rules.quartetSize && inCategory.size == rules.quartetSize) {
            val knownNames = inCategory.mapNotNull { it.name }.toSet()
            val remainingNames = names - knownNames
            val newConstraint = if (remainingNames.size != 1) InCategory(category, knownNames)
                else SingleCard(category, remainingNames.single())
            inCategory.filter { it.name == null }.forEach { it.apply(newConstraint) }
        }
    }

    internal inner class RealCard : Card {
        private var state: CardState = Blank
        override val category: Category? get() = state.singleCategoryIfAny()
        override val name: CardName? = state.singleNameIfAny()
        override val isBlank; get() = state == Blank

        internal fun matches(constraint: CardState) = state.matches(constraint)

        fun apply(constraint: CardState) {
            val match = state.matches(constraint)
            if (match == CardMatch.NO) throw GameStateException("Cannot add $constraint to $state")
            if (match == CardMatch.YES) return

            val new = state.combine(constraint)
            if (new != state) {
                val old = state
                state = new
                if (DEBUG) {
                    println("Updated $old to $new:")
                    printGameState()
                }
                notifyStateChange(this, old, new)
            }
        }

        override fun toString() = state.toString()
    }

    companion object {
        const val DEBUG = true
    }
}
