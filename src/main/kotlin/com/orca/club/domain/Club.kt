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
) {
    data class Player(
        val id: String,
        val name: String,
        val position: String,
        val matchCount: Int,
        val goal: Int,
        val assist: Int,
        val momCount: Int
    )

    data class Review(
        val id: String,
        val point: Double,
        val comment: String
    )
}