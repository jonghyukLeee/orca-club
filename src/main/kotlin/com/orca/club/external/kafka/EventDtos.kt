package com.orca.club.external.kafka

import com.orca.club.domain.Club.Player.Position
import com.orca.club.domain.JoinApplicationStatus

data class JoinAcceptMessage(
    val applicationId: String,
    val clubId: String,
    val playerId: String,
    val position: Position,
    val status: JoinApplicationStatus,
)

data class PlayerUpdateMessage(
    val id: String,
    val name: String,
    val birth: String,
    val loginId: String,
)