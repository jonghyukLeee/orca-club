package com.orca.club.repository

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface JoinApplicationRepository: ReactiveMongoRepository<JoinApplication, ObjectId> {
    fun findByClubIdAndPlayerId(clubId: ObjectId, playerId: ObjectId): Mono<JoinApplication>
    fun findAllByClubIdAndStatus(clubId: ObjectId, status: JoinApplicationStatus): Flux<JoinApplication>
    fun findAllByPlayerIdAndStatus(playerId: ObjectId, status: JoinApplicationStatus): Flux<JoinApplication>
}