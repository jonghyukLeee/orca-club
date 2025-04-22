package com.orca.club.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "clubs")
data class Club(
    @Id
    val id: String? = null,
    val name: String,
    val introduction: String,
    val win: Int = 0,
    val lose: Int = 0,
    val players: MutableList<Player> = mutableListOf(),
    val reviews: MutableList<Review> = mutableListOf(),
    val mannerPoint: Double = 0.0,
    val blacklist: MutableList<String> = mutableListOf(),
    val status: ClubStatus = ClubStatus.OPEN
)

data class Player(
    val id: String,
    val name: String,
    val position: Position,
    val matchCount: Int = 0,
    val goal: Int = 0,
    val assist: Int = 0,
    val momCount: Int = 0,
    val status: ActiveStatus = ActiveStatus.ACTIVE
) {
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