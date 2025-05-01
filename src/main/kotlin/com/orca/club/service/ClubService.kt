package com.orca.club.service

import com.orca.club.domain.Club
import com.orca.club.domain.ClubStatus
import com.orca.club.domain.Player
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.external.kafka.ClubCreatedMessage
import com.orca.club.external.kafka.EventTopics
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
class ClubService(
    private val clubManager: ClubManager,
    private val clubReader: ClubReader,
    private val eventPublisher: EventPublisher,
    private val redisService: RedisService,
    private val playerService: PlayerService
) {
    suspend fun create(command: CreateClubCommand): Club {
        return coroutineScope {
            val txId = generateTransactionId()

            validateIsDuplicatedClubName(command.name)

            val playerDeferred = async { playerService.getPlayer(command.playerId) }
            val clubDeferred = async { clubManager.create(command.name, command.introduction) }

            val player = playerDeferred.await()
            val club = clubDeferred.await()

            saveTxMessage(
                txId = txId,
                value = ClubCreatedMessage(
                    clubId = club.id.toString(),
                    playerId = command.playerId.toString()
                ).toJsonString()
            )

            try {
                launch {
                    joinPlayer(
                        clubId = club.id!!,
                        playerId = command.playerId,
                        name = player.name,
                        role = Player.Role.OWNER
                    )
                }
                launch { eventPublisher.send(EventTopics.CLUB_CREATED, txId) }
            } catch (e: Exception) {
                eventPublisher.send(EventTopics.CLUB_CREATE_FAILED, txId)
            }
            club
        }
    }

    private suspend fun saveTxMessage(txId: String, value: String) {
        redisService.set(txId, value, Duration.ofDays(1L))
    }

    suspend fun validateIsDuplicatedClubName(clubName: String) {
        if (clubReader.findByName(clubName) != null) throw BaseException(ErrorCode.DUPLICATE_NAME)
    }

    suspend fun get(clubId: ObjectId): Club {
        return clubReader.findById(clubId) ?: throw BaseException(ErrorCode.CLUB_NOT_FOUND)
    }

    suspend fun update(clubId: ObjectId, name: String?, introduction: String?): Club {
        return clubManager.update(
            clubId = clubId, name = name, introduction = introduction
        )
    }

    suspend fun releasePlayer(clubId: ObjectId, playerId: ObjectId) {
        clubManager.deletePlayer(clubId, playerId)
    }

    suspend fun getPlayer(clubId: ObjectId, playerId: ObjectId): Player {
        validateIsExistClub(clubId)
        return clubReader.findPlayerById(clubId, playerId) ?: throw BaseException(ErrorCode.PLAYER_NOT_FOUND)
    }

    private suspend fun validateIsExistClub(clubId: ObjectId) {
        get(clubId)
    }

    suspend fun joinPlayer(clubId: ObjectId, playerId: ObjectId, name: String, role: Player.Role = Player.Role.PLAYER) {
        clubManager.addPlayer(clubId, Player(id = playerId, name = name, role = role))
    }

    suspend fun switchClubStatus(clubId: ObjectId): Club {
        val club = get(clubId)
        val newStatus = if (ClubStatus.OPEN == club.status) ClubStatus.CLOSE else ClubStatus.OPEN
        return clubManager.updateStatus(clubId, newStatus)
    }

    suspend fun updatePosition(clubId: ObjectId, playerId: ObjectId, position: Player.Position): Player {
        validateIsExistClub(clubId)
        return clubManager.updatePosition(clubId, playerId, position)
    }

    suspend fun addToBlacklist(clubId: ObjectId, playerId: ObjectId): List<ObjectId> {
        return coroutineScope {
            val playerDeferred = async { playerService.getPlayer(playerId) }
            val clubDeferred = async { get(clubId) }

            playerDeferred.await()
            val club = clubDeferred.await()

            if (club.isBlacklistedPlayer(playerId)) throw BaseException(ErrorCode.ALREADY_BLACKLISTED)
            clubManager.addToBlacklist(clubId, playerId)
        }
    }

    suspend fun removeFromBlacklist(clubId: ObjectId, playerId: ObjectId): List<ObjectId> {
        return coroutineScope {
            val clubDeferred = async { get(clubId) }

            val club = clubDeferred.await()
            if (!club.isBlacklistedPlayer(playerId)) throw BaseException(ErrorCode.PLAYER_NOT_FOUND_IN_BLACKLIST)

            clubManager.removeFromBlacklist(clubId, playerId)
        }
    }
}