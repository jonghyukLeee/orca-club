package com.orca.club.api

import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.domain.toResponse
import com.orca.club.service.ClubService
import com.orca.club.service.JoinService
import com.orca.club.utils.baseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Club", description = "Club APIs")
@RequestMapping
@RestController
class ClubController(
    private val clubService: ClubService,
    private val joinService: JoinService
) {
    @Operation(
        summary = "클럽 생성",
        description = "클럽 생성 API"
    )
    @PostMapping
    suspend fun create(@RequestBody request: CreateClubRequest): ResponseEntity<ClubResponse> {
        return baseResponse(body = clubService.create(request.toCommand()).toResponse())
    }

    @Operation(
        summary = "클럽 조회",
        description = "클럽 단건 조회 API"
    )
    @GetMapping("/{clubId}")
    suspend fun get(@PathVariable clubId: String): ResponseEntity<ClubResponse> {
        return baseResponse(body = clubService.get(ObjectId(clubId)).toResponse())
    }

    @Operation(
        summary = "클럽 수정",
        description = "클럽 정보 수정 API"
    )
    @PatchMapping
    suspend fun update(@RequestBody request: UpdateRequest): ResponseEntity<ClubResponse> {
        return baseResponse(
            body = clubService.update(ObjectId(request.id), request.name, request.introduction).toResponse()
        )
    }

    @Operation(
        summary = "클럽 참가 신청",
        description = "클럽 참가 신청 API"
    )
    @PostMapping("/join-application")
    suspend fun joinRequest(
        @RequestBody request: JoinRequest
    ): ResponseEntity<JoinApplicationResponse> {
        return baseResponse(
            body = joinService.create(ObjectId(request.clubId), ObjectId(request.playerId)).toResponse()
        )
    }

    @Operation(
        summary = "클럽 참가 신청 목록 조회 by Club ID",
        description = "Club ID로 요청된 참가 신청 목록 조회 API"
    )
    @GetMapping("/{clubId}/join-application")
    suspend fun getClubApplications(
        @PathVariable clubId: String,
        @RequestParam status: JoinApplicationStatus
    ): ResponseEntity<List<JoinApplicationResponse>> {
        return baseResponse(
            body = joinService.getClubApplications(ObjectId(clubId), status).map { it.toResponse() }
        )
    }

    @Operation(
        summary = "클럽 참가 신청 목록 조회 by Player ID",
        description = "Player ID가 요청한 클럽 참가 신청 목록 조회 API "
    )
    @GetMapping("/join-application")
    suspend fun getPlayerApplications(
        @RequestParam playerId: String,
        @RequestParam status: JoinApplicationStatus
    ): ResponseEntity<List<JoinApplicationResponse>> {
        return baseResponse(
            body = joinService.getPlayerApplications(ObjectId(playerId), status).map { it.toResponse() }
        )
    }

    @Operation(
        summary = "참가 신청 수락",
        description = "클럽 참가 신청 수락 API"
    )
    @PostMapping("/join-application/{id}/accept")
    suspend fun joinAccept(
        @PathVariable id: String
    ): ResponseEntity<JoinApplicationResponse> {
        return baseResponse(body = joinService.accept(ObjectId(id)).toResponse())
    }

    @Operation(
        summary = "참가 신청 거절",
        description = "클럽 참가 신청 거절 API"
    )
    @PostMapping("/join-application/{id}/reject")
    suspend fun joinReject(
        @PathVariable id: String
    ): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "선수 조회",
        description = "클럽에 속한 선수 단건 조회 API"
    )
    @GetMapping("/{clubId}/players/{playerId}")
    suspend fun getPlayer(
        @PathVariable clubId: String,
        @PathVariable playerId: String
    ): ResponseEntity<PlayerResponse> {
        return baseResponse(body = clubService.getPlayer(ObjectId(clubId), ObjectId(playerId)).toResponse())
    }
}