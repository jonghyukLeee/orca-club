package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.domain.ClubStatus
import com.orca.club.domain.Player
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.repository.ClubRepository
import com.orca.club.utils.buildQueryById
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class ClubManager(
    private val clubRepository: ClubRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun create(name: String, introduction: String): Club {
        return clubRepository.save(
            Club(
                name = name,
                introduction = introduction
            )
        ).awaitSingle()
    }

    suspend fun update(clubId: ObjectId, name: String?, introduction: String?): Club {
        val update = Update().apply {
            name?.let { set("name", it) }
            introduction?.let { set("introduction", it) }
        }

        return reactiveMongoTemplate.findAndModify(
            buildQueryById(clubId), update, FindAndModifyOptions().returnNew(true), Club::class.java
        ).awaitSingleOrNull() ?: throw BaseException(ErrorCode.CLUB_NOT_FOUND)
    }

    suspend fun delete(clubId: ObjectId) {
        clubRepository.deleteById(clubId).awaitSingle()
    }

    suspend fun addPlayer(clubId: ObjectId, player: Player) {
        val update = Update().apply { addToSet("players", player) }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(clubId), update, Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L) throw BaseException(ErrorCode.CLUB_NOT_FOUND)
        if (result.modifiedCount == 0L) throw BaseException(ErrorCode.ALREADY_JOINED)
    }

    suspend fun updatePlayerInfo(playerId: ObjectId, name: String?) {
        val query = Query(Criteria.where("players.id").`is`(playerId))

        val update = Update().apply {
            name?.let { set("players.$.name", it) }
        }

        val result = reactiveMongoTemplate.updateMulti(
            query, update, Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L || result.modifiedCount == 0L) throw BaseException(ErrorCode.PLAYER_NOT_FOUND)
    }

    suspend fun deletePlayer(clubId: ObjectId, playerId: ObjectId) {
        val update = Update().apply {
            pull("players", buildQueryById(playerId))
        }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(clubId), update, Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L) throw BaseException(ErrorCode.CLUB_NOT_FOUND)
        if (result.modifiedCount == 0L) throw BaseException(ErrorCode.PLAYER_NOT_FOUND)
    }

    suspend fun updateStatus(clubId: ObjectId, status: ClubStatus): Club {
        val update = Update().apply {
            set("status", status)
        }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(clubId),
            update,
            Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L) throw BaseException(ErrorCode.CLUB_NOT_FOUND)

        return reactiveMongoTemplate.findById(clubId, Club::class.java).awaitSingle()
    }
}