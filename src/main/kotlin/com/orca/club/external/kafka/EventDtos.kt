package com.orca.club.external.kafka

data class JoinAcceptMessage(
    val joinApplicationId: String,
    val clubId: String,
    val playerId: String,
)

data class PlayerMessage(
    val id: String,
    val name: String,
    val birth: String,
    val loginId: String,
)

data class ClubCreatedMessage(
    val clubId: String,
    val playerId: String
)