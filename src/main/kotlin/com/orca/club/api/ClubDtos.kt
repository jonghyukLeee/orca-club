package com.orca.club.api

import com.orca.club.domain.JoinApplication
import com.orca.club.domain.JoinApplicationStatus

data class GenerateRequest(
    val name: String,
    val introduction: String = ""
)

data class UpdateRequest(
    val id: String,
    val name: String,
    val introduction: String
)

data class JoinRequest(
    val clubId: String,
    val playerId: String
)

data class JoinApplicationResponse(
    val requestId: String,
    val clubId: String,
    val playerId: String,
    val status: JoinApplicationStatus,
    val notification: String,
    val createdAt: String
) {
    constructor(joinApplication: JoinApplication) : this(
        requestId = joinApplication.id!!,
        clubId = joinApplication.clubId,
        playerId = joinApplication.playerId,
        status = joinApplication.status,
        notification = joinApplication.status.notification,
        createdAt = joinApplication.getCreateAtAsKST()
    )
}
