package com.orca.club.external.kafka.consumer

import com.orca.club.external.kafka.EventTopics
import com.orca.club.external.kafka.PlayerUpdateMessage
import com.orca.club.external.redis.RedisService
import com.orca.club.service.ClubManager
import com.orca.club.utils.getJsonValue
import org.bson.types.ObjectId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ClubEventConsumer(
    private val clubManager: ClubManager,
    private val redisService: RedisService
) {
    @KafkaListener(topics = [EventTopics.PLAYER_UPDATED])
    suspend fun playerUpdate(message: PlayerUpdateMessage) {
        clubManager.updatePlayerInfo(
            playerId = ObjectId(message.id)
            , name = message.name
        )
    }

    @KafkaListener(topics = [EventTopics.CLUB_CREATE_FAILED])
    suspend fun createFailed(txId: String) {
        val jsonString = redisService.get(txId)
        val clubId = jsonString.getJsonValue("clubId")
        clubManager.delete(ObjectId(clubId))
    }
}