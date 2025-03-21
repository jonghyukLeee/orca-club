package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class ClubService(
    private val manager: ClubManager,
    private val reader: ClubReader
) {
    suspend fun generate(name: String, introduction: String): Club {
        if (reader.byName(name) != null) throw BaseException(ErrorCode.DUPLICATE_NAME)
        return manager.create(name, introduction)
    }

    suspend fun update(id: String, name: String, introduction: String): Club {
        val club = reader.byId(id) ?: throw BaseException(ErrorCode.CLUB_NOT_FOUND)

        return manager.update(club, name, introduction)
    }
}