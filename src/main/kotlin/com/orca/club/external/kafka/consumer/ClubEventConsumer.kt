package com.orca.club.external.kafka.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.orca.club.external.kafka.EventTopics
import com.orca.club.external.kafka.PlayerMessage
import com.orca.club.external.kafka.publisher.EventPublisher
import com.orca.club.external.redis.RedisService
import com.orca.club.service.ClubManager
import com.orca.club.utils.getJsonValue
import org.bson.types.ObjectId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ClubEventConsumer(
    private val clubManager: ClubManager,
    private val redisService: RedisService,
    private val objectMapper: ObjectMapper,
    private val eventPublisher: EventPublisher
) {
    @KafkaListener(topics = [EventTopics.PLAYER_UPDATED])
    suspend fun playerUpdate(txId: String) {
        val jsonString = redisService.get(txId)
        try {
            val message = parseToMessage<PlayerMessage>(jsonString)
            clubManager.updatePlayerInfo(playerId = ObjectId(message.id), name = message.name)
        } catch (e: Exception) {
            eventPublisher.send(EventTopics.PLAYER_UPDATE_FAILED, jsonString)
        }
    }

    @KafkaListener(topics = [EventTopics.PLAYER_UPDATE_FAILED])
    suspend fun playerUpdateFailed(txId: String) {
        val jsonString = redisService.get(txId)
        val message = parseToMessage<PlayerMessage>(jsonString)
        clubManager.updatePlayerInfo(playerId = ObjectId(message.id), name = message.name)
    }

    @KafkaListener(topics = [EventTopics.CLUB_CREATE_FAILED])
    suspend fun createFailed(txId: String) {
        val jsonString = redisService.get(txId)
        val clubId = jsonString.getJsonValue("clubId")
        clubManager.delete(ObjectId(clubId))
    }

    private suspend inline fun <reified T> parseToMessage(jsonString: String): T {
        return objectMapper.readValue<T>(jsonString)
    }
}