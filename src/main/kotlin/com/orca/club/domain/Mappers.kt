package com.orca.club.domain

import com.orca.club.api.ClubResponse
import com.orca.club.api.JoinApplicationResponse
import com.orca.club.api.PlayerResponse
import com.orca.club.api.ReviewResponse

fun Club.toResponse(): ClubResponse {
    return ClubResponse(
        id = this.id.toString(),
        name = this.name,
        introduction = this.introduction,
        win = this.win,
        lose = this.lose,
        players = this.players.map { it.toResponse() },
        reviews = this.reviews.map { it.toResponse() },
        mannerPoint = this.mannerPoint,
        blacklist = this.blacklist.map { it.toString() }.toList(),
        status = this.status.name,
    )
}

fun Player.toResponse(): PlayerResponse {
    return PlayerResponse(
        id = this.id.toString(),
        name = this.name,
        role = this.role.name,
        position = this.position?.name,
        matchCount = this.matchCount,
        goal = this.goal,
        assist = this.assist,
        momCount = this.momCount,
        status = this.status.name,
    )
}

fun Review.toResponse(): ReviewResponse {
    return ReviewResponse(
        id = this.id.toString(),
        point = this.point,
        comment = this.comment,
    )
}

fun JoinApplication.toResponse(): JoinApplicationResponse {
    return JoinApplicationResponse(
        joinApplicationId = this.id.toString(),
        clubId = this.clubId.toString(),
        playerId = this.playerId.toString(),
        status = this.status.name,
        notification = this.status.notification,
        createdAt = this.getCreateAtAsKST()
    )
}