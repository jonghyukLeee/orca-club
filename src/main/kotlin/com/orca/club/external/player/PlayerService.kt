package com.orca.club.external.player

import com.orca.club.exception.ExternalException
import com.orca.club.external.config.WebClientFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

@Service
class PlayerService(
    clientFactory: WebClientFactory
) {
    private val client = clientFactory.getClient("player")

    suspend fun getPlayer(playerId: String): PlayerResponse {
        return try {
            client.get()
                .uri("/{playerId}", playerId)
                .retrieve()
                .awaitBody<PlayerResponse>()
        } catch (e: WebClientResponseException) {
            throw ExternalException(HttpStatus.valueOf(e.statusCode.value()), "player")
        }
    }
}