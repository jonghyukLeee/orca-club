package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.repository.ClubRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component

@Component
class ClubManager(
    private val clubRepository: ClubRepository
) {
    suspend fun create(name: String, introduction: String): Club {
        return clubRepository.save(
            Club(
                name = name,
                introduction = introduction
            )
        ).awaitSingle()
    }

    suspend fun update(club: Club, name: String, introduction: String): Club {
        return clubRepository.save(
            club.copy(
                name = name
                , introduction = introduction
            )
        ).awaitSingle()
    }
}