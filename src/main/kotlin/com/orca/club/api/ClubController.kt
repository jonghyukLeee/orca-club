package com.orca.club.api

import com.orca.club.domain.Club
import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus
import com.orca.club.service.ClubService
import com.orca.club.service.JoinService
import com.orca.club.utils.baseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController
class ClubController(
    private val clubService: ClubService,
    private val joinService: JoinService
) {
    @PostMapping
    suspend fun generate(@RequestBody request: GenerateRequest): ResponseEntity<Club> {
        val club = clubService.generate(request.name, request.introduction)

        return baseResponse(body = club)
    }

    @PatchMapping
    suspend fun update(@RequestBody request: UpdateRequest): ResponseEntity<Club> {
        val club = clubService.update(request.id, request.name, request.introduction)

        return baseResponse(body = club)
    }

    @PostMapping("/join-application")
    suspend fun joinRequest(
        @RequestBody request: JoinRequest
    ): ResponseEntity<JoinApplicationResponse> {
        return baseResponse(
            body = JoinApplicationResponse(
                joinService.generate(request.clubId, request.playerId)
            )
        )
    }

    @GetMapping("/{clubId}/join-application")
    suspend fun getClubApplications(
        @PathVariable clubId: String,
        @RequestParam status: JoinApplicationStatus
    ): ResponseEntity<List<JoinApplication>> {
        return baseResponse(
            body = joinService.getClubApplications(clubId, status)
        )
    }

    @GetMapping("/join-application")
    suspend fun getPlayerApplications(
        @RequestParam playerId: String,
        @RequestParam status: JoinApplicationStatus
    ): ResponseEntity<List<JoinApplicationResponse>> {
        return baseResponse(
            body = joinService.getPlayerApplications(playerId, status).map { JoinApplicationResponse(it) }
        )
    }

    @PostMapping("/join-application/{id}/accept")
    suspend fun joinAccept(
        @PathVariable id: String
    ): ResponseEntity<JoinApplicationResponse> {
        return baseResponse(
            body = JoinApplicationResponse(
                joinService.accept(id)
            )
        )
    }

    @PostMapping("/join-application/{id}/reject")
    suspend fun joinReject(
        @PathVariable id: String
    ): ResponseEntity<String> {
        return baseResponse(body = joinService.reject(id))
    }
}