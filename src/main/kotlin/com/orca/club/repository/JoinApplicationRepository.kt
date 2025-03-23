package com.orca.club.repository

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface JoinApplicationRepository: ReactiveMongoRepository<JoinApplication, String> {
    fun findByClubIdAndPlayerId(clubId: String, playerId: String): Mono<JoinApplication>
    fun findAllByClubIdAndStatus(clubId: String, status: JoinApplicationStatus): Flux<JoinApplication>
}