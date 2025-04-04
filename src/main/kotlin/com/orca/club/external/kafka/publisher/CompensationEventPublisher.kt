package com.orca.club.external.kafka.publisher

import com.orca.club.domain.JoinApplication
import com.orca.club.external.kafka.JoinAcceptMessage
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service


@Service
class CompensationEventPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, Any>
) {
    suspend fun joinAcceptFailed(joinApplication: JoinApplication) {
        send("join-accept-failed"
            , JoinAcceptMessage(
                applicationId = joinApplication.id!!,
                clubId = joinApplication.clubId,
                playerId = joinApplication.playerId,
                position = joinApplication.position,
                status = joinApplication.status,
            )
        )
    }

    private suspend fun send(topic: String, message: Any) {
        reactiveKafkaProducerTemplate.send(topic, message).awaitSingle()
    }
}