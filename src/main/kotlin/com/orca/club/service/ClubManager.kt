package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.domain.Player
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.repository.ClubRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
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

    suspend fun update(clubId: String, name: String?, introduction: String?): Club {
        val update = Update().apply {
            name?.let { set("name", it) }
            introduction?.let { set("introduction", it) }
        }

        return reactiveMongoTemplate.findAndModify(
            buildQueryById(clubId), update, FindAndModifyOptions().returnNew(true), Club::class.java
        ).awaitSingleOrNull() ?: throw BaseException(ErrorCode.CLUB_NOT_FOUND)
    }

    suspend fun addPlayer(clubId: String, playerId: String, name: String, position: Player.Position) {
        val update = Update().apply {
            addToSet(
                "players", Player(
                    id = playerId, name = name, position = position
                )
            )
        }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(clubId), update, Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L) throw BaseException(ErrorCode.CLUB_NOT_FOUND)
        if (result.modifiedCount == 0L) throw BaseException(ErrorCode.ALREADY_JOINED)
    }

    suspend fun updatePlayerInfo(playerId: String, name:String?){
        val query = Query(Criteria.where("players.id").`is`(playerId))

        val update = Update().apply {
            name?.let { set("players.$.name", it) }
        }

        val result = reactiveMongoTemplate.updateMulti(
            query, update, Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L || result.modifiedCount == 0L) throw BaseException(ErrorCode.PLAYER_NOT_FOUND)
    }

    suspend fun deletePlayer(clubId: String, playerId: String){
        val update = Update().apply {
            pull("players", buildQueryById(playerId))
        }

        val result = reactiveMongoTemplate.updateFirst(
            buildQueryById(clubId)
            , update
            , Club::class.java
        ).awaitSingle()

        if (result.matchedCount == 0L) throw BaseException(ErrorCode.CLUB_NOT_FOUND)
        if (result.modifiedCount == 0L) throw BaseException(ErrorCode.PLAYER_NOT_FOUND)
    }

    private suspend fun buildQueryById(id: String): Query {
        return Query(Criteria.where("_id").`is`(id))
    }
}