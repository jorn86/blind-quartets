package org.hertsig.quartets.api

data class Player(val name: String) {
    override fun toString() = name
}

data class Category(val name: String) {
    override fun toString() = name
}

data class CardName(val name: String) {
    override fun toString() = name
}
