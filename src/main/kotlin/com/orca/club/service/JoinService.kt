package com.orca.club.service

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class JoinService(
    private val joinManager: JoinManager,
    private val joinReader: JoinReader,
    private val clubReader: ClubReader
) {
    suspend fun generate(clubId: String, playerId: String): JoinApplication {
        return coroutineScope {
            launch { clubReader.byId(clubId) ?: BaseException(ErrorCode.CLUB_NOT_FOUND) }
            val joinDeferred = async { joinReader.findOneByExtraIds(clubId, playerId) }

            joinDeferred.await()?.let {
                if (it.status == JoinApplicationStatus.PENDING) {
                    throw BaseException(ErrorCode.JOIN_APPLICATION_DUPLICATED)
                } else if (it.status == JoinApplicationStatus.ACCEPTED) {
                    throw BaseException(ErrorCode.ALREADY_ACCEPTED)
                }
            }

            joinManager.create(clubId, playerId)
        }
    }

    suspend fun getClubApplications(clubId: String, status: JoinApplicationStatus): List<JoinApplication> {
        return joinReader.findAllByClubIdAndStatus(clubId, status)
    }

    suspend fun getPlayerApplications(playerId: String, status: JoinApplicationStatus): List<JoinApplication> {
        return joinReader.findAllByPlayerIdAndStatus(playerId, status)
    }

    // TODO 수락 또는 거절 시 사용자에게 알림 전송
    suspend fun accept(id: String): JoinApplication {
        val joinApplication = joinReader.findOneById(id) ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)

        return joinManager.updateStatus(
            joinApplication,
            JoinApplicationStatus.ACCEPTED
        )
    }

    suspend fun reject(id: String): String {
        val joinApplication = joinReader.findOneById(id) ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)

        joinManager.delete(joinApplication)
        return joinApplication.id!!
    }
}