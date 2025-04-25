package com.orca.club.external.kafka.consumer

import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.external.kafka.EventTopics
import com.orca.club.external.redis.RedisService
import com.orca.club.service.JoinManager
import com.orca.club.utils.getJsonValue
import org.bson.types.ObjectId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class JoinEventConsumer(
    private val joinManager: JoinManager,
    private val redisService: RedisService
) {
    @KafkaListener(topics = [EventTopics.JOIN_ACCEPT_FAILED])
    suspend fun revertToPending(txId: String) {
        val jsonString = redisService.get(txId)
        val joinApplicationId = jsonString.getJsonValue("joinApplicationId")
        joinManager.updateStatus(
            joinApplicationId = ObjectId(joinApplicationId),
            status = JoinApplicationStatus.PENDING
        )
    }
}