package com.orca.club.external.kafka.consumer

import com.orca.club.external.kafka.PlayerUpdateMessage
import com.orca.club.service.ClubManager
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ClubEventConsumer(
    private val clubManager: ClubManager
) {
    @KafkaListener(topics = ["player-update"])
    suspend fun playerUpdate(message: PlayerUpdateMessage) {
        clubManager.updatePlayerInfo(
            playerId = message.id
            , name = message.name
        )
    }
}