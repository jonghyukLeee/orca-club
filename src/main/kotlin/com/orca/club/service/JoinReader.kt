package com.orca.club.service

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.repository.JoinApplicationRepository
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component

@Component
class JoinReader(
    private val repository: JoinApplicationRepository
) {
    suspend fun findOneById(id: String): JoinApplication? {
        return repository.findById(id).awaitSingleOrNull()
    }

    suspend fun findOneByExtraIds(clubId: String, playerId: String): JoinApplication? {
        return repository.findByClubIdAndPlayerId(clubId, playerId).awaitSingleOrNull()
    }

    suspend fun findAllByClubIdAndStatus(clubId: String, status: JoinApplicationStatus): List<JoinApplication> {
        return repository.findAllByClubIdAndStatus(clubId, status).collectList().awaitSingle().toList()
    }
}