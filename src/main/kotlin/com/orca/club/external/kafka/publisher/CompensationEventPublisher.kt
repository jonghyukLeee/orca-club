package com.orca.club.external.kafka.publisher

import com.orca.club.domain.JoinApplication
import com.orca.club.external.kafka.JoinAcceptFailedMessage
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service


@Service
class CompensationEventPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, Any>
) {
    suspend fun joinAcceptFailed(joinApplication: JoinApplication) {
        send("join-accept-failed"
            , JoinAcceptFailedMessage(
                joinApplicationId = joinApplication.id.toString(),
                clubId = joinApplication.clubId.toString(),
                playerId = joinApplication.playerId.toString(),
                status = joinApplication.status.name,
            )
        )
    }

    private suspend fun send(topic: String, message: Any) {
        reactiveKafkaProducerTemplate.send(topic, message).awaitSingle()
    }
}