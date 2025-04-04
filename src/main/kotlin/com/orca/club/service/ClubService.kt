package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class ClubService(
    private val clubManager: ClubManager,
    private val clubReader: ClubReader
) {
    suspend fun generate(name: String, introduction: String): Club {
        if (clubReader.byName(name) != null) throw BaseException(ErrorCode.DUPLICATE_NAME)
        return clubManager.create(name, introduction)
    }

    suspend fun get(clubId: String): Club {
        return clubReader.byId(clubId) ?: throw BaseException(ErrorCode.CLUB_NOT_FOUND)
    }

    suspend fun update(id: String, name: String?, introduction: String?): Club {
        return clubManager.update(
            clubId = id
            , name = name
            , introduction = introduction
        )
    }
}