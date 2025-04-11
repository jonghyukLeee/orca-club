package com.orca.club.api

import com.orca.club.domain.*

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
    val players: List<PlayerResponse>,
    val reviews: List<ReviewResponse>,
    val mannerPoint: Double,
    val blacklist: List<String>,
    val status: String,
)

data class PlayerResponse(
    val id: String,
    val name: String,
    val position: String,
    val matchCount: Int,
    val goal: Int,
    val assist: Int,
    val momCount: Int,
    val status: String
)

data class ReviewResponse(
    val id: String,
    val point: Double,
    val comment: String,
)

data class JoinRequest(
    val clubId: String,
    val playerId: String,
    val position: Player.Position
)

data class JoinApplicationResponse(
    val requestId: String,
    val clubId: String,
    val playerId: String,
    val position: String,
    val status: JoinApplicationStatus,
    val notification: String,
    val createdAt: String
)