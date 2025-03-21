package com.orca.club.api

import com.orca.club.domain.Club
import com.orca.club.service.ClubService
import com.orca.club.utils.baseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController
class ClubController(
    private val clubService: ClubService
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
}