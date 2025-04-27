package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.domain.ClubStatus
import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.external.kafka.EventTopics
import com.orca.club.external.kafka.JoinAcceptMessage
import com.orca.club.external.kafka.publisher.EventPublisher
import com.orca.club.external.player.PlayerService
import com.orca.club.external.redis.RedisService
import com.orca.club.utils.generateTransactionId
import com.orca.club.utils.toJsonString
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class JoinService(
    private val joinManager: JoinManager,
    private val joinReader: JoinReader,
    private val clubService: ClubService,
    private val playerService: PlayerService,
    private val eventPublisher: EventPublisher,
    private val redisService: RedisService
) {
    suspend fun create(clubId: ObjectId, playerId: ObjectId): JoinApplication {
        fun validateCreatable(club: Club) {
            if (club.isBlacklistedPlayer(playerId)) throw BaseException(ErrorCode.BLACKLISTED_PLAYER)
            if (club.status == ClubStatus.CLOSE) throw BaseException(ErrorCode.APPLICATION_CLOSED)
        }

        fun validateExistingApplication(joinApplication: JoinApplication?) {
            joinApplication?.let {
                if (it.status == JoinApplicationStatus.PENDING) {
                    throw BaseException(ErrorCode.JOIN_APPLICATION_DUPLICATED)
                } else if (it.status == JoinApplicationStatus.ACCEPTED) {
                    throw BaseException(ErrorCode.ALREADY_ACCEPTED)
                }
            }
        }

        return coroutineScope {
            val clubDeferred = async { clubService.get(clubId) }
            val joinApplicationDeferred = async { joinReader.findOneByExtraIds(clubId, playerId) }

            val club = clubDeferred.await()
            val joinApplication = joinApplicationDeferred.await()

            validateCreatable(club)
            validateExistingApplication(joinApplication)

            joinManager.create(clubId, playerId)
        }
    }

    suspend fun getClubApplications(clubId: ObjectId, status: JoinApplicationStatus): List<JoinApplication> {
        return joinReader.findAllByClubIdAndStatus(clubId, status)
    }

    suspend fun getPlayerApplications(playerId: ObjectId, status: JoinApplicationStatus): List<JoinApplication> {
        return joinReader.findAllByPlayerIdAndStatus(playerId, status)
    }

    suspend fun accept(joinApplicationId: ObjectId): JoinApplication {
        val joinApplication =
            joinReader.findOneById(joinApplicationId) ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)
        val player = playerService.getPlayer(joinApplication.playerId)
        val txId = generateTransactionId()

        return coroutineScope {
            try {
                launch {
                    clubService.joinPlayer(
                        clubId = joinApplication.clubId,
                        playerId = ObjectId(player.id),
                        name = player.name
                    )
                }

                saveTxMessage(
                    txId,
                    JoinAcceptMessage(
                        joinApplicationId = joinApplication.id.toString(),
                        clubId = joinApplication.clubId.toString(),
                        playerId = player.id
                    ).toJsonString()
                )

                launch { eventPublisher.send(EventTopics.JOIN_ACCEPTED, txId) }

                joinManager.updateStatus(
                    joinApplicationId,
                    JoinApplicationStatus.ACCEPTED
                )
            } catch (e: Exception) {
                launch {
                    eventPublisher.send(
                        EventTopics.JOIN_ACCEPT_FAILED,
                        JoinAcceptMessage(
                            joinApplicationId = joinApplicationId.toString(),
                            clubId = joinApplication.clubId.toString(),
                            playerId = joinApplication.playerId.toString()
                        )
                    ).toJsonString()
                }
                launch { clubService.releasePlayer(joinApplication.clubId, ObjectId(player.id)) }
                throw BaseException(ErrorCode.JOIN_ACCEPT_FAILED)
            }
        }
    }

    private suspend fun saveTxMessage(txId: String, value: String) {
        redisService.set(txId, value, Duration.ofDays(1L))
    }

    suspend fun reject(joinApplicationId: ObjectId): JoinApplication {
        joinReader.findOneById(joinApplicationId) ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)
        return joinManager.updateStatus(joinApplicationId, JoinApplicationStatus.REJECTED)
    }
}