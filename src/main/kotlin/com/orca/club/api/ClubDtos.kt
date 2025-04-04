package com.orca.club.api

import com.orca.club.domain.Club
import com.orca.club.domain.Club.Player
import com.orca.club.domain.Club.Player.Position
import com.orca.club.domain.Club.Review
import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus

data class GenerateRequest(
    val name: String,
    val introduction: String = ""
)

data class UpdateRequest(
    val id: String,
    val name: String?,
    val introduction: String?
)

data class ClubResponse(
    val id: String,
    val name: String,
    val introduction: String,
    val win: Int,
    val lose: Int,
    val players: List<Player>,
    val reviews: List<Review>,
    val mannerPoint: Double,
    val blacklist: List<String>,
    val status: String,
) {
    constructor(club: Club) : this(
        id = club.id!!,
        name = club.name,
        introduction = club.introduction,
        win = club.win,
        lose = club.lose,
        players = club.players.toList(),
        reviews = club.reviews.toList(),
        mannerPoint = club.mannerPoint,
        blacklist = club.blacklist.toList(),
        status = club.status.name,
    )
}

data class JoinRequest(
    val clubId: String,
    val playerId: String,
    val position: Position
)

data class JoinApplicationResponse(
    val requestId: String,
    val clubId: String,
    val playerId: String,
    val position: String,
    val status: JoinApplicationStatus,
    val notification: String,
    val createdAt: String
) {
    constructor(joinApplication: JoinApplication) : this(
        requestId = joinApplication.id!!,
        clubId = joinApplication.clubId,
        playerId = joinApplication.playerId,
        position = joinApplication.position.name,
        status = joinApplication.status,
        notification = joinApplication.status.notification,
        createdAt = joinApplication.getCreateAtAsKST()
    )
}