package com.orca.club.service

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.domain.Player
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.repository.JoinApplicationRepository
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
class JoinManager(
    private val repository: JoinApplicationRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {
    suspend fun create(clubId: ObjectId, playerId: ObjectId): JoinApplication {
        return repository.save(
            JoinApplication(
                clubId = clubId,
                playerId = playerId
            )
        ).awaitSingle()
    }

    suspend fun updateStatus(joinApplicationId: ObjectId, status: JoinApplicationStatus): JoinApplication {
        val query = Query(Criteria.where("_id").`is`(joinApplicationId))

        val update = Update().apply {
            set("status", status)
        }

        return reactiveMongoTemplate.findAndModify(
            query, update, FindAndModifyOptions().returnNew(true), JoinApplication::class.java
        ).awaitSingleOrNull() ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)
    }

    suspend fun delete(entity: JoinApplication) {
        repository.delete(entity).awaitSingle()
    }
}