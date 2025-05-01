package com.orca.club.external.player

import com.orca.club.exception.ExternalException
import com.orca.club.external.config.WebClientFactory
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

@Service
class PlayerService(
    clientFactory: WebClientFactory
) {
    private val client = clientFactory.getClient("players")

    suspend fun getPlayer(playerId: ObjectId): PlayerResponse {
        return try {
            client.get()
                .uri("/{playerId}", playerId.toString())
                .retrieve()
                .awaitBody<PlayerResponse>()
        } catch (e: WebClientResponseException) {
            throw ExternalException(HttpStatus.valueOf(e.statusCode.value()), "player")
        }
    }
}