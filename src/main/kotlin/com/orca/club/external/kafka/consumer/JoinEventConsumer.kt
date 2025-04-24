package com.orca.club.external.kafka.consumer

import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.external.kafka.JoinAcceptFailedMessage
import com.orca.club.service.JoinManager
import org.bson.types.ObjectId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class JoinEventConsumer(
    private val joinManager: JoinManager
) {
    @KafkaListener(topics = ["join-accept-failed"])
    suspend fun revertToPending(message: JoinAcceptFailedMessage) {
        joinManager.updateStatus(
            joinApplicationId = ObjectId(message.joinApplicationId),
            status = JoinApplicationStatus.PENDING
        )
    }
}