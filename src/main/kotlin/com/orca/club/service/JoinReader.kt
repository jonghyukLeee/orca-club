package com.orca.club.service

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.repository.JoinApplicationRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class JoinReader(
    private val repository: JoinApplicationRepository
) {
    suspend fun findOneById(id: ObjectId): JoinApplication? {
        return repository.findById(id).awaitSingleOrNull()
    }

    suspend fun findOneByExtraIds(clubId: ObjectId, playerId: ObjectId): JoinApplication? {
        return repository.findByClubIdAndPlayerId(clubId, playerId).awaitSingleOrNull()
    }

    suspend fun findAllByClubIdAndStatus(clubId: ObjectId, status: JoinApplicationStatus): List<JoinApplication> {
        return repository.findAllByClubIdAndStatus(clubId, status).collectList().awaitSingle().toList()
    }

    suspend fun findAllByPlayerIdAndStatus(playerId: ObjectId, status: JoinApplicationStatus): List<JoinApplication> {
        return repository.findAllByPlayerIdAndStatus(playerId, status).collectList().awaitSingle().toList()
    }
}