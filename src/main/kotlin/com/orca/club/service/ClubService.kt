package com.orca.club.service

import com.orca.club.external.redis.RedisService
import com.orca.club.domain.Club
import com.orca.club.domain.Player
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.external.kafka.ClubCreatedMessage
import com.orca.club.external.kafka.EventTopics
import com.orca.club.external.kafka.publisher.EventPublisher
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
    private val redisService: RedisService
) {
    suspend fun create(command: CreateClubCommand): Club {
        return coroutineScope {
            val txId = generateTransactionId()
            validateIsDuplicatedClubName(command.name)
            val club = async { clubManager.create(command.name, command.introduction) }.await()

            saveTxMessage(
                txId = txId,
                value = ClubCreatedMessage(clubId = club.id.toString(), playerId = command.playerId.toString()).toJsonString()
            )

            try {
                launch {
                    clubManager.addPlayer(
                        clubId = club.id!!,
                        playerId = command.playerId,
                        name = command.name,
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

    suspend fun getPlayer(clubId: ObjectId, playerId: ObjectId): Player {
        return clubReader.findPlayerById(clubId, playerId) ?: throw BaseException(ErrorCode.PLAYER_NOT_FOUND)
    }
}