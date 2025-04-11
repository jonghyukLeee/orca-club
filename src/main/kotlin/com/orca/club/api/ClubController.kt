package com.orca.club.api

import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.domain.toResponse
import com.orca.club.service.ClubService
import com.orca.club.service.JoinService
import com.orca.club.utils.baseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping
@RestController
class ClubController(
    private val clubService: ClubService,
    private val joinService: JoinService
) {
    @PostMapping
    suspend fun generate(@RequestBody request: GenerateRequest): ResponseEntity<ClubResponse> {
        return baseResponse(body = clubService.generate(request.name, request.introduction).toResponse())
    }

    @GetMapping("/{clubId}")
    suspend fun get(@PathVariable clubId: String): ResponseEntity<ClubResponse> {
        return baseResponse(body = clubService.get(clubId).toResponse())
    }

    @PatchMapping
    suspend fun update(@RequestBody request: UpdateRequest): ResponseEntity<ClubResponse> {
        return baseResponse(
            body = clubService.update(request.id, request.name, request.introduction).toResponse()
        )
    }

    @PostMapping("/join-application")
    suspend fun joinRequest(
        @RequestBody request: JoinRequest
    ): ResponseEntity<JoinApplicationResponse> {
        return baseResponse(
            body = joinService.generate(request.clubId, request.playerId, request.position).toResponse()
        )
    }

    @GetMapping("/{clubId}/join-application")
    suspend fun getClubApplications(
        @PathVariable clubId: String,
        @RequestParam status: JoinApplicationStatus
    ): ResponseEntity<List<JoinApplicationResponse>> {
        return baseResponse(
            body = joinService.getClubApplications(clubId, status).map { it.toResponse() }
        )
    }

    @GetMapping("/join-application")
    suspend fun getPlayerApplications(
        @RequestParam playerId: String,
        @RequestParam status: JoinApplicationStatus
    ): ResponseEntity<List<JoinApplicationResponse>> {
        return baseResponse(
            body = joinService.getPlayerApplications(playerId, status).map { it.toResponse() }
        )
    }

    @PostMapping("/join-application/{id}/accept")
    suspend fun joinAccept(
        @PathVariable id: String
    ): ResponseEntity<JoinApplicationResponse> {
        return baseResponse(body = joinService.accept(id).toResponse())
    }

    @PostMapping("/join-application/{id}/reject")
    suspend fun joinReject(
        @PathVariable id: String
    ): ResponseEntity<String> {
        return baseResponse(body = joinService.reject(id))
    }

    @GetMapping("/{clubId}/players/{playerId}")
    suspend fun getPlayer(
        @PathVariable clubId: String,
        @PathVariable playerId: String
    ): ResponseEntity<PlayerResponse> {
        return baseResponse(body = clubService.getPlayer(clubId, playerId).toResponse())
    }
}