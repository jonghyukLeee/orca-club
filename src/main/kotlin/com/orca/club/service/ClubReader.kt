package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.domain.Player
import com.orca.club.repository.ClubRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class ClubReader(
    private val clubRepository: ClubRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun findById(id: String): Club? {
        return clubRepository.findById(id).awaitSingleOrNull()
    }

    suspend fun findByName(name: String): Club? {
        return clubRepository.findByName(name).awaitSingleOrNull()
    }

    suspend fun findPlayerById(clubId: String, playerId: String): Player? {
        val aggregation = newAggregation(
            match(Criteria.where("_id").`is`(clubId)),
            unwind("players"),
            match(Criteria.where("players.id").`is`(playerId)),
            replaceRoot("players")
        )

        return reactiveMongoTemplate.aggregate(
            aggregation,
            "club",
            Player::class.java
        ).singleOrEmpty()
            .awaitSingleOrNull()
    }

    private suspend fun buildQueryById(id: String): Query {
        return Query(Criteria.where("_id").`is`(id))
    }
}