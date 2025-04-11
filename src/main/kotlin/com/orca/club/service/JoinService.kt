package com.orca.club.service

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.domain.Player
import com.orca.club.exception.BaseException
import com.orca.club.exception.ErrorCode
import com.orca.club.external.kafka.publisher.CompensationEventPublisher
import com.orca.club.external.kafka.publisher.EventPublisher
import com.orca.club.external.player.PlayerService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class JoinService(
    private val joinManager: JoinManager,
    private val joinReader: JoinReader,
    private val clubReader: ClubReader,
    private val clubManager: ClubManager,
    private val playerService: PlayerService,
    private val eventPublisher: EventPublisher,
    private val compensationEventPublisher: CompensationEventPublisher
) {
    suspend fun generate(clubId: String, playerId: String, position: Player.Position): JoinApplication {
        return coroutineScope {
            launch { clubReader.findById(clubId) ?: BaseException(ErrorCode.CLUB_NOT_FOUND) }
            // TODO player가 해당 클럽에 가입한 이력이 있는지, 또는 클럽에 블랙리스트로 등록되어있는지 확인하는 로직 추가 필요
            val joinDeferred = async { joinReader.findOneByExtraIds(clubId, playerId) }

            joinDeferred.await()?.let {
                if (it.status == JoinApplicationStatus.PENDING) {
                    throw BaseException(ErrorCode.JOIN_APPLICATION_DUPLICATED)
                } else if (it.status == JoinApplicationStatus.ACCEPTED) {
                    throw BaseException(ErrorCode.ALREADY_ACCEPTED)
                }
            }

            joinManager.create(clubId, playerId, position)
        }
    }

    suspend fun getClubApplications(clubId: String, status: JoinApplicationStatus): List<JoinApplication> {
        return joinReader.findAllByClubIdAndStatus(clubId, status)
    }

    suspend fun getPlayerApplications(playerId: String, status: JoinApplicationStatus): List<JoinApplication> {
        return joinReader.findAllByPlayerIdAndStatus(playerId, status)
    }

    suspend fun accept(joinApplicationId: String): JoinApplication {
        val joinApplication = joinReader.findOneById(joinApplicationId) ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)
        val player = playerService.getPlayer(joinApplication.playerId)

        return coroutineScope {
            try {
                val addPlayerDeferred = async {
                    clubManager.addPlayer(
                        clubId = joinApplication.clubId,
                        playerId = player.id,
                        name = player.name,
                        position = joinApplication.position
                    )
                }

                val produceDeferred = async { eventPublisher.joinAccept(joinApplication) }

                awaitAll(addPlayerDeferred, produceDeferred)

                joinManager.updateStatus(
                    joinApplicationId,
                    JoinApplicationStatus.ACCEPTED
                )
            } catch (e: Exception) {
                launch { compensationEventPublisher.joinAcceptFailed(joinApplication) }
                launch { clubManager.deletePlayer(joinApplication.clubId, player.id) }
                throw BaseException(ErrorCode.JOIN_ACCEPT_FAILED)
            }
        }
    }

    suspend fun reject(id: String): String {
        val joinApplication = joinReader.findOneById(id) ?: throw BaseException(ErrorCode.JOIN_APPLICATION_NOT_FOUND)

        joinManager.delete(joinApplication)
        return joinApplication.id!!
    }
}