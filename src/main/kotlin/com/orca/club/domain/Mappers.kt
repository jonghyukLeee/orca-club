package com.orca.club.domain

import com.orca.club.api.ClubResponse
import com.orca.club.api.JoinApplicationResponse
import com.orca.club.api.PlayerResponse
import com.orca.club.api.ReviewResponse

fun Club.toResponse(): ClubResponse {
    return ClubResponse(
        id = this.id!!,
        name = this.name,
        introduction = this.introduction,
        win = this.win,
        lose = this.lose,
        players = this.players.map { it.toResponse() },
        reviews = this.reviews.map { it.toResponse() },
        mannerPoint = this.mannerPoint,
        blacklist = this.blacklist.toList(),
        status = this.status.name,
    )
}

fun Player.toResponse(): PlayerResponse {
    return PlayerResponse(
        id = this.id,
        name = this.name,
        position = this.position.name,
        matchCount = this.matchCount,
        goal = this.goal,
        assist = this.assist,
        momCount = this.momCount,
        status = this.status.name,
    )
}

fun Review.toResponse(): ReviewResponse {
    return ReviewResponse(
        id = this.id!!,
        point = this.point,
        comment = this.comment,
    )
}

fun JoinApplication.toResponse(): JoinApplicationResponse {
    return JoinApplicationResponse(
        requestId = this.id!!,
        clubId = this.clubId,
        playerId = this.playerId,
        position = this.position.name,
        status = this.status.name,
        notification = this.status.notification,
        createdAt = this.getCreateAtAsKST()
    )
}