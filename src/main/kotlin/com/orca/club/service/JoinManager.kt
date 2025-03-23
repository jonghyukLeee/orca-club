package com.orca.club.service

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.repository.JoinApplicationRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component

@Component
class JoinManager(
    private val repository: JoinApplicationRepository
) {
    suspend fun create(clubId: String, playerId: String): JoinApplication {
        return repository.save(
            JoinApplication(
                clubId = clubId,
                playerId = playerId
            )
        ).awaitSingle()
    }

    suspend fun updateStatus(origin: JoinApplication, status: JoinApplicationStatus): JoinApplication {
        return repository.save(
            origin.copy(status = status)
        ).awaitSingle()
    }

    suspend fun delete(entity: JoinApplication) {
        repository.delete(entity).awaitSingle()
    }
}