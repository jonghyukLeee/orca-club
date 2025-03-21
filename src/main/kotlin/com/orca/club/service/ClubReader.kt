package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.repository.ClubRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component

@Component
class ClubReader(
    private val clubRepository: ClubRepository
) {
    suspend fun byId(id: String): Club? {
        return clubRepository.findById(id).awaitSingleOrNull()
    }
    suspend fun byName(name: String): Club? {
        return clubRepository.findByName(name).awaitSingleOrNull()
    }
}