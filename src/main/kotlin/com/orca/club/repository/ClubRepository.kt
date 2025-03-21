package com.orca.club.repository

import com.orca.club.domain.Club
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ClubRepository: ReactiveMongoRepository<Club, String> {
    fun findByName(name: String): Mono<Club>
}