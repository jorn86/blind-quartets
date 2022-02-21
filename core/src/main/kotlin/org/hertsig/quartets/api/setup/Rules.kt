package org.hertsig.quartets.api.setup

data class Rules(
    val quartets: Int = 8,
    val initialHandSize: Int = 4,
    val turnMode: TurnMode = TurnMode.ROUND,
    val quartetSize: Int = 4,
)
