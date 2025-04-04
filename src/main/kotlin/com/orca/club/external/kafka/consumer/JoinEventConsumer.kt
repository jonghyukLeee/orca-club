package com.orca.club.external.kafka.consumer

import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.external.kafka.JoinAcceptMessage
import com.orca.club.service.JoinManager
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class JoinEventConsumer(
    private val joinManager: JoinManager
) {
    @KafkaListener(topics = ["join-accept-failed"])
    suspend fun revertToPending(message: JoinAcceptMessage) {
        joinManager.updateStatus(
            joinApplicationId = message.applicationId,
            status = JoinApplicationStatus.PENDING
        )
    }
}