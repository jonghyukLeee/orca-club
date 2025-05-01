package com.orca.club.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "clubs")
data class Club(
    @Id
    val id: ObjectId? = null,
    var name: String,
    var introduction: String,
    var win: Int = 0,
    var lose: Int = 0,
    val players: MutableList<Player> = mutableListOf(),
    val reviews: MutableList<Review> = mutableListOf(),
    var mannerPoint: Double = 0.0,
    val blacklist: MutableList<ObjectId> = mutableListOf(),
    var status: ClubStatus = ClubStatus.OPEN
) {
    fun isBlacklistedPlayer(playerId: ObjectId): Boolean {
        return this.blacklist.firstOrNull { it == playerId } != null
    }
}

data class Player(
    val id: ObjectId,
    var name: String,
    var role: Role,
    var position: Position? = null,
    var matchCount: Int = 0,
    var goal: Int = 0,
    var assist: Int = 0,
    var momCount: Int = 0,
    var status: ActiveStatus = ActiveStatus.ACTIVE
) {
    enum class Role(val value: String) {
        OWNER("구단주"),
        DIRECTOR("감독"),
        MANAGER("클럽 관리자"),

        COACH("코치"),
        PLAYER("선수")
    }

    enum class Position {
        FW,
        MF,
        DF
    }
}

enum class ActiveStatus {
    ACTIVE,
    INACTIVE,
    WITHDRAWN
}